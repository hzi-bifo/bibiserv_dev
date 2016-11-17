/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.wizard.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public final class IDGenerator {

    /**
     * Strips the name of all spaces and cast to lowercase (like used in ID).
     *
     * @param name name to strip
     * @return stripped name
     */
    public static String createName(String name) {
        return makeValidString(name);
    }

    public static boolean isValidName(String name) {
        return name.equals(createName(name));
    }

    /**
     * Generates the id of the object. PLEASE NOTE! This will create a temporary
     * string which has to be finalized, as soon as the toolname is finally
     * clear! (use the finalizeID-method for that).
     *
     * @param name a name.
     * @param type type of this object (e.g. Tparam)
     * @return id valid (temporary) id.
     */
    public static String createTemporaryID(String name, String type) {

        //check if it is already a valid (temporary) id.
        if (isValidTempID(name, type)) {
            return name;
        }

        return makeValidPrefix(type) + "_" + createName(name);
    }
    private static Pattern generalIDPattern = Pattern.compile("([a-z0-9]*)_([a-z0-9]*)_([a-z0-9_]*)");

    /**
     * Generates a valid name from an existing id.
     *
     * @param id input id.
     * @return valid name.
     */
    public static String buildNameFromID(String id) {
        Matcher matcher = generalIDPattern.matcher(id);
        if (matcher.matches()) {
            // if the id is valid according to wizard standard, use the name of the object.
            String name = matcher.group(3);
            return createName(name);
        } else {
            // otherwise use the whole id as name.
            return createName(id);
        }
    }

    /**
     * Checks if a given String is a valid (temporary) id already.
     *
     * PLEASE NOTE! This method assumes that the possible ID has a valid name
     * already.
     *
     * @param possibleID possible id String.
     * @param type type of this object (e.g. Tparam)
     * @return true if it is a valid (temporary) id.
     */
    public static boolean isValidTempID(String possibleID, String type) {
        Pattern idPattern = Pattern.compile(makeValidPrefix(type) + "_[a-z0-9_]*");
        Matcher idMatcher = idPattern.matcher(possibleID);
        return idMatcher.matches();
    }

    public static String finalizeID(String toolname, String id) {
        //First: Check if finalized already.
        if (isValidID(id, toolname)) {
            return id;
        }
        return makeValidPrefix(toolname) + "_" + id;
    }

    /**
     * Checks if a given String is a valid id already.
     *
     * PLEASE NOTE! This method assumes that the possible ID has a valid type
     * declaration already.
     *
     * @param possibleID possible id String.
     * @param toolname name of this tool.
     * @return true if it is a valid id.
     */
    public static boolean isValidID(String possibleID, String toolname) {
        Pattern idPattern = Pattern.compile(makeValidPrefix(toolname) + "_[a-z0-9]*_[a-z0-9_]*");
        Matcher idMatcher = idPattern.matcher(possibleID);
        return idMatcher.matches();
    }

    private static String makeValidPrefix(String toolNamePrefix) {
        toolNamePrefix = makeValidString(toolNamePrefix);
        return toolNamePrefix.replaceAll("_", "");
    }

    private static String makeValidString(String string) {
        //to lower case.
        string = string.toLowerCase();
        //replace all white spaces
        string = string.replaceAll(" ", "_");
        //remove all invalid characters.
        string = string.replaceAll("[^a-z0-9_]", "");
        return string;
    }

    /**
     * Removes the typestring in front of a valid temporary id. This only works
     * when type has no "_" chars! This is forbidden for Builder Classes!
     *
     * @param id is to strip
     * @return id without typestring aka the createName part
     */
    public static String stripType(String id) {

        String[] split = id.split("_", 2);
        if (split.length == 2) {
            return split[1];
        } else {
            return split[0];
        }
    }

    /**
     * Removes the toolname in front of a valid id.
     *
     * @param id valid id according to wizard standard.
     * @return temporary id (id without toolname).
     */
    public static String stripToolName(String id) {
        String[] split = id.split("_", 2);
        if (split.length == 2) {
            return split[1];
        } else {
            return split[0];
        }
    }
}