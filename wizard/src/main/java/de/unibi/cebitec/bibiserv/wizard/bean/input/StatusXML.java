package de.unibi.cebitec.bibiserv.wizard.bean.input;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import de.unibi.cebitec.bibiserv.wizard.bean.StatusStorage;
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
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterGroupManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ReferenceManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ViewManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.WebstartManager;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
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
public class StatusXML {

    // All beans that are questioned during export process
    private FunctionManager functionManager;
    private FunctionSelectionBean functionSelection;
    private AuthorManager authorManager;
    private AuthorSelectionBean authorSelection;
    private FileManager fileManager;
    private ImageFileManager imageFileManager;
    private FileSelectionBean fileSelectionBean;
    private ImageFileSelectionBean imageFileSelectionBean;
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
    private ParameterDependencyManager parameterDependencyManager;
    private WebstartManager webstartManager;
    private WebstartSelectionBean webstartSelectionBean;
    
    // just for load
    private BasicInfoBean basicInfoBean;
    private ExecutableInfoBean executableInfoBean;
    private EditFileBean fileBean;
    private InputBean inputBean;
    private OutputBean outputBean;
    private ParameterBean parameterBean;
    private ParameterGroupBean parameterGroupBean;
    private FunctionBean functionBean;
    private ReferenceBean referenceBean;
    private ManualBean manualBean;
    

    public StatusXML() {
        // retrieve current beans
        loadAllBeans();
    }

    private void loadAllBeans() {
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
            parameterDependencyManager = (ParameterDependencyManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterDependencyManager}",
                    ParameterDependencyManager.class);

            basicInfoBean = (BasicInfoBean) context.getApplication().
                    evaluateExpressionGet(context, "#{basicInfoBean}",
                    BasicInfoBean.class);
            executableInfoBean = (ExecutableInfoBean) context.getApplication().
                    evaluateExpressionGet(context, "#{executableInfoBean}",
                    ExecutableInfoBean.class);
            fileBean = (EditFileBean) context.getApplication().
                    evaluateExpressionGet(context, "#{editFileBean}",
                    EditFileBean.class);
            inputBean = (InputBean) context.getApplication().
                    evaluateExpressionGet(context, "#{inputBean}",
                    InputBean.class);
            outputBean = (OutputBean) context.getApplication().
                    evaluateExpressionGet(context, "#{outputBean}",
                    OutputBean.class);
            parameterBean = (ParameterBean) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterBean}",
                    ParameterBean.class);
            parameterGroupBean = (ParameterGroupBean) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupBean}",
                    ParameterGroupBean.class);
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
            referenceBean = (ReferenceBean) context.getApplication().
                    evaluateExpressionGet(context, "#{referenceBean}",
                    ReferenceBean.class);
            manualBean = (ManualBean) context.getApplication().
                    evaluateExpressionGet(context, "#{manualBean}",
                    ManualBean.class);

            webstartManager = (WebstartManager) context.getApplication().
                    evaluateExpressionGet(context, "#{webstartManager}",
                    WebstartManager.class);
            webstartSelectionBean = (WebstartSelectionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{webstartSelectionBean}",
                    WebstartSelectionBean.class);

        }
    }

    public String returnToOverview() {
        //cancel current codegen calculation if there is one.
        return "overview.xhtml?faces-redirect=true";
    }

    public StreamedContent generateFile() {

        StatusStorage storage = new StatusStorage();

        storage.setRunnableItem(basicInfoBuilder.getTRunnable());
        storage.setExecuteable(executeableInfoBuilder.getTexecutable());

        storage.setAuthorList(authorManager.getAuthors());
        storage.setSelectedAuthors(authorSelection.getSavedSelectedStrings());

        storage.setInputList(inputManager.getInput());
        storage.setOutputFileList(outputFileManager.getOutput());
        storage.setOutputList(outputManager.getOutput());
        storage.setDependencyList(parameterDependencyManager.getDependencies());

        storage.setParameterList(parameterManager.getParameter());
        storage.setParamGroupList(parameterGroupManager.getParamGroup());

        storage.setFunctionList(functionManager.getFunctions());
        storage.setSelectedFunctions(functionSelection.getSelectedFunctions());

        storage.setFiles(fileManager.getFiles());
        storage.setSelectedFiles(fileSelectionBean.getSavedSelectedFiles());

        storage.setImageFiles(imageFileManager.getFiles());
        storage.setSelectedImageFiles(imageFileSelectionBean.getSavedSelectedStrings());

        storage.setReferences(referenceManager.getReferences());
        storage.setSelectedReferences(referenceSelectionBean.getSavedSelectedReferences());

        storage.setManual(manualManager.getSavedManual());

        storage.setViewList(viewManager.getViews());
        
        storage.setWebstarts(webstartManager.getWebstarts());
        storage.setSelectedWebstarts(webstartSelectionBean.getSavedSelectedWebstarts());

        XStream xstream = new XStream();
        String xml = xstream.toXML(storage);

        return new DefaultStreamedContent(new ByteArrayInputStream(xml.getBytes()), xml, "wizarddump_" + basicInfoBuilder.getToolName() + ".wiz");
    }

    public void handleFileUpload(FileUploadEvent event) {

        //Clear the current session

        FacesContext context = FacesContext.getCurrentInstance();

        OverviewBean overviewBean = (OverviewBean) context.getApplication().
                evaluateExpressionGet(context, "#{overviewBean}",
                OverviewBean.class);
        overviewBean.clearSession();

        loadAllBeans();

        // create object from xml
        XStream xstream = new XStream();
        StatusStorage store;
        try {
            store = (StatusStorage) xstream.fromXML(event.getFile().getInputstream());
        } catch (IOException e) {
            if (FacesContext.getCurrentInstance() != null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, PropertyManager.getProperty("ioerror") + ": " + e.getMessage(), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                System.err.println("Filereading of statefile did not work.");
            }
            return;
        } catch (XStreamException e) {
            if (FacesContext.getCurrentInstance() != null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, PropertyManager.getProperty("invalidStateFile") + ": " + e.getMessage(), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            return;
        }

        try {
            // set everything up
            basicInfoBuilder.setRunnableItem(store.getRunnableItem());
            executeableInfoBuilder.setExecutableItem(store.getExecuteable());

            authorSelection.getSavedSelectedStringsReference().clear();
            authorSelection.getSavedSelectedStringsReference().addAll(store.getSelectedAuthors());
            authorManager.setAuthors(store.getAuthorList());

            inputManager.setInput(store.getInputList());

            outputFileManager.setOutput(store.getOutputFileList());

            parameterDependencyManager.setDependencies(store.getDependencyList());

            outputManager.setOutput(store.getOutputList());

            parameterManager.setParameter(store.getParameterList());
            parameterGroupManager.setParamGroup(store.getParamGroupList());

            functionManager.setFunctions(store.getFunctionList());
            functionSelection.addAllSavedSelectedFunctions(store.getSelectedFunctions());

            fileSelectionBean.getSavedSelectedFiles().clear();
            fileSelectionBean.addAllSavedSelectedFiles(store.getSelectedFiles());
            fileManager.setFiles(store.getFiles());

            imageFileSelectionBean.getSavedSelectedStringsReference().clear();
            imageFileSelectionBean.getSavedSelectedStringsReference().addAll(store.getSelectedFiles());
            imageFileManager.setFiles(store.getImageFiles());

            referenceManager.setReferences(store.getReferences());
            referenceSelectionBean.getSavedSelectedReferences().clear();
            referenceSelectionBean.addAllSavedSelectedReferences(store.getSelectedReferences());

            manualManager.setSavedManual(store.getManual());

            viewManager.setViews(store.getViewList());
            
            webstartManager.setWebstarts(store.getWebstarts());
            webstartSelectionBean.getSavedSelectedWebstarts().clear();
            webstartSelectionBean.addAllSavedSelectedWebstarts(store.getSelectedWebstarts());
            
            /*
             * Call the reload/cancel-function of all beans to reload the content
             * from the manager-beans.
             */
            webstartSelectionBean.cancel();
            basicInfoBean.reloadFromBuilder();
            executableInfoBean.reloadFromBuilder(!webstartSelectionBean.isWebstartSet());
            authorSelection.cancel();
            fileBean.preRender();
            fileSelectionBean.cancel();
            inputBean.cancel();
            outputBean.cancel();
            parameterBean.cancel();
            parameterGroupBean.cancel();
            functionBean.cancel();
            functionSelection.cancel();
            manualBean.cancel();
            referenceBean.preRender();
            referenceSelectionBean.cancel();

            if (FacesContext.getCurrentInstance() != null) {
                FacesMessage msg = new FacesMessage(
                        PropertyManager.getProperty("loadStateSucessfull"), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (NullPointerException e) {
            if (FacesContext.getCurrentInstance() != null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, PropertyManager.getProperty("nullpointer") + ": " + e.getMessage(), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                System.err.println("Filereading of statefile did not work.");
            }
        }
    }
}
