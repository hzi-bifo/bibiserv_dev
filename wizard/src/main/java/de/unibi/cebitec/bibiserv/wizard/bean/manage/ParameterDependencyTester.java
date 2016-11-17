package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.bean.input.CreateXML;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.DependencyParserTestError;
import de.unibi.cebitec.bibiserv.wizard.exceptions.DependencyParserTestErrortypes;
import de.unibi.cebitec.bibiserv.wizard.exceptions.DependencyResolveNameException;
import de.unibi.cebitec.bibiserv.wizard.tools.CopyFactory;
import de.unibi.techfak.bibiserv.cms.ObjectFactory;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Texecutable;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import de.unibi.techfak.bibiserv.util.Pair;
import de.unibi.techfak.bibiserv.util.dependencyparser.DependencyException;
import de.unibi.techfak.bibiserv.util.dependencyparser.DependencyParser;
import de.unibi.techfak.bibiserv.util.dependencyparser.Id;
import de.unibi.techfak.bibiserv.util.dependencyparser.ParameterWrapper;
import de.unibi.techfak.bibiserv.util.dependencyparser.javacc.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import de.unibi.techfak.bibiserv.util.dependencyparser.Node;
import de.unibi.techfak.bibiserv.util.dependencyparser.ParseExceptionMessageEnum;
import de.unibi.techfak.bibiserv.util.dependencyparser.javacc.TokenMgrError;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.FacesContext;

/**
 *
 * @author Thomas Gatter <tgatter@cebitec.uni-bielefeld.de>
 */
public class ParameterDependencyTester {

    public ParameterDependencyTester() {
        FacesContext context = FacesContext.getCurrentInstance();
        parametermanager =
                (ParameterManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterManager}",
                ParameterManager.class);
    }
    private ParameterManager parametermanager = null;
    private List<DependencyParserTestError> exceptions;
    /**
     * The indexes of all examples that were not validated correctly with a collection of the missing parameters.
     */
    private List<Tupel<Integer, List<String>>> incorrectExamples;

    /**
     * Tests if the given dependency ist parseable for the given function.
     * Autotest all examples in function.
     * @param function name of the function to be tested
     * @param dependency to be testedt
     * @return true: validated correct, false: validation failed
     */
    public boolean testDependency(
            Tfunction function, Tdependency dependency) {

        return testDependency(function, dependency,
                new ArrayList<List<Tupel<String, String>>>());
    }

    /**
     * Tests if the given dependency ist parseable for the given function.
     * Return with element inf Errorlist: smth went wrong.
     * @param function name of the function to be tested
     * @param dependency to be tested
     * @param examples all examples to be testet (inner list=parametersof one example)
     * @return true: validated correct, false: validation failed
     */
    public boolean testDependency(
            Tfunction function, Tdependency dependency,
            List<List<Tupel<String, String>>> examples) {

        FacesContext facecontext = FacesContext.getCurrentInstance();
        ParameterManager parametermanager =
                (ParameterManager) facecontext.getApplication().
                evaluateExpressionGet(facecontext, "#{parameterManager}",
                ParameterManager.class);

        exceptions = new ArrayList<DependencyParserTestError>();
        incorrectExamples = new ArrayList<Tupel<Integer,List<String>>>();

        boolean ret = true;

        Tdependency dependency2 = new Tdependency();
        dependency2.setId(dependency.getId());

        String resolvedDependencyDefinition = "";
        try {
            resolvedDependencyDefinition = ParameterDependencyBuilder.
                    insertRealIdsOverUserInput("", dependency.
                    getDependencyDefinition());
        } catch (DependencyResolveNameException e) {
            exceptions.addAll(e.getUnresolvedAreas());
            return false;
        }

        dependency2.setDependencyDefinition(resolvedDependencyDefinition);

        TrunnableItem runnable = new TrunnableItem();
        Texecutable executeable = new Texecutable();

        // add all parameters and ParameterGroups to executeable
        if(function.getParamGroup()!=null){
            executeable.getParamGroup().add(function.getParamGroup());
            getAllParameters(function.getParamGroup(), executeable);
        }
        // add dependency and function
        executeable.getDependency().add(dependency2);

        Tfunction function2 = CopyFactory.copyFunction(function, "");
        Tfunction.Depref depref = new Tfunction.Depref();
        depref.setRef(dependency2);
        function2.getDepref().clear();
        function2.getDepref().add(depref);
        executeable.getFunction().add(function2);

        runnable.setExecutable(executeable);

        try {
            JAXBContext context = JAXBContext.newInstance(
                    "de.unibi.techfak.bibiserv.cms");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                    CreateXML.getBIBISERVSCHEMALOCATION());

            ObjectFactory factory = new ObjectFactory();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(factory.createRunnableItem(
                    runnable), stringWriter);

            InputStream stream =
                    new ByteArrayInputStream(stringWriter.toString().getBytes());

            try {
                DependencyParser dp = new DependencyParser();
                dp.setTooldescription(stream);
                ParameterWrapper pw = new ParameterWrapper();
                dp.setParameterWrapper(pw);

                dp.setFunctionId(function.getId());  // id of function within tooldescription to be evaluted

                // parse Dependency string and generate tree
                Node node = dp.generate();

                // loop through
                for (List<Tupel<String, String>> example : examples) {

                    boolean correct = true;
                    // add all parameters of this exmaple
                    List<Pair<String, String>> pl =
                            new ArrayList<Pair<String, String>>();
                    Map<String,String> idToNameMap = new HashMap<String,String>();
                    for (Tupel<String, String> parameter : example) {
                        String id = "";
                        try {
                            id = parametermanager.getIdForName(parameter.getFirst());
                            idToNameMap.put(id, parameter.getFirst());
                        } catch (BeansException ex) {
                           correct = false;
                        }
                        pl.add(new Pair(id, parameter.getSecond()));
                    }
                    // test Parameter set
                    pw.setParameter(pl);
                    if (!correct || !node.evaluate()) {
                        
                        List<String> wrongParamIds = new ArrayList<String>();
                        for(Id current:node.getMissingConstraints().keySet()){
                            wrongParamIds.add(idToNameMap.get(current.getId()));
                        }
                        incorrectExamples.add(new Tupel<Integer, List<String>>(
                                examples.indexOf(example),
                                wrongParamIds));
                    }
                }
            } catch (ParseException ex) {
                switch (ParseExceptionMessageEnum.getType(ex)) {
                    case noParameterWidthId:
                        String id = ParseExceptionMessageEnum.getValue(
                                ex.getMessage());
                        Tupel<String,Tupel<Integer, Integer>> info =
                                findIdPosition(dependency.
                                getDependencyDefinition(), id);
                        id = info.getFirst();
                        Tupel<Integer, Integer> position = info.getSecond();
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.noParameterWidthId,
                                position.getFirst(), position.getSecond(), id));
                        break;
                    case noTypeChildParameter:
                        id = ParseExceptionMessageEnum.getValue(
                                ex.getMessage());
                        info = findIdPosition(dependency.
                                getDependencyDefinition(), id);
                        id = info.getFirst();
                        position = info.getSecond();
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.noTypeChildParameter,
                                position.getFirst(), position.getSecond(), id));
                        break;
                    case notSupportedOrImplemented:
                        String type = ParseExceptionMessageEnum.getValue(
                                ex.getMessage());
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.notSupportedOrImplemented,
                                -1, -1, type));
                        break;
                    case unknownConstantValue:
                        String value = ParseExceptionMessageEnum.getValue(
                                ex.getMessage());
                        position = findPosition(dependency.
                                getDependencyDefinition(), value);
                        exceptions.add(
                                new DependencyParserTestError(
                                DependencyParserTestErrortypes.unknownConstantValue,
                                position.getFirst(), position.getSecond(), value));
                        break;
                    case onToken:
                        value = ex.currentToken.image;
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.onToken,
                                ex.currentToken.endLine,
                                ex.currentToken.endColumn, value));
                        break;
                    case empty:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.unknown,
                                -1, -1, ""));
                        break;
                    default:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.unknown,
                                -1, -1, ""));
                        break;
                }
                ret = false;
            } catch (DependencyException ex) {
                switch (ex.getExceptionType()) {
                    case dependencyExtractionError:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.dependencyExtractionError,
                                -1, -1, ex.getValue()));
                        break;
                    case noFunctionId:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.noFunctionId,
                                -1, -1, ""));
                        break;
                    case noParameterWrapper:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.noParameterWrapper,
                                -1, -1, ""));
                        break;
                    case noRunnableItem:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.noRunnableItem,
                                -1, -1, ""));
                        break;
                    case setParameter:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.setParameter,
                                -1, -1, ""));
                        break;
                    case setTooldescriptionException:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.setTooldescriptionException,
                                -1, -1, ""));
                        break;
                    case stringToTypeCastFailed:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.stringToTypeCastFailed,
                                -1, -1, ex.getValue()));
                        break;
                    case unsolveableDependency:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.unsolveableDependency,
                                -1, -1, ex.getValue()));
                        break;
                    case unsupportedCompare:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.unsupportedCompare,
                                -1, -1, ex.getValue()));
                        break;
                    case unsupportedOperation:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.unsupportedOperation,
                                -1, -1, ex.getValue()));
                        break;
                    case unsupportedType:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.notSupportedOrImplemented,
                                -1, -1, ex.getValue()));
                        break;
                    default:
                        exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.unknown,
                                -1, -1, ""));
                        break;
                }
                ret = false;
            }
        } catch (TokenMgrError ex){
            String error = ex.getMessage();
            int lineEnd = error.indexOf(", column ");
            int columnEnd = error.indexOf(".  Encountered: ");
            int encounteredEnd = error.indexOf("after : \"");

            String lineStr = error.substring(22, lineEnd);
            String columnStr = error.substring(lineEnd+9, columnEnd);
            String encountered = error.substring(columnEnd+16, encounteredEnd);

            int line = Integer.parseInt(lineStr);
            int column = Integer.parseInt(columnStr);

             exceptions.add(new DependencyParserTestError(
                                DependencyParserTestErrortypes.lexicalError,
                                line, column, encountered));
             ret = false;

        } catch (JAXBException ex) {
            exceptions.add(new DependencyParserTestError(
                    DependencyParserTestErrortypes.marshallerror,
                    -1, -1, ""));
            return false;
        }
        return ret;
    }

    /**
     * Recursive function to get all parameters of group and put it into the executeable.
     * Will circle endless when used with circular references. This has to be
     * prohobited in creation.
     * @param group group to search trhough
     * @param exec executeable to add parameters to
     */
    private static void getAllParameters(TparamGroup group, Texecutable exec) {

        for (Object ref : group.getParamrefOrParamGroupref()) {

            Object ob = null;
            if (ref instanceof TparamGroup.ParamGroupref) {
                ob = ((TparamGroup.ParamGroupref) ref).getRef();
            } else if (ref instanceof TparamGroup.Paramref) {
                ob = ((TparamGroup.Paramref) ref).getRef();
            }

            if (ob instanceof TparamGroup) {
                TparamGroup paramGroup = (TparamGroup) ob;
                if (!exec.getParamGroup().contains(paramGroup)) {
                    exec.getParamGroup().add(paramGroup);
                }
                getAllParameters(paramGroup, exec);
            } else if (ob instanceof Tparam) {
                Tparam param = (Tparam) ob;
                if (!exec.getParam().contains(param)) {
                    exec.getParam().add(param);
                }
            } else if (ob instanceof TenumParam) {
                TenumParam param = (TenumParam) ob;
                if (!exec.getEnumParam().contains(param)) {
                    exec.getEnumParam().add(param);
                }
            }
        }
    }

    /**
     * Finds the position of the first occurence of the id as Tupel <Line,Column>
     * @param dependencyDefinition definition to search in
     * @param id id to serach for
     * @return <Line,Column>
     */
    private Tupel<String,Tupel<Integer, Integer>> findIdPosition(
            String dependencyDefinition, String id) {

        // test if user entered the id himself, although that is not recommended
        // and will probably end here...
        if (dependencyDefinition.contains("@" + id)) {
            return new Tupel<String,Tupel<Integer, Integer>>(id,findPosition(dependencyDefinition, "@" + id));
        }

        // get the name corresponding to id, as found in original user input
        String name = "";
        try {
            name = parametermanager.getNameforId(id);
        } catch (BeansException ex) {
            return new Tupel<String,Tupel<Integer, Integer>>(id ,new Tupel<Integer, Integer>(-1, -1));
        }

        return new Tupel<String,Tupel<Integer, Integer>>(name,findPosition(dependencyDefinition, "<" + name + ">"));
    }

    /**
     * Finds the position of the first occurence of the id as Tupel <Line,Column>
     * @param dependencyDefinition definition to search in
     * @param token token to search for
     * @return <Line,Column>
     */
    private Tupel<Integer, Integer> findPosition(
            String dependencyDefinition, String token) {

        String[] lines = dependencyDefinition.split("\\r?\\n|\\r");
        for (int i = 0; i < lines.length; i++) {
            int col = lines[i].indexOf(token);
            if (col != -1) {
                return new Tupel<Integer, Integer>(i + 1, col);
            }
        }
        return new Tupel<Integer, Integer>(-1, -1);
    }

    public List<DependencyParserTestError> getExceptions() {
        return exceptions;
    }

    public List<Tupel<Integer, List<String>>> getIncorrectExamples() {
        return incorrectExamples;
    }

}
