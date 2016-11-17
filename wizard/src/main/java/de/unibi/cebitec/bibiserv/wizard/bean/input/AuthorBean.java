package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.InstanceBeanInterface;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.AuthorBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.AuthorManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.Tperson;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Contains all functions for author.xhtml
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class AuthorBean implements InstanceBeanInterface {

    /**
     * Manager of the list of all current authors.
     */
    AuthorManager manager;
    //current data
    private Boolean renderLoadedFrom;
    private String loadedfrom;
    private String name;
    private String firstName;
    private String email;
    private String organisation;
    private String phone;
    private String adress;
    private boolean renderUnsavedChanges;
    //box
    private boolean authorsEmpty;
    private List<String> authorNamesList;
    //e-mail validation:
    private final static Pattern emailPattern =
            Pattern.compile(
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");

    public AuthorBean() {
        // retrieve current bean of xmlViewer
        FacesContext context = FacesContext.getCurrentInstance();
        manager = (AuthorManager) context.getApplication().evaluateExpressionGet(
                context, "#{authorManager}", AuthorManager.class);
        resetAll();
    }

    public void resetAll() {
        renderLoadedFrom = false;
        loadedfrom = "";
        name = "";
        firstName = "";
        email = "";
        organisation = "";
        phone = "";
        adress = "";
        renderUnsavedChanges = false;
        reloadNameList();
    }

    public void preRender(){
        reloadNameList();
    }

    private void reloadNameList() {
        authorsEmpty = manager.isEmpty();
        authorNamesList = manager.getAllNames();
    }

    private void loadAuthor(String name) {
        resetAll();
        Tperson author;
        try {
            author = manager.getAuthorByName(name);
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openAuthorError"),
                    ""));
            return;
        }
        this.name = author.getLastname();
        firstName = author.getFirstname();
        email = author.getEmail();

        if (author.isSetOrganisation()) {
            organisation = author.getOrganisation();
        }
        if (author.isSetAdress()) {
            adress = author.getAdress();
        }
        if (author.isSetPhone()) {
            phone = author.getPhone();
        }

        loadedfrom = name;
        renderLoadedFrom = true;
    }

    public void newAuthor() {
        resetAll();
    }

    public void editAuthor(String name) {
        loadAuthor(name);
    }

    public void removeAuthor(String name) {
        try {
            manager.removeAuthorByName(name);
            if (name.equals(loadedfrom)) {
                loadedfrom = "";
                renderLoadedFrom = false;
            }
        } catch (BeansException ex) {
        }
        reloadNameList();
    }

    private boolean validate() {
        boolean ret = true;

        if (name.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("lastNameError"), ""));
            ret = false;
        }

        if (firstName.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("firstNameError"), ""));
            ret = false;
        }

        Matcher m = emailPattern.matcher(email);
        if (!m.matches()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("invalidEMailError"), ""));
            ret = false;
        }

        return ret;
    }

    private boolean saveAll() {
        if (validate()) {
            Tperson newPerson = AuthorBuilder.createAuthor(firstName, name,
                    organisation, email, phone, adress);
            if (loadedfrom.isEmpty()) {
                try {
                    manager.addAuthor(newPerson);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "authorAlreadyExistsError"),
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
                    manager.editAuthor(loadedfrom, newPerson);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "authorAlreadyExistsError"),
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

        reloadNameList();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                PropertyManager.getProperty("saveSuccesful"), ""));
        
        renderUnsavedChanges = false;
        return true;
    }

    public void save() {
        if (saveAll()) {
            // must be identical to AuthorManager addAuthor
            loadedfrom = firstName+" "+name;
            renderLoadedFrom = true;
        }
    }

    public void saveReturn() {
        if (saveAll()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ExternalContext extContext = ctx.getExternalContext();
            String url = extContext.encodeActionURL(ctx.getApplication().
                    getViewHandler().getActionURL(ctx, "/authorSelection.xhtml"));
            try {
                extContext.redirect(url);
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public String cancel() {

        return "authorSelection.xhtml?faces-redirect=true";
    }

    public boolean isAuthorsEmpty() {
        return authorsEmpty;
    }

    public String getAdress() {
        return adress;
    }

    public List<String> getAuthorNamesList() {
        return authorNamesList;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLoadedfrom() {
        return loadedfrom;
    }

    public String getName() {
        return name;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getPhone() {
        return phone;
    }

    public Boolean getRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public void newInstance() {
        newAuthor();
    }

    @Override
    public void editInstance(String name) {
        editAuthor(name);
    }

    @Override
    public void removeInstance(String name) {
        removeAuthor(name);
    }

    @Override
    public String getURL() {
        return "author.xhtml?faces-redirect=true";
    }
    
     public void unsavedChange(){
        renderUnsavedChanges = true;
    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
  
}
