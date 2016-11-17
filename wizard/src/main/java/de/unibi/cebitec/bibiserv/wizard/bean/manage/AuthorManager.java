package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.ManagerInterface;
import de.unibi.cebitec.bibiserv.wizard.bean.input.AuthorSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Tperson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * This class manages storage of authors.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class AuthorManager implements ManagerInterface {

    /**
     * Contains all authors sorted in alphabetical order by name.
     */
    private SortedMap<String, Tperson> authors;
    private AuthorSelectionBean authorSelectionBean = null;

    public AuthorManager() {
        super();
        // init authors map
        authors = new TreeMap<String, Tperson>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * clears the authors list.
     */
    public void clearAuthors() {
        authors = new TreeMap<String, Tperson>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new Author to the list of available authors.
     * @param person author to add
     * @return name of the added author as returned by getAll
     * @throws BeansException  on error
     */
    public String addAuthor(Tperson person) throws BeansException {

        if (!person.isSetFirstname() || !person.isSetLastname()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = person.getFirstname() + " " + person.getLastname();
        name = IDGenerator.createName(name);

        if (authors.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        
        authors.put(name, person);
        
        return person.getFirstname() + " " + person.getLastname();
    }

    /**
     * Edits the author with oldname as name.
     * @param oldname name of the author
     * @param person new person to add
     * @throws BeansException on error
     */
    public void editAuthor(String oldname, Tperson person) throws
            BeansException {

        if (!person.isSetFirstname() || !person.isSetLastname()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = person.getFirstname() + " " + person.getLastname();
        String nameid = IDGenerator.createName(name);
        String oldnameid = IDGenerator.createName(oldname);

        // cannot change name, because new one ist set by other object
        if (!oldnameid.equals(nameid) && authors.containsKey(nameid)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (authors.containsKey(oldnameid)) {

            // erase old function and add new one
            authors.remove(oldnameid);
            authors.put(nameid, person);
            
             if (authorSelectionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                authorSelectionBean = (AuthorSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{authorSelectionBean}",
                        AuthorSelectionBean.class);
            }
            authorSelectionBean.changeInstanceName(oldname, name);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the author with name from the map.
     * @param name name of the author to delete
     * @throws BeansException on error
     */
    public void removeAuthorByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (authors.containsKey(name)) {

            // erase function
            authors.remove(name);
        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the author with the given name.
     * @param name of the author to retrieve
     * @return author
     * @throws BeansException on error
     */
    public Tperson getAuthorByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (authors.containsKey(name)) {

            return authors.get(name);
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    @Override
    public List<String> getAllNames() {

        List<String> ret = new ArrayList<String>();

        for (Map.Entry<String, Tperson> entry : authors.entrySet()) {
            ret.add(entry.getValue().getFirstname() + " " + entry.getValue().getLastname());
        }

        return ret;
    }

    @Override
    public boolean isEmpty() {
        return authors.isEmpty();
    }


    /**
     * This method returns null because no info objects are needed.
     */
    @Override
    public List getAllInfoObjects() {
        return null;
    }

    public SortedMap<String, Tperson> getAuthors() {
        return authors;
    }

    public void setAuthors(SortedMap<String, Tperson> authors) {
        this.authors = authors;
    }
}
