/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.wizard.bean.enums;

import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.techfak.bibiserv.cms.Tprimitive;

/**
 * This maps the Tprimitive types to corresponding strings
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public enum PrimitiveType {

   STRING("string","String", Tprimitive.STRING),
   INT("int","Int",Tprimitive.INT),
   FLOAT("float","Float", Tprimitive.FLOAT),
   DATETIME("dateTime","DateTime", Tprimitive.DATETIME),
   BOOLEAN("boolean","Boolean",  Tprimitive.BOOLEAN);

    private String name;
    private String label;
    private Tprimitive primitive;

    private PrimitiveType(String name, String label, Tprimitive primitive){
        this.name = name;
        this.label = label;
        this.primitive = primitive;
    }

    public static Tprimitive getPrimitiveForName(String name) throws BeansException{

        for(PrimitiveType prim:PrimitiveType.values()){
            if(prim.getName().equals(name)){
                return prim.getPrimitive();
            }
        }
        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    public static String getNameForPrimitive(Tprimitive primitive) throws BeansException{

        for(PrimitiveType prim:PrimitiveType.values()){
            if(prim.getPrimitive()==primitive){
                return prim.getName();
            }
        }
        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    public static String getLabelForName(String name){
        for(PrimitiveType prim:PrimitiveType.values()){
            if(prim.getName().equals(name)){
                return prim.getLabel();
            }
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public Tprimitive getPrimitive() {
        return primitive;
    }



}
