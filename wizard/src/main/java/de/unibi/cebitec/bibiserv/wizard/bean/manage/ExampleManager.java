
package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.Example;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleStore;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Warning: This is not a bean like the other ones, because
 * Examples are dependend on functions.
 * This is instantiated by functionBean.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ExampleManager {

    /**
     * Contains all examples sorted in alphabetical order by name.
     */
    private SortedMap<String, Example> examples;
    private List<ExampleStore> emptyBaseStore;

    public ExampleManager() {
        // init example map
        examples = new TreeMap<String, Example>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new example to the list of available functions.
     * @param example example to add
     * @throws BeansException  on error
     */
    public void addExample(Example example) throws BeansException {


        String name = IDGenerator.createName(example.getName());

        if (this.examples.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        this.examples.put(name, example);
    }

    /**
     * Edits the example with oldname as name.
     * @param newExample new examplevalues
     * @throws BeansException on error
     */
    public void editExample(String oldname, Example example)
            throws BeansException {

        String name = IDGenerator.createName(example.getName());
        oldname = IDGenerator.createName(oldname);

        // cannot change name, because new one ist set by other object
        if (!oldname.equals(name) && examples.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (examples.containsKey(oldname)) {

            // erase old example and add new one
            examples.remove(oldname);
            examples.put(name, example);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the example with name from the map.
     * @param name name of the example to delete
     * @throws BeansException on error
     */
    public void removeExampleByName(String name) throws
            BeansException {

        name = IDGenerator.createName(name);

        if (examples.containsKey(name)) {

            // erase example
            examples.remove(name);

        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Returns the example with the given name.
     * @param name of the example to retrieve
     * @return example
     * @throws BeansException on error
     */
    public Example getExampleByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (examples.containsKey(name)) {

            return examples.get(name);
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    public List<String> getAllNames() {

        List<String> ret = new ArrayList<String>();

        for (Map.Entry<String, Example> entry : examples.entrySet()) {
            ret.add(entry.getValue().getName());
        }

        return ret;
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    public List<Tupel<String,Boolean>> getAllNameValidTupel(){
        List<Tupel<String,Boolean>> ret = new ArrayList<Tupel<String,Boolean>>();

        for (Map.Entry<String, Example> entry : examples.entrySet()) {
            ret.add(new Tupel(entry.getValue().getName(),entry.getValue().isValid() && entry.getValue().isDependencyValid()));
        }
        return ret;
    }

    /**
     * Returns if manager contains an unvalid example.
     * @return true: all exmaples are correct, false: one or more are incorrect
     */
    public boolean isValid(){
         for (Map.Entry<String, Example> entry : examples.entrySet()) {
            if(!entry.getValue().isValid()){
                return false;
            }
        }
        return true;
    }

        /**
     * Returns if manager contains an unvalid example.
     * @return true: all exmaples are correct, false: one or more are incorrect
     */
    public boolean isDependencyValid(){
         for (Map.Entry<String, Example> entry : examples.entrySet()) {
            if(!entry.getValue().isDependencyValid()){
                return false;
            }
        }
        return true;
    }

    public Collection<Example> getValues(){
        return examples.values();
    }

    public boolean isEmpty() {
        return examples.isEmpty();
    }

    public List<ExampleStore> getEmptyBaseStore() {
        return emptyBaseStore;
    }

    public void setEmptyBaseStore(List<ExampleStore> emptyBaseStore) {
        this.emptyBaseStore = emptyBaseStore;
    }

}
