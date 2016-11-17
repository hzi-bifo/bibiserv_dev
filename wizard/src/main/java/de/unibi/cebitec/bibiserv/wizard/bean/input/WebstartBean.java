package de.unibi.cebitec.bibiserv.wizard.bean.input;

import com.sun.java.jnlp.Jnlp;
import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.DescriptionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.GeneralCallback;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.BasicInfoBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.WebstartBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.WebstartManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Twebstart;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * This bean manages the communication with the basicInfo.xhtml-page.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class WebstartBean extends DescriptionBean {

    private String title = "";
    private String introDescription = "";
    private String customContent = "";
    private String jnlpString = "";
    private String loadedFrom = "";
    private boolean renderLoadedFrom = false;
    
    private WebstartManager manager;
    
    private boolean renderUnsavedChanges;
    
    private List<String> webstartNamesList;
    private boolean webstartsEmpty;

    public WebstartBean() {
        
        this.xhtml = "webstart.xhtml?faces-redirect=true";

        FacesContext context = FacesContext.getCurrentInstance();
        manager = (WebstartManager) context.getApplication().
                evaluateExpressionGet(context, "#{webstartManager}",
                WebstartManager.class);
        
        position = PropertyManager.getProperty("webstartHeader");
        reset();
    }


    public boolean save() {

        Jnlp jnlp_object;
        if (validate()) {
            
            try {
                JAXBContext jaxbc = JAXBContext.newInstance(Jnlp.class);
                Unmarshaller um = jaxbc.createUnmarshaller();
                jnlp_object = (Jnlp) um.unmarshal(new StringReader(jnlpString));
            } catch (JAXBException ex) {
                FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("jnlpError") + ex.getMessage() , ""));
                return false;
            }
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    PropertyManager.getProperty("saveSuccesful"), ""));
            
        } else {
            return false;
        }
        
        Twebstart newWebstart = WebstartBuilder.createView(title, introDescription, customContent, jnlp_object, BasicBeanData.StandardLanguage);
        
        if (loadedFrom.isEmpty()) { // not loaded
            
            try {
                manager.addWebstart(newWebstart);
                loadedFrom = title;
                renderLoadedFrom = true;
                
            } catch (BeansException ex) {  // could not be added
                if (ex.getExceptionType() == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("webstartAlreadyExistsError"),
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
                manager.editWebstart(loadedFrom, newWebstart); // try editing
                loadedFrom = title;
                renderLoadedFrom = true;
                
            } catch (BeansException ex) {//could not edit
                
                if (ex.getExceptionType() == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("webstartAlreadyExistsError"),
                            ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("couldNotSave"), ""));
                }
                return false;
            }
        }

        getAvailableData();
        renderUnsavedChanges = false;
        
        return true;
    }

    public void saveReturn() {

        if (!save()) {
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().
                getViewHandler().getActionURL(ctx,
                "/webstartSelection.xhtml?faces-redirect=true"));

        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }
    }

    private boolean validate() {
        boolean ret = true;

        if (title.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("webstartTitleError"), ""));
            ret = false;
        }
        if (introDescription == null || introDescription.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("webstartIntroDescriptionError"), ""));
            ret = false;
        }

        return ret;
    }
    
    public String cancel() {
        reset();
        return "webstartSelection.xhtml?faces-redirect=true";
    }

    
    private void getAvailableData() {
        webstartNamesList = manager.getAllNames();
        webstartsEmpty = manager.isEmpty();
    }
    
    public void reset() {
        getAvailableData();
        title = "";
        introDescription = "";
        customContent = "";
        jnlpString = "";
        loadedFrom = "";
        renderUnsavedChanges = false;
        renderLoadedFrom = false;
    }


    public void loadWebstart(String name) {

        Twebstart webstart;
        try {
            webstart = manager.getWebstartByName(name);
        } catch (BeansException ex) {
            // should not occur when used correctly
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("couldNotLoad"), ""));
            return;
        }

        reset();

        // mark we want to change existing input
        this.loadedFrom = name;
        renderLoadedFrom = true;

        // set data of loaded object
        this.title = webstart.getTitle().get(0).getValue();
        this.introDescription = (String) webstart.getIntroductoryText().get(0).getContent().get(0);
        
        if (webstart.isSetCustomContent()) {
            this.customContent = (String) webstart.getCustomContent().get(0).getContent().get(0);
        }
       
        try {
            JAXBContext jaxbc = JAXBContext.newInstance(Jnlp.class);
            Marshaller ma = jaxbc.createMarshaller();
            
            StringWriter sw = new StringWriter();
            ma.marshal(webstart.getJnlp(), sw);
            jnlpString = sw.toString();
            
        } catch (JAXBException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("couldNotLoad") + ex.getMessage(), ""));
        }

    }
    
    
    public void newWebstart() {
        reset();
    }
    
    public String introDescription(){
        FacesContext context = FacesContext.getCurrentInstance();
        EditorMiniBean editor = (EditorMiniBean) context.getApplication().
                evaluateExpressionGet(context, "#{editorMiniBean}",
                EditorMiniBean.class);

        editor.setReturnTo(xhtml);
        editor.setEditorContent(introDescription);
        
        String name = getName();
        if(name.isEmpty()) {
            name = PropertyManager.getProperty("unnamed");
        }
        editor.setPosition(position+" - "+name);

        GeneralCallback<String> contentCaller = new GeneralCallback<String>() {

            @Override
            public void setResult(String result) {
                introDescription = result;
            }
        };

        editor.setCallback(contentCaller);

        return "editorMini.xhtml?faces-redirect=true";
    }
    
    public String editCustomContent(){
        FacesContext context = FacesContext.getCurrentInstance();
        EditorMiniBean editor = (EditorMiniBean) context.getApplication().
                evaluateExpressionGet(context, "#{editorMiniBean}",
                EditorMiniBean.class);

        editor.setReturnTo(xhtml);
        editor.setEditorContent(customContent);
        
        String name = getName();
        if(name.isEmpty()) {
            name = PropertyManager.getProperty("unnamed");
        }
        editor.setPosition(position+" - "+name);

        GeneralCallback<String> contentCaller = new GeneralCallback<String>() {

            @Override
            public void setResult(String result) {
                customContent = result;
            }
        };

        editor.setCallback(contentCaller);

        return "editorMini.xhtml?faces-redirect=true";
    }

     public void removeWebstart(String id) {
        try {
            manager.removeWebstartByName(id);
            if (id.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
        } catch (BeansException ex) {
        }
        getAvailableData();
    }
    
    
    
    
    @Override
    public String getName() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroDescription() {
        return introDescription;
    }

    public void setIntroDescription(String introDescription) {
        this.introDescription = introDescription;
    }

    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        this.customContent = customContent;
    }

    public String getJnlpString() {
        return jnlpString;
    }

    public void setJnlpString(String jnlpString) {
        this.jnlpString = jnlpString;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public void setLoadedFrom(String loadedFrom) {
        this.loadedFrom = loadedFrom;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    public void setRenderLoadedFrom(boolean renderLoadedFrom) {
        this.renderLoadedFrom = renderLoadedFrom;
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

    public List<String> getWebstartNamesList() {
        return webstartNamesList;
    }

    public boolean isWebstartsEmpty() {
        return webstartsEmpty;
    }
    
    
     
}