package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.ImageFile;
import de.unibi.cebitec.bibiserv.wizard.bean.InstanceBeanInterface;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.FileStates;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ImageFileBuilder;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.ImageFileManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.cebitec.bibiserv.wizard.tools.FileUploadIDGenerator;
import de.unibi.cebitec.bibiserv.wizard.tools.OutputFileSystemManager;
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
 * The contains everything needed to edit image files
 *
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ImageFileBean implements InstanceBeanInterface{

    // general data
    private String filename;
    private FileStates state;
    //change of files on server
    private boolean dirty;
    //actual uploaded file
    private String uploadedFileName;
    private boolean removemarked;
    //loading
    private String loadedFrom;
    private boolean renderLoadedFrom;
    // user info
    private String yesno;
    // list data
    private boolean filesEmpty;
    private List<ImageFile> fileList;
    // manager beans
    private ImageFileManager imageFileManager;
    // used path
    private final String uploadedImageFilesBasePath = FileUploadIDGenerator.generateFileUploadID();
    private boolean renderUnsavedChanges;
    
    

    public ImageFileBean() {

        // retrieve current Manager beans
        FacesContext context = FacesContext.getCurrentInstance();
        imageFileManager = (ImageFileManager) context.getApplication().
                evaluateExpressionGet(context, "#{imageFileManager}",
                ImageFileManager.class);

        resetAll();
    }

    private void getAvailableData() {
        fileList = imageFileManager.getAllInfoObjects();
        filesEmpty = imageFileManager.isEmpty();
    }

    private void resetAll() {
        getAvailableData();
        loadedFrom = "";
        renderLoadedFrom = false;
        filename = "";
        uploadedFileName = "";
        dirty = false;
        removemarked = false;
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
    public void deleteAllImageFiles() {
        if (!fileList.isEmpty()) {
            for (ImageFile currentImageFile : fileList) {
                switch (currentImageFile.getCurrentState()) {
                    case correctFile:
                        deleteFileFromServer(currentImageFile.getName());
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
    private void loadImageFile(String name) {
        resetAll();

        ImageFile loadedFile;
        try {
            loadedFile = imageFileManager.getFileByName(name);
            state = loadedFile.getCurrentState();
        } catch (BeansException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty(
                    "openImageFileError"),
                    ""));
            return;
        }

        filename = loadedFile.getName();
        loadedFrom = filename;
        renderLoadedFrom = true;

        if (state == FileStates.correctFile) {
            yesno = PropertyManager.getProperty("yes");
        }
    }

    public void editImageFile(String name) {
        cancel();
        loadImageFile(name);
    }

    public void removeImageFile(String name) {
        try {
            imageFileManager.removeFileByName(name);
            deleteFileFromServer(name);
            if (name.equals(loadedFrom)) {
                loadedFrom = "";
                renderLoadedFrom = false;
            }
        } catch (BeansException ex) {
        }
        getAvailableData();
        renderUnsavedChanges = true;
    }

    /**
     * Validate to user input and add errors to context
     *
     * @return true: file is valid; false: file is not valid
     */
    private boolean validateAll() {
        boolean ret = true;
        if (filename.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noImageFileNameError"), ""));
            ret = false;
        }
        if (state!=FileStates.correctFile) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noImageFileError"), ""));
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
            ImageFile newfile = ImageFileBuilder.createFile(filename,
                    new File(uploadedImageFilesBasePath + 
                            OutputFileSystemManager.IMGDIR + filename));

            if (loadedFrom.isEmpty()) {
                try {
                    imageFileManager.addImageFile(newfile);
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
                        renameFileOnServer(uploadedFileName, filename);
                    }
                } else {
                    if (removemarked) {
                        deleteFileFromServer(uploadedFileName);
                    }
                }

            } else {
                try {
                    imageFileManager.editImageFile(newfile, loadedFrom);
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
                    if (!uploadedFileName.equals(filename)) {
                        // if the user has changed the filename, rename the file.
                        if (!dirty) {
                            renameFileOnServer(uploadedFileName, filename);
                        } else {
                            deleteFileFromServer(loadedFrom);
                            renameFileOnServer(uploadedFileName, filename);
                        }
                    }
                } else {
                    if (removemarked) {
                        if (dirty) {
                            deleteFileFromServer(uploadedFileName);
                        }
                        deleteFileFromServer(loadedFrom);
                    }
                }
            }
        } else {
            return false;
        }

        uploadedFileName = "";
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
            loadedFrom = filename;
            renderLoadedFrom = true;
        }
    }

    public void newImageFile() {
        cancel();
        resetAll();
    }

    public void deleteFileFromServer(String filename) {
        File fileToBeDeleted = new File(uploadedImageFilesBasePath + 
                OutputFileSystemManager.IMGDIR + filename);
        fileToBeDeleted.delete();
    }

    public void renameFileOnServer(String oldFilename, String newfilename) {
        File fileToBeRenamed = new File(uploadedImageFilesBasePath + 
                OutputFileSystemManager.IMGDIR + oldFilename);
        fileToBeRenamed.renameTo(new File(uploadedImageFilesBasePath + 
                OutputFileSystemManager.IMGDIR + newfilename));
    }

    public String returnToPrev() {
        return "fileSelection.xhtml?faces-redirect=true";
    }

    public String cancel() {
        if (dirty) {
            deleteFileFromServer(uploadedFileName);
            state = FileStates.correctNoFile;
            if (!loadedFrom.isEmpty()) {
                // if the file was there before, remove the entry from the manager.
                try {
                    imageFileManager.removeFileByName(loadedFrom);
                } catch (BeansException e) {
                    // should not happen, because loadedFrom names are definitely valid.
                }
            }
        }
        return returnToPrev();
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            // Get the newly uploaded file and check, if it was uploaded already.
            uploadedFileName = event.getFile().getFileName();

            if (dirty) {
                deleteFileFromServer(uploadedFileName);
            }

            /*
             * check if the given file is an image file and create a file in the
             * respective temporary directory.
             */
            File uploadedFile;
            if (OutputFileSystemManager.isImageFile(uploadedFileName)) {
                uploadedFile = new File(uploadedImageFilesBasePath + 
                        OutputFileSystemManager.IMGDIR + uploadedFileName);
            } else {
                FacesMessage msg = new FacesMessage(uploadedFileName + " "
                        + PropertyManager.getProperty("notAnImageFileError"), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
            // Store the uploaded file in the newly created directory for the current session.

            FileOutputStream fileOutputStream = new FileOutputStream(uploadedFile);
            BufferedOutputStream fileWritingStream = new BufferedOutputStream(
                    fileOutputStream);

            byte[] uploadedContent = event.getFile().getContents();

            fileWritingStream.write(uploadedContent);
            fileWritingStream.flush();
            fileWritingStream.close();

            dirty = true;
            filename = uploadedFileName;
            removemarked = false;
            yesno = PropertyManager.getProperty("yes");
            state = FileStates.correctFile;

            FacesMessage msg = new FacesMessage(uploadedFileName + " "
                    + PropertyManager.getProperty("fileUploadSuccesfull"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            getAvailableData();
            
            renderUnsavedChanges = true;

        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    uploadedFileName + " " + PropertyManager.getProperty(
                    "fileUploadError") + " " + e.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void removeFileFromServerForCurrent() {
        removemarked = true;
        yesno = PropertyManager.getProperty("no");
        state = FileStates.correctNoFile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<ImageFile> getImageFileList() {
        return fileList;
    }

    public boolean isImageFilesEmpty() {
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

    public String getUploadedImageFilesBasePath() {
        return uploadedImageFilesBasePath;
    }

    public void setImageFile(String file) {
        this.filename = file;
    }

    public FileStates getState() {
        return state;
    }

    @Override
    public void newInstance() {
        newImageFile();
    }

    @Override
    public void editInstance(String name) {
        editImageFile(name);
    }

    @Override
    public void removeInstance(String name) {
        removeImageFile(name);
    }

    @Override
    public String getURL() {
        return "imageFile.xhtml?faces-redirect=true";
    }
    
    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
  
    public void unsavedChange(){
        renderUnsavedChanges = true;
    }
}
