package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.ImageFile;
import java.io.File;

/**
 * Class is used to build an ImageFileObject from raw data.
 *
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 */
public class ImageFileBuilder {

    public static ImageFile createFile(String filename, File file) {
        ImageFile newfile = new ImageFile(filename, file);
        return newfile;
    }
}
