package webapp.framework.web.mvc;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

public abstract class GenericDataTableModel<T> extends LazyDataModel<T> {
    
    protected static final Logger logger = Logger.getLogger(GenericDataTableModel.class);
    
    private static final long serialVersionUID = 1L;
    private T selectedRow;
    private String sortField;
    private boolean resetPaginator = true;
    abstract protected List<T> load(PageRequest pageRequest);

    public String getSortField() {
        return sortField;
    }

    //Automatically called by PrimeFaces component.
    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        this.sortField = sortField;
        return load(toPageRequest(first, pageSize, sortField, sortOrder, filters));
    }

    protected PageRequest toPageRequest(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {
        int start = resetPaginator ? 0 : first;
        resetPaginator = false;

        if (sortField != null && sortOrder != null) {
            return new PageRequest(start / pageSize, pageSize, convert(sortOrder), sortField);
        } else {
            return new PageRequest(start / pageSize, pageSize);
        }
    }

    /**
     * Convert PrimeFaces SortOrder to our OrderByDirection.
     */
    public static Direction convert(SortOrder ob) {
        switch (ob) {
        case DESCENDING:
            return Direction.DESC;
        default:
            return Direction.ASC;
        }
    }
    
    @Override
    public String getRowKey(T item) {
        return "" + item.hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getRowData(String rowKey) {
        try{
            for (T item : ((List<T>) getWrappedData())) {
                if (rowKey.equals("" + item.hashCode()) || rowKey.hashCode()==item.hashCode()) {
                    return item;
                }
            }
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}