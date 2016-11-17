package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.DescriptionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.EnumValueStore;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.GuiElement;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.PrimitiveType;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ParameterValidator;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.TenumValue;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.Tprimitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 * This bean is used to manage user input from parameter.xhtml
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ParameterBean extends DescriptionBean {

    // Parameter Data
    private String name;
    private String shortDescription;
    private String option;
    private Tprimitive type;
    private String defaultValue;
    private String min;
    private String max;
    private String minLength;
    private String maxLength;
    private String regexp;
    private boolean intervalMin;
    private boolean intervalMax;
    private String prefix;
    private String suffix;
    private String separator;
    private String minOccur;
    private String maxOccur;
    private String enumType;
    private String guiString;
    private List<EnumValueStore> enumList;
    // possible Gui
    private SelectItem[] compatibleGuiElements;
    // hiding and showing
    private boolean renderTypeIntFloat;
    private boolean renderTypeString;
    private boolean renderTypeDateTime;
    private boolean renderTypeBoolean;
    private boolean renderTypeEnum;
    private String typeString;
    private boolean enumIndexChanged;
    // information for user
    private String exampleLabel;
    // Manager
    private ParameterManager manager;
    private String loadedFrom;
    private List<String> parameterNameList;
    private boolean parametersEmpty;
    
    // changes
    private boolean renderUnsavedChanges;
    

    public ParameterBean() {

        xhtml = "parameter.xhtml";

        FacesContext context = FacesContext.getCurrentInstance();
        manager = (ParameterManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterManager}",
                ParameterManager.class);

        resetAll();
        calculateExample();
        calculateCompatibleGuiElements();
    }

    public void newParameter() {
        resetAll();
        calcPostionString();
    }

    public void editParameter(String name) {
        loadParameter(name);
    }

    public void removeParameter(String name) {
        try {
            manager.removeParameterByName(name);
            //reset parameterList
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
            }
            parameterNameList = manager.getAllNames();
            parametersEmpty = manager.isEmpty();
        } catch (BeansException ex) {
            // should not happen when used right
        }
    }

    /**
     * Show and hide corresponding to dropdown.
     * @param e
     */
    public void typeChanged(ValueChangeEvent e) {
        String newValue = (String) e.getNewValue();

        renderTypeIntFloat = false;
        renderTypeString = false;
        renderTypeEnum = false;
        renderTypeDateTime = false;
        renderTypeBoolean = false;

        Object oldvalue = e.getOldValue();

        if (oldvalue != null
                && ((String) oldvalue).equals(PrimitiveType.BOOLEAN.getName())) {
            defaultValue = "";
        }
        typeString = newValue;

        if (newValue.equals(PrimitiveType.STRING.getName())) {
            type = Tprimitive.STRING;
            renderTypeString = true;
        } else if (newValue.equals(PrimitiveType.INT.getName())) {
            type = Tprimitive.INT;
            renderTypeIntFloat = true;
        } else if (newValue.equals(PrimitiveType.FLOAT.getName())) {
            type = Tprimitive.FLOAT;
            renderTypeIntFloat = true;
        } else if (newValue.equals(PrimitiveType.DATETIME.getName())) {
            type = Tprimitive.DATETIME;
            renderTypeDateTime = true;
        } else if (newValue.equals(PrimitiveType.BOOLEAN.getName())) {
            type = Tprimitive.BOOLEAN;
            renderTypeBoolean = true;
            defaultValue = "true";
        } else if (newValue.equals("enum")) {
            type = null;
            renderTypeEnum = true;
        }

        calculateCompatibleGuiElements();

        calculateExample();
    }
    
    public void calculateCompatibleGuiElements()
    {
        renderUnsavedChanges = true;
        // fill GuiElements anew
        List<String> possibleElements;
        if (renderTypeEnum) {
            int maxOccurInt = 1;
            if (ParameterValidator.isInteger(maxOccur)) {
                maxOccurInt = Integer.parseInt(maxOccur);
                if (maxOccurInt < 0) {
                    maxOccurInt = 1;
                }
            }
            possibleElements = GuiElement.getPossibleElementsEnum(maxOccurInt);
        } else {
            possibleElements = GuiElement.getPossibleElements(type);
        }

        compatibleGuiElements = new SelectItem[possibleElements.size()];
        int i = 0;
        for (String name : possibleElements) {
            compatibleGuiElements[i] = new SelectItem(name, name);
            i++;
        }
    }

    /**
     * This calculates the shown example.
     * @param e
     */
    public void calculateExample() {

        renderUnsavedChanges = true;
        
        if (type == null) {
            String exampleValue = enumList.get(0).getValue();
            if (exampleValue.length() == 0) {
                exampleValue = "[" + PrimitiveType.getLabelForName(enumType)
                        + "]";
            }
            exampleLabel = option + prefix + exampleValue + suffix;
        } else {
            switch (type) {
                case FLOAT:
                case INT:
                    String exampleValue = defaultValue;
                    if (exampleValue.length() == 0 && intervalMin) {
                        exampleValue = min;
                    }
                    if (exampleValue.length() == 0 && intervalMax) {
                        exampleValue = max;
                    }
                    if (exampleValue.length() == 0) {
                        exampleValue = "[" + PrimitiveType.getLabelForName(
                                typeString) + "]";
                    }
                    exampleLabel = option + exampleValue;
                    break;
                case BOOLEAN:
                    exampleLabel = option;
                    break;
                case DATETIME:
                case STRING:
                    String exampleValue2 = defaultValue;
                    if (exampleValue2.length() == 0) {
                        exampleValue2 = "[" + PrimitiveType.getLabelForName(
                                typeString) + "]";
                    }
                    exampleLabel = option + exampleValue2;
                    break;
            }
        }
    }

    public List<EnumValueStore> getEnumList() {

        if (enumIndexChanged) {
            // reindex everytime this is requested
            for (int i = 0; i < enumList.size(); i++) {
                enumList.get(i).setIndex(i);
            }
        }
        enumIndexChanged = false;

        return enumList;
    }

    public void save() {
        if (saveAll()) {
            loadedFrom = name;
        }
    }

    public void saveReturn() {

        if (saveAll()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ExternalContext extContext = ctx.getExternalContext();
            String url = extContext.encodeActionURL(ctx.getApplication().
                    getViewHandler().getActionURL(ctx, "/parameterGroup.xhtml"));
            try {
                extContext.redirect(url);
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public String cancel() {

        resetAll();
        return "parameterGroup.xhtml?faces-redirect=true";
    }

    /**
     * Resets everything to empty startup.
     */
    private void resetAll() {
        // reset Display
        renderTypeString = true;
        renderTypeIntFloat = false;
        renderTypeEnum = false;
        renderTypeDateTime = false;
        renderTypeBoolean = false;

        // reste enumList
        enumList = new ArrayList<EnumValueStore>();
        enumList.add(new EnumValueStore());
        enumIndexChanged = true;

        // reset example Label
        exampleLabel = "";

        //reset input data
        name = "";
        description = "";
        shortDescription = "";
        option = "";
        type = Tprimitive.STRING;
        typeString = "string";
        defaultValue = "";
        min = "";
        max = "";
        minLength = "";
        maxLength = "";
        regexp = "";
        intervalMin = false;
        intervalMax = false;
        prefix = "";
        suffix = "";
        separator = "";
        minOccur = "";
        maxOccur = "";
        enumType = "";
        guiString = "";

        // reset loadedFrom
        loadedFrom = "";

        //reset parameterList
        parameterNameList = manager.getAllNames();
        parametersEmpty = manager.isEmpty();

        renderUnsavedChanges = false;
        
        position = "";     
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
            ParameterGroupBean parameterGroupBean = (ParameterGroupBean) context.getApplication().
                    evaluateExpressionGet(context, "#{parameterGroupBean}",
                    ParameterGroupBean.class);
            if (parameterGroupBean.getName().isEmpty()) {
                position += " - "+ unnamed;
            } else {
                position += " - " + parameterGroupBean.getName();
            }
        }
    }

    /**
     * Does all the validation and saving action.
     * @return
     */
    private boolean saveAll() {
        if (validate()) {
            Object newParam = createParameter();
            if (loadedFrom.isEmpty()) {
                try {
                    manager.addParameter(newParam);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "parameterAlreadyExistsError"),
                                ""));
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty("couldNotSave"), ""));
                    }
                    return false;
                }
            } else {
                try {
                    manager.editParameter(loadedFrom, newParam);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "parameterAlreadyExistsError"),
                                ""));
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty("couldNotSave"), ""));
                    }
                    return false;
                }
            }
        } else {
            return false;
        }

        //renew parameterList
        parameterNameList = manager.getAllNames();
        parametersEmpty = manager.isEmpty();


        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                PropertyManager.getProperty("saveSuccesful"), ""));

        renderUnsavedChanges = false;
        
        return true;
    }

    /**
     * Creates the parameter occording to current input.
     * @return
     */
    private Object createParameter() {
        Object newParam = null;
        if (type == null) {
            newParam =
                    ParameterBuilder.createParameterAsEnum(name,
                    shortDescription,
                    description, option, enumType, prefix, suffix, separator,
                    enumList, minOccur, maxOccur, guiString,
                    BasicBeanData.StandardLanguage);
        } else {
            switch (type) {
                case STRING:
                    newParam =
                            ParameterBuilder.createParameterAsString(name,
                            shortDescription, description, option, defaultValue,
                            regexp, minLength, maxLength, guiString,
                            BasicBeanData.StandardLanguage);
                    break;
                case INT:
                    newParam = ParameterBuilder.createParameterAsInt(name,
                            shortDescription, description, option, defaultValue,
                            min, intervalMin, max, intervalMax, guiString,
                            BasicBeanData.StandardLanguage);
                    break;
                case FLOAT:
                    newParam = ParameterBuilder.createParameterAsFloat(name,
                            shortDescription, description, option, defaultValue,
                            min, intervalMin, max, intervalMax, guiString,
                            BasicBeanData.StandardLanguage);
                    break;
                case DATETIME:
                    newParam = ParameterBuilder.createParameterAsDateTime(
                            name, shortDescription, description, option,
                            defaultValue, guiString,
                            BasicBeanData.StandardLanguage);
                    break;
                case BOOLEAN:
                    newParam = ParameterBuilder.createParameterAsBoolean(
                            name, shortDescription, description, option,
                            defaultValue, guiString,
                            BasicBeanData.StandardLanguage);
                    break;
            }
        }
        return newParam;
    }

    /**
     * Validates the current input and adds all error messages.
     * @return true: everything is OK, false: something is wrong
     */
    private boolean validate() {
        boolean ret = true;

        if (name.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            ret = false;
        }

        if (description.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("descriptionError"), ""));
            ret = false;
        }

        if (shortDescription.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("shortDescriptionError"), ""));
            ret = false;
        }
        if (type == null) {
            if (!validateEnum()) {
                ret = false;
            }
        } else {
            switch (type) {
                case STRING:
                    if (!validateString()) {
                        ret = false;
                    }
                    break;
                case INT:
                    if (!validateInt()) {
                        ret = false;
                    }
                    break;
                case FLOAT:
                    if (!validateFloat()) {
                        ret = false;
                    }
                    break;
                case DATETIME:
                    if (!validateDateTime()) {
                        ret = false;
                    }
                    break;
                case BOOLEAN:
                   if(option.isEmpty()){
                        FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("booleanNoOptionError"), ""));
                       ret = false;
                   }
                    break;
            }
        }

        return ret;
    }

    /**
     * Validates the current input and adds all error messages for type String.
     * @return true: everything is OK, false: something is wrong
     */
    private boolean validateString() {

        boolean ret = true;

        int minLengthInt = -1;
        if (!ParameterValidator.isInteger(minLength)) {
            if (minLength.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("minLengthError"),
                        ""));
                ret = false;
            }
        } else {
            minLengthInt = Integer.parseInt(minLength);
            if (minLengthInt < 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("minLengthSmallerZeroError"),
                        ""));
                ret = false;
            } else if (defaultValue.length() > 0 && defaultValue.length()
                    < minLengthInt) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "defaultValueShorterMinLenghtError"),
                        ""));
                ret = false;
            }
        }
        if (!ParameterValidator.isInteger(maxLength)) {
            if (maxLength.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("maxLengthError"),
                        ""));
                ret = false;
            }
        } else {
            int maxLengthInt = Integer.parseInt(maxLength);
            if (maxLengthInt < 1) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("maxLengthSmallerZeroError"),
                        ""));
                ret = false;
            } else if (maxLengthInt < minLengthInt) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "maxLengthSmallerMinLengthError"),
                        ""));
                ret = false;
            } else if (defaultValue.length() > 0 && defaultValue.length()
                    > maxLengthInt) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "defaultValueLongerMaxLenghtError"),
                        ""));
                ret = false;
            }
        }
        if (!ParameterValidator.isRexExp(regexp)) {
            if (regexp.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("regExpError"),
                        ""));
                ret = false;
            }
        } else {
            if (defaultValue.length() > 0 && regexp.length() > 0) {
                Pattern pattern = Pattern.compile(regexp);
                Matcher m = pattern.matcher(defaultValue);
                if (!m.matches()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("defaultNotRegExpError"),
                            ""));
                    ret = false;
                }
            }
        }
        return ret;
    }

    /**
     * Validates the current input and adds all error messages for type int.
     * @return true: everything is OK, false: something is wrong
     */
    private boolean validateInt() {

        boolean ret = true;

        int minInt = 0;
        boolean minSet = false;
        if (!ParameterValidator.isInteger(min)) {
            if (min.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("minError"),
                        ""));
                ret = false;
            }
        } else {
            minInt = Integer.parseInt(min);
            minSet = true;
        }

        int maxInt = 0;
        boolean maxSet = false;
        if (!ParameterValidator.isInteger(max)) {
            if (max.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("maxError"),
                        ""));
                ret = false;
            }
        } else {
            maxInt = Integer.parseInt(max);
            maxSet = true;
            if (minSet && ((intervalMin && maxInt < minInt) || (!intervalMin && maxInt
                    <= minInt))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("maxSmallerMinError"),
                        ""));
                ret = false;
            }
        }

        if (!ParameterValidator.isInteger(defaultValue)) {
            if (defaultValue.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("defaultIntError"),
                        ""));
                ret = false;
            }
        } else {
            int defaultInt = Integer.parseInt(defaultValue);
            if (minSet && ((intervalMin && defaultInt < minInt)
                    || (!intervalMin && defaultInt <= minInt))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("defaultSmallerMinError"),
                        ""));
                ret = false;
            }
            if (maxSet && ((intervalMax && defaultInt > maxInt)
                    || (!intervalMax && defaultInt >= maxInt))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("defaultGreaterMaxError"),
                        ""));
                ret = false;
            }
        }

        return ret;
    }

    /**
     * Validates the current input and adds all error messages for type float.
     * @return true: everything is OK, false: something is wrong
     */
    private boolean validateFloat() {

        boolean ret = true;

        float minFloat = 0;
        boolean minSet = false;
        if (!ParameterValidator.isFloat(min)) {
            if (min.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("minError"),
                        ""));
                ret = false;
            }
        } else {
            minFloat = Float.parseFloat(min);
            minSet = true;
        }

        float maxFloat = 0;
        boolean maxSet = false;
        if (!ParameterValidator.isFloat(max)) {
            if (max.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("maxError"),
                        ""));
                ret = false;
            }
        } else {
            maxFloat = Float.parseFloat(max);
            maxSet = true;
            if (minSet && ((intervalMin && maxFloat < minFloat) || (!intervalMin
                    && maxFloat <= minFloat))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("maxSmallerMinError"),
                        ""));
                ret = false;
            }
        }

        if (!ParameterValidator.isFloat(defaultValue)) {
            if (defaultValue.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("defaultIntError"),
                        ""));
                ret = false;
            }
        } else {
            float defaultFloat = Float.parseFloat(defaultValue);
            if (minSet && ((intervalMin && defaultFloat < minFloat)
                    || (!intervalMin && defaultFloat <= minFloat))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("defaultSmallerMinError"),
                        ""));
                ret = false;
            }
            if (maxSet && ((intervalMax && defaultFloat > maxFloat)
                    || (!intervalMax && defaultFloat >= maxFloat))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("defaultGreaterMaxError"),
                        ""));
                ret = false;
            }
        }

        return ret;
    }

    /**
     * Validates the current input and adds all error messages for type DateTime.
     * (conform to ISO 8601)
     * @return true: everything is OK, false: something is wrong
     */
    private boolean validateDateTime() {
        if (defaultValue.length() > 0 && ParameterValidator.validateDateTime(
                defaultValue)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("dateTimeError"),
                    ""));
            return false;
        }
        return true;
    }

    /**
     * Validates the current input and adds all error messages for type enum.
     * @return true: everything is OK, false: something is wrong
     */
    private boolean validateEnum() {
        boolean ret = true;

        if (separator.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("separatorError"),
                    ""));
            ret = false;
        }

        int minOccurInt = 1;
        int maxOccurInt = 1;
        boolean minOccurSet = false;
        if (!ParameterValidator.isInteger(minOccur)) {
            if (minOccur.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("minOccurError"),
                        ""));
                ret = false;
            }
        } else {
            minOccurInt = Integer.parseInt(minOccur);
            minOccurSet = true;
            if (minOccurInt < 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("minOccurSmallerZeroError"),
                        ""));
                ret = false;
            }
            if (minOccurInt > enumList.size()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "minOccurGreaterEnumListError"),
                        ""));
                ret = false;
            }
        }

        if (!ParameterValidator.isInteger(maxOccur)) {
            if (maxOccur.length() > 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("maxOccurError"),
                        ""));
                ret = false;
            }
        } else {
            maxOccurInt = Integer.parseInt(maxOccur);
            if (maxOccurInt < 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("maxOccurSmallerZeroError"),
                        ""));
                ret = false;
            }
            if (minOccurSet && maxOccurInt < minOccurInt) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "maxOccurSmallerMinOccurError"),
                        ""));
                ret = false;
            }
        }

        int defaultCount = 0;

        // there is always at least one enumValue, no need to check this if GUI is fine

        // for check for name, key and value doubles
        HashSet nameSet = new HashSet();
        HashSet keySet = new HashSet();
        HashSet valueSet = new HashSet();

        //check for correct values
        for (EnumValueStore en : enumList) {

            nameSet.add(en.getName());
            keySet.add(en.getKey());
            valueSet.add(en.getValue());

            if (en.getName().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "enumNoNameError"),
                        ""));
                ret = false;
                break;
            }

            if (en.getKey().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty(
                        "enumNoKeyError"),
                        ""));
                ret = false;
                break;
            }

            if (en.isDefaultValue()) {
                defaultCount++;
            }

            if (enumType.equals(PrimitiveType.INT.getName())) {
                if (!ParameterValidator.isInteger(en.getValue())) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "enumIntError"),
                            ""));
                    ret = false;
                    break;
                }
            } else if (enumType.equals(PrimitiveType.FLOAT.getName())) {
                if (!ParameterValidator.isFloat(en.getValue())) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "enumFloatError"),
                            ""));
                    ret = false;
                    break;
                }
            } else if (enumType.equals(PrimitiveType.DATETIME.getName())) {
                if (!ParameterValidator.validateDateTime(en.getValue())) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(
                            "enumDateTimeError"),
                            ""));
                    ret = false;
                    break;
                }
            }
        }

        if (nameSet.size() < enumList.size()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "enumDuplicateNameError"),
                    ""));
            ret = false;
        }
        if (keySet.size() < enumList.size()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "enumDuplicateKeyError"),
                    ""));
            ret = false;
        }
        if (valueSet.size() < enumList.size()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "enumDuplicateValueError"),
                    ""));
            ret = false;
        }
        if (defaultCount < minOccurInt && defaultCount > maxOccurInt) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "enumDefaultError"),
                    ""));
            ret = false;
        }

        return ret;
    }

    /**
     * Load in the parameter with given name
     * @param name name of the parameter to load
     */
    private void loadParameter(String name) {
        resetAll();
        calcPostionString();
        renderTypeString = false;
        Object param = null;
        try {
            param = manager.getParameterByName(name);
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openParameterError"),
                    ""));
            return;
        }
        if (param instanceof Tparam) {
            loadTparam((Tparam) param);
        } else if (param instanceof TenumParam) {
            loadTenumParam((TenumParam) param);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openParameterError"),
                    ""));
        }
        calculateExample();
        calculateCompatibleGuiElements();
        renderUnsavedChanges = false;
    }

    private void loadTparam(Tparam param) {
        name = param.getName().get(0).getValue();
        loadedFrom = name;
        shortDescription = param.getShortDescription().get(0).getValue();
        description = (String) param.getDescription().get(0).getContent().get(0);
        guiString = param.getGuiElement();
        
        if (param.isSetOption()) {
            option = param.getOption();
        }

        if (param.isSetDefaultValue()) {
            defaultValue = param.getDefaultValue();
        }

        type = param.getType();

        switch (param.getType()) {
            case STRING:
                if (param.isSetMinLength()) {
                    minLength = Integer.toString(param.getMinLength());
                }
                if (param.isSetMaxLength()) {
                    maxLength = Integer.toString(param.getMaxLength());
                }
                if (param.isSetRegexp()) {
                    regexp = param.getRegexp();
                }
                typeString = PrimitiveType.STRING.getName();
                renderTypeString = true;
                calculateExample();
                break;
            case INT:
                if (param.isSetMin()) {
                    min = Integer.toString((int) param.getMin().getValue());
                    intervalMin = param.getMin().isIncluded();
                }
                if (param.isSetMax()) {
                    max = Integer.toString((int) param.getMax().getValue());
                    intervalMax = param.getMax().isIncluded();
                }
                typeString = PrimitiveType.INT.getName();
                renderTypeIntFloat = true;
                break;
            case FLOAT:
                if (param.isSetMin()) {
                    min = Float.toString(param.getMin().getValue());
                    intervalMin = param.getMin().isIncluded();
                }
                if (param.isSetMax()) {
                    max = Float.toString(param.getMax().getValue());
                    intervalMax = param.getMax().isIncluded();
                }
                typeString = PrimitiveType.FLOAT.getName();
                renderTypeIntFloat = true;
                break;
            case BOOLEAN:
                renderTypeBoolean = true;
                typeString = PrimitiveType.BOOLEAN.getName();
                break;
            case DATETIME:
                renderTypeDateTime = true;
                typeString = PrimitiveType.DATETIME.getName();
                break;
        }
    }

    private void loadTenumParam(TenumParam param) {

        name = param.getName().get(0).getValue();
        loadedFrom = name;
        shortDescription = param.getShortDescription().get(0).getValue();
        description = (String) param.getDescription().get(0).getContent().get(0);
        guiString = param.getGuiElement();
        
        type = null;
        typeString = "enum";
        renderTypeEnum = true;

        if (param.isSetOption()) {
            option = param.getOption();
        }

        if (param.isSetPrefix()) {
            prefix = param.getPrefix();
        }

        if (param.isSetSuffix()) {
            suffix = param.getSuffix();
        }

        separator = param.getSeparator();

        if (param.isSetMinoccurs()) {
            minOccur = Integer.toString(param.getMinoccurs());
        }
        if (param.isSetMaxoccurs()) {
            maxOccur = Integer.toString(param.getMaxoccurs());
        }
        try {
            enumType = PrimitiveType.getNameForPrimitive(param.getType());
        } catch (BeansException ex) {
            // should not happen
            enumType = PrimitiveType.STRING.getName();
        }

        // enums can only be saved with 1 enumValue
        // not test ist necessary
        enumList = new ArrayList<EnumValueStore>();
        for (TenumValue val : param.getValues()) {
            EnumValueStore newEnum = new EnumValueStore();
            newEnum.setKey(val.getKey());
            newEnum.setName(val.getName().get(0).getValue());
            newEnum.setValue(val.getValue());
            newEnum.setDefaultValue(val.isDefaultValue());
            enumList.add(newEnum);
        }
    }

    /**
     * Adds a new enumValue to the list after position index.
     * @param index position to add
     */
    public void newEnum(int index) {
        enumList.add(index + 1, new EnumValueStore());
        enumIndexChanged = true;
    }

    /**
     * Removes the enumValue at position index.
     * @param index position to remove
     */
    public void removeEnum(int index) {
        enumList.remove(index);
        enumIndexChanged = true;
    }

    /**
     * Show the removesymbol after enumValues
     */
    public boolean isShowRemoveEnum() {
        return (enumList.size() > 1);
    }

    public void setEnumList(List<EnumValueStore> enumList) {
        this.enumList = enumList;
    }

    public String getEnumType() {
        return enumType;
    }

    public void setEnumType(String enumType) {
        this.enumType = enumType;
    }

    public String getMaxOccur() {
        return maxOccur;
    }

    public void setMaxOccur(String maxOccur) {
        this.maxOccur = maxOccur;
    }

    public String getMinOccur() {
        return minOccur;
    }

    public void setMinOccur(String minOccur) {
        this.minOccur = minOccur;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isIntervalMax() {
        return intervalMax;
    }

    public void setIntervalMax(boolean intervalMax) {
        this.intervalMax = intervalMax;
    }

    public boolean isIntervalMin() {
        return intervalMin;
    }

    public void setIntervalMin(boolean intervalMin) {
        this.intervalMin = intervalMin;
    }

    public boolean isParametersEmpty() {

        return parametersEmpty;
    }

    public List<String> getParameterNameList() {

        return parameterNameList;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean isRenderTypeEnum() {
        return renderTypeEnum;
    }

    public boolean isRenderTypeString() {
        return renderTypeString;
    }

    public boolean isRenderTypeBoolean() {
        return renderTypeBoolean;
    }

    public boolean isRenderTypeDateTime() {
        return renderTypeDateTime;
    }

    public boolean isRenderTypeIntFloat() {
        return renderTypeIntFloat;
    }

    public String getExampleLabel() {
        return exampleLabel;
    }

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public boolean isRenderLoadedFrom() {
        return loadedFrom.length() > 0;
    }

    public String getGuiString() {
        return guiString;
    }

    public void setGuiString(String guiString) {
        this.guiString = guiString;
    }

    public SelectItem[] getCompatibleGuiElements() {
        return compatibleGuiElements;
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
        renderUnsavedChanges = true;
        this.description = description;
    }  
}
