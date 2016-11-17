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
 * Contributor(s): Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 * 
 */
package <#getPackage/#>;

import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.TenumValue;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Element;

/**
 * Bean class which returns tools current tool description
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class Tooldescription implements InitializingBean {

    private static Logger log = Logger.getLogger(Tooldescription.class);
    private TrunnableItem tooldescription;
    private Map<String, Tfunction> functionmap;
    private Element e_tooldescription;
    private Properties buildprop;
    private String build ="XXX";

    /**
     * Service method that return a tool description. The tool description is
     * currently an RunableItem Object.
     *
     * @return Return the runnableItem.
     */
    public TrunnableItem getToolDescription() {
        return tooldescription;
    }

    /**
     * Return a function object belonging to given id.
     *
     * @param id
     * @return
     */
    public Tfunction getFunction(String id) {
        return functionmap.get(id);
    }

    
    /**
     * Returns request hostname
     * 
     * @return 
     */
    public String getHost(){
        return FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
    }
    
    /**
     * Returns request port
     * @return 
     */
    public int getPort(){
        return FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort();
        
    }
    
    
    /**
     * Return protocol type http or https
     * @return Returns protocol
     */
    public String getProtocol(){
	 return ((ServletRequest)(FacesContext.getCurrentInstance().getExternalContext().getRequest())).isSecure()?"https":"http";
    }

    
    /**
     * Service method that returns a tool description as DOM element.
     *
     *
     * @return
     */
    public Element getToolDescriptionAsDOM() {
        if (e_tooldescription == null) {
            try {
                /*
                 * read param from (xml-file)
                 */
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                /*
                 * read description from file
                 */
                e_tooldescription = db.parse(getClass().getResourceAsStream("/runnableitem.xml")).getDocumentElement();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return e_tooldescription;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (tooldescription == null) {
            try {
                // create JAXB Context
                JAXBContext jaxbcontext = JAXBContext.newInstance(TrunnableItem.class);
                // create Unmarshaeller
                Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
                // load runableitem from resource and ...
                JAXBElement<TrunnableItem> jaxbe = (JAXBElement) unmarshaller.unmarshal(getClass().getResourceAsStream("/runnableitem.xml"));
                // ... set tooldescription
                tooldescription = jaxbe.getValue();
                // store all functions in function map for faster access
                functionmap = new LinkedHashMap<String, Tfunction>();
                if (tooldescription.isSetExecutable()) {
                    for (Tfunction tf : tooldescription.getExecutable().getFunction()) {
                        functionmap.put(tf.getId(), tf);
                    }
                }
            } catch (JAXBException e) {
                log.fatal("A JAXBException occurred while reading RunnableItem as Stream from Resource! Errormessage was : " + e.getMessage());
                throw new RuntimeException("A JAXBException occurred while reading RunnableItem as Stream from Resource! Errormessage was : " + e.getMessage(), e);
            }
        } if (buildprop == null) {
            buildprop = new Properties();
            InputStream in  = getClass().getResourceAsStream("/build.properties");
            if (in == null) {
                buildprop.put("build.version", "unknown");
                buildprop.put("build.date","unknown date");
            } else {
                buildprop.load(in);
            }
            build = "built on "+ buildprop.get("build.date")+ " ("+buildprop.get("build.version")+")" ;
        }
        
    }
    
    public String getBuild(){
        return build;
    }
      public Set<String> getFunctionIDs() {
        Set s = functionmap.keySet();
        return s;
    }

    public boolean isSingleFunction() {
        return functionmap.size() == 1;

    }

    public Map<String, String> getFunctionNames() {
        Map<String, String> hm = new LinkedHashMap();
        for (String k : getFunctionIDs()) {
            hm.put(k, functionmap.get(k).getName().get(0).getValue());
        }
        return hm;
    }

    /**
     * Rerturns all parameter, which are defined in the given function.
     *
     * @param fct_id
     * @return Set of param objects, empty set in case of no params defined
     */
    public Set<Tparam> param(String fct_id) {
        Tfunction fct = functionmap.get(fct_id);
        Set s = new HashSet();
        Set s2 = new HashSet();
        if (fct != null && fct.isSetParamGroup()) {
            flattenPG(s, fct.getParamGroup());
        }
        for (Object o : s) {
            if (o instanceof Tparam) {
                s2.add((Tparam) o);
            }
        }
        return s2;
    }

    /**
     * Returns all Enumeration parameter, which are defined in the given
     * function.
     *
     * @param fct_id
     * @return Set of TenumParam object, empty set in case of no enums defined.
     */
    public Set<TenumParam> enumParam(String fct_id) {
        Tfunction fct = functionmap.get(fct_id);
        Set s = new HashSet();
        Set<TenumParam> s2 = new HashSet();
        if (fct != null && fct.isSetParamGroup()) {
            flattenPG(s, fct.getParamGroup());
        }
        for (Object o : s) {
            if (o instanceof TenumParam) {
                s2.add((TenumParam) o);
            }
        }

        return s2;
    }
    
        /** Check if a parameterGroup exists for this function.
     * 
     * @param fct_id
     * @return Check if a parameterGroup exists for this function.
     */
    public boolean isSetPG(String fct_id){
        Tfunction fct = functionmap.get(fct_id);
        if (fct != null) {
            return fct.isSetParamGroup();
        }
        return false;
        
    }

    /**
     * Return all
     *
     * @param fct_id
     * @return
     */
    public Set<TinputOutput> input(String fct_id) {
        Tfunction fct = functionmap.get(fct_id);
        Set<TinputOutput> s = new HashSet();
        for (Tfunction.Inputref ref : fct.getInputref()) {
            s.add((TinputOutput) ref.getRef());
        }
        return s;
    }

    public StreamedContent json_skeleton_Download(String fct_id) {
        return new DefaultStreamedContent(new ByteArrayInputStream(json_skeleton(fct_id).getBytes()), "text/plain", fct_id + ".skeleton.json");
    }

    public String json_skeleton(String fct_id) {
        Tfunction fct = functionmap.get(fct_id);
        if (fct == null) {
            return "unknown function '" + fct_id + "'";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        // inputs
        List<Tfunction.Inputref> list_in_ref = fct.getInputref();
        if (!list_in_ref.isEmpty()) {
            int counter = 1;
            for (Tfunction.Inputref in_ref : list_in_ref) {
                TinputOutput input = (TinputOutput) in_ref.getRef();
                sb.append("\"").append(input.getId()).append("\":\"");
                // type
                sb.append("TYPE[").append(input.getType()).append("]\"");

                if (counter != list_in_ref.size()) {
                    sb.append(",\n");
                }
                counter ++;
               
            }
        }


        // parameterset
        if (fct.isSetParamGroup()) {
        Set s = new HashSet();
        flattenPG(s, fct.getParamGroup());

        if (!s.isEmpty()) {
            // iterate over set
            sb.append(",\n");
            sb.append("\"paramset\":{\n\t");
            int counter = 1;
            for (Object obj : s) {
                // distinguish between param and enum
                if (obj instanceof Tparam) {
                    Tparam p = (Tparam) obj;

                    sb.append("\"").append(p.getId()).append("\":\"");
                    // type
                    sb.append("TYPE[").append(p.getType().value()).append("]");
                    // default
                    if (p.isSetDefaultValue()) {
                        sb.append(",DEFAULT[").append(p.getDefaultValue()).append("]");
                    }
                    // max
                    if (p.isSetMax()) {
                        sb.append(",MAX[").append(p.getMax().getValue()).append("]");
                    }
                    // maxLength
                    if (p.isSetMaxLength()) {
                        sb.append(",MAXLENGTH[").append(p.getMaxLength()).append("]");
                    }
                    // max
                    if (p.isSetMin()) {
                        sb.append(",MIN[").append(p.getMin().getValue()).append("]");
                    }
                    // maxLength
                    if (p.isSetMaxLength()) {
                        sb.append(",MINLENGTH[").append(p.getMinLength()).append("]");
                    }
                    // REGEXP
                    if (p.isSetRegexp()) {
                        sb.append(",REGEXP[").append(p.getRegexp()).append("]");
                    }
                    sb.append("\"");
                    if (counter < s.size()) {
                        sb.append(",");
                    }
                    sb.append("\n\t");
                } else {
                    TenumParam e = (TenumParam) obj;
                    // open construct
                    sb.append("\"").append(e.getId()).append("\":\"");
                    sb.append("TYPE[").append(e.getType().value()).append("]");
                    sb.append(",");
                    sb.append(e.getMinoccurs());
                    sb.append(" to ");
                    sb.append(e.getMaxoccurs());
                    sb.append(" of [");
                    int v_counter = 1;
                    for (TenumValue v : e.getValues()) {
                        sb.append(v.getValue());
                        if (v.isDefaultValue()) {
                            sb.append("*");
                        }
                        if (v_counter != e.getValues().size()) {
                            sb.append(",");
                        }
                        v_counter++;
                    }
                    sb.append("]");
                    // close construct
                    sb.append("\"");
                    if (counter < s.size()) {
                        sb.append(",");
                    }
                    sb.append("\n");

                }
                counter++;
            }
            sb.append("}\n");
        }
        }
        sb.append("}\n");
        return sb.toString();
    }

    
    public boolean hasExample(String fct_id){
        Tfunction fct = functionmap.get(fct_id);
        if (fct == null) {
            return false;

        }
        if (!fct.isSetExample()) {
            return false;

        }
        return true;
    }
    
    public StreamedContent json_example_Download(String fct_id) {
        return new DefaultStreamedContent(new ByteArrayInputStream(json_example(fct_id).getBytes()), "text/plain", fct_id + ".example.json");
    }

    public String json_example(String fct_id) {
        Tfunction fct = functionmap.get(fct_id);
        if (fct == null) {
            return "unknown function '" + fct_id + "'";

        }
        if (!fct.isSetExample()) {
            return "no example available";

        }
        HashMap<String, String> inputs = new HashMap();
        HashMap<String, String> params = new HashMap();

        // get first example
        for (Texample.Prop prop : fct.getExample().get(0).getProp()) {
            //distinguish between param and input
            String propid = prop.getIdref();
            // check input
            if (getInput(fct, propid) != null) {
                inputs.put(propid, prop.getValue());
            }
            // check param && enumparam
            if (getParam(propid) != null || getEnumParam(propid) != null) {
                params.put(propid, prop.getValue());
            }
        }
        // build JSON object
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        for (String k : inputs.keySet()) {
            //Inputs are base64 encoded
            Base64 decoder = new Base64();
            byte[] decodedBytes = decoder.decode(inputs.get(k));

            // encode 


            json.append("\"").append(k).append("\":").append(JSONObject.quote(new String(decodedBytes)));
            json.append(",\n");
        }
        json.append("\"paramset\":{\n");
        if (!params.isEmpty()) {
            for (String k : params.keySet()) {
                json.append("\"").append(k).append("\":\"").append(params.get(k)).append("\",\n");

            }
            // remove last two character 
            json.setLength(json.length() - 2);
        }
        json.append("}\n}\n");
        return json.toString();
    }

    /*
     * private helper methods
     */
    private TinputOutput getInput(Tfunction func, String inputid) {
        for (Tfunction.Inputref iref : func.getInputref()) {
            TinputOutput input = (TinputOutput) iref.getRef();
            if (input.getId().equals(inputid)) {
                return input;
            }
        }
        return null;
    }

    private Tparam getParam(String paramid) {
        for (Tparam param : tooldescription.getExecutable().getParam()) {
            if (param.getId().equals(paramid)) {
                return param;
            }
        }
        return null;
    }

    private TenumParam getEnumParam(String paramid) {
        for (TenumParam param : tooldescription.getExecutable().getEnumParam()) {
            if (param.getId().equals(paramid)) {
                return param;
            }
        }
        return null;
    }

    private void flattenPG(Set s, TparamGroup pg) {
        for (Object obj : pg.getParamrefOrParamGroupref()) {
            if (obj instanceof TparamGroup.ParamGroupref) {
                flattenPG(s, (TparamGroup) (((TparamGroup.ParamGroupref) obj).getRef()));
            } else { // must be a param ref
                s.add(((TparamGroup.Paramref) obj).getRef());
            }
        }
    }
}
