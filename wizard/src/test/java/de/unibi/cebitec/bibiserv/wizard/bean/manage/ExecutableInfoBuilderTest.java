package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.techfak.bibiserv.cms.Texecutable;
import de.unibi.techfak.bibiserv.cms.TexecutableTypes;
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
public class ExecutableInfoBuilderTest {

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
     * Test of the createTexecutable-method of ExecutableInfoBuilder-bean.
     */
    @Test
    public void testCreateTexecutable() throws Exception {

        ExecutableInfoBuilder testExecutableInfoBuilder = new ExecutableInfoBuilder();

        String type = "binary";
        String version = "0.1";
        String callingInfo = "testPath";

        Assert.assertNotNull(testExecutableInfoBuilder.getTexecutable());
        Assert.assertEquals("", testExecutableInfoBuilder.getVersion());
        Assert.assertEquals("", testExecutableInfoBuilder.getCallingInfo());
        Assert.assertEquals("", testExecutableInfoBuilder.getType()); 

        testExecutableInfoBuilder.createTexecutable(type,"",callingInfo,"", version);

        Texecutable testTexecutableItem = testExecutableInfoBuilder.getTexecutable();

        Assert.assertEquals(version, testExecutableInfoBuilder.getVersion());
        Assert.assertEquals(callingInfo, testExecutableInfoBuilder.getCallingInfo());
        Assert.assertEquals(type, testExecutableInfoBuilder.getType());
    }
}
