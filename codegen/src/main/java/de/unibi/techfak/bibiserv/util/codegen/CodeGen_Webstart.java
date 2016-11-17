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

import de.unibi.techfak.bibiserv.cms.Twebstart;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
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
public class CodeGen_Webstart 
    extends Abstract_CodeGen {

    
    private Twebstart webstart;
    
     /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public void generate() throws CodeGenParserException {
        try {
            if (runnableitem.isSetWebstart()) {
                
                List<Twebstart> webstarts = runnableitem.getWebstart();
                
                for(Twebstart webstart:webstarts) {
                    
                    this.webstart = webstart;
                    
                // create class name ...
                    File abs_class_file = new File(pagesdir + System.getProperty("file.separator") + webstart.getId()+".xhtml");
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
            
            } else {
                log.info("Skip [{}] ... no webstarts found!",getClass().getSimpleName());
            }
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
    
    public String getWebstartId(String arg) {
        return (webstart.getId());
    }


    /**
     * Implementation of Interface CodeGen.getDefaultTemplate
     *
     * @see CodeGen.getDefaultTemplate
     *
     */
    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream(
                "/templates/webstart.xhtml");
    }
}
