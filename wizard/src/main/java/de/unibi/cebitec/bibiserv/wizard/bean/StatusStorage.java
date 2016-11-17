
package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.bean.manage.FileManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FileManager.FileTupel;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionManager;
import de.unibi.cebitec.bibiserv.wizard.bean.manage.FunctionManager.FunctionTupel;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.Texecutable;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Tmanual;
import de.unibi.techfak.bibiserv.cms.ToutputFile;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import de.unibi.techfak.bibiserv.cms.Tperson;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import de.unibi.techfak.bibiserv.cms.Twebstart;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class is used to save a info of the current status into one object in order to serialize it.
 * @author T. Gatter -  tgatter(at)cebitec.uni-bielefeld.de
 */
public class StatusStorage {
    
    TrunnableItem runnableItem;
    Texecutable executeable;
    
    SortedMap<String, Tperson> authorList;
    List<String> selectedAuthors;
    
    SortedMap<String, TinputOutput> inputList;
    
    SortedMap<String, Tupel<String, ToutputFile>> outputFileList;
    
    SortedMap<String, Tdependency> dependencyList;
    
    SortedMap<String, TinputOutput> outputList;
    
    SortedMap<String, Object> parameterList;
    SortedMap<String, Tupel<String, TparamGroup>> paramGroupList;
    
    SortedMap<String, FunctionManager.FunctionTupel> functionList;
    List<String> selectedFunctions;
 
    List<String> selectedFiles;
    SortedMap<String, FileManager.FileTupel> files;
    
    List<String> selectedImageFiles;
    TreeMap<String, ImageFile> imageFiles;
    
    List<String> selectedReferences;
    SortedMap<String, BiBiPublication> references;
    
    Tmanual Manual;
    
    SortedMap<String, TrunnableItemView> viewList;
    
    List<String> selectedWebstarts;
    SortedMap<String, Twebstart> webstarts;

    public TrunnableItem getRunnableItem() {
        return runnableItem;
    }

    public void setRunnableItem(TrunnableItem runnableItem) {
        this.runnableItem = runnableItem;
    }

    public Texecutable getExecuteable() {
        return executeable;
    }

    public void setExecuteable(Texecutable executeable) {
        this.executeable = executeable;
    }

    public SortedMap<String, Tperson> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(SortedMap<String, Tperson> authorList) {
        this.authorList = authorList;
    }

    public List<String> getSelectedAuthors() {
        return selectedAuthors;
    }

    public void setSelectedAuthors(List<String> selectedAuthors) {
        this.selectedAuthors = selectedAuthors;
    }

    public SortedMap<String, TinputOutput> getInputList() {
        return inputList;
    }

    public void setInputList(SortedMap<String, TinputOutput> inputList) {
        this.inputList = inputList;
    }

    public SortedMap<String, Tupel<String, ToutputFile>> getOutputFileList() {
        return outputFileList;
    }

    public void setOutputFileList(SortedMap<String, Tupel<String, ToutputFile>> outputFileList) {
        this.outputFileList = outputFileList;
    }

    public SortedMap<String, Tdependency> getDependencyList() {
        return dependencyList;
    }

    public void setDependencyList(SortedMap<String, Tdependency> dependencyList) {
        this.dependencyList = dependencyList;
    }

    public SortedMap<String, TinputOutput> getOutputList() {
        return outputList;
    }

    public void setOutputList(SortedMap<String, TinputOutput> outputList) {
        this.outputList = outputList;
    }

    public SortedMap<String, Object> getParameterList() {
        return parameterList;
    }

    public void setParameterList(SortedMap<String, Object> parameterList) {
        this.parameterList = parameterList;
    }

    public SortedMap<String, Tupel<String, TparamGroup>> getParamGroupList() {
        return paramGroupList;
    }

    public void setParamGroupList(SortedMap<String, Tupel<String, TparamGroup>> paramGroupList) {
        this.paramGroupList = paramGroupList;
    }

    public SortedMap<String, FunctionTupel> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(SortedMap<String, FunctionTupel> functionList) {
        this.functionList = functionList;
    }

    public List<String> getSelectedFunctions() {
        return selectedFunctions;
    }

    public void setSelectedFunctions(List<String> selectedFunctions) {
        this.selectedFunctions = selectedFunctions;
    }

    public List<String> getSelectedFiles() {
        return selectedFiles;
    }

    public void setSelectedFiles(List<String> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    public SortedMap<String, FileTupel> getFiles() {
        return files;
    }

    public void setFiles(SortedMap<String, FileTupel> files) {
        this.files = files;
    }

    public List<String> getSelectedImageFiles() {
        return selectedImageFiles;
    }

    public void setSelectedImageFiles(List<String> selectedImageFiles) {
        this.selectedImageFiles = selectedImageFiles;
    }

    public TreeMap<String, ImageFile> getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(TreeMap<String, ImageFile> imageFiles) {
        this.imageFiles = imageFiles;
    }

    public List<String> getSelectedReferences() {
        return selectedReferences;
    }

    public void setSelectedReferences(List<String> selectedReferences) {
        this.selectedReferences = selectedReferences;
    }

    public SortedMap<String, BiBiPublication> getReferences() {
        return references;
    }

    public void setReferences(SortedMap<String, BiBiPublication> references) {
        this.references = references;
    }

    public Tmanual getManual() {
        return Manual;
    }

    public void setManual(Tmanual Manual) {
        this.Manual = Manual;
    }

    public SortedMap<String, TrunnableItemView> getViewList() {
        return viewList;
    }

    public void setViewList(SortedMap<String, TrunnableItemView> viewList) {
        this.viewList = viewList;
    }

    public List<String> getSelectedWebstarts() {
        return selectedWebstarts;
    }

    public void setSelectedWebstarts(List<String> selectedWebstarts) {
        this.selectedWebstarts = selectedWebstarts;
    }

    public SortedMap<String, Twebstart> getWebstarts() {
        return webstarts;
    }

    public void setWebstarts(SortedMap<String, Twebstart> webstarts) {
        this.webstarts = webstarts;
    }
      
}
