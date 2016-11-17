package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.FileStates;
import de.unibi.cebitec.bibiserv.wizard.bean.input.FileSelectionBean;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansExceptionTypes;
import de.unibi.techfak.bibiserv.cms.Tfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * This manages all current files
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class FileManager {

    /**
     * Contains all files sorted in alphabetical order by name.
     */
    private SortedMap<String, FileTupel> files;
    private FileSelectionBean fileSelectionBean = null;

    public FileManager() {
        // init functions map
        files = new TreeMap<String, FileTupel>(String.CASE_INSENSITIVE_ORDER);
    }

    public void clearFiles() {
        files = new TreeMap<String, FileTupel>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Adds a new file to the list of available functions.
     *
     * @param file file to add
     * @param state has file or has no file
     * @param name This is put into the map with. 
     * @throws BeansException on error
     */
    public String addFile(Tfile file, FileStates state) throws
            BeansException {

        if (!file.isSetFilename()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = file.getFilename();

        if (files.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        files.put(name, new FileTupel(file, state));
        return name;
    }

    /**
     * Edits the file
     *
     * @param file with new filevalues
     * @param state has file or has no file
     * @throws BeansException on error
     */
    public void editFile(Tfile file, String oldname, FileStates state) throws
            BeansException {

        if (!file.isSetFilename()) {
            throw new BeansException(BeansExceptionTypes.NoNameSpecified);
        }

        String name = file.getFilename();

        // cannot change name, because new one ist set by other object
        if (!oldname.equals(name) && files.containsKey(name)) {
            throw new BeansException(BeansExceptionTypes.AlreadyContainsName,
                    name);
        }

        if (files.containsKey(oldname)) {

            files.remove(oldname);
            files.put(name, new FileTupel(file, state));

            if (fileSelectionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                fileSelectionBean = (FileSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{fileSelectionBean}",
                        FileSelectionBean.class);
            }
            fileSelectionBean.changeFileName(oldname, name);

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

            // erase function
            files.remove(name);

            if (fileSelectionBean == null) {
                // get reference to function bean to send refresh signals
                FacesContext context = FacesContext.getCurrentInstance();
                fileSelectionBean = (FileSelectionBean) context.getApplication().
                        evaluateExpressionGet(context,
                        "#{fileSelectionBean}",
                        FileSelectionBean.class);
            }
            fileSelectionBean.reload();

        } else {

            throw new BeansException(BeansExceptionTypes.NotFound);
        }

    }

    /**
     * Returns the file with the given name.
     *
     * @param name of the function to retrieve
     * @return file
     * @throws BeansException on error
     */
    public Tupel<Tfile, FileStates> getFileByName(String name) throws
            BeansException {

        if (files.containsKey(name)) {

            FileTupel tupel = files.get(name);
            return new Tupel<Tfile, FileStates>(tupel.getFile(),
                    tupel.getState());
        }

        throw new BeansException(BeansExceptionTypes.NotFound);
    }

    /**
     * Returns alphabetically sorted list of all filenames with state contained
     * in map.
     *
     * @return alphabetically sorted list of all filenames with state contained
     * in map
     */
    public List<Tupel<String, FileStates>> getAllNamesTupel() {

        List<Tupel<String, FileStates>> ret =
                new ArrayList<Tupel<String, FileStates>>();

        for (Map.Entry<String, FileTupel> entry : files.entrySet()) {
            String name = entry.getValue().getFile().getFilename();
            FileStates state = entry.getValue().getState();
            ret.add(new Tupel(name, state));
        }

        return ret;
    }

    /**
     * Returns alphabetically sorted list of all filenames contained in map.
     *
     * @return alphabetically sorted list of all filenames contained in map
     */
    public List<String> getAllNames() {

        List<String> ret = new ArrayList<String>();

        for (Map.Entry<String, FileTupel> entry : files.entrySet()) {
            String name = entry.getValue().getFile().getFilename();
            ret.add(name);
        }
        return ret;
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }

    public SortedMap<String, FileTupel> getFiles() {
        return files;
    }

    public void setFiles(SortedMap<String, FileTupel> files) {
        this.files = files;
    }
    
   
    /**
     * Inner Tupel class to enhance readability.
     */
    public static class FileTupel {

        private Tfile file;
        private FileStates state;

        public FileTupel(Tfile file, FileStates valid) {
            this.file = file;
            this.state = valid;
        }

        public Tfile getFile() {
            return file;
        }

        public void setFile(Tfile file) {
            this.file = file;
        }

        public FileStates getState() {
            return state;
        }

        public void setState(FileStates state) {
            this.state = state;
        }
    }
}
