package de.unibi.cebitec.bibiserv.wizard.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * This class does all xslt processing for
 * microhtml/minihtml/plainHTML-conversion.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class XSLTProcessor {
    
    /**
     * A regular expression to detect unescaped ampers ands.
     */
    private static String ampersAndRegex = "&([^a]|a[^m]|am[^p]|amp[^;])";
    /**
     * Contains the escape sequence for ampers ands.
     */
    private static String ampersAndReplacement = "&amp;$1";

    /**
     * Calls a given xslt-script and uses it to convert the input data.
     *
     * @param inputData valid (!) xml-input-data.
     * @param scriptFileName filename (not path!) of the xslt-script. The script
     * has to be located in the properties-package.
     * @return processed input data.
     */
    public static String doXSLTConversion(String inputData, String scriptFileName)
            throws TransformerException {

        // preprocessing: escape all ampers ands.

        inputData = inputData.replaceAll(ampersAndRegex, ampersAndReplacement);

        String outputData = null;

        // Define xslt-script.

        BufferedInputStream xsltStream = new BufferedInputStream(
                XSLTProcessor.class.getResourceAsStream("../properties/" + scriptFileName));

        //open a buffered Stream for input-html.

        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new ByteArrayInputStream(inputData.getBytes(Charset.forName("UTF-8"))));
        //Open a buffered Stream for processed outputData.
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream =
                new BufferedOutputStream(byteStream);

        try {

            //Define JAXP-Classes.

            Source xmlSource = new StreamSource(bufferedInputStream);
            Source xsltSource = new StreamSource(xsltStream);
            Result resultStream = new StreamResult(bufferedOutputStream);

            //create the transformer.

            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer(
                    xsltSource);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            //do actual xslt-transforming.

            transformer.transform(xmlSource, resultStream);

            outputData = byteStream.toString("UTF-8");

        } finally {
            try {
                xsltStream.close();
                bufferedInputStream.close();
                bufferedOutputStream.close();
            } catch (IOException e) {
            }
            return outputData;
        }
    }
}
