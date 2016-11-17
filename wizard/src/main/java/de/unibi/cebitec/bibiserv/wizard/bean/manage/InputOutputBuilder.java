
package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.InputOutputType;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TinputOutput;

/**
 * Class used to create inputs and outputs.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class InputOutputBuilder {

    private static final String ID_BASE_TYPE_OUTPUT = "output";
    private static final String ID_BASE_TYPE_INPUT = "input";

    /**
     * Creates a new Input/Output and retuns it
     * @param name name
     * @param shortDesc short Description
     * @param desc Description
     * @param langcode Languagecode p.e. "en"
     * @param option Options
     * @param type Tpyp/Format
     * @param handling Handling
     * @param example file that is used as example
     * @param inputOutput is this an input or an output
     * @return new Input/Output
     */
    public static TinputOutput createInputOutPut(String name,
            String shortDesc, String desc, String langcode, String option,
            String type, String handling, byte[] example,
            InputOutputType inputOutput, boolean streamsSupported) {

        TinputOutput inputOutputItem = new TinputOutput();

        TinputOutput.Name inputOutputName = new TinputOutput.Name();
        inputOutputName.setLang(langcode);
        inputOutputName.setValue(name);
        inputOutputItem.getName().add(inputOutputName);

        TinputOutput.ShortDescription inputOutputShortDescription =
                new TinputOutput.ShortDescription();
        inputOutputShortDescription.setLang(langcode);
        inputOutputShortDescription.setValue(shortDesc);
        inputOutputItem.getShortDescription().add(inputOutputShortDescription);

        TinputOutput.Description inputOutputDescription =
                new TinputOutput.Description();
        inputOutputDescription.setLang(langcode);
        inputOutputDescription.getContent().add(desc);
        inputOutputItem.getDescription().add(inputOutputDescription);

        inputOutputItem.setHandling(handling);

        switch (inputOutput) {
            case output:
                inputOutputItem.setId(IDGenerator.createTemporaryID(name,
                        ID_BASE_TYPE_OUTPUT));
                break;
            case input:
                inputOutputItem.setId(IDGenerator.createTemporaryID(name,
                        ID_BASE_TYPE_INPUT));
                inputOutputItem.setStreamsSupported(streamsSupported);
                break;
        }

        if (option.length() > 0) {
            inputOutputItem.setOption(option);
        }

        inputOutputItem.setType(type);

        if (example != null) {
            inputOutputItem.setExample(example);
        }
        
        return inputOutputItem;

    }

    public static String getID_BASE_TYPE_INPUT() {
        return ID_BASE_TYPE_INPUT;
    }

    public static String getID_BASE_TYPE_OUTPUT() {
        return ID_BASE_TYPE_OUTPUT;
    }
}
