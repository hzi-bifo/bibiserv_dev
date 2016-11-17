package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.Tupel;
import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.exceptions.DependencyParserTestError;
import de.unibi.cebitec.bibiserv.wizard.exceptions.DependencyParserTestErrortypes;
import de.unibi.cebitec.bibiserv.wizard.exceptions.DependencyResolveNameException;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.Tdependency;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;

/**
 * Class used to create new parameter.
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ParameterDependencyBuilder {

    private static final String ID_BASE_TYPE = "parameterdependency";
    private static final Pattern idChangePattern = Pattern.compile("<(.*?)>");

    public static Tdependency createdependency(String name,
            String shortDesc, String desc, String langcode, String definition) {

        Tdependency dependencyItem = new Tdependency();

        Tdependency.Name dependencyName = new Tdependency.Name();
        dependencyName.setLang(langcode);
        dependencyName.setValue(name);
        dependencyItem.getName().add(dependencyName);

        Tdependency.ShortDescription dependencyShortDescription =
                new Tdependency.ShortDescription();
        dependencyShortDescription.setLang(langcode);
        dependencyShortDescription.setValue(shortDesc);
        dependencyItem.getShortDescription().add(dependencyShortDescription);

        if(desc !=null && !desc.isEmpty()){
        Tdependency.Description dependencyDescription =
                new Tdependency.Description();
        dependencyDescription.setLang(langcode);
        dependencyDescription.getContent().add(desc);
        dependencyItem.getDescription().add(dependencyDescription);
        }

        dependencyItem.setDependencyDefinition(definition);

        dependencyItem.setId(IDGenerator.createTemporaryID(name, ID_BASE_TYPE));

        return dependencyItem;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }

    /**
     * Resolves all namereferenced in the dependency definition (input) to the correct id.
     * @param globalPrefix If this this is the final run for xml-generation this is the name of the tool.
     * @param input the dependency definition
     * @return the resolved definition
     * @throws DependencyNameToIdResolveException on error
     */
    public static String insertRealIdsOverUserInput(String globalPrefix,
            String input) throws DependencyResolveNameException {
        List<DependencyParserTestError> unresolvedAreas =
                new ArrayList<DependencyParserTestError>();

        FacesContext context = FacesContext.getCurrentInstance();
        ParameterManager parametermanager =
                (ParameterManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterManager}",
                ParameterManager.class);

        String br = System.getProperty("line.separator");
        int brsize = br.length();
        String[] lines = input.split(br);

        Matcher matcher = idChangePattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String name = matcher.group(1);
            try {
                String id = parametermanager.getIdForName(name);
                if (!globalPrefix.isEmpty()) {
                    id = globalPrefix + "_" + id;
                }
                matcher.appendReplacement(sb, "@" + id);
            } catch (BeansException ex) {
                Tupel<Integer, Integer> position =
                        getLineColumnByAbsolutePosition(lines, brsize, matcher.
                        start());
                unresolvedAreas.add(new DependencyParserTestError(
                        DependencyParserTestErrortypes.nameresolve, position.
                        getFirst(), position.getSecond(), matcher.group()));
            }
        }

        if (!unresolvedAreas.isEmpty()) {
            throw new DependencyResolveNameException(unresolvedAreas);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Returns the position as <line,column> from absolute char position in str
     * @param lines lines of the input str
     * @param brsize size of the line delimiter
     * @param position absolute char-position
     * @return <line,column>
     */
    private static Tupel<Integer, Integer> getLineColumnByAbsolutePosition(
            String[] lines,
            int brsize, int start) {
        for (int i = 0; i < lines.length; i++) {
            int diff = start - lines[i].length() - brsize;
            if (diff < 0) {
                return new Tupel<Integer, Integer>(i + 1, start);
            }
            start = diff;
        }
        return new Tupel<Integer, Integer>(-1, -1);
    }

    /**
     * Please use IDGenerator methods for this!
     */
    @Deprecated
    public static String cleanDependencyId(String id, String toolPrefix) {
        String cleaned = id.replaceFirst("^"+toolPrefix+"_", "");
        cleaned = cleaned.replaceFirst("^"+ID_BASE_TYPE+"_", "");

        return cleaned;
    }
}
