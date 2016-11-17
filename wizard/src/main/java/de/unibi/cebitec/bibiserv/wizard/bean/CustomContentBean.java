package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.input.EditorMiniBean;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import javax.faces.context.FacesContext;

/**
 * Used to manage access to editorMini.xhtml
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class CustomContentBean {

    protected String customContent;
    protected String xhtml;
     protected String position;

    void CustomContentBean() {
        xhtml = "";
        customContent = "";
        position = "";
    }

    public String editCustomContent() {
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

    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        this.customContent = customContent;
    }
    
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public abstract String getName();
}