package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.bean.DescriptionBean;
import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.FileStates;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FileBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FileManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.cebitec.bibiserv.wizard.tools.FileUploadIDGenerator;
import de.unibi.cebitec.bibiserv.wizard.tools.OutputFileSystemManager;
import de.unibi.techfak.bibiserv.cms.Tfile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;

/**
 * The contains everything needed to edit files
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class EditFileBean extends DescriptionBean {

    // general data
    private String file;
    private String name;
    private String version;
    private String shortDescription;
    private String platform;
    private FileStates state;
    //change of files on server
    private boolean dirty;
    private String uploadedFile;
    private boolean removemarked;
    //loading
    private String loadedFrom;
    private boolean renderLoadedFrom;
    // user info
    private String yesno;
    // list data
    private boolean filesEmpty;
    private List<Tupel<String, FileStates>> fileList;
    // manager beans
    private FileManager fileManager;
    // used path
    private final String uploadedFilesBasePath = FileUploadIDGenerator.generateFileUploadID();
    
    private boolean renderUnsavedChanges;

    public EditFileBean() {
        // set xhtml to the one used in this bean as return value for other beans
        xhtml = "editFiles.xhtml";

        // retrieve current Manager beans
        FacesContext context = FacesContext.getCurrentInstance();
        fileManager = (FileManager) context.getApplication().
                evaluateExpressionGet(context, "#{fileManager}",
                FileManager.class);

        position = PropertyManager.getProperty("file");
   
        resetAll();
    }

    private void getAvailableData() {
        fileList = fileManager.getAllNamesTupel();
        filesEmpty = fileManager.isEmpty();
    }

    private void resetAll() {
        getAvailableData();
        loadedFrom = "";
        renderLoadedFrom = false;
        file = "";
        name = "";
        version = "";
        shortDescription = "";
        platform = "";
        description = "";
        dirty = false;
        removemarked = false;
        uploadedFile = "";
        state = FileStates.correctNoFile;
        yesno = PropertyManager.getProperty("no");
        renderUnsavedChanges = false;
    }

    public void preRender() {
        getAvailableData();
    }

    /**
     * Removes all files, that are uploaded currently.
     */
    public void deleteAllFiles() {
        if (!fileList.isEmpty()) {
            for (Tupel<String, FileStates> currentFile : fileList) {
                switch (currentFile.getSecond()) {
                    case correctFile:
                        deleteFileFromServer(currentFile.getFirst());
                        break;
                }
            }
        }
    }

    /**
     * load in file with name
     *
     * @param name
     */
    private void loadFile(String name) {
        resetAll();

        Tfile loadedfile;
        try {
            Tupel<Tfile, FileStates> tupel = fileManager.getFileByName(name);
            loadedfile = tupel.getFirst();
            state = tupel.getSecond();
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openFileError"),
                    ""));
            return;
        }

        file = loadedfile.getFilename();
        loadedFrom = file;
        renderLoadedFrom = true;

        if (state == FileStates.correctFile) {
            yesno = PropertyManager.getProperty("yes");
        }

        if (loadedfile.isSetName()) {
            this.name = loadedfile.getName().get(0).getValue();
        }
        if (loadedfile.isSetShortDescription()) {
            shortDescription =
                    loadedfile.getShortDescription().get(0).getValue();
        }
        if (loadedfile.isSetPlatform()) {
            platform = loadedfile.getPlatform();
        }
        if (loadedfile.isSetDescription()) {
            description = (String) loadedfile.getDescription().get(0).
                    getContent().get(0);
        }
        if (loadedfile.isSetVersion()) {
            version = loadedfile.getVersion();
        }
    }

    public void editFile(String name) {
        if (dirty) {
            deleteFileFromServer(uploadedFile);
        }
        loadFile(name);
    }

    public void removeFile(String name) {
        try {
            fileManager.removeFileByName(name);
            deleteFileFromServer(name);
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
        } catch (BeansException ex) {
        }
        getAvailableData();
    }

    /**
     * Validate to user input and add errors to context
     *
     * @return true: file is valid; false: file is not valid
     */
    private boolean validateAll() {
        boolean ret = true;
        if (file.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("fileError"), ""));
            ret = false;
        }
        if (name.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("nameError"), ""));
            ret = false;
        }
        if (shortDescription.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("shortDescriptionError"), ""));
            ret = false;
        }

        return ret;
    }

    /**
     * Saves file if it validates
     *
     * @return validation status
     */
    private boolean saveAll() {
        if (validateAll()) {

            Tfile newfile = FileBuilder.createFile(name, version, file,
                    shortDescription, description, platform,
                    BasicBeanData.StandardLanguage);

            if (loadedFrom.isEmpty()) {
                try {
                    fileManager.addFile(newfile, state);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "fileAlreadyExistsError"),
                                ""));
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty("couldNotSave"), ""));
                    }
                    return false;
                }
                if (state == FileStates.correctFile) {
                    if (dirty) {
                        renameFileOnServer(uploadedFile, file);
                    }
                } else {
                    if (removemarked) {
                        deleteFileFromServer(uploadedFile);
                    }
                }

            } else {
                try {
                    fileManager.editFile(newfile, loadedFrom, state);
                } catch (BeansException ex) {
                    if (ex.getExceptionType()
                            == BeansExceptionTypes.AlreadyContainsName) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty(
                                "fileAlreadyExistsError"),
                                ""));
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                PropertyManager.getProperty("couldNotSave"), ""));
                    }
                    return false;
                }
                if (state == FileStates.correctFile) {
                    if (!uploadedFile.equals(file)) {
                        // if the user has changed the filename, rename the file.
                        if (!dirty) {
                            renameFileOnServer(loadedFrom, file);
                        } else {
                            deleteFileFromServer(loadedFrom);
                            renameFileOnServer(uploadedFile, file);
                        }
                    }
                } else {
                    if (removemarked) {
                        if (dirty) {
                            deleteFileFromServer(uploadedFile);
                        }
                        deleteFileFromServer(loadedFrom);
                    }
                }
            }
        } else {
            return false;
        }

        uploadedFile = "";
        dirty = false;
        removemarked = false;

        getAvailableData();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                PropertyManager.getProperty("saveSuccesful"), ""));

        renderUnsavedChanges = false;
        return true;
    }

    public void saveAndReturn() {

        if (saveAll()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ExternalContext extContext = ctx.getExternalContext();
            String url = extContext.encodeActionURL(ctx.getApplication().
                    getViewHandler().getActionURL(ctx, "/fileSelection.xhtml"));
            try {
                extContext.redirect(url);
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public void save() {
        if (saveAll()) {
            loadedFrom = file;
            renderLoadedFrom = true;
        }
    }

    public void newFile() {
        if (dirty) {
            deleteFileFromServer(uploadedFile);
        }
        resetAll();
    }

    public void deleteFileFromServer(String filename) {
        File fileToBeDeleted = new File(uploadedFilesBasePath + OutputFileSystemManager.DOWNLOADDIR + filename);
        fileToBeDeleted.delete();
    }

    public void renameFileOnServer(String oldfilename, String newfilename) {

        File fileToBeRenamed = new File(uploadedFilesBasePath + OutputFileSystemManager.DOWNLOADDIR
                + oldfilename);
        fileToBeRenamed.renameTo(new File(uploadedFilesBasePath + OutputFileSystemManager.DOWNLOADDIR
                + newfilename));
    }

    public String returnToPrev() {
        return "fileSelection.xhtml?faces-redirect=true";
    }

    public String cancel() {
        if (dirty) {
            deleteFileFromServer(uploadedFile);
            state = FileStates.correctNoFile;
            if (!loadedFrom.isEmpty()) {
                // if the file was there before, change back the state of the file.
                try {
                    Tfile oldFile = fileManager.getFileByName(loadedFrom).getFirst();
                    fileManager.editFile(oldFile, loadedFrom, state);
                } catch (BeansException e) {
                    // should not happen, because loadedFrom names are definitely valid.
                }
            }
        }
        return returnToPrev();
    }

    public void handleFileUpload(FileUploadEvent event) {
        renderUnsavedChanges = true;
        String filename = "";
        try {
            // Get the newly uploaded file and check, if it was uploaded already.
            filename = event.getFile().getFileName();

            if (dirty) {
                deleteFileFromServer(uploadedFile);
            }

            /*
             * create a file in the download temporary directory.
             */
            File uploadedFileOnServer = new File(uploadedFilesBasePath + OutputFileSystemManager.DOWNLOADDIR + filename);

            // Store the uploaded file in the newly created directory for the current session.

            FileOutputStream fileOutputStream = new FileOutputStream(
                    uploadedFileOnServer);
            BufferedOutputStream fileWritingStream = new BufferedOutputStream(
                    fileOutputStream);

            byte[] uploadedContent = event.getFile().getContents();

            fileWritingStream.write(uploadedContent);
            fileWritingStream.flush();
            fileWritingStream.close();

            dirty = true;
            file = filename;
            uploadedFile = filename;
            removemarked = false;
            yesno = PropertyManager.getProperty("yes");
            state = FileStates.correctFile;

            FacesMessage msg = new FacesMessage(filename + " "
                    + PropertyManager.getProperty("fileUploadSuccesfull"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            getAvailableData();

        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    filename + " " + PropertyManager.getProperty(
                    "fileUploadError") + " " + e.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void removeFileFromServerForCurrent() {
        removemarked = true;
        yesno = PropertyManager.getProperty("no");
        state = FileStates.correctNoFile;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFile() {
        return file;
    }

    public List<Tupel<String, FileStates>> getFileList() {
        return fileList;
    }

    public boolean isFilesEmpty() {
        return filesEmpty;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public void setLoadedFrom(String loadedFrom) {
        this.loadedFrom = loadedFrom;
    }

    public boolean isRenderLoadedFrom() {
        return renderLoadedFrom;
    }

    public void setRenderLoadedFrom(boolean renderLoadedFrom) {
        this.renderLoadedFrom = renderLoadedFrom;
    }

    public String getYesno() {
        return yesno;
    }

    public String getUploadedFilesBasePath() {
        return uploadedFilesBasePath;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public FileStates getState() {
        return state;
    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }
    
    @Override
     public void setDescription(String description) {
        renderUnsavedChanges = true;
        this.description = description;
    }  
}
