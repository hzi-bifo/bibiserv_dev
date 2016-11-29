package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.util.bibtexparser.BibtexParser;
import de.unibi.cebitec.bibiserv.util.bibtexparser.ParseException;
import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.FileStates;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.AuthorBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.AuthorManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.BasicInfoBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ExecutableInfoBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FileBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FileManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.InputManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.InputOutputBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ManualManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputFileBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputFileManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterGroupBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterGroupManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ReferenceManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ViewBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ViewManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.WebstartBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.WebstartManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.cebitec.bibiserv.wizard.tools.CopyFactory;
import de.unibi.cebitec.bibiserv.wizard.tools.Disambiguator;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.techfak.bibiserv.cms.Texecutable;
import de.unibi.techfak.bibiserv.cms.Tfile;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.Tfunction.Description;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Titem;
import de.unibi.techfak.bibiserv.cms.Tmanual;
import de.unibi.techfak.bibiserv.cms.ToutputFile;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import de.unibi.techfak.bibiserv.cms.Tperson;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import de.unibi.techfak.bibiserv.cms.Twebstart;
import de.unibi.techfak.bibiserv.cms.microhtml.Flow;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.primefaces.event.FileUploadEvent;

/**
 * This bean manages the communication with the loadXML.xhtml-page.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de Thomas Gatter
 * - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class LoadXMLBean {

    private BasicInfoBean basicInfoBean;
    private BasicInfoBuilder basicInfoBuilder;
    private ExecutableInfoBean executableInfoBean;
    private ExecutableInfoBuilder executableInfoBuilder;
    private FunctionManager functionManager;
    private FunctionBean functionBean;
    private FunctionSelectionBean functionSelectionBean;
    private AuthorManager authorManager;
    private AuthorSelectionBean authorSelectionBean;
    private InputManager inputManager;
    private InputBean inputBean;
    private OutputManager outputManager;
    private OutputBean outputBean;
    private OutputFileManager outputFileManager;
    private OutputFileBean outputFileBean;
    private ParameterManager parameterManager;
    private ParameterBean parameterBean;
    private ParameterGroupBean parameterGroupBean;
    private ParameterGroupManager parameterGroupManager;
    private ParameterDependencyManager parameterDependencyManager;
    private FileManager fileManager;
    private FileSelectionBean fileSelectionBean;
    private EditFileBean fileBean;
    private ReferenceManager referenceManager;
    private ReferenceBean referenceBean;
    private ReferenceSelectionBean referenceSelectionBean;
    private ViewManager viewManager;
    private ManualManager manualManager;
    private ManualBean manualBean;
    private WebstartManager webstartManager;
    private WebstartBean webstartBean;
    private WebstartSelectionBean webstartSelectionBean;
    private Disambiguator disambiguator = new Disambiguator();
    private static final String CMSNAMESPACE = "de.unibi.techfak.bibiserv.cms";
    private static final String MICROHTMLNAMESPACE = "de.unibi.techfak.bibiserv.cms.microhtml";
    private static final String MINIHTMLNAMESPACE = "de.unibi.techfak.bibiserv.cms.minihtml";
    private static final String DEFAULTFUNCTIONNAME = "function";
    private static final String DEFAULTDEPENDENCYNAME = "dependency";
    private static final String DEFAULTVIEWNAME = "view";

    public LoadXMLBean() {
    }

    public String returnToOverview() {

        return "overview.xhtml?faces-redirect=true";
    }

    /**
     * fileUploadEvent-Listener for the loadXML.xhtml-page
     *
     * @param event thrown by the xhtml-page.
     */
    public void handleFileUpload(FileUploadEvent event) {

        //Clear the current session

        FacesContext context = FacesContext.getCurrentInstance();

        OverviewBean overviewBean = (OverviewBean) context.getApplication().
                evaluateExpressionGet(context, "#{overviewBean}",
                OverviewBean.class);
        overviewBean.clearSession();

        try {
            //Unmarshall the input xml-file.
            TrunnableItem unmarshalledRunnableItem =
                    unmarshallXML(event.getFile().getInputstream());
            //Load the data into the different beans.
            loadTRunnableData(unmarshalledRunnableItem);
        } catch (IOException e) {
            if (FacesContext.getCurrentInstance() != null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        event.getFile().getFileName() + " "
                        + PropertyManager.getProperty("unmarshallingError") + " "
                        + e.getMessage(), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                System.err.println("Filereading did not work.");
            }
        } catch (NullPointerException e) {
            if (FacesContext.getCurrentInstance() != null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        event.getFile().getFileName() + " "
                        + PropertyManager.getProperty("unmarshallingError") + " "
                        + e.getMessage(), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                System.err.println("Nullpointer: " + e.getMessage());
            }
        }
    }

    /**
     * loads all beans in the wizard and loads the content of a TRunnable into
     * the beans.
     *
     * @param runnableItem a valid TRunnable describing a BiBiServ2-tool.
     */
    public void loadTRunnableData(TrunnableItem runnableItem) throws IOException {

        loadAllBeans();
        //temporary boolean that marks if a object could be added to a managerbean
        boolean addingWorked = false;

        // list of all replacements that have to be done in dependencies
        List<Tupel<String, String>> dependencyReplaceList = new ArrayList<Tupel<String, String>>();
        // list of all idreplacements tha need to be done in idrefs in examples
        Map<String, String> exampleReplaceList = new HashMap<String, String>();

        //load the runnableItem into the basicInfoBuilder.
        TrunnableItem newRunnable = CopyFactory.copyRunnable(runnableItem, "");
        //load the description manually (if it is there).
        if (!runnableItem.getDescription().isEmpty()) {
            Titem.Description basicInfoDescription = new Titem.Description();
            basicInfoDescription.getContent().add(flowObjectList2String(runnableItem.getDescription().get(0).getContent()));
            basicInfoDescription.setLang(runnableItem.getDescription().get(0).getLang());
            newRunnable.getDescription().add(basicInfoDescription);
        }
        //load the custom content manually (if it is there).
        if (!runnableItem.getCustomContent().isEmpty()) {
            Titem.CustomContent basicInfoCustomContent = new Titem.CustomContent();
            basicInfoCustomContent.getContent().add(getCustomContent(runnableItem.getCustomContent().get(0)));
            basicInfoCustomContent.setLang(runnableItem.getCustomContent().get(0).getLang());
            newRunnable.getCustomContent().add(basicInfoCustomContent);
        }
        basicInfoBuilder.setRunnableItem(newRunnable);

                    //load the authors into the authorManager.

        authorSelectionBean.getSavedSelectedStringsReference().clear();
        while (!addingWorked) {
            try {
                String name = authorManager.addAuthor(runnableItem.getResponsibleAuthor());
                authorSelectionBean.getSavedSelectedStringsReference().add(name);
                addingWorked = true;
            } catch (BeansException e) {
                //If this exception is thrown, the given name is ambigous.
                String authorLastName = runnableItem.getResponsibleAuthor().getLastname();
                authorLastName = disambiguate(authorLastName, AuthorBuilder.getID_BASE_TYPE());
                runnableItem.getResponsibleAuthor().setLastname(authorLastName);
            }
        }
        for (Tperson author : runnableItem.getAuthor()) {
            while (!addingWorked) {
                try {
                    String name = authorManager.addAuthor(author);
                    authorSelectionBean.getSavedSelectedStringsReference().add(name);
                    addingWorked = true;
                } catch (BeansException e) {
                    //If this exception is thrown, the given name is ambigous.
                    String authorLastName = author.getLastname();
                    authorLastName = disambiguate(authorLastName, AuthorBuilder.getID_BASE_TYPE());
                    author.setLastname(authorLastName);
                }
            }
            addingWorked = false;
        }

        addingWorked = false;

        //Look if there is an executable defined.
        Texecutable executable = runnableItem.getExecutable();
        //Load the executables content only if it is defined.
        if (executable != null) {

            //load the executable into the executableInfoBuilder.
            //copy all attributes manually.
            Texecutable newExecutable = CopyFactory.copyExecutable(executable, "");
            executableInfoBuilder.setExecutableItem(newExecutable);

            //load the inputs into the input-manager.

            for (TinputOutput input : executable.getInput()) {
                //Handle the description manually.
                TinputOutput.Description inputDescription = new TinputOutput.Description();
                inputDescription.getContent().add(flowObjectList2String(input.getDescription().get(0).getContent()));
                inputDescription.setLang(input.getDescription().get(0).getLang());
                input.getDescription().set(0, inputDescription);

                //Create ID according to wizard nameconventions
                String inputName = input.getName().get(0).getValue();
                String oldId = input.getId();
                input.setId(IDGenerator.createTemporaryID(inputName, InputOutputBuilder.getID_BASE_TYPE_INPUT()));
                exampleReplaceList.put(oldId, input.getId());

                //Add the input to the manager-bean.
                while (!addingWorked) {
                    try {
                        inputManager.addInput(input);
                        addingWorked = true;
                    } catch (BeansException e) {
                        //If this exception is thrown, the given name is ambigous.
                        inputName = disambiguate(inputName, InputOutputBuilder.getID_BASE_TYPE_INPUT());
                        input.getName().get(0).setValue(inputName);
                        input.setId(IDGenerator.createTemporaryID(inputName, InputOutputBuilder.getID_BASE_TYPE_INPUT()));
                    }
                }
                addingWorked = false;
            }
            
            //load the output files into the output file -manager.

            for (ToutputFile outputFile : executable.getOutputfile()) {     
                //Create ID according to wizard nameconventions
                String outputFileName;
                outputFileName = IDGenerator.buildNameFromID(outputFile.getId());
                outputFile.setId(IDGenerator.createTemporaryID(
                        outputFileName, OutputFileBuilder.getID_BASE_TYPE()));
                
                //Add the parameterGroup to the manager-bean.
                while (!addingWorked) {
                    try {
                        outputFileManager.addOutput(outputFile, outputFileName);
                        addingWorked = true;
                    } catch (BeansException e) {
                        //If this exception is thrown, the given name is ambigous.
                        outputFileName = disambiguate(outputFileName, OutputFileBuilder.getID_BASE_TYPE());
                        outputFile.setId(IDGenerator.createTemporaryID(
                        outputFileName, OutputFileBuilder.getID_BASE_TYPE()));
                    }
                }
                addingWorked = false;
            }

            //load the parameter dependencies into the ParameterDependencyManager.
            for (Tdependency dependency : executable.getDependency()) {
                //Handle the description manually.
                if (!dependency.getDescription().isEmpty()) {
                    Tdependency.Description dependencyDescription = new Tdependency.Description();
                    dependencyDescription.getContent().add(flowObjectList2String(
                            dependency.getDescription().get(0).getContent()));
                    dependencyDescription.setLang(dependency.getDescription().get(0).getLang());
                    dependency.getDescription().set(0, dependencyDescription);
                }
                //Create ID according to wizard nameconventions
                String dependencyName;
                if (!dependency.getName().isEmpty() && !dependency.getName().get(0).getValue().isEmpty()) {
                    dependencyName = dependency.getName().get(0).getValue();
                } else {
                    dependencyName = IDGenerator.buildNameFromID(dependency.getId());
                    if (dependencyName.isEmpty()) {
                        dependencyName = DEFAULTDEPENDENCYNAME;
                    }
                    Tdependency.Name dependencyTName = new Tdependency.Name();
                    dependencyTName.setValue(dependencyName);
                    dependencyTName.setLang(BasicBeanData.StandardLanguage);
                    dependency.getName().add(dependencyTName);
                }

                dependency.setId(IDGenerator.createTemporaryID(dependencyName, ParameterDependencyBuilder.getID_BASE_TYPE()));

                //Add the input tot the manager-bean.
                while (!addingWorked) {
                    try {
                        parameterDependencyManager.addDependency(dependency);
                        addingWorked = true;
                    } catch (BeansException e) {
                        //If this exception is thrown, the given name is ambigous.
                        dependencyName = disambiguate(dependencyName, ParameterDependencyBuilder.getID_BASE_TYPE());
                        dependency.getName().get(0).setValue(dependencyName);
                        dependency.setId(IDGenerator.createTemporaryID(dependencyName, ParameterDependencyBuilder.getID_BASE_TYPE()));
                    }
                }
                addingWorked = false;
            }

            //load the outputs into the output-manager.

            for (TinputOutput output : executable.getOutput()) {
                //Handle the description manually.
                TinputOutput.Description outputDescription = new TinputOutput.Description();
                outputDescription.getContent().add(flowObjectList2String(
                        output.getDescription().get(0).getContent()));
                outputDescription.setLang(output.getDescription().get(0).getLang());
                output.getDescription().set(0, outputDescription);

                //Create ID according to wizard nameconventions
                String outputName = output.getName().get(0).getValue();
                output.setId(IDGenerator.createTemporaryID(outputName, InputOutputBuilder.getID_BASE_TYPE_OUTPUT()));

                //Add the output to the manager-bean.
                while (!addingWorked) {
                    try {
                        outputManager.addOutput(output);
                        addingWorked = true;
                    } catch (BeansException e) {
                        //If this exception is thrown, the given name is ambigous.
                        outputName = disambiguate(outputName, InputOutputBuilder.getID_BASE_TYPE_OUTPUT());
                        output.getName().get(0).setValue(outputName);
                        output.setId(IDGenerator.createTemporaryID(outputName, InputOutputBuilder.getID_BASE_TYPE_OUTPUT()));
                    }
                }
                addingWorked = false;
            }

            //Load the parameters into the parameterManager.
            //load Tparams
            for (Tparam parameter : executable.getParam()) {
                //Handle the description manually.
                Tparam.Description parameterDescription = new Tparam.Description();
                parameterDescription.getContent().add(flowObjectList2String(
                        parameter.getDescription().get(0).getContent()));
                parameterDescription.setLang(parameter.getDescription().get(0).getLang());
                parameter.getDescription().set(0, parameterDescription);

                String parameterName = parameter.getName().get(0).getValue();
                String oldId = parameter.getId();
                //add to replacementlist of dependencies
                dependencyReplaceList.add(new Tupel<String, String>(oldId, parameterName));
                //Create ID according to wizard nameconventions
                parameter.setId(IDGenerator.createTemporaryID(parameterName, ParameterBuilder.getID_BASE_TYPE()));
                // add to replacmentList of example IDRefs
                exampleReplaceList.put(oldId, parameter.getId());

                //Add the parameter to the manager-bean.
                while (!addingWorked) {
                    try {
                        parameterManager.addParameter(parameter);
                        addingWorked = true;
                    } catch (BeansException e) {
                        //If this exception is thrown, the given name is ambigous.
                        parameterName = disambiguate(parameterName, ParameterBuilder.getID_BASE_TYPE());
                        parameter.getName().get(0).setValue(parameterName);
                        parameter.setId(IDGenerator.createTemporaryID(parameterName, ParameterBuilder.getID_BASE_TYPE()));
                    }
                }
                addingWorked = false;
            }
            //load Tenumparams
            for (TenumParam enumParameter : executable.getEnumParam()) {
                //Handle the description manually.
                TenumParam.Description parameterDescription = new TenumParam.Description();
                parameterDescription.getContent().add(flowObjectList2String(
                        enumParameter.getDescription().get(0).getContent()));
                parameterDescription.setLang(enumParameter.getDescription().get(0).getLang());
                enumParameter.getDescription().set(0, parameterDescription);


                String parameterName = enumParameter.getName().get(0).getValue();
                String oldId = enumParameter.getId();
                //add to replacementlist
                dependencyReplaceList.add(new Tupel<String, String>(oldId, parameterName));
                //Create ID according to wizard nameconventions
                enumParameter.setId(IDGenerator.createTemporaryID(parameterName, ParameterBuilder.getID_BASE_TYPE()));
                // add to replacmentList of example IDRefs
                exampleReplaceList.put(oldId, enumParameter.getId());

                //Add the parameter to the manager-bean.
                while (!addingWorked) {
                    try {
                        parameterManager.addParameter(enumParameter);
                        addingWorked = true;
                    } catch (BeansException e) {
                        //If this exception is thrown, the given name is ambigous.
                        parameterName = disambiguate(parameterName, ParameterBuilder.getID_BASE_TYPE());
                        enumParameter.getName().get(0).setValue(parameterName);
                        enumParameter.setId(IDGenerator.createTemporaryID(parameterName, ParameterBuilder.getID_BASE_TYPE()));
                    }
                }
                addingWorked = false;
            }

            changeDependencyDefinitions(dependencyReplaceList);

            //Load the parameterGroups into the parameterGroupManager.
            for (TparamGroup paramGroup : executable.getParamGroup()) {
                //Handle the description manually (if there is one).
                if (!paramGroup.getDescription().isEmpty()) {
                    TparamGroup.Description parameterGroupDescription =
                            new TparamGroup.Description();
                    parameterGroupDescription.getContent().add(flowObjectList2String(
                            paramGroup.getDescription().get(0).getContent()));
                    parameterGroupDescription.setLang(paramGroup.getDescription().get(0).getLang());
                    paramGroup.getDescription().set(0, parameterGroupDescription);
                }
                String paramGroupName;
                if (paramGroup.getName().isEmpty() || paramGroup.getName().get(0).getValue().isEmpty()) {
                    paramGroupName = IDGenerator.buildNameFromID(paramGroup.getId());
                } else {
                    paramGroupName = paramGroup.getName().get(0).getValue();
                }
                paramGroup.setId(IDGenerator.createTemporaryID(
                        paramGroupName, ParameterGroupBuilder.getID_BASE_TYPE()));

                //Add the parameterGroup to the manager-bean.
                while (!addingWorked) {
                    try {
                        parameterGroupManager.addParameterGroup(paramGroup, paramGroupName);
                        addingWorked = true;
                    } catch (BeansException e) {
                        //If this exception is thrown, the given name is ambigous.
                        paramGroupName = disambiguate(paramGroupName, ParameterGroupBuilder.getID_BASE_TYPE());
                        paramGroup.getName().get(0).setValue(paramGroupName);
                        paramGroup.setId(IDGenerator.createTemporaryID(paramGroupName, ParameterGroupBuilder.getID_BASE_TYPE()));
                    }
                }
                addingWorked = false;
            }


            //load the functions into the functionManager.
            List<String> nameorder = new ArrayList<String>();
            for (Tfunction function : executable.getFunction()) {
                //Handle the description manually (if there is one).
                if (!function.getDescription().isEmpty()) {
                    Tfunction.Description functionDescription = new Tfunction.Description();
                    //validate content
                    functionDescription.getContent().add(flowObjectList2String(function.getDescription().get(0).getContent()));
                    
                    functionDescription.setLang(function.getDescription().get(0).getLang());
                    function.getDescription().set(0, functionDescription);
                }

                //Look if there is a parameter group defined
                if (function.getParamGroup() != null) {
                    // get the parameter group.
                    TparamGroup paramGroup = function.getParamGroup();
                    //correct the parameter groups id.
                    String paramGroupName;
                    if (paramGroup.getName().isEmpty() || paramGroup.getName().get(0).getValue().isEmpty()) {
                        paramGroupName = IDGenerator.buildNameFromID(paramGroup.getId());
                    } else {
                        paramGroupName = paramGroup.getName().get(0).getValue();
                    }
                    paramGroup.setId(IDGenerator.createTemporaryID(paramGroupName, ParameterGroupBuilder.getID_BASE_TYPE()));

                    try {
                        // try to get an existing parameter group with the same name.
                        TparamGroup foundParamGroup = parameterGroupManager.getParameterGroupByName(paramGroupName);
                        //If the group already exists, correct the functions parameter group to the existing one.
                        function.setParamGroup(foundParamGroup);
                    } catch (BeansException ex) {
                        // the exception marks that the parameter group does not exist already.
                        //Handle the description manually (if there is one).
                        if (!paramGroup.getDescription().isEmpty()) {
                            TparamGroup.Description paramGroupDescription = new TparamGroup.Description();
                            paramGroupDescription.getContent().add(flowObjectList2String(
                                    paramGroup.getDescription().get(0).getContent()));
                            paramGroupDescription.setLang(paramGroup.getDescription().get(0).getLang());
                            paramGroup.getDescription().set(0, paramGroupDescription);
                        }
                        //Add the functions paramGroup to the ParameterGroupManager.
                        while (!addingWorked) {
                            try {
                                parameterGroupManager.addParameterGroup(paramGroup, paramGroupName);
                                addingWorked = true;
                            } catch (BeansException e) {
                                //If this exception is thrown, the given name is ambigous.
                                paramGroupName = disambiguate(paramGroupName, ParameterGroupBuilder.getID_BASE_TYPE());
                                function.getParamGroup().getName().get(0).setValue(paramGroupName);
                                paramGroup.setId(IDGenerator.createTemporaryID(paramGroupName, ParameterGroupBuilder.getID_BASE_TYPE()));
                            }
                        }
                        addingWorked = false;
                    }
                }

                // change IDRef in example
                for (Texample example : function.getExample()) {
                    for (Texample.Prop prop : example.getProp()) {
                        prop.setIdref(exampleReplaceList.get(prop.getIdref()));
                    }
                }

                //Reset the id and set a default name if neccessary.
                String functionName;
                if (!function.getName().isEmpty() && !function.getName().get(0).getValue().isEmpty()) {
                    functionName = function.getName().get(0).getValue();
                } else {
                    functionName = IDGenerator.buildNameFromID(function.getId());
                    if (functionName.isEmpty()) {
                        functionName = DEFAULTFUNCTIONNAME;
                    }
                    Tfunction.Name functionTName = new Tfunction.Name();
                    functionTName.setValue(functionName);
                    functionTName.setLang(BasicBeanData.StandardLanguage);
                    function.getName().add(functionTName);
                }
                function.setId(IDGenerator.createTemporaryID(functionName, FunctionBuilder.getID_BASE_TYPE()));

                //Add the function to the manager-bean.
                while (!addingWorked) {
                    try {
                        functionManager.addFunction(function, false, false);
                        addingWorked = true;
                    } catch (BeansException e) {
                        //If this exception is thrown, the given name is ambigous.
                        functionName = disambiguate(functionName, FunctionBuilder.getID_BASE_TYPE());
                        function.getName().get(0).setValue(functionName);
                        function.setId(IDGenerator.createTemporaryID(functionName, FunctionBuilder.getID_BASE_TYPE()));
                    }
                }

                addingWorked = false;
                String name = function.getName().get(0).getValue();
                nameorder.add(name);
                if(!functionBean.loadAndResetOrderAndExamples(name)){
                        FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "loadXMLorderExampleResetError"),
                        ""));
                }
                Tupel<Boolean, Boolean> valid = functionBean.loadAndTestFunctionExamples(name, false);
                try {
                    functionManager.editFunction(name, function, valid.getFirst(), valid.getSecond());
                } catch (BeansException ex) {
                    // can't happen
                }
            }

            List<Tupel<String, Boolean>> functionNamesList = functionManager.getAllNames();
            List<String> newSavedFunctionsList = new ArrayList<String>();
            for(String name:nameorder) {
                for (Tupel<String, Boolean> entry : functionNamesList) {
                    if (entry.getFirst().equals(name) &&  entry.getSecond()) {
                        newSavedFunctionsList.add(entry.getFirst());
                    }
                }
            }
            functionSelectionBean.addAllSavedSelectedFunctions(newSavedFunctionsList);

        }

        //load the webstarts into the webstartManager.
        webstartSelectionBean.getSavedSelectedWebstarts().clear();
        for (Twebstart webstart : runnableItem.getWebstart()) {
            //Handle the description manually (if there is one).
            
            
            if (!webstart.getCustomContent().isEmpty()) {
                Twebstart.CustomContent customContent = new Twebstart.CustomContent();
                customContent.getContent().add(getCustomContent(webstart.getCustomContent().get(0)));
                customContent.setLang(webstart.getCustomContent().get(0).getLang());
                webstart.getCustomContent().set(0, customContent);
            }
            
            if (!webstart.getIntroductoryText().isEmpty()) {
                Twebstart.IntroductoryText intro = new Twebstart.IntroductoryText();
                intro.getContent().add(getCustomContent(webstart.getIntroductoryText().get(0)));
                intro.setLang(webstart.getIntroductoryText().get(0).getLang());
                webstart.getIntroductoryText().set(0, intro);
            }

            //Create ID according to wizard nameconventions
            String webstartTitle = webstart.getTitle().get(0).getValue();
            webstart.setId(IDGenerator.createTemporaryID(webstartTitle, WebstartBuilder.getID_BASE_TYPE()));

            //Add the file to the manager bean.
            while (!addingWorked) {
                try {
                    webstartManager.addWebstart(webstart);
                    webstartSelectionBean.getSavedSelectedWebstarts().add(webstartTitle);
                    addingWorked = true;
                } catch (BeansException e) {
                    //If this exception is thrown, the given name is ambigous.
                    webstartTitle = disambiguate(webstartTitle, WebstartBuilder.getID_BASE_TYPE());
                    webstart.getTitle().get(0).setValue(webstartTitle);
                    webstart.setId(IDGenerator.createTemporaryID(webstartTitle, FileBuilder.getID_BASE_TYPE()));
                }
            }
            addingWorked = false;
        }
        
        //load the files into the FileManager.
        fileSelectionBean.getSavedSelectedFiles().clear();
        for (Tfile file : runnableItem.getDownloadable()) {
            //Handle the description manually (if there is one).
            if (!file.getDescription().isEmpty()) {
                Tfile.Description fileDescription = new Tfile.Description();
                fileDescription.getContent().add(flowObjectList2String(
                        file.getDescription().get(0).getContent()));
                fileDescription.setLang(file.getDescription().get(0).getLang());
                file.getDescription().set(0, fileDescription);
            }

            //Create ID according to wizard nameconventions
            String fileName = file.getName().get(0).getValue();
            file.setId(IDGenerator.createTemporaryID(fileName, FileBuilder.getID_BASE_TYPE()));

            //Add the file to the manager bean.
            while (!addingWorked) {
                try {
                    String name = fileManager.addFile(file, FileStates.correctNoFile);
                    fileSelectionBean.getSavedSelectedFiles().add(name);
                    addingWorked = true;
                } catch (BeansException e) {
                    //If this exception is thrown, the given name is ambigous.
                    fileName = disambiguate(fileName, FileBuilder.getID_BASE_TYPE());
                    file.getName().get(0).setValue(fileName);
                    file.setId(IDGenerator.createTemporaryID(fileName, FileBuilder.getID_BASE_TYPE()));
                }
            }
            addingWorked = false;
        }

        //load the references into the ReferenceManager.
        //Only load references if there are any.
        referenceSelectionBean.getSavedSelectedReferences().clear();
        if (runnableItem.getReferences() != null) {
            for (String referenceString : runnableItem.getReferences().getReference()) {
                BibtexParser parser = new BibtexParser(new StringReader(referenceString));
                try {
                    parser.parse();
                    if (!parser.getPublicationObjects().isEmpty()) {
                        BiBiPublication publication = parser.getPublicationObjects().get(0);

                        //Add the reference to the manager bean.
                        while (!addingWorked) {
                            try {
                                String id = referenceManager.addReference(publication);
                                referenceSelectionBean.getSavedSelectedReferences().add(id);
                                addingWorked = true;
                            } catch (BeansException e) {
                                //If this exception is thrown, the given name is ambigous.
                                String referenceName = publication.getPubkey();
                                referenceName = disambiguate(referenceName, "reference");
                                publication.setPubkey(referenceName);
                            }
                        }
                    }
                } catch (ParseException e) {
                    if (FacesContext.getCurrentInstance() != null) {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty("referenceParsingError") + referenceString,
                                null);
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    } else {
                        System.err.println("Reference could not be parsed: " + referenceString);
                    }
                }
                addingWorked = false;
            }
        }
        //load the manual into the ManualManager.
        Tmanual manual = runnableItem.getManual();
        //Handle the manuals introductory text manually.
        Tmanual.IntroductoryText manualIntroduction = new Tmanual.IntroductoryText();
        manualIntroduction.getContent().add(getCustomContent(manual.getIntroductoryText().get(0)));
        manualIntroduction.setLang(manual.getIntroductoryText().get(0).getLang());
        manual.getIntroductoryText().set(0, manualIntroduction);
        //Handle the manuals custom content manually (if it is there).
        if (!manual.getCustomContent().isEmpty()) {
            Tmanual.CustomContent manualCustomContent = new Tmanual.CustomContent();
            manualCustomContent.getContent().add(getCustomContent(manual.getCustomContent().get(0)));
            manualCustomContent.setLang(manual.getCustomContent().get(0).getLang());
            manual.getCustomContent().set(0, manualCustomContent);
        }
        //add the manual to the manager bean.
        manualManager.setSavedManual(manual);

        //load the views into the viewManager.
        for (TrunnableItemView view : runnableItem.getView()) {
            //Handle the custom content manually (if there is any).
            if (!view.getCustomContent().isEmpty()) {
                TrunnableItemView.CustomContent viewCustomContent = new TrunnableItemView.CustomContent();
                viewCustomContent.getContent().add(getCustomContent(
                        view.getCustomContent().get(0)));
                viewCustomContent.setLang(view.getCustomContent().get(0).getLang());
                view.getCustomContent().set(0, viewCustomContent);
            }

            //Create ID according to wizard nameconventions
            String viewName = view.getType().value();
            view.setId(IDGenerator.createTemporaryID(viewName, ViewBuilder.getID_BASE_TYPE()));

            //Add the view to the manager-bean.

            try {
                viewManager.addView(view);
            } catch (BeansException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "doubleViewTypeError"),
                        ""));
            }
        }


        /*
         * Call the reload/cancel-function of all beans to reload the content
         * from the manager-beans.
         */
        webstartBean.cancel();
        webstartSelectionBean.cancel();
        basicInfoBean.reloadFromBuilder();
        executableInfoBean.reloadFromBuilder(!webstartSelectionBean.isWebstartSet());
        authorSelectionBean.cancel();
        fileBean.preRender();
        fileSelectionBean.cancel();
        inputBean.cancel();
        outputBean.cancel();
        parameterBean.cancel();
        parameterGroupBean.cancel();
        functionBean.cancel();
        functionSelectionBean.cancel();
        manualBean.cancel();
        referenceBean.preRender();
        referenceSelectionBean.cancel();

        
    }

    /**
     * Loads the beans needed to load an existing tool-description.
     */
    private void loadAllBeans() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (basicInfoBean == null) {
            basicInfoBean = (BasicInfoBean) context.getApplication().
                    evaluateExpressionGet(context, "#{basicInfoBean}",
                    BasicInfoBean.class);
        }
        if (basicInfoBuilder == null) {
            basicInfoBuilder = (BasicInfoBuilder) context.getApplication().
                    evaluateExpressionGet(context, "#{basicInfoBuilder}",
                    BasicInfoBuilder.class);
        }
        if (executableInfoBuilder == null) {
            executableInfoBuilder = (ExecutableInfoBuilder) context.getApplication().
                    evaluateExpressionGet(context, "#{executableInfoBuilder}",
                    ExecutableInfoBuilder.class);
        }
        if (executableInfoBean == null) {
            executableInfoBean = (ExecutableInfoBean) context.getApplication().
                    evaluateExpressionGet(context, "#{executableInfoBean}",
                    ExecutableInfoBean.class);
        }
        if (functionManager == null) {
            functionManager = (FunctionManager) context.getApplication().
                    evaluateExpressionGet(context, "#{functionManager}",
                    FunctionManager.class);
        }
        if (functionBean == null) {
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        if (functionSelectionBean == null) {
            functionSelectionBean = (FunctionSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionSelectionBean}",
                    FunctionSelectionBean.class);
        }
        if (authorManager == null) {
            authorManager = (AuthorManager) context.getApplication().
                    evaluateExpressionGet(context, "#{authorManager}",
                    AuthorManager.class);
        }
        if (authorSelectionBean == null) {
            authorSelectionBean = (AuthorSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{authorSelectionBean}",
                    AuthorSelectionBean.class);
        }
        if (inputManager == null) {
            inputManager = (InputManager) context.getApplication().
                    evaluateExpressionGet(context, "#{inputManager}",
                    InputManager.class);
        }

        if (inputBean == null) {
            inputBean = (InputBean) context.getApplication().
                    evaluateExpressionGet(context, "#{inputBean}",
                    InputBean.class);
        }
        if (outputManager == null) {
            outputManager = (OutputManager) context.getApplication().
                    evaluateExpressionGet(context, "#{outputManager}",
                    OutputManager.class);
        }
        if (outputBean == null) {
            outputBean = (OutputBean) context.getApplication().
                    evaluateExpressionGet(context, "#{outputBean}",
                    OutputBean.class);
        }
        if (outputFileManager == null) {
            outputFileManager = (OutputFileManager) context.getApplication().
                    evaluateExpressionGet(context, "#{outputFileManager}",
                    OutputFileManager.class);
        }
        if (outputFileBean == null) {
            outputFileBean = (OutputFileBean) context.getApplication().
                    evaluateExpressionGet(context, "#{outputFileBean}",
                    OutputFileBean.class);
        }
        if (parameterManager == null) {
            parameterManager = (ParameterManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterManager}",
                    ParameterManager.class);
        }
        if (parameterBean == null) {
            parameterBean = (ParameterBean) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterBean}",
                    ParameterBean.class);
        }
        if (parameterGroupBean == null) {
            parameterGroupBean = (ParameterGroupBean) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupBean}",
                    ParameterGroupBean.class);
        }
        if (parameterGroupManager == null) {
            parameterGroupManager = (ParameterGroupManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupManager}",
                    ParameterGroupManager.class);
        }
        if (parameterDependencyManager == null) {
            parameterDependencyManager = (ParameterDependencyManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterDependencyManager}",
                    ParameterDependencyManager.class);
        }
        if (fileManager == null) {
            fileManager = (FileManager) context.getApplication().
                    evaluateExpressionGet(context, "#{fileManager}",
                    FileManager.class);
        }
        if (fileSelectionBean == null) {
            fileSelectionBean = (FileSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{fileSelectionBean}",
                    FileSelectionBean.class);
        }
        if (fileBean == null) {
            fileBean = (EditFileBean) context.getApplication().
                    evaluateExpressionGet(context, "#{editFileBean}",
                    EditFileBean.class);
        }
        if (referenceManager == null) {
            referenceManager = (ReferenceManager) context.getApplication().
                    evaluateExpressionGet(context, "#{referenceManager}",
                    ReferenceManager.class);
        }
        if (referenceBean == null) {
            referenceBean = (ReferenceBean) context.getApplication().
                    evaluateExpressionGet(context, "#{referenceBean}",
                    ReferenceBean.class);
        }
        if (referenceSelectionBean == null) {
            referenceSelectionBean = (ReferenceSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{referenceSelectionBean}",
                    ReferenceSelectionBean.class);
        }
        if (manualManager == null) {
            manualManager = (ManualManager) context.getApplication().
                    evaluateExpressionGet(context, "#{manualManager}",
                    ManualManager.class);
        }
        if (manualBean == null) {
            manualBean = (ManualBean) context.getApplication().
                    evaluateExpressionGet(context, "#{manualBean}",
                    ManualBean.class);
        }
        if (viewManager == null) {
            viewManager = (ViewManager) context.getApplication().
                    evaluateExpressionGet(context, "#{viewManager}",
                    ViewManager.class);
        }
        if (webstartBean == null) {
            webstartBean = (WebstartBean) context.getApplication().
                    evaluateExpressionGet(context, "#{webstartBean}",
                    WebstartBean.class);
        }
        if (webstartManager == null) {
            webstartManager = (WebstartManager) context.getApplication().
                    evaluateExpressionGet(context, "#{webstartManager}",
                    WebstartManager.class);
        }
        if (webstartSelectionBean == null) {
            webstartSelectionBean = (WebstartSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{webstartSelectionBean}",
                    WebstartSelectionBean.class);
        }

    }

    /**
     * Ensures that a string is not the same as before.
     *
     * @param input input String
     * @param typestring to disambiguate the type
     * @return disambiguated string.
     */
    public String disambiguate(String input, String typestring) {
        return disambiguator.disambiguateName(input, typestring);
    }

    

    /**
     * Flatten a flow content (from a description), a list content objects, to 
     * a string representation.
     *
     * @param list of Flow content object
     * @return content as String.
     */
    public static String flowObjectList2String(List<Object> content) {

        StringWriter stringWriter = new StringWriter();
        Flow flow = new Flow();
        flow.getContent().addAll(content);
             
        try {      
            JAXBContext microHtmlContext = JAXBContext.newInstance(MICROHTMLNAMESPACE);

            Marshaller microHTMLMarshaller = microHtmlContext.createMarshaller();
            microHTMLMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            JAXBElement obj = new JAXBElement(new QName("bibiserv:de.unibi.techfak.bibiserv.cms.microhtml"),Flow.class,flow);
            microHTMLMarshaller.marshal(obj, stringWriter);

            return stringWriter.toString();
        } catch (JAXBException e) {
            e.printStackTrace(); //debug
            return PropertyManager.getProperty("couldNotLoadDescriptionError");
        } catch (ClassCastException e) {
            return PropertyManager.getProperty("couldNotLoadDescriptionError");
        } finally {
            try {
                stringWriter.close();
            } catch (IOException e) {
                //If the closing does not work, it doesn't.
            }
        }
    }

    /**
     * Extracts the minihtml data of custom content as String.
     *
     * @param customContent input minihtml data (Flow root element).
     * @return minihtml data as String.
     */
    public static String getCustomContent(Object customContent) {


        de.unibi.techfak.bibiserv.cms.minihtml.Flow customContentRootElement;

        try {
            customContentRootElement = (de.unibi.techfak.bibiserv.cms.minihtml.Flow) customContent;
        } catch (ClassCastException e) {
            return PropertyManager.getProperty("couldNotLoadDescriptionError");
        }

        StringWriter stringWriter = new StringWriter();

        try {
            JAXBContext miniHtmlContext = JAXBContext.newInstance(
                    MICROHTMLNAMESPACE + ":" + MINIHTMLNAMESPACE);

            Marshaller miniHTMLMarshaller = miniHtmlContext.createMarshaller();
            miniHTMLMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);

            de.unibi.techfak.bibiserv.cms.minihtml.ObjectFactory miniHTMLFactory =
                    new de.unibi.techfak.bibiserv.cms.minihtml.ObjectFactory();

            miniHTMLMarshaller.marshal(miniHTMLFactory.createMinihtml(customContentRootElement),
                    stringWriter);

            return stringWriter.toString();

        } catch (JAXBException e) {
            return PropertyManager.getProperty("couldNotLoadDescriptionError");
        } finally {
            try {
                stringWriter.close();
            } catch (IOException e) {
                //If the closing does not work, it doesn't.
            }
        }
    }

    /**
     * does the unmarshalling of a xmlfile given as an inputStream.
     *
     * @param inputData a valid xml-file as inputStream.
     */
    public TrunnableItem unmarshallXML(InputStream inputData) {

        TrunnableItem unmarshalledRunnable;

        if (!(inputData instanceof BufferedInputStream)) {
            inputData = new BufferedInputStream(inputData);
        }

        try {
            JAXBContext context = JAXBContext.newInstance(CMSNAMESPACE + ":"
                    + CMSNAMESPACE + ".microhtml" + ":" + CMSNAMESPACE + ".minihtml");

            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement jaxbObject;

            jaxbObject = (JAXBElement) unmarshaller.unmarshal(inputData);

            unmarshalledRunnable = (TrunnableItem) jaxbObject.getValue();

            //Inform the user about sucessfull unmarshalling.

            if (FacesContext.getCurrentInstance() != null) {
                FacesMessage msg = new FacesMessage(
                        PropertyManager.getProperty("unmarshallingSucessfull"), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                System.out.println("unmarshalling was sucessfull.");
            }

            return unmarshalledRunnable;

        } catch (JAXBException e) {
            if (FacesContext.getCurrentInstance() != null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("unmarshallingError") + " " + e.getMessage(), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                System.err.println("Unmarshalling did not work.");
            }
        }
        return null;
    }

    private void changeDependencyDefinitions(List<Tupel<String, String>> replaceList) {
        for (Tdependency dependency : parameterDependencyManager.getValues()) {
            String definition = dependency.getDependencyDefinition();
            for (Tupel<String, String> replace : replaceList) {
                definition = definition.replaceAll("@" + replace.getFirst(),
                        "<" + replace.getSecond() + ">");
            }
            dependency.setDependencyDefinition(definition);
        }
    }
}
