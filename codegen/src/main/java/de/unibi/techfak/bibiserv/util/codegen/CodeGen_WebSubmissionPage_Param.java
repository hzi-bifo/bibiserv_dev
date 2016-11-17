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

import de.unibi.cebitec.bibiserv.util.codegen.web.guiconstruction.ParameterConstructionJSF;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import static de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter.V;
import java.io.InputStream;

/**
 *  Class CodeGen_WebSubmissionInput extends CodeGen_WebSubmissionPage_Function,
 *  generates all parameters form.
 *  @ToDo: the generation result page could be redesigned using authoring tool
 *  from egypt.
 *
 *  @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 *          Daniel Hagemeier - dhagemei[aet]cebitec.uni-bielefeld.de
 */
public class CodeGen_WebSubmissionPage_Param extends CodeGen_WebSubmissionPage_Function{

    public CodeGen_WebSubmissionPage_Param(){
        super();
        setExtensionName("_p_2");
    }

    @Override
    public InputStream getDefaultTemplate() {
        return CodeGen_Implementation.class.getResourceAsStream("/templates/page_submission_param.xhtml");
    }


    /**
     * Generating Parameters of current functions
     *
     * @return jsf components (as String) representing all parameters of current function
     */
    public String generateParameters(String arg) throws CodeGenParserException {
        ParameterConstructionJSF paramconstruct = new ParameterConstructionJSF(runnableitem.getId());
        log.info(V,"Build parameters on JSF page");
        //there is only one paramgroup per function...
        TparamGroup currentGroup=function.getParamGroup();
        if (currentGroup != null) {
            log.info(V,"Generating parameters of function " + currentGroup.getId());
            return paramconstruct.retrieveParameters(currentGroup, function.getId().toString());
        } 
        log.info(V,"no parameter group found ...");
        return "No parameter defined for this function!";
    }

}
