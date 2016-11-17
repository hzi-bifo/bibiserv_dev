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

import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Generate the resetToolbean
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class CodeGen_Session_Reset extends Abstract_CodeGen {

    /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Session_Reset.class.getResourceAsStream("/templates/reset_session.java");
    }


    public String getClassName(String arg) {
        return runnableitem.getId()+"_reset";
    }
    
    public String getBeanIds(String arg) {
        StringBuilder builder = new StringBuilder();
        
        builder.append("\"awsBean\"");
        
        builder.append(", \"toolBean_").append(getToolId(null)).append("_download\"");
        
        if (runnableitem.isSetExecutable()) {
            if (runnableitem.getExecutable().isSetInput()) {
                for (TinputOutput input : runnableitem.getExecutable().getInput()) {
                    builder.append(", \"").append(input.getId()).append("\"");
                }
            }
        } else {
            log.info("Skip [{}#for_all_inputs] ... no executable found!",getClass().getSimpleName());
            
        }
        
        builder.append(", \"").append(getToolId(null)).append("_function\"");
        
        
        if (runnableitem.isSetExecutable()) {

            List<Tfunction> list_of_functions = runnableitem.getExecutable().
                    getFunction();
            for (int i = 0; i < list_of_functions.size(); i++) {
                 Tfunction function = list_of_functions.get(i); 
                 builder.append(", \"").append(function.getId()).append("_param\"");
                 builder.append(", \"").append(function.getId()).append("_result\"");
                 builder.append(", \"").append(function.getId()).append("_resulthandler\"");
                 builder.append(", \"").append(function.getId()).append("_execfunction\"");
                 builder.append(", \"").append(function.getId()).append("_controller\"");
            }
        } else {
            log.info("Skip [{}#generateBeansForFunctions] ... no executable found!",getClass().getSimpleName());
            
        }
        
        return builder.toString();
    }

    @Override
     public void generate() throws CodeGenParserException {
        try {
            if (runnableitem.isSetExecutable()){
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
            } else {
                log.info("Skip [{}] ... no executable found!",getClass().getSimpleName());
                
            }
        } catch (IOException e) {    
            throw new CodeGenParserException(e.getMessage(),e);
        }


    }
     
    public String getPackageName(String arg) {
        return ("de.unibi.techfak.bibiserv.tools." + runnableitem.getId());
    }
    
    public String getToolId(String arg) {
        return runnableitem.getId();
    }

    
    
}
