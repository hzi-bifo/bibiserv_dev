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

import de.unibi.techfak.bibiserv.cms.TinputOutput;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Creates the Input Bean classes.
 *
 * ChangeLog : 15.05.2012 Reorganisation of Input Bean : Now one input bean for 
 *                        each input instead of one bean for all inputs of each
 *                        function.
 *
 * @author jkrueger
 */
public class CodeGen_WebSubmissionBean_Input extends Abstract_CodeGen_WebSubmissionFunctionBean {

    TinputOutput input;

    /**
     * Return id of current input
     *
     * @param args
     * @return
     */
    public String getInputId(String args) {
        return input.getId();
    }
    
    public String getStreamSupport(String args) {
        if(!input.isSetStreamsSupported() || !input.isStreamsSupported()){
            return "false";
        }
        return "true"; 
    }

    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream("/templates/bean_input.java");
    }

    @Override
    public String getClassName(String arg) {
        return input.getId();
    }

    @Override
    public void generate() throws CodeGenParserException {
        /**
         * initialize some variables
         */
        try {
            if (runnableitem.isSetExecutable()) {
                initialize();
                iterate_over_inputs();
            } else {
                log.info("Skip [{}] ... no executable found ...",getClass().getSimpleName() );
            }
        } catch (Exception e) {
            throw new CodeGenParserException(e);
        }
    }

    protected void iterate_over_inputs() throws Exception {
        List<TinputOutput> list_of_inputs = runnableitem.getExecutable().getInput();



        log.info(V,"found {}  inputs ... generate beans for each of it!",list_of_inputs.size());
        for (TinputOutput inp : list_of_inputs) {
            this.input = inp;


            // create package dir if it not exists ..
            File abs_package_dir = new File(srcdir, getPackageName(null).replace('.', '/'));
            if (!abs_package_dir.isDirectory()) {
                abs_package_dir.mkdirs();
            }
            // create class name ...
            File abs_class_file = new File(abs_package_dir, getClassName(null) + ".java");
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


    }

    public String generate_validate(String arg, OntoRepresentation baseType) throws CodeGenParserException {
        StringBuilder out = new StringBuilder();

        out.append("\tpublic boolean validate(String input, String [] args, OntoRepresentation target){").append(br);
        
        out.append("return isValid();").append(br);
        out.append("}").append(br);

        return out.toString();
    }
}
