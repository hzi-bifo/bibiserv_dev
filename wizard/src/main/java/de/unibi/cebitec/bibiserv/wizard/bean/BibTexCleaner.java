
package de.unibi.cebitec.bibiserv.wizard.bean;

/**
 * This class contains functions for cleaning all unwanted chars of
 * Bibtex entries.
 * 
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class BibTexCleaner {
    
    /**
     * Remove all , from Name
     * @param name name to clean
     * @return cleaned name
     */
    public static String cleanName(String name){
        return name.replace(",", "");
    }
    
    /**
     * Removes all { } " ' from entry.
     * @param entry striing to clean
     * @return cleaned entry
     */
    public static String cleanEntry(String entry){
        return entry.replaceAll("(\\{|\\}|\"|')", "");
    }
    
}
