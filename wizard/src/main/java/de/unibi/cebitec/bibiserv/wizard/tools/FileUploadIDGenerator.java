package de.unibi.cebitec.bibiserv.wizard.tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class generates timestamps for naming tmp-directories for one tool-
 * generating session.
 * 
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class FileUploadIDGenerator {

    private static final String timeStampFormat = "yyyy-MM-dd_HH-mm-ss-SSS";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(timeStampFormat);
    private static final String primefacesUploadTmpDir = "/tmp";
    /*
     * If new directories should be created in a special directory, please specify
     * it here. Note: remember to add a slash at the end of the directory-name.
     */
    private static final String baseUploadDirectoryPath = "target/";

    /*
     * Creates an ID and generates the respective upload directory.
     */
    public static String generateFileUploadID() {
        String newID = baseUploadDirectoryPath + "upload_" + generateTimeStamp();
        (new File(newID + OutputFileSystemManager.DOWNLOADDIR)).mkdirs();
        (new File(newID + OutputFileSystemManager.IMGDIR)).mkdirs();
        //Also create a temporary directory for primefaces uploads.
        (new File(primefacesUploadTmpDir)).mkdirs();

        return newID;
    }

    public static String generateTimeStamp() {
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}
