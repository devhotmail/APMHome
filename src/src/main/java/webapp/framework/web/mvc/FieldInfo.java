package webapp.framework.web.mvc;

import java.io.Serializable;

public class FieldInfo implements Serializable {

    private String fieldName;
    private String headerName;
    private String tableAlias;
    private boolean isVisible;
    private String filterMatchMode;

    public FieldInfo(String fieldName) {
        this.isVisible = true;
        this.filterMatchMode = "EQ";
        this.fieldName = fieldName;

        String[] str = fieldName.split("/");
        if(str.length>1){
            this.fieldName = str[0];
            this.headerName = str[1];
        }
        
        str = this.fieldName.split("-");
        if(str.length>1){
            this.tableAlias = str[0];
            this.fieldName = str[1];
        }
        
        if(this.headerName==null)
            headerName = this.fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }
    
    public String getFieldNameWithTablePrefix(){
        if(tableAlias==null) return this.fieldName;
        else return this.tableAlias + "-" + this.fieldName;
    }

    public boolean isIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getFilterMatchMode() {
        return filterMatchMode;
    }

    public void setFilterMatchMode(String filterMatchMode) {
        this.filterMatchMode = filterMatchMode;
    }
}
