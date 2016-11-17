package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.input.ReferenceSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Manages all references (BibTex) that are currently in the wizard.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ReferenceManager {

    /**
     * Contains all references sorted in alphabetical order by name.
     */
    private SortedMap<String, BiBiPublication> references;
    //selection bean
    ReferenceSelectionBean referenceSelectionBean = null;

    public ReferenceManager() {
        // init reference map
        references = new TreeMap<String, BiBiPublication>(String.CASE_INSENSITIVE_ORDER);
    }

    /*
     * Clears the references map.
     */
    public void clearReferences() {
        references = new TreeMap<String, BiBiPublication>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new reference to the list of available references.
     * @param reference reference as BiBiPublication (see OntoAccess)
     * @return returns the id
     * @throws BeansException on error
     */
    public String addReference(BiBiPublication reference) throws BeansException {

        String id = reference.getPubkey();

        if (references.containsKey(id)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    id);
        }

        references.put(id, reference);
        
        return id;
    }

    /**
     * Edits the refence with oldid as id.
     * @param oldid id of the old reference
     * @param reference new reference as BiBiPublication (see OntoAccess)
     * @throws BeansException on error
     */
    public void editView(String oldid, BiBiPublication reference) throws
            BeansException {

        String id = reference.getPubkey();

        // cannot change name, because new one ist set by other object
        if (!oldid.equals(id) && references.containsKey(id)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    id);
        }

        if (references.containsKey(oldid)) {

            // erase old refrence and add new one
            references.remove(oldid);
            references.put(id, reference);

            if (referenceSelectionBean == null) {
                // get reference to referencebean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                referenceSelectionBean = (ReferenceSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{referenceSelectionBean}",
                        ReferenceSelectionBean.class);
            }
            referenceSelectionBean.changeReferenceName(oldid, id);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the reference with id from the map.
     * @param id id of the reference to delete
     * @throws BeansException on error
     */
    public void removeRefrenceById(String id) throws BeansException {

        if (references.containsKey(id)) {

            // erase refrence
            references.remove(id);

            if (referenceSelectionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                referenceSelectionBean = (ReferenceSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{referenceSelectionBean}",
                        ReferenceSelectionBean.class);
            }
            referenceSelectionBean.reload();
        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the reference with the given id.
     * @param id id of the refrence to retrieve
     * @return reference as BiBiPublication
     * @throws BeansException on error
     */
    public BiBiPublication getReferenceById(String id) throws BeansException {

        if (references.containsKey(id)) {
            return references.get(id);
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all id contained in map.
     * @return alphabetically sorted list of all id contained in map
     */
    public List<String> getAllIds() {

        return new ArrayList<String>(references.keySet());
    }

    public boolean isEmpty() {
        return references.isEmpty();
    }

    public SortedMap<String, BiBiPublication> getReferences() {
        return references;
    }

    public void setReferences(SortedMap<String, BiBiPublication> references) {
        this.references = references;
    }
    
}
