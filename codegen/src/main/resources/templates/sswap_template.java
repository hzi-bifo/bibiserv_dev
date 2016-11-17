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

import de.unibi.techfak.bibiserv.util.Pair;

import de.unibi.cebitec.bibiserv.semantics.sswap.BiBiServSSWAP;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;

import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoRepresentationImplementation;

import info.sswap.api.model.*;


import java.io.StringReader;

import java.net.URI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class <#getClassName/#> extends BiBiServSSWAP {

    // Namespace for ontology terms for BiBiServ ontology
    private String nsBaseURI;
    private String nsToolSpecificURI;
    private final static Logger logger = Logger.getLogger(<#getPackageName/#>.<#getClassName/#>.class);

    private <#getImplementationClassName/#> service_implementation;
    {
        this.nsBaseURI = "http://bibiserv.techfak.uni-bielefeld.de/ontologies/sswap/";
        this.nsToolSpecificURI = "http://bibiserv.techfak.uni-bielefeld.de/toolontologies/sswap/";
        this.service_implementation = new  <#getImplementationClassName/#>();

    } 

    // constructor
    public <#getClassName/#>() throws ServletException {


    }


        // We override this one method to implement the action of our semantic web service
    @Override
    protected void handleRequest(RIG rig) throws ServletException {
        <#getHandleRequest/#>
    }
}
