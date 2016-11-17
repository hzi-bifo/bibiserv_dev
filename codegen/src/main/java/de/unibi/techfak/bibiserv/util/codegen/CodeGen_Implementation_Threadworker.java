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
import de.unibi.techfak.bibiserv.cms.ToutputFile;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;

import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * CodeGen_Implementation extends the abstract CodeGen class. It generates
 * source files for the Java implementation
 *
 * @author Jan Krueger - jkrueger(at) techfak.uni-bielefeld.de
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class CodeGen_Implementation_Threadworker extends CodeGen_Implementation {

    /**
     * Implementation of Interface CodeGen.generate
     *
     * @see CodeGen.generate
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation_Threadworker.class.getResourceAsStream("/templates/impl_threadworker_template.java");
    }

    
    /**
     * Helper method - interate over all function It is called by generate
     * method.
     *
     * @throws Exception
     */
    @Override
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
                log.info(V,"Skip [" + getClass().getSimpleName()
                        + "] ... no executable found ...");
            }
        } catch (Exception e) {
            throw new CodeGenParserException(e);
        }
    }


    /**
     * Return the classname of current generated class (==
     * cms:runnableitem/cms:function/@id)
     *
     * @param arg
     * @return Return the classname of current generated class.
     */
    @Override
    public String getClassName(String arg) {
        return function.getId()+"_threadworker";
    }



    /**
     * Generate and return the requestImpl method, which is private for each
     * function.
     *
     * @param arg
     * @return
     */
    public String generateRequestImplThread(String arg) throws Exception {
        StringBuilder request_code = new StringBuilder();
        request_code.append("public void startRequestThread");

        
        request_code.append("(final BiBiTools bibitools, final List<Pair<String,String>> paramlist, final String accesskey, final String secretkey, final String sessiontoken, final String uploadbucket, final String uploadfolder, final File out");
        /*
         * ... and all input(s)
         */
        for (int counter = 0; counter < fct_input_rep.get(function.getId()).size(); ++counter) { // JK
           // TG 12/12: specific types no longer possible, since it could be a connection!
           // Also this means the tool can cope with sudden type changes in the abstraction
            request_code.append(",final ").append("Object").append(
                    " input_").append(counter); 
            request_code.append(",final ").append("OntoRepresentation").append(
                    " representation_").append(counter);
             request_code.append(",final ").append("OntoRepresentation").append(
                    " representation_input_").append(counter);
            request_code.append(",final ").append("boolean").append(
                    " streamsSupported_").append(counter);
            request_code.append(",final ").append("boolean").append(
                    " skipValidation_").append(counter);
        }

        request_code.append(") throws BiBiToolsException {").append(br);
        request_code.append(arg).append(br).append(br).append("}").append(br);
        return request_code.toString();
    }


    /**
     * Iterate over each input of current function. The argument must be specify
     * a string and hashmap separated by a ',', where the string contains then a
     * concatination of each parseandvalidateInput call (see
     * BiBiTools.parseandvalidateInput for a more detailed explanation) and the
     * hashmap all parameters and input values (key == id, value == 'cmd-line')
     *
     * @param arg - must be specify a string and hashmap separated by a ','
     *
     * @return
     */
    public String for_each_input(String arg) throws Exception {
        StringBuilder code = new StringBuilder();
        String args[] = arg.split(",");

        for (int counter = 0; counter < fct_input_rep.get(function.getId()).size(); ++counter) {
            OntoRepresentation ontorepresentation = fct_input_rep.get(function.getId()).get(counter).
                    getValue();
            
            String type = ontorepresentation.getType().name();
            String impltype = ontorepresentation.getImplementationType();
            
            
            // case URL Transfer
            code.append("if (input_").append(counter).append(" instanceof AWSUrlTransferConnection) {").append(br);
            
            code.append("\tAWSUrlTransferConnection connection = (AWSUrlTransferConnection) input_").append(counter).append(";").append(br).append(br);
            
            code.append("\t // TODO: TG: edit behaviour to needed parameters").append(br);
            code.append("\tStringBuilder transferString = new StringBuilder();").append(br);
            code.append("\ttransferString.append(\" \").append(connection.getBucket());").append(br);
            code.append("\ttransferString.append(\" \").append(connection.getFile());").append(br);
            code.append("\ttransferString.append(\" \").append(accesskey);").append(br);
            code.append("\ttransferString.append(\" \").append(secretkey);").append(br);
            
            code.append("\tif(!sessiontoken.isEmpty()) {").append(br);
            code.append("\t\ttransferString.append(\" \").append(sessiontoken);").append(br);
            code.append("\t}").append(br);
            
            code.append("\tinputhash.put(\"")
                .append(fct_input_rep.get(function.getId()).get(counter).getKey()).append("\", transferString.toString());").append(br);
            
            // case AWS
            code.append("} else if (input_").append(counter).append(" instanceof AWSValidationConnection) {").append(br);
            
            code.append("\tString validatorImpl_").append(counter).append(";").append(br);
            code.append("\tList<String> converterChain_").append(counter).append(";").append(br);
            code.append("\tif(skipValidation_").append(counter).append(") {").append(br);
            code.append("\t\tvalidatorImpl_").append(counter).append(" = null;").append(br);;
            code.append("\t\tconverterChain_").append(counter).append(" = null;").append(br);;
            code.append("\t} else {").append(br);
            code.append("\t\tvalidatorImpl_").append(counter).append(" = UniversalValidator.getValidatorImlementation(representation_").append(counter).append(");").append(br);
            code.append("\t\tconverterChain_").append(counter).append(" = UniversalConverter.getStreamConverterOrder(representation_").append(counter).append(", representation_input").append("_").append(counter);

            code.append(");").append(br);
            code.append("\t}").append(br).append(br);
            
            code.append("\tAWSValidationConnection connection = (AWSValidationConnection) input_").append(counter).append(";").append(br).append(br);
            
            code.append("\t").append(args[0]).append(" += bibitools.parseInputAWS(\"");
            code.append(fct_input_rep.get(function.getId()).get(counter).getKey()).append("\","); // id
            code.append(args[1]).append(", "); // inputhash
            code.append("connection.getBucket(), "); // bucket
            code.append("connection.getFile(), "); // file
            code.append("accesskey, secretkey, sessiontoken,"); // aws credentials
            code.append("generatedInfo, "); // information of the command line generation
            code.append("validatorImpl_").append(counter).append(", "); // validator implementation
            code.append("converterChain_").append(counter).append(", "); // chain of converter implementations
            code.append("(representation_").append(counter).append(".getContent()!=null) ? representation_").append(counter).append(".getContent().name() : \"NULL\" , "); // Content as String
            code.append("(representation_").append(counter).append(".getStrictness()!=null) ? representation_").append(counter).append(".getStrictness().name() : \"NULL\" , "); // Stricness as String
            code.append("(representation_").append(counter).append(".getCardinality()!=null) ? representation_").append(counter).append(".getCardinality().name() : \"NULL\" , "); // Cardinality as String
            code.append("representation_").append(counter).append(".getStructure().equals(datastructure.ALIGNMENT) || representation_").append(counter).append(".getStructure().equals(datastructure.STRUCTUREALIGNMENT), ");
            code.append("streamsSupported_").append(counter).append(");").append(br); // stream support
            
            // case AWS URL
            code.append("} else if (input_").append(counter).append(" instanceof URLValidationConnection) {").append(br);
            
             code.append("\tString validatorImpl_").append(counter).append(";").append(br);
            code.append("\tList<String> converterChain_").append(counter).append(";").append(br);
            code.append("\tif(skipValidation_").append(counter).append(") {").append(br);
            code.append("\t\tvalidatorImpl_").append(counter).append(" = null;").append(br);
            code.append("\t\tconverterChain_").append(counter).append(" = null;").append(br);
            code.append("\t} else {").append(br);
            code.append("\t\tvalidatorImpl_").append(counter).append(" = UniversalValidator.getValidatorImlementation(representation_").append(counter).append(");").append(br);
            code.append("\t\tconverterChain_").append(counter).append(" = UniversalConverter.getStreamConverterOrder(representation_").append(counter).append(", representation_input").append("_").append(counter);
            
            code.append(");").append(br);
            code.append("\t}").append(br).append(br);
            
            code.append("\tURLValidationConnection connection = (URLValidationConnection) input_").append(counter).append(";").append(br);
            
            code.append("\t").append(args[0]).append(" += bibitools.parseInputURL(\"");
            code.append(fct_input_rep.get(function. getId()).get(counter).getKey()).append("\","); // id
            code.append(args[1]).append(", "); // inputhash
            code.append("connection.getUrl(), "); // url
            code.append("generatedInfo, "); // information of the command line generation
            code.append("validatorImpl_").append(counter).append(", "); // validator implementation
            code.append("converterChain_").append(counter).append(", "); // chain of converter implementations
            code.append("(representation_").append(counter).append(".getContent()!=null) ? representation_").append(counter).append(".getContent().name() : \"NULL\" , "); // Content as String
            code.append("(representation_").append(counter).append(".getStrictness()!=null) ? representation_").append(counter).append(".getStrictness().name() : \"NULL\" , "); // Stricness as String
            code.append("(representation_").append(counter).append(".getCardinality()!=null) ? representation_").append(counter).append(".getCardinality().name() : \"NULL\" , "); // Cardinality as String
            code.append("representation_").append(counter).append(".getStructure().equals(datastructure.ALIGNMENT) || representation_").append(counter).append(".getStructure().equals(datastructure.STRUCTUREALIGNMENT), ");
            code.append("streamsSupported_").append(counter).append(");").append(br); // stream support
            
            // case ServerFile
            code.append("} else if (input_").append(counter).append(" instanceof ServerFileConnection) {").append(br);
            
             code.append("\tString validatorImpl_").append(counter).append(";").append(br);
            code.append("\tList<String> converterChain_").append(counter).append(";").append(br);
            code.append("\tif(skipValidation_").append(counter).append(") {").append(br);
            code.append("\t\tvalidatorImpl_").append(counter).append(" = null;").append(br);
            code.append("\t\tconverterChain_").append(counter).append(" = null;").append(br);
            code.append("\t} else {").append(br);
            code.append("\t\tvalidatorImpl_").append(counter).append(" = UniversalValidator.getValidatorImlementation(representation_").append(counter).append(");").append(br);
            code.append("\t\tconverterChain_").append(counter).append(" = UniversalConverter.getStreamConverterOrder(representation_").append(counter).append(", representation_input").append("_").append(counter);
            
            code.append(");").append(br);
            code.append("\t}").append(br).append(br);
            
            code.append("\tServerFileConnection connection = (ServerFileConnection) input_").append(counter).append(";").append(br);
            
            code.append("\t").append(args[0]).append(" += bibitools.parseInputLocalFile(\"");
            code.append(fct_input_rep.get(function.getId()).get(counter).getKey()).append("\","); // id
            code.append(args[1]).append(", "); // inputhash
            code.append("connection.getUri(), "); // url
            code.append("generatedInfo, "); // information of the command line generation
            code.append("validatorImpl_").append(counter).append(", "); // validator implementation
            code.append("converterChain_").append(counter).append(", "); // chain of converter implementations
            code.append("(representation_").append(counter).append(".getContent()!=null) ? representation_").append(counter).append(".getContent().name() : \"NULL\" , "); // Content as String
            code.append("(representation_").append(counter).append(".getStrictness()!=null) ? representation_").append(counter).append(".getStrictness().name() : \"NULL\" , "); // Stricness as String
            code.append("(representation_").append(counter).append(".getCardinality()!=null) ? representation_").append(counter).append(".getCardinality().name() : \"NULL\" , "); // Cardinality as String
            code.append("representation_").append(counter).append(".getStructure().equals(datastructure.ALIGNMENT) || representation_").append(counter).append(".getStructure().equals(datastructure.STRUCTUREALIGNMENT), ");
            code.append("streamsSupported_").append(counter).append(");").append(br); // stream support
            
            code.append("} else {").append(br);
            // normal case
            
            code.append("\t").append(args[0]).append(" += bibitools.parseInput(\"").append(fct_input_rep.get(function.
                    getId()).get(counter).getKey()).append("\",").append(args[1]); 
            code.append(", input_").append(counter).append(", ");
            code.append("representation_").append(counter).append(".getType().name(), ");
            code.append("representation_").append(counter).append(".getImplementationType());").append(br);
            
            code.append("}").append(br);; 
            
        }
        return code.toString();
    }

     public String addAllOutputFiles(String arg) {
          
        StringBuilder out = new StringBuilder();
        for(Tfunction.Outputfileref ref: function.getOutputfileref()){
            ToutputFile file = (ToutputFile) ref.getRef();
            String folder = file.getFolder();
            if(folder==null) {
                folder = "";
            }
            out.append("\t\tlocalFoldersAndWildcards.add(new Pair<String,String>(\"").append(folder).append("\",\"").append(file.getFilename()).append("\"));").append(br);    
        }
        
        return out.toString();
    }
    
}
