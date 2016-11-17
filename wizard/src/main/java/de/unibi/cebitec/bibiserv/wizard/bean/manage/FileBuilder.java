package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Tfile;

/**
 * Class is used to build Tfile from raw data.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class FileBuilder {

    private static final String ID_BASE_TYPE = "file";
    
    public static Tfile createFile(String name, String version, String filename,
            String shortDesc, String desc, String platform, String langcode) {

        Tfile newfile = new Tfile();

        newfile.setFilename(filename);
        
        newfile.setId(IDGenerator.createTemporaryID(name, ID_BASE_TYPE));

        if (!name.isEmpty()) {
            Tfile.Name newname = new Tfile.Name();
            newname.setLang(langcode);
            newname.setValue(name);
            newfile.getName().add(newname);
        }
        if (!shortDesc.isEmpty()) {
            Tfile.ShortDescription fileShortDesc = new Tfile.ShortDescription();
            fileShortDesc.setLang(langcode);
            fileShortDesc.setValue(shortDesc);
            newfile.getShortDescription().add(fileShortDesc);
        }
        if (!desc.isEmpty()) {
            Tfile.Description fileDescription =
                    new Tfile.Description();
            fileDescription.setLang(langcode);
            fileDescription.getContent().add(desc);
            newfile.getDescription().add(fileDescription);
        }

        if (!platform.isEmpty()) {
            newfile.setPlatform(platform);
        }

        if (!platform.isEmpty()) {
            newfile.setVersion(version);
        }

        return newfile;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }
}
