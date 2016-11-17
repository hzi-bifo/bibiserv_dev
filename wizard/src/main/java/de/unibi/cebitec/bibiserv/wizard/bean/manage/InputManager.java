package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.HandlingType;
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
public class InputManager {

    /**
     * Contains all inputs sorted in alphabetical order by name.
     */
    private SortedMap<String, TinputOutput> input;
    private FunctionManager functionmanager = null;

    public InputManager() {
        // init functions map
        input = new TreeMap<String, TinputOutput>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Reinitializes the input-map.
     */
    public void clearInputs() {
        input = new TreeMap<String, TinputOutput>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new input to the list of available functions.
     * @param input input to add
     * @throws BeansException  on error
     */
    public void addInput(TinputOutput input) throws BeansException {

        if (!input.isSetName()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                input.getName().get(0).getValue());

        if (this.input.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        this.input.put(name, input);
    }

    /**
     * Edits the input with oldname as name.
     * @param newInput new functionvalues
     * @throws BeansException on error
     */
    public void editInput(String oldname, TinputOutput newInput) throws
            BeansException {

        if (!newInput.isSetName()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                newInput.getName().get(0).getValue());
        oldname = IDGenerator.createName(oldname);


        // cannot change name, because new one ist set by other object
        if (!oldname.equals(name) && input.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (input.containsKey(oldname)) {

            TinputOutput oldinput = input.get(oldname);
            // erase old function and add new one
            input.remove(oldname);
            input.put(name, newInput);

            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            functionmanager.changeInput(oldinput, newInput);

            if (((oldinput.getHandling().equals(HandlingType.file.getValue())
                    || oldinput.getHandling().equals(HandlingType.stdin.getValue()))
                    && newInput.getHandling().equals(HandlingType.argument.getValue()))
                    || ((newInput.getHandling().equals(HandlingType.file.getValue())
                    || newInput.getHandling().equals(HandlingType.stdin.getValue()))
                    && oldinput.getHandling().equals(HandlingType.argument.getValue()))) {

                functionmanager.resetExamplesForInput(newInput.getId(),
                        newInput.getName().get(0).getValue());
            } else if((!oldinput.getHandling().equals(HandlingType.none.getValue()) && 
                    newInput.getHandling().equals(HandlingType.none.getValue()))) {
                functionmanager.rebuildExamples(newInput.getId(),newInput.getName().get(0).getValue());
            }
        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the input with name from the map.
     * @param name name of the function to delete
     * @throws BeansException on error
     */
    public void removeInputByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (input.containsKey(name)) {
            TinputOutput remove = input.get(name);
            // erase function
            input.remove(name);
            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            functionmanager.removeInput(remove);
        } else {
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the input with the given name.
     * @param name of the function to retrieve
     * @return input
     * @throws BeansException on error
     */
    public TinputOutput getInputByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (input.containsKey(name)) {

            return input.get(name);
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    public List<String> getAllNames() {

        List<String> ret = new ArrayList<String>();

        for (Map.Entry<String, TinputOutput> entry : input.entrySet()) {
            ret.add(entry.getValue().getName().get(0).getValue());
        }

        return ret;
    }

    /**
     * Returns the name for the given id or throws exception if not found.
     * @param id id to search for
     * @return name of this input
     * @throws BeansException when id can't be found
     */
    public String getNameforId(String id) throws BeansException {
        if (!id.startsWith(InputOutputBuilder.getID_BASE_TYPE_INPUT())) {
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
        String key = IDGenerator.stripType(id);

        if (input.containsKey(key)) {

            return input.get(key).getName().get(0).getValue();
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    public boolean isEmpty() {
        return input.isEmpty();
    }

    public SortedMap<String, TinputOutput> getInput() {
        return input;
    }

    public void setInput(SortedMap<String, TinputOutput> input) {
        this.input = input;
    }

}
