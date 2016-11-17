/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.IdRefType;
import java.util.Arrays;
import java.util.List;

/**
 * Store for one parameterenum example.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ExampleParameterEnumStore implements ExampleStore, Cloneable {

    private String name;
    private IdRefType type;
    private String value;
    private String separator;
    private int minOccur;
    private int maxOccur;
    private List<String> selected;
    private String defaultValue;

    /**
     * Contain Key, Name/Value (in one String) pairs.
     */
    private List<Tupel<String, String>> enumValues;

    public ExampleParameterEnumStore(String name, String value,
            List<Tupel<String, String>> enumValues, String separator,
            int minOccur, int maxOccur, String defaultValue) {
        this.name = name;
        this.type = IdRefType.parameter;
        this.value = value;
        this.enumValues = enumValues;
        this.separator = separator;
        this.minOccur = minOccur;
        this.maxOccur = maxOccur;
        this.defaultValue = defaultValue;

        String[] sel = value.split(this.separator);
        selected = Arrays.asList(sel);
    }

    public int getMaxOccur() {
        return maxOccur;
    }

    public void setMaxOccur(int maxOccur) {
        this.maxOccur = maxOccur;
    }

    public int getMinOccur() {
        return minOccur;
    }

    public void setMinOccur(int minOccur) {
        this.minOccur = minOccur;
    }

    public List<Tupel<String, String>> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(List<Tupel<String, String>> enumValues) {
        this.enumValues = enumValues;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public List<String> getSelected() {
        return selected;
    }

    public void setSelected(List<String> selected) {
        this.selected = selected;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public IdRefType getType() {
        return type;
    }

    @Override
    public void setType(IdRefType type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public ExampleStore clone() {
        try {

            return (ExampleParameterEnumStore) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
