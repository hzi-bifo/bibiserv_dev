package de.unibi.cebitec.bibiserv.wizard.testOutputData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.ejb.Singleton;

/**
 * This class provides a method to write output data.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class OutputWriter {

    private static final String basepath = "target/testOutputData";

    private OutputWriter() {
    }

    /**
     * Writes data to an output file with the specified name.
     * 
     * @param data output data as String.
     * @param filename name of the output file (DO NOT SPECIFY A PATH HERE!).
     */
    public static void writeOutputData(String data, String filename) throws IOException {

        BufferedWriter bufferedWriter = getOutputWriter(filename);
        bufferedWriter.write(data);
        bufferedWriter.close();
    }

    /**
     * Returns an output writer for the specified file.
     * 
     * @param filename name of the output file (DO NOT SPECIFY A PATH HERE!).
     * @return Writer for the file.
     */
    public static BufferedWriter getOutputWriter(String filename) throws IOException {

        String filepath = getPath(filename);
        FileWriter writer = new FileWriter(createDirectoryAndFile(filepath));
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        return bufferedWriter;
    }

    /**
     * Returns the output path a given file would be written to.
     * 
     * @param filename name of the file (DO NOT SPECIFY A PATH HERE!).
     * @return output path.
     */
    public static String getPath(String filename) {
        return new File(basepath + "/" + filename).getAbsolutePath();
    }
    private static boolean directoryChecked = false;

    private static File createDirectoryAndFile(String filepath) throws IOException {
        if (!directoryChecked) {
            directoryChecked = true;
            File basedir = new File(basepath);
            if (!basedir.exists()) {
                boolean directoryWasCreated = basedir.mkdir();
                if (!directoryWasCreated) {
                    directoryChecked = false;
                    throw new IOException("Directory could not be created: " + basedir.getAbsolutePath());
                }
            }
        }

        File outputFile = new File(filepath);
        if (!outputFile.exists()) {
            boolean fileWasCreated = outputFile.createNewFile();
            if (!fileWasCreated) {
                throw new IOException("File could not be created: " + outputFile.getAbsolutePath());
            }
        }
        return outputFile;
    }
}
