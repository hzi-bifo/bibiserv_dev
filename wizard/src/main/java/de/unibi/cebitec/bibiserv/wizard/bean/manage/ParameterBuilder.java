package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.EnumValueStore;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.TenumValue;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.Tprimitive;
import java.util.List;

/**
 * Class used to create new parameter.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ParameterBuilder {

    private static final String ID_BASE_TYPE = "parameter";

    private static Tparam createParameterBase(String name,
            String shortDesc, String desc, String option,
            String defaultValue, String guiElement, String langcode) {

        Tparam param = new Tparam();

        param.setId(IDGenerator.createTemporaryID(name, ID_BASE_TYPE));

        Tparam.Name paramName = new Tparam.Name();
        paramName.setLang(langcode);
        paramName.setValue(name);
        param.getName().add(paramName);

        Tparam.ShortDescription paramShortDescription =
                new Tparam.ShortDescription();
        paramShortDescription.setLang(langcode);
        paramShortDescription.setValue(shortDesc);
        param.getShortDescription().add(paramShortDescription);

        Tparam.Description paramDescription = new Tparam.Description();
        paramDescription.setLang(langcode);
        paramDescription.getContent().add(desc);
        param.getDescription().add(paramDescription);

        if (option.length() > 0) {
            param.setOption(option);
        }

        if (defaultValue.length() > 0) {
            param.setDefaultValue(defaultValue);
        }

        return param;
    }

    public static Tparam createParameterAsString(String name,
            String shortDesc, String desc, String option, String defaultValue,
            String regexp, String minLength, String maxLength, String guiElement,
            String langcode) {

        Tparam param = createParameterBase(name, shortDesc, desc, option,
                defaultValue, guiElement, langcode);

        param.setType(Tprimitive.STRING);
        
        param.setGuiElement(guiElement);

        if (regexp.length() > 0) {
            param.setRegexp(regexp);
        }

        if (minLength.length() > 0) {
            param.setMinLength(Integer.parseInt(minLength));
        }
        if (maxLength.length() > 0) {
            param.setMaxLength(Integer.parseInt(maxLength));
        }

        return param;
    }

    public static Tparam createParameterAsDateTime(String name,
            String shortDesc, String desc, String option, String defaultValue,
            String guiElement, String langcode) {

        Tparam param = createParameterBase(name, shortDesc, desc, option,
                defaultValue, guiElement, langcode);

        param.setType(Tprimitive.DATETIME);
        param.setGuiElement(guiElement);

        return param;
    }

    public static Tparam createParameterAsBoolean(String name,
            String shortDesc, String desc, String option, String defaultValue,
            String guiElement, String langcode) {

        Tparam param = createParameterBase(name, shortDesc, desc, option,
                defaultValue, guiElement,langcode);

        param.setType(Tprimitive.BOOLEAN);
        param.setGuiElement(guiElement);

        return param;
    }

    public static Tparam createParameterAsInt(String name,
            String shortDesc, String desc, String option, String defaultValue,
            String min, boolean includeMin, String max, boolean includeMax,
            String guiElement, String langcode) {

        Tparam param = createParameterBase(name, shortDesc, desc, option,
                defaultValue, guiElement, langcode);

        param.setType(Tprimitive.INT);
        param.setGuiElement(guiElement);

        if (min.length() > 0) {
            Tparam.Min paramMin = new Tparam.Min();
            paramMin.setIncluded(includeMin);
            paramMin.setValue(Integer.parseInt(min));
            param.setMin(paramMin);
        }

        if (max.length() > 0) {
            Tparam.Max paramMax = new Tparam.Max();
            paramMax.setIncluded(includeMax);
            paramMax.setValue(Integer.parseInt(max));
            param.setMax(paramMax);
        }

        return param;
    }

    public static Tparam createParameterAsFloat(String name,
            String shortDesc, String desc, String option, String defaultValue,
            String min, boolean includeMin, String max, boolean includeMax,
            String guiElement, String langcode) {

        Tparam param = createParameterBase(name, shortDesc, desc, option,
                defaultValue, guiElement, langcode);

        param.setType(Tprimitive.FLOAT);
        param.setGuiElement(guiElement);

        if (min.length() > 0) {
            Tparam.Min paramMin = new Tparam.Min();
            paramMin.setIncluded(includeMin);
            paramMin.setValue(Float.parseFloat(min));
            param.setMin(paramMin);
        }

        if (max.length() > 0) {
            Tparam.Max paramMax = new Tparam.Max();
            paramMax.setIncluded(includeMax);
            paramMax.setValue(Float.parseFloat(max));
            param.setMax(paramMax);
        }
        return param;
    }

    public static TenumParam createParameterAsEnum(String name,
            String shortDesc, String desc, String option, String type,
            String prefix, String suffix, String seperator,
            List<EnumValueStore> values, String minoccures, String maxoccures,
            String guiElement, String langcode) {

        TenumParam param = new TenumParam();

        param.setId(IDGenerator.createTemporaryID(name, ID_BASE_TYPE));

        TenumParam.Name paramName = new TenumParam.Name();
        paramName.setLang(langcode);
        paramName.setValue(name);
        param.getName().add(paramName);

        TenumParam.ShortDescription paramShortDescription =
                new TenumParam.ShortDescription();
        paramShortDescription.setLang(langcode);
        paramShortDescription.setValue(shortDesc);
        param.getShortDescription().add(paramShortDescription);

        TenumParam.Description paramDescription = new TenumParam.Description();
        paramDescription.setLang(langcode);
        paramDescription.getContent().add(desc);
        param.getDescription().add(paramDescription);

        if (type.equals("string")) {
            param.setType(Tprimitive.STRING);
        } else if (type.equals("int")) {
            param.setType(Tprimitive.INT);
        } else if (type.equals("float")) {
            param.setType(Tprimitive.FLOAT);
        } else if (type.equals("dateTime")) {
            param.setType(Tprimitive.DATETIME);
        } else if (type.equals("boolean")) {
            param.setType(Tprimitive.BOOLEAN);
        }

        if (option.length() > 0) {
            param.setOption(option);
        }
        if (suffix.length() > 0) {
            param.setSuffix(suffix);
        }
        if (prefix.length() > 0) {
            param.setPrefix(prefix);
        }

        param.setSeparator(seperator);

        param.setGuiElement(guiElement);

        for (EnumValueStore enums : values) {
            TenumValue enumValue = new TenumValue();
            enumValue.setKey(enums.getKey());
            enumValue.setValue(enums.getValue());

            TenumValue.Name enumName = new TenumValue.Name();
            enumName.setLang(langcode);
            enumName.setValue(enums.getName());
            enumValue.getName().add(enumName);
            enumValue.setDefaultValue(enums.isDefaultValue());
            param.getValues().add(enumValue);
        }

        if (minoccures.length() > 0) {
            param.setMinoccurs(Integer.parseInt(minoccures));
        }

        if (maxoccures.length() > 0) {
            param.setMaxoccurs(Integer.parseInt(maxoccures));
        }

        return param;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }

    
}
