package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.input.FunctionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.input.FunctionSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.TenumValue;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.Tfunction.ParamAndInputOutputOrder;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.ToutputFile;
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
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Manages all saved functions. Receives calls from other beans upon deletion.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class FunctionManager {

    private FunctionBean functionBean = null;
    private FunctionSelectionBean functionSelectionBean = null;
    private ParameterGroupManager parameterGroupManager = null;
    private ParameterManager parameterManager = null;
    /**
     * Contains all functions sorted in alphabetical order by name.
     */
    private SortedMap<String, FunctionTupel> functions;

    // <editor-fold defaultstate="collapsed" desc="function management">
    public FunctionManager() {
        // init functions map
        functions =
                new TreeMap<String, FunctionTupel>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Sets the functions-list back to an empty state. This should only be used
     * when loading a new functions object.
     */
    public void resetAllFunctions() {
        functions =
                new TreeMap<String, FunctionTupel>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new Function to the list of available functions.
     * @param function function to add
     * @param valid true: valid function; false: unvalid function (except dependency)
     * @param depValid true: dependency is valid; false dependency is unvalid
     * @param the generic name this is saved by
     * @throws BeansException  on error
     */
    public String addFunction(Tfunction function, boolean valid, boolean depValid)
            throws
            BeansException {

        if (!function.isSetName()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                function.getName().get(0).getValue());

        if (functions.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        functions.put(name, new FunctionTupel(
                function, valid, depValid));
        
        return name;
    }

    /**
     * Edits the function with oldname as name.
     * @param function new functionvalues
     * @param valid true: valid function; false: unvalid function (except dependency)
     * @param depValid true: dependency is valid; false dependency is unvalid
     * @throws BeansException on error
     */
    public void editFunction(String oldname, Tfunction function, boolean valid,
            boolean depValid) throws BeansException {

        if (!function.isSetName()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = IDGenerator.createName(
                function.getName().get(0).getValue());
        oldname = IDGenerator.createName(oldname);

        // cannot change name, because new one ist set by other object
        if (!oldname.equals(name) && functions.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (functions.containsKey(oldname)) {

            Tfunction oldfunction = functions.get(oldname).getFunction();
            // erase old function and add new one
            functions.remove(oldname);
            functions.put(name, new FunctionTupel(function, valid, depValid));

            if (functionSelectionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                functionSelectionBean = (FunctionSelectionBean) context.
                        getApplication().
                        evaluateExpressionGet(context,
                        "#{functionSelectionBean}",
                        FunctionSelectionBean.class);
            }
            functionSelectionBean.changeFunctionName(oldfunction.getName().get(0).
                    getValue(), function.getName().get(0).getValue());
            if (!valid) {
                functionSelectionBean.reload();
            }

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the function with name from the map.
     * @param name name of the function to delete
     * @throws BeansException on error
     */
    public void removeFunctionByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (functions.containsKey(name)) {

            // erase function
            functions.remove(name);
        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }

        if (functionSelectionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionSelectionBean = (FunctionSelectionBean) context.
                    getApplication().
                    evaluateExpressionGet(context,
                    "#{functionSelectionBean}",
                    FunctionSelectionBean.class);
        }
        functionSelectionBean.reload();
    }

    /**
     * Returns the function with the given name.
     * @param name of the function to retrieve
     * @return function
     * @throws BeansException on error
     */
    public Tfunction getFunctionByName(String name) throws BeansException {

        name = IDGenerator.createName(name);

        if (functions.containsKey(name)) {

            return functions.get(name).getFunction();
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all names contained in map.
     * @return alphabetically sorted list of all names contained in map
     */
    public List<Tupel<String, Boolean>> getAllNames() {

        List<Tupel<String, Boolean>> ret =
                new ArrayList<Tupel<String, Boolean>>();

        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            String name = entry.getValue().getFunction().getName().get(0).
                    getValue();
            boolean valid = entry.getValue().isValid();
            boolean depValid = entry.getValue().isDependencyValid();
            ret.add(new Tupel(name, valid && depValid));
        }

        return ret;
    }

    /**
     * Returns alphabetically sorted list of all names of all avlid functions contained in map.
     * This does return names that are not dependencyValid!!!!!
     * @return alphabetically sorted list of all names of all ready functions contained in map
     */
    public List<String> getAllValidNames() {

        List<String> ret =
                new ArrayList<String>();

        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            String name = entry.getValue().getFunction().getName().get(0).
                    getValue();
            if (entry.getValue().isValid()) {
                ret.add(name);
            }
        }
        return ret;
    }

    /**
     * Returns alphabetically sorted list of all names of all ready functions contained in map.
     * @return alphabetically sorted list of all names of all ready functions contained in map
     */
    public List<String> getAllReadyNames() {

        List<String> ret =
                new ArrayList<String>();

        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            String name = entry.getValue().getFunction().getName().get(0).
                    getValue();
            if (entry.getValue().isValid()
                    && entry.getValue().isDependencyValid()) {
                ret.add(name);
            }
        }
        return ret;
    }

    public boolean isEmpty() {
        return functions.isEmpty();
    }

    public SortedMap<String, FunctionTupel> getFunctions() {
        return functions;
    }

    public void setFunctions(SortedMap<String, FunctionTupel> functions) {
        this.functions = functions;
    }
    
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="remove calls from other beans">
    public void removeParameterGroup(TparamGroup group) {

        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            FunctionTupel tupel = entry.getValue();

            // this this function contain output?
            if (tupel.getFunction().isSetParamGroup() && group.equals(tupel.
                    getFunction().getParamGroup())) {

                // remove reference
                tupel.getFunction().setParamGroup(null);
                // remove parameters from everything
                removeAllParametersOfGroup(tupel.getFunction(), group);

                tupel.setDependencyValid(validateDependencyForFunction(tupel.
                        getFunction()));

                // if function is not already unvalid
                if (tupel.isValid()) {
                    // test if function was unvalidated and set value
                    tupel.setValid(validateInvalidateableFunctionParts(tupel.
                            getFunction()));
                }
                if (!tupel.isValid() || !tupel.isDependencyValid()) {
                    if (functionSelectionBean == null) {
                        // get reference to function bean to send refresh signals
                        FacesContext context = FacesContext.getCurrentInstance();
                        functionSelectionBean =
                                (FunctionSelectionBean) context.getApplication().
                                evaluateExpressionGet(context,
                                "#{functionSelectionBean}",
                                FunctionSelectionBean.class);
                    }
                    functionSelectionBean.reload();
                }
            }
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.reload();
    }

    public void removeInput(TinputOutput remove) {
        String id = remove.getId();

        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            // remove input from everything
            FunctionTupel tupel = entry.getValue();
            removeInputFromFunction(tupel.getFunction(), remove);
            removeObjectFromOrder(tupel.getFunction(), remove);
            removeObjectFromExamples(tupel.getFunction(), id);
            // if function is not already unvalid
            if (tupel.isValid()) {
                // test if function was unvalidated and set value
                tupel.setValid(validateInvalidateableFunctionParts(tupel.
                        getFunction()));
                if (!tupel.isValid()) {
                    if (functionSelectionBean == null) {
                        // get reference to function bean to send refresh signals
                        FacesContext context = FacesContext.getCurrentInstance();
                        functionSelectionBean =
                                (FunctionSelectionBean) context.getApplication().
                                evaluateExpressionGet(context,
                                "#{functionSelectionBean}",
                                FunctionSelectionBean.class);
                    }
                    functionSelectionBean.reload();
                }
            }
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.reload();
    }
    
    public void removeOutputFile(ToutputFile remove) {
        String id = remove.getId();

        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            // remove input from everything
            FunctionTupel tupel = entry.getValue();
            removeOutputFileFromFunction(tupel.getFunction(), remove);
            // if function is not already unvalid 
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.reload();
    }

    public void removeOutput(TinputOutput remove) {
        String id = remove.getId();
        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            FunctionTupel tupel = entry.getValue();

            // this this function contain output?
            if (tupel.getFunction().isSetOutputref() && remove.equals(tupel.
                    getFunction().getOutputref().getRef())) {

                // remove reference
                tupel.getFunction().setOutputref(null);
                // remove parameter from everything exmaples can't contain outputs
                removeObjectFromOrder(tupel.getFunction(), remove);
                // if function is not already unvalid
                if (tupel.isValid()) {
                    // test if function was unvalidated and set value
                    tupel.setValid(validateInvalidateableFunctionParts(tupel.
                            getFunction()));
                    if (!tupel.isValid()) {
                        if (functionSelectionBean == null) {
                            // get reference to function bean to send refresh signals
                            FacesContext context = FacesContext.
                                    getCurrentInstance();
                            functionSelectionBean =
                                    (FunctionSelectionBean) context.
                                    getApplication().
                                    evaluateExpressionGet(context,
                                    "#{functionSelectionBean}",
                                    FunctionSelectionBean.class);
                        }
                        functionSelectionBean.reload();
                    }
                }
            }
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.reload();
    }

    /**
     * Removes the (enum)parameter from all functions.
     * @param remove (enum)parameter to remove
     */
    public void removeParameter(Object remove) {
        // get the id of the parameter
        String id = "";
        if (remove instanceof Tparam) {
            id = ((Tparam) remove).getId();
        } else if (remove instanceof TenumParam) {
            id = ((TenumParam) remove).getId();
        }
        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            // remove parameter from everything
            FunctionTupel tupel = entry.getValue();
            removeObjectFromOrder(tupel.getFunction(), remove);
            removeObjectFromExamples(tupel.getFunction(), id);

            tupel.setDependencyValid(validateDependencyForFunction(tupel.
                    getFunction()));

            // if function is not already unvalid
            if (tupel.isValid()) {
                // test if function was unvalidated and set value
                tupel.setValid(validateInvalidateableFunctionParts(tupel.
                        getFunction()));
            }
            if (!tupel.isValid() || !tupel.isDependencyValid()) {
                if (functionSelectionBean == null) {
                    // get reference to function bean to send refresh signals
                    FacesContext context = FacesContext.getCurrentInstance();
                    functionSelectionBean =
                            (FunctionSelectionBean) context.getApplication().
                            evaluateExpressionGet(context,
                            "#{functionSelectionBean}",
                            FunctionSelectionBean.class);
                }
                functionSelectionBean.reload();
            }
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.reload();
    }

    public void removeDependency(Tdependency remove) {
        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            // remove input from everything
            FunctionTupel tupel = entry.getValue();
            removeDependencyFromFunction(tupel.getFunction(), remove);
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.reload();
    }

    /**
     * Tests if the function was invalidated by deleting stuff.
     * @param function function to test
     * @return true: function is still valid; false: function is not valid
     */
    private boolean validateInvalidateableFunctionParts(Tfunction function) {

        //It seems only output is really needed

        // test if outputref exits
        if (function.getOutputref() == null) {
            return false;
        }

        return true;
    }

    private boolean validateDependencyForFunction(Tfunction function) {

        String name = function.getName().get(0).getValue();
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        Tupel<Boolean, Boolean> valid =
                functionBean.loadAndTestFunctionExamples(name, true);
        // List containing errors is empty if dependency is coorect for this function
        return valid.getSecond();
    }

    /**
     * Removes the input remove from the inputrefs of the given function
     * @param function function to remove remove from
     * @param remove input to remove
     */
    private void removeInputFromFunction(Tfunction function, TinputOutput remove) {
        // loop through all input references
        Iterator<Tfunction.Inputref> inputIterator = function.getInputref().
                iterator();
        while (inputIterator.hasNext()) {
            // safely remove ref if input is the one to remove
            Tfunction.Inputref ref = inputIterator.next();
            if (remove.equals(ref.getRef())) {
                inputIterator.remove();
            }
        }
    }
    
     /**
     * Removes the output file remove from the outputfilerefs of the given function
     * @param function function to remove remove from
     * @param remove output file to remove
     */
    private void removeOutputFileFromFunction(Tfunction function, ToutputFile remove) {
        // loop through all input references
        Iterator<Tfunction.Outputfileref> outputFileIterator = function.getOutputfileref().
                iterator();
        while (outputFileIterator.hasNext()) {
            // safely remove ref if input is the one to remove
            Tfunction.Outputfileref ref = outputFileIterator.next();
            if (remove.equals(ref.getRef())) {
                outputFileIterator.remove();
            }
        }
    }

    /**
     * Removes the dependency remove from the deprefs of the given function
     * @param function function to remove remove from
     * @param remove input to remove
     */
    private void removeDependencyFromFunction(Tfunction function,
            Tdependency remove) {
        // loop through all input references
        Iterator<Tfunction.Depref> dependencyIterator = function.getDepref().
                iterator();
        while (dependencyIterator.hasNext()) {
            // safely remove ref if input is the one to remove
            Tfunction.Depref ref = dependencyIterator.next();
            if (remove.equals(ref.getRef())) {
                dependencyIterator.remove();
            }
        }
    }

    /**
     * removes ob from ParamAndInputOutputOrder of the given function
     * @param function functio  to remove ob from
     * @param ob object to be removed
     */
    private void removeObjectFromOrder(Tfunction function, Object ob) {
        ParamAndInputOutputOrder order = function.getParamAndInputOutputOrder();

        // loop through all elements of the order
        Iterator<JAXBElement<?>> orderIterator = order.
                getReferenceOrAdditionalString().iterator();
        while (orderIterator.hasNext()) {
            JAXBElement<?> element = orderIterator.next();
            // if the object os the searched one savely delete it
            if (element.getValue().equals(ob)) {
                orderIterator.remove();
            }
        }
    }

    /**
     * removes the object with given id from all examples of the function.
     * @param function function object has to be removed from.
     * @param id id of the object to be removed
     */
    private void removeObjectFromExamples(Tfunction function, String id) {

        Iterator<Texample> exampleIterator = function.getExample().iterator();
        while (exampleIterator.hasNext()) {
            Texample example = exampleIterator.next();

            Iterator<Texample.Prop> propIterator = example.getProp().iterator();
            while (propIterator.hasNext()) {
                Texample.Prop prop = propIterator.next();
                if (prop.getIdref().equals(id)) {
                    propIterator.remove();
                }
            }
        }
    }

    /**
     * Moves recursivly over all paremeters of the given group
     * and removes them from examples, order and dependencies.
     * This can't be done easier, because we need the ids of the paremeters.
     * Complexity is really bad thought, meaby we have to change this.
     * TODO: Test if this is too slow
     * @param function function to remove them from
     * @param group container of the parameters
     */
    private void removeAllParametersOfGroup(Tfunction function,
            TparamGroup group) {

        for (Object ref : group.getParamrefOrParamGroupref()) {

            Object ob = null;
            if (ref instanceof TparamGroup.ParamGroupref) {
                ob = ((TparamGroup.ParamGroupref) ref).getRef();
            } else if (ref instanceof TparamGroup.Paramref) {
                ob = ((TparamGroup.Paramref) ref).getRef();
            }

            if (ob instanceof TparamGroup) {
                removeAllParametersOfGroup(function, (TparamGroup) ob);
            } else if (ob instanceof Tparam) {
                Tparam param = (Tparam) ob;
                removeObjectFromExamples(function, param.getId());
                removeObjectFromOrder(function, param);
            } else if (ob instanceof TenumParam) {
                TenumParam param = (TenumParam) ob;
                removeObjectFromExamples(function, param.getId());
                removeObjectFromOrder(function, param);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="change calls from other beans">
    public void changeParameterGroup(TparamGroup oldgroup, String oldid,
            TparamGroup newgroup, String newid) {

        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            FunctionTupel tupel = entry.getValue();

            // is this function contain oldgroup?
            if (tupel.getFunction().isSetParamGroup() && oldgroup.equals(tupel.
                    getFunction().getParamGroup())) {

                // set new reference
                tupel.getFunction().setParamGroup(newgroup);
                // create new oder, examples, dependencies
                buildOrderWithChangedParams(tupel.getFunction(), newid);
                if (buildExampleWithChangedParams(tupel.getFunction(), newid)) {
                    // uncomment if every parameter has to contain data
                    //setValid(false);
                }

                tupel.setDependencyValid(validateDependencyForFunction(tupel.
                        getFunction()));

                if (!tupel.isValid() || !tupel.isDependencyValid()) {
                    if (functionSelectionBean == null) {
                        // get reference to function bean to send refresh signals
                        FacesContext context = FacesContext.getCurrentInstance();
                        functionSelectionBean =
                                (FunctionSelectionBean) context.getApplication().
                                evaluateExpressionGet(context,
                                "#{functionSelectionBean}",
                                FunctionSelectionBean.class);
                    }
                    functionSelectionBean.reload();
                }
            }
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.changedParamGroup(oldid, newid);
    }

    public void changeInput(TinputOutput oldInput, TinputOutput newInput) {
        String oldid = oldInput.getId();
        String newid = newInput.getId();

        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            // remove parameter from everything
            FunctionTupel tupel = entry.getValue();
            changeInputInFunction(tupel.getFunction(), oldInput, newInput);
            changeObjectInOrder(tupel.getFunction(), oldInput, newInput);

            changeObjectInExamples(tupel.getFunction(), oldid, newid);

        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.changedInput(oldInput.getName().get(0).getValue(),
                newInput.getName().get(0).getValue());
    }
    

    /**
     * Changes all oldInput inputs in function to newInput
     * @param function function to change in
     * @param oldInput input to change
     * @param newInput input to change is into
     */
    private void changeInputInFunction(Tfunction function, TinputOutput oldInput,
            TinputOutput newInput) {
        // loop through all input references
        Iterator<Tfunction.Inputref> inputIterator = function.getInputref().
                iterator();
        while (inputIterator.hasNext()) {
            // safely change ref if input is the one to remove
            Tfunction.Inputref ref = inputIterator.next();
            if (oldInput.equals(ref.getRef())) {
                ref.setRef(newInput);
            }
        }
    }
    
     public void changeOutputFile(ToutputFile oldoutput, String oldname,
            ToutputFile newoutput, String newname) {
        String oldid = oldoutput.getId();
        String newid = newoutput.getId();

        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            // remove parameter from everything
            FunctionTupel tupel = entry.getValue();
            changeOutputFileInFunction(tupel.getFunction(), oldoutput, newoutput);
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.changedOutputFile(oldname, newname);
    }
    
    private void changeOutputFileInFunction(Tfunction function, ToutputFile oldoutput,
            ToutputFile newoutput) {
        // loop through all input references
        Iterator<Tfunction.Outputfileref> outputFileIterator = function.getOutputfileref().
                iterator();
        while (outputFileIterator.hasNext()) {
            // safely change ref if input is the one to remove
            Tfunction.Outputfileref ref = outputFileIterator.next();
            if (oldoutput.equals(ref.getRef())) {
                ref.setRef(newoutput);
            }
        }
    }
    

    public void changeOutput(TinputOutput oldOutput, TinputOutput newOutput) {

        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            FunctionTupel tupel = entry.getValue();

            // this this function contain output?
            if (tupel.getFunction().isSetOutputref() && oldOutput.equals(tupel.
                    getFunction().getOutputref().getRef())) {

                // set reference
                tupel.getFunction().getOutputref().setRef(newOutput);
                // change references in order and dependecies
                changeObjectInOrder(tupel.getFunction(), oldOutput, newOutput);
            }
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.changedOutput(oldOutput.getName().get(0).getValue(),
                newOutput.getName().get(0).getValue());
    }

    /**
     * changes the (enum)parameter  oldparam in all functions to newparam
     * @param oldparam (enum)parameter to change
     * @param newparam (enum)parameter to change oldparam to
     */
    public void changeParameter(Object oldparam, Object newparam) {
        // get the id and name of the oldparam
        String oldid = "";
        String oldname = "";
        if (oldparam instanceof Tparam) {
            oldid = ((Tparam) oldparam).getId();
            oldname = ((Tparam) oldparam).getName().get(0).getValue();
        } else if (oldparam instanceof TenumParam) {
            oldid = ((TenumParam) oldparam).getId();
            oldname = ((TenumParam) oldparam).getName().get(0).getValue();
        }
        // get the id and name of the newparam
        String newid = "";
        String newname = "";
        if (newparam instanceof Tparam) {
            newid = ((Tparam) newparam).getId();
            newname = ((Tparam) newparam).getName().get(0).getValue();
        } else if (newparam instanceof TenumParam) {
            newid = ((TenumParam) newparam).getId();
            newname = ((TenumParam) newparam).getName().get(0).getValue();
        }
        // loop through all functions
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            // remove parameter from everything
            FunctionTupel tupel = entry.getValue();
            changeObjectInOrder(tupel.getFunction(), oldparam, newparam);
            changeObjectInExamples(tupel.getFunction(), oldid, newid);

            tupel.setDependencyValid(validateDependencyForFunction(tupel.
                    getFunction()));

            if (!tupel.isValid() || !tupel.isDependencyValid()) {
                if (functionSelectionBean == null) {
                    // get reference to function bean to send refresh signals
                    FacesContext context = FacesContext.getCurrentInstance();
                    functionSelectionBean =
                            (FunctionSelectionBean) context.getApplication().
                            evaluateExpressionGet(context,
                            "#{functionSelectionBean}",
                            FunctionSelectionBean.class);
                }
                functionSelectionBean.reload();
            }
        }
        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.changedParameter(oldname, newname);
    }

    public void changeParameterDependency(Tdependency oldDependency,
            Tdependency newDependency) {

        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            // remove parameter from everything
            FunctionTupel tupel = entry.getValue();
            changeDependencyInFunction(tupel.getFunction(), oldDependency,
                    newDependency);

            tupel.setDependencyValid(validateDependencyForFunction(tupel.
                    getFunction()));

            if (!tupel.isValid() || !tupel.isDependencyValid()) {
                if (functionSelectionBean == null) {
                    // get reference to function bean to send refresh signals
                    FacesContext context = FacesContext.getCurrentInstance();
                    functionSelectionBean =
                            (FunctionSelectionBean) context.getApplication().
                            evaluateExpressionGet(context,
                            "#{functionSelectionBean}",
                            FunctionSelectionBean.class);
                }
                functionSelectionBean.reload();
            }
        }

        if (functionBean == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
        }
        functionBean.changedDependency(oldDependency.getName().get(0).getValue(),
                newDependency.getName().get(0).getValue());
    }

    /**
     * Update order of the given function.
     * This is called at loadin of functions.
     * @param function 
     */
    public void updateOrder(Tfunction function) {
        buildOrderWithChangedParams(function,
                function.getName().get(0).getValue());
    }

    /**
     * Changes all oldDependency in function to newDependency
     * @param function function to change in
     * @param oldDependency dependency to change
     * @param newDependency dependency to change is into
     */
    private void changeDependencyInFunction(Tfunction function,
            Tdependency oldDependency,
            Tdependency newDependency) {
        // loop through all input references
        Iterator<Tfunction.Depref> depIterator = function.getDepref().
                iterator();
        while (depIterator.hasNext()) {
            // safely change ref if input is the one to remove
            Tfunction.Depref ref = depIterator.next();
            if (oldDependency.equals(ref.getRef())) {
                ref.setRef(newDependency);
            }
        }
    }

    /**
     * changes oldob in ParamAndInputOutputOrder of the given function to newob
     * @param function functio  to remove ob from
     * @param ob oldob to be removed
     */
    private void changeObjectInOrder(Tfunction function, Object oldob,
            Object newob) {
        ParamAndInputOutputOrder order = function.getParamAndInputOutputOrder();

        // loop through all elements of the order
        ListIterator<JAXBElement<?>> orderIterator = order.
                getReferenceOrAdditionalString().listIterator();
        while (orderIterator.hasNext()) {
            JAXBElement<?> element = orderIterator.next();
            // if the object os the searched one
            if (element.getValue().equals(oldob)) {
                orderIterator.remove();
                orderIterator.add(new JAXBElement(new QName(
                        "bibiserv:de.unibi.techfak.bibiserv.cms",
                        "reference"), Object.class,
                        Tfunction.ParamAndInputOutputOrder.class,
                        newob));
            }
        }
    }

    /**
     * changes the object with given old id in all examples of the function to newid.
     * @param function function object has to be changed in.
     * @param oldid id of the old object
     * @param id of the new object
     */
    private void changeObjectInExamples(Tfunction function, String oldid,
            String newid) {

        Iterator<Texample> exampleIterator = function.getExample().iterator();
        while (exampleIterator.hasNext()) {
            Texample example = exampleIterator.next();

            Iterator<Texample.Prop> propIterator = example.getProp().iterator();
            while (propIterator.hasNext()) {
                Texample.Prop prop = propIterator.next();
                if (prop.getIdref().equals(oldid)) {
                    prop.setIdref(newid);
                }
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="example reset calls">
    public void resetExamplesForParameter(Object parameteOb) {

        String id = "";
        String name = "";
        if (parameteOb instanceof Tparam) {
            id = ((Tparam) parameteOb).getId();
            name = ((Tparam) parameteOb).getName().get(0).getValue();
        } else if (parameteOb instanceof TenumParam) {
            id = ((TenumParam) parameteOb).getId();
            name = ((TenumParam) parameteOb).getName().get(0).getValue();
        }

        boolean message = false;

        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            FunctionTupel tupel = entry.getValue();

            Iterator<Texample> exampleIterator =
                    tupel.getFunction().getExample().iterator();
            while (exampleIterator.hasNext()) {
                Texample example = exampleIterator.next();

                Iterator<Texample.Prop> propIterator = example.getProp().
                        iterator();
                while (propIterator.hasNext()) {
                    Texample.Prop prop = propIterator.next();
                    if (prop.getIdref().equals(id)) {
                        if (!correctParameterValue(prop.getValue(), parameteOb)) {
                            prop.setValue("");
                            // set function unvalid
                            tupel.valid = false;
                            message = true;
                        }
                    }
                }
            }
        }
        if (message) {
            if (functionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                functionBean = (FunctionBean) context.getApplication().
                        evaluateExpressionGet(context, "#{functionBean}",
                        FunctionBean.class);
            }
            functionBean.reloadExamplesParameter(name);
            if (functionSelectionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                functionSelectionBean = (FunctionSelectionBean) context.
                        getApplication().evaluateExpressionGet(context,
                        "#{functionSelectionBean}",
                        FunctionSelectionBean.class);
            }
            functionSelectionBean.reload();
        }
    }

    public void resetExamplesForInput(String id, String name) {

        boolean message = false;
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            FunctionTupel tupel = entry.getValue();

            Iterator<Texample> exampleIterator =
                    tupel.getFunction().getExample().iterator();
            while (exampleIterator.hasNext()) {
                Texample example = exampleIterator.next();

                Iterator<Texample.Prop> propIterator = example.getProp().
                        iterator();
                while (propIterator.hasNext()) {
                    Texample.Prop prop = propIterator.next();
                    if (prop.getIdref().equals(id)) {
                        prop.setValue("");
                        // set function unvalid
                        tupel.valid = false;
                        message = true;
                    }
                }
            }
        }
        if (message) {
            if (functionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                functionBean = (FunctionBean) context.getApplication().
                        evaluateExpressionGet(context, "#{functionBean}",
                        FunctionBean.class);
            }
            functionBean.reloadExamplesInput(name);
            if (functionSelectionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                functionSelectionBean = (FunctionSelectionBean) context.
                        getApplication().evaluateExpressionGet(context,
                        "#{functionSelectionBean}",
                        FunctionSelectionBean.class);
            }
            functionSelectionBean.reload();
        }
    }

    public void rebuildExamples(String id, String name) {
        for (Map.Entry<String, FunctionTupel> entry : functions.entrySet()) {
            FunctionTupel tupel = entry.getValue();

            Iterator<Texample> exampleIterator =
                    tupel.getFunction().getExample().iterator();
            while (exampleIterator.hasNext()) {
                Texample example = exampleIterator.next();

                Iterator<Texample.Prop> propIterator = example.getProp().
                        iterator();
                while (propIterator.hasNext()) {
                    Texample.Prop prop = propIterator.next();
                    if (prop.getIdref().equals(id)) {
                        propIterator.remove();
                    }
                }
            }
        }
    }

    /**
     * Tests if the given value is correct for the parameter object.
     * @param value value to test
     * @param parameteOb
     * @return
     */
    private boolean correctParameterValue(String value, Object parameterOb) {
        if (parameterOb instanceof Tparam) {
            Tparam param = (Tparam) parameterOb;
            switch (param.getType()) {
                case BOOLEAN:
                    return ParameterValidator.validateBoolean(value);
                case DATETIME:
                    return ParameterValidator.validateDateTime(value);
                case STRING:
                    int minLength = Integer.MIN_VALUE;
                    if (param.isSetMinLength()) {
                        minLength = param.getMinLength();
                    }
                    int maxLength = Integer.MAX_VALUE;
                    if (param.isSetMaxLength()) {
                        maxLength = param.getMaxLength();
                    }
                    String regexp = "";
                    if (param.isSetRegexp()) {
                        regexp = param.getRegexp();
                    }
                    return ParameterValidator.validateString(value, minLength,
                            maxLength, regexp);
                case FLOAT:
                    float min = Float.MIN_VALUE;
                    boolean includeMin = false;
                    if (param.isSetMin()) {
                        min = param.getMin().getValue();
                        includeMin = param.getMin().isIncluded();
                    }
                    float max = Float.MAX_VALUE;
                    boolean includeMax = false;
                    if (param.isSetMax()) {
                        max = param.getMax().getValue();
                        includeMax = param.getMax().isIncluded();
                    }
                    return ParameterValidator.validateFloat(value, min,
                            includeMin, max, includeMax);
                case INT:
                    int minInt = Integer.MIN_VALUE;
                    includeMin = false;
                    if (param.isSetMin()) {
                        minInt = (int) param.getMin().getValue();
                        includeMin = param.getMin().isIncluded();
                    }
                    int maxInt = Integer.MAX_VALUE;
                    includeMax = false;
                    if (param.isSetMax()) {
                        maxInt = (int) param.getMax().getValue();
                        includeMax = param.getMax().isIncluded();
                    }
                    return ParameterValidator.validateInt(value, minInt,
                            includeMin, maxInt, includeMax);
            }
        } else if (parameterOb instanceof TenumParam) {
            TenumParam param = (TenumParam) parameterOb;
            int minOccur = -1;
            if (param.isSetMinoccurs()) {
                minOccur = param.getMinoccurs();
            }
            int maxOccur = Integer.MAX_VALUE;
            if (param.isSetMaxoccurs()) {
                maxOccur = param.getMaxoccurs();
            }
            String separator = param.getSeparator();
            List<String> keys = new ArrayList<String>();
            for (TenumValue enumVal : param.getValues()) {
                keys.add(enumVal.getKey());
            }
            return ParameterValidator.validateEnum(value, keys, minOccur,
                    maxOccur, separator);
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="rebuild of order/example">
    private void buildOrderWithChangedParams(Tfunction function,
            String newgroupid) {

        if (parameterGroupManager == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            parameterGroupManager = (ParameterGroupManager) context.
                    getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupManager}",
                    ParameterGroupManager.class);
        }
        if (parameterManager == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            parameterManager = (ParameterManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterManager}",
                    ParameterManager.class);
        }

        Map<String, Integer> parameterMultiplicity =
                new HashMap<String, Integer>();
        Map<String, Integer> foundMultiplicity = new HashMap<String, Integer>();
        try {
            parameterMultiplicity = parameterGroupManager.
                    getAllParametersOfParamGroupByName(newgroupid);
        } catch (BeansException ex) {
            // should no occur
        }

        Tfunction.ParamAndInputOutputOrder order = function.
                getParamAndInputOutputOrder();
        // loop through all elements of the order
        Iterator<JAXBElement<?>> orderIterator = order.
                getReferenceOrAdditionalString().iterator();
        while (orderIterator.hasNext()) {
            JAXBElement<?> element = orderIterator.next();

            String name = "";
            if (element.getValue() instanceof Tparam) {
                name = ((Tparam) element.getValue()).getName().get(0).getValue();
            } else if (element.getValue() instanceof TenumParam) {
                name = ((TenumParam) element.getValue()).getName().get(0).
                        getValue();
            }

            if (parameterContained(parameterMultiplicity, foundMultiplicity,
                    name)) {
                if (foundMultiplicity.containsKey(name)) {
                    int count = foundMultiplicity.get(name);
                    count++;
                    foundMultiplicity.put(name, count);
                } else {
                    foundMultiplicity.put(name, 1);
                }
            } else if (!(element.getValue() instanceof String) || !(element.
                    getValue() instanceof TinputOutput)) {
                orderIterator.remove();
            }
        }

        // add new parameters
        for (Map.Entry<String, Integer> entry : parameterMultiplicity.entrySet()) {
            int multiplicity = 0;
            String name = entry.getKey();
            if (foundMultiplicity.containsKey(name)) {
                multiplicity = foundMultiplicity.get(name);
            }

            Object param;
            try {
                param = parameterManager.getParameterByName(name);

                for (int i = multiplicity; i < entry.getValue(); i++) {
                    order.getReferenceOrAdditionalString().add(new JAXBElement(new QName(
                            "bibiserv:de.unibi.techfak.bibiserv.cms",
                            "reference"), Object.class,
                            Tfunction.ParamAndInputOutputOrder.class,
                            param));
                }
            } catch (BeansException ex) {
            }

        }
    }

    private boolean parameterContained(
            Map<String, Integer> parameterMultiplicity,
            Map<String, Integer> foundMultiplicity, String name) {
        int multiplicity = 0;
        if (foundMultiplicity.containsKey(name)) {
            multiplicity = foundMultiplicity.get(name);
        }
        if (parameterMultiplicity.containsKey(name)) {
            if (parameterMultiplicity.get(name) > multiplicity) {
                return true;
            }
        }
        return false;
    }

    private boolean buildExampleWithChangedParams(Tfunction function,
            String newgroupid) {
        boolean changed = false;
        if (parameterGroupManager == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            parameterGroupManager = (ParameterGroupManager) context.
                    getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupManager}",
                    ParameterGroupManager.class);
        }
        if (parameterManager == null) {
            // get reference to function bean to send refresh signals
            FacesContext context = FacesContext.getCurrentInstance();
            parameterManager = (ParameterManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterManager}",
                    ParameterManager.class);
        }

        Map<String, Integer> parameterMultiplicity =
                new HashMap<String, Integer>();
        Map<String, Integer> foundMultiplicity = new HashMap<String, Integer>();
        try {
            parameterMultiplicity = parameterGroupManager.
                    getAllParametersOfParamGroupByName(newgroupid);
        } catch (BeansException ex) {
            // should no occur
        }

        // loop through all elements of the order
        Iterator<Texample> exampleIterator = function.getExample().iterator();
        while (exampleIterator.hasNext()) {

            Texample example = exampleIterator.next();
            Iterator<Texample.Prop> propIterator = example.getProp().iterator();
            while (propIterator.hasNext()) {
                Texample.Prop prop = propIterator.next();
                boolean foundName = true;
                String name = "";
                try {
                    name = parameterManager.getNameforId(prop.getIdref());
                } catch (BeansException ex) {
                    // this is not a parameter
                    foundName = false;
                }
                if (foundName) {
                    if (parameterContained(parameterMultiplicity,
                            foundMultiplicity,
                            name)) {
                        if (foundMultiplicity.containsKey(name)) {
                            int count = foundMultiplicity.get(name);
                            count++;
                            foundMultiplicity.put(name, count);
                        } else {
                            foundMultiplicity.put(name, 1);
                        }
                    } else {
                        if (!prop.getIdref().startsWith(InputOutputBuilder.
                                getID_BASE_TYPE_OUTPUT()) && !prop.getIdref().
                                startsWith(InputOutputBuilder.
                                getID_BASE_TYPE_INPUT())) {
                            propIterator.remove();
                        }
                    }
                }
            }
            // add new parameters
            for (Map.Entry<String, Integer> entry : parameterMultiplicity.
                    entrySet()) {

                int multiplicity = 0;
                String name = entry.getKey();
                if (foundMultiplicity.containsKey(name)) {
                    multiplicity = foundMultiplicity.get(name);
                }

                for (int i = multiplicity; i < entry.getValue(); i++) {
                    Texample.Prop newprop = new Texample.Prop();
                    newprop.setIdref(IDGenerator.createTemporaryID(name,
                            ParameterBuilder.getID_BASE_TYPE()));
                    newprop.setValue("");
                    changed = true;
                    example.getProp().add(newprop);
                }
            }
        }
        return changed;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="inner class functiontupel">
    /**
     * Little hepler subclass, improves readability.
     */
    public static class FunctionTupel {

        private Tfunction function;
        private boolean valid;
        private boolean dependencyValid;

        public FunctionTupel(Tfunction function, boolean valid,
                boolean dependencyValid) {
            this.function = function;
            this.valid = valid;
            this.dependencyValid = dependencyValid;
        }

        public Tfunction getFunction() {
            return function;
        }

        public void setFunction(Tfunction function) {
            this.function = function;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public boolean isDependencyValid() {
            return dependencyValid;
        }

        public void setDependencyValid(boolean dependencyValid) {
            this.dependencyValid = dependencyValid;
        }
    }
// </editor-fold>
}
