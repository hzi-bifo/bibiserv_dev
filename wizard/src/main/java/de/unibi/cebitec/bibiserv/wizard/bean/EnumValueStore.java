
package de.unibi.cebitec.bibiserv.wizard.bean;

/**
 * Used to save the date of the type TenumValue.
 * TenumValue is not used directly because of the complicated way
 * names are stored and accessed in a list and because we need the index.
 * @author Gatter
 */
public class EnumValueStore {

    private String key;
    private String value;
    private String name;
    private boolean defaultValue;
    private int index;

    public EnumValueStore(){
        key="";
        value="";
        name="";
        defaultValue=false;
        index=0;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


}
