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

import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Tparam;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Daniel Hagemeier - dhagemei(aet)cebitec.uni-bielefeld.de (initial
 * release) Jan Krueger - jkrueger(aet)cebitec.uni-bielefeld.de
 */
public class CodeGen_WebManual extends CodeGen_Implementation {

    /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public void generate() throws CodeGenParserException {
        if (runnableitem.isSetExecutable()) {
            initialize();
        }
        try {
            // create class name ...
            File abs_class_file = new File(pagesdir + "/manual.xhtml");
            // create In-/Outputstreams
            InputStream in = template == null ? getDefaultTemplate() :template;
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

    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream(
                "/templates/WebManualTemplate");
    }

    public String ifexecutableexists(String arg) {
        if (runnableitem.isSetExecutable()) {
            return arg;
        }
        return "";

    }

    public String for_each_function(String arg) {
        StringBuilder out = new StringBuilder();
        if (runnableitem.isSetExecutable()) {
            for (Tfunction tfunction : runnableitem.getExecutable().getFunction()) {
                out.append(arg.replaceAll("###FUNCTION_ID###", tfunction.getId()));
            }
        }
        return out.toString();
    }

    
    public String for_each_input(String arg) {
        StringBuilder out = new StringBuilder();
        if (runnableitem.isSetExecutable()) {
            for (TinputOutput tio : runnableitem.getExecutable().getInput()) {
                out.append(arg.replaceAll("###INPUT_ID###", tio.getId()));
            }
        }
        return out.toString();
    }

    public String for_each_output(String arg) {
        StringBuilder out = new StringBuilder();
        if (runnableitem.isSetExecutable()) {
            for (TinputOutput tio : runnableitem.getExecutable().getOutput()) {
                out.append(arg.replaceAll("###OUTPUT_ID###", tio.getId()));
            }
        }
        return out.toString();
    }

    public String for_each_parameter(String arg) {
        StringBuilder out = new StringBuilder();
        int counter = 1;
        if (runnableitem.isSetExecutable()) {
            for (Tparam tparam : runnableitem.getExecutable().getParam()) {
                out.append(arg.replaceAll("###PARAMETER_ID###", tparam.getId()).
                        replaceAll("###CSS_CLASS###", (counter % 2 == 0) ? "param_tablerow_odd" : "param_tablerow_even"));
                ++counter;
            }
            for (TenumParam tenum : runnableitem.getExecutable().getEnumParam()) {
                out.append(arg.replaceAll("###PARAMETER_ID###", tenum.getId()).
                        replaceAll("###CSS_CLASS###", (counter % 2 == 0) ? "param_tablerow_odd" : "param_tablerow_even"));
                ++counter;
            }
        }
        return out.toString();
    }
    
    public String getManualId(String arg) {
        if (runnableitem.isSetManual()) {
            return runnableitem.getManual().getId();
        }
        return "none";
    }
    
    
    /**
     * Returns argument, if manual exists in current description.
     * 
     * @param arg
     * @return 
     */
    public String ifmanualexists(String arg) {
        if (runnableitem.isSetManual()) {
            return arg;
        }
        return "";
    }
    
    
    public String ifmanualnotexists(String arg){
        if (!runnableitem.isSetManual()) {
            log.info("Description contains no manual definition. Create a template page for possible later usage!");
            return arg;
        }
        return "";
    }
}
