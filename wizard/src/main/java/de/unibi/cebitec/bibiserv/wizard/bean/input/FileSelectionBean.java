package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.FileStates;
import de.unibi.cebitec.bibiserv.wizard.bean.enums.TrafficLightEnum;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FileManager;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class FileSelectionBean {

    /**
     * Manager of the list of all current files.
     */
    private FileManager fileManager;
    /**
     * EditFileBean object
     */
    private EditFileBean fileBean;
    /**
     * ImageFileSelectionBean object
     */
    private ImageFileSelectionBean imageFileSelectionBean;
    /**
     * Current edit list.
     */
    private List<String> selectedFiles;
    /**
     * Last saved list.
     */
    private List<String> savedSelectedFiles;
    /**
     * for communication to xhtml
     */
    private DataModel<Tupel<Integer, String>> tableModel;
    /**
     * Tell if tableModel needs to be calculated anew.
     */
    private boolean dataModelEdited;
    /**
     * Full list of all files
     */
    private List<String> fileNamesList;
    /**
     * List of all files with status
     */
    private List<Tupel<String, FileStates>> fileList;
    
    private boolean renderUnsavedChanges;

    public FileSelectionBean() {

        // retrieve current bean of xmlViewer
        FacesContext context = FacesContext.getCurrentInstance();

        fileManager = (FileManager) context.getApplication().
                evaluateExpressionGet(context, "#{fileManager}",
                FileManager.class);
        fileBean = (EditFileBean) context.getApplication().
                evaluateExpressionGet(context, "#{editFileBean}",
                EditFileBean.class);
        imageFileSelectionBean = (ImageFileSelectionBean) context.getApplication().
                evaluateExpressionGet(context, "#{imageFileSelectionBean}",
                ImageFileSelectionBean.class);

        // init the selected files with one element
        selectedFiles = new ArrayList<String>();
        selectedFiles.add("");

        savedSelectedFiles = new ArrayList<String>();

        dataModelEdited = true;
        renderUnsavedChanges = false;
    }

    private void getLists() {
        fileNamesList = fileManager.getAllNames();
        fileList = fileManager.getAllNamesTupel();
    }

    /**
     * Retuns a list of Tupels containing all selected strings
     * with position in list.
     *
     * @return as above
     */
    public DataModel<Tupel<Integer, String>> getSelectedFilesWithId() {

        if (dataModelEdited) {
            List<Tupel<Integer, String>> ret =
                    new ArrayList<Tupel<Integer, String>>();

            int i = 0;
            for (String str : selectedFiles) {
                ret.add(new Tupel(i, str));
                i++;
            }

            tableModel = new ListDataModel<Tupel<Integer, String>>(ret);
        }
        dataModelEdited = false;
        return tableModel;
    }

    /**
     * Returns whether the remove symbol is shown behind dropdown.
     *
     * @return true: draw X; false: don't draw
     */
    public boolean isShowRemove() {
        return selectedFiles.size() > 1;
    }

    public boolean isFilesEmpty() {
        return fileManager.isEmpty();
    }

    public void dropDownValueChangeMethod(ValueChangeEvent e) {
        String value = "";
        if (e.getNewValue() != null) {
            value = (String) e.getNewValue();
        } else if (e.getOldValue()==null || ((String) e.getOldValue()).isEmpty()) {
            return;
        }
        Tupel<Integer, String> currentTupel = tableModel.getRowData();

        selectedFiles.set(currentTupel.getFirst(), value);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public void removeDropdown(int index) {
        selectedFiles.remove(index);
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public void addDropdown(int index) {
        selectedFiles.add(index + 1, "");
        dataModelEdited = true;
        renderUnsavedChanges = true;
    }

    public String newFile() {
        fileBean.newFile();
        return "editFiles.xhtml?faces-redirect=true";
    }

    public String editFile(String name) {
        fileBean.editFile(name);
        return "editFiles.xhtml?faces-redirect=true";
    }

    public void removeFile(String name) {
        fileBean.removeFile(name);
        getLists();
    }

    private void saveAll() {

        savedSelectedFiles.clear();
        Set<String> savedSet = new HashSet<String>();

        for (String str : selectedFiles) {
            if (!str.isEmpty()) {
                if(!savedSet.contains(str)) {
                    savedSelectedFiles.add(str);
                }
                savedSet.add(str);
            }
        }
        
        selectedFiles.clear();
        selectedFiles.addAll(savedSelectedFiles);
        if (selectedFiles.isEmpty()) {
            selectedFiles.add("");
        }
        
        dataModelEdited = true;
        renderUnsavedChanges = false;
    }

    /**
     * Save all none-empty selected authors to saved authors.
     */
    public void save() {
        // First call the save method of the ImageFileSelectionBean.
        imageFileSelectionBean.save();
        // Do own save actions.
        RequestContext context = RequestContext.getCurrentInstance();

        saveAll();

        if (savedSelectedFiles.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    PropertyManager.getProperty("noSelectedFileError"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", true);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    PropertyManager.getProperty("saveSuccesful"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", false);
        }
        
    }

    public void saveReturn() {

        // First call the save method of the ImageFileSelectionBean.
        imageFileSelectionBean.saveReturn();
        // Do own save actions.
        RequestContext context = RequestContext.getCurrentInstance();

        saveAll();

        // redirect from javax context
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, "/overview.xhtml"));
        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }
    }

    public String cancel() {

        // First call the cancel method of the ImageFileSelectionBean.
        imageFileSelectionBean.cancel();
        // Do own cancel actions.
        selectedFiles.clear();
        selectedFiles.addAll(savedSelectedFiles);
        if (selectedFiles.isEmpty()) {
            selectedFiles.add("");
        }
        dataModelEdited = true;
        renderUnsavedChanges = false;
        return "overview.xhtml?faces-redirect=true";
    }

    public String returnToPrev() {
        return "overview.xhtml?faces-redirect=true";
    }

    public void preRender() {
        getLists();
        imageFileSelectionBean.preRender();
    }

    public void reload() {
        getLists();
        List<String> newSaved = new ArrayList<String>();
        for (String str : savedSelectedFiles) {
            if (fileNamesList.contains(str)) {
                newSaved.add(str);
            }
        }
        savedSelectedFiles = newSaved;
        renderUnsavedChanges = false;
    }

    public String getFileStatus() {
        if (savedSelectedFiles.isEmpty()) {
            return TrafficLightEnum.YELLOW.getPath();
        }
        for (String savedfile : savedSelectedFiles) {
            try {
                if (fileManager.getFileByName(savedfile).getSecond() == FileStates.correctNoFile) {
                    return TrafficLightEnum.YELLOW.getPath();
                }
            } catch (BeansException ex) {
            }
        }
        return TrafficLightEnum.GREEN.getPath();
    }

    public void changeFileName(String oldname, String newname) {
        // change name in saved
        ListIterator<String> savedIterator = savedSelectedFiles.listIterator();
        while (savedIterator.hasNext()) {
            String str = savedIterator.next();
            if (str.equals(oldname)) {
                savedIterator.remove();
                savedIterator.add(newname);
            }
        }

        // change name in saved
        ListIterator<String> selectedIterator = selectedFiles.listIterator();
        while (selectedIterator.hasNext()) {
            String str = selectedIterator.next();
            if (str.equals(oldname)) {
                selectedIterator.remove();
                selectedIterator.add(newname);
            }
        }
        dataModelEdited = true;

    }

    public List<Tupel<String, FileStates>> getFileList() {
        return fileList;
    }

    public void setFileList(List<Tupel<String, FileStates>> fileList) {
        this.fileList = fileList;
    }

    public List<String> getFileNamesList() {
        return fileNamesList;
    }

    public void setFileNamesList(List<String> fileNamesList) {
        this.fileNamesList = fileNamesList;
    }

    public List<String> getSavedSelectedFiles() {
        return savedSelectedFiles;
    }

    public void addAllSavedSelectedFiles(List<String> savedSelectedFiles) {
        this.savedSelectedFiles.clear();
        this.savedSelectedFiles.addAll(savedSelectedFiles);
    }

    public boolean isRenderUnsavedChanges() {
        return renderUnsavedChanges;
    }
    
}
