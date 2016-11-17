package de.unibi.cebitec.bibiserv.wizard.tools;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This tests the bs2 post processing.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class IDGeneratorTest {
    
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
    private static final String testType = "testtype_43";
    private static final String testToolName = "test_Tool'_Name";
    private static final String testName = "TEST%%3@''99__NAME";

    /**
     * Test of the different IDGeneration-methods of the IDGenerator-class.
     *
     * @throws Exception
     */
    @Test
    public void testIDGeneration() {
        String currentTestID = IDGenerator.createName(testName);
        Assert.assertTrue(IDGenerator.isValidName(currentTestID));
        Assert.assertEquals(currentTestID, IDGenerator.createName(currentTestID));
        currentTestID = IDGenerator.createTemporaryID(currentTestID, testType);
        Assert.assertTrue(IDGenerator.isValidTempID(currentTestID, testType));
        Assert.assertEquals(currentTestID, IDGenerator.createTemporaryID(currentTestID, testType));
        currentTestID = IDGenerator.finalizeID(testToolName, currentTestID);
        Assert.assertTrue(IDGenerator.isValidID(currentTestID, testToolName));
        Assert.assertEquals(currentTestID, IDGenerator.finalizeID(testToolName, currentTestID));
    }
    
    @Test
    public void testStripFunctions() {
        
        String testTempID = IDGenerator.createTemporaryID(
                IDGenerator.createName(testName), testType);
        String testStrippedName = IDGenerator.stripType(testTempID);
        Assert.assertEquals(IDGenerator.createName(testName), testStrippedName);
        String testID = IDGenerator.finalizeID(testToolName, testTempID);
        Assert.assertEquals(testTempID, IDGenerator.stripToolName(testID));
    }
}
