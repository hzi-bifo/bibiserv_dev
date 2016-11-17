/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
 * All rights reserved.
 * 
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You 
 * may not use this file except in compliance with the License. You can 
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 * 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  When distributing the software, include 
 * this License Header Notice in each file.  If applicable, add the following 
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 * 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 */
package de.unibi.cebitec.bibiserv.util.codegen.web.guiconstruction;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author atoepfer & dhagemei
 */
public class ParameterADT  {

    //General information
    private String name;
    private String id;
    private ParameterType type;
    private String option;
    private String description;
    private ParameterJsfElement jsfElement;
    private Boolean isRequired;
    //Information Needed for Numbers/Integers/floats
    private Object min;
    private Object max;
    private Object minIncluded;
    private Object maxIncluded;
    private Object defaultValueInt;
    //information for strings
    private String defaultValueString;
    private Object minLength;
    private Object maxLength;
    private Object regexp;
    //information for boolean(Default entry )
    private Object isEnabled;
    //information needed for enum params
    private ArrayList<String> defaultValueStringList;
    private Map<String, String> keyValuePair;

    /**
     * Constructor for inputText(INT).
     * @param id
     * @param name
     * @param type
     * @param option
     * @param description
     * @param jsfElement
     * @param isRequired
     * @param defaultValue
     * @param min
     * @param max
     * @param minIncluded
     * @param maxIncluded
     *
     *
     */
    public ParameterADT(String id, String name, ParameterType type,
            String option, String description, ParameterJsfElement jsfElement, Boolean isRequired,
            Object defaultValueInt, Object min, Object max, Object minIncluded,
            Object maxIncluded) {

        this.id = id;
        this.name = name;
        //this.type = ParameterType.valueOf(type.toUpperCase());
        this.type=type;
        this.option = option;
        this.description = description;
        //this.jsfElement = ParameterJsfElement.valueOf(jsfElement.toUpperCase());
        this.jsfElement=jsfElement;
        this.defaultValueInt = String.valueOf(defaultValueInt);
        this.min = min;
        this.max = max;
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;
        this.isRequired = isRequired;
    }

    /**
     * Constructor for inputText(STRING).
     * @param id
     * @param name
     * @param type
     * @param option
     * @param description
     * @param jsfElement
     * @param isRequired(Bool)
     * @param defaultValue
     * @param minLength
     * @param maxLength
     * @param regexp
     */
    public ParameterADT(String id, String name, ParameterType type,
            String option, String description, ParameterJsfElement jsfElement, Boolean isRequired,
            String defaultValueString, Object minLength, Object maxLength, Object regexp) {

        this.id = id;
        this.name = name;

        this.type = type;//ParameterType.valueOf(type.toUpperCase());
        this.option = option;
        this.description = description;
        this.jsfElement = jsfElement;//ParameterJsfElement.valueOf(jsfElement.toUpperCase());
        this.defaultValueString = defaultValueString;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.regexp = regexp;
        this.isRequired = isRequired;
       

    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    /**
     * Constructor for selectMany...COMPONENTS.
     * @param id
     * @param name
     * @param type
     * @param option
     * @param shortDescription
     * @param jsfElement
     * @param boolean reqired..
     * @param defaultValues list of stringlist
     * @param MAP of keyValuePair
     */
    public ParameterADT(String id, String name, ParameterType type, String option, String description, ParameterJsfElement jsfElement, Boolean isRequired, ArrayList<String> defaultValueStringList, Map<String, String> keyValuePair) {

        this.id = id;
        this.name = name;
        this.type = type;//ParameterType.valueOf(type.toUpperCase());
        this.option = option;
        this.description = description;
        this.jsfElement = jsfElement;//ParameterJsfElement.valueOf(jsfElement.toUpperCase());
        this.keyValuePair = keyValuePair;
        this.defaultValueStringList = defaultValueStringList;
        this.isRequired = isRequired;
    }

    /**
     * Constructor for selectOne..COMPONENTS.
     * @param id
     * @param name
     * @param type
     * @param option
     * @param shortDescription
     * @param jsfElement
     * @param boolean isRequired
     *  @param defaultValue
     * @param MAP of keyValuePair
     *
     *
     *
     */
    public ParameterADT(String id, String name, ParameterType type, String option,
            String description, ParameterJsfElement jsfElement, Boolean isRequired,String defaultValueString, Map<String, String> keyValuePair) {

        this.id = id;
        this.name = name;
        this.type = type;//ParameterType.valueOf(type.toUpperCase());
        this.option = option;
        this.description = description;
        this.jsfElement = jsfElement;//ParameterJsfElement.valueOf(jsfElement.toUpperCase());
        this.keyValuePair = keyValuePair;
        this.defaultValueString = defaultValueString;
        this.isRequired = isRequired;
    }

    /**
     * Constructor for selectBooleanCheckbox.
     * @param id
     * @param name
     * @param type
     * @param option
     * @param shortDescription
     * @param jsfElement
     * @param is required param
     * @param default value boolean (selected or not...)
     *
     */
    public ParameterADT(String id, String name, ParameterType type, String option, String description, ParameterJsfElement jsfElement,Boolean isRequired, Object isEnabled ) {
       
        this.id = id;
        this.name = name;
        this.type = type;//ParameterType.valueOf(type.toUpperCase());
        this.option = option;
        this.description = description;
        this.jsfElement = jsfElement;//ParameterJsfElement.valueOf(jsfElement.toUpperCase());
        this.isEnabled = isEnabled;
        this.isRequired = isRequired;
    }

    public Object getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Object isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Object getDefaultValueInt() {
        return defaultValueInt;
    }

    public void setDefaultValueInt(Object defaultValueInt) {
        this.defaultValueInt = defaultValueInt;
    }

    public ArrayList<String> getDefaultValueStringList() {
        return defaultValueStringList;
    }

    public void setDefaultValueStringList(ArrayList<String> defaultValueStringList) {
        this.defaultValueStringList.clear();
        this.defaultValueStringList = defaultValueStringList;
    }

    public String getDefaultValueString() {
        return (String) defaultValueString;
    }

    public void setDefaultValueString(String defaultValueString) {
        this.defaultValueString = defaultValueString;
    }

    public Object getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Object maxLength) {
        this.maxLength = maxLength;
    }

    public Object getMinLength() {
        return minLength;
    }

    public void setMinLength(Object minLength) {
        this.minLength = minLength;
    }

    public Object getRegexp() {
        return regexp;
    }

    public void setRegexp(Object regexp) {
        this.regexp = regexp;
    }

    public Map<String, String> getKeyValuePair() {
        return keyValuePair;
    }

    public void setKeyValuePair(Map<String, String> keyValuePair) {
        this.keyValuePair.clear();
        this.keyValuePair = keyValuePair;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ParameterJsfElement getJsfElement() {
        return jsfElement;
    }

    public void setJsfElement(ParameterJsfElement jsfElement) {
        this.jsfElement = jsfElement;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public Object isMaxIncluded() {
        return maxIncluded;
    }

    public void setMaxIncluded(Object maxIncluded) {
        this.maxIncluded = maxIncluded;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object isMinIncluded() {
        return minIncluded;
    }

    public void setMinIncluded(Object minIncluded) {
        this.minIncluded = minIncluded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    
}
