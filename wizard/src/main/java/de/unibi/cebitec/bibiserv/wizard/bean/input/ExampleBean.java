package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.Example;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleInputStore;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleParameterEnumStore;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleParameterStore;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleStore;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.HandlingType;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.IdRefType;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ExampleManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyTester;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterValidator;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.cebitec.bibiserv.wizard.tools.Base64DeAndEncoder;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *  This bean is used to manage data of examples for example.xhtml
 *  @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ExampleBean {

    //basic data
    private boolean renderLoadedFrom;
    private String loadedFrom;
    private String name;
    private String shortDescription;
    private String functionName;
    // input file
    private List<ExampleInputStore> exampleInputFileList;
    private ExampleInputStore selectedInput;
    private String fileContent;
    private boolean selectionChange;
    //input string
    private List<ExampleInputStore> exampleInputStringList;
    private DataModel<ExampleInputStore> exampleInputStringDataModel;
    private boolean isExampleInputStringEdited;
    //parameter
    private List<ExampleParameterStore> exampleParameterList;
    private DataModel<ExampleParameterStore> exampleParameterListDataModel;
    private boolean isExampleParameterEdited;
    //parameter enum
    private List<ExampleParameterEnumStore> exampleParameterEnumList;
    // exampleList
    private boolean examplesEmpty;
    private List<Tupel<String, Boolean>> exampleNameList;
    // manager
    private ExampleManager exampleManager;
    // raw function for validation
    private Tfunction function;
    
    
    private String position;
    private boolean renderUnsavedChanges;

    /**
     * Called when user uploads a file.
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        renderUnsavedChanges = true;
        
        if (selectedInput != null) {
            String base64 = Base64DeAndEncoder.ByteArrayToBase64(event.getFile().
                    getContents());
            selectedInput.setValue(base64);
            selectedInput.setNonbase64(PropertyManager.getProperty(
                    "inputFileUploaded"));
            fileContent = Base64DeAndEncoder.Base64ToString(base64);
        }
    }

    /**
     * Called when file was edited with textarea.
     */
    public void updateFileContent(){
        renderUnsavedChanges = true;
        if (selectedInput != null) {
            String base64 = Base64DeAndEncoder.StringToBase64(fileContent);
            selectedInput.setValue(base64);
            selectedInput.setNonbase64(PropertyManager.getProperty(
                    "inputFileUploaded"));
        }
    }

    private void getAvailableData() {
        exampleNameList = exampleManager.getAllNameValidTupel();
        examplesEmpty = exampleManager.isEmpty();
    }

    /**
     * Sets the new manager and loads resets everything.
     * @param newManager
     */
    public void setManager(ExampleManager newManager, String functionName,
            Tfunction function) {
        this.functionName = functionName;
        exampleManager = newManager;
        this.function = function;
        resetAll();
        getAvailableData();
    }

    private void loadStoreList(List<ExampleStore> storeList) {
        exampleInputFileList =
                new ArrayList<ExampleInputStore>();
        exampleInputStringList =
                new ArrayList<ExampleInputStore>();
        exampleParameterList =
                new ArrayList<ExampleParameterStore>();
        exampleParameterEnumList =
                new ArrayList<ExampleParameterEnumStore>();

        for (ExampleStore store : storeList) {
            if (store instanceof ExampleInputStore) {
                ExampleInputStore input = (ExampleInputStore) store;
                if (input.getHandling() == HandlingType.argument) {
                    exampleInputStringList.add(input);
                } else if(input.getHandling() == HandlingType.stdin || input.getHandling() == HandlingType.file){
                    exampleInputFileList.add(input);
                }
            } else if (store instanceof ExampleParameterEnumStore) {
                exampleParameterEnumList.add((ExampleParameterEnumStore) store);
            } else if (store instanceof ExampleParameterStore) {
                exampleParameterList.add((ExampleParameterStore) store);
            }
        }
        if (!exampleInputFileList.isEmpty()) {
            selectedInput = exampleInputFileList.get(0);
        }
        isExampleInputStringEdited = true;
        isExampleParameterEdited = true;
    }

    private boolean loadExample(String name) {
        calcPostionString();
        Example example;
        try {
            example = exampleManager.getExampleByName(name);
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openExampleError"),
                    ""));
            return false;
        }
        renderUnsavedChanges = false;
        this.name = example.getName();
        this.shortDescription = example.getDescription();
        loadStoreList(example.getExamples());
        
        if(selectedInput!=null){
            fileContent = Base64DeAndEncoder.Base64ToString(selectedInput.getValue());
        }
        
        return true;
    }

    public void resetAll() {
        loadedFrom = "";
        renderLoadedFrom = false;
        name = "";
        shortDescription = "";
        selectionChange=false;
        selectedInput=null;
        fileContent="";
        renderUnsavedChanges = false;
        List<ExampleStore> newEmpty = new ArrayList<ExampleStore>();
        for (ExampleStore store : exampleManager.getEmptyBaseStore()) {
            newEmpty.add(store.clone());
        }
        loadStoreList(newEmpty);
    }

    public void newExample() {
        calcPostionString();
        resetAll();
    }

    public void editExample(String name) {
        if (loadExample(name)) {
            loadedFrom = name;
            renderLoadedFrom = true;
        }
    }

    public void copyExample(String name) {
        loadExample(name);
        loadedFrom = "";
        renderLoadedFrom = false;
    }

    public void removeExample(String name) {
        try {
            exampleManager.removeExampleByName(name);
            if (loadedFrom.equals(name)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
            getAvailableData();
        } catch (BeansException ex) {
            // can't happen
        }
    }

    public String returnToPrev() {
        return "function.xhtml?faces-redirect=true";
    }

    public String cancel() {
        return "function.xhtml?faces-redirect=true";
    }

    private void updateBase64Strings() {
        for (ExampleInputStore store : exampleInputStringList) {
            store.setValue(Base64DeAndEncoder.StringToBase64(
                    store.getNonbase64()));
        }
    }

    private void updateEnumValues() {
        for (ExampleParameterEnumStore store : exampleParameterEnumList) {
            String newValue = "";
            for (int i = 0; i < store.getSelected().size(); i++) {
                newValue += store.getSelected().get(i);
                if (i < store.getSelected().size() - 1) {
                    newValue += store.getSeparator();
                }
            }
            store.setValue(newValue);
        }
    }

    /**
     * Test if the name is set.
     * @return true: name is set; false: no name there
     */
    private boolean validateName() {
        if (name.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            return false;
        }
        return true;
    }

    private boolean validateInputs() {
        boolean ret = true;
        // errors for empty storages have been disabled for now!
        // comment in again if you want to force the user to give a value for every input
//        for (ExampleInputStore store : exampleInputStringList) {
//            if (store.getValue().isEmpty()) {
//                ret = false;
//                FacesContext.getCurrentInstance().addMessage(null,
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                        PropertyManager.getProperty("emptyInputError") + ": "
//                        + store.getName(), ""));
//            }
//        }
//
//        for (ExampleInputStore store : exampleInputFileList) {
//            if (store.getValue().isEmpty()) {
//                ret = false;
//                FacesContext.getCurrentInstance().addMessage(null,
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                        PropertyManager.getProperty("emptyInputError") + ": "
//                        + store.getName(), ""));
//            }
//        }

        return ret;
    }

    private boolean validateParameter() {
        boolean ret = true;
        for (ExampleParameterStore store : exampleParameterList) {
            // test if value is empty and defaultvalue is set
            if(!store.getValue().isEmpty()) {
                
            switch (store.getPrimitive()) {
                case BOOLEAN:
                    if (!ParameterValidator.validateBoolean(store.getValue())) {
                        ret = false;
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "invalidBooleanExampleError") + ": "
                                + store.getName(), ""));
                    }
                    break;
                case DATETIME:
                    if (!ParameterValidator.validateDateTime(store.getValue())) {
                        ret = false;
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "invalidDateTimeExampleError") + ": "
                                + store.getName(), ""));
                    }
                    break;
                case FLOAT:
                    if (!ParameterValidator.validateFloat(store.getValue(),
                            store.getMin(), store.isIncludeMin(), store.getMax(),
                            store.isIncludeMax())) {
                        ret = false;
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "invalidFloatExampleError") + ": "
                                + store.getName(), ""));
                    }
                    break;
                case INT:
                    if (!ParameterValidator.validateInt(store.getValue(),
                            (int) store.getMin(), store.isIncludeMin(),
                            (int) store.getMax(), store.isIncludeMax())) {
                        ret = false;
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "invalidIntExampleError") + ": "
                                + store.getName(), ""));
                    }
                    break;
                case STRING:
                    if (!ParameterValidator.validateString(store.getValue(),
                            store.getMinLength(),
                            store.getMaxLength(), store.getRegexp())) {
                        ret = false;
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "invalidStringExampleError") + ": "
                                + store.getName(), ""));
                    }
                    break;
            }                   
            }
        }
        return ret;
    }

    private boolean validateParameterEnum() {
        boolean ret = true;
        for (ExampleParameterEnumStore store : exampleParameterEnumList) {
            if (store.getSelected().size() > store.getMaxOccur()) {
                ret = false;
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "tooManyEnumsSelectedError") + ": "
                        + store.getName(), ""));
            }
            if (store.getSelected().size()>0 && store.getSelected().size() < store.getMinOccur()) {
                ret = false;
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "notEnoughEnumsSelectedError") + ": "
                        + store.getName(), ""));
            }
        }
        return ret;
    }

    /**
     * Validate the example and add error messages to context.
     * @return true: everything is OK; false: somethign is wrong
     */
    private boolean validate() {
        boolean ret = true;
        if (!validateInputs()) {
            ret = false;
        }
        if (!validateParameter()) {
            ret = false;
        }
        if (!validateParameterEnum()) {
            ret = false;
        }
        return ret;
    }

    /**
     * Validate all Dependencies with the examples in this manager.
     * @return true: all examples validated correctly
     */
    public boolean validateAllDependencys() {
        List<List<Tupel<String, String>>> examples =
                new ArrayList<List<Tupel<String, String>>>();
        List<Example> exampleList = new ArrayList<Example>();
        // loop through all contained examples
        for (Example example : exampleManager.getValues()) {
            exampleList.add(example);

            // add parameters for each example as new list
            List<Tupel<String, String>> parameters =
                    new ArrayList<Tupel<String, String>>();

            for (ExampleStore store : example.getExamples()) {
                if (store.getType() == IdRefType.parameter) {
                    if (!store.getValue().isEmpty()) {
                        parameters.add(new Tupel(store.getName(),
                                store.getValue()));
                        
                    } else {
                        String defaultVal = "";
                        if (store instanceof ExampleParameterStore) {
                            defaultVal = ((ExampleParameterStore) store).
                                    getDefaultValue();
                        } else if (store instanceof ExampleParameterEnumStore) {
                            defaultVal = ((ExampleParameterEnumStore) store).
                                    getDefaultValue();
                        }
                        if (!defaultVal.isEmpty()) {
                            parameters.add(new Tupel(store.getName(),
                                    defaultVal));
                        }
                    }
                }
            }
            examples.add(parameters);
        }

        // validate and reset all examples dependencyValidDation
        return validateDependency(examples, exampleList);
    }

    /**
     * Validate only the given example
     * @param example Example to validate
     * @return if dependency is correct.
     */
    private boolean validateCurrentDependency(Example example) {
        List<List<Tupel<String, String>>> examples =
                new ArrayList<List<Tupel<String, String>>>();
        List<Tupel<String, String>> parameters =
                new ArrayList<Tupel<String, String>>();

        for (ExampleStore store : example.getExamples()) {
            if (store.getType() == IdRefType.parameter) {
                if (!store.getValue().isEmpty()) {
                    parameters.add(new Tupel(store.getName(),
                            store.getValue()));
                } else {
                    String defaultVal = "";
                    if (store instanceof ExampleParameterStore) {
                        defaultVal = ((ExampleParameterStore) store).
                                getDefaultValue();
                    } else if (store instanceof ExampleParameterEnumStore) {
                        defaultVal = ((ExampleParameterEnumStore) store).
                                getDefaultValue();
                    }
                    if (!defaultVal.isEmpty()) {
                        parameters.add(new Tupel(store.getName(),
                                defaultVal));
                    }
                }
            }
        }
        examples.add(parameters);

        List<Example> exampleList = new ArrayList<Example>();
        exampleList.add(example);

        boolean valid = validateDependency(examples, exampleList);
        if(!valid){
            FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "depUnvalidExample"), ""));
        } else if(!example.isDependencyValid()){
            FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "depNotSatiesfiedExample")+" "+example.getDependencyReason(), ""));
        }
        
        return valid & example.isDependencyValid();
    }

    /**
     * Validate if the dependencies for this function are correct.
     * @param examples parameters to be testet with dependencies for all examples
     * @param exampleList list of all Exmaple object to reset (same order as in examples list)
     * @return true: dependencies are valid; false: dependencies are not valid
     */
    private boolean validateDependency(
            List<List<Tupel<String, String>>> examples,
            List<Example> exampleList) {

        ParameterDependencyTester tester = new ParameterDependencyTester();

        for (Example example : exampleList) {
            example.setDependencyValid(true);
            example.setDependencyReason("");
        }

        boolean ret = true;
        // test for each dependency
        for (Tfunction.Depref depRef : function.getDepref()) {
            Tdependency dependency = (Tdependency) depRef.getRef();
            // run test
            if (!tester.testDependency(function, dependency, examples)) {
                ret = false;

                // all examples are incorrect since
                for (Example example : exampleList) {
                    example.setDependencyValid(false);
                }
            } else {
                // reset all validation for all incorrect examples
                for (Tupel<Integer,List<String>> wrong : tester.getIncorrectExamples()) {
                    Example ex = exampleList.get(wrong.getFirst());
                    ex.setDependencyValid(false);
                    String dependencyReason = "";
                    boolean first = true;
                    for(String name:wrong.getSecond()){
                        if(first){
                            first=false;
                            dependencyReason += name;
                        } else {
                            dependencyReason +=", "+name;
                        }
                    }
                    ex.setDependencyReason(dependencyReason);
                }
            }
        }
        return ret;
    }

    /**
     * Clears all messages from corrent context.
     */
    private void clearMessages() {
        Iterator iter = FacesContext.getCurrentInstance().getMessages();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
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

    /**
     * Saves the function with or without errors. Add callbackParams for showing
     * of PopUp. Add further messages to context.
     * @param isReturn is this called from save (false) order saveReturn (true)
     * @return true: was saved; false: was not saved
     */
    private boolean saveAll(boolean isReturn) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (!validateName()) {
            context.addCallbackParam("show", true);
            context.addCallbackParam("errors", true);
            context.addCallbackParam("saved", false);
            context.addCallbackParam("returns", false);
            return false;
        }

        // update values
        updateBase64Strings();
        updateEnumValues();

        // validate
        boolean valid = validate();

        // creat exmaple
        List<ExampleStore> saveStore = new ArrayList<ExampleStore>();
        saveStore.addAll(exampleInputFileList);
        saveStore.addAll(exampleInputStringList);
        saveStore.addAll(exampleParameterList);
        saveStore.addAll(exampleParameterEnumList);
        Example newExample = new Example(name, shortDescription, saveStore,
                valid, true);

        // validate dependency and set in example
        boolean depvalid = validateCurrentDependency(newExample);

        if (loadedFrom.isEmpty()) { // not loaded
            try {
                exampleManager.addExample(newExample);
            } catch (BeansException ex) {  // could not be added
                clearMessages();
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "exampleAlreadyExistsError"),
                            ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("couldNotSave"), ""));
                }
                context.addCallbackParam("show", true);
                context.addCallbackParam("errors", true);
                context.addCallbackParam("saved", false);
                context.addCallbackParam("returns", false);
                return false;
            }
        } else { // function is loaded
            try {
                exampleManager.editExample(loadedFrom, newExample); // try editing
            } catch (BeansException ex) {//could not edit
                clearMessages();
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "exampleAlreadyExistsError"),
                            ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("couldNotSave"), ""));
                }
                context.addCallbackParam("show", true);
                context.addCallbackParam("errors", true);
                context.addCallbackParam("saved", false);
                context.addCallbackParam("returns", false);
                return false;
            }
        }
        if (valid & depvalid) {
            if (isReturn) {
                context.addCallbackParam("show", false);
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                        PropertyManager.getProperty("saveSuccesful"), ""));
                context.addCallbackParam("show", true);
            }
            context.addCallbackParam("errors", false);
            context.addCallbackParam("saved", true);
            context.addCallbackParam("returns", isReturn);
        } else {
            context.addCallbackParam("show", true);
            context.addCallbackParam("errors", true);
            context.addCallbackParam("saved", true);
            context.addCallbackParam("returns", isReturn);
        }
        renderUnsavedChanges = false;
        return true;
    }

    public void save() {
        if (saveAll(false)) {
            loadedFrom = name;
            renderLoadedFrom = true;
            getAvailableData();
        }
    }

    public void saveReturn() {
        // did it save
        if (saveAll(true)) {
            loadedFrom = name;
            renderLoadedFrom = true;
            getAvailableData();
            // if yes did an error occur?
            RequestContext context = RequestContext.getCurrentInstance();
            if (!(Boolean) context.getCallbackParams().get("errors")) {

                // no error occured
                // redirect from javax context
                FacesContext ctx = FacesContext.getCurrentInstance();
                ExternalContext extContext = ctx.getExternalContext();
                String url = extContext.encodeActionURL(ctx.getApplication().
                        getViewHandler().getActionURL(ctx,
                        "/function.xhtml"));

                try {
                    extContext.redirect(url);
                } catch (IOException ioe) {
                    // ignore
                }
            }
        }
    }

    public DataModel<ExampleInputStore> getExampleInputStringListDataModel() {
        if (isExampleInputStringEdited) {
            exampleInputStringDataModel =
                    new ListDataModel<ExampleInputStore>(exampleInputStringList);
        }
        isExampleInputStringEdited = false;

        return exampleInputStringDataModel;
    }

    public DataModel<ExampleParameterStore> getExampleParameterListDataModel() {
        if (isExampleParameterEdited) {
            exampleParameterListDataModel =
                    new ListDataModel<ExampleParameterStore>(
                    exampleParameterList);
        }
        isExampleParameterEdited = false;

        return exampleParameterListDataModel;
    }

    public void onRowSelect(SelectEvent se) {
        renderUnsavedChanges = true;
        selectedInput = (ExampleInputStore) se.getObject();
        fileContent = Base64DeAndEncoder.Base64ToString(selectedInput.getValue());
        selectionChange=true;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        renderUnsavedChanges = true;
        if(selectionChange){
            selectionChange=false;
        } else {
            this.fileContent = fileContent;
        }
    }

    public boolean isRenderUploader() {
        return !exampleInputFileList.isEmpty();
    }

    public List<ExampleInputStore> getExampleInputFileList() {
        return exampleInputFileList;
    }

    public List<ExampleInputStore> getExampleInputStringList() {
        return exampleInputStringList;
    }

    public List<Tupel<String, Boolean>> getExampleNameList() {
        return exampleNameList;
    }

    public List<ExampleParameterEnumStore> getExampleParameterEnumList() {
        return exampleParameterEnumList;
    }

    public List<ExampleParameterStore> getExampleParameterList() {
        return exampleParameterList;
    }

    public boolean isExamplesEmpty() {
        return examplesEmpty;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public String getName() {
        return name;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    public ExampleInputStore getSelectedInput() {
        return selectedInput;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelectedInput(ExampleInputStore selectedInput) {
        this.selectedInput = selectedInput;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean isExampleInputFileListNotEmpty(){
        return !exampleInputFileList.isEmpty();
    }

    public boolean isExampleInputStringListNotEmpty(){
        return !exampleInputStringList.isEmpty();
    }

    public boolean isExampleParameterListNotEmpty(){
        return !exampleParameterList.isEmpty();
    }

    public boolean isExampleParameterEnumListNotEmpty(){
        return !exampleParameterEnumList.isEmpty();
    }
    
    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }

    public void setRenderUnsavedChanges(boolean renderUnsavedChanges) {
        this.renderUnsavedChanges = renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }

    public String getPosition() {
        return position;
    } 
}
