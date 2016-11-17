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

package <#getPackageName/#>;

import java.io.IOException;
import java.util.Map;
import org.apache.log4j.Logger;
import javax.faces.context.FacesContext;

/**
 * A bean used to reset the session for just this tool.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class <#getClassName/#> {

    private static Logger log = Logger.getLogger(<#getClassName/#>.class);


    public void reset() {
        
       String[] beanIds = {<#getBeanIds/#>};
        
        Map<String,Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        
        for(String id: beanIds) {
            if(sessionMap.containsKey(id)) {
                sessionMap.remove(id);
            }
        }
        
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/<#getToolId/#>");
        } catch (IOException ex) {
            log.warn("Could not redirect on reset: "+ex);
        }
    }

}
