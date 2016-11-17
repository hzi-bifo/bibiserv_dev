package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
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
public class OutputManager {

    /**
     * Contains all outputs sorted in alphabetical order by name.
     */
    private SortedMap<String, TinputOutput> output;
    private FunctionManager functionmanager = null;

    public OutputManager() {
        // init functions map
        output = new TreeMap<String, TinputOutput>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Reinitializes the output-map.
     */
    public void clearOutputs() {
        output = new TreeMap<String, TinputOutput>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new outputs to the list of available functions.
     *
     * @param output input to add
     * @throws BeansException on error
     */
    public void addOutput(TinputOutput output) throws BeansException {

        if (!output.isSetName()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                output.getName().get(0).getValue());

        if (this.output.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        this.output.put(name, output);
    }

    /**
     * Edits the outputs with oldname as name.
     *
     * @param newOutput new functionvalues
     * @throws BeansException on error
     */
    public void editOutput(String oldname, TinputOutput newOutput) throws
            BeansException {

        if (!newOutput.isSetName()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                newOutput.getName().get(0).getValue());
        oldname = IDGenerator.createName(oldname);

        // cannot change name, because new one ist set by other object
        if (!oldname.equals(name) && output.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (output.containsKey(oldname)) {

            TinputOutput oldoutput = output.get(oldname);
            // erase old function and add new one
            output.remove(oldname);
            output.put(name, newOutput);

            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            functionmanager.changeOutput(oldoutput, newOutput);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the output with name from the map.
     *
     * @param name name of the output to delete
     * @throws BeansException on error
     */
    public void removeOutputByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (output.containsKey(name)) {

            TinputOutput remove = output.get(name);
            // erase function
            output.remove(name);

            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            // erase function
            functionmanager.removeOutput(remove);
        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the output with the given name.
     *
     * @param name of the output to retrieve
     * @return output
     * @throws BeansException on error
     */
    public TinputOutput getOutputByName(String name) throws BeansException {

        if (name != null) {
            name = IDGenerator.createName(name);

            if (output.containsKey(name)) {

                return output.get(name);
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

        for (Map.Entry<String, TinputOutput> entry : output.entrySet()) {
            ret.add(entry.getValue().getName().get(0).getValue());
        }

        return ret;
    }

    public boolean isEmpty() {
        return output.isEmpty();
    }

    public SortedMap<String, TinputOutput> getOutput() {
        return output;
    }

    public void setOutput(SortedMap<String, TinputOutput> output) {
        this.output = output;
    }
    
}
