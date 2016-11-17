
package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Tmanual;

/**
 * Class used to create Tmanual.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ManualBuilder {

    private static final String ID_BASE_TYPE = "manual";

    /**
     * Creates an object of type Tmanual for given data.
     * @param introduction introductory text in minihtml
     * @param content customContent in minihtml
     * @param Languagecode p.e. "en"
     * @return created tmanual
     */
    public static Tmanual createManual(String introduction, String content,
            String langcode) {

        Tmanual manual = new Tmanual();
        manual.setId(IDGenerator.createTemporaryID(ID_BASE_TYPE, ID_BASE_TYPE));

        if (content != null && !content.isEmpty()) {
            Tmanual.CustomContent custom = new Tmanual.CustomContent();
            custom.setLang(langcode);
            custom.getContent().add(content);
            manual.getCustomContent().add(custom);
        }

        Tmanual.IntroductoryText intro = new Tmanual.IntroductoryText();
        intro.setLang(langcode);
        intro.getContent().add(introduction);
        manual.getIntroductoryText().add(intro);

        return manual;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }
}
