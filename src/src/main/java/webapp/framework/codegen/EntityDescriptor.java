package webapp.framework.codegen;

/**
 *
 * @author wujianb
 */
public class EntityDescriptor {

    String filterBy;
    String filterFacet;
    String sortBy;
    String outputValue;
    
    String id;
    String label;
    String name;
    String dateTimeFormat;
    boolean blob;
    boolean relationshipOne;
    boolean relationshipMany;
    boolean required;
    Object returnType;
    String bundle;
    String valuesGetter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public boolean isBlob() {
        return blob;
    }

    public void setBlob(boolean blob) {
        this.blob = blob;
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

    public Object getReturnType() {
        return returnType;
    }

    public void setReturnType(Object returnType) {
        this.returnType = returnType;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getValuesGetter() {
        return valuesGetter;
    }

    public void setValuesGetter(String valuesGetter) {
        this.valuesGetter = valuesGetter;
    }

    public String getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(String filterBy) {
        this.filterBy = filterBy;
    }

    public String getFilterFacet() {
        return filterFacet;
    }

    public void setFilterFacet(String filterFacet) {
        this.filterFacet = filterFacet;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(String outputValue) {
        this.outputValue = outputValue;
    }

    
}
