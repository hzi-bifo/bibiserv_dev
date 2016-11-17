package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.exceptions.BeansException;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import java.util.List;
import javax.faces.context.FacesContext;

/**
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class ParameterGroupBuilder {

    private static final String ID_BASE_TYPE = "paramgroup";

    public static TparamGroup createParameterGroup(String name,
            String display, String shortDesc, String desc, String langcode,
            List<String> parameters, List<String> groups) {

        // get manager for ref generating
        FacesContext context = FacesContext.getCurrentInstance();
        ParameterManager paramManager =
                (ParameterManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterManager}",
                ParameterManager.class);
        ParameterGroupManager paramGroupManager =
                (ParameterGroupManager) context.getApplication().
                evaluateExpressionGet(context, "#{parameterGroupManager}",
                ParameterGroupManager.class);

        TparamGroup newGroup = new TparamGroup();

        newGroup.setId(IDGenerator.createTemporaryID(name, ID_BASE_TYPE));

        if (!display.isEmpty()) {
            TparamGroup.Name paramGroupName = new TparamGroup.Name();
            paramGroupName.setLang(langcode);
            paramGroupName.setValue(display);
            newGroup.getName().add(paramGroupName);
        }

        if (!shortDesc.isEmpty()) {
            TparamGroup.ShortDescription paramGroupShortDescription =
                    new TparamGroup.ShortDescription();
            paramGroupShortDescription.setLang(langcode);
            paramGroupShortDescription.setValue(shortDesc);
            newGroup.getShortDescription().add(paramGroupShortDescription);
        }

        if (desc != null && !desc.isEmpty()) {
            TparamGroup.Description paramGroupDescription =
                    new TparamGroup.Description();
            paramGroupDescription.setLang(langcode);
            paramGroupDescription.getContent().add(desc);
            newGroup.getDescription().add(paramGroupDescription);
        }

        for (String param : parameters) {
            try {
                TparamGroup.Paramref newRef = new TparamGroup.Paramref();
                Object refOb = paramManager.getParameterByName(param);
                newRef.setRef(refOb);
                newGroup.getParamrefOrParamGroupref().add(newRef);
            } catch (BeansException ex) {
                // should not happen when used correct
            }
        }

        for (String paramGroup : groups) {
            try {
                TparamGroup.ParamGroupref newRef =
                        new TparamGroup.ParamGroupref();
                Object refOb = paramGroupManager.getParameterGroupByName(
                        paramGroup);
                newRef.setRef(refOb);
                newGroup.getParamrefOrParamGroupref().add(newRef);
            } catch (BeansException ex) {
                // should not happen when used correct
            }
        }

        return newGroup;
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }

    /**
     * Please use methods of the ID generator here!
     */
    @Deprecated
    public static String cleanParameterId(String id, String toolPrefix) {
        String cleaned = id.replaceFirst("^"+toolPrefix+"_", "");
        cleaned = cleaned.replaceFirst("^"+ID_BASE_TYPE+"_", "");
        if (cleaned.isEmpty()){
            return id;
        }
        return cleaned;
    }
}
