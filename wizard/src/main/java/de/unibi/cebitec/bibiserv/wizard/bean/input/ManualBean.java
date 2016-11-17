package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.CustomContentBean;
import de.unibi.cebitec.bibiserv.wizard.bean.GeneralCallback;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ManualBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ManualManager;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.Tmanual;
import java.io.IOException;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * This is the bean to manual.xhtml
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ManualBean extends CustomContentBean {

    private ManualManager manualManager;
    private String introduction;
    
    private boolean renderUnsavedChanges;

    public ManualBean() {
        xhtml = "manual.xhtml";

        FacesContext context = FacesContext.getCurrentInstance();
        manualManager = (ManualManager) context.getApplication().
                evaluateExpressionGet(
                context, "#{manualManager}", ManualManager.class);
        position = PropertyManager.getProperty("manual");
        resetAll();
    }

    private void resetAll() {
        introduction = "";
        customContent = "";
        renderUnsavedChanges = false;
    }

    public String editIntroductionContent() {
        FacesContext context = FacesContext.getCurrentInstance();
        EditorMiniBean editor = (EditorMiniBean) context.getApplication().
                evaluateExpressionGet(context, "#{editorMiniBean}",
                EditorMiniBean.class);

        editor.setReturnTo(xhtml);
        editor.setEditorContent(introduction);
        editor.setPosition(position +" - "+PropertyManager.getProperty("introduction"));

        GeneralCallback<String> contentCaller = new GeneralCallback<String>() {

            @Override
            public void setResult(String result) {
                setIntroduction(result);
            }
        };

        editor.setCallback(contentCaller);

        return "editorMini.xhtml?faces-redirect=true";
    }

    public void setIntroduction(String introduction) {
        renderUnsavedChanges = true;
        this.introduction = introduction;
    }

    private void loadSaved() {
        renderUnsavedChanges = false;
        Tmanual saved = manualManager.getSavedManual();
        if (saved == null) {
            resetAll();
            return;
        }
        List<Tmanual.IntroductoryText> introductions = saved.getIntroductoryText();
        List<Tmanual.CustomContent> customContents = saved.getCustomContent();
        if (!introductions.isEmpty() && !introductions.get(0).getContent().isEmpty()) {
            introduction = (String) introductions.get(0).getContent().
                    get(0);
        } else {
            introduction = "";
        }
        if (!customContents.isEmpty() && !customContents.get(0).getContent().isEmpty()) {
            customContent = (String) saved.getCustomContent().get(0).getContent().
                    get(0);
        } else {
            customContent = "";
        }
    }

    private String returnToPrev() {
        return "overview.xhtml?faces-redirect=true";
    }

    public String cancel() {
        loadSaved();
        return returnToPrev();
    }

    private boolean validateAll() {
        boolean ret = true;

        if (introduction.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noIntroductionError"), ""));
            ret = false;
        }
        return ret;
    }

    private boolean saveAll() {
        if (!validateAll()) {
            return false;
        }
        Tmanual newmanual = ManualBuilder.createManual(introduction,
                customContent, BasicBeanData.StandardLanguage);
        manualManager.setSavedManual(newmanual);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                PropertyManager.getProperty("saveSuccesful"), ""));

        renderUnsavedChanges = false;
        return true;
    }

    public void save() {
        saveAll();
    }

    public void saveAndReturn() {
        if (saveAll()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ExternalContext extContext = ctx.getExternalContext();
            String url = extContext.encodeActionURL(ctx.getApplication().
                    getViewHandler().getActionURL(ctx, "/overview.xhtml"));
            try {
                extContext.redirect(url);
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public String getManualStatus() {
        if (introduction == null || introduction.isEmpty()) {
            return TrafficLightEnum.RED.getPath();
        }
        if (customContent == null || customContent.isEmpty()) {
            return TrafficLightEnum.YELLOW.getPath();
        }
        return TrafficLightEnum.GREEN.getPath();
    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }
    
    @Override
    public void setCustomContent(String customContent) {
        renderUnsavedChanges= true;
        this.customContent = customContent;
    }

    @Override
    public String getName() {
        return PropertyManager.getProperty("customContent");
    }
    
    
}
