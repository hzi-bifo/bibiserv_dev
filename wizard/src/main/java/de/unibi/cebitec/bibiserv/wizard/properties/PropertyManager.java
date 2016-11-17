/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.wizard.properties;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;


/**
 * This class gives static access to properties file.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class PropertyManager {

    /**
     * Location URL of resources.
     */
    private static final String RESOURCE_LOCATION =
            "/de/unibi/cebitec/bibiserv/wizard/properties/messages";

    private PropertyManager(){
    }

    /**
     * Returns the String corresponding to key in properties file.
     * @param key key in properties file
     * @return the String corresponding to key
     */
    public static String getProperty(String key){

        FacesContext context = FacesContext.getCurrentInstance();
        Locale currentLocal = context.getViewRoot().getLocale();

        ResourceBundle manager = ResourceBundle.getBundle(RESOURCE_LOCATION, currentLocal);

        try {
             return manager.getString(key);
        } catch(MissingResourceException e){
            return "";
        }
    }

}
