package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class FunctionSelectionBean {

    /**
     * Manager of the list of all current functions.
     */
    private FunctionManager functionManager;
    /**
     * FunctionBean object
     */
    private FunctionBean functionBean;
    /**
     * Current edit list.
     */
    private List<String> selectedFunctions;
    /**
     * Last saved list.
     */
    private List<String> savedSelectedFunctions;
    /**
     * for communication to xhtml
     */
    private DataModel<Tupel<Integer, String>> tableModel;
    /**
     * Tell if tableModel needs to be calculated anew.
     */
    private boolean dataModelEdited;
    /**
     * Contains the names of all valid functions
     */
    private List<String> readyFunctionsList;
    /**
     * Full list of all functions with valid value.
     */
    private List<Tupel<String, Boolean>> functionNamesList;
    
    private boolean renderUnsavedChanges;

    public FunctionSelectionBean() {

        // retrieve current bean of xmlViewer
        FacesContext context = FacesContext.getCurrentInstance();

        functionManager = (FunctionManager) context.getApplication().
                evaluateExpressionGet(context, "#{functionManager}",
                FunctionManager.class);
        functionBean = (FunctionBean) context.getApplication().
                evaluateExpressionGet(context, "#{functionBean}",
                FunctionBean.class);

        // init the selected functions with one element
        selectedFunctions = new ArrayList<String>();
        selectedFunctions.add("");

        savedSelectedFunctions = new ArrayList<String>();

        dataModelEdited = true;
        renderUnsavedChanges = false;
    }

    private void getLists() {

        functionNamesList = functionManager.getAllNames();
        readyFunctionsList = functionManager.getAllReadyNames();
        dataModelEdited = true;
    }

    /**
     * Retuns a list of Tupels containing all selected strings
     * with position in list.
     * @return as above
     */
    public DataModel<Tupel<Integer, String>> getSelectedFunctionsWithId() {

        if (dataModelEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedFunctions) {
                ret.add(new Tupel(i, str));
                i++;
            }

            tableModel = new ListDataModel<Tupel<Integer, String>>(ret);
        }
        dataModelEdited = false;
        return tableModel;
    }

    /**
     * Returns all saved seleced functions.
     * @return all saved seleced functions
     */
    public List<String> getSelectedFunctions() {
        return savedSelectedFunctions;
    }

    /**
     * sets the saved seleced functions.
     * @param newSelectedFunctions new list of saved selected functions.
     */
    public void addAllSavedSelectedFunctions(List<String> newSelectedFunctions) {
        savedSelectedFunctions.clear();
        savedSelectedFunctions.addAll(newSelectedFunctions);
    }

    /**
     * Returns whether the remove symbol is shown behind dropdown.
     * @return true: draw X; false: don't draw
     */
    public boolean isShowRemove() {
        return selectedFunctions.size() > 1;
    }

    public boolean isFunctionsEmpty() {
        return functionManager.isEmpty();
    }

    public void dropDownValueChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = tableModel.getRowData();

        selectedFunctions.set(currentTupel.getFirst(), value);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public void removeDropdown(int index) {
        selectedFunctions.remove(index);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public void addDropdown(int index) {
        selectedFunctions.add(index + 1, "");
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public String newFunction() {
        functionBean.newFunction();
        return "function.xhtml?faces-redirect=true";
    }

    public String editFunction(String name) {
        functionBean.editFunction(name);
        return "function.xhtml?faces-redirect=true";
    }

    public void removeFunction(String name) {
        try {
            functionManager.removeFunctionByName(name);
        } catch (BeansException ex) {
            // should not happen
        }
        getLists();
    }

    /**
     * Save all none-empty selected functions to saved functiosn.
     */
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();

        savedSelectedFunctions = new ArrayList<String>();
        for (String str : selectedFunctions) {
            if (!str.equals("")) {
                savedSelectedFunctions.add(str);
            }
        }

        selectedFunctions.clear();
        selectedFunctions.addAll(savedSelectedFunctions);
        if (selectedFunctions.isEmpty()) {
            selectedFunctions.add("");
        }
        dataModelEdited = true;
        
        if (savedSelectedFunctions.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedFunctionError"), ""));
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
        renderUnsavedChanges = false;
    }

    public void saveReturn() {
        RequestContext context = RequestContext.getCurrentInstance();
        savedSelectedFunctions = new ArrayList<String>();

        for (String str : selectedFunctions) {
            if (!str.isEmpty()) {
                savedSelectedFunctions.add(str);
            }
        }

        if (savedSelectedFunctions.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedFunctionError"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", true);
            return;
        }
        context.addCallbackParam("show", false);
        context.addCallbackParam("returns", true);

        // redirect from javax context
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, "/overview.xhtml"));
        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }
        renderUnsavedChanges = false;
    }

    public String cancel() {

        selectedFunctions.clear();
        selectedFunctions.addAll(savedSelectedFunctions);
        if (selectedFunctions.isEmpty()) {
            selectedFunctions.add("");
        }
        dataModelEdited = true;
        renderUnsavedChanges = false;
        return "overview.xhtml?faces-redirect=true";
    }

    public String returnToPrev() {
         renderUnsavedChanges = false;
        return "overview.xhtml?faces-redirect=true";
    }

    public void preRender() {
        getLists();
    }

    public void reload() {
        getLists();
        List<String> newSaved = new ArrayList<String>();
        for (String str : savedSelectedFunctions) {
            if (readyFunctionsList.contains(str)) {
                newSaved.add(str);
            }
        }
        savedSelectedFunctions = newSaved;
    }

    public String getFunctionStatus() {
        if (savedSelectedFunctions.isEmpty()) {
            return TrafficLightEnum.RED.getPath();
        }
        return TrafficLightEnum.GREEN.getPath();
    }

    public List<Tupel<String, Boolean>> getFunctionNamesList() {
        return functionNamesList;
    }

    public List<String> getReadyFunctionsList() {
        return readyFunctionsList;
    }

    public void changeFunctionName(String oldname, String newname) {
        // change name in saved
        ListIterator<String> savedIterator = savedSelectedFunctions.listIterator();
        while (savedIterator.hasNext()) {
            String str = savedIterator.next();
            if (str.equals(oldname)) {
                savedIterator.remove();
                savedIterator.add(newname);
            }
        }

        // change name in saved
        ListIterator<String> selectedIterator = selectedFunctions.listIterator();
        while (selectedIterator.hasNext()) {
            String str = selectedIterator.next();
            if (str.equals(oldname)) {
                selectedIterator.remove();
                selectedIterator.add(newname);
            }
        }
        dataModelEdited = true;

    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }
}
