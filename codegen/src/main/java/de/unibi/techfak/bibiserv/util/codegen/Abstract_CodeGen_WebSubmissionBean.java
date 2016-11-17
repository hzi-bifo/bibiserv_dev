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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract Base class for generating tool depending beans
 * 
 *
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 *         
 */
public abstract class Abstract_CodeGen_WebSubmissionBean extends Abstract_CodeGen {

  
    /**
     * Return the full package name of the generated class(es).
     * cms:runnableitem/@id
     *
     * @param arg - not used
     * @return Return the full package name of the generated class(es).
     */
    public String getPackageName(String arg) {
        return ("de.unibi.techfak.bibiserv.tools." + runnableitem.getId() + ".web");
    }

    /**
     * Return the classname of current generated class
     * (== cms:runnableitem/cms:function/@id)
     *
     * @param arg
     * @return Return the classname of current generated class.
     */
    public abstract String getClassName(String arg);
    
    /**
     * Return the id of current tool.
     *
     * @param arg
     * @return Return the id of current tool.
     */
    public String getToolId(String arg) {
        return runnableitem.getId();
    }

    /**
     * Iterate over all functions and replace all occurences of ___FUNCTIONID___ within the argument String with the function id.
     *
     * @param arg
     * @return
     */
    public String for_each_function(String arg) {
        StringBuilder out = new StringBuilder();
        for (Tfunction func : runnableitem.getExecutable().getFunction()) {
            out.append(arg.
                    replaceAll("___FUNCTIONID___", func.getId()).
                    replaceAll("___fUNCTIONID___", func.getId().substring(0,1).toUpperCase()+func.getId().substring(1)));
        }
        return out.toString();
    }

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
            e.printStackTrace();
            throw new CodeGenParserException(e.getMessage());
        }


    }
}
