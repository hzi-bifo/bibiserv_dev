
package de.unibi.cebitec.bibiserv.wizard.bean.enums;

/**
 * This contains the string of error messages thrown by xslt,
 * mapping them to the name in msg for displaying.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public enum XSLTParsingErrors {

    listEntryContentError("ERROR! Content between list-entries is not allowed! Was removed.","listEntryContentError"),
    tableEntryContentError("ERROR! Content between table-entries is not allowed! Was removed.","tableEntryContentError"),
    uError("ERROR! u-tags are not allowed in microhtml. Was replaced by em.","uError"),
    bError("ERROR! b-tags are not allowed in microhtml. Was replaced by strong","bError"),
    iError("ERROR! i-tags are not allowed in microhtml. Was replaced by em.","iError"),
    brError("ERROR! br/-tags are not allowed in microhtml. Was replaced by paragraph.","brError"),
    unknownError("ERROR! At least one of your nodes could not be parsed. It was removed.","unknownError");

    private String error;
    private String message;

    private XSLTParsingErrors(String error, String message){
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
    
}
