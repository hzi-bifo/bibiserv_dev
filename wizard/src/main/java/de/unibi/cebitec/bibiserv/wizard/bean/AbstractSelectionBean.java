package de.unibi.cebitec.bibiserv.wizard.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * This class is a general template for SelectionBean-classes.
 *
 * Please note! Until now this is conceptual work and not yet widely used.
 *
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public abstract class AbstractSelectionBean<X> {

    /**
     * ManagerBean for instances of the respective type.
     */
    private ManagerInterface<X> managerBean;
    /**
     * Instance creation bean for instances of the respective type.
     */
    private InstanceBeanInterface instanceBean;
    /**
     * Current list of keys the user has selected.
     */
    private List<String> selectedStrings;
    /**
     * Last saved state of user-selected keys.
     */
    private List<String> savedSelectedStrings;
    /**
     * Full list of all instances.
     */
    private List<String> instanceNamesList;
    /**
     * List of additional information objects for this beans instances.
     */
    private List<X> instanceObjectList;
    /**
     * DataModel for communication purposes with frontend. This model is shown
     * to the user to select items.
     */
    private DataModel<Tupel<Integer, String>> tableModel;
    /**
     * Tells if the DataModel needs to be calculated anew.
     */
    private boolean dataModelEdited;
    
    protected boolean renderUnsavedChanges;
    

    /**
     * This constructor just initializes all variables. Manager and instance
     * Bean
     * is NOT yet set. This has to be done immediatly after instanciation!
     */
    public AbstractSelectionBean() {

        // init the selected strings with one element
        selectedStrings = new ArrayList<String>();
        selectedStrings.add("");

        savedSelectedStrings = new ArrayList<String>();

        dataModelEdited = true;
        renderUnsavedChanges = false;
    }

    private void reloadLists() {
        if (managerBean != null) {
            instanceNamesList = managerBean.getAllNames();
            instanceObjectList = managerBean.getAllInfoObjects();
        }
    }

    /**
     * @return returns true if there are no instances yet.
     */
    public boolean noInstances() {
        return managerBean.isEmpty();
    }
    
    /**
     * @return true if there are no saved selected strings.
     */
    public boolean noSavedSelectedStrings(){
        return savedSelectedStrings.isEmpty();
    }

    /**
     * Retuns a list of Tupels containing all selected strings
     * with position in list.
     *
     * @return as above
     */
    public DataModel<Tupel<Integer, String>> getSelectedStringsWithId() {

        if (dataModelEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedStrings) {
                ret.add(new Tupel(i, str));
                i++;
            }

            tableModel = new ListDataModel<Tupel<Integer, String>>(ret);
        }
        dataModelEdited = false;
        return tableModel;
    }

    /**
     * Returns all saved selected strings.
     *
     * @return all saved selected strings
     */
    public List<String> getSavedSelectedStrings() {
        ArrayList<String> returnList = new ArrayList<String>();
        returnList.addAll(savedSelectedStrings);
        return returnList;
    }
    
    public List<String> getSavedSelectedStringsReference() {
        return savedSelectedStrings;
    }

    /**
     * sets the saved seleced strings.
     *
     * @param newSelectedStrings new list of saved selected strings.
     */
    public void addAllSavedSelectedStrings(List<String> newSelectedStrings) {
        savedSelectedStrings.clear();
        savedSelectedStrings.addAll(newSelectedStrings);
    }

    /**
     * Returns whether the remove symbol is shown behind dropdown.
     *
     * @return true: draw X; false: don't draw
     */
    public boolean isShowRemove() {
        return selectedStrings.size() > 1;
    }

    /**
     * This method is called by the frontend if a dropdown menu value has
     * changed.
     *
     * @param e the ValueChangeEvent fired by the frontend.
     */
    public void dropDownValueChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = tableModel.getRowData();

        selectedStrings.set(currentTupel.getFirst(), value);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    /**
     * This method removes one dropdown menu .
     *
     * @param index position of the dropdown menu.
     */
    public void removeDropdown(int index) {
        selectedStrings.remove(index);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    /**
     * This method adds one dropdown menu.
     *
     * @param index position of the dropdown menu.
     */
    public void addDropdown(int index) {
        selectedStrings.add(index + 1, "");
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    /**
     * This method saves the selected strings and should be used in all
     * implementations of the save() and saveReturn() method.
     */
    public void saveSelectedStrings() {

        savedSelectedStrings.clear();
        Set<String> savedSet = new HashSet<String>();

        for (String str : selectedStrings) {
            if (!str.isEmpty()) {
                if(!savedSet.contains(str)) {
                    savedSelectedStrings.add(str);
                }
                savedSet.add(str);
            }
        }
        
        selectedStrings.clear();
        selectedStrings.addAll(savedSelectedStrings);
        if (selectedStrings.isEmpty()) {
            selectedStrings.add("");
        }
        
        dataModelEdited = true;
        renderUnsavedChanges = false;
    }

    /**
     * This method clears the selected Strings.
     */
    public void clearSelectedStrings() {

        selectedStrings.clear();
        selectedStrings.addAll(savedSelectedStrings);
        if (selectedStrings.isEmpty()) {
            selectedStrings.add("");
        }
        dataModelEdited = true;
    }

    /**
     * Orders the respective InstanceBeanInterface to create a new instance of
     * this type.
     *
     * @return URL of the InstanceBean for this type of object.
     */
    public String newInstance() {
        instanceBean.newInstance();
        return instanceBean.getURL();
    }

    /**
     * Orders the respective InstanceBeanInterface to edit an existing instance
     * of this type.
     *
     * @param name name of the object that shall be edited.
     * @return URL of the InstanceBean for this type of object.
     */
    public String editInstance(String name) {
        instanceBean.editInstance(name);
        return instanceBean.getURL();
    }

    /**
     * Orders the respective InstanceBeanInterface to remove an existing
     * instance of this type.
     * 
     * @param name name of the object that shall be deleted.
     * @return URL of the InstanceBean for this type of object.
     */
    public void removeInstance(String name) {
        instanceBean.removeInstance(name);
        reloadLists();
    }

    /**
     * This method is called by the frontend if the user wants to save her/his
     * current working state.
     */
    public abstract void save();

       /**
     * This method is called by the frontend if the user wants to save her/his
     * current working state and wants to return to the parent page.
     */
    public abstract void saveReturn();

       /**
     * This method is called by the frontend if the user wants to return to the
     * parent page WITHOUT saving her/his current working state.
     */
    public String cancel() {
        clearSelectedStrings();
        renderUnsavedChanges = false;
        return returnToPrev();
    }

     /**
      * 
      * @return the parent pages URL. 
      */
    public String returnToPrev() {
        renderUnsavedChanges = false;
        return "overview.xhtml?faces-redirect=true";
    }

    /**
     * Prepares for rendering of this SelectionBean.
     */
    public void preRender() {
        reloadLists();
    }

    /**
     * Reloads the contents of this page and checks if the current selection is
     * still valid. If not, the selection is corrected.
     */
    public void reload() {
        reloadLists();
        List<String> newSaved = new ArrayList<String>();
        for (String str : getSavedSelectedStrings()) {
            if (instanceNamesList.contains(str)) {
                newSaved.add(str);
            }
        }
        addAllSavedSelectedStrings(newSaved);
    }

    /**
     * Changes the name of a selected objects in all relevant lists.
     * 
     * @param oldname the objects old name.
     * @param newname the objects new name.
     */
    public void changeInstanceName(String oldname, String newname) {
        // change name in saved
        ListIterator<String> savedIterator = savedSelectedStrings.listIterator();
        while (savedIterator.hasNext()) {
            String str = savedIterator.next();
            if (str.equals(oldname)) {
                savedIterator.remove();
                savedIterator.add(newname);
            }
        }

        // change name in selected
        ListIterator<String> selectedIterator = selectedStrings.listIterator();
        while (selectedIterator.hasNext()) {
            String str = selectedIterator.next();
            if (str.equals(oldname)) {
                selectedIterator.remove();
                selectedIterator.add(newname);
            }
        }
        dataModelEdited = true;
    }

    /**
     * 
     * @return the list of names of objects the user can select if she/he wishes to.
     */
    public List<String> getInstanceNamesList() {
        ArrayList<String> returnList = new ArrayList<String>();
        returnList.addAll(instanceNamesList);
        return returnList;
    }

    /**
     * 
     * @param instanceNamesList the list of names of objects the user can select
     * if she/he wishes to.
     */
    public void setInstanceNamesList(List<String> instanceNamesList) {
        this.instanceNamesList.clear();
        this.instanceNamesList.addAll(instanceNamesList);
    }

    /**
     * 
     * @return the list of objects the user has already created with possible
     * additional information.
     */
    public List<X> getInstanceObjectList() {
        ArrayList<X> returnList = new ArrayList<X>();
        returnList.addAll(instanceObjectList);
        return returnList;
    }

    /**
     * 
     * @param instanceObjectList the list of objects the user has already created
     * with possible additional information.
     */
    public void setInstanceObjectList(List<X> instanceObjectList) {
        this.instanceObjectList.clear();
        this.instanceObjectList.addAll(instanceObjectList);
    }

    /**
     * This method has to be called immediatly after creating an instance of
     * this class!
     * 
     * @param managerBean ManagerBean for instances of the respective type.
     */
    public void setManagerBean(ManagerInterface<X> managerBean) {
        this.managerBean = managerBean;
    }

    /**
     * This method has to be called immediatly after creating an instance of
     * this class!
     * 
     * @param instanceBean Instance creation bean for instances of the respective type.
     */
    public void setInstanceBean(InstanceBeanInterface instanceBean) {
        this.instanceBean = instanceBean;
    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
}
