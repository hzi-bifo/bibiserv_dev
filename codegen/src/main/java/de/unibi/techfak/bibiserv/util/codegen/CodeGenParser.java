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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The Class CodeGenParser parses a template file and generate source code replacing
 * special tags with functionality. A template can look like :
 * 
 * <pre>
 * package <#getPackageName/#>;
 *
 * import <#getPackageName/#>.ws.param;
 * 
 * import de.unibi.techfak.bibiserv.BiBiTools;
 * import de.unibi.techfak.bibiserv.Status;
 * 
 * 
 * public class <#getClassName/#> {
 * 
 * private BiBiTools bibitools;
 * 
 * private Status status;
 * 
 * <#generateRequest#>
 *         // create new BiBiTools object
 *         bibitools = new BiBiTools();
 *         // get status object from bibitools;
 *         status = bibitools.getStatus();
 *         // parse and check parameter
 *         HashMap<String,String> paramhash = bibitools.checkandparseParameter(Param paramdoc);
 *         // check and parse input
 *         HashMap<String,String> inputhash = new HashMap<String,String>();
 *         <#for_each_input#>inputhash<#/for_each_input#>
 *         // build cmdline string 
 *         String cmdline = createCMDline(paramhash,inputhash);
 *         // create new ProcessingThread
 *         Thread processing = new <#getClassName/#>Processing(cmdline);
 *         processing.start();
 *         // return bibiserv id and finish
 *         return bibitools.getId();
 * <#/generateRequest#>
 * 	
 * ...
 * 
 * </pre>
 * 
 * A simple tag <#simpleTag/#> is a function call without parameter. 
 * A complex tag <#compleyxTag#>...<#/complexTag> is function call with the content 
 * between opening and closing tag as parameter. Complex tags can be nested.
 * 
 * 
 * @author Jan Krueger <jkrueger(at)techfak.uni-bielefeld.de>
 */
public class CodeGenParser {

    private CodeGen codegen = null;
    private BufferedReader reader;
    private BufferedWriter writer;
    /*line number */
    private int linenumber;
    /* char position in current line */
    private int charpos;

    
    /**
     * This Constructor is mainly for test purpose. It generates a CodeGenParser
     * object without any CodeGen class.
     * 
     * @param in - InputStream of the text (template)
     * @param out - OutputStream, contaiing the generated text (source code)
     */
    public CodeGenParser(InputStream in, OutputStream out) {
        this(null, in, out);
    }

    /**
     * Constructor
     * 
     * 
     * @param codegen  - A CodeGen Class that contains all necessary functions.
     * @param in - Inputstream of the text (template)
     * @param out - OutputStream, containg the generated text (source code)
     */
    public CodeGenParser(CodeGen codegen, InputStream in, OutputStream out) {
        this.codegen = codegen;
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.writer = new BufferedWriter(new OutputStreamWriter(out));
        linenumber = 1;
        charpos = 1;
    }

    
    /**
     * Function run parses a text (template) and replaces simple <#simpletag/#> or
     * complex <#complextag#> ... <#/complextag> tag with text (code) which is generated
     * by functions within the given CodeGen instance. A <b>tagname</b> indicates a
     * function in the CodeGen instance.
     * 
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    public void run() throws CodeGenParserException {
        try {
            writer.append(internal_run(null));
            writer.flush();
        } catch (IOException e) {
            throw new CodeGenParserException("An IOExeption occurred!",e);
        }
    }

    /**
     * Function internal_run parses a textfragmet surrounded by <#tagname#> and
     * <#/tagname>. Any nested tag is parsed recursivley.
     * 
     * 
     * @param name - name of tag which sourrounds the text - can be null in the case no surrounding tag 
     * @return A parsed and generated textfragment.
     * 
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException in case of any parsing or generation errors.
     */
    private String internal_run(String name) throws CodeGenParserException {

        try {

            int c = 0;
            int mode = 0;

            /* fctname == tag name */
            StringBuffer fctname = new StringBuffer();

            /* content */
            StringBuilder content = new StringBuilder();

            while ((c = reader.read()) != -1) {

                if (c == '\n') {
                    linenumber++;
                    charpos = 1;
                } else {
                    charpos++;
                }

                switch (mode) {
                    case 0:
                        if (c == '<') {
                            mode = 1;
                        } else {
                            content.append((char) c);
                        }
                        break;
                    case 1:
                        if (c == '#') {
                            mode = 2;
                        } else {
                            mode = 0;
                            content.append("<");
                            content.append((char) c);
                        }
                        break;
                    case 2:
                        switch (c) {
                            case '/': {
                                if (fctname.length() == 0) {
                                    mode = 14;
                                } else {
                                    mode = 3;
                                }
                                break;
                            }
                            case '#':
                                mode = 10;
                                break;
                            default:
                                fctname.append((char) c);
                        }
                        break;
                    case 3: {
                        if (c == '#') {
                            mode = 4;
                        } else {
                            mode = 0;
                            content.append("<#");
                            content.append(fctname);
                            content.append('/');
                            content.append((char) c);

                        }
                        break;
                    }
                    case 4: {
                        if (c == '>') {
                            content.append(callFunction(fctname.toString()));
                            fctname = new StringBuffer();
                        } else {
                            content.append("<#");
                            content.append(fctname);
                            content.append("/#");
                            content.append((char) c);
                        }
                        mode = 0;
                        break;
                    }
                    case 10: {
                        if (c == '>') {
                            content.append(internal_run(fctname.toString()));
                            fctname = new StringBuffer();
                            mode = 0;
                        } else {
                            mode = 0;
                            content.append("<#");
                            content.append(fctname);
                            content.append("#");
                            content.append((char) c);
                        }
                        break;
                    }

                    case 14: {
                        if (c == '#') {
                            mode = 15;
                        } else {
                            fctname.append((char) c);
                        }
                        break;
                    }
                    case 15: {
                        if (c == '>') {
                            if (fctname.toString().equals(name)) {
                                return callFunction(fctname.toString(), content.toString()).toString();
                            } else {
                                throw new CodeGenParserException("Opening tag <#" + name + "#> mismatches closing tag <#/" + fctname + "#> at pos " + charpos + " in line " + linenumber + "!");
                            }

                        } else {
                            content.append("<#/" + fctname.toString() + "#");
                            content.append((char) c);
                            mode = 0;
                        }
                        break;
                    }
                }
            }
            if (name != null && !name.isEmpty()) {
                throw new CodeGenParserException("Missing closing tag <#"+name+"#>!" );
            }

            return content.toString();

        } catch (IOException e) {
            throw new CodeGenParserException("An IOException occurred!",e);
        }
    }

    /**
     * private helper function, call callFunction with NULL argument.
     * 
     * @param name : Name of the function to be called
     * @return Returns the result of the called functions, which is normally a piece of generated source code.
     * 
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    private CharSequence callFunction(String name) throws CodeGenParserException {
        return callFunction(name, null);
    }

    /**
     * private helper function, which do a function call with content as argument.
     * 
     * @param name : Name of the function to be called
     * @return Returns the result of the called functions, which is normally a piece of generated source code.
     * 
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    private CharSequence callFunction(String name, String content) throws CodeGenParserException {
        if (codegen == null) {
            return "No CodeGen Object given ... ignore function call '" + name + "'(" + content + ")";

        }
        /* get all public method supported by the codegen class */
        Method[] method_array = codegen.getClass().getMethods();
        /* search if method matches the 'name' string */
        Method method = null;
        for (Method m : method_array) {
            if (m.getName().equals(name)) {
                method = m;
                break; // leave loop

            }
        }
        if (method == null) {
            throw new CodeGenParserException("No method with name '" + name + "' found in CodeGen Object!");
        }

        /* some checks are still missing
         * - the return type of the method must be a String
        */

        // return type must be a String
        if (!method.getReturnType().getSimpleName().equals("String")) {
            throw new CodeGenParserException("Return Type of called method '"+name+"' MUST be of type 'String'!");
        }

        try {
          
            return (String) method.invoke(codegen, content);
        } catch (IllegalAccessException e) {
            throw new CodeGenParserException("IllegalAccessException while invoking method '" + method.getName() + "'.",e);
        } catch (IllegalArgumentException e) {
            throw new CodeGenParserException("IllegalArgumentException while invoking method '" + method.getName() + "(\"" + content + "\")'.",e);
        } catch (InvocationTargetException e) {
            throw new CodeGenParserException("InvocationTargetException while invoking method '" + method.getName() + "'.",e);
        }
    }
}
