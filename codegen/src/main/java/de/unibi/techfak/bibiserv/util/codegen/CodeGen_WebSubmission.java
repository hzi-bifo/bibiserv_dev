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

import de.unibi.techfak.bibiserv.cms.Tfunction;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *  Class CodeGen_WebSubmission extends Abstract_CodeGen which means it iterate 
 *  over all functions.
 *
 *  Work on the template 'WebSubmissionInputTemplate', which builds a collapse
 *  IceFaces Page (collapse groups for input, parameter and result) for each
 *  function.
 *
 * @author Daniel Hagemeier - dhagemei(at)cebitec.uni-bielefeld.de
 *         Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class CodeGen_WebSubmission extends CodeGen_Implementation {

    private String extensionname = "";

    /**
     * Implemetation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public void generate()
            throws CodeGenParserException {

        /** initialize some variables, using CodeGen_Implementation initialize method */
        try {
            initialize();
        } catch (Exception e) {
            throw new CodeGenParserException(e);
        }

        try {
            List<Tfunction> list_of_functions = runnableitem.getExecutable().getFunction();

            for (Tfunction tfunction : list_of_functions) {
                function = tfunction;
                log.info(V,"generating submission page for function ",function.getId());
                // create page name ...
                File abs_class_file = new File(pagesdir + System.getProperty("file.separator") + function.getId() + extensionname + ".xhtml");
                // create In-/Outputstreams
                InputStream in = template == null ? getDefaultTemplate() : template;
                OutputStream out = new FileOutputStream(abs_class_file);
                // create new CodeGenParser
                CodeGenParser codegenparser = new CodeGenParser(this, in, out);
                // start CodeGenParser
                codegenparser.run();
                // close Streams
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new CodeGenParserException(e.getMessage());
        }

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

    /**
     * Set the filename extension (before .xthml extension). Should be overwritten
     * by all child classes.
     *
     * @param name
     */
    protected void setExtensionName(String name) {
        extensionname = name;
    }

//    public String replaceStrings(String arg) {
//        String output;
//        output = arg.replaceAll("###TOOLBEAN###","toolBean_" + currentfunction.getId());
//
//        output = output.replaceAll("###FUNCTIONID###",currentfunction.getId());
//
//        output = output.replaceAll("###RESULTBEAN###","resultBean_" + currentfunction.getId());
//
//        return output;
//    }
//
//
//    public String generateWebTabs(String arg) {
//        StringBuffer buffer = new StringBuffer();
//        String result;
//
//        List<Tfunction> list_of_functions = runnableitem.getExecutable().
//                getFunction();
//        for (int i = 0; i < list_of_functions.size(); i++) {
//            //for (Tfunction tfunction : list_of_functions) {
//            currentfunction = list_of_functions.get(i);
//
//            result = arg.replaceAll("###TABID###",currentfunction.getId());
//
//            result = result.replaceAll("###NUMBER###",Integer.toString(i + 1));
//
//            result = result.replaceAll("###TOOLID###",getToolId(null));
//
//            buffer.append(result);
//            //}
//        }
//        return buffer.toString();
//    }
//
//
//    public String generateUpload(String arg) {
//
//        String result = arg;
//
//        for (Inputref ref : currentfunction.getInputref()) {
//
//            TinputOutput input = (TinputOutput) ref.getRef();
//            String inputid = input.getId();
//            result = result.replaceAll("###INPUTID###", inputid);
//
//        }
//
//        return result;
//    }
//    public String generateForeach(String arg) {
//        String result = arg;
//
//        List<> list_of_input =currentfunction.getInputref();
//        for (int i = 0; i < list_of_input.size(); i++) {
//
//            result = result.replaceAll("###GROUPID###","InputGroup" + (i + 1));
//            result = result.replaceAll("###INPUTID###",list_of_input.get(i).getId());
//
//        }
//        //result=replaceStrings(result);
//
//        return result;
//    }
//    /**
//     * Generating Parameters for all Paramgroups
//     *
//     * @return represetinfg the jsf compoinents with all properties...
//     */
//    public String generateParameters(String arg) throws CodeGenParserException {
//
//        StringBuffer out = new StringBuffer();
//
//        ParameterConstructionJSF paramconstruct = new ParameterConstructionJSF();
//
//        System.err.println("Build parameters on JSF page");
//
//        //there is only one paramgroup per function...
//        TparamGroup currentGroup=currentfunction.getParamGroup();
//
//        System.err.println("Generating parameters of function " + currentGroup.getId());
//
//        out.append(paramconstruct.retrieveParameters(currentGroup, currentfunction.getId().toString()));
//
//
//        return out.toString();
//    }
    @Override
    public String getClassName(String arg) {
        if (arg != null) {
            return arg + "_" + function.getId();
        } else {
            return function.getId();
        }
    }

    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream("/templates/WebSubmissionTemplate");
    }
}
