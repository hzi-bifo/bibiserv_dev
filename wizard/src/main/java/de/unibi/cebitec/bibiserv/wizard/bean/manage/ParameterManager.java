package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Tparam;
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
public class ParameterManager {

    /**
     * Contains all parameters (enum and normal!) sorted in alphabetical order by name.
     */
    private SortedMap<String, Object> parameter;
    private ParameterDependencyManager dependencymanager = null;
    private FunctionManager functionmanager = null;
    private ParameterGroupManager groupmanager = null;

    public ParameterManager() {
        // init functions map
        parameter = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
    }

    public void clearParameters(){
        parameter = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new parameter to the list of available parameters.
     * @param parameterObject parameter to add (Tparam or TenumParam)
     * @throws BeansException on error
     */
    public void addParameter(Object parameterObject) throws BeansException {

        String baseName = getName(parameterObject);

        String name = IDGenerator.createName(baseName);

        if (this.parameter.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        this.parameter.put(name, parameterObject);
    }

    /**
     * Edits the parameter with oldname as name.
     * @param parameterObject new parameter
     * @throws BeansException on error
     */
    public void editParameter(String oldname, Object parameterObject) throws
            BeansException {

        String baseName = getName(parameterObject);

        String name = IDGenerator.createName(baseName);
        String oldnameid = IDGenerator.createName(oldname);

        // cannot change name, because new one ist set by other object
        if (!oldnameid.equals(name) && parameter.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (parameter.containsKey(oldnameid)) {

            Object oldparam = parameter.get(oldnameid);
            // erase old function and add new one
            parameter.remove(oldnameid);
            parameter.put(name, parameterObject);

            if (dependencymanager==null){
                FacesContext context = FacesContext.getCurrentInstance();
                dependencymanager =
                        (ParameterDependencyManager) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{parameterDependencyManager}",
                        ParameterDependencyManager.class);
            }
            if (groupmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                groupmanager =
                        (ParameterGroupManager) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{parameterGroupManager}",
                        ParameterGroupManager.class);
            }
            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            dependencymanager.changeNameReference(oldname, baseName);
            groupmanager.changeParameter(oldparam, parameterObject);
            functionmanager.changeParameter(oldparam, parameterObject);
            functionmanager.resetExamplesForParameter(parameterObject);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the parameter with name from the map.
     * @param name name of the parameter to delete
     * @throws BeansException on error
     */
    public void removeParameterByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (parameter.containsKey(name)) {

            Object param = parameter.get(name);
            // erase function
            parameter.remove(name);
            // notify deletion
            if (groupmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                groupmanager =
                        (ParameterGroupManager) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{parameterGroupManager}",
                        ParameterGroupManager.class);
            }
            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            groupmanager.removeParameterFromGroups(param);
            functionmanager.removeParameter(param);
        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the parameter with the given name as Object,
     * since it can be of type Tparam and TenumParam.
     * @param name of the parameter to retrieve
     * @return the parameter
     * @throws BeansException on error
     */
    public Object getParameterByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (parameter.containsKey(name)) {

            return parameter.get(name);
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    public List<String> getAllNames() {

        List<String> ret = new ArrayList<String>();

        for (Map.Entry<String, Object> entry : parameter.entrySet()) {

            String baseName;
            try {
                baseName = getName(entry.getValue());
                ret.add(baseName);
            } catch (BeansException ex) {
                // should not occur
            }
        }

        return ret;
    }

    public boolean isEmpty() {
        return parameter.isEmpty();
    }

    /**
     * Returns the name for the given id or throws exception if not found.
     * @param id id to search for
     * @return name of this parameter
     * @throws BeansException when id can't be found
     */
    public String getNameforId(String id) throws BeansException {
        if (!id.startsWith(ParameterBuilder.getID_BASE_TYPE())) {
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
        String key = IDGenerator.stripType(id);

        if (parameter.containsKey(key)) {

            return getName(parameter.get(key));
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns the id of the given parameter if it exists
     * @param name name of the parameter
     * @return the id
     * @throws BeansException on error
     */
    public String getIdForName(String name) throws BeansException {
        name = IDGenerator.createName(name);
        if (parameter.containsKey(name)) {
            Object param = parameter.get(name);
             if (param instanceof Tparam) {
                 return ((Tparam) param).getId();
             } else if (param instanceof TenumParam) {
                 return ((TenumParam) param).getId();
             }
        }
        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Retrieves the name of the given param
     * @param parameterOb parameter object
     * @return name
     * @throws BeansException   NoNameSpecified: Object has no name
     *                          InvalidType no valid parameter input
     */
    private String getName(Object parameterOb) throws BeansException {

        if (parameterOb instanceof Tparam) {
            Tparam param = (Tparam) parameterOb;
            if (!param.isSetName()) {
                throw new BeansException(BeansExceptionTypes.NoNameSpecified);
            }
            return param.getName().get(0).getValue();
        } else if (parameterOb instanceof TenumParam) {
            TenumParam param = (TenumParam) parameterOb;
            if (!param.isSetName()) {
                throw new BeansException(BeansExceptionTypes.NoNameSpecified);
            }
            return param.getName().get(0).getValue();
        }
        throw new BeansException(BeansExceptionTypes.InvalidType);
    }

    public SortedMap<String, Object> getParameter() {
        return parameter;
    }

    public void setParameter(SortedMap<String, Object> parameter) {
        this.parameter = parameter;
    }
    
}
