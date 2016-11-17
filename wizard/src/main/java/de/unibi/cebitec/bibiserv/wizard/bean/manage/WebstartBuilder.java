package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Twebstart;
import com.sun.java.jnlp.Jnlp;

/**
 * Webstart builder.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class WebstartBuilder {

    private static final String ID_BASE_TYPE = "webstart";

    public static Twebstart createView(String id,
        
        String introductoryText, String customContent, Jnlp jlnp, String langcode) {

        Twebstart webstart = new Twebstart();
        webstart.setId(IDGenerator.createTemporaryID(id, ID_BASE_TYPE));
        
        Twebstart.Title title = new Twebstart.Title();
        title.setLang(langcode);
        title.setValue(id);
        webstart.getTitle().add(title);
        
        
        if (customContent != null && !customContent.isEmpty()) {
            Twebstart.CustomContent content = new Twebstart.CustomContent(); 
            content.setLang(langcode); 
            content.getContent().add(customContent);
            webstart.getCustomContent().add(content);
        }
        
        Twebstart.IntroductoryText intro = new Twebstart.IntroductoryText();
        intro.setLang(langcode);
        intro.getContent().add(introductoryText);
        webstart.getIntroductoryText().add(intro);
        
        webstart.setJnlp(jlnp);

        return webstart;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }
}
