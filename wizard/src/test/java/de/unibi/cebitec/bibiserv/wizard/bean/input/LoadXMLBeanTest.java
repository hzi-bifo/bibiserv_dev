package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.testInputData.InputLoader;
import de.unibi.cebitec.bibiserv.wizard.tools.MicroHTMLPostProcessor;
import de.unibi.techfak.bibiserv.cms.TexecPath;
import de.unibi.techfak.bibiserv.cms.Texecutable;
import de.unibi.techfak.bibiserv.cms.TexecutableTypes;
import de.unibi.techfak.bibiserv.cms.Tperson;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import java.io.BufferedInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * * Comment out test due to ::
 * java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/servlet/ServletException
 * 
 * when running test
 * 
 * 
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class LoadXMLBeanTest {

    private static final String inputToolDescription = "testInputToolDescription.xml";

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
     * Test of the unmarshallBS2-method of the loadXML-bean.
     */
//    @Test
//    public void testUnmarshallBs2() throws Exception {
//        BufferedInputStream testBS2Stream = InputLoader.getInputStream(inputToolDescription);
//
//        LoadXMLBean testLoadXMLBean = new LoadXMLBean();
//
//        TrunnableItem testUnmarshalledTRunnable =
//                testLoadXMLBean.unmarshallBS2(testBS2Stream);
//
//        //Test some general attributes of the runnableItem.
//
//        Assert.assertEquals("genefisher", testUnmarshalledTRunnable.getId());
//        Assert.assertEquals("GeneFisher", testUnmarshalledTRunnable.getName().
//                get(0).getValue());
//        Assert.assertEquals("Primerdesign", testUnmarshalledTRunnable.getToolTipText().get(0).getValue());
//
//        //Test the description handling.
//
//        String testDescriptionContent = testLoadXMLBean.getDescriptionContent(
//                testUnmarshalledTRunnable.getDescription().get(0));
//        Assert.assertTrue(testDescriptionContent.contains("assumption that genes"));
//
//        //Test the author.
//
//        Tperson author = testUnmarshalledTRunnable.getResponsibleAuthor();
//
//        Assert.assertNotNull(author);
//        Assert.assertEquals("Daniel ", author.getFirstname());
//        Assert.assertEquals("Hagemeier", author.getLastname());
//        Assert.assertEquals("University Bielefeld", author.getOrganisation());
//        Assert.assertEquals("dhagemei@cebitec.uni-bielefeld.de", author.getEmail());
//
//        //Test executable.
//
//        Texecutable executable = testUnmarshalledTRunnable.getExecutable();
//        Assert.assertEquals("2.0", executable.getVersion());
//
//        //Test execInfos.
//
//        TexecPath execInfos = testUnmarshalledTRunnable.getExecutable().getExecInfo();
//
//        //Test view custom content handling.
//
//        String testCustomContent = testLoadXMLBean.getCustomContent(
//                testUnmarshalledTRunnable.getView().get(0).getCustomContent().get(0));
//        Assert.assertTrue(testCustomContent.contains("the calculation of degenerat"));
//
//        Assert.assertNotNull(execInfos);
//        Assert.assertEquals(TexecutableTypes.BINARY, execInfos.getExecutableType());
//    }
}
