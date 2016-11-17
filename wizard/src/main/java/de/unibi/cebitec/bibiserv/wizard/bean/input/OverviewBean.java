package de.unibi.cebitec.bibiserv.wizard.bean.input;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Contains all functions for overview.xhtml
 *
 * @author Benjamin Paassen - bpaassen(aet)cebitec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class OverviewBean {

    public OverviewBean() {
    }

    public String clearSession() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(false);
        if (httpSession != null) {
            httpSession.invalidate();
        }
        return "overview.xhtml?faces-redirect=true";
    }
}
