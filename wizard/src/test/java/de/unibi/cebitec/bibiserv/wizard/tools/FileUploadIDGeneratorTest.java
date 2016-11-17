package de.unibi.cebitec.bibiserv.wizard.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class FileUploadIDGeneratorTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests the ID-generation of the FileUploadIDGenerator.
     */
    @Test
    public void generateFileUploadIDTest() throws Exception {

        String testID = FileUploadIDGenerator.generateFileUploadID();

        File testFile = new File(testID + "/testfile.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(testFile);
        BufferedOutputStream fileWritingStream = new BufferedOutputStream(fileOutputStream);

        byte[] uploadedContent = "testContent".getBytes();

        fileWritingStream.write(uploadedContent);
        fileWritingStream.flush();
        fileWritingStream.close();
    }
}
