
package de.unibi.cebitec.bibiserv.wizard.bean.enums;

import de.unibi.techfak.bibiserv.cms.Tprimitive;
import java.util.ArrayList;
import java.util.List;

/**
 * All GuiElements that can be choosen for parameters with compatibility info.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public enum GuiElement {
    

    SELECTBOOLEANCHECKBOX("SELECTBOOLEANCHECKBOX", new ArrayList<Tprimitive>(){{add(Tprimitive.BOOLEAN);}},-1,-1),
    INPUTTEXT("INPUTTEXT", new ArrayList<Tprimitive>(){{add(Tprimitive.INT);add(Tprimitive.FLOAT);add(Tprimitive.DATETIME);add(Tprimitive.BOOLEAN);add(Tprimitive.STRING);}},-1,-1),
    INPUTTEXTAREA("INPUTTEXTAREA", new ArrayList<Tprimitive>(){{add(Tprimitive.INT);add(Tprimitive.FLOAT);add(Tprimitive.DATETIME);add(Tprimitive.BOOLEAN);add(Tprimitive.STRING);}},-1,-1),
    
    SELECTONERADIO("SELECTONERADIO", new ArrayList<Tprimitive>(),0,1),
    SELECTONEMENU("SELECTONEMENU", new ArrayList<Tprimitive>(),0,1),
    SELECTONELISTBOX("SELECTONELISTBOX", new ArrayList<Tprimitive>(),0,1),
    
    SELECTMANYCHECKBOX("SELECTMANYCHECKBOX", new ArrayList<Tprimitive>(),2 , Integer.MAX_VALUE),
    SELECTMANYMENU("SELECTMANYMENU", new ArrayList<Tprimitive>(),2, Integer.MAX_VALUE),
    SELECTMANYLISTBOX("SELECTMANYLISTBOX", new ArrayList<Tprimitive>(),2,Integer.MAX_VALUE);
    
    
    private String name;
    private List<Tprimitive> compatibleTypes;
    private int minoccurs;
    private int maxoccurs;
    
    private GuiElement(String name, List<Tprimitive> compatibleTypes, int minoccurs, int maxoccurs){
        this.name = name;
        this.compatibleTypes = compatibleTypes;
        this.minoccurs = minoccurs;
        this.maxoccurs = maxoccurs;
    }
    
    /**
     * Return a list of al possible elements for a Tenumparam with given maxoccurs.
     * Return order is the order of items in this enum!
     * @param maxoccurs maxoccurs of the TenumParam
     * @return List of al possible GuiElements as their string representation.
     */
    public static List<String> getPossibleElementsEnum(int maxoccurs){
        List<String> ret = new ArrayList<String>();
        
        for(GuiElement element: GuiElement.values()){
            if(element.minoccurs<=maxoccurs && (element.maxoccurs>=maxoccurs)){
                ret.add(element.name);
            }
        }   
        return ret;
    }
    
     /**
     * Return a list of al possible elements for a Tparam with given type.
     * Return order is the order of items in this enum!
     * @param type type of the Tparam
     * @return List of al possible GuiElements as their string representation.
     */
    public static List<String> getPossibleElements(Tprimitive type){
        List<String> ret = new ArrayList<String>();
        
        for(GuiElement element: GuiElement.values()){
            if(element.compatibleTypes.contains(type)){
                ret.add(element.name);
            }
        }   
        return ret;
    }
    
    
}
