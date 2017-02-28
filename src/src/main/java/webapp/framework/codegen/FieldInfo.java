package webapp.framework.codegen;

/**
 *
 * @author wujianb
 */
public class FieldInfo {
    public static boolean dataTableEnableFilter = true;
    public static boolean dataTableEnableSort = true;
    
    public String entityName;
    public String managedBean;
    public boolean isNativeSQL;

    String fieldType;
    String fieldName;
    String itemId;
    String filterBy;
    String filterFacet;
    String sortBy;
    String itemFieldValue;
    String booleanFieldValue;
    String selectedFieldValue;
    String refObject;
    
    boolean blob;
    boolean booleanField;
    boolean dateTime;
    boolean relationshipOne;
    boolean relationshipMany;
    boolean required;

    public void setManagedBean(String managedBean) {
        this.managedBean = managedBean;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }

    public boolean isBlob() {
        return blob;
    }

    public void setBlob(boolean blob) {
        this.blob = blob;
    }

    public boolean isDateTime() {
        return dateTime;
    }

    public void setDateTime(boolean dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isRelationshipOne() {
        return relationshipOne;
    }

    public void setRelationshipOne(boolean relationshipOne) {
        this.relationshipOne = relationshipOne;
    }

    public boolean isRelationshipMany() {
        return relationshipMany;
    }

    public void setRelationshipMany(boolean relationshipMany) {
        this.relationshipMany = relationshipMany;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getFieldLabel() {
        return String.format("#{msg.%s}", fieldName);
    }

    public String getItemId() {
        return "#{item.id}";
    }

    public String getItemFieldValue() {
        if(isNativeSQL)
            return String.format("#{item.'%s'}", fieldName);
        else
            return String.format("#{item.%s}", fieldName);
    }

    public String getSelectedFieldValue() {
        return String.format("#{%s.selected.%s}", managedBean, fieldName);
    }

    public String getRefObjIdFieldValue() {
        return String.format("#{%s.selected.%sId}", managedBean, fieldName);
    }
    
    public String getFilterBy() {
        if(dataTableEnableFilter){
            String matchMode = "EQ";
            if(this.isDateTime()) matchMode = "GTE";
            else if(this.fieldType.contains("String")) matchMode = "LIKE";
            
            return String.format(" filterBy=\"#{item.%s}\" filterMatchMode=\"%s\"", fieldName, matchMode);
        }
        else
            return "";
    }

    public String getSortBy() {
        if(dataTableEnableSort){
            if(isNativeSQL)
                return String.format(" sortBy=\"#{item.'%s'}\"", fieldName);
            else
                return String.format(" sortBy=\"#{item.%s}\"", fieldName);
        }
        else
            return "";
    }

    public void setRefObject(String refObject) {
        this.refObject = refObject;
    }

    public String getRefObject() {
        return refObject;
    }
    public String getRefObjectList() {
        return String.format("#{%sController.itemList}", refObject);
    }

    public String getRefObjectLabel() {
        return String.format("#{%s.name}", refObject);
    }

    public String getRefObjectValue() {
        return String.format("#{%s.id}", refObject);
    }

    public String getFilterFacet() {
        return filterFacet;
    }

    public String getBooleanFieldValue() {
        if(isNativeSQL)
            return String.format("#{item.'%s'? msg.True2Yes : msg.False2No }", fieldName);
        else
            return String.format("#{item.%s? msg.True2Yes : msg.False2No }", fieldName);
    }
    
    public String getRequiredTag(){
        if(this.required) 
            return String.format(" required=\"true\" requiredMessage=\"#{msg.%s} #{msg.ValidationRequire}\"", fieldName);
        else
            return "";
    }
    
    public String getFieldLabelWithRequiredMark(){
        if(this.required) 
            return String.format("#{msg.%s}*", fieldName);
        else
            return String.format("#{msg.%s}", fieldName);
    }
}
