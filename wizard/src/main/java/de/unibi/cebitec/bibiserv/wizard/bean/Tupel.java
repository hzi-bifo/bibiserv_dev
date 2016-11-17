/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.wizard.bean;

import java.util.Comparator;

/**
 * Basic Tupe. Just needed to transport data to xhtml.
 * It's just more aesthetic than using HashMaps.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class Tupel<T, S> {

    private T first;
    private S second;

    public Tupel() {
    }

    public Tupel(T first, S second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public void setBoth(T first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Sort String,String tupels by first string
     */
    public static class TupelStringComparator implements Comparator<Tupel<String, String>> {

        @Override
        public int compare(Tupel<String, String> o1,
                Tupel<String, String> o2) {
            return o1.getFirst().compareToIgnoreCase(o2.getFirst());
        }
    }
}
