
package de.unibi.cebitec.bibiserv.wizard.bean.enums;

/**
 * This enum is needed for orderStore and exampleStore to specify the type of
 * the stored name value. The user only knows this type and names and does not
 * need to be bothered with id generation.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public enum IdRefType {

    input("input"), output("output"), parameter("parameter"), none("none");

    private String str;

    private IdRefType(String str){
        this.str = str;
    }

    @Override
    public String toString(){
        return str;
    }
}
