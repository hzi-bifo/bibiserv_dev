/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2010-2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
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
 * "Portions Copyrighted 2010-2013 BiBiServ Team"
 * 
 * Contributor(s): Jan Krueger <jkrueger(at)cebitec.uni-bielefeld.de>
 * 
 */
package <#getPackageName/#>;


import de.unibi.techfak.bibiserv.tools.<#getToolId/#>.Tooldescription;
import de.unibi.techfak.bibiserv.web.beans.SpringApplicationContext;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.apache.log4j.Logger;

/**
 * Provides a GET method that return all function example data in json format 
 * as a zipped byte stream. 
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
@Path("<#getToolId/#>")
public class <#getClassName/#> {

    final static Logger log = Logger.getLogger(<#getClassName/#>.class);
    
    @GET
    @Path("/examplesAsZip")
    public byte[] examplesAsZip(@Context HttpServletResponse response) throws Exception{
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ZipOutputStream zipout = new ZipOutputStream(bout);
            Tooldescription tooldescription = (Tooldescription) SpringApplicationContext.getBean("toolBean_<#getToolId/#>_tooldescription");
            // iterate over all functions
            for (String fct_id : tooldescription.getFunctionIDs()) {
                if (tooldescription.hasExample(fct_id))  {
                    // create new enrty for each function
                    ZipEntry ze = new ZipEntry(fct_id + ".example.json");
                    zipout.putNextEntry(ze);
                    zipout.write(tooldescription.json_skeleton(fct_id).getBytes());
                    zipout.closeEntry();
                }
            }
            zipout.close();
            return bout.toByteArray();
        } catch (Exception e) {
            response.setStatus(500);
            response.addHeader("X-Application-Error-Code", e.getMessage());
            log.error("Exception while 'exampleAsZip'",e);
        }
        response.flushBuffer();
        return null;
    }
}
