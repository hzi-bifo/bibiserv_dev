/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.Example;
import de.unibi.cebitec.bibiserv.wizard.bean.OrderStore;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.Tfunction.ParamAndInputOutputOrder;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.ToutputFile;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import java.util.Collection;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Class used to create functions.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class FunctionBuilder {

    private static final String ID_BASE_TYPE = "function";

    public static Tfunction createFunction(String name,
            String shortDesc, String desc, List<String> inputs,
            String output, List<String> outputFiles, List<String> dependencies,
            String paramGroup,
            List<OrderStore> order, Collection<Example> examples,
            String langcode) {

        // get manager for referencing
        // this is a bit suboptimal, but the references need real objects
        FacesContext context = FacesContext.getCurrentInstance();

        ParameterGroupManager paramGroupManager =
                (ParameterGroupManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterGroupManager}",
                ParameterGroupManager.class);

        InputManager inputManager = (InputManager) context.getApplication().
                evaluateExpressionGet(context, "#{inputManager}",
                InputManager.class);

        OutputManager outputManager = (OutputManager) context.getApplication().
                evaluateExpressionGet(context, "#{outputManager}",
                OutputManager.class);
        
       OutputFileManager outputfileManager = (OutputFileManager) context.getApplication().
                evaluateExpressionGet(context, "#{outputFileManager}",
                OutputFileManager.class);

        ParameterManager parameterManager =
                (ParameterManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterManager}",
                ParameterManager.class);

        ParameterDependencyManager dependencyManager =
                (ParameterDependencyManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterDependencyManager}",
                ParameterDependencyManager.class);

        // create the function
        Tfunction function = new Tfunction();

        function.setId(IDGenerator.createTemporaryID(name, ID_BASE_TYPE));

        Tfunction.Name functionName = new Tfunction.Name();
        functionName.setLang(langcode);
        functionName.setValue(name);
        function.getName().add(functionName);

        Tfunction.ShortDescription functionShortDescription =
                new Tfunction.ShortDescription();
        functionShortDescription.setLang(langcode);
        functionShortDescription.setValue(shortDesc);
        function.getShortDescription().add(functionShortDescription);

        if (!desc.isEmpty()) {
            Tfunction.Description functionDescription =
                    new Tfunction.Description();
            functionDescription.setLang(langcode);
            functionDescription.getContent().add(desc);
            function.getDescription().add(functionDescription);
        }

        for (String input : inputs) {
            try {
                TinputOutput newRef = inputManager.getInputByName(input);
                Tfunction.Inputref ref = new Tfunction.Inputref();
                ref.setRef(newRef);
                function.getInputref().add(ref);
            } catch (BeansException ex) {
                // nothing, this is correct
            }
        }
        
        for (String outputFile : outputFiles) {
            try {
                ToutputFile newRef = outputfileManager.getOutputByName(outputFile);
                Tfunction.Outputfileref ref = new Tfunction.Outputfileref();
                ref.setRef(newRef);
                function.getOutputfileref().add(ref);
            } catch (BeansException ex) {
               // nothing, this is correct
            }
        }

        try {
            TinputOutput newRef = outputManager.getOutputByName(output);
            Tfunction.Outputref ref = new Tfunction.Outputref();
            ref.setRef(newRef);
            function.setOutputref(ref);
        } catch (BeansException ex) {
            // nothing, this is correct
        }

        for (String dependency : dependencies) {
            try {
                Tdependency newRef = dependencyManager.
                        getParameterDependencyByName(dependency);
                Tfunction.Depref ref = new Tfunction.Depref();
                ref.setRef(newRef);
                function.getDepref().add(ref);
            } catch (BeansException ex) {
                // nothing, this is correct
            }
        }

        try {
            TparamGroup newGroup = paramGroupManager.getParameterGroupByName(
                    paramGroup);
            function.setParamGroup(newGroup);
        } catch (BeansException ex) {
            // nothing, this is correct
        }

        // Add elements as JAXBElement, no namespace needed, just value needed
        ParamAndInputOutputOrder newOrder = new ParamAndInputOutputOrder();
        for (OrderStore store : order) {
            if (store.isIsString()) {

                JAXBElement jax = new JAXBElement(
                        new QName("bibiserv:de.unibi.techfak.bibiserv.cms",
                        "additionalString"), String.class,
                        store.getValue());
                newOrder.getReferenceOrAdditionalString().add(jax);

            } else {
                switch (store.getType()) {
                    case input:
                        try {
                            TinputOutput element =
                                    inputManager.getInputByName(store.getValue());
                            JAXBElement jax = new JAXBElement(new QName(
                                    "bibiserv:de.unibi.techfak.bibiserv.cms",
                                    "reference"), Object.class,
                                    Tfunction.ParamAndInputOutputOrder.class,
                                    element);
                            newOrder.getReferenceOrAdditionalString().add(jax);
                        } catch (BeansException ex) {
                            // should not happen!
                        }
                        break;
                    case output:
                        try {
                            TinputOutput element =
                                    outputManager.getOutputByName(
                                    store.getValue());
                            JAXBElement jax = new JAXBElement(new QName(
                                    "bibiserv:de.unibi.techfak.bibiserv.cms",
                                    "reference"), Object.class,
                                    Tfunction.ParamAndInputOutputOrder.class,
                                    element);
                            newOrder.getReferenceOrAdditionalString().add(jax);
                        } catch (BeansException ex) {
                            // should not happen!
                        }
                        break;
                    case parameter:
                        try {
                            Object element =
                                    parameterManager.getParameterByName(
                                    store.getValue());
                            JAXBElement jax = new JAXBElement(new QName(
                                    "bibiserv:de.unibi.techfak.bibiserv.cms",
                                    "reference"), Object.class,
                                    Tfunction.ParamAndInputOutputOrder.class,
                                    element);
                            newOrder.getReferenceOrAdditionalString().add(jax);
                        } catch (BeansException ex) {
                            // should not happen!
                        }
                        break;
                }
            }
        }
        function.setParamAndInputOutputOrder(newOrder);

        for (Example ex : examples) {
            Texample newExample = ExampleBuilder.createExample(ex.getName(), ex.
                    getDescription(), ex.getExamples(), langcode);
            function.getExample().add(newExample);
        }

        return function;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }

    /**
     * Please use IDGenerator methods for this!
     */
    @Deprecated
    public static String cleanFunctionId(String id, String toolPrefix) {
        String cleaned = id.replaceFirst("^"+toolPrefix+"_", "");
        cleaned = cleaned.replaceFirst("^"+ID_BASE_TYPE+"_", "");

        return cleaned;
    }
}
