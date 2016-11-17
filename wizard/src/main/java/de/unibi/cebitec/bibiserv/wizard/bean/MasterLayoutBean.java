
package de.unibi.cebitec.bibiserv.wizard.bean;

import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.TabChangeEvent;

/**
 * Contains all info for Masterlayout.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class MasterLayoutBean {

    int infoBoxOpen;
    int standardResizeWidth;
    int codegenResize;
    int exampleInputArea;
    int exampleInputArea2;
    boolean infoCollapsed;
    String infoButtonText;

    public MasterLayoutBean(){
        // set open
        infoBoxOpen = 1;
        standardResizeWidth = 450;
        codegenResize = 700;
        exampleInputArea = 600;
        exampleInputArea2 = 400;
        infoCollapsed = false;
        infoButtonText = PropertyManager.getProperty("close");
    }

    public int getInfoBoxOpen() {
        return infoBoxOpen;
    }

    public void setInfoBoxOpen(int infoBoxOpen) {
        this.infoBoxOpen = infoBoxOpen;
    }

    public int getStandardResizeWidth() {
        return standardResizeWidth;
    }

    public int getCodegenResize() {
        return codegenResize;
    }

    public int getExampleInputArea() {
        return exampleInputArea;
    }

    public int getExampleInputArea2() {
        return exampleInputArea2;
    }

    public boolean isInfoCollapsed() {
        return infoCollapsed;
    }


    public void toggleInfoCollapsed(){
        if(infoCollapsed){
            infoCollapsed = false;
            infoButtonText = PropertyManager.getProperty("close");
        } else {
            infoCollapsed = true;
            infoButtonText = PropertyManager.getProperty("open");
        }
    }

    public String getInfoButtonText() {
        return infoButtonText;
    }




}
