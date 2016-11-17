/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.wizard.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This is a HashMap storing referenced objects and the objects that reference
 * to them.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class ReferenceMap<T, E> {

    HashMap<T, ArrayList<E>> hashMap = new HashMap<T, ArrayList<E>>();

    public ReferenceMap() {
    }

    /**
     * Puts a new object into the map.
     *
     * @param referencedObject referenced object
     * @param referencingObject object that references the referenced object.
     * @return true if the object was new and inserted into the map.
     */
    public boolean put(T referencedObject, E referencingObject) {
        if (hashMap.containsKey(referencedObject)) {
            ArrayList<E> referencingList = hashMap.get(referencedObject);
            referencingList.add(referencingObject);
            hashMap.put(referencedObject, referencingList);
            return false;
        } else {
            ArrayList<E> referencingList = new ArrayList<E>();
            referencingList.add(referencingObject);
            hashMap.put(referencedObject, referencingList);
            return true;
        }
    }

    public Set<T> getObjects() {
        return hashMap.keySet();
    }

    public ArrayList<E> getReferencingObjects(T key) {
        ArrayList<E> resultList = hashMap.get(key);
        if (resultList == null) {
            return new ArrayList<E>();
        } else {
            return resultList;
        }
    }
}
