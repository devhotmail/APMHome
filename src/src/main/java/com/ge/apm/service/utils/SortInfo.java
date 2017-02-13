/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

/**
 *
 * @author 212560881
 */
public class SortInfo {
    public enum TYPE{
        ASC, DESC
    }
    public String tableAlias;
    public String fieldName;
    public TYPE type;
    
    public SortInfo(String field, boolean isAscending){
        this.fieldName = field;
        if(isAscending) 
            this.type = TYPE.ASC;
        else
            this.type = TYPE.DESC;

        parseFieldName();
    }
    
    private void parseFieldName() {
        int fromIndex = fieldName.indexOf("'");
        fromIndex = (fromIndex == -1) ? 0 : (fromIndex + 1);

        int toIndex = fieldName.indexOf("'", fromIndex);
        toIndex = (toIndex == -1) ? fieldName.length() : toIndex;
        
        fieldName = fieldName.substring(fromIndex, toIndex);
        
        String[] nameParts = fieldName.split("-");
        if (nameParts.length > 1) {
            this.tableAlias = nameParts[0];
            this.fieldName = nameParts[1];
        }
    }

    public String getOrderBySql(){
        if(tableAlias==null)
            return String.format("%s %s", fieldName, type.toString());
        else
            return String.format("%s.%s %s", tableAlias, fieldName, type.toString());
    }
}
