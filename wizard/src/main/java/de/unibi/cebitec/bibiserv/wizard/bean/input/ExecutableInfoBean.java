package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ExecutableInfoBuilder;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.TexecutableTypes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * This bean manages the communication with the executableInfo.xhtml-page.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ExecutableInfoBean {

    
    private String path = "";
    private String image = "";
    private String type = "docker";
    private String callingInfo = "";
    private String version = "";
    private String executableInfoStatus = TrafficLightEnum.RED.getPath();
    private ExecutableInfoBuilder builder;
    
   private boolean renderUnsavedChanges;

    public ExecutableInfoBean() {

        FacesContext context = FacesContext.getCurrentInstance();
        builder = (ExecutableInfoBuilder) context.getApplication().
                evaluateExpressionGet(context, "#{executableInfoBuilder}",
                ExecutableInfoBuilder.class);
        renderUnsavedChanges = false;
    }

    public void save() {

        if (validate()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    PropertyManager.getProperty("saveSuccesful"), ""));
            executableInfoStatus = TrafficLightEnum.GREEN.getPath();
        } else {
            return;
        }

        builder.createTexecutable(type,path,callingInfo, image, version);
        renderUnsavedChanges = false;
    }

    public void saveReturn() {

        if (!validate()) {
            return;
        }

        builder.createTexecutable(type,path,callingInfo, image, version);

        executableInfoStatus = TrafficLightEnum.GREEN.getPath();

        //Redirect using javax-context.

        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().
                getViewHandler().getActionURL(ctx,
                "/overview.xhtml"));

        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }
        
        renderUnsavedChanges = false;
    }

    private boolean validate() {
        return validate(true);
    }
    
    private boolean validate(boolean addmessage) {
        boolean ret = true;
        
        if ((!type.equals("docker")) || (!type.equals("binary"))) {
            if (addmessage) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("execTypeError"), ""));
            }
                
        }

        if (callingInfo.equals("")) {
            if (addmessage) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("callingInfoError"), ""));
            }
            ret = false;
        }
        if (version.equals("")) {
            if (addmessage) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("versionError"), ""));
            }
            ret = false;
        }

        return ret;
    }

    public String cancel() {

        reloadFromBuilder(false);
        renderUnsavedChanges = false;
        
        return "overview.xhtml";
    }

    public void reloadFromBuilder(boolean addmessage) {
        renderUnsavedChanges = false;
        callingInfo = builder.getCallingInfo();
        version = builder.getVersion();
        if (!validate(addmessage)) {
            executableInfoStatus = TrafficLightEnum.RED.getPath();
        } else {
            executableInfoStatus = TrafficLightEnum.GREEN.getPath();
        }
    }

    public String getExecutableInfoStatus() {
        return executableInfoStatus;
    }

    public String getCallingInfo() {
        return callingInfo;
    }

    public void setCallingInfo(String callingInfo) {
        this.callingInfo = callingInfo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }
    
    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }   
}
