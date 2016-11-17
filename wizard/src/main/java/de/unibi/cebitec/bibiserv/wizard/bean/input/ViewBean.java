package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.CustomContentBean;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ViewBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ViewManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import de.unibi.techfak.bibiserv.cms.TviewType;
import java.io.IOException;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 * Contains all functions for view.xhtml
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ViewBean extends CustomContentBean {

    // general data
    private String loadedFrom;
    private boolean renderLoadedFrom;
    private String title;
    private TviewType viewType;
    private SelectItem[] viewTypes;
    // manager
    private ViewManager viewManager;
    private boolean viewsEmpty;
    private List<String> viewNamesList;
    
    private boolean renderUnsavedChanges;

    public ViewBean() {

        xhtml = "view.xhtml";

        FacesContext context = FacesContext.getCurrentInstance();
        viewManager = (ViewManager) context.getApplication().
                evaluateExpressionGet(
                context, "#{viewManager}", ViewManager.class);
        // init view Types for dropdown
        viewTypes = new SelectItem[TviewType.values().length];
        int i = 0;
        for (TviewType type : TviewType.values()) {
            viewTypes[i] = new SelectItem(type, type.value());
            i++;
        }
        
        position = PropertyManager.getProperty("views");
        
        resetAll();
    }

    private void getAvailableData() {
        viewNamesList = viewManager.getAllNames();
        viewsEmpty = viewManager.isEmpty();
    }

    private void resetAll() {
        getAvailableData();
        title = "";
        loadedFrom = "";
        renderLoadedFrom = false;
        viewType = (TviewType) viewTypes[0].getValue();
        customContent = "";
        
        renderUnsavedChanges = false;
    }

    public void preRender() {
        getAvailableData();
    }

    private void loadView(String name) {
        resetAll();
        TrunnableItemView view;
        try {
            view = viewManager.getViewByName(name);
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openViewError"),
                    ""));
            return;
        }


        renderLoadedFrom = true;
        if(!view.getTitle().isEmpty()){
            title = view.getTitle().get(0).getValue();
        }
        viewType = view.getType();
        loadedFrom = viewType.value();

        if (view.isSetCustomContent()) {
            customContent = (String) view.getCustomContent().get(0).getContent().
                    get(
                    0);
        }
        renderUnsavedChanges = false;
    }

    public void editView(String name) {
        loadView(name);
    }

    public void removeView(String name) {
        try {
            viewManager.removeViewByName(name);
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
        } catch (BeansException ex) {
        }
        getAvailableData();
    }

    private boolean validateAll() {
        boolean ret = true;

        return ret;
    }

    /**
     * Saves file if it validates
     * @return validation status
     */
    private boolean saveAll() {
        if (validateAll()) {

            TrunnableItemView newview = ViewBuilder.createView(title,
                    customContent, viewType, BasicBeanData.StandardLanguage);

            if (loadedFrom.isEmpty()) {
                try {
                    viewManager.addView(newview);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "viewAlreadyExistsError"),
                                ""));
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty("couldNotSave"), ""));
                    }
                    return false;
                }
            } else {
                try {
                    viewManager.editView(loadedFrom, newview);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "viewAlreadyExistsError"),
                                ""));
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty("couldNotSave"), ""));
                    }
                    return false;
                }
            }
        } else {
            return false;
        }

        getAvailableData();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                PropertyManager.getProperty("saveSuccesful"), ""));

        renderUnsavedChanges = false;
        
        return true;
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

    public void save() {
        if (saveAll()) {
            loadedFrom = viewType.value();
            renderLoadedFrom = true;
        }
    }

   public String getViewStatus() {
        if (viewManager.isEmpty()) {
            return TrafficLightEnum.YELLOW.getPath();
        }
        return TrafficLightEnum.GREEN.getPath();
    }

    public void newView() {
        resetAll();
    }

    public String returnToPrev() {
        return "overview.xhtml?faces-redirect=true";
    }

    public String cancel() {
        renderUnsavedChanges = false;
        return returnToPrev();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TviewType getViewType() {
        return viewType;
    }

    public void setViewType(TviewType viewType) {
        this.viewType = viewType;
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

    public List<String> getViewNamesList() {
        return viewNamesList;
    }

    public void setViewNamesList(List<String> viewNamesList) {
        this.viewNamesList = viewNamesList;
    }

    public SelectItem[] getViewTypes() {
        return viewTypes;
    }


    public boolean isViewsEmpty() {
        return viewsEmpty;
    }

    public void setViewsEmpty(boolean viewsEmpty) {
        this.viewsEmpty = viewsEmpty;
    }

    @Override
    public String getName() {
        return title;
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
    public void setCustomContent(String customContent) {
       renderUnsavedChanges = true; 
       this.customContent = customContent;
    }
    

}