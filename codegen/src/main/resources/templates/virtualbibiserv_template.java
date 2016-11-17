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


import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import de.unibi.techfak.bibiserv.tools.<#getId/#>.Tooldescription;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;


@WebService()
public class <#getClassName/#> {



    private Tooldescription tooldescription;

    public void setTooldescription(Tooldescription tooldescription) {
        this.tooldescription = tooldescription;
    }

   
    /**
     * Service method that return a tool description. The tool description is
     * currently an RunableItem Object.
     *

     * @return Return a RunnableItem object.
     */
    @WebMethod()
    @WebResult(name = "runnableItem")
    public TrunnableItem getRunnableItem() {      
           return tooldescription.getToolDescription();
    }

}
