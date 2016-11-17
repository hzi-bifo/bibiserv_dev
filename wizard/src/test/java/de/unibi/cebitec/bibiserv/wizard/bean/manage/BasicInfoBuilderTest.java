package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.testOutputData.OutputWriter;
import de.unibi.techfak.bibiserv.cms.ObjectFactory;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class BasicInfoBuilderTest {

    private static final String outputFileName = "test-basic-description.bs2";

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
     * Test of the createTrunnable-method of BasicInfoBuilder-bean.
     */
    @Test
    public void testCreateTrunnable() throws Exception {

        BasicInfoBuilder testBasicInfoBuilder = new BasicInfoBuilder();

        String toolName = "TestTool";
        String toolShortDescription = "This is a Test-Tool";
        String toolDescription = "This is a tool for test-purposes";
        String toolCustomContent = "testCustomContent";
        String toolTipText = "This is the tools toolTipText";
        String keywords = "test,testtest,testtesttest";

        testBasicInfoBuilder.createTrunnable(toolName, toolShortDescription,
                toolDescription, toolCustomContent, toolTipText, keywords);

        TrunnableItem testTRunnableItem = testBasicInfoBuilder.getTRunnable();

        createBS2(testTRunnableItem);

        Assert.assertEquals(toolName, testBasicInfoBuilder.getToolName());
        Assert.assertEquals(toolShortDescription, testBasicInfoBuilder.getShortDescription());
        Assert.assertEquals(toolDescription, testBasicInfoBuilder.getDescription());
        Assert.assertEquals(toolTipText, testBasicInfoBuilder.getToolTipText());
        Assert.assertEquals(keywords, testBasicInfoBuilder.getKeywords());
    }

    private void createBS2(TrunnableItem runnableItem) throws Exception {

        JAXBContext context = JAXBContext.newInstance("de.unibi.techfak.bibiserv.cms");
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        ObjectFactory factory = new ObjectFactory();
        BufferedWriter outputWriter = OutputWriter.getOutputWriter(outputFileName);

        m.marshal(factory.createRunnableItem(runnableItem), outputWriter);

        outputWriter.close();
    }
}
