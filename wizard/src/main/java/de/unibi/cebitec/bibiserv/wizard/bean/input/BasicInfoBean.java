package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.DescriptionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.GeneralCallback;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.BasicInfoBuilder;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * This bean manages the communication with the basicInfo.xhtml-page.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class BasicInfoBean extends DescriptionBean {

    private String toolName = "";
    private String shortDescription = "";
    private String customContent = "";
    private String toolTipText = "";
    private String keywords = "";
    private String basicInfoStatus = TrafficLightEnum.RED.getPath();
    private BasicInfoBuilder builder;
    
    private boolean renderUnsavedChanges;

    public BasicInfoBean() {
        this.xhtml = "basicInfo.xhtml?faces-redirect=true";

        FacesContext context = FacesContext.getCurrentInstance();
        builder = (BasicInfoBuilder) context.getApplication().
                evaluateExpressionGet(context, "#{basicInfoBuilder}",
                BasicInfoBuilder.class);
        
        position = PropertyManager.getProperty("basicInfo");
        renderUnsavedChanges = false;
        
    }

    public void save() {

        if (validate()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    PropertyManager.getProperty("saveSuccesful"), ""));
        } else {
            return;
        }

        builder.createTrunnable(toolName, shortDescription, description, customContent,
                toolTipText, keywords);

        renderUnsavedChanges = false;
        checkAndSetStatus();
    }

    public void saveReturn() {

        if (!validate()) {
            return;

        }

        builder.createTrunnable(toolName, shortDescription, description,
                customContent, toolTipText, keywords);

        checkAndSetStatus();

        //Redirect using javax-context.

        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().
                getViewHandler().getActionURL(ctx,
                "/overview.xhtml?faces-redirect=true"));

        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }
        renderUnsavedChanges = false;
    }

    private boolean validate() {
        boolean ret = true;

        if (toolName.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            ret = false;
        }
        if (shortDescription.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("shortDescriptionError"), ""));
            ret = false;
        }
        if (description == null || description.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("descriptionError"), ""));
            ret = false;
        }

        return ret;
    }

    public String cancel() {

        reloadFromBuilder();
        renderUnsavedChanges = false;
        return "overview.xhtml?faces-redirect=true";
    }

    public void reloadFromBuilder() {
        renderUnsavedChanges = false;
        toolName = builder.getToolName();
        shortDescription = builder.getShortDescription();
        description = builder.getDescription();
        customContent = builder.getCustomContent();
        toolTipText = builder.getToolTipText();
        keywords = builder.getKeywords();
        validate();
        checkAndSetStatus();
    }

    private void checkAndSetStatus() {

        if (toolName.equals("") || shortDescription.equals("") || description.equals("")) {
            basicInfoStatus = TrafficLightEnum.RED.getPath();
        } else if (customContent.equals("") || toolTipText.equals("") || keywords.equals("")) {
            basicInfoStatus = TrafficLightEnum.YELLOW.getPath();
        } else {
            basicInfoStatus = TrafficLightEnum.GREEN.getPath();
        }

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
                setCustomContent(result);
            }
        };

        editor.setCallback(contentCaller);

        return "editorMini.xhtml?faces-redirect=true";
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        renderUnsavedChanges = true;
        this.customContent = customContent;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }

    public String getBasicInfoStatus() {
        return basicInfoStatus;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }
    
    @Override
     public void setDescription(String description) {
        renderUnsavedChanges = true;
        this.description = description;
    }  

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }

    @Override
    public String getName() {
        return toolName;
    }
    
    
}