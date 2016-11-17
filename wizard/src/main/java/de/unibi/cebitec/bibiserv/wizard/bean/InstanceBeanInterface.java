package de.unibi.cebitec.bibiserv.wizard.bean;

/**
 * This is an interface for input beans that manage the user input for creating
 * an instance of a certain type.
 *
 * Please note! Until now this is conceptual work and not yet widely used.
 * 
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public interface InstanceBeanInterface {
    
    /**
     * Orders the bean to create a new instance of the respective type.
     */
    public void newInstance();
    
    /**
     * Orders the bean to edit an existing instance of this type.
     * 
     * @param name name of the object that shall be edited.
     */
    public void editInstance(String name);
    
    /**
     * Orders the Bean to remove an existing instance of this type.
     * 
     * @param name name of the object that shall be deleted.
     */
    public void removeInstance(String name);
    
    /**
     * 
     * @return the URL of this bean.
     */
    public String getURL();
    
}
