package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.ImageFile;
import de.unibi.cebitec.bibiserv.wizard.bean.ManagerInterface;
import de.unibi.cebitec.bibiserv.wizard.bean.input.ImageFileSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * This manages all current image files
 *
 * @author Benjamin Paassen - bpaassen(at)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ImageFileManager implements ManagerInterface<ImageFile>{

    /**
     * Contains all image files sorted in alphabetical order by name.
     */
    private TreeMap<String, ImageFile> files;
    private ImageFileSelectionBean imageFileSelectionBean = null;

    public ImageFileManager() {
        // init image files map
        files = new TreeMap<String, ImageFile>(String.CASE_INSENSITIVE_ORDER);
    }

    public void clearFiles() {
        files = new TreeMap<String, ImageFile>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new file to the list of available image files.
     *
     * @param file file to add
     * @throws BeansException on error
     */
    public void addImageFile(ImageFile file) throws
            BeansException {

        if (file.getName().isEmpty()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = file.getName();

        if (files.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        files.put(name, file);
    }

    /**
     * Edits the file
     *
     * @param file with new filevalues
     * @param oldname the files old name
     * @throws BeansException on error
     */
    public void editImageFile(ImageFile file, String oldname) throws BeansException {

        if (file.getName().isEmpty()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = file.getName();

        // cannot change name, because new one ist set by other object
        if (!oldname.equals(name) && files.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (files.containsKey(oldname)) {

            files.remove(oldname);
            files.put(name, file);

            if (imageFileSelectionBean == null) {
                // get reference to image file bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                imageFileSelectionBean = (ImageFileSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{imageFileSelectionBean}",
                        ImageFileSelectionBean.class);
            }
            imageFileSelectionBean.changeInstanceName(oldname, name);

        } else {
            // should not occur when used correctly
            throw new BeansException(BeansExceptionTypes.NotFound);
        }
    }

    /**
     * Removes the file with name from the map.
     *
     * @param name name of the file to delete
     * @throws BeansException on error
     */
    public void removeFileByName(String name) throws BeansException {

        if (files.containsKey(name)) {

            // erase image file
            files.remove(name);

            if (imageFileSelectionBean == null) {
                // get reference to ImageFileSelectionBean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                imageFileSelectionBean = (ImageFileSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{imageFileSelectionBean}",
                        ImageFileSelectionBean.class);
            }
            imageFileSelectionBean.reload();

        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }

    }

    /**
     * Returns the file with the given name.
     *
     * @param name of the image file to retrieve
     * @return file
     * @throws BeansException on error
     */
    public ImageFile getFileByName(String name) throws
            BeansException {

        if (files.containsKey(name)) {

            return files.get(name);
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns an alphabetically sorted list of all files.
     *
     * @return an alphabetically sorted list of all files.
     */
    @Override
    public List<ImageFile> getAllInfoObjects() {

        List<ImageFile> ret = new ArrayList<ImageFile>();

        for (Map.Entry<String, ImageFile> entry : files.entrySet()) {
            ret.add(entry.getValue());
        }

        return ret;
    }

    /**
     * Returns an alphabetically sorted list of all filenames.
     *
     * @return an alphabetically sorted list of all filenames.
     */
    @Override
    public List<String> getAllNames() {

        List<String> ret =
                new ArrayList<String>();

        for (Map.Entry<String, ImageFile> entry : files.entrySet()) {
            String name = entry.getValue().getName();
            ret.add(name);
        }

        return ret;
    }

    @Override
    public boolean isEmpty() {
        return files.isEmpty();
    }

    public TreeMap<String, ImageFile> getFiles() {
        return files;
    }

    public void setFiles(TreeMap<String, ImageFile> files) {
        this.files = files;
    }
    
}
