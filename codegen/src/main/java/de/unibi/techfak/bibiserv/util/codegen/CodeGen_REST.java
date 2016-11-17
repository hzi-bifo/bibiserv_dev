/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
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
 * "Portions Copyrighted 2013 BiBiServ"
 *  
 * Contributor(s): Jan Krueger
 * 
 */
package de.unibi.techfak.bibiserv.util.codegen;

import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import java.io.InputStream;

/**
 * CodeGen_REST extends CodeGenImplementation
 * 
 *
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 *         
 */
public class CodeGen_REST extends CodeGen_Implementation {

    @Override
    public  InputStream getDefaultTemplate(){
        return CodeGen_Implementation.class.getResourceAsStream("/templates/rest_template.java");
    }


    /**
     * Return the full package name of the generated class(es).
     * cms:runnableitem/@id
     *
     * @param arg - not used
     * @return Return the full package name of the generated class(es).
     */
    @Override
    public String getPackageName(String arg) {
        return ("de.unibi.techfak.bibiserv.tools." + runnableitem.getId()+".rest");
    }
    
    
    /**
     * Iterate over all inputs and replace :
     *  - ___NUMBER___ with an empty string (on input) or a consecutive number
     *  - ###INPUT_ID### with an id of current input
     * 
     * @param arg
     * @return 
     */
    
    public String for_each_input(String arg){
        return for_each_input(arg, null);
    }
    
    
    public String for_each_input_separated(String arg){
        return for_each_input(arg, ",");
    }
    
    private String for_each_input(String arg,String sep) {
        
         StringBuilder out = new StringBuilder();
        boolean first = true;
        // iterate over all inputs
        int n = 0;
        for (Tfunction.Inputref inputref : function.getInputref()) {
            String argclone = new String(arg);
            TinputOutput input = (TinputOutput)inputref.getRef();
            String inputid = input.getId();
            // replace all occurences of ###INPUT_ID### with inputid and
            // replace all occurences of ###INPUT_FORMATS### with all possible representations
            // Notice TG: there are no occurences of ###INPUT_FORMATS###, so I commented the corresponding code
            argclone = argclone.replaceAll("###INPUT_ID###", inputid);    
            argclone = argclone.replaceAll("###INPUT_ID_BEAN###",inputid.substring(0,1).toUpperCase()+inputid.substring(1));
            
            if (function.getInputref().size() == 1) { 
                argclone = argclone.replaceAll("___NUMBER___","");
            } else {
                argclone = argclone.replaceAll("___NUMBER___",new Integer(n).toString());
            }
            out.append(argclone);
            n++;
            if (sep != null && n != function.getInputref().size()) {
                out.append(",");
            }
            out.append(br);
        }
        return out.toString();
        // iterate over all inputs
     
        
    }
   
}
