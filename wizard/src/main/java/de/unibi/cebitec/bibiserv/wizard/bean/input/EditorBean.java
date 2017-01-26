package de.unibi.cebitec.bibiserv.wizard.bean.input;

import de.unibi.cebitec.bibiserv.wizard.bean.GeneralCallback;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import de.unibi.cebitec.bibiserv.wizard.tools.MicroHTMLPostProcessor;
import de.unibi.cebitec.bibiserv.wizard.tools.XSLTProcessor;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.transform.TransformerException;
import org.primefaces.context.RequestContext;
import org.w3c.tidy.Tidy;

/**
 * The bean for editor.xhtml.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de, Benjamin
 * Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class EditorBean {

    private String input;
    private String returnTo;
    private GeneralCallback<String> callback;
    private boolean parsingErrorsOccured = false;
    private String position;

    public EditorBean() {

        System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
    }

    /*
     * ########################################
     * # Communication with editor.xhtml-page #
     * ########################################
     */
    public void saveAndReturn() {

        String parsedInput = rawHTMLtoMicroHTML(input);

        callback.setResult(parsedInput);
        setInput(microHTMLtoHTML(parsedInput));

        RequestContext context = RequestContext.getCurrentInstance();
        if (parsingErrorsOccured) {
            parsingErrorsOccured = false;

            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", true);
            return;
        }
        context.addCallbackParam("show", false);
        context.addCallbackParam("returns", true);

        // redirect from javax context
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();
        String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, "/" + returnTo));
        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            // ignore
        }

    }

    public void save() {

        String parsedInput = rawHTMLtoMicroHTML(input);

        callback.setResult(parsedInput);
        setInput(microHTMLtoHTML(parsedInput));

        RequestContext context = RequestContext.getCurrentInstance();
        if (parsingErrorsOccured) {
            parsingErrorsOccured = false;

            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", true);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                    PropertyManager.getProperty("setSuccesful"), ""));
            context.addCallbackParam("show", true);
            context.addCallbackParam("returns", false);
            context.addCallbackParam("error", false);
        }

    }

    public String returnToPrev() {

        return returnTo + "?faces-redirect=true";
    }

    public String getInput() {
        return input;
    }

    public String getPosition() {
        return position;
    }

    /**
     * DO NOT USE THIS METHOD FROM OTHER CLASSES!
     *
     * Please use setEditorContentWithMicroHTML instead from outside beans.
     */
    public void setInput(String input) {
        this.input = input;
    }

    public String getReturnTo() {
        return returnTo;
    }
    

    /*
     * ###############################
     * # Interface for other classes #
     * ###############################
     */
    /**
     * Sets the content of the editor.
     *
     * @param microHTMLInput microHTML-input that gets converted to HTML and is
     * shown in the editor.
     */
    public void setEditorContent(String microHTMLInput) {
        this.input = microHTMLtoHTML(microHTMLInput);
    }

    public void setReturnTo(String returnTo) {
        this.returnTo = returnTo;
    }

    public GeneralCallback<String> getCallback() {
        return callback;
    }

    public void setCallback(GeneralCallback<String> callback) {
        this.callback = callback;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    

    /*
     * ###################
     * # Other functions #
     * ###################
     */
    private static final String MICROHTMLTOHTMLXSLTFILE = "parseMicroHTMLtoHTML.xsl";
    private static final String HTMLTOMICROHTMLXSLTFILE = "parseHTMLtoMicroHTML.xsl";

    /**
     * This parses a String containing raw html and returns a microhtml version.
     * Possible errors while parsing are forwarded to primefaces.
     *
     * The parsing is done by an xslt-script contained in the
     * properties-package.
     *
     * @param html raw html-data as String.
     * @return microhtml as String.
     */
    public String rawHTMLtoMicroHTML(String html) {

        // The html-data is wrapped in html-tags to create a well-formed root-element.

        String tmphtml = "<html>\n";
        tmphtml += html;  
        tmphtml += "\n</html>";
        
        StringWriter sw = new StringWriter();
        
       Tidy tidy = new Tidy();
       tidy.setXHTML(true);
       tidy.parse(new StringReader(tmphtml),sw);
       
       String tiddyhtml = sw.toString();
       
       
        
        String microhtml = null;
        try {
            microhtml = XSLTProcessor.doXSLTConversion(tiddyhtml, HTMLTOMICROHTMLXSLTFILE);
        } catch (TransformerException e) {
            if (FacesContext.getCurrentInstance() != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        PropertyManager.getProperty("microHTMLParsingError")
                        + " Message was: " + e.getMessage(), ""));
            } else {
                System.err.println("An error occured during xslt-parsing."
                        + "Message was: " + e.getMessageAndLocation());
            }
        }

        if (microhtml != null) {
            parsingErrorsOccured = MicroHTMLPostProcessor.parseConversionErrorComments(microhtml);
            microhtml = MicroHTMLPostProcessor.postProcessing(microhtml);
            return microhtml;
        } else {
            return "";
        }
    }

    /**
     * This takes valid microhtml-data and turns it into raw html as it can be
     * shown by the built-in text-editor.
     *
     * @param microhtml microhtml data as String
     * @return html data as String.
     */
    public String microHTMLtoHTML(String microhtml) {

        if (microhtml != null && !microhtml.isEmpty()) {
            String html = null;
            try {
                html = XSLTProcessor.doXSLTConversion(microhtml, MICROHTMLTOHTMLXSLTFILE);
            } catch (TransformerException e) {
                if (FacesContext.getCurrentInstance() != null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty("HTMLParsingError")
                            + " Message was: " + e.getMessage(), ""));
                } else {
                    System.err.println("An error occured during xslt-parsing."
                            + "Message was: " + e.getMessageAndLocation());
                }
            }
            if (html != null) {
                //Final manual parsing for finetuning-purposes.

                html = html.replaceAll("<\\?.*xml.*?\\?>", "");
                html = html.replaceAll("</?html>", "");
                return html;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
}