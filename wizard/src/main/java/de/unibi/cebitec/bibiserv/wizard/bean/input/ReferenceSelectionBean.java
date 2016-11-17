package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ReferenceManager;
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
public class ReferenceSelectionBean {

    /**
     * Manager of the list of all current references.
     */
    private ReferenceManager referenceManager;
    /**
     *ViewBean instance
     */
    private ReferenceBean referenceBean;
    /**
     * Current edit list.
     */
    private List<String> selectedReferences;
    /**
     * Last saved list.
     */
    private List<String> savedSelectedReferences;
    /**
     * for communication to xhtml
     */
    private DataModel<Tupel<Integer, String>> tableModel;
    /**
     * Tell if tableModel needs to be calculated anew.
     */
    private boolean dataModelEdited;
    /**
     * Full list of all files
     */
    private List<String> readyReferencesList;
    
    private boolean renderUnsavedChanges;

 
    public ReferenceSelectionBean() {

        // retrieve current bean of xmlViewer
        FacesContext context = FacesContext.getCurrentInstance();

        referenceManager = (ReferenceManager) context.getApplication().
                evaluateExpressionGet(context, "#{referenceManager}",
                ReferenceManager.class);
        referenceBean = (ReferenceBean) context.getApplication().
                evaluateExpressionGet(context, "#{referenceBean}",
                ReferenceBean.class);

        // init the selected functions with one element
        selectedReferences = new ArrayList<String>();
        selectedReferences.add("");

        savedSelectedReferences = new ArrayList<String>();

        dataModelEdited = true;
        renderUnsavedChanges = false;
    }

    private void getLists() {
        readyReferencesList = referenceManager.getAllIds();
    }

    /**
     * Retuns a list of Tupels containing all selected strings
     * with position in list.
     * @return as above
     */
    public DataModel<Tupel<Integer, String>> getSelectedReferencesWithId() {

        if (dataModelEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedReferences) {
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
        return selectedReferences.size() > 1;
    }

    public boolean isViewsEmpty() {
        return referenceManager.isEmpty();
    }

    public void dropDownValueChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = tableModel.getRowData();

        selectedReferences.set(currentTupel.getFirst(), value);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public void removeDropdown(int index) {
        selectedReferences.remove(index);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public void addDropdown(int index) {
        selectedReferences.add(index + 1, "");
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public String newReference() {
        referenceBean.newReference();
        return "reference.xhtml?faces-redirect=true";
    }

    public String editReference(String id) {
        referenceBean.editReference(id);
        return "reference.xhtml?faces-redirect=true";
    }

    public void removeReference(String id) {
        referenceBean.removeReference(id);
        getLists();
    }

    private void saveAll() {
        
        // remove double values while saving
        savedSelectedReferences.clear();
        Set<String> savedSet = new HashSet<String>();

        for (String str : selectedReferences) {
            if (!str.isEmpty()) {
                if(!savedSet.contains(str)) {
                    savedSelectedReferences.add(str);
                }
                savedSet.add(str);
            }
        }
        
        selectedReferences.clear();
        selectedReferences.addAll(savedSelectedReferences);
        if (selectedReferences.isEmpty()) {
            selectedReferences.add("");
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
        if (savedSelectedReferences.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedViewError"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", true);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    PropertyManager.getProperty("saveSuccesful"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", false);
        }
    }

    public void saveReturn() {
        RequestContext context = RequestContext.getCurrentInstance();

        saveAll();

        if (savedSelectedReferences.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedViewError"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", true);
            return;
        }
        context.addCallbackParam("show", false);
        context.addCallbackParam("returns", true);

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

        selectedReferences.clear();
        selectedReferences.addAll(savedSelectedReferences);
        if (selectedReferences.isEmpty()) {
            selectedReferences.add("");
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
        for (String str : savedSelectedReferences) {
            if (readyReferencesList.contains(str)) {
                newSaved.add(str);
            }
        }
        savedSelectedReferences = newSaved;
    }

    public String getReferenceStatus() {
        if (savedSelectedReferences.isEmpty()) {
            return TrafficLightEnum.YELLOW.getPath();
        }
        return TrafficLightEnum.GREEN.getPath();
    }

    public void changeReferenceName(String oldid, String newid) {
        // change name in saved
        ListIterator<String> savedIterator = savedSelectedReferences.
                listIterator();
        while (savedIterator.hasNext()) {
            String str = savedIterator.next();
            if (str.equals(oldid)) {
                savedIterator.remove();
                savedIterator.add(newid);
            }
        }

        // change name in saved
        ListIterator<String> selectedIterator =
                selectedReferences.listIterator();
        while (selectedIterator.hasNext()) {
            String str = selectedIterator.next();
            if (str.equals(oldid)) {
                selectedIterator.remove();
                selectedIterator.add(newid);
            }
        }
        dataModelEdited = true;
    }
    
    
    public List<String> getSavedSelectedReferences() {
        return savedSelectedReferences;
    }

    public void addAllSavedSelectedReferences(List<String> savedSelectedReferences) {
        this.savedSelectedReferences.clear();
        this.savedSelectedReferences.addAll(savedSelectedReferences);
    }

    public DataModel<Tupel<Integer, String>> getTableModel() {
        return tableModel;
    }

    public void setTableModel(DataModel<Tupel<Integer, String>> tableModel) {
        this.tableModel = tableModel;
    }

    public List<String> getReadyReferencesList() {
        return readyReferencesList;
    }

    public void setReadyReferencesList(List<String> readyReferencesList) {
        this.readyReferencesList = readyReferencesList;
    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
}
