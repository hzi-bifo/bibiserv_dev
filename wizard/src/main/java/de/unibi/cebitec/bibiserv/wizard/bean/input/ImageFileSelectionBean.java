package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.AbstractSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.ImageFile;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ImageFileManager;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 * This class is the backend infrastructure for managing image file selection.
 *
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ImageFileSelectionBean extends AbstractSelectionBean<ImageFile> {

    public ImageFileSelectionBean() {
        super();
        FacesContext context = FacesContext.getCurrentInstance();

        // set image file manager as manager bean.
        ImageFileManager imageFileManager = (ImageFileManager) context.getApplication().
                evaluateExpressionGet(context, "#{imageFileManager}",
                ImageFileManager.class);
        setManagerBean(imageFileManager);
        // set image file bean as instance creation bean.
        ImageFileBean imageFileBean = (ImageFileBean) context.getApplication().
                evaluateExpressionGet(context, "#{imageFileBean}",
                ImageFileBean.class);
        setInstanceBean(imageFileBean);

    }

    /**
     * Save all none-empty selected authors to saved authors.
     */
    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();

        saveSelectedStrings();

        if (getSavedSelectedStrings().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedImageFileError"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", true);
        }
    }

    @Override
    public void saveReturn() {
        RequestContext context = RequestContext.getCurrentInstance();

        saveSelectedStrings();

        if (getSavedSelectedStrings().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedImageFileError"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", true);
        }
        //context will be called in the FileSelectionBean.
    }
}
