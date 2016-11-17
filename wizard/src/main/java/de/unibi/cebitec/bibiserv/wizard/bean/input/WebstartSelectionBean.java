package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ReferenceManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.WebstartManager;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.primefaces.context.RequestContext;

   /**
     * This is used to manage viewSelection.xhtml
     * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
     */
    @ManagedBean
    @SessionScoped
public class WebstartSelectionBean {

    /**
     * Manager of the list of all current webstarts.
     */
    private WebstartManager webstartManager;
    /**
     * webstart instance
     */
    private WebstartBean webstartBean;
    /**
     * Current edit list.
     */
    private List<String> selectedWebstart;
    /**
     * Last saved list.
     */
    private List<String> savedSelectedWebstarts;
    /**
     * for communication to xhtml
     */
    private DataModel<Tupel<Integer, String>> tableModel;
    /**
     * Tell if tableModel needs to be calculated anew.
     */
    private boolean dataModelEdited;
    /**
     * Full list of all webstarts
     */
    private List<String> readyWebstartList;
    
    private boolean renderUnsavedChanges;

 
    public WebstartSelectionBean() {

        // retrieve current bean of xmlViewer
        FacesContext context = FacesContext.getCurrentInstance();

        webstartManager = (WebstartManager) context.getApplication().
                evaluateExpressionGet(context, "#{webstartManager}",
                WebstartManager.class);
        webstartBean = (WebstartBean) context.getApplication().
                evaluateExpressionGet(context, "#{webstartBean}",
                WebstartBean.class);

        // init the selected functions with one element
        selectedWebstart = new ArrayList<String>();
        selectedWebstart.add("");

        savedSelectedWebstarts = new ArrayList<String>();

        dataModelEdited = true;
        renderUnsavedChanges = false;
    }

    private void getLists() {
        readyWebstartList = webstartManager.getAllNames();
    }

    /**
     * Retuns a list of Tupels containing all selected strings
     * with position in list.
     * @return as above
     */
    public DataModel<Tupel<Integer, String>> getSelectedWebstartsWithId() {

        if (dataModelEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedWebstart) {
                ret.add(new Tupel(i, str));
                i++;
            }

            tableModel = new ListDataModel<Tupel<Integer, String>>(ret);
        }
        dataModelEdited = false;
        return tableModel;
    }

    /**
     * Returns whether the remove symbol is shown behind dropdown.
     * @return true: draw X; false: don't draw
     */
    public boolean isShowRemove() {
        return selectedWebstart.size() > 1;
    }

    public boolean isWebstartsEmpty() {
        return webstartManager.isEmpty();
    }

    public void dropDownValueChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = tableModel.getRowData();

        selectedWebstart.set(currentTupel.getFirst(), value);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public void removeDropdown(int index) {
        selectedWebstart.remove(index);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public void addDropdown(int index) {
        selectedWebstart.add(index + 1, "");
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public String newWebstart() {
        webstartBean.newWebstart();
        return "webstart.xhtml?faces-redirect=true";
    }

    public String editWebstart(String id) {
        webstartBean.loadWebstart(id);
        return "webstart.xhtml?faces-redirect=true";
    }

    public void removeWebstart(String id) {
        webstartBean.removeWebstart(id);
        getLists();
    }

    private void saveAll() {
        
        // remove double values while saving
        savedSelectedWebstarts.clear();
        Set<String> savedSet = new HashSet<String>();

        for (String str : selectedWebstart) {
            if (!str.isEmpty()) {
                if(!savedSet.contains(str)) {
                    savedSelectedWebstarts.add(str);
                }
                savedSet.add(str);
            }
        }
        
        selectedWebstart.clear();
        selectedWebstart.addAll(savedSelectedWebstarts);
        if (selectedWebstart.isEmpty()) {
            selectedWebstart.add("");
        }
        
        dataModelEdited = true;
        renderUnsavedChanges = false;
    }

    /**
     * Save all none-empty selected authors to saved authors.
     */
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();

        saveAll();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                PropertyManager.getProperty("saveSuccesful"), ""));
        
    }

    public void saveReturn() {
        RequestContext context = RequestContext.getCurrentInstance();

        saveAll();

        // redirect from javax context
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().
                getViewHandler().getActionURL(ctx, "/overview.xhtml"));
        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }
    }

    public String cancel() {

        selectedWebstart.clear();
        selectedWebstart.addAll(savedSelectedWebstarts);
        if (selectedWebstart.isEmpty()) {
            selectedWebstart.add("");
        }
        dataModelEdited = true;
        renderUnsavedChanges = false;
        return "overview.xhtml?faces-redirect=true";
    }

    public String returnToPrev() {
        return "overview.xhtml?faces-redirect=true";
    }

    public void preRender() {
        getLists();
    }

    public void reload() {
        getLists();
        List<String> newSaved = new ArrayList<String>();
        for (String str : savedSelectedWebstarts) {
            if (readyWebstartList.contains(str)) {
                newSaved.add(str);
            }
        }
        savedSelectedWebstarts = newSaved;
    }

    public String getWebstartStatus() {
        return TrafficLightEnum.GREEN.getPath();
    }

    public void changeWebstartName(String oldid, String newid) {
        // change name in saved
        ListIterator<String> savedIterator = savedSelectedWebstarts.listIterator();
        
        while (savedIterator.hasNext()) {
            String str = savedIterator.next();
            if (str.equals(oldid)) {
                savedIterator.remove();
                savedIterator.add(newid);
            }
        }

        // change name in saved
        ListIterator<String> selectedIterator = selectedWebstart.listIterator();
        while (selectedIterator.hasNext()) {
            String str = selectedIterator.next();
            if (str.equals(oldid)) {
                selectedIterator.remove();
                selectedIterator.add(newid);
            }
        }
        dataModelEdited = true;
    }
    
    
    public List<String> getSavedSelectedWebstarts() {
        return savedSelectedWebstarts;
    }

    public void addAllSavedSelectedWebstarts(List<String> savedSelectedWebstarts) {
        this.savedSelectedWebstarts.clear();
        this.savedSelectedWebstarts.addAll(savedSelectedWebstarts);
    }

    public DataModel<Tupel<Integer, String>> getTableModel() {
        return tableModel;
    }

    public void setTableModel(DataModel<Tupel<Integer, String>> tableModel) {
        this.tableModel = tableModel;
    }

    public List<String> getReadyWebstartList() {
        return readyWebstartList;
    }

    public void setReadyWebstartList(List<String> readyWebstartsList) {
        this.readyWebstartList = readyWebstartsList;
    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
    public boolean isWebstartSet() {
        return !savedSelectedWebstarts.isEmpty();
    }
    
}
