package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.ExampleStore;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Texample;
import java.util.List;

/**
 * Class is used to build examples from raw data.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ExampleBuilder {

    public static Texample createExample(String name, String description,
            List<ExampleStore> exampleData, String langcode) {


        String parameterBase = ParameterBuilder.getID_BASE_TYPE();
        String inputBase = InputOutputBuilder.getID_BASE_TYPE_INPUT();

        Texample example = new Texample();

        Texample.Name exampleName = new Texample.Name();
        exampleName.setLang(langcode);
        exampleName.setValue(name);
        example.getName().add(exampleName);

        Texample.Description exampleDescription =
                new Texample.Description();
        exampleDescription.setLang(langcode);
        exampleDescription.setValue(description);
        example.getDescription().add(exampleDescription);

        for (ExampleStore store : exampleData) {
            if (!store.getValue().isEmpty()) {
                Texample.Prop newProp = new Texample.Prop();
                newProp.setValue(store.getValue());

                switch (store.getType()) {
                    case input:
                        newProp.setIdref(IDGenerator.createTemporaryID(store.
                                getName(),
                                inputBase));
                        break;
                    case parameter:
                        newProp.setIdref(IDGenerator.createTemporaryID(store.
                                getName(),
                                parameterBase));
                        break;
                }
                example.getProp().add(newProp);
            }
        }
        return example;
    }
}
