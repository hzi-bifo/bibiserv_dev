package de.unibi.cebitec.bibiserv.wizard.tools;

import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import de.unibi.techfak.bibiserv.cms.TviewType;
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
public class DisambiguatorTest {

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
     * Test of the disambiguator methods of the Disambiguator-class.
     */
    @Test
    public void testDisambiguation() throws Exception {

        Disambiguator testDisambiguator = new Disambiguator();

        String testName = "testName";
        Assert.assertTrue(1 == testDisambiguator.testAmbiguity("test_"+testName));
        Assert.assertTrue(2 == testDisambiguator.testAmbiguity("test_"+testName));
        Assert.assertEquals(testName + "3", testDisambiguator.disambiguateName(testName, "test"));

        testDisambiguator = new Disambiguator();

        TrunnableItemView newView = new TrunnableItemView();
        newView.setId(testName);
        newView.setType(TviewType.OTHER);

        TrunnableItemView disambiguatedview = (TrunnableItemView) testDisambiguator.disambiguateObject(newView, testName);

        Assert.assertSame(newView, disambiguatedview);

        disambiguatedview = (TrunnableItemView) testDisambiguator.disambiguateObject(newView, testName);

        Assert.assertNotSame(newView, disambiguatedview);
        Assert.assertEquals(TviewType.OTHER, disambiguatedview.getType());
    }
}
