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
package de.unibi.techfak.bibiserv.util.codegen;

import de.unibi.cebitec.bibiserv.util.codegen.web.guiconstruction.ParameterConstructionToolBean;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Tfunction.Inputref;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Tparam;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Creates the Bean classes (controller, input, param, result) for each function
 * of current tool.
 *
 * Initial work was done by Daniel Hagemeier.
 *
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de Daniel Hagemeier
 * - dhagemei[aet]cebitec.uni-bielefeld.de
 */
public abstract class Abstract_CodeGen_WebSubmissionFunctionBean extends CodeGen_Implementation {

    /*
     * classprefix - effect classname and generated file. Use setter method to
     * modify content in e.g. subclass constructor
     */
    private String classprefix = "ToolBean";

    /**
     * Set the ClassPrefix, has effect on classname and generated file
     *
     * @param classprefix
     */
    public void setClassprefix(String classprefix) {
        this.classprefix = classprefix;
    }

    /**
     * @return Return ClassPrefix
     */
    public String getClassprefix() {
        return classprefix;
    }

    /**
     * Iterate over all inputs of current functions -- by Jan
     *
     * @param args
     * @return
     */
    public String for_each_input(String arg) {
        StringBuilder out = new StringBuilder();
        //log.info(V,"Call for_each_input - START");
        int n = 0;
        // iterate over all inputs
        for (Inputref inputref : function.getInputref()) {
            String argclone = new String(arg);
            TinputOutput input = (TinputOutput) inputref.getRef();
            String inputid = input.getId();
            log.info(V,"replace all occurences of ___INPUT_ID___ with '{}'",inputid);
            // replace of ___INPUT_ID___
            argclone = argclone.replaceAll("___INPUT_ID___", inputid);
            // replace of ___NUMBER___ with empty String or increasing number if more than one input
            if (function.getInputref().size() == 1) {
                argclone = argclone.replaceAll("___NUMBER___", "");
            } else {
                argclone = argclone.replaceAll("___NUMBER___", new Integer(n).toString());
            }
            out.append(argclone);
            n++;
        }
        //log.info(V,"Call for_each_input - END");
        return out.toString();

    }

    private String for_each_param_and_enum_helper(String arg, ParameterConstructionToolBean.ParamTyp pt) {
        StringBuilder out = new StringBuilder();
        ParameterConstructionToolBean paramconstruct = new ParameterConstructionToolBean(runnableitem.getId());

        if (function.getParamGroup() == null) {
            return "";
        }
        // iterate over all params
        for (String paramid : paramconstruct.getParameterIdList(function.getParamGroup(), pt)) {
            String rep = arg.replaceAll("___PARAM_ID___", paramid);
            // if paramr
            TenumParam enumParam = getEnumParamById(paramid);
            if ( enumParam != null) {
                String separator = enumParam.isSetSeparator() ? enumParam.getSeparator() : ",";

                rep = rep.replaceAll("___SEPARATOR___", separator);
            }
            out.append(rep);
        }

        return out.toString();

    }

    /**
     * Iterate over all enums used by current functions and replace the
     * following substring
     *
     * <table>
     * <tr>
     * <th>substring</th><th>replacement</th>
     * </tr>
     * <tr>
     * <td>___PARAM_ID___</td><td> parameter id </td>
     * </tr>
     * <tr>
     * <td>
     *
     * </table>
     *
     * @param args
     * @return
     */
    public String for_each_enum(String arg) {
        return for_each_param_and_enum_helper(arg, ParameterConstructionToolBean.ParamTyp.ENUM);
    }

    public String for_each_param(String arg) {
        return for_each_param_and_enum_helper(arg, ParameterConstructionToolBean.ParamTyp.PARAM);
    }

    public String for_each_param_enum(String arg) {
        return for_each_param_and_enum_helper(arg, ParameterConstructionToolBean.ParamTyp.ALL);
    }

    /**
     * Return TenumParam object with given id. Return 'null' if no enumParam
     * exists.
     *
     * @param id
     * @return
     */
    public TenumParam getEnumParamById(String id) {
        List<TenumParam> lep = runnableitem.getExecutable().getEnumParam();

        for (TenumParam ep : lep) {
            if (ep.getId().equals(id)) {
                return ep;
            }
        }
        return null;
    }

    /**
     * Return TParam object with given id. Return 'null' if no parama with given
     * id exists.
     *
     * @param id
     * @return
     */
    public Tparam getParamById(String id) {
        List<Tparam> lp = runnableitem.getExecutable().getParam();
        for (Tparam p : lp) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    /**
     *
     * @param args
     * @return
     */
    public String generateInputDefaults(String args) {
        StringBuilder out = new StringBuilder();
        for (Inputref inputref : function.getInputref()) {
            TinputOutput input = (TinputOutput) inputref.getRef();

            if (input.isSetExample()) {

                out.append("String sample_").append(input.getId()).append(" = ");

                byte[] tmp = input.getExample();
                if (tmp.length == 0) {
                    out.append("\"Bad luck ... no example given by author!\";").append(br);
                } else {
                    out.append("\"");
                    try {
                        String tmpstr = new String(tmp, "UTF-8");
                        out.append(tmpstr.replaceAll("(\n|\r\n|\r)", "\"+br+\n\"")); // remark : br is defined as constant in template
                    } catch (UnsupportedEncodingException e) {
                        System.err.println(e.getMessage());
                        out.append(e.getMessage());
                    }
                    out.append("\";").append(br);
                }
            }
        }
        return out.toString();

    }

    /**
     * Generate all parameter default for this function. -- by Daniel,
     * modified/restructured by JK
     *
     * @param args
     * @return
     * @throws CodeGenParserException
     */
    public String generateParamDefaults(String args) throws CodeGenParserException {
        ParameterConstructionToolBean paramconstruct = new ParameterConstructionToolBean(runnableitem.getId());
        if (function.getParamGroup() == null) {
            return "public void reset(){}";
        }
        return paramconstruct.retrieveDefaultValues(function.getParamGroup(), function.getId());
    }

    /**
     * Generate all setter and getter for all parameters. -- by Daniel,
     * modified/restructured by JK
     *
     * @param args
     * @return
     * @throws CodeGenParserException
     */
    public String generateParamGetterSetterValidator(String args)
            throws CodeGenParserException {
        if (function.getParamGroup() == null) {
            return "";
        }

        ParameterConstructionToolBean paramconstruct = new ParameterConstructionToolBean(runnableitem.getId());
        return paramconstruct.makeParamGetterSetterValidator(function.getParamGroup(),
                function.getId());
    }

    /**
     * -- by Daniel
     *
     * @param arg
     * @return
     * @throws CodeGenParserException
     */
    public String generateCalculation(String arg) throws CodeGenParserException {

        StringBuilder code = new StringBuilder();
        ParameterConstructionToolBean params = new ParameterConstructionToolBean(runnableitem.getId());
        code.append(params.createPairList(function.getParamGroup(),
                function.getId()));

        return code.toString();
    }

    /**
     * Return the full package name of the generated class(es).
     * cms:runnableitem/@id
     *
     * @param arg - not used
     * @return Return the full package name of the generated class(es).
     */
    @Override
    public String getPackageName(String arg) {
        return ("de.unibi.techfak.bibiserv.tools." + runnableitem.getId() + ".web");
    }

    public String validate_each_input(String args) {
        StringBuilder out = new StringBuilder();

        for (int c = 0; c < function.getInputref().size(); ++c) {
            TinputOutput input = (TinputOutput) function.getInputref().get(c).getRef();
            if (c != 0) {
                out.append(" && ");
            }
            out.append("input.isValid_").append(input.getId()).append("()");
        }

        return out.toString();
    }

    /**
     * Generate a lot of "if check" to determine the correct request function
     *
     * @param arg
     * @return
     */
    public String generate_exec_request(String arg) {

        StringBuilder out = new StringBuilder();
        StringBuilder inputparam = new StringBuilder();

        for (Pair<String, OntoRepresentation> input : fct_input_rep.get(function.getId())) {
            inputparam.append(", ").append(input.getKey()).append(".getInput().getInput()")
                    .append(", ").append(input.getKey()).append(".getInput().getChosen()")
                    .append(", ").append(input.getKey()).append(".supportsStreamedInput()")
                    .append(", ").append(input.getKey()).append(".getInput().isSkipValidation()");
        }

        out.append("requestid = execfunction.request(pairlist, accesskey, secretkey, sessiontoken ").append(arg).append(inputparam)
                .append(");").append(br);
        return out.toString();
    }

}
