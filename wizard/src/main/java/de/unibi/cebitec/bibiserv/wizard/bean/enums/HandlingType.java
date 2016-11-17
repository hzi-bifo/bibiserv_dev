
package de.unibi.cebitec.bibiserv.wizard.bean.enums;

/**
 * Contains all possible handling types for TinputOutput
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public enum HandlingType {

    file("file"),
    stdin("stdin"),
    stdout("stdout"),
    argument("ARGUMENT"),
    none("NONE");

    private String value;

    private HandlingType(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Returns the enum coresponding to the value;
     * @param value value of the enum
     * @return the enum
     */
    public static HandlingType StringToEnum(String value){
        for(HandlingType type:HandlingType.values()){
            if(type.getValue().equals(value.toLowerCase())){
                return type;
            }
        }
        return HandlingType.none;
    }

}
