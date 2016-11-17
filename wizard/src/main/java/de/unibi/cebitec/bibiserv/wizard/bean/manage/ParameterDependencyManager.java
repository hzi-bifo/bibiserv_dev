
package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public class ParameterDependencyManager {

    /**
     * Contains all dependencies sorted in alphabetical order by name.
     */
    private SortedMap<String, Tdependency> dependencies;
    /**
     * Send messages to functionmanager on change events.
     */
    private FunctionManager functionmanager = null;

    public ParameterDependencyManager() {
        // init dependency map
        dependencies = new TreeMap<String, Tdependency>(String.CASE_INSENSITIVE_ORDER);

    }

    public void clearDependencies(){
        dependencies = new TreeMap<String, Tdependency>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new dependency to the list of available dependencies.
     * @param dependency dependency to add
     * @throws BeansException  on error
     */
    public void addDependency(Tdependency dependency) throws BeansException {

        if (!dependency.isSetName()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                dependency.getName().get(0).getValue());

        if (this.dependencies.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        this.dependencies.put(name, dependency);
    }

    /**
     * Edits the dependency with oldname as name.
     * @param newDependency new dependencyvalues
     * @throws BeansException on error
     */
    public void editDependency(String oldname, Tdependency newDependency) throws
            BeansException {

        if (!newDependency.isSetName()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                newDependency.getName().get(0).getValue());
        oldname = IDGenerator.createName(oldname);

        // cannot change name, because new one ist set by other object
        if (!oldname.equals(name) && dependencies.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (dependencies.containsKey(oldname)) {

            Tdependency oldDependency = dependencies.get(oldname);
            // erase old dependency and add new one
            dependencies.remove(oldname);
            dependencies.put(name, newDependency);

            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            functionmanager.changeParameterDependency(oldDependency, newDependency);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the dependency with name from the map.
     * @param name name of the dependency to delete
     * @throws BeansException on error
     */
    public void removeParameterDependencyByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (dependencies.containsKey(name)) {

            Tdependency dependency = dependencies.get(name);
            // erase dependency
            dependencies.remove(name);

            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            functionmanager.removeDependency(dependency);
        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the dependency with the given name.
     * @param name of the dependency to retrieve
     * @return dependency
     * @throws BeansException on error
     */
    public Tdependency getParameterDependencyByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (dependencies.containsKey(name)) {

            return dependencies.get(name);
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    public List<String> getAllNames(){

      List<String> ret = new ArrayList<String>();

      for (Map.Entry<String, Tdependency> entry: dependencies.entrySet()) {
           ret.add(entry.getValue().getName().get(0).getValue());
      }

      return ret;
    }

    public Collection<Tdependency> getValues(){
        return dependencies.values();
    }

    public boolean isEmpty(){
        return dependencies.isEmpty();
    }

     /**
     * Changes the namedefinitions in all dependencies from old to new.
     * For this the real name ist needed, not the ID!
     * @param oldname name to be replaced
     * @param newName the new name
     */
    void changeNameReference(String oldname, String newName) {
        for(Entry<String,Tdependency> entry: dependencies.entrySet()){
            Tdependency dependency=entry.getValue();

            String definition = dependency.getDependencyDefinition();
            definition = definition.replaceAll("<"+oldname+">", "<"+newName+">");
            dependency.setDependencyDefinition(definition);
        }
    }

    public SortedMap<String, Tdependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(SortedMap<String, Tdependency> dependencies) {
        this.dependencies = dependencies;
    }
    
}
