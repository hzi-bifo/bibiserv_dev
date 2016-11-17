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
 * "Portions Copyrighted 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 * 
 * Contributor(s):
 * 
 */
package de.unibi.techfak.bibiserv.util.codegen;

import de.unibi.techfak.bibiserv.cms.Tfunction.Inputref;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import java.io.InputStream;
import java.util.List;

/**
 * Class CodeGen_WebSubmissionPage_Input extends CodeGen_WebSubmissionPage_Function,
 * generates for each input an TextArea and FileUpload Field.
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class CodeGen_WebSubmissionPage_Input extends CodeGen_WebSubmissionPage_Function{

    public CodeGen_WebSubmissionPage_Input(){
        super();
        setExtensionName("_p_1");
    }

   
    
    public String for_each_input(String arg){
        StringBuilder out = new StringBuilder();
        boolean first = true;
        // iterate over all inputs
        int n = 0;
        for (Inputref inputref : function.getInputref()) {
            String argclone = new String(arg);
            TinputOutput input = (TinputOutput)inputref.getRef();
            String inputid = input.getId();
            // replace all occurences of ###INPUT_ID### with inputid and
            // replace all occurences of ###INPUT_FORMATS### with all possible representations
            // Notice TG: there are no occurences of ###INPUT_FORMATS###, so I commented the corresponding code
            argclone = argclone.replaceAll("###INPUT_ID###", inputid);     
            if (function.getInputref().size() == 1) { 
                argclone = argclone.replaceAll("___NUMBER___","");
            } else {
                argclone = argclone.replaceAll("___NUMBER___",new Integer(n).toString());
            }
            out.append(argclone);
            n++;
            out.append(br);
        }
        return out.toString();
    }


    /*
     * Generate Links to Detailed descriptions of Reps
     */
    //@TODO: create a proper description page for each rep from the descriptions
    //and labels of its three constituent classes
    private String getRepresentationsAsLinks(String inputid){
        StringBuilder out = new StringBuilder();
        String prefix="";
        String postfix="";

        //NOT READY!
        
        return "no representations found ...";
    }

    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream("/templates/page_submission_input.xhtml");
    }

}
