package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.input.ParameterGroupBean;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
public class ParameterGroupManager {

    /**
     * Contains all parameter groups sorted in alphabetical order by name.
     */
    private SortedMap<String, Tupel<String, TparamGroup>> paramGroup;
    private FunctionManager functionmanager = null;
    private ParameterGroupBean parameterGroupBean = null;

    public ParameterGroupManager() {
        // init parameter group map
        paramGroup = new TreeMap<String, Tupel<String, TparamGroup>>(
                String.CASE_INSENSITIVE_ORDER);

    }
    
    public void clearParameterGroups(){
        paramGroup = new TreeMap<String, Tupel<String, TparamGroup>>(
                String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new parameter group to the list of available parameter groups.
     * @param goup group input to add
     * @param id base of id for this group
     * @throws BeansException  on error
     */
    public void addParameterGroup(TparamGroup group, String id) throws BeansException {

        if (id.isEmpty()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String realid = IDGenerator.createName(id);

        if (this.paramGroup.containsKey(realid)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    id);
        }

        this.paramGroup.put(realid, new Tupel<String, TparamGroup>(id, group));
    }

    /**
     * Edits the parameter group with oldname as name.
     * @param group new parameter group values
     * @param oldid base of id of old group
     * @param id base of id for this group
     * @throws BeansException on error
     */
    public void editParameterGroup(String oldid, String id, TparamGroup group) throws
            BeansException {

        if (id.isEmpty()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String realid = IDGenerator.createName(id);
        String realoldid = IDGenerator.createName(oldid);

        // cannot change name, because new one ist set by other object
        if (!realoldid.equals(realid) && paramGroup.containsKey(realid)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    id);
        }

        if (paramGroup.containsKey(realoldid)) {

            TparamGroup oldgroup = paramGroup.get(realoldid).getSecond();
            // erase old function and add new one
            paramGroup.remove(realoldid);
            paramGroup.put(realid, new Tupel<String, TparamGroup>(id, group));

            changeGroupInOtherGroups(oldgroup, oldid, group, id);

            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            functionmanager.changeParameterGroup(oldgroup, oldid, group, id);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the output with name from the map.
     * @param name name of the parameter group to delete
     * @throws BeansException on error
     */
    public void removeParameterGroupByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (paramGroup.containsKey(name)) {
            // erase function
            TparamGroup group = paramGroup.get(name).getSecond();
            paramGroup.remove(name);

            removeParameterGroupFromOtherGroups(group);
            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            functionmanager.removeParameterGroup(group);

        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Loop throuh all parameter groups and remove remove from children.
     * Delete Group as well if empty.
     * @param remove group to be removed
     */
    private void removeParameterGroupFromOtherGroups(TparamGroup remove) {

        // iterate through map
        Iterator<Map.Entry<String, Tupel<String, TparamGroup>>> mapIterator = paramGroup.
                entrySet().iterator();
        while (mapIterator.hasNext()) {

            Map.Entry<String, Tupel<String,TparamGroup>> entry = mapIterator.next();
            TparamGroup group = entry.getValue().getSecond();

            // iterate through children
            Iterator childIterator =
                    group.getParamrefOrParamGroupref().iterator();
            while (childIterator.hasNext()) {

                Object ref = childIterator.next();

                Object ob = null;
                if (ref instanceof TparamGroup.ParamGroupref) {
                    ob = ((TparamGroup.ParamGroupref) ref).getRef();
                } else if (ref instanceof TparamGroup.Paramref) {
                    ob = ((TparamGroup.Paramref) ref).getRef();
                }

                if (ob instanceof TparamGroup) {
                    if (((TparamGroup) ob).equals(remove)) {
                        // remove savely
                        childIterator.remove();
                    }
                }
            }
            if (group.getParamrefOrParamGroupref().isEmpty()) {
                // remove Group if empty
                mapIterator.remove();
            }
        }
    }

    /**
     * Loop throuh all parameter groups and remove remove from children.
     * Delete Group as well if empty.
     * @param remove parameter to be removed
     */
    public void removeParameterFromGroups(Object remove) {

        // iterate through map
        Iterator<Map.Entry<String, Tupel<String, TparamGroup>>> mapIterator = paramGroup.
                entrySet().iterator();
        while (mapIterator.hasNext()) {

            Map.Entry<String, Tupel<String, TparamGroup>> entry = mapIterator.next();
            TparamGroup group = entry.getValue().getSecond();

            // iterate through children
            Iterator childIterator =
                    group.getParamrefOrParamGroupref().iterator();
            while (childIterator.hasNext()) {

                Object ref = childIterator.next();

                Object ob = null;
                if (ref instanceof TparamGroup.ParamGroupref) {
                    ob = ((TparamGroup.ParamGroupref) ref).getRef();
                } else if (ref instanceof TparamGroup.Paramref) {
                    ob = ((TparamGroup.Paramref) ref).getRef();
                }

                if (ob != null && ob.equals(remove)) {
                    // remove savely
                    childIterator.remove();
                }
            }

            if (group.getParamrefOrParamGroupref().isEmpty()) {
                // remove Group if empty
                mapIterator.remove();
            }
        }
        if (parameterGroupBean == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            parameterGroupBean =
                    (ParameterGroupBean) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupBean}",
                    ParameterGroupBean.class);
        }
        parameterGroupBean.reload();
    }

    /**
     * Loop through all parameterGroups and change object.
     * @param oldparam parameter to be replaced
     * @param parameterObject new parameter object
     */
     void changeParameter(Object oldparam, Object newparam) {

        String oldname = "";
        if (oldparam instanceof Tparam) {
            oldname = ((Tparam) oldparam).getName().get(0).getValue();
        } else if (oldparam instanceof TenumParam) {
            oldname = ((TenumParam) oldparam).getName().get(0).getValue();
        }
        // get the id and name of the newparam
        String newname = "";
        if (newparam instanceof Tparam) {
            newname = ((Tparam) newparam).getName().get(0).getValue();
        } else if (newparam instanceof TenumParam) {
            newname = ((TenumParam) newparam).getName().get(0).getValue();
        }

         // iterate through map
        Iterator<Map.Entry<String, Tupel<String, TparamGroup>>> mapIterator = paramGroup.
                entrySet().iterator();
        while (mapIterator.hasNext()) {

            Map.Entry<String, Tupel<String, TparamGroup>> entry = mapIterator.next();
            TparamGroup group = entry.getValue().getSecond();

            // iterate through children
            ListIterator childIterator =
                    group.getParamrefOrParamGroupref().listIterator();
            while (childIterator.hasNext()) {

                Object ref = childIterator.next();

                 if (ref instanceof TparamGroup.Paramref) {
                    Object ob = ((TparamGroup.Paramref) ref).getRef();
                    if(ob.equals(oldparam)){
                        childIterator.remove();
                        TparamGroup.Paramref newRef = new TparamGroup.Paramref();
                        newRef.setRef(newparam);
                        childIterator.add(newRef);
                    }
                }

            }
        }
        if (parameterGroupBean == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            parameterGroupBean =
                    (ParameterGroupBean) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupBean}",
                    ParameterGroupBean.class);
        }
        parameterGroupBean.paramNameChanged(oldname,newname);
    }

     /**
      * loops through all groups and changes erefernces of oldgroup to newgroup
      * @param oldgroup group to change
      * @param group group to change oldgroup into
      */
     private void changeGroupInOtherGroups(TparamGroup oldgroup, String oldid,
            TparamGroup newgroup, String id) {

         // iterate through map
        Iterator<Map.Entry<String, Tupel<String, TparamGroup>>> mapIterator = paramGroup.
                entrySet().iterator();
        while (mapIterator.hasNext()) {

            Map.Entry<String, Tupel<String, TparamGroup>> entry = mapIterator.next();
            TparamGroup group = entry.getValue().getSecond();

            // iterate through children
            ListIterator childIterator =
                    group.getParamrefOrParamGroupref().listIterator();
            while (childIterator.hasNext()) {

                Object ref = childIterator.next();

                Object ob = null;
                if (ref instanceof TparamGroup.ParamGroupref) {
                    ob = ((TparamGroup.ParamGroupref) ref).getRef();
                } else if (ref instanceof TparamGroup.Paramref) {
                    ob = ((TparamGroup.Paramref) ref).getRef();
                }

                if (ob instanceof TparamGroup) {
                    if (((TparamGroup) ob).equals(oldgroup)) {
                        // remove savely
                        childIterator.remove();
                        //add new one
                        TparamGroup.ParamGroupref newRef = new TparamGroup.ParamGroupref();
                        newRef.setRef(newgroup);
                        childIterator.add(newRef);
                    }
                }
            }
        }
        if (parameterGroupBean == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            parameterGroupBean =
                    (ParameterGroupBean) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupBean}",
                    ParameterGroupBean.class);
        }
        parameterGroupBean.paramGroupNameChanged(oldid
                ,id);
    }

    /**
     * Returns the parameter group with the given name.
     * @param name of the parameter group to retrieve
     * @return output
     * @throws BeansException on error
     */
    public TparamGroup getParameterGroupByName(String name) throws
            BeansException {

        name = IDGenerator.createName(name);

        if (paramGroup.containsKey(name)) {

            return paramGroup.get(name).getSecond();
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }
    
     /**
     * Returns the reference used for this parameter group with the given name.
     * @param id of the parameter group to retrieve
     * @return reference String
     * @throws BeansException on error
     */
    public String getParameterGroupRefByFullId(String id) throws
            BeansException {

        id = IDGenerator.stripType(id);

        if (paramGroup.containsKey(id)) {
            return paramGroup.get(id).getFirst();
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    public List<String> getAllNames() {

        List<String> ret = new ArrayList<String>();

        for (Map.Entry<String,Tupel<String, TparamGroup>> entry : paramGroup.entrySet()) {
            ret.add(entry.getValue().getFirst());
        }

        return ret;
    }

    public boolean isEmpty() {
        return paramGroup.isEmpty();
    }

    /**
     * Returns the names of all ParameterGroups that do not already contain
     * the given group somehow.
     * @param name of the group to be excluded
     * @return list of all names
     */
    public List<String> getAllNamesNotContaining(String name) {

        List<String> ret = new ArrayList<String>();

        name = IDGenerator.createName(name);
        
        TparamGroup compare = null;
        if (paramGroup.containsKey(name)) {
            compare = paramGroup.get(name).getSecond();
        }
        if (compare == null) {
            return getAllNames();
        }

        // loop through all groups
        for (Map.Entry<String, Tupel<String, TparamGroup>> entry : paramGroup.entrySet()) {
            if (!containsParamGroup(entry.getValue().getSecond(), compare)) {
                ret.add(entry.getValue().getFirst());
            }
        }

        return ret;
    }

    /**
     * Tests recursively if compare is in value. This is needed to avoid
     * circular references.
     * @param value TparamGroup to test if compare is in
     * @param compare value to be tested if inside
     * @return true: compre is inside value; false: compare is not inside value
     */
    private boolean containsParamGroup(TparamGroup value, TparamGroup compare) {

        if (value.equals(compare)) {
            return true;
        }

        for (Object ref : value.getParamrefOrParamGroupref()) {
            Object ob = null;
            if (ref instanceof TparamGroup.ParamGroupref) {
                ob = ((TparamGroup.ParamGroupref) ref).getRef();
            } else if (ref instanceof TparamGroup.Paramref) {
                ob = ((TparamGroup.Paramref) ref).getRef();
            }

            if (ob instanceof TparamGroup) {
                if (containsParamGroup((TparamGroup) ob, compare)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the list of all parameters contained in this group.
     * @param name name of the Group
     * @return list of all parameters in this group.
     * @throws BeansException on error
     */
    public Map<String, Integer> getAllParametersOfParamGroupByName(String name)
            throws
            BeansException {

        name = IDGenerator.createName(name);

        if (paramGroup.containsKey(name)) {

            Map<String, Integer> names = new HashMap<String, Integer>();
            getAllParameters(paramGroup.get(name).getSecond(), names);
            return names;
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Recursive function to get all parameters of group and put it into list.
     * Will circle endless when used with circular references. This has to be
     * prohobited in creation.
     * @param group group to search trhough
     * @param list list to add parameters to
     */
    private void getAllParameters(TparamGroup group, Map<String, Integer> list) {

        for (Object ref : group.getParamrefOrParamGroupref()) {

            Object ob = null;
            if (ref instanceof TparamGroup.ParamGroupref) {
                ob = ((TparamGroup.ParamGroupref) ref).getRef();
            } else if (ref instanceof TparamGroup.Paramref) {
                ob = ((TparamGroup.Paramref) ref).getRef();
            }

            if (ob instanceof TparamGroup) {
                getAllParameters((TparamGroup) ob, list);
            } else if (ob instanceof Tparam) {
                Tparam param = (Tparam) ob;
                String name = param.getName().get(0).getValue();
                if (list.containsKey(name)) {
                    int val = list.get(name);
                    val++;
                    list.put(name, val);
                } else {
                    list.put(name, 1);
                }
            } else if (ob instanceof TenumParam) {
                TenumParam param = (TenumParam) ob;
                String name = param.getName().get(0).getValue();
                if (list.containsKey(name)) {
                    int val = list.get(name);
                    val++;
                    list.put(name, val);
                } else {
                    list.put(name, 1);
                }
            }
        }
    }

    public SortedMap<String, Tupel<String, TparamGroup>> getParamGroup() {
        return paramGroup;
    }

    public void setParamGroup(SortedMap<String, Tupel<String, TparamGroup>> paramGroup) {
        this.paramGroup = paramGroup;
    }

}
