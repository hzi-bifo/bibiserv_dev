package de.unibi.cebitec.bibiserv.wizard.tools;

import de.unibi.cebitec.bibiserv.wizard.bean.enums.XSLTParsingErrors;
import de.unibi.cebitec.bibiserv.wizard.properties.PropertyManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * This class manages the post processing for any html conversion that involves
 * microhtml content.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class MicroHTMLPostProcessor {

    /*
     * These final variables define properties used for postprocessing done in
     * java. NOTE! IF YOU CHANGE THE parseMicroHTML.xsl-SCRIPT YOU HAVE TO
     * CHANGE THESE VARIABLES PROBABLY!
     */
    private static final String MICROHTMLNAMESPACENAME = "microhtml";
    private static final String MICROHTMLROOTNAME = "microhtml";
    private static final String MICROHTMLNAMESPACE =
            "xmlns:" + MICROHTMLNAMESPACENAME
            + "=\"bibiserv:de.unibi.techfak.bibiserv.cms.microhtml\"";
    private static final String MINIHTMLNAMESPACENAME = "minihtml";
    private static final String MINIHTMLROOTNAME = "minihtml";
    /**
     * This tag forces a br in microhtml content.
     */
    private static final String BRFORCETAG = "<!--br-->";
    private static final String MICROHTMLBRTAG = "<" + MICROHTMLNAMESPACENAME + ":br/>";
    private static final String BRREPLACESTRING = "<!--insertparagraph-->";
    /**
     * This tag forces a hr in microhtml content.
     */
    private static final String HRFORCETAG = "<!--hr-->";
    private static final String MICROHTMLHRTAG = "<" + MICROHTMLNAMESPACENAME + ":hr/>";
    /*
     * Compositions of tags.
     */
    public static final String MICROHTMLSTARTTAG = "<" + MICROHTMLNAMESPACENAME + ":" + MICROHTMLROOTNAME + " " + MICROHTMLNAMESPACE + ">";
    public static final String MICROHTMLENDTAG = "</" + MICROHTMLNAMESPACENAME + ":" + MICROHTMLROOTNAME + ">";
    public static final String MINIHTMLSTARTTAG = "<" + MINIHTMLNAMESPACENAME + ":" + MINIHTMLROOTNAME + " xmlns:[^>]*>";
    public static final String MINIHTMLENDTAG = "</" + MINIHTMLNAMESPACENAME + ":" + MINIHTMLROOTNAME + ">";
    private static final String MICROHTMLPSTARTTAG = "<" + MICROHTMLNAMESPACENAME + ":p>";
    private static final String MICROHTMLPENDTAG = "</" + MICROHTMLNAMESPACENAME + ":p>";
    private static final Pattern MICROHTMLLISTSTARTPATTERN = Pattern.compile("(<" + MICROHTMLNAMESPACENAME + ":([uo]l)[^>]*>)");
    private static final Pattern MICROHTMLLISTENDPATTERN = Pattern.compile("(</" + MICROHTMLNAMESPACENAME + ":([uo]l)>)");
    /*
     * Temporary structures.
     */
    private static final String TEMPPSTARTTAG = "%%startp%%";
    private static final String TEMPPENDTAG = "%%endp%%";
    private static final String TEMPNEWLINE = "%%newline%%";
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String PSTARTREPLACESTRING = "<!--startparagraph-->";
    private static final String PENDREPLACESTRING = "<!--endparagraph-->";
    private static final Pattern PBLOCKPATTERN = Pattern.compile(PSTARTREPLACESTRING + "(.*?)" + PENDREPLACESTRING);

    /**
     * This method does the post processing for raw html to unserhtml conversion
     * processes
     *
     * @param content The content that shall be processed.
     * @param rootStartTag the start root tag of your content (e.g.
     * <microhtml:microhtml xmlns= ...>)
     * @param rootEndTag the end root tag of your content (e.g.
     * </microhtml:microhtml>)
     * @return post processed content.
     */
    @Deprecated
    public static String postProcessing(String content, String rootStartTag, String rootEndTag) {

        //remove newlines for better parsing.
        content = replaceNewLinesTemporarily(content, TEMPNEWLINE);

        if (content.contains(BRREPLACESTRING)) {
            // manual parsing to process <br/>s (which are changed to paragraphs).

            // Wrap the whole document in p tags.

            content = content.replaceAll(rootStartTag, "$0" + TEMPPSTARTTAG);

            content = content.replaceAll(rootEndTag, TEMPPENDTAG + "$0");

            // end an existing paragraph if a new one starts.
            content = content.replaceAll(MICROHTMLPSTARTTAG, TEMPPENDTAG + TEMPPSTARTTAG);
            // start a new paragraph if an existing one ends.
            content = content.replaceAll(MICROHTMLPENDTAG, TEMPPENDTAG + TEMPPSTARTTAG);

            // start searching for list tags

            int listCounter = 0;
            // split the microhtml content into the parts seperated by list starts.
            String[] listParts = manualSplit(MICROHTMLLISTSTARTPATTERN, content);

            if (listParts.length > 1) {
                // If a list start tag was found at all, end the current paragraph there.
                listParts[0] += TEMPPENDTAG;

                for (int i = 1; i < listParts.length; i++) {
                    //Increment the list counter for every found list start.
                    listCounter++;

                    Matcher listEndMatcher = MICROHTMLLISTENDPATTERN.matcher(listParts[i]);
                    while (listEndMatcher.find()) {
                        //decrement the list counter for every found list end.
                        listCounter--;
                        if (listCounter == 0) {
                            // if the list counter is down to zero again, start a new paragraph.
                            int start = listEndMatcher.start();
                            int end = listEndMatcher.end();
                            if (start < 0) {
                                start = 0;
                            }
                            if (end > listParts[i].length()) {
                                end = listParts[i].length();
                            }
                            listParts[i] = listParts[i].substring(0, start)
                                    + listEndMatcher.group(0) + TEMPPSTARTTAG
                                    + listParts[i].substring(end, listParts[i].length());
                            if (i < listParts.length - 1) {
                                // if there is another list start match following, end the current paragraph again.
                                listParts[i] += TEMPPENDTAG;
                            }
                        }
                    }
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < listParts.length; i++) {
                    builder.append(listParts[i]);
                }
                content = builder.toString();
            }
            // replace the line breaks by paragraphs.

            content = content.replaceAll(BRREPLACESTRING, TEMPPENDTAG + TEMPPSTARTTAG);

            // remove empty temp tags.

            content = content.replaceAll(TEMPPSTARTTAG + "(" + TEMPNEWLINE + "| )*?" + TEMPPENDTAG, "");

            // replace the temporary tags with real ones.

            content = content.replaceAll(TEMPPSTARTTAG, MICROHTMLPSTARTTAG);
            content = content.replaceAll(TEMPPENDTAG, MICROHTMLPENDTAG);
        }

        //If the user signals that she/he _really_ wants a br here, allow it.

        if (content.contains(BRFORCETAG)) {
            content = content.replaceAll(BRFORCETAG, MICROHTMLBRTAG);
        }

        //If the user signals that she/he _really_ wants a hr here, allow it.

        if (content.contains(HRFORCETAG)) {
            content = content.replaceAll(HRFORCETAG, MICROHTMLHRTAG);
        }

        // get newline strings back into the content.

        content = content.replaceAll(TEMPNEWLINE, NEWLINE);

        return content;
    }

    /**
     * This method does the post processing for raw html to unserhtml conversion
     * processes.
     *
     * Post processing includes handling of br replacement as well as br- and
     * hr-force-tags.
     *
     * @param content The content that shall be processed.
     * @return post processed content.
     */
    public static String postProcessing(String content) {

        // Replace newline characters with temporary ones for easier parsing.
        content = replaceNewLinesTemporarily(content, TEMPNEWLINE);

        Matcher blockMatcher = PBLOCKPATTERN.matcher(content);

        StringBuilder builder = new StringBuilder();

        int lastMatchEnd = 0;

        while (blockMatcher.find()) {

            //append all content between last match and this one.
            builder.append(content.substring(lastMatchEnd, blockMatcher.start()));


            String blockContent = blockMatcher.group(1);
            if (blockContent.contains(BRREPLACESTRING)) {
                builder.append(TEMPPSTARTTAG);
                blockContent = blockContent.replaceAll(BRREPLACESTRING, TEMPPENDTAG + TEMPPSTARTTAG);
                builder.append(blockContent);
                builder.append(TEMPPENDTAG);
            } else {
                builder.append(blockContent);
            }
            lastMatchEnd = blockMatcher.end();
        }

        builder.append(content.substring(lastMatchEnd, content.length()));

        content = builder.toString();

        // remove empty temp tags.

        content = content.replaceAll(TEMPPSTARTTAG + "(" + TEMPNEWLINE + "| )*" + TEMPPENDTAG, "");

        // replace the temporary p tags with real ones.

        content = content.replaceAll(TEMPPSTARTTAG, MICROHTMLPSTARTTAG);
        content = content.replaceAll(TEMPPENDTAG, MICROHTMLPENDTAG);

        //If the user signals that she/he _really_ wants a br here, allow it.

        if (content.contains(BRFORCETAG)) {
            content = content.replaceAll(BRFORCETAG, MICROHTMLBRTAG);
        }

        //If the user signals that she/he _really_ wants a hr here, allow it.

        if (content.contains(HRFORCETAG)) {
            content = content.replaceAll(HRFORCETAG, MICROHTMLHRTAG);
        }
        
        // remove all error comments from the parsed micro-html.

        content = content.replaceAll("<!--ERROR!.*?-->", "");

        //bring back actual new lines.

        content = content.replaceAll(TEMPNEWLINE, NEWLINE);
        

        return content;
    }

    /**
     * this method replaces newlines with a temporary character.
     *
     * @param content content that shall be stripped of newline characters.
     * @param tmpNewLine the string that shall be used to replace newlines.
     * @return content without newline characters.
     */
    private static String replaceNewLinesTemporarily(String content, String tmpNewLine) {
        StringReader stringReader = new StringReader(content);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        String nextLine;
        StringBuilder builder = new StringBuilder();
        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                builder.append(nextLine);
                builder.append(tmpNewLine);
            }
        } catch (IOException ex) {
            // should not happen
        }
        //remove last appended tmpnewline string
        return builder.substring(0, builder.length() - tmpNewLine.length());
    }

    /**
     * This method splits a given String at every match of a given pattern. The
     * match is left at the beginning of the next part.
     *
     * @param pattern Pattern that is used to identify matches.
     * @param content String that may contain the pattern.
     * @return Array of strings that has been split at every occurence of the
     * given pattern.
     */
    private static String[] manualSplit(Pattern pattern, String content) {
        ArrayList<Integer> matchIndexList = new ArrayList<Integer>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            matchIndexList.add(matcher.start());
        }
        if (matchIndexList.isEmpty()) {
            String[] split = {content};
            return split;
        } else {
            int matches = matchIndexList.size();
            String[] split = new String[matches + 1];
            split[0] = content.substring(0, matchIndexList.get(0));
            for (int i = 1; i < matches; i++) {
                split[i] = content.substring(matchIndexList.get(i - 1), matchIndexList.get(i));
            }
            split[matches] = content.substring(matchIndexList.get(matches - 1), content.length());
            return split;
        }
    }
    
      /**
     * This removes all error-comment-strings from microhtml or minihtml data
     * and forwards the errors to primefaces.
     *
     * @param processedHTML microhtml or minihtml data with error-comments
     * @return true if errors have occured
     */
    public static boolean parseConversionErrorComments(String processedHTML) {

        boolean errorOccured = false;
        
        // append error message
        for (XSLTParsingErrors possibleError : XSLTParsingErrors.values()) {
            if (processedHTML.contains(possibleError.getError())) {
                if (FacesContext.getCurrentInstance() != null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            PropertyManager.getProperty(possibleError.getMessage()), ""));
                }
                errorOccured = true;
            }
        }

        return errorOccured;
    }
}
