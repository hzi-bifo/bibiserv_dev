
package de.unibi.cebitec.bibiserv.wizard.bean.enums;

/**
 * Maps filestaes to trafficlight.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public enum FileStates {
    
    correctNoFile(TrafficLightEnum.YELLOW),
    correctFile(TrafficLightEnum.GREEN);
    
    private TrafficLightEnum trafficLight;
    
    private FileStates(TrafficLightEnum trafficLight){
        this.trafficLight = trafficLight;
    }

    public TrafficLightEnum getTrafficLight() {
        return trafficLight;
    }
    
    
}
