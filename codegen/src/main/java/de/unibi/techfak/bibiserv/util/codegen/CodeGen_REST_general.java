/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
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
 * "Portions Copyrighted 2013 BiBiServ"
 *  
 * Contributor(s): Jan Krueger
 * 
 */
package de.unibi.techfak.bibiserv.util.codegen;

import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * CodeGen_REST_general extends Abstract_CodeGen
 * 
 * Provides general rest method(s).
 * 
 *
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 *         
 */
public class CodeGen_REST_general extends Abstract_CodeGen {

    @Override
    public  InputStream getDefaultTemplate(){
        return CodeGen_Implementation.class.getResourceAsStream("/templates/rest_general.java");
    }


    /**
     * Return the full package name of the generated class(es).
     * cms:runnableitem/@id
     *
     * @param arg - not used
     * @return Return the full package name of the generated class(es).
     */
    public String getPackageName(String arg) {
        return ("de.unibi.techfak.bibiserv.tools." + runnableitem.getId()+".rest");
    }
    
    
    /**
     * Return the class name 
     * @param arg - not used
     * 
     * @return Return the class name of the generated class
     */
    public String getClassName(String arg) {
        return "general";
    }
    
    
    /**
     * Id of current tool
     * 
     * @param arg
     * @return 
     */
    public String getToolId(String arg) {
        return runnableitem.getId();
    }
    
    /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public void generate()
            throws CodeGenParserException {
        if (runnableitem.isSetExecutable()) {
            try {
                // create class name ...
                File abs_class_file = new File(srcdir + "/"+ getPackageName(null).replaceAll("\\.","/"),"/"+getClassName(null)+".java");
                
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
                
                throw new CodeGenParserException(e.getMessage());
            }
        } else {
            log.info("Skip [" + getClass().getSimpleName() + "] ... no executable found ...");
        }
    }
}
