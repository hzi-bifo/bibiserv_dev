/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
 * All rights reserved.
 * 
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You 
 * may not use this file except in compliance with the License. You can 
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 * 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  When distributing the software, include 
 * this License Header Notice in each file.  If applicable, add the following 
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 * 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 */
package de.unibi.cebitec.bibiserv.util.codegen.web.guiconstruction;

import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.TenumValue;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @TODO Write Java DOC
 *
 * @author Daniel Hagemeier - dhagemei[at]cebitec.uni-bielefeld.de Jan Krueger -
 * jkrueger[at]cebitec.uni-bielefeld.de
 */
public class ParameterConstructionJSF {

    private StringBuilder paramstring;
    private String beanName;
    private String functionId;
    private String toolid;
    //Parameters to create a PADT
    //General information
    private String name;
    private String id;
    private ParameterType type;
    private String option;
    private String description;
    private ParameterJsfElement jsfElement;
    private Boolean isRequired;
    //Information Needed for Numbers/Integers/floats
    private Object min;
    private Object max;
    private Object minIncluded;
    private Object maxIncluded;
    private Object defaultValueNum;
    //information for strings
    private String defaultValueString;
    private Object minLength;
    private Object maxLength;
    private Object regexp;
    //information for boolean(Default entry )
    private Object isEnabled;
    //information needed for enum params
    private ArrayList<String> defaultValueStringList;
    private Map<String, String> keyValuePair;
    // line break
    private static final String br = System.getProperty("line.separator");

    public ParameterConstructionJSF(String toolid) {
        this.toolid = toolid;
    }

    /**
     * Method for generation of the Faces-Pages using a PADT... the elements are
     * generated on the bottom...
     *
     * @param paramGroup
     * @param functionId
     * @return
     * @throws CodeGenParserException
     */
    public String retrieveParameters(TparamGroup paramGroup, String functionId) throws CodeGenParserException {
        try {

            Map<String, ParameterADT> paramItemMap = new LinkedHashMap<String, ParameterADT>();
            List<Object> paramlist = paramGroup.getParamrefOrParamGroupref();
            StringBuilder paramcode = new StringBuilder();
            beanName = functionId + "_param";
            this.functionId = functionId;

            for (Object param : paramlist) {

                if (param.getClass().equals(TparamGroup.ParamGroupref.class)) {
                    TparamGroup subGroup = (TparamGroup) (((TparamGroup.ParamGroupref) param).getRef());
                    // add fieldset arround paramgroup
                    paramcode.append("<fieldset style=\"clear:both\">").append(br);
                    paramcode.append("<legend title=\"#{messages.property('").append(subGroup.getId()).append("_shortDescription')}\">")
                            .append("#{messages.property('").append(subGroup.getId()).append("_name')}</legend>").append(br);
                    paramcode.append("<div class=\"paramBox\">").append(br);
                    paramcode.append(retrieveParameters(subGroup, functionId)).append(br);
                    paramcode.append("</div>").append(br);
                    paramcode.append("</fieldset>").append(br);
                } else if (param.getClass().equals(TparamGroup.Paramref.class)) {
                    // get paramref which could be a param or enumreference
                    Object paramref = ((TparamGroup.Paramref) param).getRef();
                    ParameterADT padt = null;
                    // paramref could be 'real' parameter (Tparam class) or ...
                    if (paramref.getClass().equals(Tparam.class)) {
                        Tparam parameter = (Tparam) paramref;
                        /**
                         * @param id
                         * @param name
                         * @param type
                         * @param option
                         * @param description
                         * @param jsfElement
                         * @param isRequired
                         */
                        id = parameter.getId();
                        name = id + "_name";
                        type = ParameterType.valueOf(parameter.getType().toString().toUpperCase());
                        option = (parameter.isSetOption()) ? parameter.getOption() : "";
                        description = id + "_shortDescription";
                        jsfElement = ParameterJsfElement.valueOf(parameter.getGuiElement().toString().toUpperCase());
                        isRequired = false; //@ToDo this is the wrong place to decide wether this parameter is required ...
                        /**
                         * @param defaultValue
                         * @param min
                         * @param max
                         * @param minIncluded
                         * @param maxIncluded
                         */
                        if (type.equals(ParameterType.INT) || type.equals(ParameterType.FLOAT)) {
                            defaultValueNum = parameter.getDefaultValue();
                            min = null;
                            minIncluded = null;
                            if (parameter.isSetMin()) {
                                min = (int) parameter.getMin().getValue();
                                minIncluded = parameter.getMin().isIncluded();
                            }
                            max = null;
                            maxIncluded = null;
                            if (parameter.isSetMax()) {
                                max = (int) parameter.getMax().getValue();
                                maxIncluded = parameter.getMax().isIncluded();
                            }
                            padt = new ParameterADT(id, name, type, option, description, jsfElement, isRequired, defaultValueNum, min, max, minIncluded, maxIncluded);

                            /**
                             * @param defaultValue
                             * @param minLength
                             * @param maxLength
                             * @param regexp
                             */
                        } else if (type.equals(ParameterType.STRING)) {
                            defaultValueString = parameter.getDefaultValue();
                            minLength = parameter.getMinLength();
                            maxLength = parameter.getMaxLength();
                            regexp = parameter.getRegexp();
                            padt = new ParameterADT(id, name, type, option, description, jsfElement, isRequired, defaultValueString, minLength, maxLength, regexp);
                            /**
                             * @param default value boolean isEnabled (selected
                             * or not...)
                             */
                        } else if (type.equals(ParameterType.BOOLEAN)) {
                            isEnabled = parameter.getDefaultValue();
                            padt = new ParameterADT(id, name, type, option, description, jsfElement, isRequired, isEnabled);
                        } else {
                            throw new CodeGenParserException("Parameter '" + id + "' has unsupported type " + ParameterType.DATETIME.name());
                        }

                        // ... a enum parameter (TenumParam)
                    } else if (paramref.getClass().equals(TenumParam.class)) {
                        TenumParam enumparameter = (TenumParam) paramref;
                        id = enumparameter.getId();
                        name = id + "_name";
                        type = ParameterType.valueOf(enumparameter.getType().toString().toUpperCase());
                        option = enumparameter.isSetOption() ? enumparameter.getOption() : "";
                        description = id + "_shortDescription";
                        jsfElement = ParameterJsfElement.valueOf(enumparameter.getGuiElement().toString().toUpperCase());
                        isRequired = false;
                        // Generating LinkedHashmap for EnumParam GUI-Elements
                        keyValuePair = new LinkedHashMap<String, String>();
                        // distinguish between SelectMany (maxoccurs > 1) or SelectOne(maxoccurs == 1) enum parameter
                        boolean selectmany;
                        if (enumparameter.isSetMaxoccurs() && enumparameter.getMaxoccurs() > 1 && jsfElement.name().contains("SELECTMANY")) {
                            selectmany = true;
                        } else if (((enumparameter.isSetMaxoccurs() && enumparameter.getMaxoccurs() == 1) || !enumparameter.isSetMaxoccurs())
                                && !jsfElement.name().contains("SELECTMANY")) {
                            selectmany = false;
                        } else {
                            throw new CodeGenParserException("Detected an invalid combination of @maxoccurs("
                                    + enumparameter.getMaxoccurs() + ") and jsfelement (" + jsfElement.name() + ") "
                                    + "when analyzing enum parameter with id '" + id + "'. "
                                    + "An jsfelement '___SELECTMANY' must have @maxoccurs value > 1 and "
                                    + "an jsfelement !'___SELECTONE' must have @maxoccurs value of 0 or 1.");
                        }


                        defaultValueStringList = new ArrayList<String>();
                        for (TenumValue enumValue : enumparameter.getValues()) {
                            // collect all default values
                            if (enumValue.isDefaultValue()) {
                                defaultValueStringList.add(enumValue.getKey().toString());
                            }
                            //Creating a Hashmap containing the selectable values of the selection components
                            keyValuePair.put(enumValue.getKey(), enumValue.getValue());
                        }
                        // only selectmany enum parameter could have more than 1 default value
                        if (!selectmany && defaultValueStringList.size() > 1) {
                            throw new CodeGenParserException("Enum parameter with id '" + id + "' isn't a SELECTMANY parameter, but have "
                                    + "more than one default value!");
                        }
                        padt = new ParameterADT(id,
                                name,
                                type,
                                option,
                                description,
                                jsfElement,
                                isRequired,
                                defaultValueStringList,
                                keyValuePair);

                    } else {
                        throw new CodeGenParserException("This Exception could only be thrown, if BiBiServAbstraction schema and corresponding JAXB classes have changed.");
                    }
                    paramcode.append(createParameter(padt));
                }
            }
            return paramcode.toString();
        } catch (IllegalArgumentException e) {

            StringBuilder sb = new StringBuilder("Unknown or unsupported JSF element. Supported elements are :");

            for (ParameterJsfElement jsf : ParameterJsfElement.values()) {
                sb.append(jsf).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);

            throw new CodeGenParserException(sb.toString());

        }
    }

    private String createParameter(ParameterADT padt) throws CodeGenParserException {
        log.info(V,"Creating parameter construction for ...",padt.getJsfElement());
        StringBuilder out = new StringBuilder();
        out.append("<h:panelGroup layout=\"block\" styleClass=\"formblock_parambox\">").append(br);
        switch (padt.getJsfElement()) {
            case INPUTTEXT:
                out.append(createInputText(padt));
                break;
            case INPUTTEXTAREA:
                out.append(createInputTextArea(padt));
                break;
            case SELECTMANYMENU:
                out.append(createSMM(padt));
                break;
            case SELECTMANYLISTBOX:
                out.append(createSML(padt));
                break;
            case SELECTMANYCHECKBOX:
                out.append(createSMC(padt));
                break;
            case SELECTONEMENU:
                out.append(createSOM(padt));
                break;
            case SELECTONERADIO:
                out.append(createSOR(padt));
                break;
            case SELECTONELISTBOX:
                out.append(createSOL(padt));
                break;
            case SELECTBOOLEANCHECKBOX:
                out.append(createSBC(padt));
                break;
            default:
                out.append("");
                break;
        }
        out.append("</h:panelGroup>").append(br);
        return out.toString();
    }

    private String createParameterGrid(String id,
            int columns) {
        StringBuilder panelGrid = new StringBuilder();

        panelGrid.append("<h:panelGrid ");
        panelGrid.append("id=\"" + id + "_" + functionId + "\"");
        panelGrid.append("style=\"parameterGroup\"");
        panelGrid.append("cellpadding=\"0\"");
        panelGrid.append("cellspacing=\"0\"");
        panelGrid.append("cellspacing=\"" + columns + "\"/>");
        panelGrid.append(System.getProperty("line.separator"));
        return panelGrid.toString();
    }

    private String createParameterGroup(String id) {
        StringBuilder panelGroup = new StringBuilder();
        panelGroup.append("<h:panelGroup layout=\"block\" ");
        panelGroup.append("id=\"" + id + "_" + functionId + "\"");
        panelGroup.append("style=\"parameterGroup\"/>");
        panelGroup.append(System.getProperty("line.separator"));
        return panelGroup.toString();
    }

    private String createParameter(String id,
            String[] children) {
        StringBuilder panelGrid = new StringBuilder();

        panelGrid.append("<h:panelGrid ");
        panelGrid.append("id=\"" + id + "_" + functionId + "\"");
        panelGrid.append("style=\"parameterUnion\">");
        panelGrid.append(System.getProperty("line.separator"));
        for (String object : children) {

            panelGrid.append(object);
            //group.getChildren().add(object);
        }
        panelGrid.append("</h:panelGrid>" + br);

        return panelGrid.toString();
    }

    /**
     * Provide SelectManyCheckbox
     *
     * @param padt
     * @param context
     * @param component
     * @param beanName
     * @param grid
     */
    private String createSMC(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createAddDelSMC(padt));
        out.append(createMessage(padt));
        return out.toString();
    }

    private String createAddDelSMC(ParameterADT padt) {
        StringBuilder selectManyCheckbox = new StringBuilder();
       
        selectManyCheckbox.append("<h:selectManyCheckbox");

        selectManyCheckbox.append(" id=\"" + padt.getId() + "_" + functionId
                + "\"");
        selectManyCheckbox.append(" value=\"#{" + beanName + "." + padt.getId()
                + "}\"");

        selectManyCheckbox.append(" title=\"#{messages.property('").append(padt.getDescription()).append("')}\"");
        selectManyCheckbox.append(" layout=\"pageDirecion\"");
        selectManyCheckbox.append(">");
        Integer index = 0;
        for (String key : padt.getKeyValuePair().keySet()) {
         
            selectManyCheckbox.append("<f:selectItem itemValue=\"").append(key).append("\"");
            selectManyCheckbox.append(" itemLabel=\"#{messages.property('").
                    append(padt.getId()).append("_").append(key).append("')} [").
                    append(padt.getKeyValuePair().get(key)).append("]\"/>");
            index++;
        }
        selectManyCheckbox.append("</h:selectManyCheckbox> <br/> ");
        return selectManyCheckbox.toString();
    }

    /**
     * Creating SelectManyListbox
     *
     * @param padt
     * @param context
     * @param component
     * @param beanName
     * @param grid
     */
    private String createSML(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createAddDelSML(padt));
        out.append(createMessage(padt));
        return out.toString();
    }

    private String createAddDelSML(ParameterADT padt) {
        StringBuilder selectManyListbox = new StringBuilder();
        selectManyListbox.append("<h:selectManyListbox");
        selectManyListbox.append(" id=\"" + padt.getId() + "_" + functionId + "\"");
        selectManyListbox.append(" value=\"#{" + beanName + "." + padt.getId() + "}\"");
        selectManyListbox.append(" title=\"#{messages.property('" + padt.getDescription() + "')}\"");
        selectManyListbox.append(">");
        for (String key : padt.getKeyValuePair().keySet()) {
            selectManyListbox.append("<f:selectItem");
            selectManyListbox.append(" itemValue=\"" + key + "\"");
            selectManyListbox.append(" itemLabel=\"#{messages.property('").
                    append(padt.getId()).append("_").append(key).append("')} [").
                    append(padt.getKeyValuePair().get(key)).append("]\"/>");
            selectManyListbox.append(br);
        }
        selectManyListbox.append("</h:selectManyListbox>  <br/>" + br);
        return selectManyListbox.toString();

    }

    /**
     * Creating selectOneRadio
     *
     * @param padt
     * @param context
     * @param component
     * @param beanName
     * @param grid
     */
    private String createSOR(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createAddDelSOR(padt));
        out.append(createMessage(padt));
        return out.toString();
    }

    private String createAddDelSOR(ParameterADT padt) {
        StringBuilder selectOneRadio = new StringBuilder();


        selectOneRadio.append("<h:selectOneRadio");
        selectOneRadio.append(" id=\"" + padt.getId() + "_" + functionId + "\"");
        selectOneRadio.append(" value=\"#{" + beanName + "." + padt.getId()
                + "}\"");
        selectOneRadio.append(" title=\"#{messages.property('").append(padt.getDescription()).append("')}\"");
        selectOneRadio.append(" layout=\"pageDirection\"");
        selectOneRadio.append(">");
        Integer index = 0;
        for (String key : padt.getKeyValuePair().keySet()) {


            selectOneRadio.append("<f:selectItem itemValue=\"").append(key).append("\"");
            selectOneRadio.append(" itemLabel=\"#{messages.property('").
                    append(padt.getId()).append("_").append(key).append("')} [").
                    append(padt.getKeyValuePair().get(key)).append("]\"/>");
            index++;
        }
        selectOneRadio.append("</h:selectOneRadio> <br/> ");
        return selectOneRadio.toString();
    }

    /**
     * Creating SelectOneListbox
     *
     * @param padt
     * @param context
     * @param component
     * @param beanName
     * @param grid
     */
    private String createSOL(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createAddDelSOL(padt));
        out.append(createMessage(padt));
        return out.toString();
    }

    private String createAddDelSOL(ParameterADT padt) {
        StringBuilder selectOneListbox = new StringBuilder();
        selectOneListbox.append("<h:selectOneListbox");
        selectOneListbox.append(" id=\"" + padt.getId() + "_" + functionId + "\"");
        selectOneListbox.append(" value=\"#{" + beanName + "." + padt.getId() + "}\"");
        selectOneListbox.append(" title=\"#{messages.property('" + padt.getDescription() + "')}\"");
        selectOneListbox.append(">");
        for (String key : padt.getKeyValuePair().keySet()) {
            selectOneListbox.append("<f:selectItem");
            selectOneListbox.append(" itemValue=\"" + key + "\"");
            selectOneListbox.append(" itemLabel=\"#{messages.property('").
                    append(padt.getId()).append("_").append(key).append("')} [").
                    append(padt.getKeyValuePair().get(key)).append("]\"/>");
        }
        selectOneListbox.append("</h:selectOneListbox>  <br/>" + br);
        return selectOneListbox.toString();
    }

    /**
     * Creating SelectOneMenu
     *
     * @param padt
     * @param context
     * @param component
     * @param beanName
     * @param grid
     */
    private String createSOM(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createAddDelSOM(padt));
        out.append(createMessage(padt));
        return out.toString();
    }

    private String createAddDelSOM(ParameterADT padt) throws CodeGenParserException {


        StringBuilder selectOneMenu = new StringBuilder();
        selectOneMenu.append("<h:selectOneMenu");

        selectOneMenu.append(" id=\"" + padt.getId() + "_" + functionId + "\"");
        selectOneMenu.append(" value=\"#{" + beanName + "." + padt.getId() + "}\"");
        selectOneMenu.append(" title=\"#{messages.property('" + padt.getDescription() + "')}\"");
        selectOneMenu.append(">");
        for (String key : padt.getKeyValuePair().keySet()) {
            selectOneMenu.append("<f:selectItem");
            selectOneMenu.append(" itemValue=\"" + key + "\"");
            selectOneMenu.append(" itemLabel=\"#{messages.property('").
                    append(padt.getId()).append("_").append(key).append("')} [").
                    append(padt.getKeyValuePair().get(key)).append("]\"/>");
        }
        selectOneMenu.append("</h:selectOneMenu>  <br/>");
        return selectOneMenu.toString();
    }

    /**
     * Creation of SelectManyMenu
     *
     * @param padt
     * @param context
     * @param component
     * @param beanName
     * @param grid
     */
    private String createSMM(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createAddDelSMM(padt));
        out.append(createMessage(padt));
        return out.toString();
    }

    private String createAddDelSMM(ParameterADT padt) {
        StringBuilder selectManyMenu = new StringBuilder();
        selectManyMenu.append("<h:selectManyMenu");
        selectManyMenu.append(" id=\"" + padt.getId() + "_" + functionId + "\"");
        selectManyMenu.append(" value=\"#{" + beanName + "." + padt.getId() + "}\"");
        selectManyMenu.append(" title=\"#{messages.property('" + padt.getDescription() + "')}\"");
        selectManyMenu.append(">");
        for (String key : padt.getKeyValuePair().keySet()) {
            selectManyMenu.append("<f:selectItem");
            selectManyMenu.append(" itemValue=\"" + key + "\"");
            selectManyMenu.append(" itemLabel=\"#{messages.property('" + padt.getId() + "_" + key + "')}\"/>");
        }
        selectManyMenu.append("</h:selectManyMenu>  <br/>");
        return selectManyMenu.toString();
    }

    /**
     * Provide SelectBooleanCheckbox
     *
     * @param padt
     * @param context
     * @param component
     * @param beanName
     * @param grid
     */
    private String createSBC(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createAddDelSBC(padt));
        out.append(createMessage(padt));
        return out.toString();

    }

    private String createAddDelSBC(ParameterADT padt) {
        StringBuilder selectBooleanCheckbox = new StringBuilder();
        selectBooleanCheckbox.append("<h:selectBooleanCheckbox");
        selectBooleanCheckbox.append(" id=\"" + padt.getId() + "_" + functionId + "\"");
        selectBooleanCheckbox.append(" value=\"#{" + beanName + "." + padt.getId() + "}\"");
        //selectBooleanCheckbox.append(" title=\"#{messages.property('" + padt.getDescription() + "')}\"");
        selectBooleanCheckbox.append(" />" + "<br/>" + br);
        return selectBooleanCheckbox.toString();
    }

    /**
     * Creating InputText Component of JSF-Pages
     *
     *
     * @param padt
     * @param context
     * @param component
     * @param beanName
     * @param grid
     */
    private String createInputText(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createInputField(padt));
        out.append(createMessage(padt));
        return out.toString();
    }

    private String createInputTextArea(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append(createLabel(padt));
        out.append(createInputArea(padt));
        out.append(createMessage(padt));
        return out.toString();
    }

    private String createLabel(ParameterADT padt) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();
        out.append("<div class=\"label_wrapper\" title=\"#{messages.property('").append(padt.getDescription()).append("')}");
        // parameter constraints
        String constraints = null;
        if (padt.getType() == ParameterType.INT || padt.getType() == ParameterType.FLOAT) {
            constraints = "#{messages.property('de.unibi.techfak.bibiserv.bibimainapp.input.param.num.VALUE')}";
            if (padt.getMax() != null) {
                if (padt.isMaxIncluded() != null && (Boolean) padt.isMaxIncluded()) {
                    constraints = constraints + " &#8804; " + padt.getMax().toString();
                } else {
                    constraints = constraints + " &lt; " + padt.getMax().toString();
                }
            }
            if (padt.getMin() != null) {
                if (padt.isMinIncluded() != null && (Boolean) padt.isMinIncluded()) {
                    constraints = padt.getMin().toString() + " &#8804; " + constraints;
                } else {
                    constraints = padt.getMin().toString() + " &lt; " + constraints;
                }
            }
            if (constraints.equals("#{messages.property('de.unibi.techfak.bibiserv.bibimainapp.input.param.num.VALUE')}")) {
                constraints = null;
            }

        } else if (padt.getType() == ParameterType.STRING) {
            constraints = "#{messages.property('de.unibi.techfak.bibiserv.bibimainapp.input.param.string.STRINGLENGTH')}";
            if (padt.getMaxLength() != null) {
                constraints = constraints + " &#8804; " + padt.getMaxLength();
            }
            if (padt.getMinLength() != null) {
                constraints = padt.getMinLength() + "  &#8804; " + constraints;
            }
            if (constraints.equals("#{messages.property('de.unibi.techfak.bibiserv.bibimainapp.input.param.string.STRINGLENGTH')}")) {
                constraints = "";
            } else {
                constraints = constraints + ", ";
            }

            if (padt.getRegexp() != null) {
                constraints = constraints + "#{messages.property('de.unibi.techfak.bibiserv.bibimainapp.input.param.string.REGEXP','" + padt.getRegexp().toString().replaceAll("\\\\", "\\\\\\\\") + "')}";
            }

            if (constraints.equals("")) {
                constraints = null;
            }

        } else if (padt.getType() == ParameterType.BOOLEAN) {
            // Boolean type should have no constraints
        } else {
            throw new CodeGenParserException("unsupported parameter type ...");
        }
        if (constraints != null) {
            out.append("&#0010;").append(constraints);
        }

        out.append("\">");
        out.append("<h:outputLabel value=\"#{messages.property('").
                append(padt.getName()).
                append("')}\" for=\"").append(padt.getId()).append("_").append(functionId).append("\" id=\"").
                append(padt.getId()).append("_").append(functionId).append("_label\"/>").append(br);

        out.append("<a class=\"icon\" href=\"").append(toolid).append("?viewType=manual#").append(padt.getId()).append("\">(?)</a>");
        out.append("</div>");


        out.append(br);
        return out.toString();

    }

    /**
     * Provides a HtmlInputText.
     *
     * @param context
     * @param component
     * @return
     */
    private String createInputField(ParameterADT padt) {
        StringBuilder inputField = new StringBuilder();
        inputField.append("<h:inputText");
        inputField.append(" styleClass=\"input_parameter\"");
        inputField.append(" id=\"" + padt.getId() + "_" + functionId + "\"");
        inputField.append(" value=\"#{" + beanName + "." + padt.getId() + "}\"");
        inputField.append("/>  <br/>" + br);
        return inputField.toString();
    }

    /**
     * Provides a HtmlInputTextArea.
     *
     * @param context
     * @param component
     * @return
     */
    private String createInputArea(ParameterADT padt) {
        StringBuilder inputField = new StringBuilder();
        inputField.append("<h:inputTextarea");
        inputField.append(" styleClass=\"input_parameter\"");
        inputField.append(" id=\"" + padt.getId() + "_" + functionId + "\"");
        inputField.append(" value=\"#{" + beanName + "." + padt.getId() + "}\"");
        inputField.append("/>  <br/>" + br);
        return inputField.toString();

    }

    /**
     * Provides a wrapper div.
     *
     * @param context
     * @param component
     * @return
     */
    private String getGroup() {
        StringBuilder panelGroup = new StringBuilder();
        panelGroup.append("<h:panelGroup layout=\"block\"");
        panelGroup.append(" id=\"PARAMETER_WRAPPER_DIV\"");
        panelGroup.append(" style=\"max-width:500px\"/>");
        panelGroup.append(br);
        return panelGroup.toString();
    }

    /**
     * Provides a Message component
     */
    private String createMessage(ParameterADT padt) {
        return "<h:outputText value=\"#{" + functionId + "_param.faultmsg('" + padt.getId() + "_" + functionId + "')}\" "
                + " rendered=\"#{" + functionId + "_param.faultmsgExist('" + padt.getId() + "_" + functionId + "')}\" "
                + "styleClass=\"formblock_normal invalid\" style=\"color: #E32119\"/>" + br;
    }
}
