package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputFileBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputFileManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.ToutputFile;
import java.io.IOException;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * This bean is used to manage user input from additionalOutput.xhtml
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class OutputFileBean {

    // Input Data
    private String name;
    private String displayName;
    private String fileName;
    private String folder;
    private String contenttype;
    // Loading and Saving
    private String loadedFrom;
    private OutputFileManager manager;
    private List<String> outputFileNameList;
    private boolean outputFilesEmpty;
    private boolean renderLoadedFrom;
    
    private String position;
    private boolean renderUnsavedChanges;

    public OutputFileBean() {

        FacesContext context = FacesContext.getCurrentInstance();
        manager = (OutputFileManager) context.getApplication().
                evaluateExpressionGet(context, "#{outputFileManager}",
                OutputFileManager.class);

        resetAll();
    }

    /**
     * Loads the output file with name.
     * @param name name of the Output file.
     */
    public void loadOutputFile(String name) {
        ToutputFile output;
        try {
            output = manager.getOutputByName(name);
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
        this.displayName = output.getName().get(0).getValue();
        this.name = name;
        if(output.isSetFolder()) {
            this.folder = output.getFolder();
        }
        this.contenttype = output.getContenttype();
        this.fileName = output.getFilename();
    }


    /**
     * Resets data of bean.
     */
    private void resetAll() {

        loadedFrom = "";
        name = "";
        renderLoadedFrom = false;
        displayName = "";
        fileName = "";
        folder = "";
        contenttype="";
        
        outputFileNameList = manager.getAllNames();
        outputFilesEmpty = manager.isEmpty();
        
        renderUnsavedChanges = false;
        position = "";

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
     * Validates input and adds warnings to context.
     *
     * @return  true: everything ok; false: smth is wrong
     */
    private boolean validate() {

        boolean ret = true;

        if (name.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            ret = false;
        }
        
        if(displayName.isEmpty()){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("displayNameError"), ""));
            ret = false; 
        }
        if(contenttype.isEmpty()){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("contenttypeNameError"), ""));
            ret = false; 
        }
        if(fileName.isEmpty()){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("fileNameError"), ""));
            ret = false; 
        }
        

        return ret;
    }

    private boolean saveAll() {

        if (!validate()) {
            return false;
        }

        ToutputFile newOutput = OutputFileBuilder.createOutputFile(name,
                displayName, fileName, folder,  contenttype, BasicBeanData.StandardLanguage);
        if (loadedFrom.isEmpty()) { // not loaded
            try {
                manager.addOutput(newOutput, name);
            } catch (BeansException ex) {  // could not be added
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "additionalOutputAlreadyExistsError"),
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
                manager.editOutput(loadedFrom, name, newOutput); // try editing
            } catch (BeansException ex) {//could not edit
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "additionalOutputAlreadyExistsError"),
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
        outputFileNameList = manager.getAllNames();
        outputFilesEmpty = manager.isEmpty();

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

    public void newOutputFile() {
        resetAll();
        calcPostionString();
    }

    public void editOutputFile(String name) {
        calcPostionString();
        loadOutputFile(name);
    }

    public void removeOutputFile(String name) {
        try {
            manager.removeOutputByName(name);
            //reset inputList
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
            outputFileNameList = manager.getAllNames();
            outputFilesEmpty = manager.isEmpty();
        } catch (BeansException ex) {
            // should not happen when used right
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public List<String> getOutputFileNameList() {
        return outputFileNameList;
    }

    public boolean isOutputFilesEmpty() {
        return outputFilesEmpty;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }


}
