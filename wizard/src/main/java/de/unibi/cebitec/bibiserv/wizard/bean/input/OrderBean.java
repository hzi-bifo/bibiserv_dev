package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.GeneralCallback;
import de.unibi.cebitec.bibiserv.wizard.bean.OrderStore;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.HandlingType;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.IdRefType;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.InputManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.OutputManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Tparam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * This is used to edit the data of a function in order.xhtml
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class OrderBean {

    private List<OrderStore> orderList;
    private List<Tupel<Integer, OrderStore>> orderListWithId;
    private boolean isOrderEdited;
    private int newSelectedIndex;
    private Tupel<Integer, OrderStore> selectedOrder;
    private GeneralCallback<List<OrderStore>> callback;
    private Map<OrderStore, String> exampleStringMap;
    private String exampleValue;
    //Manager Beans
    private ParameterManager parameterManager = null;
    private InputManager inputManager = null;
    private OutputManager outputManager = null;
    private boolean renderUnsavedChanges;
    private String position;

    public OrderBean() {
        isOrderEdited = true;
        orderList = new ArrayList<OrderStore>();
        orderListWithId = new ArrayList<Tupel<Integer, OrderStore>>();
        isOrderEdited = true;
         position = "";
    }

    public void initOrderBean(GeneralCallback<List<OrderStore>> callback,
            List<OrderStore> orderList) {
        this.callback = callback;
        // true copy of orderlist, or we will be editing driectly in list,
        this.orderList = new ArrayList<OrderStore>();
        this.orderList.addAll(orderList);
        if (this.orderList.isEmpty()) {
            this.orderList.add(new OrderStore("", IdRefType.none, true));
        }
        isOrderEdited = true;
        buildExampleStringMap();
        getOrderListWithId();
        newSelectedIndex = 0;
        selectedOrder = orderListWithId.get(0);
        changeExample();
        renderUnsavedChanges = false;
        calcPostionString();
    }

    private void buildExampleStringMap() {
        // retrieve current Manager beans
        if (parameterManager == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            parameterManager = (ParameterManager) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterManager}",
                    ParameterManager.class);
        }
        if (inputManager == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            inputManager = (InputManager) context.getApplication().
                    evaluateExpressionGet(context, "#{inputManager}",
                    InputManager.class);
        }
        if (outputManager == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            outputManager = (OutputManager) context.getApplication().
                    evaluateExpressionGet(context, "#{outputManager}",
                    OutputManager.class);
        }

        // build the map
        exampleStringMap = new HashMap<OrderStore, String>();
        for (OrderStore store : orderList) {
            if (!store.isIsString()) {
                switch (store.getType()) {
                    case input:
                        try {
                            TinputOutput input = inputManager.getInputByName(
                                    store.getValue());
                            exampleStringMap.put(store, getInputOutputExample(
                                    input, IdRefType.input));
                        } catch (BeansException ex) {
                            // should not happen
                        }
                        break;
                    case output:
                        try {
                            TinputOutput input = outputManager.getOutputByName(
                                    store.getValue());
                            exampleStringMap.put(store, getInputOutputExample(
                                    input, IdRefType.output));
                        } catch (BeansException ex) {
                            // should not happen
                        }
                        break;
                    case parameter:
                        try {
                            Object param = parameterManager.getParameterByName(
                                    store.getValue());
                            exampleStringMap.put(store, getParameterExample(
                                    param));
                        } catch (BeansException ex) {
                            // should not happen
                        }
                        break;
                }
            }
        }
    }

    private void changeExample() {
        exampleValue = "";
        for (OrderStore store : orderList) {
            if (store.isIsString()) {
                exampleValue += store.getValue();
            } else {
                exampleValue += exampleStringMap.get(store);
            }
        }
    }

    public void up() {
        if (selectedOrder != null && selectedOrder.getFirst() > 0) {
            int index = selectedOrder.getFirst();
            Collections.swap(orderList, index, index - 1);
            newSelectedIndex = index - 1;
            isOrderEdited = true;
            changeExample();
            renderUnsavedChanges = true;
        }
    }

    public void down() {
        if (selectedOrder != null && selectedOrder.getFirst() < orderList.size()
                - 1) {
            int index = selectedOrder.getFirst();
            Collections.swap(orderList, index, index + 1);
            newSelectedIndex = index + 1;
            isOrderEdited = true;
            changeExample();
            renderUnsavedChanges = true;
        }
    }

    public void add() {
        if (selectedOrder != null) {
            int index = selectedOrder.getFirst();
            orderList.add(index + 1, new OrderStore("", IdRefType.none, true));
            isOrderEdited = true;
            changeExample();
            renderUnsavedChanges = true;
        }
    }

    /**
     * Returns the list of all orderStore as tupel with index.
     * At list 1 Element in the list must be gauranteed!
     * One element must always be selected!
     * @return list of all orderStore as tupel with index
     */
    public List<Tupel<Integer, OrderStore>> getOrderListWithId() {
        if (isOrderEdited) {
            orderListWithId = new ArrayList<Tupel<Integer, OrderStore>>();

            int i = 0;
            for (OrderStore store : orderList) {
                orderListWithId.add(new Tupel(i, store));
                i++;
            }
            if (newSelectedIndex < orderListWithId.size() && newSelectedIndex>0) {
                selectedOrder = orderListWithId.get(newSelectedIndex);
            } else {
                selectedOrder = orderListWithId.get(0);
            }
        }
        isOrderEdited = false;
        return orderListWithId;
    }

    private void calcPostionString(){
        position = "";
        String unnamed = PropertyManager.getProperty("unnamed");
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            FunctionBean functionBean = (FunctionBean) context.getApplication().
                    evaluateExpressionGet(context, "#{functionBean}",
                    FunctionBean.class);
            if (functionBean.getName().isEmpty()) {
                position += unnamed;
            } else {
                position += functionBean.getName();
            }
        }
    }
    
    public Tupel<Integer, OrderStore> getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Tupel<Integer, OrderStore> selectedOrder) {
        this.selectedOrder = selectedOrder;
        this.newSelectedIndex = selectedOrder.getFirst();
    }

    public String getExampleValue() {
        return exampleValue;
    }

    public void setExampleValue(String exampleValue) {
        this.exampleValue = exampleValue;
    }

    public String cancel() {
        renderUnsavedChanges = false;
        return "function.xhtml?faces-redirect=true";
    }

    public void save() {
        callback.setResult(orderList);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                PropertyManager.getProperty("saveSuccesful"), ""));
         renderUnsavedChanges = false;
    }

    public String saveReturn() {
        callback.setResult(orderList);
        renderUnsavedChanges = false;
        return "function.xhtml?faces-redirect=true";
    }

    /**
     * Takes an TinputOutput and returns the String used for example.
     * @param inout object to build example String from
     * @return example String
     */
    private String getInputOutputExample(TinputOutput inout, IdRefType type) {

        String option = "";
        if (inout.isSetOption()) {
            option = inout.getOption();
        }

        if (inout.getHandling().equals(HandlingType.stdout.getValue())) {
            return "> " + option + "[" + PropertyManager.getProperty(
                    "fileResultCommandLine") + "]";
        } else if (inout.getHandling().equals(HandlingType.stdin.getValue())) {
            return "> " + option + "[" + PropertyManager.getProperty("fileCommandLine")
                    + "]";
        } else {
            if (type == IdRefType.input) {
                return option + "[" + PropertyManager.getProperty("fileCommandLine")
                        + "]";
            } else {

                return option + "[" + PropertyManager.getProperty(
                        "fileResultCommandLine") + "]";
            }
        }

    }

    /**
     * Takes a parameter of type Tparam or TenuParam and returns the String used
     * for example.
     * @param paramOb object to build example String from
     * @return example String
     */
    private String getParameterExample(Object paramOb) {

        if (paramOb instanceof TenumParam) {
            TenumParam param = (TenumParam) paramOb;

            String example = param.getValues().get(0).getValue();

            String option = "";
            if (param.isSetOption()) {
                option = param.getOption();
            }
            String prefix = "";
            if (param.isSetPrefix()) {
                prefix = param.getPrefix();
            }
            String suffix = "";
            if (param.isSetSuffix()) {
                suffix = param.getSuffix();
            }
            return option + prefix + example + suffix;

        } else if (paramOb instanceof Tparam) {
            Tparam param = (Tparam) paramOb;
            switch (param.getType()) {
                case FLOAT:
                    String exampleVal = "";
                    if (param.isSetDefaultValue()) {
                        param.getDefaultValue();
                    }
                    if (exampleVal.length() == 0 && param.isSetMin() && param.
                            getMin().isIncluded()) {
                        exampleVal = Float.toString(param.getMin().
                                getValue());
                    }
                    if (exampleVal.length() == 0 && param.isSetMax() && param.
                            getMax().isIncluded()) {
                        exampleVal = Float.toString(param.getMax().
                                getValue());
                    }
                    if (exampleVal.length() == 0) {
                        exampleVal = "[float]";
                    }
                    String option = "";
                    if (param.isSetOption()) {
                        option = param.getOption();
                    }
                    return option + exampleVal;
                case INT:
                    exampleVal = "";
                    if (param.isSetDefaultValue()) {
                        param.getDefaultValue();
                    }
                    if (exampleVal.length() == 0 && param.isSetMin() && param.
                            getMin().isIncluded()) {
                        exampleVal = Integer.toString((int) param.getMin().
                                getValue());
                    }
                    if (exampleVal.length() == 0 && param.isSetMax() && param.
                            getMax().isIncluded()) {
                        exampleVal = Integer.toString((int) param.getMax().
                                getValue());
                    }
                    if (exampleVal.length() == 0) {
                        exampleVal = "[int]";
                    }
                    option = "";
                    if (param.isSetOption()) {
                        option = param.getOption();
                    }
                    return option + exampleVal;
                case BOOLEAN:
                    option = "";
                    if (param.isSetOption()) {
                        option = param.getOption();
                    }
                    return option;
                case DATETIME:
                    option = "";
                    if (param.isSetOption()) {
                        option = param.getOption();
                    }
                    if (param.isSetDefaultValue()) {
                        return option + param.getDefaultValue();
                    }
                    return option + "[dateTime]";
                case STRING:
                    option = "";
                    if (param.isSetOption()) {
                        option = param.getOption();
                    }
                    if (param.isSetDefaultValue()) {
                        return option + param.getDefaultValue();
                    }
                    return option + "[string]";

            }
        }
        return "";
    }

    public void remove(int index) {
        orderList.remove(index);
        if(index<newSelectedIndex || index==orderListWithId.size()-1){
            newSelectedIndex--;
            selectedOrder = orderListWithId.get(newSelectedIndex);
        } 
        isOrderEdited = true;
        changeExample();
        renderUnsavedChanges = true;
    }

    public void calculateExample(){
        changeExample();
    }

    public boolean isShowRemove() {
        return orderList.size() > 1;
    }
    
    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }

    public String getPosition() {
        return position;
    }
    
    
}
