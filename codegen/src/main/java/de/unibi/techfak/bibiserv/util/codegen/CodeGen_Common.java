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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author Jan Krueger - jkrueger[at]cebitec.uni-bielefeld.de
 */
public class CodeGen_Common extends Abstract_CodeGen {

    @Override
    public void generate() throws CodeGenParserException {
        if (template != null) {
            try {
                start_codegen_process_and_close_streams(template, new FileOutputStream(generate_file(new File(resultfilename))));
            } catch (IOException e) {
                throw new CodeGenParserException("An IOException occurred while run '" + CodeGen.class.getSimpleName() + "' generating " + resultfilename, e);
            }
        } else {
            throw new CodeGenParserException("Template must not be null since '"+ CodeGen.class.getSimpleName() + "' doesn't support a default template");
        }
         
    }

    @Override
    public InputStream getDefaultTemplate() {
        throw new UnsupportedOperationException("if you use " + CodeGen_Common.class.getSimpleName() + " you MUST set a template file (or dir)!");
    }

    /**
     * Return the id of current tool.
     *
     * @param arg - not used, should be a null value
     * @return
     */
    public String getId(String arg) {
        return runnableitem.getId();
    }

    public String getPackage(String arg) {
        return "de.unibi.techfak.bibiserv.tools." + getId(null);
    }

    private File generate_file(File file) {
        // create package dir if it not exists ..
        File abs_package_dir = new File(srcdir, getPackage(null).replace('.', '/'));
        if (!abs_package_dir.isDirectory()) {
            abs_package_dir.mkdirs();
        }
        // create class name ...
        return new File(abs_package_dir, file.getName());
    }

    private void start_codegen_process_and_close_streams(InputStream in, OutputStream out) throws CodeGenParserException, IOException {
        // create new CodeGenParser
        CodeGenParser codegenparser = new CodeGenParser(this, in, out);
        // start CodeGenParser
        codegenparser.run();
        // close Streams
        in.close();
        out.close();
    }
}
