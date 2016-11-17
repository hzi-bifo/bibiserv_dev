package de.unibi.cebitec.bibiserv.wizard.bean;

import java.util.List;

/**
 * This class is an interface for manager beans.
 * 
 * Please note! Until now this is conceptual work and not yet widely used.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public interface ManagerInterface<X> {
    
    /**
     * 
     * @return a list of names of all objects of this type that have been 
     * created by the user until now.
     */
    public List<String> getAllNames();
    
    /**
     * 
     * @return a list of information holding objects of type X for all objects of
     * this type the user has created until now.
     */
    public List<X> getAllInfoObjects();
    
    /**
     * 
     * @return true if the user has not yet created and saved an object of this
     * type yet.
     */
    public boolean isEmpty();
    
}
