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
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoRepresentationImplementation;

import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;

import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CodeGen_Implementation extends the abstract CodeGen class. It generates
 * source files for the Java implementation
 *
 * @author Jan Krueger - jkrueger(at) techfak.uni-bielefeld.de
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class CodeGen_Implementation extends Abstract_CodeGen {
    
    

    protected Tfunction function;
    protected Map<String, List<Pair<String, OntoRepresentation>>> fct_input_rep =
            new HashMap();
    protected Map<String, Pair<String, OntoRepresentation>> fct_output_rep =
            new HashMap();
    protected String br = System.getProperty("line.separator");

    /**
     * Helper method - initialize all proteced values for input/output
     * representation. It is called by generate method.
     *
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    protected void initialize() throws CodeGenParserException {


        log.info(V,"Build function_input/output_represention");
        try {
            for (Tfunction tfunc : runnableitem.getExecutable().getFunction()) {
                List<Pair<String, OntoRepresentation>> inputlist =
                        new ArrayList<>();
                // iterate over all inputs
                for (Tfunction.Inputref inputref : tfunc.getInputref()) {
                    TinputOutput tin = (TinputOutput) inputref.getRef();
                    log.info(V,"IO " + tin.getType());
                    // get Ontology Representation object for current object
                    OntoRepresentation ontorepresentation =
                            new OntoRepresentationImplementation(tin.getType());
                    if (ontorepresentation.getImplementationType() == null) {
                        throw new CodeGenParserException("No ImplementationsType for type " + tin.
                                getType() + " available.");
                    } else {
                        log.info(V,"add input type " + tin.getType() + " !");
                    }
                    // add found represenation to tmp list
                    log.info(V,"add onto done");
                    // add new Pair of inputid and tmp list to input list
                    inputlist.add(new Pair(tin.getId(), ontorepresentation));
                    log.info(V,"add pair done");
                }
                fct_input_rep.put(tfunc.getId(), inputlist);

                // get representation for output
                TinputOutput tout = (TinputOutput) tfunc.getOutputref().getRef();
                OntoRepresentation ontorepresentation = new OntoRepresentationImplementation((tout.
                        getType()));
                if (ontorepresentation.getImplementationType() == null) {
                    throw new CodeGenParserException("No ImplementationsType for type " + tout.
                            getType() + " available.");
                } else {
                    log.info(V,"add output type " + tout.getType() + " !");
                }

                fct_output_rep.put(tfunc.getId(), new Pair(tout.getId(), ontorepresentation));
            }
        } catch (URISyntaxException | OntoAccessException e) {
            throw new CodeGenParserException(e);
        }


    }

    /**
     * Helper method - interate over all function It is called by generate
     * method.
     *
     * @throws Exception
     */
    protected void iterate_over_functions() throws Exception {



        List<Tfunction> list_of_functions = runnableitem.getExecutable().getFunction();
        log.info(V,"found " + list_of_functions.size()
                + " functions ... generate a class for each!");
        for (Tfunction tfunction : list_of_functions) {
            // store reference on current function
            function = tfunction;
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
        }
    }

    /**
     * Implementation of Interface CodeGen.generate
     *
     * @return 
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream("/templates/impl_template.java");
    }

    /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public void generate() throws CodeGenParserException {
        /**
         * initialize some variables
         */
        try {
            if (runnableitem.isSetExecutable()) {
                initialize();
                iterate_over_functions();
            } else {
                log.info(V,"Skip [" + getClass().getSimpleName() + "] ... no executable found ...");
              
            }
        } catch (Exception e) {
            throw new CodeGenParserException(e);
        }
    }

    /**
     * Return the full package name of the generated class(es).
     * cms:runnableitem/@id
     *
     * @param arg - not used
     * @return Return the full package name of the generated class(es).
     */
    public String getPackageName(String arg) {
        return ("de.unibi.techfak.bibiserv.tools." + runnableitem.getId());
    }

    /**
     * Return the classname of current generated class (==
     * cms:runnableitem/cms:function/@id)
     *
     * @param arg
     * @return Return the classname of current generated class.
     */
    public String getClassName(String arg) {
        return function.getId();
    }

    /**
     * Return the id of current function. (== cms:runnableitem/cms:function/@id)
     *
     * @param arg
     * @return Return the id of current function
     */
    public String getFunctionId(String arg) {
        return function.getId();
    }

    /**
     * Return the id of current tool. (== cms:runnableitem/@id)
     *
     * @param arg
     * @return Return the id of current tool.
     */
    public String getToolId(String arg) {
        return runnableitem.getId();
    }

    public String generateOntoRepresentationInputs(String arg) {
        StringBuilder out = new StringBuilder();

        int index = 0;
        for (Pair<String, OntoRepresentation> input : fct_input_rep.get(function.getId())) {

            
            
            // create the OntoRepresentation
            out.append("\tprivate static final OntoRepresentation representation_input");
            if(fct_input_rep.get(function.getId()).size()>1) {
                out.append("_").append(index);
            }
            out.append(" = buildInput");
            if(fct_input_rep.get(function.getId()).size()>1) {        
                out.append(index);
            }
            out.append("();");
            out.append(br).append(br);
            out.append("\tprivate static OntoRepresentation buildInput");
            if(fct_input_rep.get(function.getId()).size()>1) {        
                out.append(index);
            }
            out.append("() {").append(br);

            out.append("\t\t\ttry {").append(br);
            out.append("\t\t\treturn new OntoRepresentationImplementation(\"").append(input.getValue().
                    getKey()).append("\");").append(br);
            out.append("\t\t} catch (Exception e) {").append(br);
            // This only occures if the typekeys are wrong. THIS SHOULD NEVER HAPPEN IN A CORRECT TOOL!
            out.append("\t\t\tlog.fatal(e.getMessage(),e);").append(br);
            out.append("\t\t\treturn null;").append(br);
            out.append("\t\t}").append(br);
            out.append("\t}").append(br).append(br);

            // build a getter for controller
            out.append("\tpublic static OntoRepresentation getRepresentationInput");
            if(fct_input_rep.get(function.getId()).size()>1) {
                out.append(index);
            }
            out.append("() {").append(br);
            out.append("\t\t return representation_input");
            if(fct_input_rep.get(function.getId()).size()>1) {
                out.append("_").append(index);
            }
            out.append(";").append(br);
            out.append("\t}").append(br);

            index++;
        }
        return out.toString();
    }

    public String generateOntoRepresentationOutput(String arg) {
        StringBuilder out = new StringBuilder();

        OntoRepresentation output = fct_output_rep.get(function.getId()).getValue();

        out.append("\tprivate static final OntoRepresentation representation_output").append(
                " = buildOutput();");
        out.append(br).append(br);
        out.append("\tprivate static OntoRepresentation buildOutput() {").append(br);

        out.append("\t\ttry {").append(br);
        out.append("\t\t\treturn new OntoRepresentationImplementation(\"").append(output.getKey())
                .append("\");").append(br);
        out.append("\t\t} catch (Exception e) {").append(br);
        // This only occures if the typekeys are wrong. THIS SHOULD NEVER HAPPEN IN A CORRECT TOOL!
        out.append("\t\t\tlog.fatal(e.getMessage(),e);").append(br);
        out.append("\t\t\treturn null;").append(br);
        out.append("\t\t}").append(br);
        out.append("\t}").append(br).append(br);

        out.append("\tpublic static OntoRepresentation getRepresentationOutput").append("() {").append(br);
        out.append("\t\t return representation_output;").append(br);
        out.append("\t}").append(br);


        return out.toString();
    }

    /**
     * Generate and return the requestImpl method, which is private for each
     * function.
     *
     * @param arg
     * @return
     */
    public String generateRequestImpl(String arg) throws Exception {
        StringBuilder request_code = new StringBuilder();
        request_code.append("private String requestImpl");


        /*
         * add the parameter (JAXB) object ...
         */
        request_code.append("( List<Pair<String,String>> paramlist,  String accesskey,  String secretkey, String sessiontoken,  String uploadbucket,  String uploadfolder");
        /*
         * ... and all input(s)
         */
        for (int counter = 0; counter < fct_input_rep.get(function.getId()).size(); ++counter) { // JK
           // TG 12/12: specific types no longer possible, since it could be a connection!
           // Also this means the coode can cope with sudden type changes in the abstraction
            request_code.append(", ").append("Object").append(
                    " input_").append(counter); 
            request_code.append(", ").append("OntoRepresentation").append(
                    " representation_").append(counter);
            request_code.append(", ").append("boolean").append(
                    " streamsSupported_").append(counter);
            request_code.append(", ").append("boolean").append(
                    " skipValidation_").append(counter);
        }

        request_code.append(") throws BiBiToolsException {").append(br);
        request_code.append(arg).append(br).append(br).append("}").append(br);
        return request_code.toString();
    }
    
        public String generateRequestImplThreadCall(String arg) throws Exception {
        StringBuilder request_code = new StringBuilder();
        request_code.append("threadworker.startRequestThread");

        
        request_code.append("(bibitools, paramlist, accesskey, secretkey, sessiontoken, uploadbucket, uploadfolder, getOutputFile()");
        /*
         * ... and all input(s)
         */
        for (int counter = 0; counter < fct_input_rep.get(function.getId()).size(); ++counter) { // JK
           // TG 12/12: specific types no longer possible, since it could be a connection!
           // Also this means the tool can cope with sudden type changes in the abstraction
            request_code.append(", ").append("input_").append(counter); 
            request_code.append(", ").append("representation_").append(counter);
            request_code.append(", ").append("representation_input");
            if(fct_input_rep.get(function.getId()).size()>1) {
                request_code.append("_").append(counter);
            }
            
            request_code.append(", ").append("streamsSupported_").append(counter);
            request_code.append(", ").append("skipValidation_").append(counter);
        }

        request_code.append(");").append(br);
        return request_code.toString();
    }



    public String generateRequestRepresentation(String arg) throws Exception {

        StringBuilder all = new StringBuilder();

        StringBuilder validationConversion = new StringBuilder();

        StringBuilder inputParam = new StringBuilder();
        StringBuilder inputParamNoValid = new StringBuilder();
        StringBuilder inputParamCall = new StringBuilder();
        StringBuilder inputImplParam = new StringBuilder();
        StringBuilder inputParamCallNoValid = new StringBuilder();


        // loop through all inputs
        int index = 0;
        for (Pair<String, OntoRepresentation> input : fct_input_rep.get(function.getId())) {
            // add to calling parameters of this function
            inputParam.append(", Object input_").append(index).append(
                    ", OntoRepresentation representation_").append(index)
                    .append(", boolean streamsSupported_").append(index)
                    .append(", boolean skipValidation_").append(index);
            inputParamNoValid.append(", Object input_").append(index).append(
                    ", OntoRepresentation representation_").append(index);
            inputParamCall.append(", input_").append(index)
                    .append(", representation_").append(index)
                    .append(", streamsSupported_").append(index)
                    .append(", skipValidation_").append(index);
            inputParamCallNoValid.append(", input_").append(index)
                    .append(", representation_").append(index)
                    .append(", false")
                    .append(", true");
            
            validationConversion.append("\t\tObject converted_input_").append(index).append(" = input_").append(index).append(";").append(br);
            
            // add to validation
            validationConversion.append("\t\tif (!(input_").append(index).append(" instanceof ValidationConnection) && !skipValidation_").append(index).append(") {").append(br);
            validationConversion.append("\t\t\tinput_").append(index).append(" = Utilities.validate(input_").append(index)
                    .append(", representation_").append(index).append(");").append(br);

            // add conversion, no need to check if source and target is the same is this is already
            // done in conversion method
            validationConversion.append("\t\t\tconverted_input_").append(index).append(
                    " = Utilities.convert(input_").append(index).append(", representation_").append(
                    index).append(", representation_input");
            if(fct_input_rep.get(function.getId()).size()>1) {
                validationConversion.append("_").append(index);
            }
            validationConversion.append(");").append(br);

            // add to requestImpl call
            inputImplParam.append(", ").append("converted_input_").append(index);
            inputImplParam.append(", ").append("representation_").append(index);
            inputImplParam.append(", ").append("streamsSupported_").append(index);
            inputImplParam.append(", ").append("skipValidation_").append(index);
            
            validationConversion.append("\t\t}").append(br);
            
            index++;
        }

        // put everything together
        
        // call without upload option
        all.append("\tpublic String request(List<Pair<String,String>> param_hash ").append(inputParamNoValid).
                append(
                ") throws BiBiToolsException, ConversionException, ValidationException{").append(
                br);
        // call  Implementing class
        all.append("\t\treturn request(param_hash, \"\", \"\", null, null ").append(inputParamCallNoValid).append(");").append(br);
        all.append("\t}").append(br).append(br);
        
        all.append("\tpublic String request(List<Pair<String,String>> param_hash, String accesskey, String secretkey").append(inputParam).
                append(
                ") throws BiBiToolsException, ConversionException, ValidationException{").append(
                br);
        // call  Implementing class
        all.append("\t\treturn request(param_hash, accesskey, secretkey, \"\", null, null ").append(inputParamCall).append(");").append(br);
        all.append("\t}").append(br).append(br);
        
        
        all.append("\tpublic String request(List<Pair<String,String>> param_hash, String accesskey, String secretkey, String sessiontoken").append(inputParam).
                append(
                ") throws BiBiToolsException, ConversionException, ValidationException{").append(
                br);
        // call  Implementing class
        all.append("\t\treturn request(param_hash, accesskey, secretkey, sessiontoken, null, null ").append(inputParamCall).append(");").append(br);
        all.append("\t}").append(br).append(br);

        
       all.append("\tpublic String request(List<Pair<String,String>> param_hash, String accesskey, String secretkey, String uploadbucket, String uploadfolder").append(inputParam).
                append(
                ") throws BiBiToolsException, ConversionException, ValidationException{").append(
                br);
        // call  Implementing class
        all.append("\t\treturn request(param_hash, accesskey, secretkey, \"\", uploadbucket, uploadfolder ").append(inputParamCall).append(");").append(br);
        all.append("\t}").append(br).append(br);
        
        // call with upload option
        all.append("\tpublic String request(List<Pair<String,String>> param_hash, String accesskey, String secretkey, String sessiontoken, String uploadbucket, String uploadfolder").append(inputParam).
        append(
        ") throws BiBiToolsException, ConversionException, ValidationException{").append(
        br);

        // add validation of input
        all.append(validationConversion);

        // call  Implementing class
        all.append("\t\treturn requestImpl(param_hash, accesskey, secretkey, sessiontoken, uploadbucket, uploadfolder ").append(inputImplParam).append(");").append(br);
        all.append("\t}").append(br);
        
        return all.toString();
    }

    
    /**
     * Small helper method - Returns a namespace for the given
     * ontorepresentation. Depending on the type the value is generated
     * (Primitive type) or used directly.
     *
     * @param tmp - Ontorepresentationobject
     * @return Returns a simple NS view of this ontorepresentationobject
     */
    protected String createNS_for_Type(OntoRepresentation tmp) throws CodeGenParserException {

        if (tmp.getType().equals(OntoRepresentation.representationType.XML)) {
            return tmp.getNameSpace().toString();
        } else if (tmp.getType().equals(OntoRepresentation.representationType.PRIMITIVE)) {
            return "bibiserv:" + tmp.getKey();
        }
        throw new CodeGenParserException("Unknown (or unsupported) type '" + tmp.getType());


    }

    /**
     * Returns the argument if current function has at least one input.
     *
     * @param arg
     * @return
     * @throws CodeGenParserException
     */
    public String ifInput(String arg) throws CodeGenParserException {
        if (fct_input_rep.get(function.getId()).size() > 0) {
            return arg;


        }
        return "";


    }

    /**
     * Return the argument if current function has at least not input
     *
     * @param arg
     * @return
     * @throws CodeGenParserException
     */
    public String ifNotInput(String arg) throws CodeGenParserException {
        if (fct_input_rep.get(function.getId()).isEmpty()) {
            return arg;
        }
        return "";

    }
}
