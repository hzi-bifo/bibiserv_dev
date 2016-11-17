package de.unibi.cebitec.bibiserv.wizard.bean.manage;

import de.unibi.cebitec.bibiserv.wizard.bean.BasicBeanData;
import de.unibi.cebitec.bibiserv.wizard.tools.IDGenerator;
import de.unibi.techfak.bibiserv.cms.ObjectFactory;
import de.unibi.techfak.bibiserv.cms.Titem;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
@ManagedBean
@SessionScoped
public class BasicInfoBuilder {

    private TrunnableItem runnableItem;
    private static final String ID_BASE_TYPE = "basic_info";

    public BasicInfoBuilder() {
        //initialize the RunnableItem using a bibiserv-objectFactory.
        ObjectFactory factory = new ObjectFactory();

        this.runnableItem = factory.createTrunnableItem();
    }

    public void createTrunnable(String toolName, String shortDescription,
            String description, String customContent, String toolTipText,
            String keywords) {

        setName(toolName);
        setShortDescription(shortDescription);
        setDescription(description);
        setCustomContent(customContent);
        setToolTipText(toolTipText);
        setKeywords(keywords);
    }

    private void setName(String name) {

        Titem.Name runnableItemName;

        if (runnableItem.getName().isEmpty()) {
            runnableItemName = new Titem.Name();
            runnableItemName.setLang(BasicBeanData.StandardLanguage);
            runnableItem.getName().add(runnableItemName);
        } else {
            runnableItemName = runnableItem.getName().get(0);
        }
        runnableItemName.setValue(name);
        String runnableItemID = IDGenerator.createTemporaryID(name, ID_BASE_TYPE);
        runnableItem.setId(runnableItemID);
    }

    private void setShortDescription(String shortDescription) {

        Titem.ShortDescription runnableItemShortDescription;

        if (runnableItem.getShortDescription().isEmpty()) {

            runnableItemShortDescription = new Titem.ShortDescription();
            runnableItemShortDescription.setLang(BasicBeanData.StandardLanguage);
            runnableItem.getShortDescription().
                    add(runnableItemShortDescription);
        } else {
            runnableItemShortDescription =
                    runnableItem.getShortDescription().get(0);
        }

        runnableItemShortDescription.setValue(shortDescription);
    }

    private void setDescription(String description) {

        if (description != null && !description.isEmpty()) {
            Titem.Description runnableItemDescription;

            if (runnableItem.getDescription().isEmpty()) {

                runnableItemDescription = new Titem.Description();
                runnableItemDescription.setLang(BasicBeanData.StandardLanguage);
                runnableItemDescription.getContent().add(description);
                runnableItem.getDescription().add(runnableItemDescription);
            } else {
                runnableItemDescription = runnableItem.getDescription().get(0);
                runnableItemDescription.getContent().clear();
                runnableItemDescription.getContent().add(description);
            }
        }
    }

    private void setCustomContent(String customContent) {

        if (customContent != null && !customContent.isEmpty()) {
            Titem.CustomContent runnableItemCustomContent;

            if (runnableItem.getCustomContent().isEmpty()) {
                runnableItemCustomContent = new Titem.CustomContent();
                runnableItemCustomContent.setLang(BasicBeanData.StandardLanguage);
                runnableItemCustomContent.getContent().add(customContent);
                runnableItem.getCustomContent().add(runnableItemCustomContent);
            } else {
                runnableItemCustomContent = runnableItem.getCustomContent().get(0);
                runnableItemCustomContent.getContent().clear();
                runnableItemCustomContent.getContent().add(customContent);
            }
        }
    }

    private void setToolTipText(String toolTipText) {

        Titem.ToolTipText runnableItemTooltipText;

        if (runnableItem.getToolTipText().isEmpty()) {

            runnableItemTooltipText = new Titem.ToolTipText();
            runnableItemTooltipText.setLang(BasicBeanData.StandardLanguage);
            runnableItem.getToolTipText().add(runnableItemTooltipText);
        } else {
            runnableItemTooltipText = runnableItem.getToolTipText().get(0);
        }

        runnableItemTooltipText.setValue(toolTipText);
    }

    private void setKeywords(final String keywords) {

        //try to use , as delimiter first.
        String[] keywordsArray = keywords.split(",");
        if(keywordsArray.length == 1){
            //if that did not work, try ;
            keywordsArray = keywords.split(";");
             if(keywordsArray.length == 1){
                 //if even that did not work, try whitespaces
                 keywordsArray = keywords.split(" ");
             }
        }

        Titem.Keywords runnableItemKeywords;

        if (runnableItem.getKeywords().isEmpty()) {

            runnableItemKeywords = new Titem.Keywords();
            runnableItemKeywords.setLang(BasicBeanData.StandardLanguage);
            runnableItem.getKeywords().add(runnableItemKeywords);
        } else {
            runnableItemKeywords = runnableItem.getKeywords().get(0);
            runnableItemKeywords.unsetValue();
        }
        runnableItemKeywords.getValue().addAll(Arrays.asList(keywordsArray));
    }

    public TrunnableItem getTRunnable() {
        return runnableItem;
    }

    public void setRunnableItem(TrunnableItem runnableItem) {
        this.runnableItem = runnableItem;
    }

    public String getToolName() {

        if (runnableItem.getName().isEmpty()) {
            return "";
        }

        return runnableItem.getName().get(0).getValue();
    }

    public String getDescription() {

        if (runnableItem.getDescription().isEmpty() || runnableItem.getDescription().get(0).getContent().isEmpty()) {
            return "";
        }

        return (String) runnableItem.getDescription().get(0).getContent().get(0);
    }

    public String getShortDescription() {

        if (runnableItem.getShortDescription().isEmpty()) {
            return "";
        }

        return runnableItem.getShortDescription().get(0).getValue();
    }

    public String getCustomContent() {
        if (runnableItem.getCustomContent().isEmpty()) {
            return "";
        }

        return (String) runnableItem.getCustomContent().get(0).getContent().get(0);
    }

    public String getToolTipText() {

        if (runnableItem.getToolTipText().isEmpty()) {
            return "";
        }

        return runnableItem.getToolTipText().get(0).getValue();
    }

    public String getKeywords() {

        if (runnableItem.getKeywords().isEmpty()) {
            return "";
        }

        StringBuilder keywordsBuilder = new StringBuilder();

        //Get the list of keywords.
        List<String> keywordsList = runnableItem.getKeywords().get(0).getValue();

        //build a string containing all keywords seperated by commas.
        for (String keyword : keywordsList) {
            keywordsBuilder.append(keyword);
            keywordsBuilder.append(',');
        }

        //Remove last comma in the string and return it.
        if (keywordsBuilder.toString().isEmpty()) {
            return "";
        }
        return keywordsBuilder.substring(0, keywordsBuilder.length() - 1);
    }

    public static String getID_BASE_TYPE() {
        return ID_BASE_TYPE;
    }
}