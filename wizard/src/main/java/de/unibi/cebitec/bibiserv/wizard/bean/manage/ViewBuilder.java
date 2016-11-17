package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import de.unibi.techfak.bibiserv.cms.TviewType;

/**
 * Class is used to build TrunnableItemView from raw data.
 * TODO: ask anew fro manual integration
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ViewBuilder {

    private static final String ID_BASE_TYPE = "view";

    public static TrunnableItemView createView(String title,
            String customContent, TviewType viewType, String langcode) {

        TrunnableItemView view = new TrunnableItemView();
        view.setType(viewType);

        view.setId(IDGenerator.createTemporaryID(title, ID_BASE_TYPE));

        TrunnableItemView.Title newtitle = new TrunnableItemView.Title();
        newtitle.setLang(langcode);
        newtitle.setValue(title);
        view.getTitle().add(newtitle);

        if (!customContent.isEmpty()) {
            TrunnableItemView.CustomContent newcontent =
                    new TrunnableItemView.CustomContent();
            newcontent.setLang(langcode);
            newcontent.getContent().add(customContent);
            view.getCustomContent().add(newcontent);
        }
        return view;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }
}
