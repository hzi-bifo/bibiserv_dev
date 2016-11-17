package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.util.bibtexparser.BibTexType;
import de.unibi.cebitec.bibiserv.wizard.bean.BibTexCleaner;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ReferenceBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ReferenceManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPerson;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

/**
 * This is the bean to manual.xhtml
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ReferenceBean {
    //journal autocomplete

    private static final List<String> JOURNALS = Arrays.asList(new String[]{
                "formerly Comp. Appl. Biosci.)", "BMC Bioinformatics",
                "Comp. Appl. Biosci.", "Communications of the ACM",
                "IEEE Computer Society Press", "J. Discrete Mathematics",
                "BMC Bioinformatics", "Information Processing Letters",
                "Journal of the ACM", "Journal of Algorithms", "J. Comp. Biol.",
                "Journal of Computer and Systems Sciences",
                "J. Functional Programming", "J. Mol. Biol.", "J. Mol. Evol.",
                "Journal of Symbolic Computation",
                "Springer Lecture Notes in Computer Science",
                "Nucleic Acids Res.", "Proc. Nat. Acad. Sci., U.S.A.",
                "Science of Computer Programming", "SIAM Journal on Computing",
                "Software---Practice and Experience",
                "Springer-Verlag, New York",
                "Proceedings of the Symposium on Theoretical Aspects of Computer Science",
                "Theoretical Computer Science", "Trends in Biochemical Science",
                "Trends Genet.", "IEEE Trans. on Computers",
                "IEEE Trans. on Inform. Theory",
                "University of Arizona, Tucson, Department of Computer Science",
                "Yale University, New Haven, Department of Computer Science"
            });
    private static final SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat(
            "yyyy");
    // loading
    private boolean renderLoadedFrom;
    private String loadedFrom;
    // values
    private BibTexType referenceType;
    private SelectItem[] referenceTypes;
    private List<Tupel<String, String>> authors;
    private boolean isAuthorsEdited;
    DataModel<Tupel<Integer, Tupel<String, String>>> authorsWithId;
    private String title, year, journal, publisher, school, doi, url, note;
    private String id;
    // Box
    private List<String> referenceNamesList;
    private boolean referencesEmpty;
    private ReferenceManager referenceManager;
    private boolean renderUnsavedChanges;

    public ReferenceBean() {
        FacesContext context = FacesContext.getCurrentInstance();
        referenceManager = (ReferenceManager) context.getApplication().
                evaluateExpressionGet(
                context, "#{referenceManager}", ReferenceManager.class);

        // init reference Types for dropdown
        referenceTypes = new SelectItem[BibTexType.values().length];
        int i = 0;
        for (BibTexType type : BibTexType.values()) {
            referenceTypes[i] = new SelectItem(type, type.getName());
            i++;
        }
        resetAll();
    }

    /**
     * Gets current data from manager bean.
     */
    private void getAvailableData() {
        referenceNamesList = referenceManager.getAllIds();
        referencesEmpty = referenceManager.isEmpty();
    }

    public void preRender() {
        getAvailableData();
    }

    /**
     * Reset all data to startup state.
     */
    private void resetAll() {
        getAvailableData();
        loadedFrom = "";
        renderLoadedFrom = false;

        authors = new ArrayList<Tupel<String, String>>();
        authors.add(new Tupel<String, String>());
        isAuthorsEdited = true;

        referenceType = (BibTexType) referenceTypes[0].getValue();
        title = "";
        year = "";
        journal = "";
        publisher = "";
        school = "";
        doi = "";
        url = "";
        note = "";
        
        renderUnsavedChanges = false;
    }

    private void loadReference(String id) {
        resetAll();
        BiBiPublication reference;
        try {
            reference = referenceManager.getReferenceById(id);
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openReferenceError"),
                    ""));
            return;
        }

        loadedFrom = reference.getPubkey();
        renderLoadedFrom = true;

        authors = new ArrayList<Tupel<String, String>>();
        for (BiBiPerson person : reference.getAuthors()) {
            authors.add(new Tupel<String, String>(person.getGivenname(), person.getFamily_name()));
        }
        isAuthorsEdited = true;

        title = reference.getTitle();
        year = FORMAT_YEAR.format(reference.getPublicationdate());
        doi = reference.getDoi();
        if (reference.getUrl() != null) {
            url = reference.getUrl().toString();
        }
        referenceType = reference.getType();

        switch (referenceType) {
            case article:
                journal = reference.getJournal();
                note = reference.getNote();
                break;
            case book:
                publisher = reference.getPublisher();
                note = reference.getNote();
                break;
            case inproceedings:
                note = reference.getNote();
                publisher = reference.getPublisher();
                break;
            case manual:
                note = reference.getNote();
            case mastersthesis:
            case phdthesis:
                school = reference.getSchool();
                note = reference.getNote();
                break;
            case proceedings:
                note = reference.getNote();
                publisher = reference.getPublisher();
                break;
            case techreport:
                school = reference.getInstitution();
                note = reference.getNote();
                break;
        }
        renderUnsavedChanges = false;
    }

    public void newReference() {
        resetAll();
    }

    public void editReference(String id) {
        loadReference(id);
    }

    public void removeReference(String id) {
        try {
            referenceManager.removeRefrenceById(id);
            if (id.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
        } catch (BeansException ex) {
        }
        getAvailableData();
    }

    private boolean testNames() {
        boolean ret = true;
        boolean uncompleteName = false;

        Iterator<Tupel<String, String>> authorIterator = authors.iterator();
        while (authorIterator.hasNext()) {
            Tupel<String, String> author = authorIterator.next();

            author.setFirst(BibTexCleaner.cleanName(author.getFirst()));
            author.setSecond(BibTexCleaner.cleanName(author.getSecond()));

            // remove all whitespaces
            String firstname = author.getFirst().replaceAll("(\\s)", "");
            String lastname = author.getSecond().replaceAll("(\\s)", "");
            if (firstname.isEmpty() && lastname.isEmpty()) {
                authorIterator.remove();
            } else if (firstname.isEmpty() || lastname.isEmpty()) {
                uncompleteName = true;
            }
        }
        if (authors.isEmpty()) {
            authors.add(new Tupel<String, String>());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("referenceNoNamesError"), ""));
            ret = false;
        }
        if (uncompleteName) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("referenceUncompleteNamesError"),
                    ""));
            ret = false;
        }
        return ret;
    }

    /**
     * Cleans all entrys of unwanted chars.
     */
    private void cleanAll() {
        title = BibTexCleaner.cleanEntry(title);
        year = BibTexCleaner.cleanEntry(year);
        journal = BibTexCleaner.cleanEntry(journal);
        publisher = BibTexCleaner.cleanEntry(publisher);
        school = BibTexCleaner.cleanEntry(school);
        doi = BibTexCleaner.cleanEntry(doi);
        url = BibTexCleaner.cleanEntry(url);
        note = BibTexCleaner.cleanEntry(note);
    }

    private boolean validateAll() {
        boolean ret = true;
        // clean string of unwantecd chars
        cleanAll();

        ret = testNames();

        if (year.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("referenceNoYearError"), ""));
            ret = false;
        } else {
            try {
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(year), 1, 1);
            } catch (NumberFormatException ex) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "referenceIncorrectYearError"), ""));
                ret = false;
            }
        }

        switch (referenceType) {
            case article:
                if (title.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("referenceNoTitleError"),
                            ""));
                    ret = false;
                }
                if (journal.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "referenceNoJournalError"), ""));
                    ret = false;
                }
                break;
            case book:
                if (title.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("referenceNoTitleError"),
                            ""));
                    ret = false;
                }
                if (publisher.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "referenceNoPublisherError"), ""));
                    ret = false;
                }
            case inproceedings:
                if (title.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("referenceNoTitleError"),
                            ""));
                    ret = false;
                }
                break;
            case manual:
                if (title.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("referenceNoTitleError"),
                            ""));
                    ret = false;
                }
                break;
            case mastersthesis:
            case phdthesis:
                if (title.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("referenceNoTitleError"),
                            ""));
                    ret = false;
                }
                if (school.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("referenceNoSchoolError"),
                            ""));
                    ret = false;
                }
                break;
            case proceedings:
                if (title.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("referenceNoTitleError"),
                            ""));
                    ret = false;
                }
                break;
            case techreport:
                if (title.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("referenceNoTitleError"),
                            ""));
                    ret = false;
                }
                if (school.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "referenceNoInstitutionError"), ""));
                    ret = false;
                }
                break;
        }

        if (!url.isEmpty()) {
            try {
                new URI(url);
            } catch (URISyntaxException ex) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "referenceUnvalidUrlError"), ""));
                ret = false;
            }
        }

        return ret;
    }

    /**
     * Saves file if it validates
     *
     * @return validation status
     */
    private boolean saveAll() {
        if (validateAll()) {

            BiBiPublication newreference = ReferenceBuilder.createReference(
                    referenceType, authors, title, year, journal, school, doi,
                    url, publisher, note);

            id = newreference.getPubkey();

            if (loadedFrom.isEmpty()) {
                try {
                    referenceManager.addReference(newreference);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "referenceAlreadyExistsError"),
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
                    referenceManager.editView(loadedFrom, newreference);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "referenceAlreadyExistsError"),
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
                    getViewHandler().getActionURL(ctx,
                    "/referenceSelection.xhtml"));
            try {
                extContext.redirect(url);
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public void save() {
        if (saveAll()) {
            loadedFrom = id;
            renderLoadedFrom = true;
        }
    }

    public String returnToPrev() {
        return "referenceSelection.xhtml?faces-redirect=true";
    }

    public String cancel() {
        renderUnsavedChanges = false;
        return returnToPrev();
    }

    public void addAuthor(int index) {
        authors.add(index + 1, new Tupel<String, String>("", ""));
        isAuthorsEdited = true;
        renderUnsavedChanges=true;
    }

    public void removeAuthor(int index) {
        authors.remove(index);
        isAuthorsEdited = true;
        renderUnsavedChanges=true;
    }

    public DataModel<Tupel<Integer, Tupel<String, String>>> getAuthorsWithId() {
        if (isAuthorsEdited) {
            List<Tupel<Integer, Tupel<String, String>>> ret =
                    new ArrayList<Tupel<Integer, Tupel<String, String>>>();

            int i = 0;
            for (Tupel<String, String> str : authors) {
                ret.add(new Tupel(i, str));
                i++;
            }

            authorsWithId =
                    new ListDataModel<Tupel<Integer, Tupel<String, String>>>(ret);
        }
        isAuthorsEdited = false;

        return authorsWithId;
    }

    /**
     * Method called by auto-complete for journals. Filters all Methods
     * containing query.
     *
     * @param query string to be contained
     * @return list of all possible strings
     */
    public List<String> journalAutocomplete(String query) {
        List<String> results = new ArrayList<String>(JOURNALS);

        Iterator<String> resultIterator = results.iterator();
        while (resultIterator.hasNext()) {
            if (!resultIterator.next().contains(query)) {
                resultIterator.remove();
            }
        }

        return results;
    }

    public boolean isShowRemove() {
        return authors.size() > 1;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        if (journal == null) {
            this.journal = "";
        } else {
            this.journal = journal;
        }
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public BibTexType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(BibTexType referenceType) {
        this.referenceType = referenceType;
    }

    public boolean isIsAuthorsEdited() {
        return isAuthorsEdited;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public SelectItem[] getReferenceTypes() {
        return referenceTypes;
    }

    public boolean isReferencesEmpty() {
        return referencesEmpty;
    }

    public List<String> getReferenceNamesList() {
        return referenceNamesList;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }
    
    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }

}
