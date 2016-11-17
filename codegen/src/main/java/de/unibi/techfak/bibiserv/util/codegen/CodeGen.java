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

import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Interface declaring a CodeGen typ. See CodeGen_Implementation for an example ...
 * @ToDo : Write better and (a lot of) more documentation.
 *
 *
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public interface CodeGen {

    /**
     * Starts the source code generation.
     *
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException if template can't be processed.
     */
    public void generate() throws CodeGenParserException;

    /**
     * Set a JAXB BiBiServabstraction containing a runnableitem description
     *
     * @param trunnableItem
     */
    public void setRunnableItem(TrunnableItem trunnableItem);

    /**
     * Set a XML description file following the BiBiServAbstraction Schema.
     *
     * @param filename
     */
    public void setRunnableFile(File filename);

    /**
     * Set the filename of the template, which should be processed.
     *
     * @param filename - Filename of the Template
     * @throws java.io.FileNotFoundException
     */
    public void setTemplateFile(File filename) throws FileNotFoundException;
    
    
    /**
     * Set the resourcename of the template, whoich should be processed.
     * 
     * @param resourcename  - Resourcename of the template
     */
    public void setTemplateResource(String resourcename);
            

    /** Set the  directory where to store the generated source.
     *
     * @param dirname - Directoryname where to store the generated source.
     */
    public void setResultDir(File dirname);
   

    /** Return the default template, which should be available as resource.
     * 
     * @return inputstream - Inputstream of the default template corresponding to this class.
     */
    public InputStream getDefaultTemplate();

    

}
