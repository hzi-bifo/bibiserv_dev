package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.testInputData.InputLoader;
import de.unibi.cebitec.bibiserv.wizard.testOutputData.OutputWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class EditorBeanTest {

    private static final String MICROHTMLINPUTFILE = "testInputFile.xml";
    private static final String HTMLTOMICROHTMLOUTPUTFILE = "HTMLtoMicroHTMLOutput.xml";
    private static final String MICROHTMLTOHTMLOUTPUTFILE = "MicroHTMLtoHTMLOutput.xml";
    private static final String MINIHTMLINPUTFILE = "testInputFileForMiniHTML.xml";
    private static final String HTMLTOMINIHTMLOUTPUTFILE = "HTMLtoMiniHTMLOutput.xml";
    private static final String MINIHTMLTOHTMLOUTPUTFILE = "MiniHTMLtoHTMLOutput.xml";

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
     * Test of the html conversion methods of the EditorBeans.
     */
//    @Test
//    public void xsltParsingTest() throws Exception {
//
//        System.out.println("Now start scanning of" + MICROHTMLINPUTFILE + "\n\n");
//
//        String inputDataString = InputLoader.loadTestData(MICROHTMLINPUTFILE);
//
////        System.out.println("Content of" + MICROHTMLINPUTFILE + ":\n\n\n" + inputDataString + "\n\n\n");
//
//        /*
//         * #####################################
//         * # test of html to microhtml parsing #
//         * #####################################
//         */
//
//        System.out.println("Now starting html to microhtml parsing ... \n\n\n");
//
//        EditorBean testEditorBean = new EditorBean();
//        String microhtml = testEditorBean.rawHTMLtoMicroHTML(inputDataString);
//
//        System.out.println("Parsed microhtml is written to "
//                + OutputWriter.getPath(HTMLTOMICROHTMLOUTPUTFILE)
//                + " ... \n\n\n");
//
//        // Write data to temporal directory for further testing
//        InputLoader.writeTemporalData(microhtml, HTMLTOMICROHTMLOUTPUTFILE);
//        // Also write it to the output directory.
//        OutputWriter.writeOutputData(microhtml, HTMLTOMICROHTMLOUTPUTFILE);
//        
//        /*
//         * #####################################
//         * # test of microhtml to html parsing #
//         * #####################################
//         */
//
//        System.out.println("Now starting microhtml to html parsing of the output-data ... \n\n\n");
//
//        String html = testEditorBean.microHTMLtoHTML(microhtml);
//
//
//        System.out.println("Parsed html is written to "
//                + OutputWriter.getPath(MICROHTMLTOHTMLOUTPUTFILE)
//                + " ... \n\n\n");
//
//        OutputWriter.writeOutputData(html, MICROHTMLTOHTMLOUTPUTFILE);
//
//        /*
//         * ##################################
//         * # New input for minihtml-testing #
//         * ##################################
//         */
//
//        System.out.println("Now start scanning of " + MINIHTMLINPUTFILE + "\n\n");
//
//        inputDataString = InputLoader.loadTestData(MINIHTMLINPUTFILE);
//
////        System.out.println("Content of " + MINIHTMLINPUTFILE + ":\n\n\n" + inputDataString + "\n\n\n");
//
//        /*
//         * #####################################
//         * # test of html to minihtml parsing #
//         * #####################################
//         */
//
//        System.out.println("Now starting html to minihtml parsing ... \n\n\n");
//
//        EditorMiniBean testMiniBean = new EditorMiniBean();
//        String minihtml = testMiniBean.rawHTMLtoMiniHTML(inputDataString);
//
//        System.out.println("Parsed minihtml is written to "
//                + OutputWriter.getPath(HTMLTOMINIHTMLOUTPUTFILE)
//                + " ... \n\n\n");
//
//        // Write data to temporal directory for further testing
//        InputLoader.writeTemporalData(minihtml, HTMLTOMINIHTMLOUTPUTFILE);
//        // Also write it to the output directory.
//        OutputWriter.writeOutputData(minihtml, HTMLTOMINIHTMLOUTPUTFILE);
//
//        /*
//         * #####################################
//         * # test of minihtml to html parsing #
//         * #####################################
//         */
//
//        System.out.println("Now starting minihtml to html parsing of the output-data ... \n\n\n");
//
//        html = testMiniBean.miniHTMLtoHTML(minihtml);
//
//        System.out.println("Parsed html is written to "
//                + OutputWriter.getPath(MINIHTMLTOHTMLOUTPUTFILE)
//                + " ... \n\n\n");
//
//        OutputWriter.writeOutputData(html, MINIHTMLTOHTMLOUTPUTFILE);
//
//        System.out.println("###### TEST SUCCEEDED! ######");
//
//    }
}
