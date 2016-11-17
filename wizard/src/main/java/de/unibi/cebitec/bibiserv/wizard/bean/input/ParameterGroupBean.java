package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.DescriptionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterGroupBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterGroupManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

/**
 * This beans manages user input auf ParameterGroup data.
 * Is called by ParameterGroupManager for reloading when parameters changed.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ParameterGroupBean extends DescriptionBean {

    //generall data
    private String name;
    private String shortDescription;
    private String loadedFrom;
    private String nameDisplay;
    //showing and hiding
    private boolean renderLoadedFrom;
    //data from other beans
    private DataModel<Tupel<Integer, String>> selectedParametersWithId;
    private List<String> selectedParameters;
    private boolean isParametersEdited;
    private DataModel<Tupel<Integer, String>> selectedParameterGroupsWithId;
    private List<String> selectedParameterGroups;
    private boolean isParameterGroupEdited;
    private List<String> parameterNameList;
    private List<String> parameterGroupNameList; // from dropdown, without circular references
    private List<String> parameterGroupNameListFull; // all for box
    private boolean parametersEmpty;
    private boolean parameterGroupsEmpty;
    //manager
    private ParameterManager parameterManager;
    private ParameterGroupManager parameterGroupManager;
    //beans
    private ParameterBean parameterBean;
    
    private boolean renderUnsavedChanges;

    public ParameterGroupBean() {

        xhtml = "parameterGroup.xhtml";

        FacesContext context = FacesContext.getCurrentInstance();
        parameterManager = (ParameterManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterManager}",
                ParameterManager.class);
        parameterGroupManager = (ParameterGroupManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterGroupManager}",
                ParameterGroupManager.class);
        parameterBean = (ParameterBean) context.getApplication().
                evaluateExpressionGet(context, "#{parameterBean}",
                ParameterBean.class);

        resetAll();
        refillSelected();
    }

    /**
     * tries to load parameter group with speciefied name
     * @param name name of the parameter to load
     */
    private void loadParameterGroup(String name) {
        // reset current data
        resetAll();
        //try loading TparamGroupObject
        TparamGroup group;
        try {
            group = parameterGroupManager.getParameterGroupByName(name);
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openParameterGroupError"),
                    ""));
            return;
        }


        // set data
        if (group.getName().size() > 0) {
            this.nameDisplay = group.getName().get(0).getValue();
        }
        this.name = name;
        this.loadedFrom = name;
        this.renderLoadedFrom = true;

        parameterGroupNameList = parameterGroupManager.getAllNamesNotContaining(
                name);

        if (!group.getShortDescription().isEmpty()) {
            this.shortDescription = group.getShortDescription().get(0).getValue();
        } else {
            this.shortDescription = "";
        }

        if (!group.getDescription().isEmpty() && !group.getDescription().get(0).getContent().isEmpty()) {
            this.description = (String) group.getDescription().get(0).getContent().
                    get(0);
        } else {
            this.description = "";
        }

        //set dropdown data
        for (Object ref : group.getParamrefOrParamGroupref()) {

            Object ob = null;
            if (ref instanceof TparamGroup.ParamGroupref) {
                ob = ((TparamGroup.ParamGroupref) ref).getRef();
            } else if (ref instanceof TparamGroup.Paramref) {
                ob = ((TparamGroup.Paramref) ref).getRef();
            }

            if (ob instanceof TparamGroup) {
                TparamGroup param = (TparamGroup) ob;
                try {
                    selectedParameterGroups.add(parameterGroupManager.getParameterGroupRefByFullId(param.getId()));
                } catch (BeansException ex) {
                }
            } else if (ob instanceof Tparam) {
                Tparam param = (Tparam) ob;
                selectedParameters.add(param.getName().get(0).getValue());
            } else if (ob instanceof TenumParam) {
                TenumParam param = (TenumParam) ob;
                selectedParameters.add(param.getName().get(0).getValue());
            }
        }
        refillSelected();
        renderUnsavedChanges = false;
    }

    /**
     * Gets events of dropdown and changes selected list accordingly.
     */
    public void dropDownValueParameterGroupChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = selectedParameterGroupsWithId.getRowData();

        selectedParameterGroups.set(currentTupel.getFirst(), value);
        isParameterGroupEdited = true;
        renderUnsavedChanges = true;
    }

    /**
     * Gets events of dropdown and changes selected list accordingly.
     */
    public void dropDownValueParameterChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty())  {
            return;
        }
        Tupel<Integer, String> currentTupel = selectedParametersWithId.getRowData();

        selectedParameters.set(currentTupel.getFirst(), value);
        isParametersEdited = true;
        renderUnsavedChanges = true;
    }

    public void addParamDropdown(int index) {
        selectedParameters.add(index + 1, "");
        isParametersEdited = true;
    }

    public void removeParamDropdown(int index) {
        selectedParameters.remove(index);
        isParametersEdited = true;
    }

    public void addGroupDropdown(int index) {
        selectedParameterGroups.add(index + 1, "");
        isParameterGroupEdited = true;
    }

    public void removeGroupDropdown(int index) {
        selectedParameterGroups.remove(index);
        isParameterGroupEdited = true;
    }

    public String newParameter() {
        parameterBean.newParameter();
        return "parameter.xhtml?faces-redirect=true";
    }

    public String editParameter(String name) {
        parameterBean.editParameter(name);
        return "parameter.xhtml?faces-redirect=true";
    }

    public void removeParameter(String name) {
        try {
            // reload called at the end in parameterGroupManager
            parameterManager.removeParameterByName(name);
        } catch (BeansException ex) {
            //should not occur
        }
    }

    public void newParameterGroup() {
        resetAll();
        refillSelected();
    }

    public void editParameterGroup(String name) {
        loadParameterGroup(name);
    }

    public void removeParameterGroup(String name) {
        try {
            parameterGroupManager.removeParameterGroupByName(name);
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
            reload();
        } catch (BeansException ex) {
            // should not happen
        }
    }

    /**
     * Validates the current content and adds error messages.
     * @return true: everything is OK; false: smth is wrong
     */
    private boolean validate() {
        boolean ret = true;

        if (name.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            ret = false;
        }

        int selectedCount = 0;

        for (String str : selectedParameters) {
            if (!str.isEmpty()) {
                selectedCount++;
            }
        }
        for (String str : selectedParameterGroups) {
            if (!str.isEmpty()) {
                selectedCount++;
            }
        }

        if (selectedCount == 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("emptyParameterGroupError"), ""));
            ret = false;
        }

        return ret;
    }

    /**
     * Remove all empty selected parameters and parametergroups.
     */
    private void removeEmptySelected() {
        Iterator<String> paramIterator = selectedParameters.iterator();
        while (paramIterator.hasNext()) {
            if (paramIterator.next().isEmpty()) {
                paramIterator.remove();
            }
        }
        Iterator<String> paramGroupIterator = selectedParameterGroups.iterator();
        while (paramGroupIterator.hasNext()) {
            if (paramGroupIterator.next().isEmpty()) {
                paramGroupIterator.remove();
            }
        }
    }

    private boolean saveAll() {
        if (!validate()) {
            return false;
        }

        removeEmptySelected();

        TparamGroup newGroup = ParameterGroupBuilder.createParameterGroup(name,
                nameDisplay, shortDescription, description,
                BasicBeanData.StandardLanguage,
                selectedParameters, selectedParameterGroups);
        refillSelected();
        isParametersEdited = true;
        isParameterGroupEdited = true;

        if (loadedFrom.isEmpty()) { // not loaded
            try {
                parameterGroupManager.addParameterGroup(newGroup, name);
            } catch (BeansException ex) {  // could not be added
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("paramGroupAlreadyExistsError"),
                            ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("couldNotSave"), ""));
                }

                return false;
            }
        } else { // already exited is loaded
            try {
                parameterGroupManager.editParameterGroup(loadedFrom, name, newGroup); // try editing
            } catch (BeansException ex) {//could not edit
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("paramGroupAlreadyExistsError"),
                            ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("couldNotSave"), ""));
                }
                return false;
            }
        }

        //renew managerlists
        parameterGroupNameListFull = parameterGroupManager.getAllNames();
        parameterGroupsEmpty = parameterGroupManager.isEmpty();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                PropertyManager.getProperty("saveSuccesful"), ""));

        renderUnsavedChanges = false;
        return true;
    }

    public void save() {
        if (saveAll()) {
            loadedFrom = name;
            renderLoadedFrom = true;
        }
    }

    public void saveReturn() {
        if (saveAll()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ExternalContext extContext = ctx.getExternalContext();
            String url = extContext.encodeActionURL(ctx.getApplication().
                    getViewHandler().getActionURL(ctx, "/function.xhtml"));
            try {
                extContext.redirect(url);
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public String cancel() {
        resetAll();
        refillSelected();
        return "function.xhtml?faces-redirect=true";
    }

    public void reload() {

        parameterGroupNameListFull = parameterGroupManager.getAllNames();
        if (!parameterGroupNameListFull.contains(loadedFrom)) {
            loadedFrom = "";
            renderLoadedFrom = false;
        }

        parameterGroupNameList = parameterGroupManager.getAllNamesNotContaining(
                loadedFrom);


        parameterNameList = parameterManager.getAllNames();
        parametersEmpty = parameterManager.isEmpty();
        parameterGroupsEmpty = parameterGroupManager.isEmpty();

        isParameterGroupEdited = true;
        isParametersEdited = true;
    }

    public void preRender() {
        parameterNameList = parameterManager.getAllNames();
        parametersEmpty = parameterManager.isEmpty();
        isParametersEdited = true;
        calcPostionString();
    }
    
    private void calcPostionString(){
        position = "";
        String unnamed = PropertyManager.getProperty("unnamed");
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            FunctionBean functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
            if (functionBean.getName().isEmpty()) {
                position += unnamed;
            } else {
                position += functionBean.getName();
            }
        }
    }

    private void resetAll() {
        //reset loaded
        loadedFrom = "";
        renderLoadedFrom = false;
        // reset normal data
        name = "";
        nameDisplay = "";
        shortDescription = "";
        description = "";
        //reset dropdown lists
        selectedParameters = new ArrayList<String>();
        isParametersEdited = true;
        selectedParameterGroups = new ArrayList<String>();
        isParameterGroupEdited = true;

        parameterNameList = parameterManager.getAllNames();
        parametersEmpty = parameterManager.isEmpty();

        parameterGroupNameListFull = parameterGroupManager.getAllNames();
        parameterGroupNameList = parameterGroupManager.getAllNamesNotContaining(
                name);
        parameterGroupsEmpty = parameterGroupManager.isEmpty();
        renderUnsavedChanges = false;
    }

    /**
     * Adds one empty element to the selected lists of they are empty.
     */
    private void refillSelected() {
        if (selectedParameters.isEmpty()) {
            selectedParameters.add("");
        }
        if (selectedParameterGroups.isEmpty()) {
            selectedParameterGroups.add("");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public List<String> getParameterGroupNameList() {
        return parameterGroupNameList;
    }

    public List<String> getParameterGroupNameListFull() {
        return parameterGroupNameListFull;
    }

    public boolean isParameterGroupsEmpty() {
        return parameterGroupsEmpty;
    }

    public List<String> getParameterNameList() {
        return parameterNameList;
    }

    public boolean isParametersEmpty() {
        return parametersEmpty;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    /**
     * Returns a datamodel containg tupel with index and selection.
     * This is only done if selectedParametergroups was edited.
     * The model is needed to retrieve rowdata from event.
     * @return new model
     */
    public DataModel<Tupel<Integer, String>> getSelectedParameterGroupsWithId() {
        if (isParameterGroupEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedParameterGroups) {
                ret.add(new Tupel(i, str));
                i++;
            }

            selectedParameterGroupsWithId =
                    new ListDataModel<Tupel<Integer, String>>(ret);
        }
        isParameterGroupEdited = false;
        return selectedParameterGroupsWithId;
    }

    /**
     * Returns a datamodel containg tupel with index and selection.
     * This is only done if selectedParametergroups was edited.
     * The model is needed to retrieve rowdata from event.
     * @return new model
     */
    public DataModel<Tupel<Integer, String>> getSelectedParametersWithId() {
        if (isParametersEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedParameters) {
                ret.add(new Tupel(i, str));
                i++;
            }

            selectedParametersWithId =
                    new ListDataModel<Tupel<Integer, String>>(ret);
        }
        isParametersEdited = false;
        return selectedParametersWithId;
    }

    public boolean isShowParameterGroupRemove() {
        return selectedParameterGroups.size() > 1;
    }

    public boolean isShowParameterRemove() {
        return selectedParameters.size() > 1;
    }

    public void paramNameChanged(String oldname, String newname) {
        reload();
        isParametersEdited = true;
        ListIterator<String> paramIterator = selectedParameters.listIterator();
        while (paramIterator.hasNext()) {
            String str = paramIterator.next();
            if (str.equals(oldname)) {
                paramIterator.remove();
                paramIterator.add(newname);
            }
        }
    }

    public void paramGroupNameChanged(String oldname, String newname) {
        reload();
        isParameterGroupEdited = true;
        ListIterator<String> paramGroupIterator = selectedParameterGroups.listIterator();
        while (paramGroupIterator.hasNext()) {
            String str = paramGroupIterator.next();
            if (str.equals(oldname)) {
                paramGroupIterator.remove();
                paramGroupIterator.add(newname);
            }
        }
    }

    public String getNameDisplay() {
        return nameDisplay;
    }

    public void setNameDisplay(String nameDisplay) {
        this.nameDisplay = nameDisplay;
    }
    
    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }
    
    @Override
     public void setDescription(String description) {
        renderUnsavedChanges = true;
        this.description = description;
    }  
    
}
