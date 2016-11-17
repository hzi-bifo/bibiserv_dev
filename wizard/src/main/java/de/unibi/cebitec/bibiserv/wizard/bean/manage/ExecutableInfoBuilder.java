package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.techfak.bibiserv.cms.ObjectFactory;
import de.unibi.techfak.bibiserv.cms.TexecPath;
import de.unibi.techfak.bibiserv.cms.Texecutable;
import de.unibi.techfak.bibiserv.cms.TexecutableTypes;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ExecutableInfoBuilder {
    
    private Texecutable executableItem;
    private static final String ID_BASE_TYPE = "executable_info";
    
    public ExecutableInfoBuilder() {
        //initialize the ExecutableItem using a bibiserv-objectFactory.
        ObjectFactory factory = new ObjectFactory();
        
        this.executableItem = factory.createTexecutable();
    }
    
    public void createTexecutable(String callingInfo, String version) {

        //Add empty path (attribute is not needed anymore but still specified by schema)
        //TODO: Remove this when not needed anymore.
        setPath("");
        setCallingInfo(callingInfo);
        setVersion(version);
    }
    
    public void setPath(String path) {
        TexecPath execpath;
        
        if (executableItem.getExecInfo() == null) {
            execpath = new TexecPath();
            executableItem.setExecInfo(execpath);
        } else {
            execpath = executableItem.getExecInfo();
        }
        execpath.setPath(path);
    }
    
    public void setCallingInfo(String callingInfo) {
        TexecPath execpath;
        
        if (executableItem.getExecInfo() == null) {
            execpath = new TexecPath();
            executableItem.setExecInfo(execpath);
        } else {
            execpath = executableItem.getExecInfo();
        }
        execpath.setCallingInformation(callingInfo);
    }
    
    public void setVersion(String version) {
        executableItem.setVersion(version);
    }
    
    
    public Texecutable getTexecutable() {
        return executableItem;
    }
    
    public void setExecutableItem(Texecutable executableItem) {
        this.executableItem = executableItem;
    }
    
    public String getCallingInfo() {
        if (executableItem.getExecInfo() == null
                || !executableItem.getExecInfo().isSetCallingInformation()) {
            return "";
        }
        return executableItem.getExecInfo().getCallingInformation();
    }
       
    public String getVersion() {
        if (!executableItem.isSetVersion()) {
            return "";
        }
        return executableItem.getVersion();
    }
}
