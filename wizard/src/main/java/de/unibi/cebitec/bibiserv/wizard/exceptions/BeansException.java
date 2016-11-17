package de.unibi.cebitec.bibiserv.wizard.exceptions;



/**
 *  Thrown by Manager and PrimitiveType.
 *
 *  @author Thomas Gatter <tgatter@cebitec.uni-bielefeld.de>
 */
public class BeansException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private BeansExceptionTypes exceptionType;


    /**
     * Creates an exception with empty message and sets type.
     * @param value
     */
    public BeansException(BeansExceptionTypes value) {
        super("");
        this.exceptionType = value;
    }

    /**
     * Creates an exception with str as message and sets type.
     * @param value
     * @param str
     */
    public BeansException(BeansExceptionTypes value, String str){
        super(str);
        this.exceptionType = value;
    }

    public BeansExceptionTypes getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(BeansExceptionTypes exceptionType) {
        this.exceptionType = exceptionType;
    }

}