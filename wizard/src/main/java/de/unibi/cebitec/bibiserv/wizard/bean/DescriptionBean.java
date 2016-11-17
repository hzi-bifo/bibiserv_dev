package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.input.EditorBean;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import javax.faces.context.FacesContext;

/**
 * Used to manage access to editor.xhtml
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public abstract class DescriptionBean {

    protected String description;
    protected String xhtml;
    protected String position;

    void DescriptionBean() {
        xhtml = "";
        description = "";
        position = "";
    }

    public String editDescription() {
        FacesContext context = FacesContext.getCurrentInstance();
        EditorBean editor = (EditorBean) context.getApplication().evaluateExpressionGet(context, "#{editorBean}", EditorBean.class);

        editor.setReturnTo(xhtml);
        editor.setEditorContent(description);
        
        String name = getName();
        if(name.isEmpty()) {
            name = PropertyManager.getProperty("unnamed");
        }
        editor.setPosition(position+" - "+name);

        GeneralCallback<String> descriptionCaller = new GeneralCallback<String>() {

            @Override
            public void setResult(String result) {
                setDescription(result);
            }
        };

        editor.setCallback(descriptionCaller);

        return "editor.xhtml?faces-redirect=true";

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }  

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public abstract String getName();

}