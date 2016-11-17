package de.unibi.cebitec.bibiserv.wizard.bean.enums;

/**
 * This enum contains all possible stati of the traffic-light-status-display at
 * overview.xhtml
 * 
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public enum TrafficLightEnum {
    
    RED("resources/redlight.gif"), GREEN("resources/greenlight.gif"),
    YELLOW("resources/yellowlight.gif");
    
    private String path;
    
    private TrafficLightEnum(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    
}
