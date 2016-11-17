package de.unibi.cebitec.bibiserv.wizard.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class serves as a method store for actions concerning the distribution
 * of Wizard files (e.g.: Zipping output files).
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class OutputFileSystemManager {

    // directory for uploaded images
    public static final String IMGDIR = "/resources/images/";
    // directory for uploaded other files
    public static final String DOWNLOADDIR = "/resources/downloads/";
    // Pattern to check if a given file is an image.
    private static Pattern imageFilenameExtensionsPattern =
            Pattern.compile("[^/]+(.jpg|.gif|.tif|.bmp|.png)");

    /**
     * Zips the files in a given directory and stores the .zip-archive at the
     * specified location.
     *
     * @param sourceDir path of the directory that contains all source files.
     * @param ziplink path of the target .zip that shall be created.
     * @param outputZipFile file the target zip shall be stored into.
     * @throws IOException Any exception that might occur during zipping.
     */
    public static File zipFiles(String sourceDir, String ziplink, File outputZipFile)
            throws IOException {

        // create the target zip file
        outputZipFile = new File(ziplink);
        outputZipFile.createNewFile();
        // open the zip output stream
        FileOutputStream dest = new FileOutputStream(outputZipFile);
        ZipOutputStream zipOutputStream =
                new ZipOutputStream(new BufferedOutputStream(dest));
        zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
        // go through files recursively
        zipDirectoryRecursively(sourceDir, zipOutputStream, "");
        zipOutputStream.close();

        return outputZipFile;
    }
    private static final int bufferLength = 100;

    private static void zipDirectoryRecursively(String inputDirectoryPath,
            ZipOutputStream zipOutputStream, String currentEntryRelativePath)
            throws IOException {

        File inputDirectory = new File(inputDirectoryPath);
        //get a listing of the directory content
        String[] files = inputDirectory.list();
        //if the directory is empty, create an empty entry in the output zip
        if (files.length <= 0) {
            zipOutputStream.putNextEntry(new ZipEntry(currentEntryRelativePath));
            zipOutputStream.closeEntry();
        } else {
            //otherwise go through the list and zip the files
            byte[] readBuffer = new byte[bufferLength];
            int bytesIn;
            //loop through dirList, and zip the files
            for (int i = 0; i < files.length; i++) {
                final File currentFile = new File(inputDirectory, files[i]);
                final String currentFileRelativePath =
                        currentEntryRelativePath + currentFile.getName();
                if (currentFile.isDirectory()) {
                    //if the File object is a directory, call this
                    //function again to add its content recursively
                    String filePath = currentFile.getPath();
                    zipDirectoryRecursively(filePath, zipOutputStream,
                            currentFileRelativePath + "/");
                    //loop again
                    continue;
                }
                //if we reached here, the File object f was not a directory.
                FileInputStream fileInputStream = new FileInputStream(currentFile);
                BufferedInputStream inputStream = new BufferedInputStream(
                        fileInputStream);
                //create a new zip entry
                ZipEntry anEntry = new ZipEntry(currentFileRelativePath);
                //place the zip entry in the ZipOutputStream object
                zipOutputStream.putNextEntry(anEntry);
                //now write the content of the file to the ZipOutputStream
                while ((bytesIn = inputStream.read(readBuffer)) != -1) {
                    zipOutputStream.write(readBuffer, 0, bytesIn);
                }
                //close the Stream
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }
        }
    }

    /**
     * Copies a file to a target directory.
     *
     * @param file a given file
     * @param target path to the directory all files are going to be.
     */
    public static void copyFileToTarget(File file, String target) throws IOException {

        File targetDir = new File(target);
        if (!targetDir.isDirectory()) {
            throw new IOException("Target path does not lead to a directory!");
        }
        //generate target File
        File targetFile = new File(targetDir, file.getName());
        //open streams for copying process
        BufferedInputStream sourceStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream targetStream = new BufferedOutputStream(new FileOutputStream(targetFile));
        //create a buffer
        byte[] buffer = new byte[bufferLength];
        int bytesIn;
        while ((bytesIn = sourceStream.read(buffer)) != -1) {
            targetStream.write(buffer, 0, bytesIn);
        }
        sourceStream.close();
        targetStream.close();
    }

    /**
     *
     * @param filename name of the file to be analyzed
     * @return true if the given file is an image file
     */
    public static boolean isImageFile(String filename) {
        Matcher matcher = imageFilenameExtensionsPattern.matcher(filename);
        return matcher.matches();
    }
}
