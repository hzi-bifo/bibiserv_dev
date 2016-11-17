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
import static de.unibi.techfak.bibiserv.util.codegen.Main.log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Abstract CogeGen that implements some parts of the CodeGen interface,
 * that are propably equal in every implementing class.
 *
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public abstract class Abstract_CodeGen implements CodeGen {

    protected InputStream template = null;
    
    protected File resultdir = null;
    protected String resultfilename = null;
    protected File srcdir = null;
    protected File configdir = null;
    protected File resourcedir = null;
    protected File pagesdir = null;

    protected File xmlfile=null;
    protected TrunnableItem runnableitem;


    

    @Override
    public void setRunnableFile(File filename) {
        xmlfile=filename;
        /* read runnableitem from XML File into the JAXB objects using the JAXB Marshaller */
        try {
            JAXBContext jaxbc = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms",this.getClass().getClassLoader());
            Unmarshaller um = jaxbc.createUnmarshaller();
            JAXBElement<TrunnableItem> jaxbe = (JAXBElement) um.unmarshal(filename);
            runnableitem = jaxbe.getValue();
        } catch (JAXBException e) {
            log.error("An JAXBException occured (fn :{})",filename);
        }
    }

    @Override
    public void setRunnableItem(TrunnableItem runnableitem) {
        this.runnableitem = runnableitem;
    }

    @Override
    public void setTemplateFile(File filename) throws FileNotFoundException{
        resultfilename = filename.getName();
        template = new FileInputStream(filename);
    }
    
    
    @Override
    public void setTemplateResource(String resourcename) {
        String [] tmp = resourcename.split("/");
        resultfilename = tmp[tmp.length-1];
        template = getClass().getResourceAsStream(resourcename);
    }

    @Override
    public void setResultDir(File resultdir) {
        this.resultdir = resultdir;
        srcdir = new File (resultdir,"/src/main/java");
        if (!srcdir.isDirectory()) {
            if (srcdir.mkdirs()) {
                log.info("Dir '{}' created ...",srcdir);
            }
        }
        configdir = new File(resultdir,"/src/main/config");
        if (!configdir.isDirectory()){
            if (configdir.mkdirs()) {
                log.info("Dir '{}' created ...",configdir);
            }
        }
        resourcedir = new File(resultdir,"/src/main/resources");
        if (!resourcedir.isDirectory()) {
            if (resourcedir.mkdirs()) {
                log.info("Dir '{}' created ...",resourcedir);
            }
        }
        pagesdir = new File(resultdir,"/src/main/pages");
        if (!pagesdir.isDirectory()){
            if (pagesdir.mkdirs()){
                log.info("Dir '{}' created ...",pagesdir);
            }
        }
    }
    
       /**
     * Return the argument in the case the tool provides more than one function,
     * and an empty string otherwise.
     *
     * @param arg
     * @return
     */
    public String more_than_one_function(String arg) {
        if (runnableitem.getExecutable().getFunction().size() > 1) {
            return arg;
        }
        return "";
    }

    /**
     * Return the argument in the case the tool provides one function,
     * and an empty string otherwise.
     *
     * @param arg
     * @return
     */
    public String one_function(String arg) {
        if (runnableitem.getExecutable().getFunction().size() == 1) {
            return arg;
        }
        return "";
    }
}
