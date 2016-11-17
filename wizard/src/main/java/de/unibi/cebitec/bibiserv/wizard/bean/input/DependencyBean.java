package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.DescriptionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyTester;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.exceptions.DependencyParserTestError;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import java.io.IOException;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 * This bean is used to manage all user input from dependency.xhtml
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class DependencyBean extends DescriptionBean {

    private boolean renderLoadedFrom;
    private String loadedFrom;
    private String name;
    private String shortDescription;
    private String errors;
    private String definition;
    private String hiddenpos;
    private String hiddenposEnd;
    private String hiddentext;
    private List<String> parameterNameList;
    private List<String> functionNameList;
    private boolean dependenciesEmpty;
    private List<String> dependencyNamesList;
    private String functionToTest;
    private ParameterManager parameterManager;
    private FunctionManager functionManager;
    private ParameterDependencyManager dependencyManager;
    private FunctionBean functionBean;
    
    private boolean renderUnsavedChanges;

    public DependencyBean() {

        xhtml = "dependency.xhtml";

        FacesContext context = FacesContext.getCurrentInstance();
        parameterManager = (ParameterManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterManager}",
                ParameterManager.class);
        functionManager = (FunctionManager) context.getApplication().
                evaluateExpressionGet(context, "#{functionManager}",
                FunctionManager.class);
        dependencyManager =
                (ParameterDependencyManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterDependencyManager}",
                ParameterDependencyManager.class);
        functionBean = null;

        resetAll();
    }

    private void resetAll() {
        //reset loaded
        loadedFrom = "";
        renderLoadedFrom = false;
        // reset normal data
        name = "";
        shortDescription = "";
        errors = "";
        definition = "";
        hiddenpos = "";
        hiddentext = "";

        dependencyNamesList = dependencyManager.getAllNames();
        dependenciesEmpty = dependencyManager.isEmpty();

        functionNameList = functionManager.getAllValidNames();
        parameterNameList = parameterManager.getAllNames();
        
        renderUnsavedChanges= false;
    }

    /**
     * Load the Dependency with given name.
     * @param name name of the dependency
     */
    private void loadDependency(String name) {
        // reset current data
        resetAll();
        //try loading TparamGroupObject
        Tdependency dependency;
        try {
            dependency = dependencyManager.getParameterDependencyByName(name);
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openDependencyError"),
                    ""));
            return;
        }

        this.name = dependency.getName().get(0).getValue();
        loadedFrom = name;
        if (!dependency.getShortDescription().isEmpty()) {
            shortDescription =
                    dependency.getShortDescription().get(0).getValue();
        }
        definition = dependency.getDependencyDefinition();
    }

    /**
     * Validates the current content and adds error messages.
     * @return true: everything is OK; false: smth is wrong
     */
    private boolean validate() {
        boolean ret = true;

        if (name.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            ret = false;
        }

        if (shortDescription.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("shortDescriptionError"), ""));
            ret = false;
        }

        if (definition.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("definitionError"), ""));
            ret = false;
        }

        return ret;
    }

    private boolean saveAll() {
        if (!validate()) {
            return false;
        }

        Tdependency newDep = ParameterDependencyBuilder.createdependency(name,
                shortDescription, description, BasicBeanData.StandardLanguage,
                definition);

        if (loadedFrom.isEmpty()) { // not loaded
            try {
                dependencyManager.addDependency(newDep);
            } catch (BeansException ex) {  // could not be added
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "dependencyAlreadyExistsError"),
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
                dependencyManager.editDependency(loadedFrom, newDep); // try editing
            } catch (BeansException ex) {//could not edit
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "dependencyAlreadyExistsError"),
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
        dependencyNamesList = dependencyManager.getAllNames();
        dependenciesEmpty = dependencyManager.isEmpty();

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

    public void reload() {

        errors = "";

        dependencyNamesList = dependencyManager.getAllNames();
        dependenciesEmpty = dependencyManager.isEmpty();

        functionNameList = functionManager.getAllValidNames();
        parameterNameList = parameterManager.getAllNames();
    }

    public void insertText() {

        int pos = 0;
        try {
            pos = Integer.parseInt(hiddenpos);
        } catch (NumberFormatException e) {
            // ignore
        }
        int posEnd = 0;
        try {
            posEnd = Integer.parseInt(hiddenposEnd);
        } catch (NumberFormatException e) {
            // ignore
        }

        String linebreak = "";
        if(definition.contains("\r\n")){
            linebreak = "\r\n";
        } else if(definition.contains("\n")) {
            linebreak = "\n";
        } else if(definition.contains("\r")){
            linebreak = "\r";
        }
        
        definition = definition.replaceAll("\r\n|\r|\n","\n");
        
        StringBuilder s = new StringBuilder(definition);
        s.replace(pos, posEnd, "<" + hiddentext + ">");
        definition = s.toString();
        
        definition = definition.replaceAll("\n", linebreak);
        
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("start", false);
        context.addCallbackParam("pos", pos);
        
        renderUnsavedChanges = true;
    }

    public void startInsert(String text) {
        hiddentext = text;
        renderUnsavedChanges = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("start", true);
    }

    public void testFunction() {

        if (functionBean == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }

        Tfunction function;
        // is the currently loaded function tested?
        if (functionBean.getLoadedFrom().equals(functionToTest)) {
            function = functionBean.getCurrentFunctionAsObject();
        } else {

            try {
                function = functionManager.getFunctionByName(functionToTest);
            } catch (BeansException ex) {
                return;
            }
        }
        Tdependency dependency = ParameterDependencyBuilder.createdependency(
                name,
                shortDescription, description, BasicBeanData.StandardLanguage,
                definition);

        ParameterDependencyTester tester = new ParameterDependencyTester();

        if (tester.testDependency(function, dependency)) {
            errors = PropertyManager.getProperty("depNoError");
        } else {
            // errors were encountered, make them readable
            StringBuilder errorString = new StringBuilder();
            for (DependencyParserTestError error : tester.getExceptions()) {
                errorString.append(error.createPrintableMessage()).append("\n");
            }
            errors = errorString.toString();
        }
    }

    public List<String> getParameters() {
        return parameterNameList;
    }

    public void newDependency() {
        resetAll();
        calcPostionString();
    }

    public void editDependency(String name) {
        loadDependency(name);
        calcPostionString();
    }

    public void removeDependency(String str) {
        try {
            dependencyManager.removeParameterDependencyByName(str);
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
            reload();
        } catch (BeansException ex) {
            // should not happen
        }
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getHiddenpos() {
        return hiddenpos;
    }

    public void setHiddenpos(String hiddenpos) {
        this.hiddenpos = hiddenpos;
    }

    public boolean isDependenciesEmpty() {
        return dependenciesEmpty;
    }

    public void setDependenciesEmpty(boolean dependenciesEmpty) {
        this.dependenciesEmpty = dependenciesEmpty;
    }

    public List<String> getDependencyNamesList() {
        return dependencyNamesList;
    }

    public void setDependencyNamesList(List<String> dependencyNamesList) {
        this.dependencyNamesList = dependencyNamesList;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public List<String> getFunctionNameList() {
        return functionNameList;
    }

    public void setFunctionNameList(List<String> functionNameList) {
        this.functionNameList = functionNameList;
    }

    public String getHiddentext() {
        return hiddentext;
    }

    public void setHiddentext(String hiddentext) {
        this.hiddentext = hiddentext;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public void setLoadedFrom(String loadedFrom) {
        this.loadedFrom = loadedFrom;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParameterNameList() {
        return parameterNameList;
    }

    public void setParameterNameList(List<String> parameterNameList) {
        this.parameterNameList = parameterNameList;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    public void setRenderLoadedFrom(boolean renderLoadedFrom) {
        this.renderLoadedFrom = renderLoadedFrom;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFunctionToTest() {
        return functionToTest;
    }

    public void setFunctionToTest(String functionToTest) {
        this.functionToTest = functionToTest;
    }

    public String getHiddenposEnd() {
        return hiddenposEnd;
    }

    public void setHiddenposEnd(String hiddenposEnd) {
        this.hiddenposEnd = hiddenposEnd;
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
    
    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }

    public void setRenderUnsavedChanges(boolean renderUnsavedChanges) {
        this.renderUnsavedChanges = renderUnsavedChanges;
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
