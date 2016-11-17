package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Manages all views that are currently in the wizard.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ViewManager {

    /**
     * Contains all views.
     */
    private SortedMap<String, TrunnableItemView> views;

    public ViewManager() {
        // init views map
        views = new TreeMap<String, TrunnableItemView>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * resets the views list to an empty state.
     */
    public void resetAllViews() {
        // init views map
        views = new TreeMap<String, TrunnableItemView>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new view to the list of available views.
     * @param view view to add
     * @throws BeansException on error
     */
    public void addView(TrunnableItemView view) throws BeansException {

        String name = view.getType().value();
        name = IDGenerator.createName(name);

        if (views.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        views.put(name, view);
    }

    /**
     * Edits the view with oldname as name(title).
     * @param oldname name (title) of the view
     * @param view new view to add
     * @throws BeansException on error
     */
    public void editView(String oldname, TrunnableItemView view) throws
            BeansException {

        String name = view.getType().value();
        String nameid = IDGenerator.createName(name);
        String oldnameid = IDGenerator.createName(oldname);

        // cannot change name, because new one ist set by other object
        if (!oldnameid.equals(nameid) && views.containsKey(nameid)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (views.containsKey(oldnameid)) {

            // erase old function and add new one
            views.remove(oldnameid);
            views.put(nameid, view);

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
    public void removeViewByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (views.containsKey(name)) {

            // erase function
            views.remove(name);

        } else {
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the view with the given name (title).
     * @param name (tile) of the view to retrieve
     * @return author
     * @throws BeansException on error
     */
    public TrunnableItemView getViewByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (views.containsKey(name)) {

            return views.get(name);
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    public List<String> getAllNames() {

        List<String> ret = new ArrayList<String>();

        for (Map.Entry<String, TrunnableItemView> entry : views.entrySet()) {
            ret.add(entry.getValue().getType().value());
        }
        return ret;
    }

    public Collection<TrunnableItemView> getValues(){
        return views.values();
    }

    public boolean isEmpty() {
        return views.isEmpty();
    }

    public SortedMap<String, TrunnableItemView> getViews() {
        return views;
    }

    public void setViews(SortedMap<String, TrunnableItemView> views) {
        this.views = views;
    }
    
}
