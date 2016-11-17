package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.AbstractSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.AuthorManager;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class AuthorSelectionBean extends AbstractSelectionBean {

    public AuthorSelectionBean() {
        super();
        FacesContext context = FacesContext.getCurrentInstance();

        //set management bean
        AuthorManager authorManager = (AuthorManager) context.getApplication().
                evaluateExpressionGet(context, "#{authorManager}",
                AuthorManager.class);
        setManagerBean(authorManager);
        // set author creation bean
        AuthorBean authorBean = (AuthorBean) context.getApplication().
                evaluateExpressionGet(context, "#{authorBean}",
                AuthorBean.class);
        setInstanceBean(authorBean);
    }

    /**
     * Save all none-empty selected authors to saved authors.
     */
    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();

        saveSelectedStrings();

        if (noSavedSelectedStrings()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedAuthorError"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", true);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    PropertyManager.getProperty("saveSuccesful"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", false);
        }
        renderUnsavedChanges = false;
    }

    @Override
    public void saveReturn() {
        RequestContext context = RequestContext.getCurrentInstance();

        saveSelectedStrings();

        if (noSavedSelectedStrings()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedAuthorError"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", true);
            return;
        }
        context.addCallbackParam("show", false);
        context.addCallbackParam("returns", true);

        // redirect from javax context
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, "/overview.xhtml"));
        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }
        renderUnsavedChanges = false;
    }

    public String getAuthorStatus() {
        if (noSavedSelectedStrings()) {
            return TrafficLightEnum.RED.getPath();
        }
        return TrafficLightEnum.GREEN.getPath();
    }
}
