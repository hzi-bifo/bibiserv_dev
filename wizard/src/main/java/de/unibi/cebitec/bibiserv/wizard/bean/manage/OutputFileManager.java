package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.ToutputFile;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
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
public class OutputFileManager {

    /**
     * Contains all additional output files sorted in alphabetical order by name.
     */
    private SortedMap<String,  Tupel<String, ToutputFile>> output;
    private FunctionManager functionmanager = null;

    public OutputFileManager() {
        // init functions map
        output = new TreeMap<String,  Tupel<String, ToutputFile>>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Reinitializes the output-map.
     */
    public void clearOutputs() {
        output = new TreeMap<String,  Tupel<String, ToutputFile>>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new output to the list of available output files.
     *
     * @param output output file to add
     * @param id name not meant for display, but for id
     * @throws BeansException on error
     */
    public void addOutput(ToutputFile output, String id) throws BeansException {

        if (id.isEmpty()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }
        
        String realid = IDGenerator.createName(id);


        if (this.output.containsKey(realid)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    id);
        }

        this.output.put(realid, new Tupel<String, ToutputFile>(id, output));
    }

    /**
     * Edits the output with oldname as name.
     *
     * @param newOutput new output file
     * @param oldid id of the output file to change
     * @param id new id of the output file
     * @throws BeansException on error
     */
    public void editOutput(String oldid, String id, ToutputFile newOutput) throws
            BeansException {

        if (id.isEmpty()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String realid = IDGenerator.createName(id);
        String realoldid = IDGenerator.createName(oldid);

        // cannot change name, because new one ist set by other object
        if (!realoldid.equals(realid) && output.containsKey(realid)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    id);
        }

        if (output.containsKey(realoldid)) {

            ToutputFile oldoutput = output.get(realoldid).getSecond();
            // erase old output and add new one
            output.remove(realoldid);
            output.put(realid, new Tupel<String, ToutputFile>(id, newOutput));

            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            functionmanager.changeOutputFile(oldoutput,oldid, newOutput,id);

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

            ToutputFile remove = output.get(name).getSecond();
            // erase output
            output.remove(name);

            if (functionmanager == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                functionmanager =
                        (FunctionManager) context.getApplication().
                        evaluateExpressionGet(context, "#{functionManager}",
                        FunctionManager.class);
            }
            // erase function
            functionmanager.removeOutputFile(remove);
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
    public ToutputFile getOutputByName(String name) throws BeansException {

        if (name != null) {
            name = IDGenerator.createName(name);

            if (output.containsKey(name)) {

                return output.get(name).getSecond();
            }
        }
        throw new BeansException(BeansExceptionTypes.NotFound);
    }
    
    public String getNameById(String id) throws BeansException{
        id = IDGenerator.stripType(id);
        if (output.containsKey(id)) {
                return output.get(id).getFirst();
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

        for (Map.Entry<String, Tupel<String, ToutputFile>> entry : output.entrySet()) {
            ret.add(entry.getValue().getFirst());
        }

        return ret;
    }

    public boolean isEmpty() {
        return output.isEmpty();
    }

    public SortedMap<String, Tupel<String, ToutputFile>> getOutput() {
        return output;
    }

    public void setOutput(SortedMap<String, Tupel<String, ToutputFile>> output) {
        this.output = output;
    }

}
