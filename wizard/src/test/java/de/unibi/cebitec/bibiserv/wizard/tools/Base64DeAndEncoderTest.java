package de.unibi.cebitec.bibiserv.wizard.tools;

import de.unibi.cebitec.bibiserv.wizard.testInputData.InputLoader;
import java.io.BufferedInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class Base64DeAndEncoderTest {
    
    private static final String testFile = "testInputFile.xml";
    
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
     * Tests all methods of the Base64DeAndEncoder-class.
     */
    @Test
    public void deAndEncodingTest() throws Exception {
        
        String testInputText = InputLoader.loadTestData(testFile);
        
        String encodedTestInputText = Base64DeAndEncoder.StringToBase64(testInputText);
        String decodedTestInputText = Base64DeAndEncoder.Base64ToString(encodedTestInputText);
        
        Assert.assertEquals(testInputText, decodedTestInputText);
        
        // Now test it with a file as byte array.
        
        BufferedInputStream testFileStream = InputLoader.getInputStream(testFile);
        byte[] byteArrayTestInputData = new byte[testInputText.length()];
        testFileStream.read(byteArrayTestInputData);
        testFileStream.close();
        
        String encodedTestInputData = Base64DeAndEncoder.ByteArrayToBase64(byteArrayTestInputData);
        String decodedTestInputData = Base64DeAndEncoder.Base64ToString(encodedTestInputData);
        
        byte[] byteArrayTestOutputData = decodedTestInputData.getBytes();
        
        Assert.assertArrayEquals(byteArrayTestInputData, byteArrayTestOutputData);
    }
}
