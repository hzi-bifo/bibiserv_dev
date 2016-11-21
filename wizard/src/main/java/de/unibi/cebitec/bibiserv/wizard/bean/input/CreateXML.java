package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.ImageFile;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.FileStates;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.AuthorManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.BasicInfoBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ExecutableInfoBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FileManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ImageFileManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.InputManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ManualManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputFileManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterGroupManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ReferenceManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ViewManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.WebstartManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.DependencyResolveNameException;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.cebitec.bibiserv.wizard.tools.CopyFactory;
import de.unibi.cebitec.bibiserv.wizard.tools.Disambiguator;
import de.unibi.cebitec.bibiserv.wizard.tools.FileUploadIDGenerator;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.cebitec.bibiserv.wizard.tools.OutputFileSystemManager;
import de.unibi.cebitec.bibiserv.wizard.tools.ReferenceMap;
import de.unibi.techfak.bibiserv.cms.ObjectFactory;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.techfak.bibiserv.cms.Texecutable;
import de.unibi.techfak.bibiserv.cms.Tfile;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Titem.CustomContent;
import de.unibi.techfak.bibiserv.cms.Titem.Description;
import de.unibi.techfak.bibiserv.cms.Tmanual;
import de.unibi.techfak.bibiserv.cms.ToutputFile;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import de.unibi.techfak.bibiserv.cms.Tperson;
import de.unibi.techfak.bibiserv.cms.Treferences;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import de.unibi.techfak.bibiserv.cms.Twebstart;
import de.unibi.techfak.bibiserv.cms.microhtml.Flow;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * The class creates to xml and gives corresponding user output
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class CreateXML {

    // All beans that are questioned during export process
    private FunctionManager functionManager;
    private FunctionSelectionBean functionSelection;
    private AuthorManager authorManager;
    private AuthorSelectionBean authorSelection;
    private BasicInfoBean basicInfoBean;
    private ExecutableInfoBean executableInfoBean;
    private FileManager fileManager;
    private ImageFileManager imageFileManager;
    private FileSelectionBean fileSelectionBean;
    private ImageFileSelectionBean imageFileSelectionBean;
    private EditFileBean editFileBean;
    private ViewManager viewManager;
    private ManualManager manualManager;
    private BasicInfoBuilder basicInfoBuilder;
    private ExecutableInfoBuilder executeableInfoBuilder;
    private ReferenceManager referenceManager;
    private ReferenceSelectionBean referenceSelectionBean;
    private ParameterGroupManager parameterGroupManager;
    private ParameterManager parameterManager;
    private InputManager inputManager;
    private OutputManager outputManager;
    private OutputFileManager outputFileManager;
    private WebstartManager webstartManager;
    private WebstartSelectionBean webstartSelectionBean;
    
    private static final String referenceExportFormat = "bibtex";
    // defines if the download-button is shown.
    private boolean renderLinks = false;
    // the xml-file containing the finished tool description.
    private File xmlFile;
    // defines whether the .zip-download-button is shown.
    private boolean renderCodegenLink = false;
    // the .zip file containing all generated code for the finished tool description.
    private File codegenFile;
    // the current tool name.
    private String toolName = "";
    /*
     * this list contains all image files that have been selected and shall
     * be part of the final tool description.
     */
    private ArrayList<File> finalImageFileList = new ArrayList<File>();
    /*
     * this list contains all downloadable files that have been selected and
     * shall
     * be part of the final tool description.
     */
    private ArrayList<File> finalDownloadFileList = new ArrayList<File>();
    // this StringBuilder contains all commandline-output of the BiBiServ code generation.
    private StringBuilder codegenOutput = new StringBuilder();
    // the thread where the code generation happens.
    private Thread codegenWorkingThread;
    // defines whether the code generation button is disabled
    private boolean codegenButtonDisabled = false;
    /**
     * is true as soon as the PrimeFaces-poll that shows the current command
     * line output for the code generation should be stopped.
     */
    private boolean stopCodegenPoll = false;
    // determines if the without_ws-option for code generation is set.
    private boolean withoutWS = false;
    // determines if the without_moby-option for code generation is set.
    private boolean withoutMoby = false;
    // determines if the without_vb-option for code generation is set.
    private boolean withoutVB = false;
    // determines if the without_sswap-option for code generation is set.
    private boolean withoutSSWAP = true;
    private static final String MICROHTMLNAMESPACE =
            "de.unibi.techfak.bibiserv.cms.microhtml";
    private static final String MINIHTMLNAMESPACE =
            "de.unibi.techfak.bibiserv.cms.minihtml";

    public CreateXML() {
        // retrieve current beans
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            functionManager = (FunctionManager) context.getApplication().
                    evaluateExpressionGet(context, "#{functionManager}",
                    FunctionManager.class);
            functionSelection = (FunctionSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionSelectionBean}",
                    FunctionSelectionBean.class);
            authorManager = (AuthorManager) context.getApplication().
                    evaluateExpressionGet(context, "#{authorManager}",
                    AuthorManager.class);
            authorSelection = (AuthorSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{authorSelectionBean}",
                    AuthorSelectionBean.class);
            basicInfoBean = (BasicInfoBean) context.getApplication().
                    evaluateExpressionGet(context, "#{basicInfoBean}",
                    BasicInfoBean.class);
            executableInfoBean = (ExecutableInfoBean) context.getApplication().
                    evaluateExpressionGet(context, "#{executableInfoBean}",
                    ExecutableInfoBean.class);
            viewManager = (ViewManager) context.getApplication().
                    evaluateExpressionGet(context, "#{viewManager}",
                    ViewManager.class);
            fileManager = (FileManager) context.getApplication().
                    evaluateExpressionGet(context, "#{fileManager}",
                    FileManager.class);
            imageFileManager = (ImageFileManager) context.getApplication().
                    evaluateExpressionGet(context, "#{imageFileManager}",
                    ImageFileManager.class);
            fileSelectionBean = (FileSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{fileSelectionBean}",
                    FileSelectionBean.class);
            imageFileSelectionBean = (ImageFileSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{imageFileSelectionBean}",
                    ImageFileSelectionBean.class);
            editFileBean = (EditFileBean) context.getApplication().
                    evaluateExpressionGet(context, "#{editFileBean}",
                    EditFileBean.class);
            basicInfoBuilder = (BasicInfoBuilder) context.getApplication().
                    evaluateExpressionGet(context, "#{basicInfoBuilder}",
                    BasicInfoBuilder.class);
            executeableInfoBuilder =
                    (ExecutableInfoBuilder) context.getApplication().
                    evaluateExpressionGet(context, "#{executableInfoBuilder}",
                    ExecutableInfoBuilder.class);
            manualManager =
                    (ManualManager) context.getApplication().
                    evaluateExpressionGet(context, "#{manualManager}",
                    ManualManager.class);
            referenceManager = (ReferenceManager) context.getApplication().
                    evaluateExpressionGet(context, "#{referenceManager}",
                    ReferenceManager.class);
            referenceSelectionBean = (ReferenceSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{referenceSelectionBean}",
                    ReferenceSelectionBean.class);
            parameterGroupManager = (ParameterGroupManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupManager}",
                    ParameterGroupManager.class);
            parameterManager = (ParameterManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterManager}",
                    ParameterManager.class);
            inputManager = (InputManager) context.getApplication().
                    evaluateExpressionGet(context, "#{inputManager}",
                    InputManager.class);
            outputManager = (OutputManager) context.getApplication().
                    evaluateExpressionGet(context, "#{outputManager}",
                    OutputManager.class);
            outputFileManager = (OutputFileManager) context.getApplication().
                    evaluateExpressionGet(context, "#{outputFileManager}",
                    OutputFileManager.class);
            webstartManager = (WebstartManager) context.getApplication().
                    evaluateExpressionGet(context, "#{webstartManager}",
                    WebstartManager.class);
            webstartSelectionBean = (WebstartSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{webstartSelectionBean}",
                    WebstartSelectionBean.class);
        }
    }

    
    private void reset() {
        renderLinks = false;
        renderCodegenLink = false;
        toolName = "";
        finalImageFileList = new ArrayList<File>();

        finalDownloadFileList = new ArrayList<File>();
        codegenOutput = new StringBuilder();
        codegenButtonDisabled = false;
        stopCodegenPoll = false;
        withoutWS = false;
        withoutMoby = false;
        withoutVB = false;
        withoutSSWAP = true;
    }
    
    public String returnToOverview() {
        //cancel current codegen calculation if there is one.
        if (codegenWorkingThread != null && codegenWorkingThread.isAlive()) {
            codegenWorkingThread.interrupt();
            codegenOutput.append(PropertyManager.getProperty("codegenInterrupted"));
        }
        reset();

        return "overview.xhtml?faces-redirect=true";
    }

    /**
     * This method does the xml file creation. The method goes through the
     * following steps:
     *
     * 1.) validation - check if the user has entered enough data to create
     * a valid tool description
     * 2.) preprocessing - go through all managing beans and retrieve the data
     * 3.) processing - marshall the xml
     * 4.) postprocessing - restore the old state of the manager beans
     *
     * Further documentation can be found in the method itself.
     */
    public void createXmlFile() {
        renderLinks = false;

        if (!validate()) {
            return;
        }

        /*
         * ################### PREPROCESSING! ####################
         */

        /*
         * In the preprocessing section, the created data is stored in a new
         * TrunnableItem to prepare for marshalling. The basic concept here is
         * to copy all elements that are stored in the manager beans and to
         * attach them to the runnable. However, the complex references between
         * some objects make it impossible to copy them. Therefore, a few
         * objects have to be postprocessed (see below) to garantee there
         * further function. These objects are:
         *
         * 1.) Tparam
         * 2.) TenumParam
         * 3.) TparamGroup
         * 4.) TinputOutput
         *
         * The main part of the preprocessing is ID finalization and the
         * conversion of micro- and minihtml content from xml-text to objects.
         *
         * Please note: The handling of Tdependencies is done special here:
         * Tdpendencies are manipulated in a way during preprocessing that makes
         * copying necessary. Therefore, all references of Tfunction objects
         * have to be changed to the new dependency object. This has NOT to be
         * changed back during post processing, because Tfunction objects have
         * been copied before. Therefore, no of the references in the
         * manager-functions has been changed and the manager-content is still
         * fully functional.
         */

        TrunnableItem originalRunnable = basicInfoBuilder.getTRunnable();

        TrunnableItem runnable = CopyFactory.copyRunnable(originalRunnable, "");

        toolName = runnable.getName().get(0).getValue();
        toolName = IDGenerator.createName(toolName);
        //finalize ID
        runnable.setId(toolName);
        //Handle description and custom content manually.
        List<Object> runnableDescriptionContent =
                buildDescriptionContent((String) originalRunnable.getDescription().
                get(0).getContent().get(0));
        TrunnableItem.Description runnableItemDescription = new Description();
        runnableItemDescription.setLang(originalRunnable.getDescription().get(0).
                getLang());
        runnableItemDescription.getContent().addAll(runnableDescriptionContent);
        runnable.getDescription().add(runnableItemDescription);
        if (!originalRunnable.getCustomContent().isEmpty() && !originalRunnable.getCustomContent().get(0).getContent().isEmpty()) {
            List<Object> runnableCustomContent =
                    buildCustomContent((String) originalRunnable.getCustomContent().get(0).getContent().get(0));
            TrunnableItem.CustomContent customContent = new CustomContent();
            customContent.setLang(originalRunnable.getCustomContent().get(0).
                    getLang());
            customContent.getContent().addAll(runnableCustomContent);
            runnable.getCustomContent().add(customContent);
        }
        // add authors
        List<String> savedSelectedAuthors = authorSelection.getSavedSelectedStrings();
        //TODO: Remove this as soon as responsible author is removed from schema.
        //Take first author and make her/him responsible author according to schema
        try {
            runnable.setResponsibleAuthor(authorManager.getAuthorByName(savedSelectedAuthors.remove(0)));
        } catch (BeansException ex) {
            // should not happen
        }
        for (String name : savedSelectedAuthors) {
            try {
                Tperson newperson = authorManager.getAuthorByName(name);
                runnable.getAuthor().add(newperson);
            } catch (BeansException ex) {
                // should not happen
            }
        }

        
        // Set for all used inputs to avoid double definitions.
        Set<TinputOutput> inputSet = new HashSet<TinputOutput>();
        // Set for all used output files to avoid double definitions.
        Set<ToutputFile> outputfileSet = new HashSet<ToutputFile>();
        // Set for all used outputs to avoid double definitions.
        Set<TinputOutput> outputSet = new HashSet<TinputOutput>();
        // ReferenceMap for all used parameter groups included in functions 
        ReferenceMap<TparamGroup, Tfunction> functionParamGroupMap =
                new ReferenceMap<TparamGroup, Tfunction>();

        Set<Tparam> paramSet = new HashSet<Tparam>();
        Set<TenumParam> enumParamSet = new HashSet<TenumParam>();
        Set<TparamGroup> paramGroupSet = new HashSet<TparamGroup>();
            
        if (!executableInfoBean.getExecutableInfoStatus().equals(TrafficLightEnum.RED.getPath())
                && !functionSelection.getSelectedFunctions().isEmpty()) {

            Texecutable executeable =
                    CopyFactory.copyExecutable(
                    executeableInfoBuilder.getTexecutable(), "");

            // From here on, stuff is added to the Executable.
            Disambiguator disambiguator = new Disambiguator();
            Tfunction disambiguatedFunction;

            // ReferenceMap for storing all used parameter dependencies with the functions
            // that are referencing to them.
            ReferenceMap<Tdependency, Tfunction> dependencyMap =
                    new ReferenceMap<Tdependency, Tfunction>();
            for (String name : functionSelection.getSelectedFunctions()) {
                try {
                    Tfunction oldfunction = functionManager.getFunctionByName(name);
                    Tfunction newfunction =
                            CopyFactory.copyFunction(oldfunction, "");

                    if (!(disambiguatedFunction = (Tfunction) disambiguator.disambiguateObject(newfunction, name)).equals(
                            newfunction)) {
                        newfunction = disambiguatedFunction;
                    } else {
                        // add all inputs
                        for (Tfunction.Inputref inputRef : newfunction.getInputref()) {
                            TinputOutput input = (TinputOutput) inputRef.getRef();
                            inputSet.add(input);
                        }
                        // add all outputfiles
                        for (Tfunction.Outputfileref outputFileRef : newfunction.getOutputfileref()) {
                            ToutputFile outputFile = (ToutputFile) outputFileRef.getRef();
                            outputfileSet.add(outputFile);
                        }
                        // add output
                        TinputOutput output = (TinputOutput) newfunction.getOutputref().getRef();
                        outputSet.add(output);
                        // add parameterGroup
                        TparamGroup paramGroup = newfunction.getParamGroup();
                        if (paramGroup != null) {
                            functionParamGroupMap.put(paramGroup, newfunction);
                        }
                        // add all dependencies
                        for (Tfunction.Depref depRef : newfunction.getDepref()) {
                            dependencyMap.put((Tdependency) depRef.getRef(),
                                    newfunction);
                        }
                    }

                    // change ID-Refs in Function examples
                    newfunction.getExample().clear();
                    for (Texample example : oldfunction.getExample()) {
                        Texample newexample = CopyFactory.copyExample(example);
                        for (Texample.Prop prop : newexample.getProp()) {
                            prop.setIdref(IDGenerator.finalizeID(toolName, prop.getIdref()));
                        }
                        newfunction.getExample().add(newexample);
                    }

                    // clear depRef for now. It will be filled later again.
                    newfunction.getDepref().clear();
                    //finalize ID
                    newfunction.setId(IDGenerator.finalizeID(toolName, newfunction.getId()));
                    // Handle description content manually (if there is description content).
                    if (!newfunction.getDescription().isEmpty() && !newfunction.getDescription().get(0).getContent().isEmpty()) {
                        List<Object> functionDescriptionContent =
                                buildDescriptionContent((String) newfunction.getDescription().get(0).getContent().get(0));
                        Tfunction.Description functionDescription =
                                new Tfunction.Description();
                        functionDescription.setLang(newfunction.getDescription().get(
                                0).getLang());
                        functionDescription.getContent().addAll(
                                functionDescriptionContent);
                        newfunction.getDescription().set(0, functionDescription);
                    }
                    // add funtion to list in executeable
                    executeable.getFunction().add(newfunction);

                } catch (BeansException ex) {
                    // if this happen function selection does not work!
                }
            }

            // finalize dependencies

            for (Tdependency dependency : dependencyMap.getObjects()) {
                Tdependency newDependency =
                        CopyFactory.copyDependency(dependency);
                //finalize ID
                newDependency.setId(
                        IDGenerator.finalizeID(toolName, newDependency.getId()));
                // Handle description content manually (if there is description content).
                if (!newDependency.getDescription().isEmpty()
                        && !newDependency.getDescription().get(0).getContent().isEmpty()) {
                    List<Object> dependencyDescriptionContent =
                            buildDescriptionContent(
                            (String) newDependency.getDescription().get(0).getContent().get(0));
                    Tdependency.Description dependencyDescription =
                            new Tdependency.Description();
                    dependencyDescription.setLang(newDependency.getDescription().get(
                            0).getLang());
                    dependencyDescription.getContent().addAll(
                            dependencyDescriptionContent);
                    newDependency.getDescription().set(0, dependencyDescription);
                }
                //edit definition.
                try {
                    String newdef = ParameterDependencyBuilder.insertRealIdsOverUserInput(toolName,
                            newDependency.getDependencyDefinition());
                    newDependency.setDependencyDefinition(newdef);
                } catch (DependencyResolveNameException ex) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "dependencyParameterResolveError"), ""));
                }
                // Change all references to the copy.
                Tfunction.Depref depRef = new Tfunction.Depref();
                depRef.setRef(newDependency);
                for (Tfunction function : dependencyMap.getReferencingObjects(
                        dependency)) {
                    function.getDepref().add(depRef);
                }
                executeable.getDependency().add(newDependency);
            }

            // finalize inputs
            if (inputSet.size() < inputManager.getAllNames().size()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                        PropertyManager.getProperty("lostInputs"), ""));
            }
            for (TinputOutput input : inputSet) {
                //finalize ID
                input.setId(IDGenerator.finalizeID(toolName,
                        input.getId()));
                // Handle description manually.
                List<Object> inputDescriptionContent =
                        buildDescriptionContent((String) input.getDescription().get(
                        0).getContent().get(0));
                TinputOutput.Description inputDescription =
                        new TinputOutput.Description();
                inputDescription.setLang(input.getDescription().get(0).
                        getLang());
                inputDescription.getContent().addAll(
                        inputDescriptionContent);
                input.getDescription().set(0, inputDescription);
            }


            // finalize output files
            if (outputfileSet.size() < outputFileManager.getAllNames().size()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                        PropertyManager.getProperty("lostOutputFiles"), ""));
            }
            for (ToutputFile ourputFile : outputfileSet) {
                //finalize ID
                ourputFile.setId(IDGenerator.finalizeID(toolName,
                        ourputFile.getId()));
            }

            // finalize outputs

            if (outputSet.size() < outputManager.getAllNames().size()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                        PropertyManager.getProperty("lostOutputs"), ""));
            }
            for (TinputOutput output : outputSet) {
                //finalize ID
                output.setId(
                        IDGenerator.finalizeID(toolName, output.getId()));
                // Handle description manually.
                List<Object> outputDescriptionContent =
                        buildDescriptionContent((String) output.getDescription().get(
                        0).getContent().get(0));
                TinputOutput.Description outputDescription =
                        new TinputOutput.Description();
                outputDescription.setLang(output.getDescription().get(0).
                        getLang());
                outputDescription.getContent().addAll(
                        outputDescriptionContent);
                output.getDescription().set(0, outputDescription);
            }

            // add dependencies, inputs and outputs to the executable.

            executeable.getInput().addAll(inputSet);
            executeable.getOutput().addAll(outputSet);
            executeable.getOutputfile().addAll(outputfileSet);


            // go through the parameter groups that have been found until now and
            // wrap all parameter groups, that are referenced two times, into
            // dummy parameter groups.

            // Set for all used parameter groups to avoid double definitions.
            paramGroupSet.addAll(functionParamGroupMap.getObjects());
            //Create new List to cash all paramgroups that have not been created in functions before
            ArrayList<TparamGroup> newParamGroups = new ArrayList<TparamGroup>();

            for (TparamGroup paramGroup : functionParamGroupMap.getObjects()) {
                List<Tfunction> referenceList = functionParamGroupMap.getReferencingObjects(paramGroup);
                if (referenceList.size() > 1) {
                    newParamGroups.add(paramGroup);
                    for (Tfunction referencingFunction : referenceList) {
                        TparamGroup wrapperGroup = new TparamGroup();
                        String wrapperID = paramGroup.getId() + "_wrapperGroup_";
                        wrapperID = wrapperID
                                + disambiguator.testAmbiguity(wrapperID);
                        wrapperGroup.setId(wrapperID);
                        TparamGroup.ParamGroupref paramGroupRef = new TparamGroup.ParamGroupref();
                        paramGroupRef.setRef(paramGroup);
                        wrapperGroup.getParamrefOrParamGroupref().add(paramGroupRef);
                        referencingFunction.setParamGroup(wrapperGroup);
                        paramGroupSet.add(wrapperGroup);
                    }
                }
            }

            //Create a queue to go through all parameters, enumparameters and
            //parameter groups using breadth first search.
            Queue<TparamGroup> next = new ArrayDeque<TparamGroup>();
            next.addAll(paramGroupSet);
            // get all subelements of parameter groups
            TparamGroup nextObject = next.poll();
            while (nextObject != null) {
                for (Object ref : nextObject.getParamrefOrParamGroupref()) {

                    // get referenced object
                    Object ob = null;
                    if (ref instanceof TparamGroup.ParamGroupref) {
                        ob = ((TparamGroup.ParamGroupref) ref).getRef();
                    } else if (ref instanceof TparamGroup.Paramref) {
                        ob = ((TparamGroup.Paramref) ref).getRef();
                    }

                    if (ob instanceof TparamGroup) {
                        // add group to queue if new
                        TparamGroup paramGroup = (TparamGroup) ob;
                        boolean fresh = paramGroupSet.add(paramGroup);
                        if (fresh) {
                            next.add(paramGroup);
                            newParamGroups.add(paramGroup);
                        }
                    } else if (ob instanceof Tparam) {
                        // add parameters to set
                        Tparam param = (Tparam) ob;
                        paramSet.add(param);
                    } else if (ob instanceof TenumParam) {
                        // add enumParameters to set
                        TenumParam enumParam = (TenumParam) ob;
                        enumParamSet.add(enumParam);
                    }
                }
                nextObject = next.poll(); // removes the next object to visit, null if empty
            }

            // finalize paramGroups

            if (paramGroupSet.size() < parameterGroupManager.getAllNames().size()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                        PropertyManager.getProperty("lostParamGroups"), ""));
            }
            for (TparamGroup paramGroup : paramGroupSet) {
                //finalize ID
                paramGroup.setId(IDGenerator.finalizeID(toolName,
                        paramGroup.getId()));
                // Handle description manually.
                if (!paramGroup.getDescription().isEmpty() && !paramGroup.getDescription().get(0).getContent().isEmpty()) {
                    List<Object> paramGroupDescriptionContent =
                            buildDescriptionContent((String) paramGroup.getDescription().get(0).getContent().get(0));
                    TparamGroup.Description paramGroupDescription =
                            new TparamGroup.Description();
                    paramGroupDescription.setLang(paramGroup.getDescription().
                            get(0).getLang());
                    paramGroupDescription.getContent().addAll(
                            paramGroupDescriptionContent);
                    paramGroup.getDescription().set(0, paramGroupDescription);
                }
            }

            // finalize Params
            if (paramSet.size() + enumParamSet.size() < parameterManager.getAllNames().size()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                        PropertyManager.getProperty("lostParams"), ""));
            }
            for (Tparam param : paramSet) {
                //finalize ID
                param.setId(IDGenerator.finalizeID(toolName, param.getId()));
                // Handle description manually.
                List<Object> paramDescriptionContent =
                        buildDescriptionContent((String) param.getDescription().get(
                        0).getContent().get(0));
                Tparam.Description paramDescription =
                        new Tparam.Description();
                paramDescription.setLang(param.getDescription().get(0).
                        getLang());
                paramDescription.getContent().addAll(paramDescriptionContent);
                param.getDescription().set(0, paramDescription);
            }

            // finalize EnumParams

            for (TenumParam enumParam : enumParamSet) {
                //finalize ID
                enumParam.setId(IDGenerator.finalizeID(toolName, enumParam.getId()));
                // Handle description manually.
                List<Object> enumParamDescriptionContent =
                        buildDescriptionContent((String) enumParam.getDescription().
                        get(0).getContent().get(0));
                TenumParam.Description enumParamDescription =
                        new TenumParam.Description();
                enumParamDescription.setLang(enumParam.getDescription().get(
                        0).getLang());
                enumParamDescription.getContent().addAll(
                        enumParamDescriptionContent);
                enumParam.getDescription().set(0, enumParamDescription);
            }

            // add all (new) param groups, enum params and params to the executable

            executeable.getParamGroup().addAll(newParamGroups);
            executeable.getParam().addAll(paramSet);
            executeable.getEnumParam().addAll(enumParamSet);

            runnable.setExecutable(executeable);
        
        }

        //Add views to the runnable.
        for (TrunnableItemView view : viewManager.getValues()) {
            TrunnableItemView newView = CopyFactory.copyView(view, "");
            //finalize ID
            newView.setId(IDGenerator.finalizeID(toolName, newView.getId()));
            //Handle custom content manually.
            if (!newView.getCustomContent().isEmpty() && !newView.getCustomContent().get(0).getContent().isEmpty()) {
                List<Object> viewCustomContentContent =
                        buildCustomContent((String) newView.getCustomContent().
                        get(0).getContent().get(0));
                TrunnableItemView.CustomContent viewCustomContent =
                        new TrunnableItemView.CustomContent();
                viewCustomContent.setLang(newView.getCustomContent().get(0).
                        getLang());
                viewCustomContent.getContent().addAll(viewCustomContentContent);
                newView.getCustomContent().set(0, viewCustomContent);
            }
            runnable.getView().add(newView);
        }

        //Add manual to the runnable.
        Tmanual newManual = CopyFactory.copyManual(
                manualManager.getSavedManual());
        //finalize ID
        newManual.setId(IDGenerator.finalizeID(toolName, newManual.getId()));
        //Handle introductory text manually
        List<Object> manualIntroductionContent =
                buildCustomContent((String) newManual.getIntroductoryText().get(
                0).
                getContent().get(0));
        Tmanual.IntroductoryText manualIntroduction =
                new Tmanual.IntroductoryText();
        manualIntroduction.setLang(newManual.getIntroductoryText().get(0).
                getLang());
        manualIntroduction.getContent().addAll(manualIntroductionContent);
        newManual.getIntroductoryText().set(0, manualIntroduction);

        //Handle custom content manually.
        if (!newManual.getCustomContent().isEmpty() && !newManual.getCustomContent().
                get(0).getContent().isEmpty()) {
            List<Object> manualCustomContentContent =
                    buildCustomContent((String) newManual.getCustomContent().get(
                    0).
                    getContent().get(0));
            Tmanual.CustomContent manualCustomContent =
                    new Tmanual.CustomContent();
            manualCustomContent.setLang(
                    newManual.getCustomContent().get(0).getLang());
            manualCustomContent.getContent().addAll(manualCustomContentContent);
            newManual.getCustomContent().set(0, manualCustomContent);
        }
        runnable.setManual(newManual);

        // get the directory path where files are stored temporarily
        final String tmpDirPath = (new File(editFileBean.getUploadedFilesBasePath())).getAbsolutePath();
        // clear the file lists
        finalImageFileList.clear();
        finalDownloadFileList.clear();
        //Add files.
        for (String name : fileSelectionBean.getSavedSelectedFiles()) {
            try {
                // retrieve the file tuple
                Tupel<Tfile, FileStates> fileTuple = fileManager.getFileByName(
                        name);
                // copy the Tfile object
                Tfile newfile = CopyFactory.copyFile(fileTuple.getFirst());
                //finalize ID
                newfile.setId(IDGenerator.finalizeID(toolName, newfile.getId()));
                // Handle description manually (if it is not empty).
                if (!newfile.getDescription().isEmpty() && !newfile.getDescription().get(0).getContent().isEmpty()) {
                    List<Object> newfileDescriptionContent =
                            buildDescriptionContent((String) newfile.getDescription().
                            get(0).getContent().get(0));
                    Tfile.Description newfileDescription = new Tfile.Description();
                    newfileDescription.setLang(newfile.getDescription().get(0).
                            getLang());
                    newfileDescription.getContent().addAll(newfileDescriptionContent);
                    newfile.getDescription().set(0, newfileDescription);
                }
                /*
                 * retrieve the uploaded file (if there is one) and add it to
                 * the list of files that shall be exported.
                 */
                if (fileTuple.getSecond() == FileStates.correctFile) {
                    String filename = newfile.getFilename();
                    File file = new File(tmpDirPath + OutputFileSystemManager.DOWNLOADDIR + filename);
                    if (file.exists()) {
                        finalDownloadFileList.add(file);
                    } else {
                        // if the file does not exist, show an error
                        FacesContext context = FacesContext.getCurrentInstance();
                        if (context != null) {
                            context.addMessage(null,
                                    new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    PropertyManager.getProperty("fileNotFoundError") + " " + filename,
                                    ""));
                        } else {
                            System.out.println(
                                    "Could not find a downloadable file: " + filename);
                        }
                    }
                }
                // finally add the file to the tool description.
                runnable.getDownloadable().add(newfile);
            } catch (BeansException ex) {
                // should not happen
            }
        }
        // get the additional image files.
        for (String imageFileName : imageFileSelectionBean.getSavedSelectedStrings()) {
            try {
                ImageFile imageFile = imageFileManager.getFileByName(imageFileName);
                // add the image file to the list of files that shall be exported.
                finalImageFileList.add(imageFile.getFile());
            } catch (BeansException ex) {
                // should not happen
            }
        }

        //Add references.
        for (String name : referenceSelectionBean.getSavedSelectedReferences()) {
            try {
                BiBiPublication newreference =
                        referenceManager.getReferenceById(name);
                if (runnable.getReferences() == null) {
                    runnable.setReferences(new Treferences());
                }
                runnable.getReferences().getReference().add(newreference.getExport(referenceExportFormat));
            } catch (BeansException ex) {
                // should not happen
            }
        }
        
        //webstarts
        for (String name : webstartSelectionBean.getSavedSelectedWebstarts()) {
            try {
                // retrieve the webstart
                Twebstart webstart = webstartManager.getWebstartByName(name);
                
                // copy the Tfile object
                Twebstart newWebstart = CopyFactory.copyWebstart(webstart);
                //finalize ID
                newWebstart.setId(IDGenerator.finalizeID(toolName, newWebstart.getId()));
                
                // Handle description manually (if it is not empty).
                if (!newWebstart.getCustomContent().isEmpty() && !newWebstart.getCustomContent().get(0).getContent().isEmpty()) {
                    
                    List<Object> newWebstartCustomContent =
                            buildCustomContent((String) newWebstart.getCustomContent().
                            get(0).getContent().get(0));
                    Twebstart.CustomContent newWebstartContent = new Twebstart.CustomContent();
                    newWebstartContent.setLang(newWebstart.getCustomContent().get(0).getLang());
                    newWebstartContent.getContent().addAll(newWebstartCustomContent);
                    newWebstart.getCustomContent().set(0, newWebstartContent);
                }
                
                                // Handle description manually (if it is not empty).
                if (!newWebstart.getIntroductoryText().isEmpty() && !newWebstart.getIntroductoryText().get(0).getContent().isEmpty()) {
                    
                    List<Object> newWebstartIntroContent =
                            buildCustomContent((String) newWebstart.getIntroductoryText().
                            get(0).getContent().get(0));
                    Twebstart.IntroductoryText newWebstartIntro = new Twebstart.IntroductoryText();
                    newWebstartIntro.setLang(newWebstart.getIntroductoryText().get(0).getLang());
                    newWebstartIntro.getContent().addAll(newWebstartIntroContent);
                    newWebstart.getIntroductoryText().set(0, newWebstartIntro);
                }

                // finally add the webstart to the tool description.
                runnable.getWebstart().add(newWebstart);
            } catch (BeansException ex) {
                // should not happen
            }
        }
        
        

        /*
         * ################### MARSHALLING! ######################
         */

        marshall(runnable);

        /*
         * ################### POSTPROCESSING! ###################
         */

        /*
         * During post processing, manipulated objects that are still contained
         * in managers have to be changed back to ensure the wizards function.
         * These objects are:
         *
         * 1.) Tparam
         * 2.) TenumParam
         * 3.) TparamGroup
         * 4.) TinputOutput
         *
         * Postprocessing has two steps:
         *
         * 1.) convert the finalized IDs back to temporary ones.
         * 2.) change the description content back to xml text.
         */

        // post process Tparams

        for (Tparam param : paramSet) {
            // strip id.
            param.setId(IDGenerator.stripToolName(param.getId()));
            // get back xml description content.
            if (!param.getDescription().isEmpty() && !param.getDescription().get(
                    0).getContent().isEmpty()) {
                Tparam.Description description = param.getDescription().get(0);
                String descriptionContent = LoadXMLBean.getDescriptionContent(
                        description);
                description.getContent().clear();
                description.getContent().add(descriptionContent);
            }
        }

        // post process TenumParams

        for (TenumParam enumParam : enumParamSet) {
            // strip id.
            enumParam.setId(IDGenerator.stripToolName(enumParam.getId()));
            // get back xml description content.
            if (!enumParam.getDescription().isEmpty() && !enumParam.getDescription().get(0).getContent().isEmpty()) {
                TenumParam.Description description = enumParam.getDescription().
                        get(0);
                String descriptionContent = LoadXMLBean.getDescriptionContent(
                        description);
                description.getContent().clear();
                description.getContent().add(descriptionContent);
            }
        }

        // post process TparamGroups

        for (TparamGroup paramGroup : paramGroupSet) {
            // strip id.
            paramGroup.setId(IDGenerator.stripToolName(paramGroup.getId()));
            // get back xml description content.
            if (!paramGroup.getDescription().isEmpty() && !paramGroup.getDescription().get(0).getContent().isEmpty()) {
                TparamGroup.Description description =
                        paramGroup.getDescription().get(0);
                String descriptionContent = LoadXMLBean.getDescriptionContent(
                        description);
                description.getContent().clear();
                description.getContent().add(descriptionContent);
            }
        }

        // post process inputs

        for (TinputOutput input : inputSet) {
            // strip id.
            input.setId(IDGenerator.stripToolName(input.getId()));
            // get back xml description content.
            if (!input.getDescription().isEmpty() && !input.getDescription().get(
                    0).getContent().isEmpty()) {
                TinputOutput.Description description = input.getDescription().
                        get(0);
                String descriptionContent = LoadXMLBean.getDescriptionContent(
                        description);
                description.getContent().clear();
                description.getContent().add(descriptionContent);
            }
        }
        
        // post process outputfiles

        for (ToutputFile outputFile : outputfileSet) {
            // strip id.
            outputFile.setId(IDGenerator.stripToolName(outputFile.getId()));
        }

        // post process outputs

        for (TinputOutput output : outputSet) {
            // strip id.
            output.setId(IDGenerator.stripToolName(output.getId()));
            // get back xml description content.
            if (!output.getDescription().isEmpty() && !output.getDescription().
                    get(0).getContent().isEmpty()) {
                TinputOutput.Description description = output.getDescription().
                        get(0);
                String descriptionContent = LoadXMLBean.getDescriptionContent(
                        description);
                description.getContent().clear();
                description.getContent().add(descriptionContent);
            }
        }
    }

    private boolean validate() {
        boolean validated = true;

        if (functionSelection.getSelectedFunctions().isEmpty() && !webstartSelectionBean.isWebstartSet()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noFunctionSelectedError"), ""));
            validated = false;
        } else if (authorSelection.noSavedSelectedStrings()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noAuthorSelectedError"), ""));
            validated = false;
        } else if (basicInfoBean.getBasicInfoStatus().equals(TrafficLightEnum.RED.getPath())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("incorrectBasicInfoError"), ""));
            validated = false;
        } else if (executableInfoBean.getExecutableInfoStatus().equals(TrafficLightEnum.RED.getPath()) && !webstartSelectionBean.isWebstartSet() ) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("incorrectExecInfoError"), ""));
            validated = false;
        } else if (manualManager.getSavedManual() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("incorrectManualDataError"), ""));
            validated = false;
        }

        return validated;
    }
    private static final String BIBISERVSCHEMALOCATION =
            "bibiserv:de.unibi.techfak.bibiserv.cms http://bibiserv.techfak.uni-bielefeld.de/xsd/bibiserv2/BiBiServAbstraction.xsd";

    /**
     * Unmarshall to file.
     *
     * @param runnable object to unmarshall
     */
    private void marshall(TrunnableItem runnable) {
        Process child = null;
        Writer fileWriter = null;
        try {
            JAXBContext context = JAXBContext.newInstance(
                    "de.unibi.techfak.bibiserv.cms");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                    BIBISERVSCHEMALOCATION);

            //toolname is filled above at createXML().

            final String filepath = editFileBean.getUploadedFilesBasePath()
                    + "/" + toolName;

            final String xmllink = filepath + ".xml";
            xmlFile = new File(xmllink);
            xmlFile.createNewFile();

            FileOutputStream outStream = new FileOutputStream(xmlFile);

            ObjectFactory factory = new ObjectFactory();
            marshaller.marshal(factory.createRunnableItem(runnable),
                    outStream);

            renderLinks = true;

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    PropertyManager.getProperty("succesFullCreated"), ""));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("xmlGenerationFailed"), ""));
            renderLinks = false;
        } finally {
            if (child != null) {
                child.destroy();
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ex) {
                }
            }
        }
    }
    private static final String CODEGENANTSCRIPTPATH =
            "../../tools/antCodegenScript.xml";

    /**
     * This method uses the created xml description to call the
     * base-code-creation. The created description is packed into a .zip-file.
     */
    public void startCodegen() {
        //Disable codegen button
        codegenButtonDisabled = true;
        stopCodegenPoll = false;
        codegenOutput = new StringBuilder();
        final String tmpDirPath = (new File(editFileBean.getUploadedFilesBasePath())).getAbsolutePath();
        //Environment is defined here as backup and is not needed normally.
        String[] env = {"TMP_DIR=" + tmpDirPath};
        //define output directory.
        final String outputdirectory =
                tmpDirPath + "/" + toolName + "_" + FileUploadIDGenerator.generateTimeStamp();
        //get path to ant script which calls base which calls codegen.
        String codegenAntScriptPath = CreateXML.class.getResource(
                CODEGENANTSCRIPTPATH).getFile();
        //start to build the command
        ArrayList<String> commandLineOptions = new ArrayList<String>();
        //ant call
        commandLineOptions.add("ant");
        //file option
        commandLineOptions.add("-f");
        commandLineOptions.add(codegenAntScriptPath);
        //command
        commandLineOptions.add("codegen");
        //xml input file option
        commandLineOptions.add("-Dbs2file=" + xmlFile.getAbsolutePath());
        //output directory option
        commandLineOptions.add("-Doutput_dir=" + outputdirectory);
        //without_ws option
        if (withoutWS) {
            commandLineOptions.add("-Dwithout_ws=true");
        }
        //without_moby option
        if (withoutMoby) {
            commandLineOptions.add("-Dwithout_moby=true");
        }
        //without_vb option
        if (withoutVB) {
            commandLineOptions.add("-Dwithout_vb=true");
        }
        //without_sswap option
        if (withoutSSWAP) {
            commandLineOptions.add("-Dwithout_sswap=true");
        }
        String[] command = commandLineOptions.toArray(new String[commandLineOptions.size()]);

        try {
            //the actual call.
            final Process p = Runtime.getRuntime().exec(command, env);

            //Create a thread to show input and error stream to user.
            codegenWorkingThread = new Thread() {

                @Override
                public void run() {

                    String line;

                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader err =
                            new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    try {
                        //read input and error stream and append them to output.
                        while ((line = in.readLine()) != null) {
                            codegenOutput.append(line).append("\n");
                        }

                        codegenOutput.append("\n\n Error messages: \n\n");

                        while ((line = err.readLine()) != null) {
                            codegenOutput.append(line).append("\n");
                        }

                        in.close();
                        err.close();

                        //wait for codegen to be completed.
                        int returnvalue = p.waitFor();

                        //Check the return value of codegen.
                        if (returnvalue != 0) {
                            throw new RuntimeException("Invalid codegen return value: "
                                    + returnvalue);
                        }

                        //move all uploaded files to the new directory
                        final String imageTargetDirectory = outputdirectory + OutputFileSystemManager.IMGDIR;

                        for (File imageFile : finalImageFileList) {
                            // first try to copy image files.
                            try {
                                OutputFileSystemManager.copyFileToTarget(imageFile, imageTargetDirectory);
                            } catch (IOException ex) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                if (context != null) {
                                    context.addMessage(null,
                                            new FacesMessage(
                                            FacesMessage.SEVERITY_ERROR,
                                            PropertyManager.getProperty("fileMoveError") + " " + imageFile.getName(),
                                            ""));
                                } else {
                                    System.out.println(
                                            "Error while moving downloadable file: " + imageFile.getName());
                                }
                            }
                        }

                        final String downloadsTargetDirectory = outputdirectory + OutputFileSystemManager.DOWNLOADDIR;

                        for (File downloadFile : finalDownloadFileList) {
                            // then try to copy all other downloadables
                            try {
                                OutputFileSystemManager.copyFileToTarget(downloadFile, downloadsTargetDirectory);
                            } catch (IOException ex) {
                                FacesContext context = FacesContext.getCurrentInstance();
                                if (context != null) {
                                    context.addMessage(null,
                                            new FacesMessage(
                                            FacesMessage.SEVERITY_ERROR,
                                            PropertyManager.getProperty("fileMoveError") + " " + downloadFile.getName(),
                                            ""));
                                } else {
                                    System.out.println(
                                            "Error while moving downloadable file: " + downloadFile.getName());
                                }
                            }
                        }
                        //create zip file containing created pages from codegen.
                        final String projectZip = tmpDirPath + "/" + toolName + "_project.zip";

                        try {
                            codegenFile = OutputFileSystemManager.zipFiles(outputdirectory, projectZip, codegenFile);
                            renderCodegenLink = true;
                        } catch (IOException ex) {
                            FacesContext context = FacesContext.getCurrentInstance();
                            if (context != null) {
                                context.addMessage(null,
                                        new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR,
                                        PropertyManager.getProperty("zipError"),
                                        ""));
                            } else {
                                System.out.println(
                                        "Error while creating zip file.");
                            }
                        }

                        FacesContext context = FacesContext.getCurrentInstance();
                        if (context != null) {
                            context.addMessage(null,
                                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                                    PropertyManager.getProperty(
                                    "codegenSuccessfull"), ""));
                        } else {
                            System.out.println("Codegen was successfull.");
                        }
                    } catch (IOException e) {
                        codegenOutput.append(PropertyManager.getProperty(
                                "codegenLoggingError"));
                    } catch (Exception e) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        if (context != null) {
                            context.addMessage(null,
                                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    PropertyManager.getProperty("codegenError")
                                    + e.getMessage(), ""));
                        } else {
                            e.printStackTrace();
                        }
                    } finally {
                        codegenButtonDisabled = false;
                        stopCodegenPoll = true;
                    }
                }
            };
            //Start the thread
            codegenWorkingThread.start();
        } catch (IOException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("codegenError") + e.getMessage(), ""));
            } else {
                e.printStackTrace();
            }

            codegenButtonDisabled = false;
            stopCodegenPoll = true;
        }
    }

    /**
     * Takes a valid microhtml description with microhtml-root-Element,
     * unmarshalls it using JaxB and returns a list of child nodes.
     *
     * @param description Description content as String.
     * @return List of unmarshalled microhtml child nodes.
     */
    public List<Object> buildDescriptionContent(String description) {

        StringReader stringReader = new StringReader(description);
        JAXBElement unmarshalledMicrohtml = null;

        try {
            JAXBContext microHtmlContext = JAXBContext.newInstance(
                    MICROHTMLNAMESPACE);

            Unmarshaller microHTMLUnmarshaller = microHtmlContext.createUnmarshaller();

            unmarshalledMicrohtml = (JAXBElement) microHTMLUnmarshaller.unmarshal(stringReader);


        } catch (JAXBException e) {
            // will be shown later (NullPointer).
        } finally {
            stringReader.close();
        }

        List<Object> microHtmlNodes = null;

        try {

            Flow unmarshalledDescription =
                    (Flow) unmarshalledMicrohtml.getValue();

            microHtmlNodes = unmarshalledDescription.getContent();

        } catch (ClassCastException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("descriptionBuildError"), ""));
            } else {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("descriptionBuildError"), ""));
            } else {
                e.printStackTrace();
            }
        }

        return microHtmlNodes;
    }

    /**
     * Takes a valid minihtml custom content with minihtml-root-Element,
     * unmarshalls it using JaxB and returns a list of child nodes.
     *
     * @param customContent custom content as String.
     * @return List of unmarshalled minihtml child nodes.
     */
    public List<Object> buildCustomContent(String customContent) {

        StringReader stringReader = new StringReader(customContent);
        JAXBElement unmarshalledMinihtml = null;

        try {
            JAXBContext miniHtmlContext = JAXBContext.newInstance(
                    MINIHTMLNAMESPACE);

            Unmarshaller miniHTMLUnmarshaller = miniHtmlContext.createUnmarshaller();

            unmarshalledMinihtml = (JAXBElement) miniHTMLUnmarshaller.unmarshal(
                    stringReader);


        } catch (JAXBException e) {
            //Will be shown later (NullPointer).
        } finally {
            stringReader.close();
        }

        List<Object> miniHtmlNodes = null;

        try {

            de.unibi.techfak.bibiserv.cms.minihtml.Flow unmarshalledCustomContent =
                    (de.unibi.techfak.bibiserv.cms.minihtml.Flow) unmarshalledMinihtml.getValue();

            miniHtmlNodes = unmarshalledCustomContent.getContent();

        } catch (ClassCastException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("customContentBuildError"),
                        ""));
            } else {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("customContentBuildError"),
                        ""));
            } else {
                e.printStackTrace();
            }
        }

        return miniHtmlNodes;
    }

    /**
     * @return defines if the download-button is shown.
     */
    public boolean getRenderLinks() {
        return renderLinks;
    }

    /**
     * This is no simple getter! It opens the stream for primefaces streamed
     * content!
     *
     * @return streamed content for an existing xml file.
     */
    public StreamedContent getXMLFile() {

        StreamedContent xmlStreamedContent = null;

        try {
            xmlStreamedContent =
                    new DefaultStreamedContent(new FileInputStream(xmlFile),
                    "xml", toolName + ".xml");
        } catch (FileNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("fileNotFoundError"), ""));
            renderLinks = false;
        }

        return xmlStreamedContent;
    }

    /**
     *
     * @return defines whether the .zip-download-button is shown.
     */
    public boolean isRenderCodegenLink() {
        return renderCodegenLink;
    }

    /**
     * This is no simple getter! It opens the stream for primefaces streamed
     * content!
     *
     * @return streamed content for an existing zip file containing all files
     * generated by base.
     */
    public StreamedContent getCodegenFile() {
        StreamedContent codegenStreamedContent = null;

        try {
            codegenStreamedContent =
                    new DefaultStreamedContent(new FileInputStream(codegenFile),
                    "zip", toolName + "_project.zip");
        } catch (FileNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("fileNotFoundError"), ""));
            renderCodegenLink = false;
        }

        return codegenStreamedContent;
    }

    public String getCodegenOutput() {
        return codegenOutput.toString();
    }

    public static String getBIBISERVSCHEMALOCATION() {
        return BIBISERVSCHEMALOCATION;
    }

    public boolean isCodegenButtonDisabled() {
        return codegenButtonDisabled;
    }

    public void stopPolling() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("stopPoll", stopCodegenPoll);
    }

    public boolean isWithoutWS() {
        return withoutWS;
    }

    public void setWithoutWS(boolean withoutWS) {
        this.withoutWS = withoutWS;
    }

    public boolean isWithoutMoby() {
        return withoutMoby;
    }

    public void setWithoutMoby(boolean withoutMoby) {
        this.withoutMoby = withoutMoby;
    }

    public boolean isWithoutVB() {
        return withoutVB;
    }

    public void setWithoutVB(boolean withoutVB) {
        this.withoutVB = withoutVB;
    }

    public boolean isWithoutSSWAP() {
        return withoutSSWAP;
    }

    public void setWithoutSSWAP(boolean withoutSSWAP) {
        this.withoutSSWAP = withoutSSWAP;
    }
}
