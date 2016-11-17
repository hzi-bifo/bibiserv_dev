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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Generates submission entry page with function selection.
 * but only if more than one function is defined.
 * 
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 */
public class CodeGen_WebSubmissionPage
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
            if (runnableitem.isSetExecutable()) {
            // create class name ...
            File abs_class_file = new File(pagesdir + "/submission.xhtml");
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

    /**
     * Returns the ID of the current runnableItem
     * @param arg
     * @return
     */
    public String getToolId(String arg) {
        return (runnableitem.getId());
    }

    /**
     * Iterate over all functions provided by the tool an replaces all
     * occurrences  of String "___FUNCTION_ID___" with the current function id
     * and all occurrences of String  "___FUNCTION_COUNTER___" with current
     * function number (starting with 0)
     *
     * @param arg
     * @return
     */
    public String for_each_function(String arg) {
        StringBuilder out = new StringBuilder();
        int counter = 0;
        for (Tfunction function : runnableitem.getExecutable().getFunction()) {
            out.append(arg.replaceAll("___FUNCTION_ID___", function.getId()).replaceAll("___FUNCTION_COUNTER___", new Integer(counter).toString()));
            counter++;
        }
        return out.toString();
    }

    /**
     * Implementation of Interface CodeGen.getDefaultTemplate
     *
     * @see CodeGen.getDefaultTemplate
     *
     */
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream(
                "/templates/page_submission.xhtml");
    }
}
