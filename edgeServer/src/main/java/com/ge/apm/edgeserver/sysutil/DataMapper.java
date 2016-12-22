package com.ge.apm.edgeserver.sysutil;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class DataMapper {

    private static final Logger logger = Logger.getLogger(DataMapper.class);

    private String defaultNullValue = null;

    protected final Map<String, FieldMappingRule> fieldMappingRules = new HashMap<String, FieldMappingRule>();
    
    public DataMapper(Map<String, String> mappingRule, Map<String, String> defaultNullValues) throws IllegalArgumentException, Exception {
        for (Map.Entry<String, String> ruleItem : mappingRule.entrySet()) {
            FieldMappingRule fieldMappingRule = new FieldMappingRule();
            fieldMappingRule.setFieldName(ruleItem.getKey());
            fieldMappingRules.put(ruleItem.getKey(), fieldMappingRule);
                    
            fieldMappingRule.setMappingType(FieldMappingRule.parseFieldMappingType(ruleItem.getValue()));
            switch(fieldMappingRule.getMappingType()){
                case constantString:
                case constantInteger:
                    fieldMappingRule.setConstValue(FieldMappingRule.parseConstValue(ruleItem.getValue()));
                    break;
                case javaMethod:
                    // javaMethod should be handled in sub class
                    break;
                case mapFromSingleField:
                    fieldMappingRule.parseFieldMappingRule(ruleItem.getValue());
                    break;
                case mapFromMultipleFields:
                    fieldMappingRule.parseFromMultipleFieldsRule(ruleItem.getValue());
                    break;
                case mapValueMappingTable:
                    fieldMappingRule.parseValueMappingTableRule(ruleItem.getValue());
                    break;
                case mapFieldMappingTable:
                    fieldMappingRule.parseFieldMappingTableRule(ruleItem.getValue());
                    break;
                default:
                    break;
            }
        }
        
        for (Map.Entry<String, String> item: defaultNullValues.entrySet()) {
            if("_defaultNullValue".equals(item.getKey())){
                defaultNullValue = FieldMappingRule.parseConstString(item.getValue());
            }
            else {
                FieldMappingRule fieldMappingRule = fieldMappingRules.get(item.getKey());
                if(fieldMappingRule==null) throw new Exception("Error loading DataMapping.defaultNullValues, data field not found: fieldName="+item.getKey());
                fieldMappingRule.setDefaultNullValue(FieldMappingRule.parseConstValue(item.getValue()));
            }
        }
        
        for (Map.Entry<String, FieldMappingRule> item: fieldMappingRules.entrySet()) {
            if(defaultNullValue!=null) {
                FieldMappingRule rule = item.getValue();
                if(rule.getDefaultNullValue()==null) {
                    //using default value for NULL value if itself not defined in XML
                    rule.setDefaultNullValue(defaultNullValue);
                }
            }
            
            logger.debug("mapping rule="+item);
        }
    }

    public Object getMappedFieldValue(Map<String, Object> dataMap, FieldMappingRule fieldRule) throws Exception{
        //first ensure all the from field's value is set into dataMap
        if(fieldRule.getConstValue()==null){
            if(fieldRule.getFromFieldName()!=null){
                String fromFieldName = fieldRule.getFromFieldName();
                if(!dataMap.containsKey(fromFieldName)){
                    FieldMappingRule fromFromFieldRule = fieldMappingRules.get(fromFieldName);
                    if(fromFromFieldRule==null)
                        throw new Exception("Error getMappedFieldValue, from field rule not found in input. field="
                                +fieldRule.getFieldName()+"; fromField="+fromFieldName);

                    dataMap.put(fromFieldName, 
                            getMappedFieldValue(dataMap, fieldMappingRules.get(fromFieldName)));
                }
            }
            else if(fieldRule.getFromFieldRules()!=null) {
                for(FieldMappingRule fromFieldRule: fieldRule.getFromFieldRules()){
                    if(fromFieldRule.getConstValue()!=null) continue;

                    String fromFieldName = fromFieldRule.getFromFieldName();
                    if(fromFieldName==null) continue;

                    if(!dataMap.containsKey(fromFieldName)){
                        FieldMappingRule fromFromFieldRule = fieldMappingRules.get(fromFieldName);
                        if(fromFromFieldRule==null)
                            throw new Exception("Error getMappedFieldValue, from field rule not found in input. field="
                                    +fieldRule.getFieldName()+"; fromField="+fromFieldName);

                        dataMap.put(fromFieldName, getMappedFieldValue(dataMap, fromFromFieldRule));
                    }
                }
            }
        }
        //now try to get mapped field value:
        Object mappedValue = fieldRule.getMappedValue(dataMap);
        dataMap.put(fieldRule.getFieldName(), mappedValue);
        return mappedValue;
    }
    
    public void preProcessData(Map<String, Object> dataMap){
    }
    
    public Map<String, Object> doDataMapping(Map<String, Object> dataMap) throws Exception {
        preProcessData(dataMap);
        
        for (Map.Entry<String, FieldMappingRule> item: fieldMappingRules.entrySet()) {
            getMappedFieldValue(dataMap, item.getValue());
        }
        
        return dataMap;
    }

}
