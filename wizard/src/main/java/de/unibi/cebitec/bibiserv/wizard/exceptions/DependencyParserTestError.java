package de.unibi.cebitec.bibiserv.wizard.exceptions;

import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;

/**
 * Warning, this is not a real Error/Exception.
 * Objects of this type are collected in a List by ParameterDependencyTester.
 * @author Thomas Gatter <tgatter@cebitec.uni-bielefeld.de>
 */
public class DependencyParserTestError {

    private DependencyParserTestErrortypes errorType;
    private int line;
    private int column;
    private String errorValue;

    public DependencyParserTestError(DependencyParserTestErrortypes errorType,
            int line, int column, String errorValue) {
        this.errorType = errorType;
        this.line = line;
        this.column = column;
        this.errorValue = errorValue;
    }

    public DependencyParserTestErrortypes getErrorType() {
        return errorType;
    }

    public void setErrorType(DependencyParserTestErrortypes errorType) {
        this.errorType = errorType;
    }

    public String getErrorValue() {
        return errorValue;
    }

    public void setErrorValue(String errorValue) {
        this.errorValue = errorValue;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String createPrintableMessage() {

        StringBuilder message = new StringBuilder();
        message.append(PropertyManager.getProperty(errorType.getProperty()));

        switch (errorType) {
            case dependencyExtractionError:
            case marshallerror:
            case noFunctionId:
            case noParameterWrapper:
            case noRunnableItem:
            case notSupportedOrImplemented:
            case setParameter:
            case setTooldescriptionException:
            case stringToTypeCastFailed:
            case unknown:
            case unsolveableDependency:
            case unsupportedCompare:
                message.append(" ").append(errorValue);
                break;
            case unknownConstantValue:
            case noTypeChildParameter:
            case noParameterWidthId:
            case nameresolve:
                message.append(" ").append(errorValue);
                message.append(" ").append(PropertyManager.getProperty("depLine"));
                message.append(": ").append(line);
                message.append(" ").append(PropertyManager.getProperty("depColumn"));
                message.append(": ").append(column);
                break;
            case lexicalError:
            case onToken:
                message.append(" ").append(errorValue);
                message.append(" ").append(PropertyManager.getProperty("depLine"));
                message.append(": ").append(line);
                break;
        }
        return message.toString();
    }
}
