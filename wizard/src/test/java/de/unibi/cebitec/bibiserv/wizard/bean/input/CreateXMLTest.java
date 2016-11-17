package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.testInputData.InputLoader;
import de.unibi.techfak.bibiserv.cms.Titem.Description;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This tests the bs2 building.
 * 
 * Comment out test due to ::
 * java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/servlet/ServletException
 * 
 * when running test
 * 
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class CreateXMLTest {

    private static final String preprocessedData = "preprocessedCreateXMLData.xml";
    private static final String microhtmlData = "testMicrohtmlData.xml";

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
     * Test of the buildDescriptionContent-method of the CreateXML-class.
     * 
     * @throws Exception 
     */
//    @Test
//    public void testDescriptionBuilding() throws Exception {
//        String loadTestData = InputLoader.loadTestData(microhtmlData);
//
//        CreateXML createXMLTestBean = new CreateXML();
//        List<Object> descriptionContent =
//                createXMLTestBean.buildDescriptionContent(loadTestData);
//        Assert.assertNotNull(descriptionContent);
//        Description description = new TrunnableItem.Description();
//        description.getContent().addAll(descriptionContent);
//        LoadXMLBean loadXMLTestBean = new LoadXMLBean();
//        System.out.println(loadXMLTestBean.getDescriptionContent(description));
//    }
}
