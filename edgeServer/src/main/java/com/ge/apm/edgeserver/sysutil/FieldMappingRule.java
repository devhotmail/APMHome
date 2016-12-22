package com.ge.apm.edgeserver.sysutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author 212547631
 */
public class FieldMappingRule {
    
    private static final Logger logger = Logger.getLogger(FieldMappingRule.class);
    
    private static final String TAG_CONST_STRING="_const?string=";
    private static final String ESCAPED_TAG_CONST_STRING="_const\\?string=";
    
    private static final String TAG_CONST_INTEGER="_const?integer=";
    private static final String ESCAPED_TAG_CONST_INTEGER="_const\\?integer=";
    
    private static final String TAG_JAVA_METHOD ="_javaMethod";
    public static final String JAVA_METHOD_TO_DO="***";

    private static final String TAG_REPLACE_FROM="replace=";

    private static final String TAG_FIELD_MAPPING="_mapping?fieldMap=";
    private static final String TAG_VALUE_MAPPING="_mapping?valueMap=";
    
    public static enum FieldMappingType{
        constantString, constantInteger, javaMethod, mapFromSingleField, mapFromMultipleFields, mapValueMappingTable,mapFieldMappingTable;
    }
    
    private FieldMappingType mappingType;
    private String fieldName;
    private String fromFieldName;
    private List<FieldMappingRule> fromFieldRules;
    
    private Map<String, Object> mappingTable;
    
    private Object defaultNullValue;
    private Object constValue;

    private String replaceFrom;
    private String replaceTo;

    private int fixLength;
    private String paddingLeftChar;

    public FieldMappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(FieldMappingType mappingType) {
        this.mappingType = mappingType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFromFieldName() {
        return fromFieldName;
    }

    public void setFromFieldName(String fromFieldName) {
        this.fromFieldName = fromFieldName;
    }

    public List<FieldMappingRule> getFromFieldRules() {
        return fromFieldRules;
    }

    public void setFromFieldRules(List<FieldMappingRule> fromFieldRules) {
        this.fromFieldRules = fromFieldRules;
    }


    public Object getDefaultNullValue() {
        return defaultNullValue;
    }

    public void setDefaultNullValue(Object defaultNullValue) {
        this.defaultNullValue = defaultNullValue;
    }

    public Object getConstValue() {
        return constValue;
    }

    public void setConstValue(Object constValue) {
        this.constValue = constValue;
    }

    public String getReplaceFrom() {
        return replaceFrom;
    }

    public void setReplaceFrom(String replaceFrom) {
        if(replaceFrom!=null)
            replaceFrom = Pattern.quote(replaceFrom);
        
        this.replaceFrom = replaceFrom;
    }

    public String getReplaceTo() {
        return replaceTo;
    }

    public void setReplaceTo(String replaceTo) {
        this.replaceTo = replaceTo;
    }

    public int getFixLength() {
        return fixLength;
    }

    public void setFixLength(int fixLength) {
        this.fixLength = fixLength;
    }

    public String getPaddingLeftChar() {
        return paddingLeftChar;
    }

    public void setPaddingLeftChar(String paddingLeftChar) {
        this.paddingLeftChar = paddingLeftChar;
    }
    
    public static FieldMappingType parseFieldMappingType(String value){
        if(value.startsWith(TAG_FIELD_MAPPING))
            return FieldMappingType.mapFieldMappingTable;
        else if(value.startsWith(TAG_VALUE_MAPPING))
            return FieldMappingType.mapValueMappingTable;
        else if(value.startsWith(TAG_CONST_STRING))
            return FieldMappingType.constantString;
        else if(value.startsWith(TAG_CONST_INTEGER))
            return FieldMappingType.constantInteger;
        else if(value.startsWith(TAG_JAVA_METHOD))
            return FieldMappingType.javaMethod;
        else if(value.contains("+"))
            return FieldMappingType.mapFromMultipleFields;
        else
            return FieldMappingType.mapFromSingleField;
    }
    
    public static String parseConstString(String value){
        return value.replaceAll(ESCAPED_TAG_CONST_STRING, "");
    }
    public static Integer parseConstInteger(String value){
        return Integer.parseInt(value.replaceAll(ESCAPED_TAG_CONST_INTEGER, ""));
    }
    public static Object parseConstValue(String value){
        if(value.contains(TAG_CONST_INTEGER))
            return parseConstInteger(value);
        else
            return parseConstString(value);
    }    

    public void parseFromMultipleFieldsRule(String ruleString) throws Exception{
        String[] rules = ruleString.split("\\+");
        
        for(String strRule: rules){
            FieldMappingRule rule = new FieldMappingRule();
            rule.setMappingType(FieldMappingRule.parseFieldMappingType(strRule));
            rule.parseFieldMappingRule(strRule);

            this.addFromFieldRule(rule);
        }
    }
    
    public void parseValueMappingTableRule(String ruleString) throws Exception{
        parseMappingTableRule(FieldMappingType.mapValueMappingTable, ruleString);
    }
    public void parseFieldMappingTableRule(String ruleString) throws Exception{
        parseMappingTableRule(FieldMappingType.mapFieldMappingTable, ruleString);
    }

    public void parseMappingTableRule(FieldMappingType mappingType,String ruleString) throws Exception{
        String tag;
        String mapName;
        if(mappingType.equals(FieldMappingType.mapValueMappingTable)){
            tag = TAG_VALUE_MAPPING;
            mapName = "mapToValues";
        }
        else{
            tag = TAG_FIELD_MAPPING;
            mapName = "mapToFields";
        }
        tag = Pattern.quote(tag);

        String mappingRule = ruleString.replaceFirst(tag, "").replace("'", "\"");
        JsonMapper mapper = JsonMapper.nonEmptyMapper();
        Map<String, Object> map = mapper.fromJson(mappingRule, HashMap.class);
        
        if(map.get("mapBy")==null)
            throw new Exception("Missing 'mapBy' in mapping rule, fieldName="+this.fieldName+", ruleString="+ruleString);
        String mapBy = map.get("mapBy").toString();

        if(map.get(mapName)==null){
            throw new Exception(String.format("Missing %s in mapping rule, fieldName=%s, ruleString=%s", 
                        mapName, this.fieldName, ruleString));
        }
        mappingTable = (Map<String, Object>)map.get(mapName);
        mappingTable.put("_mapBy", mapBy);
    }
    
    public void parseFieldMappingRule(String ruleString) throws Exception{
        if(mappingType.equals(FieldMappingType.constantString) || 
                mappingType.equals(FieldMappingType.constantInteger)){
            this.constValue = FieldMappingRule.parseConstValue(ruleString);    
            return;
        }
        
        String[] ruleItems = ruleString.split("\\?");
        this.fromFieldName = ruleItems[0]; // first part is source field name

        if(ruleItems.length==1) return;
        
        String[] ruleOptions = ruleItems[1].split("&");
        for (String ruleOption : ruleOptions) {
            if (ruleOption.contains(TAG_REPLACE_FROM)) {
                ruleOption = ruleOption.replaceAll(TAG_REPLACE_FROM, "").replaceAll("'", "");

                String[] replaceRule = ruleOption.split("\\|");
                if(replaceRule.length<1) throw new Exception("Error loading DataMapping.mappingRule, missing replace pattern: fieldName="+this.fieldName+", rule="+ruleString);
                this.replaceFrom = replaceRule[0];
                
                if(replaceRule.length>1)
                    this.replaceTo = replaceRule[1];
                else
                    this.replaceTo = "";
            }
        }
    }
    
    public void addFromFieldRule(FieldMappingRule rule){
        if(this.fromFieldRules==null) {
            fromFieldRules = new ArrayList<FieldMappingRule>();
        }
        fromFieldRules.add(rule);
    }
    
    public Object getMappedValue(Map<String, Object> dataMap) throws Exception{
        Object mappedValue = null;
        switch(mappingType){
            case constantString:
            case constantInteger:
                mappedValue = constValue;
                break;
            case javaMethod:
                // javaMethod should be handled in sub class
                mappedValue = dataMap.get(this.fieldName);
                break;
            case mapFromSingleField:
                mappedValue = dataMap.get(this.fromFieldName);
                break;
            case mapFromMultipleFields:
                mappedValue = "";
                boolean valueNotMapped = true;
                for(FieldMappingRule rule: fromFieldRules){
                    if(rule.getConstValue()!=null){
                        mappedValue = mappedValue + rule.getConstValue().toString();
                        valueNotMapped = false;
                    }
                    else{
                        Object value = dataMap.get(rule.getFromFieldName());
                        if(value==null) 
                            throw new Exception("Error mapping fieldValue, from field not found from input. targetFeildName="+this.fieldName+";  fromFieldName="+rule.getFromFieldName());

                        if(value!=null && rule.replaceFrom!=null && rule.replaceTo!=null)
                            value = value.toString().replaceAll(rule.replaceFrom, rule.replaceTo);
                        mappedValue = mappedValue + value.toString();
                        valueNotMapped = false;
                    }
                }
                if(valueNotMapped) mappedValue = null;
                
                break;
                
            case mapValueMappingTable:
                if(mappingTable==null)
                    throw new Exception("No mapping table defined for field:"+this.fieldName);

                String mapBy = mappingTable.get("_mapBy").toString();
                if(dataMap.get(mapBy)!=null){
                    String mapByFieldValue = dataMap.get(mapBy).toString();
                    mappedValue = mappingTable.get(mapByFieldValue);
                }
                else{
                    logger.debug("mapBy Field not found in dataMap, targetField="+this.fieldName);
                }

                break;
            case mapFieldMappingTable:
                if(mappingTable==null)
                    throw new Exception("No mapping table defined for field:"+this.fieldName);

                mapBy = mappingTable.get("_mapBy").toString();
                if(dataMap.get(mapBy)!=null){
                    String mapByFieldValue = dataMap.get(mapBy).toString();
                    if(mappingTable.get(mapByFieldValue)==null)
                        logger.debug(String.format("mapToField not found, targetField=%s, mapByField=%s, mapByFieldValue=%s",
                                this.fieldName, mapBy, mapByFieldValue));
                    else{
                        String mapToField = mappingTable.get(mapByFieldValue).toString();
                        if(dataMap.get(mapToField)!=null)
                            mappedValue = dataMap.get(mapToField).toString();
                        else{
                            logger.debug("mapToField's value is null, targetField="+this.fieldName+", mapToField="+mapToField);
                        }
                    }
                        
                }
                else{
                    logger.debug("mapBy Field not found in dataMap, targetField="+this.fieldName);
                }
                
                break;
                
            default:
                break;
        }
        
        if(mappedValue==null) {
            if(defaultNullValue!=null)
                mappedValue = defaultNullValue;
        }
        else if("".equals(mappedValue.toString().trim())) {
            if(defaultNullValue!=null)
                mappedValue = defaultNullValue;
        }
                    
        if(mappedValue!=null && this.replaceFrom!=null && this.replaceTo!=null)
            mappedValue = mappedValue.toString().replaceAll(replaceFrom, replaceTo);

        return mappedValue;
    }

    @Override
    public String toString() {
        return "{mappingType=" + mappingType + ", mappingTable="+mappingTable+" fieldName=" + fieldName + ", fromFieldName=" + fromFieldName + ", fromFieldRules=" + fromFieldRules + ", defaultNullValue=" + defaultNullValue + ", constValue=" + constValue + ", replaceFrom=" + replaceFrom + ", replaceTo=" + replaceTo+"}";
    }
    
    
}
