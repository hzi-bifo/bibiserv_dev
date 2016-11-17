package de.unibi.cebitec.bibiserv.wizard.exceptions;

/**
 *
 * @author Gatter
 */
public enum DependencyParserTestErrortypes {

    /**
     * While replacing the name references by the real ids a name
     * could not be matched to a parameter.
     */
    nameresolve("depNameresolve"),
    /**
     * Something went wrong while marshalling. Should not occur.
     */
    marshallerror("depMarshallerror"),
    /**
     * The parameter with the given id could not be found.
     */
    noParameterWidthId("depNoParameterWidthId"),
    /**
     * The Parameter has no type tag in xml. Should not occur.
     * Error can only be caused on bad xml-generation.
     */
    noTypeChildParameter("depNoTypeChildParameter"),
    /**
     * p.e. Datetime as Type for parameters is not supported yet.
     */
    notSupportedOrImplemented("depNotSupportedOrImplemented"),
    /**
     * A constant could not be matched to a known type.
     */
    unknownConstantValue("depUnknownConstantValue"),
    /**
     * The token behind the given one contains an error.
     */
    onToken("depOnToken"),
    /**
     * Errors without specific information.
     */
    unknown("depUnknown"),
    /**
     * The dependency string could not be extracted, should not happen.
     */
    dependencyExtractionError("depDependencyExtractionError"),
    /**
     * The id of the function to test was not set in dependencyparser.
     */
    noFunctionId("depNoFunctionId"),
    /**
     * The parameterWrapper was not set in dependencyparser.
     */
    noParameterWrapper("depNoParameterWrapper"),
    /**
     * The tooldescription was not set in dependencyparser.
     */
    noRunnableItem("depNoRunnableItem"),
    /**
     * Error while setting the route element in dependencyparser.
     */
    setParameter("depSetParameter"),
    /**
     * Error while setting the tooldescription in dependencyparser.
     */
    setTooldescriptionException("depSetTooldescriptionException"),
    /**
     * A string could not be casted to its value as parameter.
     */
    stringToTypeCastFailed("depStringToTypeCastFailed"),
    /**
     * An operation combination can't be solved.
     */
    unsolveableDependency("depUnsolveableDependency"),
    /**
     * Comparing of types that can't be compared.
     */
    unsupportedCompare("depUnsupportedCompare"),
    /**
     * An operation is not supported.
     */
    unsupportedOperation("depUnsupportedOperation"),
    /**
     * A lexical error was encountered.
     */
    lexicalError("depLexicalError");

    private String property;

    private DependencyParserTestErrortypes(String property){
        this.property = property;
    }

    public String getProperty() {
        return property;
    }


}
