
package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.ToutputFile;

/**
 * Class used to create additional output files.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class OutputFileBuilder {

    private static final String ID_BASE_TYPE = "additionaloutput";


    public static ToutputFile createOutputFile(String idbase, String name,
            String filename, String folder, String contenttype, String langcode) {

        
        
        ToutputFile output = new ToutputFile();
        ToutputFile.Name outputName = new ToutputFile.Name();
        outputName.setLang(langcode);
        outputName.setValue(name);
        output.getName().add(outputName);
        
        output.setFilename(filename);
        output.setContenttype(contenttype);
        if (!folder.isEmpty()) {
            output.setFolder(folder);
        }
        
        output.setId(IDGenerator.createTemporaryID(idbase,ID_BASE_TYPE));
        
        return output;

    }


    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }
}
