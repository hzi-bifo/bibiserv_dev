/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.wizard.testInputData;

import de.unibi.cebitec.bibiserv.wizard.bean.input.EditorBean;
import de.unibi.techfak.bibiserv.BiBiTools;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This Class tests import from (broken) Micro-/MiniHTML to  
 * 
 *
 * @author  Jan Krueger - jkrueger(at) 
 */
public class MicroMiniHTML {
    
    
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
    
    @Test
    public void test(){
        EditorBean eb = new EditorBean();
        try { 
        String html = BiBiTools.i2s(new InputStreamReader(MicroMiniHTML.class.getResourceAsStream("/test.html")));
        String microhtml = BiBiTools.i2s(new InputStreamReader(MicroMiniHTML.class.getResourceAsStream("/test-microhtml.xml")));
        String minihtml = BiBiTools.i2s(new InputStreamReader(MicroMiniHTML.class.getResourceAsStream("/test-minihtml.xml")));
        
        
         //   System.out.println(html);
        
        String result = eb.rawHTMLtoMicroHTML(html);
        
            System.out.println(result);
        
        Assert.assertEquals(strip(microhtml), strip(result));
        
        } catch (IOException e) {
            System.err.println("Can't read from resource!");
        }
        
        
    }
    
    
    public String strip(String input){
        return input.replaceAll("\\s", "").replaceAll("\\n", "");
        
        
        
    }
    
    
}
