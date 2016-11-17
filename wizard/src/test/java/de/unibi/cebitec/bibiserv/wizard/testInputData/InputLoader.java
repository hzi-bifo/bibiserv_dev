package de.unibi.cebitec.bibiserv.wizard.testInputData;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class provides a method to load input data.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class InputLoader {

    private static InputLoader singleton;
    private static final String newline = System.getProperty("line.separator");

    private InputLoader() {
    }

    /**
     * Loads data from a test file.
     * 
     * @param filename name of the file (DO NOT SPECIFY A PATH HERE!).
     * @return data in the file (or null if the file could not be found).
     */
    public static String loadTestData(String filename) throws URISyntaxException,
            IOException {
        if (singleton == null) {
            singleton = new InputLoader();
        }

        URI resourcePath = singleton.getClass().getResource("/"+filename).toURI();

        if (resourcePath == null) {
            return null;
        }
        FileReader fileReader = new FileReader(new File(resourcePath));
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder builder = new StringBuilder();
        String nextLine;
        while ((nextLine = bufferedReader.readLine()) != null) {
            builder.append(nextLine);
            builder.append(newline);
        }
        bufferedReader.close();
        return builder.toString();
    }

    /**
     * Opens an inputStream for the specified test file.
     * 
     * @param filename name of the file (DO NOT SPECIFY A PATH HERE!).
     * @return InputStream for the file (or null if the file could not be found).
     */
    public static BufferedInputStream getInputStream(String filename) {
        if (singleton == null) {
            singleton = new InputLoader();
        }
        return new BufferedInputStream(singleton.getClass().getResourceAsStream("/"+filename));
    }

    /**
     * Writes data that is needed for later tests to the input directory.
     * 
     * @param data test data as string.
     * @param filename name of the temporal file (DO NOT SPECIFY A PATH HERE!).
     */
    public static void writeTemporalData(String data, String filename) throws IOException {
        if (singleton == null) {
            singleton = new InputLoader();
        }
        String filepath = singleton.getClass().getResource("").getPath() + "/" + filename;
        FileWriter writer = new FileWriter(filepath);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(data);
        bufferedWriter.close();
    }
}
