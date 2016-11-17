package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.IdRefType;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.PrimitiveType;

/**
 * Store for one parameter example.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ExampleParameterStore implements ExampleStore, Cloneable {

    private String name;
    private IdRefType type;
    private String value;
    private PrimitiveType primitive;
    private float min;
    private float max;
    private boolean includeMin;
    private boolean includeMax;
    private int maxLength;
    private int minLength;
    private String regexp;
    private String defaultValue;

    /**
     * Create store for boolean, Datetime.
     */
    public ExampleParameterStore(String name, String value,
            PrimitiveType primitive, String defaultValue) {
        this.name = name;
        this.value = value;
        this.primitive = primitive;
        this.type = IdRefType.parameter;
        this.defaultValue = defaultValue;

        this.min = 0;
        this.max = 0;
        this.includeMin = false;
        this.includeMax = false;
        this.maxLength = 0;
        this.minLength = 0;
        this.regexp = "";
    }

    /**
     * Create store for int, float.
     */
    public ExampleParameterStore(String name, String value,
            PrimitiveType primitive, float min, float max, boolean includeMin,
            boolean includeMax, String defaultValue) {
        this.name = name;
        this.value = value;
        this.primitive = primitive;
        this.type = IdRefType.parameter;
        this.min = min;
        this.max = max;
        this.includeMin = includeMin;
        this.includeMax = includeMax;
        this.defaultValue = defaultValue;

        this.maxLength = 0;
        this.minLength = 0;
        this.regexp = "";
    }

    /**
     * Create store for string.
     */
    public ExampleParameterStore(String name, String value, String regexp,
            PrimitiveType primitive, int minLength, int maxLength,
            String defaultValue) {
        this.name = name;
        this.value = value;
        this.primitive = primitive;
        this.type = IdRefType.parameter;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.regexp = regexp;
        this.defaultValue = defaultValue;

        this.min = 0;
        this.max = 0;
        this.includeMin = false;
        this.includeMax = false;

    }

    public boolean isIncludeMax() {
        return includeMax;
    }

    public void setIncludeMax(boolean includeMax) {
        this.includeMax = includeMax;
    }

    public boolean isIncludeMin() {
        return includeMin;
    }

    public void setIncludeMin(boolean includeMin) {
        this.includeMin = includeMin;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public PrimitiveType getPrimitive() {
        return primitive;
    }

    public void setPrimitive(PrimitiveType primitive) {
        this.primitive = primitive;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
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
            return (ExampleParameterStore) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
