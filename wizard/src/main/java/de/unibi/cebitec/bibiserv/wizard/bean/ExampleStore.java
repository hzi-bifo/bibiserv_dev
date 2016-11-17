package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.IdRefType;

/**
 * This is used to store one example for inputs and parameters.
 * Inputs, Parameters and EnumParameters have different instances of this interface.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public interface ExampleStore extends Cloneable  {

    /**
     * Clone the current value;
     * @return
     */
    ExampleStore clone();

    /**
     * Name of an input or a parameter.
     */
    String getName();

    /**
     * Name of an input or a parameter.
     */
    void setName(String name);

    /**
     * Type of the example (input or parameter).
     */
    IdRefType getType();

    /**
     * Type of the example (input or parameter).
     */
    void setType(IdRefType type);

    /**
     * Stored value as string.
     */
    String getValue();

    /**
     * Stored value as string.
     */
    void setValue(String value);

}
