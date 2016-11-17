package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This contains static functions to test if value is correct for Tparam
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ParameterValidator {

    private static final Pattern dateTimeRegExp =
            Pattern.compile(
            "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$");

    public static boolean validateInt(String value, int min, boolean includeMin,
            int max, boolean includeMax) {
        if (!isInteger(value)) {
            return false;
        }
        int intVal = Integer.parseInt(value);

        if (includeMin) {
            if (intVal < min) {
                return false;
            }
        } else {
            if (intVal <= min) {
                return false;
            }
        }
        if (includeMax) {
            if (intVal > max) {
                return false;
            }
        } else {
            if (intVal >= max) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateFloat(String value, float min,
            boolean includeMin, float max, boolean includeMax) {
        if (!isFloat(value)) {
            return false;
        }
        float floatVal = Float.parseFloat(value);

        if (includeMin) {
            if (floatVal < min) {
                return false;
            }
        } else {
            if (floatVal <= min) {
                return false;
            }
        }
        if (includeMax) {
            if (floatVal > max) {
                return false;
            }
        } else {
            if (floatVal >= max) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateBoolean(String value) {
        return (value.equals("true") || value.equals("false"));
    }

    public static boolean validateDateTime(String value) {
        Matcher match = dateTimeRegExp.matcher(value);
        return match.matches();
    }

    public static boolean validateString(String value, int minLength,
            int maxLength, String regexp) {

        if (value.length() < minLength) {
            return false;
        }
        if (value.length() > maxLength) {
            return false;
        }
        if (regexp.length() > 0 && !isRexExp(regexp)) {
            return false;
        }

        if (regexp.length() > 0) {
            Pattern pattern = Pattern.compile(regexp);
            Matcher m = pattern.matcher(value);
            if (!m.matches()) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateEnum(String value, List<String> supportedValues,
            int minOccur, int maxOccur, String separator) {

        String[] values = value.split(separator);
        if(values.length<minOccur || values.length>maxOccur){
            return false;
        }
        for(String val: values){
            if(!supportedValues.contains(val)){
                return false;
            }
        }

        return true;
    }


    /**
     * Test if the inputstring is an integer
     * @param input string to be testes
     * @return true: string is int, false: string not an int
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test if the inputstring is a float
     * @param input string to be testes
     * @return true: string is float, false: string not a float
     */
    public static boolean isFloat(String input) {
        try {
            Float.parseFloat(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test if the inputstring is a regExp
     * @param input string to be testes
     * @return true: string is regExp, false: string not a regExp
     */
    public static boolean isRexExp(String input) {
        try {
            Pattern.compile(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
