
package de.unibi.cebitec.bibiserv.wizard.exceptions;

import java.util.List;

/**
 * This exception is throw if one or more namereferences could not be resolved to an id.
 * @author Gatter
 */
public class DependencyResolveNameException extends Exception{

    List<DependencyParserTestError> unresolvedAreas;

    public DependencyResolveNameException(List<DependencyParserTestError> unresolvedAreas) {
        super("");
        this.unresolvedAreas = unresolvedAreas;
    }

    public List<DependencyParserTestError> getUnresolvedAreas() {
        return unresolvedAreas;
    }

}
