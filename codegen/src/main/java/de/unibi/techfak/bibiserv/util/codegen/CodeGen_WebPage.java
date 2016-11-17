/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2010-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team"
 * 
 * Contributor(s): Jan Krueger
 * 
 */
package de.unibi.techfak.bibiserv.util.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.unibi.techfak.bibiserv.cms.Tfunction;

/**
 * CodeGen Class for generating general pages.
 * 
 * Supports only one method, and input is filename is the same as out template
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class CodeGen_WebPage
        extends Abstract_CodeGen {

   
    /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public void generate()
            throws CodeGenParserException {
        try {
            if (template == null) {
                throw new CodeGenParserException("Template must not be null!");
                
            }
             
            // create page name ...
            File abs_class_file = new File(pagesdir, resultfilename);
            // create In-/Outputstreams
            InputStream in =  template;
            OutputStream out = new FileOutputStream(abs_class_file);
            // create new CodeGenParser
            CodeGenParser codegenparser = new CodeGenParser(this, template, out);
            // start CodeGenParser
            codegenparser.run();
            // close Streams
            in.close();
            out.close();
        } catch (IOException e) {
            throw new CodeGenParserException(e.getMessage());
        }
    }

    /**
     * Returns the ID of the current runnableItem
     * @param arg
     * @return
     */
    public String getToolId(String arg) {
        return (runnableitem.getId());
    }

    /**
     * Iterate over all functions and replace all occurrences of
     * '___FUNCTION_ID___' with current function id.
     *
     * @param arg
     * @return
     */
    public String for_each_function(String arg) {
        StringBuilder sb = new StringBuilder();
        for (Tfunction func : runnableitem.getExecutable().getFunction()) {
            sb.append(arg.replaceAll("___FUNCTION_ID___", func.getId()));
        }
        return sb.toString();

    }
    
    /**
     * General page has no default, so return  null
     * @return 
     */
    @Override
    public InputStream getDefaultTemplate() {
        return null;
    }
}
