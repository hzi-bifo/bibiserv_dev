package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.input.ReferenceSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.input.WebstartSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Twebstart;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class WebstartManager {

    /**
     * Contains all webstarts sorted in alphabetical order by name.
     */
    private SortedMap<String, Twebstart> webstart;

    private WebstartSelectionBean webstartSelectionBean = null;

    public WebstartManager() {
        // init functions map
        webstart = new TreeMap<String, Twebstart>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Reinitializes the webstart-map.
     */
    public void clearWebstarts() {
        webstart = new TreeMap<String, Twebstart>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new webstarts to the list of available functions.
     *
     * @param webstart input to add
     * @throws BeansException on error
     */
    public void addWebstart(Twebstart webstart) throws BeansException {

        if (!webstart.isSetTitle()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                webstart.getTitle().get(0).getValue());

        if (this.webstart.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        this.webstart.put(name, webstart);
    }

    /**
     * Edits the webstarts with oldname as name.
     *
     * @param newWebstart new webstart
     * @throws BeansException on error
     */
    public void editWebstart(String oldname, Twebstart newWebstart) throws
            BeansException {

        if (!newWebstart.isSetTitle()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                newWebstart.getTitle().get(0).getValue());
        oldname = IDGenerator.createName(oldname);

        // cannot change name, because new one is set by other object
        if (!oldname.equals(name) && webstart.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (webstart.containsKey(oldname)) {

            // erase old function and add new one
            webstart.remove(oldname);
            webstart.put(name, newWebstart);
            
            
            if (webstartSelectionBean == null) {
                // get reference to referencebean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                webstartSelectionBean = (WebstartSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{webstartSelectionBean}",
                        WebstartSelectionBean.class);
            }
            webstartSelectionBean.changeWebstartName(oldname, name);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the output with name from the map.
     *
     * @param name name of the webstart to delete
     * @throws BeansException on error
     */
    public void removeWebstartByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (webstart.containsKey(name)) {

            // erase function
            webstart.remove(name);
            
             if (webstartSelectionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                webstartSelectionBean = (WebstartSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{webstartSelectionBean}",
                        WebstartSelectionBean.class);
            }
            webstartSelectionBean.reload();
            
        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the webstart with the given name.
     *
     * @param name of the webstart to retrieve
     * @return webstart
     * @throws BeansException on error
     */
    public Twebstart getWebstartByName(String name) throws BeansException {

        if (name != null) {
            name = IDGenerator.createName(name);

            if (webstart.containsKey(name)) {

                return webstart.get(name);
            }
        }
        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     *
     * @return alphabetically sorted list of all names contained in map
     */
    public List<String> getAllNames() {

        List<String> ret = new ArrayList<String>();

        for (Map.Entry<String, Twebstart> entry : webstart.entrySet()) {
            ret.add(entry.getValue().getTitle().get(0).getValue());
        }

        return ret;
    }

    public boolean isEmpty() {
        return webstart.isEmpty();
    }

    public SortedMap<String, Twebstart> getWebstarts() {
        return webstart;
    }

    public void setWebstarts(SortedMap<String, Twebstart> webstarts) {
        this.webstart = webstarts;
    }
    
}
