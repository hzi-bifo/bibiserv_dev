package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.HandlingType;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.IdRefType;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.cebitec.bibiserv.wizard.tools.Base64DeAndEncoder;

/**
 * Store for one input example.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ExampleInputStore implements ExampleStore, Cloneable {

    private String name;
    private IdRefType type;
    private String value;
    private HandlingType handling;
    private String nonbase64;

    public ExampleInputStore(String name, String value, HandlingType handling) {
        this.name = name;
        this.type = IdRefType.input;
        this.value = value;
        this.handling = handling;

        switch (handling) {
            case file:
            case stdin:
            case stdout:
                if (value.isEmpty()) {
                    nonbase64 = "";
                } else {
                    nonbase64 = PropertyManager.getProperty("inputFileUploaded");
                }
                break;
            case argument:
                nonbase64 = Base64DeAndEncoder.Base64ToString(value);
                break;
        }
    }

    public HandlingType getHandling() {
        return handling;
    }

    public void setHandling(HandlingType handling) {
        this.handling = handling;
    }

    public String getNonbase64() {
        return nonbase64;
    }

    public void setNonbase64(String nonbase64) {
        this.nonbase64 = nonbase64;
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

            return (ExampleInputStore) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
