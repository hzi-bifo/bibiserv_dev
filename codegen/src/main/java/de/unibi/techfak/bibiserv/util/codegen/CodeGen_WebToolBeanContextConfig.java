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
import de.unibi.techfak.bibiserv.cms.Tfunction.Inputref;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Create a SpringConfiguration Context for WebInterfaces backing beans
 *
 *
 * @author Daniel Hagemeier - dhagemei[aet]cebitec.uni-bielefeld.de Jan Krueger
 * - jkrueger[aet]cebitec.uni-bielefeld.de
 */
public class CodeGen_WebToolBeanContextConfig
        extends Abstract_CodeGen {

    private Tfunction function;
    private String appendCode;

    /**
     * Implemetation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    public void generate()
            throws CodeGenParserException {

        try {

            // create class name ...
            File abs_class_file = new File(configdir
                    + "/ToolBeanContextConfig.xml");
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
    public String getPackageName(String arg) {
        return ("de.unibi.techfak.bibiserv.tools." + runnableitem.getId()
                + ".web");
    }

    /**
     * Returns the ID of the current runnableItem
     *
     * @param arg
     * @return
     */
    public String getToolId(String arg) {
        return (runnableitem.getId());
    }

    public String generateBeansForFunctions(String arg) {
        StringBuilder buffer = new StringBuilder();
        String result;

        if (runnableitem.isSetExecutable()) {

            List<Tfunction> list_of_functions = runnableitem.getExecutable().
                    getFunction();
            for (int i = 0; i < list_of_functions.size(); i++) {
                //for (Tfunction tfunction : list_of_functions) {
                function = list_of_functions.get(i);
                result = arg.replaceAll("###FUNCTIONID###",
                        function.getId());
                result = result.replaceAll("###TOOLID###",
                        runnableitem.getId());
                
                if (function.isSetInputref()) {
                    StringBuilder sb = new StringBuilder();
                    // determine all input for current function 
                    int n  = 0;
                    for (Inputref inref : function.getInputref()) {
                        TinputOutput in = (TinputOutput)inref.getRef();
                        // create input properties for controller, add number to input if
                        // more than one 
                        if (function.getInputref().size() == 1) {
                            sb.append("<property name=\"input\" ref=\"").append(in.getId()).append("\"/>\n");
                        } else {
                            sb.append("<property name=\"input").append(n).append("\" ref=\"").append(in.getId()).append("\"/>\n");
                        }
                        n++; // increase input counter
                    
                    }
                
                    result = result.replaceAll("###INPUTBEANDECLARATION###",sb.toString());

                } else {
                    result = result.replace("###INPUTBEANDECLARATION###","");
                }
                buffer.append(result);
                //}
            }
        } else {
            log.info("Skip [{}#generateBeansForFunctions] ... no executable found!",getClass().getSimpleName());
           
        }
        return buffer.toString();
    }

    
   public String generateIfExecuteableExists(String arg) {
        if (runnableitem.isSetExecutable()) {
            return arg;
        } else {
            log.info("Skip [{}#generateIfExecuteableExists] ... no executable found!",getClass().getSimpleName());
            
        }
        return "";
   }
    
    
    public String getClassName(String arg) {
        if (arg != null) {
            return arg + "_" + function.getId();
        } else {
            return function.getId();
        }
    }

    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream(
                "/templates/WebToolBeanContextConfigTemplate");
    }

    public String for_all_inputs(String args) {
        StringBuilder buffer = new StringBuilder();
        String result;

        if (runnableitem.isSetExecutable()) {
            
            if (runnableitem.getExecutable().isSetInput()) {
                for (TinputOutput input : runnableitem.getExecutable().getInput()) {
                    result = args.replaceAll("___INPUTID___",input.getId());
                    buffer.append(result);
                }
            }
        } else {
            log.info(V,"Skip [{}] ... no executable found!",getClass().getSimpleName());
        }
        return buffer.toString();
    }
}
