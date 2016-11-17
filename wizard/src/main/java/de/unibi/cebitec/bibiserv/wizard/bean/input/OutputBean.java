package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.DescriptionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.InputOutputType;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.InputOutputBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiObjectProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;

/**
 * This bean is used to manage user input from output.xhtml
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class OutputBean extends DescriptionBean {

    private static final String ALLSTRING = "all";
    // Output Data
    private String name;
    private String shortDescrition;
    private String representation;
    private String option;
    private String handling;
    private byte[] example;
    private String exampleName;
    // Loading and Saving
    private String loadedFrom;
    private OutputManager manager;
    private List<String> outputNameList;
    private boolean outputsEmpty;
    private boolean renderLoadedFrom;
    // temp data of dropdowns
    private String currentContent;
    private String currentDatastructure;
    private String currentFormat;
    // List of Contents, Datastructures and Formats
    private List<Tupel<String, String>> contents, datastructures, formats, representations;

    private static final Tupel.TupelStringComparator TUPEL_COMPERATOR = new Tupel.TupelStringComparator();
    
    private boolean renderUnsavedChanges;

    public OutputBean() {

        xhtml = "output.xhtml";

        FacesContext context = FacesContext.getCurrentInstance();
        manager = (OutputManager) context.getApplication().
                evaluateExpressionGet(context, "#{outputManager}",
                OutputManager.class);
        position = "";
        resetAll();
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

    public void dropDownValueChangeMethodContent() {
        filter();
    }

    public void dropDownValueChangeMethodFormat() {
        filter();
    }

    public void dropDownValueChangeMethodDataStructure() {
        filter();
    }

/**
     * Called by filter dropdown menus.
     */
    private void filter() {

        String inputFormat = currentFormat;
        String inputContent = currentContent;
        String inputDatastructure = currentDatastructure;

        List<BiBiObjectProperty> representations = new ArrayList<BiBiObjectProperty>();

        // Questioner need null if empty, but dropdowns provide ALLSTRING
        if (inputFormat.equals(ALLSTRING)) {
            inputFormat = null;
        }

        if (inputContent.equals(ALLSTRING)) {
            inputContent = null;
        }

        if (inputDatastructure.equals(ALLSTRING)) {
            inputDatastructure = null;
        }



        //  get data for format from OntoAccess
        Map<String, Collection> answerMap = TypeOntoQuestioner.
                getOtherInfoFrom_F_C_or_DS(null, inputContent,
                inputDatastructure);
        // write to dropdownLists
        ontoTypeCollectionToTupelLists(answerMap, false, true,
                false);
        representations.addAll((Collection<BiBiObjectProperty>) answerMap.get(
                "representations"));

        //  get data for content from OntoAccess
        answerMap = TypeOntoQuestioner.getOtherInfoFrom_F_C_or_DS(inputFormat,
                null,
                inputDatastructure);
        // write to dropdownLists
        ontoTypeCollectionToTupelLists(answerMap, true, false,
                false);
        representations.retainAll((Collection<BiBiObjectProperty>) answerMap.get(
                "representations"));

        //  get data for datastructure from OntoAccess
        answerMap = TypeOntoQuestioner.getOtherInfoFrom_F_C_or_DS(inputFormat,
                inputContent,
                null);
        // write to dropdownLists
        ontoTypeCollectionToTupelLists(answerMap, false, false,
                true);
        representations.retainAll((Collection<BiBiObjectProperty>) answerMap.get(
                "representations"));

        representationsToTupelList(representations);
    }

    /**
     * Writes the map gotten from TypeOntoQuestioner.getOtherInfoFrom_F_C_or_DS
     * into the TupelLists used by dropdowns. Tupel.First = Label, Tupel.second = ID
     * @param map
     * @param updateContent are the contents updated?
     * @param updateFormat are the formats updated?
     * @param updateDatastructures are the datastrukture updated?
     */
    private void ontoTypeCollectionToTupelLists(Map map, boolean updateContent,
            boolean updateFormat, boolean updateDatastructures) {

        if (updateContent) {
            contents = new ArrayList<Tupel<String, String>>();
            Iterator<BiBiObjectProperty> contentIterator =
                    ((Collection<BiBiObjectProperty>) map.get("contents")).iterator();

            while (contentIterator.hasNext()) {
                BiBiObjectProperty content = contentIterator.next();
                contents.add(new Tupel<String, String>(content.getLabel(),
                        content.getKey()));
            }
            Collections.sort(contents,TUPEL_COMPERATOR);
        }

        if (updateFormat) {
            formats = new ArrayList<Tupel<String, String>>();
            Iterator<BiBiObjectProperty> formatsIterator =
                    ((Collection<BiBiObjectProperty>) map.get("formats")).iterator();

            while (formatsIterator.hasNext()) {
                BiBiObjectProperty content = formatsIterator.next();
                formats.add(new Tupel<String, String>(content.getLabel(),
                        content.getKey()));
            }
            Collections.sort(formats,TUPEL_COMPERATOR);
        }

        if (updateDatastructures) {
            datastructures = new ArrayList<Tupel<String, String>>();
            Iterator<BiBiObjectProperty> dataIterator =
                    ((Collection<BiBiObjectProperty>) map.get("datastructures")).
                    iterator();

            while (dataIterator.hasNext()) {
                BiBiObjectProperty content = dataIterator.next();
                datastructures.add(new Tupel<String, String>(content.getLabel(),
                        content.getKey()));
            }
            Collections.sort(datastructures,TUPEL_COMPERATOR);
        }
    }

    /**
     * convert a given collection of representations to the tupel list
     * @param representationList representations to convert
     */
    private void representationsToTupelList(
            Collection<BiBiObjectProperty> representationList) {

        representations = new ArrayList<Tupel<String, String>>();
        Iterator<BiBiObjectProperty> representaionIterator = representationList.iterator();

        while (representaionIterator.hasNext()) {
            BiBiObjectProperty content = representaionIterator.next();
            representations.add(new Tupel<String, String>(content.getLabel(), content.getKey())); 
        }
        Collections.sort(representations,TUPEL_COMPERATOR);
    }

    /**
     * Loads the Output with name.
     * @param name name of the Output.
     */
    public void loadOutput(String name) {
        calcPostionString();
        TinputOutput input;
        try {
            input = manager.getOutputByName(name);
        } catch (BeansException ex) {
            // should not occur when used correctly
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("couldNotLoad"), ""));
            return;
        }

        resetAll();

        // mark we want to change existing input
        this.loadedFrom = name;
        renderLoadedFrom = true;


        // set data of loaded object
        this.name = input.getName().get(0).getValue();
        this.shortDescrition = input.getShortDescription().get(0).getValue();
        this.representation = input.getType();
        System.out.println(representation);
        this.description = (String) input.getDescription().get(0).getContent().
                get(0);
        if (input.isSetOption()) {
            this.option = input.getOption();
        }
        this.handling = input.getHandling();
        if (input.isSetExample()) {
            this.example = input.getExample();
            this.exampleName = PropertyManager.getProperty("outputFileUploaded");
        }

    }

    /**
     * Called when user uploads a file.
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        renderUnsavedChanges = true;
        example = event.getFile().getContents();
        exampleName = event.getFile().getFileName();
        // if to lang cut to 30 chars
        if (exampleName.length() > 30) {
            exampleName = exampleName.substring(0, 28) + "..";
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, PropertyManager.getProperty("fileUploaded"), ""));
    }

    /**
     * Resets data of bean.
     */
    private void resetAll() {

        loadedFrom = "";
        name = "";
        renderLoadedFrom = false;
        description = "";
        shortDescrition = "";
        representation = "";
        option = "";
        handling = "";
        example = null;
        exampleName = "";

        currentContent = ALLSTRING;
        currentDatastructure = ALLSTRING;
        currentFormat = ALLSTRING;

        outputNameList = manager.getAllNames();
        outputsEmpty = manager.isEmpty();

        Map<String, Collection> answerMap = TypeOntoQuestioner.
                getOtherInfoFrom_F_C_or_DS(null, null, null);
        ontoTypeCollectionToTupelLists(answerMap, true, true, true);
        representationsToTupelList(answerMap.get(
                "representations"));
        
        renderUnsavedChanges = false;
    }

    /**
     * Validates input and adds warnings to context.
     *
     * @return  true: everything ok; false: smth is wrong
     */
    private boolean validate() {

        boolean ret = true;

        if (name.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            ret = false;
        }

        if (description.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("descriptionError"), ""));
            ret = false;
        }

        if (shortDescrition.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("shortDescriptionError"), ""));
            ret = false;
        }
        return ret;
    }

    private boolean saveAll() {

        if (!validate()) {
            return false;
        }

        TinputOutput newOutput = InputOutputBuilder.createInputOutPut(name,
                shortDescrition, description, BasicBeanData.StandardLanguage,
                option, representation, handling, example,
                InputOutputType.output, false);
        if (loadedFrom.isEmpty()) { // not loaded
            try {
                manager.addOutput(newOutput);
            } catch (BeansException ex) {  // could not be added
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("outputAlreadyExistsError"),
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
                manager.editOutput(loadedFrom, newOutput); // try editing
            } catch (BeansException ex) {//could not edit
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("outputAlreadyExistsError"),
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
        outputNameList = manager.getAllNames();
        outputsEmpty = manager.isEmpty();

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
        return "function.xhtml?faces-redirect=true";
    }

    public void newOutput() {
        resetAll();
        calcPostionString();
    }

    public void editOutput(String name) {
        loadOutput(name);
    }

    public void removeOutput(String name) {
        try {
            manager.removeOutputByName(name);
            //reset inputList
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
            outputNameList = manager.getAllNames();
            outputsEmpty = manager.isEmpty();
        } catch (BeansException ex) {
            // should not happen when used right
        }
    }

    public String getExampleName() {

        return exampleName;
    }

    public String getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(String currentContent) {
        this.currentContent = currentContent;
    }

    public String getCurrentDatastructure() {
        return currentDatastructure;
    }

    public void setCurrentDatastructure(String currentDatastructure) {
        this.currentDatastructure = currentDatastructure;
    }

    public String getCurrentFormat() {
        return currentFormat;
    }

    public void setCurrentFormat(String currentFormat) {
        this.currentFormat = currentFormat;
    }

    public String getHandling() {
        return handling;
    }

    public void setHandling(String handling) {
        this.handling = handling;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    public List<Tupel<String, String>> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<Tupel<String, String>> representations) {
        this.representations = representations;
    }

    public String getShortDescrition() {
        return shortDescrition;
    }

    public void setShortDescrition(String shortDescrition) {
        this.shortDescrition = shortDescrition;
    }

    public List<Tupel<String, String>> getContents() {
        return contents;
    }

    public List<Tupel<String, String>> getDatastructures() {
        return datastructures;
    }

    public List<Tupel<String, String>> getFormats() {
        return formats;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    public List<String> getOutputNameList() {
        return outputNameList;
    }

    public boolean isOutputsEmpty() {
        return outputsEmpty;
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
