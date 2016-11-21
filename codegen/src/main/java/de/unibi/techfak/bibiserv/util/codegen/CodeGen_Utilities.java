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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team,"
 * 
 * Contributor(s): Jan Krueger
 * 
 */
package de.unibi.techfak.bibiserv.util.codegen;

import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.TenumValue;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.Tprimitive;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * CodeGen_Utilities extends the abstract CodeGen_Implementation class. This
 * class generates Utility functions used by all enduser interface (webservice,
 * webinterface and ...)
 *
 * - add generation of iotype validation
 *
 * @author Jan Krueger - jkrueger(at) techfak.uni-bielefeld.de
 */
public class CodeGen_Utilities extends CodeGen_Implementation {

    public CodeGen_Utilities()  {
        super();
    }

    /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream("/templates/util_template.java");
    }

    /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public void generate() throws CodeGenParserException {
        try {
            if (runnableitem.isSetExecutable()) {
                // create package dir if it not exists ..
                File abs_package_dir = new File(srcdir, getPackageName(null).replace('.', '/'));
                if (!abs_package_dir.isDirectory()) {
                    abs_package_dir.mkdirs();
                }
                // create class name ...
                File abs_class_file = new File(abs_package_dir, "Utilities.java");
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
            throw new CodeGenParserException(e);
        }
    }

    /**
     * Generate a validator function for each iotype.
     *
     * @param args
     * @return
     */
    public String generateValidators(String args) throws Exception {
        // result output buffer
        StringBuilder out = new StringBuilder();

        out.append("\tpublic static boolean validate(Object content, OntoRepresentation representation) throws ValidationException{" + br);

        
        return out.toString();
    }


    public String generateParamValidators(String args) throws Exception {
        StringBuilder out = new StringBuilder();
        // handling all parameter
        for (Tparam tp : runnableitem.getExecutable().getParam()) {



            // distinguish between different types
            if (tp.getType() == Tprimitive.INT || tp.getType() == Tprimitive.FLOAT) {

                // first method without i18n
                out.append("public static boolean validate_" + tp.getId() + "(" + tp.getType().toString().toLowerCase() + " content) throws ValidationException{").append(br);
                out.append("return validate_" + tp.getId() + "(content,null);").append(br);
                out.append("}").append(br);
                // second method with i18n
                out.append("public static boolean validate_" + tp.getId() + "(" + tp.getType().toString().toLowerCase() + " content,MessagesInterface msg) throws ValidationException{").append(br);
                // getMin
                if (tp.isSetMin()) {
                    // is Included
                    if (tp.getMin().isSetIncluded() && tp.getMin().isIncluded()) {
                        out.append("if (content < " + tp.getMin().getValue() + ") { ").append(br);

                        out.append("throw new ValidationException((msg == null?\"Value must be greater or equal than '" + tp.getMin().getValue() + "' !\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.num.GREATEREQUAL\",\"" + tp.getMin().getValue() + "\")));").append(br);
                    } else {
                        out.append("if (content <= " + tp.getMin().getValue() + ") { ").append(br);
                        out.append("throw new ValidationException((msg == null?\"Value must be greater than '" + tp.getMin().getValue() + "' !\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.num.GREATER\",\"" + tp.getMin().getValue() + "\")));").append(br);
                    }
                    out.append("}").append(br);
                }
                // getMax
                if (tp.isSetMax()) {
                    // isIncluded
                    if (tp.getMax().isSetIncluded() && tp.getMax().isIncluded()) {
                        out.append("if (content > " + tp.getMax().getValue() + ") { ").append(br);
                        out.append("throw new ValidationException((msg == null?\"Value must be lesser or equal than '" + tp.getMax().getValue() + "' !\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.num.LESSEREQUAL\",\"" + tp.getMax().getValue() + "\")));").append(br);

                    } else {
                        out.append("if (content >= " + tp.getMax().getValue() + ") { ").append(br);
                        out.append("throw new ValidationException((msg == null?\"Value must be lesser than '" + tp.getMax().getValue() + "' !\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.num.LESSER\",\"" + tp.getMax().getValue() + "\")));").append(br);
                    }

                    out.append("}").append(br);
                }
            } else if (tp.getType() == Tprimitive.BOOLEAN) {
                // first method without i18n
                out.append("public static boolean validate_" + tp.getId() + "(boolean content) throws ValidationException{").append(br);
                out.append("return  validate_" + tp.getId() + "(content,null);").append(br);
                out.append("}").append(br);
                // second method with i18n
                out.append("public static boolean validate_" + tp.getId() + "(boolean content,MessagesInterface msg) throws ValidationException{").append(br);
                // nothing to do, because booleab can't have any constraints
            } else if (tp.getType() == Tprimitive.STRING) {
                // first method without i18n
                out.append("public static boolean validate_" + tp.getId() + "(String content) throws ValidationException{").append(br);
                out.append("return validate_" + tp.getId() + "(content,null);").append(br);
                out.append("}").append(br);
                // second method with i18n
                out.append("public static boolean validate_" + tp.getId() + "(String content,MessagesInterface msg) throws ValidationException{").append(br);
                if (tp.isSetMaxLength()) {
                    out.append("if (content.length()  > " + tp.getMaxLength() + ") {").append(br);
                    out.append("throw new ValidationException((msg == null?\"Value length must be lesser or equal than '" + tp.getMaxLength() + "' !\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.string.LESSER\",\"" + tp.getMaxLength() + "\")));").append(br);
                    out.append("}").append(br);
                }
                if (tp.isSetMinLength()) {
                    out.append("if (content.length() < " + tp.getMinLength() + ") {").append(br);
                    out.append("throw new ValidationException((msg == null?\"Value length must be greater or equal than '" + tp.getMinLength() + "' !\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.string.GREATER\",\"" + tp.getMinLength() + "\")));").append(br);
                    out.append("}").append(br);
                }
                if (tp.isSetRegexp()) {
                    out.append("if (!content.matches(\"" + tp.getRegexp().replaceAll("\\\\", "\\\\\\\\") + "\")) {").append(br);
                    out.append("throw new ValidationException((msg == null?\"Value doesn't match regexp '" + tp.getRegexp().replaceAll("\\\\", "\\\\\\\\") + "' !\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.string.REGEXP\",\"" + tp.getRegexp().replaceAll("\\\\", "\\\\\\\\") + "\")));").append(br);
                    out.append("}").append(br);
                }
            } else {
                throw new CodeGenParserException("Unsupported Type " + tp.getType().toString());
            }
            out.append("return true;");
            out.append("}").append(br);
        }
        return out.toString();
    }

    public String generateEnumParamValidators(String args) throws Exception {
        StringBuilder out = new StringBuilder();
        // handling all enum parameter
        for (TenumParam tp : runnableitem.getExecutable().getEnumParam()) {
            // distinguish between selectOne and selectMany

            if (tp.getGuiElement().toLowerCase().startsWith("selectone")) {
                StringBuilder tp_values = new StringBuilder();
                out.append("public static boolean validate_").append(tp.getId()).append("(String content) throws ValidationException{").append(br);
                out.append("return validate_").append(tp.getId()).append("(content,null);").append(br);
                out.append("}").append(br);
                out.append("public static boolean validate_").append(tp.getId()).append("(String content,MessagesInterface msg) throws ValidationException{").append(br);
                for (TenumValue tev : tp.getValues()) {
                    tp_values.append(tp_values.length() == 0 ? "'" + tev.getValue() + "'" : " or '" + tev.getValue() + "'");
                    out.append("if (content != null && content.equals(\"").append(tev.getKey()).append("\")){").append("return true;}").append(br);
                }
                out.append("throw new ValidationException((msg == null ? \"Value must match one of ").append(tp_values).append("\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.enum.SELECTONE\",\"").append(tp_values).append("\")));").append(br);
                out.append("}").append(br);
            } else if (tp.getGuiElement().toLowerCase().startsWith("selectmany")) {
                out.append("public static boolean validate_").append(tp.getId()).append("(String [] contentarray) throws ValidationException{").append(br);
                out.append("return validate_").append(tp.getId()).append("(contentarray,null);").append(br);
                out.append("}").append(br);
                out.append("public static boolean validate_").append(tp.getId()).append("(String [] contentarray,MessagesInterface msg) throws ValidationException{").append(br);
                
                
                int minOcc = 0;
                if(tp.isSetMinoccurs()) {
                    minOcc = tp.getMinoccurs();
                }
                int maxOcc = tp.getValues().size(); 
                if(tp.isSetMaxoccurs()) {
                    maxOcc = tp.getMaxoccurs();
                }
                
                out.append("if(contentarray.length < ").append(minOcc).append(" || contentarray.length > ").append(maxOcc).append(" ) {").append(br);
        
                out.append("\tthrow new ValidationException(\"").append(minOcc).append("-").append(maxOcc).append(" \"+msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.enum.SELECTNUMBER\"));").append(br);
                out.append("}").append(br);

                
                out.append("for (String content : contentarray) {").append(br);
                out.append("if (content == null || !content.matches(\"(");
                StringBuilder tp_values = new StringBuilder();             
                Iterator<TenumValue> it = tp.getValues().iterator();
                while (it.hasNext()) {
                    TenumValue tev = it.next();
                    out.append(tev.getKey());
                    if (it.hasNext()) {
                        out.append("|");
                    } 
                }
                out.append(")\")){").append(br);
                out.append("throw new ValidationException((msg == null ? \"All values must match one of ").append(tp_values).append("\":msg.property(\"de.unibi.techfak.bibiserv.bibimainapp.input.param.enum.SELECTMANY\",\"").append(tp_values).append("\")));").append(br);
                out.append("}}").append(br);
                out.append("return true;");
                out.append("}").append(br);
            } else {
                throw new CodeGenParserException("Unknown GuiElement value for EnumParam '" + tp.getGuiElement() + "'");
            }

        }

        return out.toString();
    }
}
