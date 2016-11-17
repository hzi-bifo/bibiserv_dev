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



/**
 * CodeGenMain implements a "simple" Commandline interface for the CodeGenerator.
 *
 * @ToDO: - better help using the joptsimple possibilties
 *        - comments !
 *
 * @author jkrueger
 */
public class CodeGenMain {

    public static void main(String[] args)  {
//        OptionParser parser = new OptionParser();
//        parser.accepts("dest").withOptionalArg();
//        parser.accepts("template").withOptionalArg();
//        parser.accepts("class").withOptionalArg();
//        parser.accepts("runnableitem").withOptionalArg();
//
//        OptionSet options = parser.parse(args);
//
//        if (options.has("dest") && options.has("class") && options.has("runnableitem")) {
//            try {
//                Class codegen_class = Class.forName(((String) options.valueOf("class")).trim());
//                CodeGen codegen = (CodeGen) codegen_class.newInstance();
//                codegen.setResultDir(new File((String) options.valueOf("dest")));
//                codegen.setRunnableFile(new File((String) options.valueOf("runnableitem")));
//                if (options.has("template")) {
//                    codegen.setTemplateFile(new File((String) options.valueOf("template")));
//                }
//                codegen.generate();
//            } catch (ClassNotFoundException e) {
//                System.err.println("ClassNotFoundException e while loading class '"+((String) options.valueOf("class")).trim()+"'!");
//                e.printStackTrace();
//                System.err.println("ClassPath : "+System.getProperty("java.class.path"));
//                System.err.println("LibraryPath : "+System.getProperty("java.library.path"));
//                System.exit(1);
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.exit(1);
//            }
//
//        } else {
//            System.err.println("usage: \n" +
//                    "java -jar CodeGen.jar --dest <dir> --class <class> --runnableitem <file> [--template <file>]\n\n");
//            System.exit(1);
//        }


    }
}
