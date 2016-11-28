package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.DescriptionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.Example;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleInputStore;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleParameterEnumStore;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleParameterStore;
import de.unibi.cebitec.bibiserv.wizard.bean.ExampleStore;
import de.unibi.cebitec.bibiserv.wizard.bean.GeneralCallback;
import de.unibi.cebitec.bibiserv.wizard.bean.OrderStore;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.HandlingType;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.IdRefType;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.PrimitiveType;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ExampleManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.InputManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.InputOutputBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputFileManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterDependencyTester;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterGroupManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterValidator;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.xml.bind.JAXBElement;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class FunctionBean extends DescriptionBean {

    // <editor-fold defaultstate="collapsed" desc="private variables">
    //basic data
    private String name;
    private String shortDescription;
    private String output;
    private String paramGroup;
    private List<String> selectedInputs;
    private DataModel<Tupel<Integer, String>> selectedInputsWithId;
    private boolean isInputsEdited;
    private List<String> selectedOutputFiles;
    private DataModel<Tupel<Integer, String>> selectedOutputFilesWithId;
    private boolean isOutputFilesEdited;
    private List<String> selectedDependencies;
    private DataModel<Tupel<Integer, String>> selectedDependenciesWithId;
    private boolean isDependenciesEdited;
    private List<OrderStore> order;
    //data non-bean manager
    private ExampleManager exampleManager;
    //available Data
    private List<Tupel<String, Boolean>> functionNameList;
    private boolean functionsEmpty;
    private List<String> inputNameList;
    private boolean inputsEmpty;
    private List<String> outputNameList;
    private boolean outputsEmpty;
   private List<String> outputFileNameList;
    private boolean outputFilesEmpty;
    private List<String> parameterGroupNameList;
    private boolean parameterGroupsEmpty;
    private List<String> dependencyNamesList;
    private boolean dependenciesEmpty;
    //manager beans
    private FunctionManager functionManager;
    private ParameterManager parameterManager;
    private ParameterGroupManager parameterGroupManager;
    private InputManager inputManager;
    private OutputManager outputManager;
    private OutputFileManager outputFileManager;
    private ParameterDependencyManager dependencyManager;
    //beans for editing
    private ParameterGroupBean parameterGroupBean;
    private InputBean inputBean;
    private OutputBean outputBean;
    private OutputFileBean outputFileBean;
    private DependencyBean dependencyBean;
    //Other beans
    private OrderBean orderBean;
    private ExampleBean exampleBean;
    //loaded
    private String loadedFrom;
    private boolean renderLoadedFrom;
    //information store
    private Map<String, Integer> containedParametersMultiplicity;
    private Map<String, Integer> containedInputsMultiplicity;
    private Map<String, Integer> encounteredOrderStore;

    private boolean renderUnsavedChanges;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="constructor">
    public FunctionBean() {
        // set xhtml to the one used in this bean as return value for other beans
        xhtml = "function.xhtml";

        // retrieve current Manager beans
        FacesContext context = FacesContext.getCurrentInstance();
        functionManager = (FunctionManager) context.getApplication().
                evaluateExpressionGet(context, "#{functionManager}",
                FunctionManager.class);
        parameterGroupManager = (ParameterGroupManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterGroupManager}",
                ParameterGroupManager.class);
        parameterManager = (ParameterManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterManager}",
                ParameterManager.class);
        inputManager = (InputManager) context.getApplication().
                evaluateExpressionGet(context, "#{inputManager}",
                InputManager.class);
        outputManager = (OutputManager) context.getApplication().
                evaluateExpressionGet(context, "#{outputManager}",
                OutputManager.class);
        outputFileManager = (OutputFileManager) context.getApplication().
                evaluateExpressionGet(context, "#{outputFileManager}",
                OutputFileManager.class);
        dependencyManager =
                (ParameterDependencyManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterDependencyManager}",
                ParameterDependencyManager.class);

        //retrieve current beans
        parameterGroupBean = (ParameterGroupBean) context.getApplication().
                evaluateExpressionGet(context, "#{parameterGroupBean}",
                ParameterGroupBean.class);
        inputBean = (InputBean) context.getApplication().
                evaluateExpressionGet(context, "#{inputBean}",
                InputBean.class);
        outputBean = (OutputBean) context.getApplication().
                evaluateExpressionGet(context, "#{outputBean}",
                OutputBean.class);
        outputFileBean = (OutputFileBean) context.getApplication().
                evaluateExpressionGet(context, "#{outputFileBean}",
                OutputFileBean.class);
        dependencyBean = (DependencyBean) context.getApplication().
                evaluateExpressionGet(context, "#{dependencyBean}",
                DependencyBean.class);

        // retrieve other beans
        orderBean = (OrderBean) context.getApplication().
                evaluateExpressionGet(context, "#{orderBean}",
                OrderBean.class);
        exampleBean = (ExampleBean) context.getApplication().
                evaluateExpressionGet(context, "#{exampleBean}",
                ExampleBean.class);

        position = PropertyManager.getProperty("function");
        
        resetAll();
        refillSelected();

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reset/refill">
    private void refillSelected() {
        if (selectedInputs.isEmpty()) {
            selectedInputs.add("");
        }
        
        if(selectedOutputFiles.isEmpty()){
            selectedOutputFiles.add("");
        }

        if (selectedDependencies.isEmpty()) {
            selectedDependencies.add("");
        }
    }

    private void resetAll() {
        //reset loaded
        loadedFrom = "";
        renderLoadedFrom = false;
        // reset normal data
        name = "";
        shortDescription = "";
        description = "";
        //reset dropdown
        output = "";
        paramGroup = "";
        //selected Lists
        selectedInputs = new ArrayList<String>();
        isInputsEdited = true;
        selectedOutputFiles = new ArrayList<String>();
        isOutputFilesEdited= true;
        selectedDependencies = new ArrayList<String>();
        isDependenciesEdited = true;
        //non-bean manager
        exampleManager = new ExampleManager();
        order = new ArrayList<OrderStore>();
        
        renderUnsavedChanges = false;
        
        getAvailableData();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="data retrieval">
    private void getAvailableData() {
        parameterGroupNameList = parameterGroupManager.getAllNames();
        parameterGroupsEmpty = parameterGroupManager.isEmpty();

        functionNameList = functionManager.getAllNames();
        functionsEmpty = functionManager.isEmpty();

        inputNameList = inputManager.getAllNames();
        inputsEmpty = inputManager.isEmpty();

        outputNameList = outputManager.getAllNames();
        outputsEmpty = outputManager.isEmpty();
        
        outputFileNameList = outputFileManager.getAllNames();
        outputFilesEmpty = outputFileManager.isEmpty();

        dependencyNamesList = dependencyManager.getAllNames();
        dependenciesEmpty = dependencyManager.isEmpty();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calculate example/order/dependency">
    /**
     * Builds example, dependency and order.
     */
    private void buildAll() {
        buildExamples();
        buildOrder();
    }

    /**
     * Retrieves the current parameters of the parameter group as
     * MultiplicityMap.
     */
    private void getMultiplicities() {
        try {
            if (paramGroup == null) {
                paramGroup = "";
            }
            containedParametersMultiplicity = parameterGroupManager.getAllParametersOfParamGroupByName(paramGroup);
        } catch (BeansException ex) {
            containedParametersMultiplicity = new HashMap<String, Integer>();
        }
        containedInputsMultiplicity = new HashMap<String, Integer>();
        for (String input : selectedInputs) {
            if (!input.isEmpty()) {
                if (containedInputsMultiplicity.containsKey(input)) {
                    int count = containedInputsMultiplicity.get(input);
                    count++;
                    containedInputsMultiplicity.put(input, count);
                } else {
                    containedInputsMultiplicity.put(input, 1);
                }
            }
        }
    }

    /**
     * Builds up the new order keeping old items in order and adding new ones to
     * the end.
     */
    private void buildOrder() {
        getMultiplicities();
        List<OrderStore> newOrder = new ArrayList<OrderStore>();
        encounteredOrderStore = new HashMap<String, Integer>();
        // test if current items of store are still valid
        for (OrderStore store : order) {
            if (store.isIsString()) {
                newOrder.add(store);
            } else if (functionContainsStore(store.getValue(), store.getType())) {
                String key = store.getValue() + store.getType();
                newOrder.add(store);
                if (encounteredOrderStore.containsKey(key)) {
                    int count = encounteredOrderStore.get(key);
                    count++;
                    encounteredOrderStore.put(key, count);
                } else {
                    encounteredOrderStore.put(key, 1);
                }
            }
        }
        // from this point on updating of encounteredMap is not needed anymore
        // first add parameters
        for (Map.Entry<String, Integer> entry : containedParametersMultiplicity.entrySet()) {
            int multiplicity = 0;
            String key = entry.getKey() + IdRefType.parameter.toString();
            if (encounteredOrderStore.containsKey(key)) {
                multiplicity = encounteredOrderStore.get(key);
            }
            OrderStore newStore = new OrderStore(entry.getKey(),
                    IdRefType.parameter, false);
            for (int i = multiplicity; i < entry.getValue(); i++) {
                newOrder.add(newStore);
            }
        }
        //then add inputs
        for (Map.Entry<String, Integer> entry : containedInputsMultiplicity.entrySet()) {
            int multiplicity = 0;
            String key = entry.getKey() + IdRefType.input.toString();
            if (encounteredOrderStore.containsKey(key)) {
                multiplicity = encounteredOrderStore.get(key);
            }
            OrderStore newStore = new OrderStore(entry.getKey(), IdRefType.input,
                    false);
            for (int i = multiplicity; i < entry.getValue(); i++) {
                newOrder.add(newStore);
            }
        }
        if (output != null && !output.isEmpty()) {
            //last check output
            String key = output + IdRefType.output.toString();
            if (!encounteredOrderStore.containsKey(key)) {
                OrderStore newStore =
                        new OrderStore(output, IdRefType.output, false);
                newOrder.add(newStore);
            }
        }
        // copy new order over old one
        order = newOrder;
    }

    /**
     * Loops through all examples and rebuilds them with new data.
     */
    private void buildExamples() {
        getMultiplicities();
        Iterator<Example> examplesIterator =
                exampleManager.getValues().iterator();

        while (examplesIterator.hasNext()) {
            Example example = examplesIterator.next();
            boolean valid = buildExample(example.getExamples());
            // uncomment this if empty parameters and inputs are not allowed
//            if (example.isValid()) {
//                example.setValid(valid);
//            }
        }

        exampleManager.setEmptyBaseStore(buildEmptyExampleBase());

        Tfunction testfunction = FunctionBuilder.createFunction(name,
                shortDescription, description,
                selectedInputs, output, selectedOutputFiles, selectedDependencies,
                paramGroup, order, exampleManager.getValues(),
                BasicBeanData.StandardLanguage);

        exampleBean.setManager(exampleManager, name, testfunction);
        exampleBean.validateAllDependencys();
    }

    /**
     * Builds up the new example.
     */
    private boolean buildExample(List<ExampleStore> example) {
        List<ExampleStore> exampleStore = new ArrayList<ExampleStore>();
        encounteredOrderStore = new HashMap<String, Integer>();
        // test if current items of store are still valid
        for (ExampleStore store : example) {
            if (functionContainsStore(store.getName(), store.getType())) {
                String key = store.getName() + store.getType();
                exampleStore.add(store);
                if (encounteredOrderStore.containsKey(key)) {
                    int count = encounteredOrderStore.get(key);
                    count++;
                    encounteredOrderStore.put(key, count);
                } else {
                    encounteredOrderStore.put(key, 1);
                }
            }
        }
        boolean changed = true;
        // from this point on updating of encounteredMap is not needed anymore
        // first add parameters
        for (Map.Entry<String, Integer> entry : containedParametersMultiplicity.entrySet()) {
            int multiplicity = 0;
            String key = entry.getKey() + IdRefType.parameter.toString();
            if (encounteredOrderStore.containsKey(key)) {
                multiplicity = encounteredOrderStore.get(key);
            }

            ExampleStore newStore = null;
            Object param = null;
            try {
                param = parameterManager.getParameterByName(entry.getKey());
            } catch (BeansException ex) {
                // that really should not happen
            }

            if (param instanceof Tparam) {
                newStore = parameterToExampleStore((Tparam) param, "", false).
                        getSecond();
            } else if (param instanceof TenumParam) {
                newStore = parameterEnumToExampleStore((TenumParam) param, "",
                        false).
                        getSecond();
            }

            for (int i = multiplicity; i < entry.getValue(); i++) {
                exampleStore.add(newStore.clone());
                changed = false;
            }
        }
        //then add inputs
        for (Map.Entry<String, Integer> entry : containedInputsMultiplicity.entrySet()) {
            int multiplicity = 0;
            String key = entry.getKey() + IdRefType.input.toString();
            if (encounteredOrderStore.containsKey(key)) {
                multiplicity = encounteredOrderStore.get(key);
            }
            try {
                TinputOutput in = inputManager.getInputByName(entry.getKey());
                ExampleStore newStore = inputToExampleStore(in, "").getSecond();

                for (int i = multiplicity; i < entry.getValue(); i++) {
                    exampleStore.add(newStore.clone());
                    changed = false;
                }
            } catch (BeansException ex) {
                // should not happen
            }
        }

        example.clear();
        example.addAll(exampleStore);
        return changed;
    }

    /**
     * Builds up an empty example to load as new empty example.
     *
     * @return new empty example
     */
    private List<ExampleStore> buildEmptyExampleBase() {

        List<ExampleStore> emptyStore = new ArrayList<ExampleStore>();

        for (Map.Entry<String, Integer> entry : containedParametersMultiplicity.entrySet()) {

            ExampleStore newStore = null;
            Object param = null;
            try {
                param = parameterManager.getParameterByName(entry.getKey());
            } catch (BeansException ex) {
                // that really should not happen
            }

            if (param instanceof Tparam) {
                newStore = parameterToExampleStore((Tparam) param, "", false).
                        getSecond();
            } else if (param instanceof TenumParam) {
                newStore = parameterEnumToExampleStore((TenumParam) param, "",
                        false).
                        getSecond();
            }

            for (int i = 0; i < entry.getValue(); i++) {
                emptyStore.add(newStore.clone());
            }
        }
        //then add inputs
        for (Map.Entry<String, Integer> entry : containedInputsMultiplicity.entrySet()) {
            try {
                TinputOutput in = inputManager.getInputByName(entry.getKey());
                ExampleStore newStore = inputToExampleStore(in, "").getSecond();

                for (int i = 0; i < entry.getValue(); i++) {
                    emptyStore.add(newStore.clone());
                }
            } catch (BeansException ex) {
                // should not happen
            }
        }
        return emptyStore;
    }

    /**
     * Test if the given Store is represented by this function
     *
     * @param value Name of the stored item
     * @param type type of the store item
     * @return true: it is contained in function; false: it is not used by
     * function
     */
    private boolean functionContainsStore(String value, IdRefType type) {

        int multiplicity = 0;
        if (encounteredOrderStore.containsKey(value + type)) {
            multiplicity = encounteredOrderStore.get(value + type);
        }

        switch (type) {
            case input:
                if (containedInputsMultiplicity.containsKey(value)) {
                    if (containedInputsMultiplicity.get(value)
                            > multiplicity) {
                        return true;
                    }
                }
                break;
            case output:
                if (output != null && output.equals(value)) {
                    if (multiplicity == 0) {
                        return true;
                    }
                }
                break;
            case parameter:
                if (containedParametersMultiplicity.containsKey(value)) {
                    if (containedParametersMultiplicity.get(value)
                            > multiplicity) {
                        return true;
                    }
                }
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dropdowns">
    /**
     * Get dropdown change events for inputs.
     */
    public void dropDownValueInputChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = selectedInputsWithId.getRowData();

        selectedInputs.set(currentTupel.getFirst(), value);
        isInputsEdited = true;
        renderUnsavedChanges = true;
    }

    public void addInputDropdown(int index) {
        selectedInputs.add(index + 1, "");
        isInputsEdited = true;
        renderUnsavedChanges = true;
    }

    public void removeInputDropdown(int index) {
        selectedInputs.remove(index);
        isInputsEdited = true;
        renderUnsavedChanges = true;
    }

    /**
     * Get dropdown change events for dependencies.
     */
    public void dropDownValueDependencyChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = selectedDependenciesWithId.getRowData();

        selectedDependencies.set(currentTupel.getFirst(), value);
        isDependenciesEdited = true;
        renderUnsavedChanges = true;
    }

    public void addDependencyDropdown(int index) {
        selectedDependencies.add(index + 1, "");
        isDependenciesEdited = true;
        renderUnsavedChanges = true;
    }

    public void removeDependencyDropdown(int index) {
        selectedDependencies.remove(index);
        isDependenciesEdited = true;
        renderUnsavedChanges = true;
    }
    
        /**
     * Get dropdown change events for output files.
     */
    public void dropDownValueOutputFileChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = selectedOutputFilesWithId.getRowData();

        selectedOutputFiles.set(currentTupel.getFirst(), value);
        isOutputFilesEdited = true;
        renderUnsavedChanges = true;
    }

    public void addOutputFileDropdown(int index) {
        selectedOutputFiles.add(index + 1, "");
        isOutputFilesEdited = true;
        renderUnsavedChanges = true;
    }

    public void removeOutputFileDropdown(int index) {
        selectedOutputFiles.remove(index);
        isOutputFilesEdited = true;
        renderUnsavedChanges = true;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="buttons for example/order/dependency">
    public String editOrder() {
        buildOrder();
        GeneralCallback<List<OrderStore>> callback =
                new GeneralCallback<List<OrderStore>>() {

                    @Override
                    public void setResult(List<OrderStore> result) {
                        renderUnsavedChanges = true;
                        order.clear();
                        order.addAll(result);
                    }
                };
        orderBean.initOrderBean(callback, order);
        return "order.xhtml?faces-redirect=true";
    }

    public String editExample() {
        buildExamples();

        removeEmptySelected();

        if (output == null) {
            output = "";
        }
        if (paramGroup == null) {
            paramGroup = "";
        }

        renderUnsavedChanges = true;
        
        return "example.xhtml?faces-redirect=true";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="loading of function">
    private Tfunction loadFunction(String name) {
        // reset current data
        resetAll();
        //try loading TparamGroupObject
        Tfunction function;
        try {
            function = functionManager.getFunctionByName(name);
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openFunctionError"),
                    ""));
            return null;
        }

        // set data
        this.name = function.getName().get(0).getValue();
        this.loadedFrom = name;
        this.renderLoadedFrom = true;

        this.shortDescription = function.getShortDescription().get(0).getValue();

        if (function.isSetDescription()) {
            this.description = (String) function.getDescription().get(0).
                    getContent().
                    get(0);
        }

        //set dropdown data
        for (Tfunction.Inputref ref : function.getInputref()) {
            TinputOutput input = (TinputOutput) ref.getRef();
            selectedInputs.add(input.getName().get(0).getValue());
        }
        for (Tfunction.Depref ref : function.getDepref()) {
            Tdependency dependency = (Tdependency) ref.getRef();
            selectedDependencies.add(dependency.getName().get(0).getValue());
        }
        for (Tfunction.Outputfileref ref : function.getOutputfileref()) {
            ToutputFile outputFile = (ToutputFile) ref.getRef();
            try {
                selectedOutputFiles.add(outputFileManager.getNameById(outputFile.getId()));
            } catch (BeansException ex) {
            }
        }
        refillSelected();

        if (function.isSetOutputref()) {
            TinputOutput output =
                    (TinputOutput) function.getOutputref().getRef();
            this.output = output.getName().get(0).getValue();
        }

        if (function.isSetParamGroup()) {
            TparamGroup group = function.getParamGroup();
            try {
                this.paramGroup = parameterGroupManager.getParameterGroupRefByFullId(group.getId());
            } catch (BeansException ex) {
            }
        }

        // create order
        ParamAndInputOutputOrder orders =
                function.getParamAndInputOutputOrder();
        for (JAXBElement<?> element : orders.getReferenceOrAdditionalString()) {
            if (element.getValue() instanceof TinputOutput) {
                TinputOutput inout = (TinputOutput) element.getValue();
                String inoutName = inout.getName().get(0).getValue();
                if (inout.getId().startsWith(InputOutputBuilder.getID_BASE_TYPE_OUTPUT())) {
                    // this is an output
                    OrderStore newOrder = new OrderStore(inoutName,
                            IdRefType.output, false);
                    order.add(newOrder);
                } else {
                    // this is an input
                    OrderStore newOrder = new OrderStore(inoutName,
                            IdRefType.input, false);
                    order.add(newOrder);
                }
            } else if (element.getValue() instanceof Tparam) {
                Tparam param = (Tparam) element.getValue();
                OrderStore newOrder = new OrderStore(param.getName().get(0).
                        getValue(), IdRefType.parameter, false);
                order.add(newOrder);
            } else if (element.getValue() instanceof TenumParam) {
                TenumParam param = (TenumParam) element.getValue();
                OrderStore newOrder = new OrderStore(param.getName().get(0).
                        getValue(), IdRefType.parameter, false);
                order.add(newOrder);
            } else if (element.getValue() instanceof String) {
                OrderStore newOrder = new OrderStore((String) element.getValue(),
                        IdRefType.none, true);
                order.add(newOrder);
            }
        }

        // create examples
        for (Texample example : function.getExample()) {
            List<ExampleStore> newExampleStoreList =
                    new ArrayList<ExampleStore>();
            boolean valid = true;
            for (Texample.Prop prop : example.getProp()) {

                String id = prop.getIdref();
                ExampleStore newStore = null;
                try {
                    String propname = inputManager.getNameforId(id);
                    // this is an input
                    TinputOutput in = inputManager.getInputByName(propname);
                    Tupel<Boolean, ExampleInputStore> tupel =
                            inputToExampleStore(in, prop.getValue());
                    if (valid) {
                        valid = tupel.getFirst();
                    }
                    newStore = tupel.getSecond();
                } catch (BeansException ex) {
                    try {
                        // this is not an input
                        String propname = parameterManager.getNameforId(id);
                        // this is a parameter
                        Object param = parameterManager.getParameterByName(
                                propname);

                        if (param instanceof Tparam) {
                            Tupel<Boolean, ExampleParameterStore> tupel =
                                    parameterToExampleStore((Tparam) param,
                                    prop.getValue(), true);
                            if (valid) {
                                valid = tupel.getFirst();
                            }
                            newStore = tupel.getSecond();
                        } else if (param instanceof TenumParam) {
                            Tupel<Boolean, ExampleParameterEnumStore> tupel =
                                    parameterEnumToExampleStore(
                                    (TenumParam) param,
                                    prop.getValue(), true);
                            if (valid) {
                                valid = tupel.getFirst();
                            }
                            newStore = tupel.getSecond();
                        }
                    } catch (BeansException ex1) {
                        // this is also not a parameter
                        // should not happen
                    }
                }
                if (newStore != null) {
                    newExampleStoreList.add(newStore);
                }
            }
            String exampleName = example.getName().get(0).getValue();

            //Add default string if no description is there.
            String exampleDescription = "";
            if (!example.getDescription().isEmpty()) {
                exampleDescription =
                        example.getDescription().get(0).getValue();
            }
            try {
                exampleManager.addExample(new Example(exampleName,
                        exampleDescription, newExampleStoreList, valid, false));
            } catch (BeansException ex) {
                // should not happen here, but exception is needed
            }
        }
        // set the dependencyValid Value in all examples.
        buildOrder();
        buildExamples();

        renderUnsavedChanges = false;
        
        return function;
    }

    /**
     * Trys to load the current function and returns if examples are valid by
     * dependency.
     *
     * @param name
     * @param only calculate dependency (valid always false)
     * @return
     */
    public Tupel<Boolean, Boolean> loadAndTestFunctionExamples(String name,
            boolean onlyDep) {

        // save old data
        String loadedFrom = this.loadedFrom;
        boolean renderLoadedFrom = this.renderLoadedFrom;
        String nameSave = this.name;
        String shortDescription = this.shortDescription;
        String description = this.description;
        String output = this.output;
        String paramGroup = this.paramGroup;
        List<String> selectedInputs = this.selectedInputs;
        List<String> selectedDependencies = this.selectedDependencies;
        ExampleManager exampleManager = this.exampleManager;
        List<OrderStore> order = this.order;
        List<String> outputFiles = this.selectedOutputFiles;

        Tfunction function = loadFunction(name);

        if (function == null) {
            return new Tupel<Boolean, Boolean>(false, false);
        }

        removeEmptySelected();

        boolean valid = false;
        if (!onlyDep) {
            valid = validate();
        }
        boolean depValid = validateDependency(function);

        // reset old data
        this.loadedFrom = loadedFrom;
        this.renderLoadedFrom = renderLoadedFrom;
        this.name = nameSave;
        this.shortDescription = shortDescription;
        this.description = description;
        this.output = output;
        this.paramGroup = paramGroup;
        this.selectedInputs = selectedInputs;
        this.isInputsEdited = true;
        this.selectedDependencies = selectedDependencies;
        this.isDependenciesEdited = true;
        this.exampleManager = exampleManager;
        this.order = order;
        this.selectedOutputFiles = outputFiles;

        return new Tupel<Boolean, Boolean>(valid, depValid);
    }

    /**
     * Trys to load the current function and changes the examples and order
     * to something valid.
     * Should only be used in loadxml.
     *
     * @param name
     * @return
     */
    public boolean loadAndResetOrderAndExamples(String name) {

        // save old data
        String loadedFrom = this.loadedFrom;
        boolean renderLoadedFrom = this.renderLoadedFrom;
        String nameSave = this.name;
        String shortDescription = this.shortDescription;
        String description = this.description;
        String output = this.output;
        String paramGroup = this.paramGroup;
        List<String> selectedInputs = this.selectedInputs;
        List<String> selectedDependencies = this.selectedDependencies;
        ExampleManager exampleManager = this.exampleManager;
        List<OrderStore> order = this.order;
        List<String> outputFiles = this.selectedOutputFiles;

        Tfunction function = loadFunction(name);

        if (function == null) {
            return false;
        }
        
        if (this.output == null) {
            this.output = "";
        }
        if (this.paramGroup == null) {
            this.paramGroup = "";
        }

        Tfunction newfunction = FunctionBuilder.createFunction(this.name,
                this.shortDescription, this.description,
                this.selectedInputs, this.output, this.selectedOutputFiles,
                this.selectedDependencies,
                this.paramGroup, this.order, this.exampleManager.getValues(),
                BasicBeanData.StandardLanguage);
        
        try {
           functionManager.editFunction(name, newfunction, false, false);
                // try editing
         } catch (BeansException ex) {
             return false;
         }

        // reset old data
        this.loadedFrom = loadedFrom;
        this.renderLoadedFrom = renderLoadedFrom;
        this.name = nameSave;
        this.shortDescription = shortDescription;
        this.description = description;
        this.output = output;
        this.paramGroup = paramGroup;
        this.selectedInputs = selectedInputs;
        this.isInputsEdited = true;
        this.selectedDependencies = selectedDependencies;
        this.isDependenciesEdited = true;
        this.exampleManager = exampleManager;
        this.order = order;
        this.selectedOutputFiles = outputFiles;
        
        return true;
    }

    /**
     * Takes in a parameter of type Tparam and returns corresponding
     * ExampleParameterStore with validation boolean
     *
     * @param parame parameter to return
     * @param value to test
     * @param validate is validation needed? (nor for new created params)
     * @return Tupel validated, ExampleParameterStore
     */
    private Tupel<Boolean, ExampleParameterStore> parameterToExampleStore(
            Tparam param, String value, boolean validate) {

        Tupel<Boolean, ExampleParameterStore> newTupel =
                new Tupel<Boolean, ExampleParameterStore>();

        if (!validate) {
            newTupel.setFirst(false);
        }
        String name = param.getName().get(0).getValue();

        String defaultValue = "";
        if (param.isSetDefaultValue()) {
            defaultValue = param.getDefaultValue();
        }

        // comment this if empty parameters are not allowed
        if(value.isEmpty()){
            newTupel.setFirst(true);
            validate=false;
        }
        
        switch (param.getType()) {
            case BOOLEAN:
                newTupel.setSecond(new ExampleParameterStore(name, value,
                        PrimitiveType.BOOLEAN, defaultValue));
                if (validate) {
                    newTupel.setFirst(ParameterValidator.validateBoolean(value));
                }
                break;
            case DATETIME:
                newTupel.setSecond(new ExampleParameterStore(name, value,
                        PrimitiveType.DATETIME, defaultValue));
                if (validate) {
                    newTupel.setFirst(ParameterValidator.validateDateTime(value));
                }
                break;
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
                newTupel.setSecond(
                        new ExampleParameterStore(name, value, regexp,
                        PrimitiveType.STRING, minLength, maxLength, defaultValue));
                if (validate) {
                    newTupel.setFirst(ParameterValidator.validateString(value,
                            minLength,
                            maxLength, regexp));
                }
                break;
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
                newTupel.setSecond(new ExampleParameterStore(name, value,
                        PrimitiveType.FLOAT, min, max, includeMax, includeMin,
                        defaultValue));
                if (validate) {
                    newTupel.setFirst(ParameterValidator.validateFloat(value,
                            min,
                            includeMin, max, includeMax));
                }
                break;
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
                if (validate) {
                    newTupel.setFirst(ParameterValidator.validateInt(value,
                            minInt,
                            includeMin, maxInt, includeMax));
                }
                newTupel.setSecond(new ExampleParameterStore(name, value,
                        PrimitiveType.INT, minInt, maxInt, includeMax,
                        includeMin, defaultValue));
                break;
        }
        return newTupel;
    }

    /**
     * Takes in a parameter of type TenumParam and returns corresponding
     * ExampleParameterEnumStore with validation boolean
     *
     * @param parame parameter to return
     * @param value to test
     * @param validate is validation needed? (not for new created params)
     * @return Tupel validated, ExampleParameterEnumStore
     */
    private Tupel<Boolean, ExampleParameterEnumStore> parameterEnumToExampleStore(
            TenumParam param,
            String value, boolean validate) {

        Tupel<Boolean, ExampleParameterEnumStore> newTupel = new Tupel<>();

        String defaultValue = "";

        
        
        int minOccur = 1;
        if (param.isSetMinoccurs()) {
            minOccur = param.getMinoccurs();
        }
        int maxOccur = 1;
        if (param.isSetMaxoccurs()) {
            maxOccur = param.getMaxoccurs();
        }
        String separator = param.getSeparator();
        if (separator == null || separator.isEmpty()) {
            //Append default separator if no separator is there.
            separator = ",";
        }
        List<String> values = new ArrayList<>();
        List<Tupel<String, String>> enumValues = new ArrayList<>();
        for (TenumValue enumVal : param.getValues()) {
            values.add(enumVal.getKey()); // JK : use the key instead of the value for checks when using enum param
            String valueNameValue = enumVal.getName().get(0).getValue();
            valueNameValue += " / " + enumVal.getValue();
            enumValues.add(new Tupel<>(enumVal.getKey(),
                    valueNameValue));
            if (enumVal.isDefaultValue()) {
                if (!defaultValue.isEmpty()) {
                    defaultValue += separator;
                }
                defaultValue += enumVal.getKey();
            }
        }


        String name = param.getName().get(0).getValue();
        newTupel.setSecond(new ExampleParameterEnumStore(name, value, enumValues,
                separator, minOccur, maxOccur, defaultValue));

        if (validate) {
            // comment first 3 lines if empty parameters are not allowed
            if(value.isEmpty()){
                newTupel.setFirst(true);
            } else {
            newTupel.setFirst(ParameterValidator.validateEnum(value, values,
                    minOccur,
                    maxOccur, separator));
            }
        } else {
            newTupel.setFirst(false);
        }
        
        return newTupel;
    }

    /**
     * Converts give input to ExampleInputStore
     *
     * @param in TinputOutput to convert
     * @param value value to set
     * @return
     * @return Tupel validated, ExampleInputStore
     */
    private Tupel<Boolean, ExampleInputStore> inputToExampleStore(
            TinputOutput in,
            String value) {

        Tupel<Boolean, ExampleInputStore> newTupel = new Tupel<>();
        
        newTupel.setFirst(true);
        // use this line instaed if empty inputs are not allowed
        //newTupel.setFirst(value.equals(""));

        String name = in.getName().get(0).getValue();
        newTupel.setSecond(new ExampleInputStore(name, value, HandlingType.StringToEnum(in.getHandling())));

        return newTupel;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="box editing buttons">
    public void newFunction() {
        resetAll();
        refillSelected();
    }

    public void editFunction(String name) {
        loadFunction(name);
    }

    public void removeFunction(String name) {
        try {
            functionManager.removeFunctionByName(name);
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
            reload();
        } catch (BeansException ex) {
        }
    }

    public String newParameterGroup() {
        parameterGroupBean.newParameterGroup();
        return "parameterGroup.xhtml?faces-redirect=true";
    }

    public String editParameterGroup(String name) {
        parameterGroupBean.editParameterGroup(name);
        return "parameterGroup.xhtml?faces-redirect=true";
    }

    public void removeParameterGroup(String name) {
        // reload is called automatically
        parameterGroupBean.removeParameterGroup(name);
    }

    public String newInput() {
        inputBean.newInput();
        return "input.xhtml?faces-redirect=true";
    }

    public String editInput(String name) {
        inputBean.editInput(name);
        return "input.xhtml?faces-redirect=true";
    }

    public void removeInput(String name) {
        // reload is called automatically
        inputBean.removeInput(name);
    }

    public String newOutput() {
        outputBean.newOutput();
        return "output.xhtml?faces-redirect=true";
    }

    public String editOutput(String name) {
        outputBean.editOutput(name);
        return "output.xhtml?faces-redirect=true";
    }

    public void removeOutput(String name) {
        // reload is called automatically
        outputBean.removeOutput(name);
    }

    public String newDependency() {
        dependencyBean.newDependency();
        return "dependency.xhtml?faces-redirect=true";
    }

    public String editDependency(String name) {
        dependencyBean.editDependency(name);
        return "dependency.xhtml?faces-redirect=true";
    }

    public void removeDependency(String name) {
        // reload is called automatically
        dependencyBean.removeDependency(name);
    }
    
    public String newOutputFile() {
        outputFileBean.newOutputFile();
        return "additionalOutput.xhtml?faces-redirect=true";
    }

    public String editOutputFile(String name) {
        outputFileBean.editOutputFile(name);
        return "additionalOutput.xhtml?faces-redirect=true";
    }

    public void removeOutputFile(String name) {
        // reload is called automatically
        outputFileBean.removeOutputFile(name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reload / preRender">
    public void reload() {
        getAvailableData();
        buildAll();
    }

    public void preRender() {
        getAvailableData();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="validating, saving">
    /**
     * Test if the name is set.
     *
     * @return true: name is set; false: no name there
     */
    private boolean validateName() {
        if (name.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            return false;
        }
        return true;
    }

    /**
     * Validates the function and add error messages to context.
     *
     * @return true: everything is OK; false: something is wrong
     */
    private boolean validate() {
        boolean ret = true;

        if (shortDescription.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("shortDescriptionError"), null));
            ret = false;
        }

        if (output == null || output.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("specifyOutputError"),null));
            ret = false;
        }

        if (!exampleManager.isValid()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("invalidExampleError"), null));
            ret = false;
        }
        return ret;
    }

    /**
     * Validate if the dependencies for this function are correct.
     *
     * @param function function to test
     * @return true: dependencies are valid; false: dependencies are not valid
     */
    private boolean validateDependency(Tfunction function) {
        boolean ret = true;
        for (String dependencyName : selectedDependencies) {
            Tdependency dependency;
            try {
                dependency = dependencyManager.getParameterDependencyByName(
                        dependencyName);

                ParameterDependencyTester tester = new ParameterDependencyTester();

                // The dependency is valid if no error was returned in the list (list is empty)
                if (!tester.testDependency(function, dependency)) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("dependencyUnvalidError")
                            + ": " + dependencyName, ""));
                    ret = false;
                }

            } catch (BeansException ex) {
            }

        }

        if (!exampleManager.isDependencyValid()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("invalidExampleDepError"), ""));
            ret = false;
        }

        return ret;
    }

    /**
     * Clears all messages from corrent context.
     */
    private void clearMessages() {
        Iterator iter = FacesContext.getCurrentInstance().getMessages();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    /**
     * Saves the function with or without errors. Add callbackParams for showing
     * of PopUp. Add further messages to context.
     *
     * @param isReturn is this called from save (false) order saveReturn (true)
     * @return true: was saved; false: was not saved
     */
    private boolean saveAll(boolean isReturn) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (!validateName()) {
            context.addCallbackParam("show", true);
            context.addCallbackParam("errors", true);
            context.addCallbackParam("saved", false);
            context.addCallbackParam("returns", false);
            return false;
        }

        if (output == null) {
            output = "";
        }
        if (paramGroup == null) {
            paramGroup = "";
        }
        
        buildExamples();
        buildOrder();

        removeEmptySelected();

        Tfunction newfunction = FunctionBuilder.createFunction(name,
                shortDescription, description,
                selectedInputs, output, selectedOutputFiles, selectedDependencies,
                paramGroup, order, exampleManager.getValues(),
                BasicBeanData.StandardLanguage);


        boolean valid = validate();
        boolean depValid = validateDependency(newfunction);

        refillSelected();
        isInputsEdited = true;
        isOutputFilesEdited = true;

        if (loadedFrom.isEmpty()) { // not loaded
            try {
                functionManager.addFunction(newfunction, valid, depValid);
            } catch (BeansException ex) {  // could not be added
                clearMessages();
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "functionAlreadyExistsError"),
                            null));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("couldNotSave"), null));
                }
                context.addCallbackParam("show", true);
                context.addCallbackParam("errors", true);
                context.addCallbackParam("saved", false);
                context.addCallbackParam("returns", false);
                return false;
            }
        } else { // function is loaded
            try {
                functionManager.editFunction(loadedFrom, newfunction, valid,
                        depValid); // try editing
            } catch (BeansException ex) {//could not edit
                clearMessages();
                if (ex.getExceptionType()
                        == BeansExceptionTypes.AlreadyContainsName) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "functionAlreadyExistsError"),
                            null));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("couldNotSave"), null));
                }
                context.addCallbackParam("show", true);
                context.addCallbackParam("errors", true);
                context.addCallbackParam("saved", false);
                context.addCallbackParam("returns", false);
                return false;
            }
        }
        if (valid & depValid) {
            if (isReturn) {
                context.addCallbackParam("show", false);
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                        PropertyManager.getProperty("saveSuccesful"), null));
                context.addCallbackParam("show", true);
            }
            context.addCallbackParam("errors", false);
            context.addCallbackParam("saved", true);
            context.addCallbackParam("returns", isReturn);
        } else {
            context.addCallbackParam("show", true);
            context.addCallbackParam("errors", true);
            context.addCallbackParam("saved", true);
            context.addCallbackParam("returns", isReturn);
        }
        
        renderUnsavedChanges = false;
        return true;
    }

    /**
     * Remove all empty selected inputs and dependencies.
     */
    private void removeEmptySelected() {
        Iterator<String> inputIterator = selectedInputs.iterator();
        while (inputIterator.hasNext()) {
            if (inputIterator.next().isEmpty()) {
                inputIterator.remove();
            }
        }
        
        Iterator<String> outputFileIterator = selectedOutputFiles.iterator();
        while (outputFileIterator.hasNext()) {
            if (outputFileIterator.next().isEmpty()) {
                outputFileIterator.remove();
            }
        }

        Iterator<String> dependencyIterator = selectedDependencies.iterator();
        while (dependencyIterator.hasNext()) {
            if (dependencyIterator.next().isEmpty()) {
                dependencyIterator.remove();
            }
        }
    }

    public void save() {
        if (saveAll(false)) {
            loadedFrom = name;
            renderLoadedFrom = true;
            getAvailableData();
        }
    }

    public void saveReturn() {
        // did it save
        if (saveAll(true)) {
            loadedFrom = name;
            renderLoadedFrom = true;
            getAvailableData();
            // if yes did an error occur?
            RequestContext context = RequestContext.getCurrentInstance();
            if (!(Boolean) context.getCallbackParams().get("errors")) {

                // no error occured
                // redirect from javax context
                FacesContext ctx = FacesContext.getCurrentInstance();
                ExternalContext extContext = ctx.getExternalContext();
                String url = extContext.encodeActionURL(ctx.getApplication().
                        getViewHandler().getActionURL(ctx,
                        "/functionSelection.xhtml"));

                try {
                    extContext.redirect(url);
                } catch (IOException ioe) {
                    // ignore
                }
            }
        }
    }

    public String cancel() {
        resetAll();
        refillSelected();
        return "functionSelection.xhtml?faces-redirect=true";
    }

    public String returnToPrev() {
        resetAll();
        refillSelected();
        return "functionSelection.xhtml?faces-redirect=true";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="change calls">
    public void reloadExamplesParameter(String name) {
        for (Example example : exampleManager.getValues()) {
            for (ExampleStore rawstore : example.getExamples()) {
                if (rawstore.getType() == IdRefType.parameter && rawstore.getName().
                        equals(name)) {
                    boolean correct = true;
                    if (rawstore instanceof ExampleParameterStore) {
                        ExampleParameterStore store =
                                (ExampleParameterStore) rawstore;
                        switch (store.getPrimitive()) {
                            case BOOLEAN:
                                if (!ParameterValidator.validateBoolean(store.getValue())) {
                                    correct = false;
                                }
                                break;
                            case DATETIME:
                                if (!ParameterValidator.validateDateTime(store.getValue())) {
                                    correct = false;
                                }
                                break;
                            case FLOAT:
                                if (!ParameterValidator.validateFloat(store.getValue(),
                                        store.getMin(), store.isIncludeMin(),
                                        store.getMax(),
                                        store.isIncludeMax())) {
                                    correct = false;
                                }
                                break;
                            case INT:
                                if (!ParameterValidator.validateInt(store.getValue(),
                                        (int) store.getMin(),
                                        store.isIncludeMin(),
                                        (int) store.getMax(),
                                        store.isIncludeMax())) {
                                    correct = false;
                                }
                                break;
                            case STRING:
                                if (!ParameterValidator.validateString(store.getValue(),
                                        store.getMinLength(),
                                        store.getMaxLength(), store.getRegexp())) {
                                    correct = false;
                                }
                                break;
                        }
                    } else if (rawstore instanceof ExampleParameterEnumStore) {
                        ExampleParameterEnumStore store =
                                (ExampleParameterEnumStore) rawstore;
                        List<String> keys = new ArrayList<String>();
                        for (Tupel<String, String> tupel : store.getEnumValues()) {
                            keys.add(tupel.getFirst());
                        }
                        ParameterValidator.validateEnum(store.getValue(), keys,
                                store.getMinOccur(),
                                store.getMaxOccur(), store.getSeparator());
                    }
                    if (!correct) {
                        rawstore.setValue("");
                        example.setValid(false);
                    }
                }
            }
        }
    }

    public void reloadExamplesInput(String name) {
        for (Example example : exampleManager.getValues()) {
            for (ExampleStore store : example.getExamples()) {
                if (store.getType() == IdRefType.input && store.getName().equals(
                        name)) {
                    store.setValue("");
                    example.setValid(false);
                    if (store instanceof ExampleInputStore) {
                        // should always happen
                        ((ExampleInputStore) store).setNonbase64("");
                    }
                }
            }
        }
    }

    public void rebuildExamples(){
        buildExamples();
    }
    
    public void changedParamGroup(String oldname, String newname) {

        if (paramGroup != null && paramGroup.equals(oldname)) {
            paramGroup = newname;
            reload();
        }
    }

    public void changedInput(String oldname, String newname) {
        getAvailableData();
        isInputsEdited = true;
        ListIterator<String> inputIterator = selectedInputs.listIterator();
        while (inputIterator.hasNext()) {
            String str = inputIterator.next();
            if (str.equals(oldname)) {
                inputIterator.remove();
                inputIterator.add(newname);
            }
        }
        changeNameInExample(IdRefType.input, oldname, newname);
        changeNameInOrder(IdRefType.input, oldname, newname);
    }
    
    public void changedOutputFile(String oldname, String newname) {
        getAvailableData();
        isOutputFilesEdited = true;
        ListIterator<String> outputFilesIterator = selectedOutputFiles.listIterator();
        while (outputFilesIterator.hasNext()) {
            String str = outputFilesIterator.next();
            if (str.equals(oldname)) {
                outputFilesIterator.remove();
                outputFilesIterator.add(newname);
            }
        }
    }

    public void changedDependency(String oldname, String newname) {
        getAvailableData();
        isDependenciesEdited = true;
        ListIterator<String> depIterator = selectedDependencies.listIterator();
        while (depIterator.hasNext()) {
            String str = depIterator.next();
            if (str.equals(oldname)) {
                depIterator.remove();
                depIterator.add(newname);
            }
        }
    }

    public void changedOutput(String oldname, String newname) {
        getAvailableData();
        if (output.equals(oldname)) {
            output = newname;
        }
        changeNameInOrder(IdRefType.output, oldname, newname);
    }

    public void changedParameter(String oldname, String newname) {
        changeNameInExample(IdRefType.parameter, oldname, newname);
        changeNameInOrder(IdRefType.parameter, oldname, newname);
    }

    private void changeNameInExample(IdRefType type, String oldname,
            String newname) {
        for (Example example : exampleManager.getValues()) {
            ListIterator<ExampleStore> storeIterator = example.getExamples().
                    listIterator();
            while (storeIterator.hasNext()) {
                ExampleStore store = storeIterator.next();

                if (store.getType() == type && store.getName().equals(oldname)) {
                    String value = store.getValue();
                    ExampleStore newStore = null;
                    switch (type) {
                        case input:
                            try {
                                TinputOutput in = inputManager.getInputByName(
                                        newname);
                                newStore = inputToExampleStore(in, value).
                                        getSecond();
                            } catch (BeansException ex) {
                            }
                            break;
                        case parameter:
                            try {
                                Object param = parameterManager.getParameterByName(
                                        newname);
                                if (param instanceof Tparam) {
                                    newStore = parameterToExampleStore(
                                            (Tparam) param,
                                            value, false).getSecond();
                                } else if (param instanceof TenumParam) {
                                    newStore =
                                            parameterEnumToExampleStore(
                                            (TenumParam) param,
                                            value, false).getSecond();
                                }
                            } catch (BeansException ex) {
                            }
                    }
                    storeIterator.remove();
                    storeIterator.add(newStore);
                }
            }
        }
    }

    private void changeNameInOrder(IdRefType type, String oldname,
            String newname) {

        for (OrderStore store : order) {
            if (store.getType() == type && !store.isIsString()
                    && store.getValue().equals(oldname)) {
                store.setValue(newname);
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="getter, setter">
    
    public Tfunction getCurrentFunctionAsObject(){
        
        if (output == null) {
            output = "";
        }
        if (paramGroup == null) {
            paramGroup = "";
        }
        
         Tfunction newfunction = FunctionBuilder.createFunction(name,
            shortDescription, description,
            selectedInputs, output, selectedOutputFiles, selectedDependencies,
            paramGroup, order, exampleManager.getValues(),
            BasicBeanData.StandardLanguage);
         return newfunction;
    }
    
    public DataModel<Tupel<Integer, String>> getSelectedInputsWithId() {
        if (isInputsEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedInputs) {
                ret.add(new Tupel(i, str));
                i++;
            }

            selectedInputsWithId =
                    new ListDataModel<Tupel<Integer, String>>(ret);
        }
        isInputsEdited = false;

        return selectedInputsWithId;
    }
    
        public DataModel<Tupel<Integer, String>> getSelectedOutputFilesWithId() {
        if (isOutputFilesEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedOutputFiles) {
                ret.add(new Tupel(i, str));
                i++;
            }

            selectedOutputFilesWithId =
                    new ListDataModel<Tupel<Integer, String>>(ret);
        }
        isOutputFilesEdited = false;

        return selectedOutputFilesWithId;
    }

    public DataModel<Tupel<Integer, String>> getSelectedDependenciesWithId() {
        if (isDependenciesEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedDependencies) {
                ret.add(new Tupel(i, str));
                i++;
            }

            selectedDependenciesWithId =
                    new ListDataModel<Tupel<Integer, String>>(ret);
        }
        isDependenciesEdited = false;

        return selectedDependenciesWithId;
    }

    public boolean isShowInputRemove() {
        return selectedInputs.size() > 1;
    }
    
    public boolean isShowOutputFileRemove() {
        return selectedOutputFiles.size() > 1;
    }

    public boolean isShowDependencyRemove() {
        return selectedDependencies.size() > 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getParamGroup() {
        return paramGroup;
    }

    public void setParamGroup(String paramGroup) {
        this.paramGroup = paramGroup;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public List<Tupel<String, Boolean>> getFunctionNameList() {
        return functionNameList;
    }

    public boolean isFunctionsEmpty() {
        return functionsEmpty;
    }

    public List<String> getInputNameList() {
        return inputNameList;
    }

    public boolean isInputsEmpty() {
        return inputsEmpty;
    }
    
    public List<String> getOutputFileNameList() {
        return outputFileNameList;
    }

    public boolean isOutputFilesEmpty() {
        return outputFilesEmpty;
    }

    public List<String> getOutputNameList() {
        return outputNameList;
    }

    public boolean isOutputsEmpty() {
        return outputsEmpty;
    }

    public List<String> getParameterGroupNameList() {
        return parameterGroupNameList;
    }

    public boolean isParameterGroupsEmpty() {
        return parameterGroupsEmpty;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    public boolean isDependenciesEmpty() {
        return dependenciesEmpty;
    }

    public List<String> getDependencyNamesList() {
        return dependencyNamesList;
    }
    
    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }

    public void setRenderUnsavedChanges(boolean renderUnsavedChanges) {
        this.renderUnsavedChanges = renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }
    
    @Override
    public void setDescription(String description) {
        this.description = description;
        renderUnsavedChanges = true;
    }
    
    
    // </editor-fold>

}
