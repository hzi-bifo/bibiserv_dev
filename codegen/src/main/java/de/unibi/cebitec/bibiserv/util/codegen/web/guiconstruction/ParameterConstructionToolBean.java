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
import de.unibi.techfak.bibiserv.cms.Tprimitive;
import de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class generates necessary beans (setter, getter, validator) to handle all
 * parameters belonging to current function.
 *
 * @author Daniel Hagemeier - dhagemei(aet)cebitec.uni-bielefeld.de (inital and
 * main work) Sven Hartmeier - shartmei(aet)cebitec.uni-bielefeld.de Jan Krueger
 * - jkrueger(aet) cebitec.uni-bielefeld.de
 */
public class ParameterConstructionToolBean {

    private StringBuffer paramstring;
    private String beanName;
    private String toolid;
    private String functionId;
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
    private final String br = System.getProperty("line.separator");

    public enum ParamTyp {

        ENUM, PARAM, ALL
    }

    public ParameterConstructionToolBean(String toolid) {
        this.toolid = toolid;
    }

    public List<String> getParameterIdList(TparamGroup paramGroup, ParamTyp pt) {
        List<String> pl = new ArrayList();

        List<Object> paramlist = paramGroup.getParamrefOrParamGroupref();
        for (Object param : paramlist) {
            if (param instanceof TparamGroup.ParamGroupref) {
                TparamGroup.ParamGroupref groupref = (TparamGroup.ParamGroupref) param;
                if (groupref.getRef() instanceof TparamGroup) {
                    TparamGroup group = (TparamGroup) groupref.getRef();
                    pl.addAll(getParameterIdList(group,pt));
                }
            }
            if (param instanceof TparamGroup.Paramref) {
                TparamGroup.Paramref paramref = (TparamGroup.Paramref) param;
                
                // An dieser Stelle muss ich zwischen Parametern/SelectOneEnum 
                // und SelectMultiEnum unterscheiden ...
                if ((paramref.getRef() instanceof Tparam) && (pt.equals(ParamTyp.PARAM) || pt.equals(ParamTyp.ALL))) {
                    Tparam paramSingle = (Tparam) paramref.getRef();
                    pl.add(paramSingle.getId());
                }
                if ((paramref.getRef() instanceof TenumParam)) {
                    TenumParam paramenum = (TenumParam) paramref.getRef();
                    if (pt.equals(ParamTyp.ALL))  {
                        pl.add(paramenum.getId());
                    } else {
                        if (paramenum.isSetMaxoccurs() && (paramenum.getMaxoccurs() > 1)) {
                            if (pt.equals(ParamTyp.ENUM)) {
                                pl.add(paramenum.getId());
                            }
                        } else {
                            if (pt.equals(ParamTyp.PARAM)) {
                                pl.add(paramenum.getId());
                            }
                        }
                    }
                }
            }
        }
        return pl;
    }

    /**
     * Generation Method for the getters, setters and validators in the ToolBean
     *
     * @param paramGroup
     * @param functionId
     * @return
     * @throws CodeGenParserException
     */
    public String makeParamGetterSetterValidator(TparamGroup paramGroup, String functionId) throws CodeGenParserException {

        HashMap<String, ParameterADT> paramItemMap = new HashMap<String, ParameterADT>();
        List<Object> paramlist = paramGroup.getParamrefOrParamGroupref();
        StringBuilder paramcode = new StringBuilder();
        beanName = "toolBean_" + functionId;
        this.functionId = functionId;
        for (Object param : paramlist) {

            if (param instanceof TparamGroup.ParamGroupref) {
                TparamGroup.ParamGroupref groupref = (TparamGroup.ParamGroupref) param;
                if (groupref != null && groupref.getRef() instanceof TparamGroup) {
                    TparamGroup group = (TparamGroup) groupref.getRef();
                    if (group != null) {
                        paramcode.append(makeParamGetterSetterValidator(group, functionId));
                    }
                }
            }

            if (param instanceof TparamGroup.Paramref) {
                TparamGroup.Paramref paramref = (TparamGroup.Paramref) param;
                if (paramref != null) {

                    if (paramref.getRef() instanceof Tparam) {
                        Tparam paramSingle = (Tparam) paramref.getRef();
                        if (paramSingle != null) {
                            id = paramSingle.getId();
                            // create doc
                            paramcode.append("/*--- parameter '").append(id).append("' --- */");
                            // create help bean
                            paramcode.append("private boolean help_").append(id).append(" = false;");
                            paramcode.append("public boolean isHelp_").append(id).append("(){").append(" return help_").append(id).append(";}");
                            // create help action
                            paramcode.append("public void helpAction_").append(id).append("(ActionEvent e){").append("help_").append(id).append(" = !help_").append(id).append(";}");

                            paramcode.append("private String ").append(id).append(" = defaultValue_").append(id).append(";").append(br);
                            paramcode.append("private boolean valid_").append(id).append(" = true;").append(br);

                            /* Ugly if block ... we MUST build different getter/setter for type BOOLEAN depended
                             * on visualiation. If anyone knows how to solve this in a better way ... feel free
                             to modify the code */
                            if (paramSingle.getType().equals(Tprimitive.BOOLEAN)) {
                                if (paramSingle.getGuiElement().equalsIgnoreCase("INPUTTEXT")) {
                                    paramcode.append("public String get").append(id.substring(0, 1).toUpperCase()).append(id.substring(1)).append("(){return ").append(id).append(";}");
                                    paramcode.append("public void set").append(id.substring(0, 1).toUpperCase()).append(id.substring(1)).append("(String input){").append(br);
                                    paramcode.append("if (!").append(id).append(".equals(input)) {").append(br);

                                    paramcode.append("this.").append(id).append("=Boolean.parseBoolean(input).toString();").append(br);
                                    paramcode.append("}").append(br).append("}").append(br);
                                } else {
                                    paramcode.append("public Boolean get").append(id.substring(0, 1).toUpperCase()).append(id.substring(1)).append("(){").append(br);
                                    paramcode.append("return  Boolean.parseBoolean(").append(id).append(");").append(br);
                                    paramcode.append("}").append(br);
                                    paramcode.append("public void set").append(id.substring(0, 1).toUpperCase()).append(id.substring(1)).append("(Boolean input){");
                                    paramcode.append("if (Boolean.parseBoolean(").append(id).append(") != input ){").append(br);

                                    paramcode.append("this.").append(id).append("=input.toString();").append(br);
                                    paramcode.append("}").append(br).append("}").append(br);
                                }
                            } else {
                                // create setter and getter
                                paramcode.append("public String get").append(id.substring(0, 1).toUpperCase()).append(id.substring(1)).append("(){return ").append(id).append(";}");
                                paramcode.append("public void set").append(id.substring(0, 1).toUpperCase()).append(id.substring(1)).append("(String input){").append(br);
                                paramcode.append("if (!").append(id).append(".equals(input)) {").append(br);

                                paramcode.append("this.").append(id).append("=input;").append(br);
                                paramcode.append("try {").append(br).append("if (!input.isEmpty()) {").append(br);

                                if (paramSingle.getType().equals(Tprimitive.INT)) {
                                    paramcode.append("int tmp =  Integer.parseInt(input);").append(br);
                                } else if (paramSingle.getType().equals(Tprimitive.FLOAT)) {
                                    paramcode.append("float tmp =  Float.parseFloat(input);").append(br);
                                } else {
                                    paramcode.append("String tmp = input;").append(br);
                                }
                                paramcode.append("Utilities.validate_").append(id).append("(tmp,messages);").append(br);
                                paramcode.append("}").append(br);
                                paramcode.append("//remove possible previous set faultmsg").append(br);
                                paramcode.append("faultmsg.remove(\"").append(id).append('_').append(functionId).append("\");").append(br);
                                paramcode.append("valid_").append(id).append(" = true;").append(br);
                                paramcode.append("return;").append(br);
                                if (!paramSingle.getType().equals(Tprimitive.STRING)) {
                                    paramcode.append("}catch (NumberFormatException e) {").append(br);
                                    if (paramSingle.getType().equals(Tprimitive.INT)) {
                                        paramcode.append("faultmsg.put(\"").append(id).append('_').append(functionId).append("\",messages.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.num.INTEGER\"));").append(br);

                                    } else {
                                        paramcode.append("faultmsg.put(\"").append(id).append('_').append(functionId).append("\",messages.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.num.FLOAT\"));").append(br);
                                    }
                                }
                                paramcode.append("}catch (ValidationException e) {").append(br).append("faultmsg.put(\"").append(id).append('_').append(functionId).append("\",e.getMessage());").append(br);
                                paramcode.append("}").append(br);
                                paramcode.append("valid_").append(id).append(" = false;");
                                paramcode.append("}").append(br).append("}").append(br);
                            }
                        }

                    }

                    if (paramref.getRef() instanceof TenumParam) {

                        TenumParam paramEnum = (TenumParam) paramref.getRef();
                        if (paramEnum != null) {
                            id = paramEnum.getId();
                            String returntype;
                            ParameterJsfElement jsfElem = ParameterJsfElement.valueOf(paramEnum.getGuiElement().toString().toUpperCase());
                            log.info(V,"***----------JSF element is : {}",jsfElem);
                            switch (jsfElem) {
                                //TODO SH,20100702: THIS SWITCH DOES NOT SEEM TO WORK, AS WE NEVER SEE String[] in generated code
                                case SELECTMANYMENU:
                                    // log.info(V,"***----------SELECTMANYMENU");
                                    returntype = "String[]";
                                    break;
                                case SELECTMANYLISTBOX:
                                    // log.info(V,"***----------SELECTMANYLISTBOX");
                                    returntype = "String[]";
                                    break;
                                case SELECTMANYCHECKBOX:
                                    // log.info(V,"***----------SELECTMANYCHECKBOX");
                                    returntype = "String[]";
                                    break;
                                case SELECTONEMENU:
                                    // log.info(V,"***----------SELECTONEMENU");
                                    returntype = "String";
                                    break;
                                case SELECTONERADIO:
                                    // log.info(V,"***----------SELECTONERADIO");
                                    returntype = "String";
                                    break;
                                case SELECTONELISTBOX:
                                    // log.info(V,"***----------SELECTONELISTBOX");
                                    returntype = "String";
                                    break;
                                case INPUTTEXT:
                                    // log.info(V,"***----------INPUTTEXT");
                                    returntype = "String";
                                    break;
                                case INPUTTEXTAREA:
                                    // log.info(V,"***----------INPUTTEXTAREA");
                                    returntype = "String";
                                    break;
                                default:
                                    throw new CodeGenParserException("Unkown or unsupported jsfElement guielement!");

                            }
                            // create doc
                            paramcode.append("/*--- parameter '").append(id).append("' --- */").append(br);

                            // create help bean
                            paramcode.append("private boolean help_").append(id).append(" = false;").append(br);

                            paramcode.append("public boolean isHelp_").append(id).append("(){").append(br).
                                    append(" return help_").append(id).append(";").append(br).
                                    append(" }").append(br);
                            // create help action
                            paramcode.append("public void helpAction_" + id + "(ActionEvent e){" + br
                                    + "help_" + id + " = !help_" + id + ";" + br
                                    + "} " + br);

                            paramcode.append("private " + returntype + " " + id + "=defaultValue_" + id + ";" + br);
                            paramcode.append("private boolean valid_").append(id).append("= true;").append(br);
                            paramcode.append("public " + returntype + " get" + id.substring(0, 1).toUpperCase() + id.substring(1) + "(){" + br
                                    + "return " + id + ";" + br
                                    + "}" + br);
                            paramcode.append("public void set").append(id.substring(0, 1).toUpperCase()).append(id.substring(1)).append("(").append(returntype).append(" input){").append(br);
                            paramcode.append("if (!").append(id).append(".equals(input)) {").append(br);

                            paramcode.append("try {").append(br);
                            paramcode.append("Utilities.validate_").append(id).append("(input,messages);").append(br);
                            paramcode.append("this.").append(id).append("=input;").append(br);
                            paramcode.append("faultmsg.remove(\"").append(id).append('_').append(functionId).append("\");").append(br);
                            paramcode.append("valid_").append(id).append(" = true;").append(br);
                            paramcode.append("return;").append(br);
                            paramcode.append("} catch (ValidationException e){").append(br);
                            paramcode.append("faultmsg.put(\"").append(id).append('_').append(functionId).append("\",e.getMessage());");
                            paramcode.append("}").append(br).append("valid_").append(id).append(" = false;").append(br);
                            paramcode.append("}").append(br).append("}").append(br);
                        }

                    }
                }
            }

        }

        return paramcode.toString();

    }

    /**
     * Generation of default values that are placed inside the toolbean and an
     * method that reset all beans to the default value;
     *
     * @param paramGroup
     * @param functionId
     * @return
     * @throws CodeGenParserException
     */
    public String retrieveDefaultValues(TparamGroup paramGroup, String functionId) throws CodeGenParserException {
        StringBuilder paramcode = new StringBuilder();
        StringBuilder resetcode = new StringBuilder();
        StringBuilder checkcode = new StringBuilder();
        resetcode.append("public void reset(){").append(br);
        checkcode.append("public boolean defaultParams(){").append(br);
        checkcode.append("if (true ").append(br);
        retrieveDefaultValues(
                paramGroup, functionId, paramcode, resetcode, checkcode);
        //resetcode.append("// Attention EnumParam still missing ").append(br);
        resetcode.append("}").append(br);
        checkcode.append("){").append(br);
        checkcode.append("return true;").append(br).append("} else { return false; }").append(br).append("}").append(br);

        return paramcode.toString() + br + br + resetcode.toString() + br + br + checkcode.toString() + br;

    }

    public String retrieveDefaultValues(TparamGroup paramGroup, String functionId, StringBuilder paramcode, StringBuilder resetcode, StringBuilder checkcode) throws CodeGenParserException {
        List<Object> paramlist = paramGroup.getParamrefOrParamGroupref();
        beanName = "toolBean_" + functionId;

        this.functionId = functionId;

        for (Object param : paramlist) {
            try {
                if (param.getClass().getSimpleName().equalsIgnoreCase("ParamGroupref")) {
                    TparamGroup.ParamGroupref ref = (TparamGroup.ParamGroupref) param;

                    if (ref != null) {
                        if (ref.getRef().getClass().getSimpleName().equalsIgnoreCase("TparamGroup")) {
                            TparamGroup group = (TparamGroup) ref.getRef();

                            if (group != null) {
                                paramcode.append(retrieveDefaultValues(group, functionId, paramcode, resetcode, checkcode));

                            }
                        }
                    }
                }
            } catch (ClassCastException e) {
                throw new CodeGenParserException(e);

            }

            try {
                if (param.getClass().getSimpleName().equalsIgnoreCase("Paramref")) {
                    TparamGroup.Paramref group = (TparamGroup.Paramref) param;

                    if (group != null) {
                        if (group.getRef().getClass().getSimpleName().equalsIgnoreCase("Tparam")) {
                            Tparam paramSingle = (Tparam) group.getRef();

                            if (paramSingle != null) {
                                /* build reset code function */
                                resetcode.append(paramSingle.getId()).append(" = defaultValue_").append(paramSingle.getId()).append(";").append(br);
                                /* build check code function */
                                checkcode.append("&& ").append(paramSingle.getId()).append(".equals(defaultValue_").append(paramSingle.getId()).append(")").append(br);
                                /* build param code function */

                                if (paramSingle.getType().toString().equalsIgnoreCase("INT") || paramSingle.getType().toString().equalsIgnoreCase("FLOAT")) {
                                    if (paramSingle.isSetDefaultValue()) {
                                        paramcode.append("private static final String defaultValue_" + paramSingle.getId() + "=\"" + paramSingle.getDefaultValue() + "\";" + br);

                                    } else {
                                        paramcode.append("private static final String defaultValue_" + paramSingle.getId() + "=\"\"; //no default specified" + br);

                                    }
                                } else if (paramSingle.getType().toString().equalsIgnoreCase("STRING")) {
                                    if (paramSingle.isSetDefaultValue()) {
                                        paramcode.append("private static final String defaultValue_" + paramSingle.getId() + "=\"" + paramSingle.getDefaultValue() + "\";" + br);

                                    } else {
                                        paramcode.append("private static final String defaultValue_" + paramSingle.getId() + "=\"\"; //no default specified" + br);

                                    }
                                } else if (paramSingle.getType().toString().equalsIgnoreCase("BOOLEAN")) {
                                    if (paramSingle.isSetDefaultValue()) {
                                        paramcode.append("private static final String defaultValue_" + paramSingle.getId() + "=\"" + paramSingle.getDefaultValue() + "\";" + br);

                                    } else {
                                        paramcode.append("private static final String defaultValue_" + paramSingle.getId() + "=\"\"; //no default specified" + br);

                                    }
                                }

                            }

                        }
                    }

                }
            } catch (ClassCastException e) {
                System.err.println("Class cast exception occured " + e);
                throw new CodeGenParserException(e);
            } catch (Exception ex) {
                System.err.println("Exception occured: " + ex);
                throw new CodeGenParserException(ex);
            }

            try {
                if (param.getClass().getSimpleName().equalsIgnoreCase("Paramref")) {
                    TparamGroup.Paramref group = (TparamGroup.Paramref) param;
                    if (group != null) {
                        if (group.getRef().getClass().getSimpleName().equalsIgnoreCase("TenumParam")) {
                            TenumParam paramEnum = (TenumParam) group.getRef();
                            if (paramEnum != null) {
                                
                                /* build reset code function */
                                resetcode.append(paramEnum.getId()).append(" = defaultValue_").append(paramEnum.getId()).append(";").append(br);

                                /* build param code function */                                
                                String returntype;
                                ParameterJsfElement jsfElem = ParameterJsfElement.valueOf(paramEnum.getGuiElement().toString().toUpperCase());
                                switch (jsfElem) {
                                    case SELECTMANYMENU:
                                        System.err.println("***----------SELECTMANYMENU");
                                        returntype = "String[]";
                                        break;

                                    case SELECTMANYLISTBOX:
                                        System.err.println("***----------SELECTMANYLISTBOX");
                                        returntype = "String[]";
                                        break;

                                    case SELECTMANYCHECKBOX:
                                        System.err.println("***----------SELECTMANYCHECKBOX");
                                        returntype = "String[]";
                                        break;

                                    case SELECTONEMENU:
                                        returntype = "String";
                                        break;

                                    case SELECTONERADIO:
                                        returntype = "String";
                                        break;

                                    case SELECTONELISTBOX:
                                        returntype = "String";
                                        break;

                                    default:
                                        System.err.println("***----------DEFAULT");
                                        returntype = "String";
                                }

                                paramcode.append("private static final " + returntype + " defaultValue_" + paramEnum.getId() + "=");
                                if (paramEnum.getGuiElement().startsWith("SELECTMANY")) {
                                    StringBuilder arraystring = new StringBuilder();
                                    //creating a list of default values...
                                    for (TenumValue enumValue : paramEnum.getValues()) {
                                        if (enumValue.isDefaultValue()) {
                                            arraystring.append("\"" + enumValue.getKey().toString() + "\",");
                                        }
                                    }
                                    //check if we have any content, if yes, remove last comma&add content to paramcode, if no, create empty default
                                    if (arraystring.length() > 0) {
                                        arraystring.deleteCharAt(arraystring.length() - 1);
                                        paramcode.append("{" + arraystring + "};");
                                    } else {
                                        paramcode.append("{\"\"};");
                                    }
                                    
                                    /* build check code function */                         
                                    checkcode.append("&& Arrays.equals(").append(paramEnum.getId()).append(", defaultValue_").append(paramEnum.getId()).append(")").append(br);
                                    
                                } else {
                                    //creating the one default value string
                                    //check if at least one value has been set...
                                    Boolean isSet = false;
                                    for (TenumValue enumValue : paramEnum.getValues()) {
                                        if (enumValue.isDefaultValue()) {
                                            isSet = true;
                                            paramcode.append("\"" + enumValue.getValue().toString() + "\";" + br);
                                            //defaultValueStringList.add(enumValue.getKey().toString());
                                        }
                                    }
                                    if (isSet == false) {
                                        paramcode.append("\"\";" + br);
                                    }
                                    
                                    /* build check code function */
                                    checkcode.append("&& ").append(paramEnum.getId()).append(".equals(defaultValue_").append(paramEnum.getId()).append(")").append(br);                       
                                }
                            }
                        }
                    }
                }
            } catch (ClassCastException e) {
                throw new CodeGenParserException(e);
            }
        }
        paramcode.append(br);
        return "";
    }

    /* used by Controller bean */
    public String createPairList(TparamGroup paramGroup, String functionId) throws CodeGenParserException {

        HashMap<String, ParameterADT> paramItemMap = new HashMap<String, ParameterADT>();
        List<Object> paramlist = paramGroup.getParamrefOrParamGroupref();
        StringBuilder paramcode = new StringBuilder();
        beanName = "toolBean_" + functionId;

        this.functionId = functionId;
        int i = 0;

        for (Object param : paramlist) {
            try {
                if (param.getClass().getSimpleName().equalsIgnoreCase("ParamGroupref")) {
                    TparamGroup.ParamGroupref ref = (TparamGroup.ParamGroupref) param;
                    if (ref != null) {
                        if (ref.getRef().getClass().getSimpleName().equalsIgnoreCase("TparamGroup")) {
                            TparamGroup group = (TparamGroup) ref.getRef();
                            if (group != null) {
                                paramcode.append(createPairList(group, functionId));
                            }
                        }
                    }
                }
            } catch (ClassCastException e) {
                throw new CodeGenParserException(e);
            }

            try {
                if (param.getClass().getSimpleName().equalsIgnoreCase("Paramref")) {
                    TparamGroup.Paramref paramref = (TparamGroup.Paramref) param;

                    if (paramref != null) {
                        if (paramref.getRef().getClass().getSimpleName().equalsIgnoreCase("Tparam")) {
                            Tparam paramSingle = (Tparam) paramref.getRef();
                            if (paramSingle != null) {
                                id = paramSingle.getId();
                                String tmp = "param.get" + id.substring(0, 1).toUpperCase() + id.substring(1) + "().toString()";
                                paramcode.append("if (!" + tmp + ".isEmpty()) {").append(br);
                                paramcode.append("Pair pair_" + id + " = new Pair<String, String>(\"" + id + "\"," + tmp + ");").append(br);
                                paramcode.append("pairlist.add(pair_" + id + "); ").append(br);
                                paramcode.append("}").append(br);
                            }
                            ++i;
                        } else if (paramref.getRef().getClass().getSimpleName().equalsIgnoreCase("TenumParam")) {
                            TenumParam paramEnum = (TenumParam) paramref.getRef();
                            id = paramEnum.getId();
                            String tmp = "param.get" + id.substring(0, 1).toUpperCase() + id.substring(1) + "().toString()";
                            paramcode.append("if (!" + tmp + ".isEmpty()) {").append(br);
                            paramcode.append("Pair pair_" + id + " = new Pair<String, String>(\"" + id + "\"," + tmp + ");" + br);
                            paramcode.append("pairlist.add(pair_" + id + "); " + br);
                            paramcode.append("}").append(br);
                            ++i;
                        }
                    }
                }
            } catch (ClassCastException e) {
                throw new CodeGenParserException(e);
            } catch (Exception e) {
                throw new CodeGenParserException(e);
            }
        }
        return paramcode.toString();
    }
}
