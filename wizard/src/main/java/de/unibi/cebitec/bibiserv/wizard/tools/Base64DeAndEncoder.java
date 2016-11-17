package de.unibi.cebitec.bibiserv.wizard.tools;

import org.apache.commons.codec.binary.Base64;

/**
 *
 * This class manages all de- and encoding happening in the wizard-application
 * for base64-formats. To accomplish that, the apache-commons-codec.jar is used.
 * 
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class Base64DeAndEncoder {

    /**
     * Encodes a string to base64.
     * 
     * @param plainTextInput String as input
     * @return String containing the encodes base64-data.
     */
    public static String StringToBase64(String plainTextInput) {

        byte[] inputData = plainTextInput.getBytes();

        return ByteArrayToBase64(inputData);

    }

    /**
     * Encodes a byte-array to base64.
     * 
     * @param byteArrayInput byte-array as input.
     * @return String containing the encodes base64-data.
     */
    public static String ByteArrayToBase64(byte[] byteArrayInput) {

        return Base64.encodeBase64String(byteArrayInput);
    }

    /**
     * Decodes Base64-data and returns it as plainText.
     * 
     * @param base64inputData base64-data as input.
     * @return a String containing the decoded content.
     */
    public static String Base64ToString(String base64inputData) {

        return new String(Base64.decodeBase64(base64inputData));
    }
}
