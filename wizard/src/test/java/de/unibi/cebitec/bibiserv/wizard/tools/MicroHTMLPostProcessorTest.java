package de.unibi.cebitec.bibiserv.wizard.tools;

import de.unibi.cebitec.bibiserv.wizard.testInputData.InputLoader;
import de.unibi.cebitec.bibiserv.wizard.testOutputData.OutputWriter;
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
public class MicroHTMLPostProcessorTest {

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
    private static final String POSTPROCESSINGTESTFILE = "preProcessedMiniHTMLData.xml";
    private static final String POSTPROCESSINGOUTPUTFILE = "MicroHTMLPostProcessingOutput.xml";

    /**
     * Test of the postProcessingMethod.
     *
     * @throws Exception
     */
    @Test
    public void testPostProcessing() throws Exception {

        System.out.println("Now start scanning of" + POSTPROCESSINGTESTFILE + "\n\n");

        String testContent = InputLoader.loadTestData(POSTPROCESSINGTESTFILE);

//        System.out.println("--- START OF TEST CONTENT ---");
//        System.out.println(testContent);

        String processedContent = MicroHTMLPostProcessor.postProcessing(testContent);

//        System.out.println("--- START OF PROCESSED TEST CONTENT ---");
//        System.out.println(processedContent);

        Assert.assertTrue(processedContent.contains("<microhtml:p>"));
        Assert.assertTrue(processedContent.contains("</microhtml:p>"));
        Assert.assertFalse(processedContent.contains("<!--startparagraph-->"));
        Assert.assertFalse(processedContent.contains("<!--endparagraph-->"));
        Assert.assertFalse(processedContent.contains("<!--insertparagraph-->"));
        Assert.assertFalse(processedContent.contains("<!--ERROR"));

        System.out.println("Parsed html is written to "
                + OutputWriter.getPath(POSTPROCESSINGOUTPUTFILE)
                + " ... \n\n\n");

        OutputWriter.writeOutputData(processedContent, POSTPROCESSINGOUTPUTFILE);
    }
}
