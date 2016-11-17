package de.unibi.cebitec.bibiserv.wizard.tools;

import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import java.util.HashMap;

/**
 * This class tests if names occur multiple times.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class Disambiguator {

    HashMap<String, Integer> multiplicityMap = new HashMap<String, Integer>();

    /**
     * Tests if a name did occur already and returns how often it did occur.
     * 
     * @param name input name.
     * @return multiplicity of occurence (1 if the name did not occur before).
     */
    public int testAmbiguity(String name) {

        Integer multiplicity = 0;

        if (multiplicityMap.containsKey(name)) {
            multiplicity = multiplicityMap.get(name);
        }
        multiplicity++;
        multiplicityMap.put(name, multiplicity);
        return multiplicity;
    }

    public String disambiguateName(String name, String typestring) {
        return name + testAmbiguity(typestring+"_"+name);
    }
    
    /**
     * Checks if a given objects name is ambigous and returns a copy of that
     * object if it is.
     * 
     * @param inputObject the object itself.
     * @param objectName the objects name.
     * @return the object itself if it is unambigous or a copy.
     */
    public Object disambiguateObject(Object inputObject, String objectName){
        int multiplicity;
        
        if ((multiplicity = testAmbiguity(objectName)) > 1) {
            //If the object is ambigous, return a copy.
            
            if(inputObject instanceof TrunnableItemView){
                return CopyFactory.copyView((TrunnableItemView) inputObject,
                        Integer.toString(multiplicity));
            }
            
            if(inputObject instanceof Tfunction){
                return CopyFactory.copyFunction((Tfunction) inputObject,
                        Integer.toString(multiplicity));
            }
            
            //This should be unreachable.
            
            return null;
            
        } else{
            //If it isn't, return the object itself
            return inputObject;
        }
    }
}
