
package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.techfak.bibiserv.cms.Tmanual;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Class used to save current manual Object.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class ManualManager {
    
    private Tmanual savedManual;
    
    public ManualManager(){
        savedManual = null;
    }

    public Tmanual getSavedManual() {
        return savedManual;
    }

    public void setSavedManual(Tmanual savedManual) {
        this.savedManual = savedManual;
    }
    
}
