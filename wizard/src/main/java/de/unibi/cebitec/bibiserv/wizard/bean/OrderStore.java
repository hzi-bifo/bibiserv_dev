
package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.IdRefType;

/**
 * This is used as storage for paramInputOutputOder
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class OrderStore {

    private boolean isString;
    private String value;
    private IdRefType type;


    /**
     * Constructor.
     * @param value Can be an inserted string or the name of a parameter/input/output.
     * @param type Specifies whether id is a input, output or parameter.
     * @param isString  true: this is an inserted string; false: this is the name of a parameter/input/output
     */
    public OrderStore(String value, IdRefType type, boolean isString){
        this.value = value;
        this.type = type;
        this.isString = isString;
    }

    /**
     * true: this is an inserted string; false: this is the name of a parameter/input/output
     */
    public boolean isIsString() {
        return isString;
    }

     /**
     * true: this is an inserted string; false: this is the name of a parameter/input/output
     */
    public void setIsString(boolean isString) {
        this.isString = isString;
    }

    /**
     * Can be an inserted string or the name of a parameter/input/output.
     * @return stringvalue
     */
    public String getValue() {
        return value;
    }

    /**
     * Can be an inserted string or the name of a parameter/input/output.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Specifies whether id is a input, output or parameter.
     */
    public IdRefType getType() {
        return type;
    }

    /**
     * Specifies whether id is a input, output or parameter.
     */
    public void setType(IdRefType type) {
        this.type = type;
    }


}
