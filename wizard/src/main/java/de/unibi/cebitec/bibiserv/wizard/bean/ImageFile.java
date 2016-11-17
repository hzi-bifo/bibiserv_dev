package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.FileStates;
import java.io.File;

/**
 * This is a datastructure containing all necessary data for an image file.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class ImageFile {

    private String name = "";
    private File actualFile;
    private FileStates currentState = FileStates.correctNoFile;

    public ImageFile(String name, File actualFile) {
        this.name = name;
        this.actualFile = actualFile;
        this.currentState = FileStates.correctFile;
    }

    public File getFile() {
        return actualFile;
    }

    public FileStates getCurrentState() {
        return currentState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFile(File actualFile) {
        if (actualFile == null || !actualFile.exists()) {
            unsetFile();
        } else {
            this.actualFile = actualFile;
            this.currentState = FileStates.correctFile;
        }
    }

    public void unsetFile() {
        this.actualFile = null;
        this.currentState = FileStates.correctNoFile;
    }
}
