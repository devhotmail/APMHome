package webapp.framework.web.mvc;

import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import net.sf.ehcache.hibernate.management.impl.BeanUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.Visibility;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.HttpRequestUtil;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

public abstract class JpaCRUDController<E> implements Serializable, ServerEventInterface {
    private static final long serialVersionUID = 1L;

    protected boolean filterBySite = true;
    protected boolean filterByHospital = false;
    protected boolean filterByLoginUser = false;
  
    protected static final String searchFilterPrefix = ":search_";
    protected static final Logger logger = Logger.getLogger(GenericCRUDController.class);

    abstract protected GenericRepository<E> getDAO();

    public Map<String, String> filterMatchModes;
    public List<SearchFilter> searchFilters;
    protected boolean warnOnEmptySearchFilters = false;
    public String crudActionName;

    public boolean hasFilterOnField(String fieldName){
        for(SearchFilter filter: searchFilters){
            if(fieldName.equals(filter.fieldName)){
                return true;
            }
        }

        return false;
    }
    
    public void setSiteFilter(){
        if(filterBySite){
            if (this.searchFilters == null) {
                this.searchFilters = new ArrayList<SearchFilter>();
            }
            
            if(!hasFilterOnField("siteId"))
                UserContextService.setSiteFilter(searchFilters);
        }
    }
    public void setHospitalFilter(){
        if(filterByHospital){
            if (this.searchFilters == null) {
                this.searchFilters = new ArrayList<SearchFilter>();
            }

            if(!hasFilterOnField("hospitalId"))
                UserContextService.setHospitalFilter(searchFilters);
        }
    }
    public void setLoginUserFilter(){
        if(filterByLoginUser){
            if (this.searchFilters == null) {
                this.searchFilters = new ArrayList<SearchFilter>();
            }

            UserContextService.setSiteFilter(searchFilters);
        }
    }
    
    public boolean isWarnOnEmptySearchFilters() {
        return warnOnEmptySearchFilters;
    }
    public void setWarnOnEmptySearchFilters(boolean warnOnEmptySearchFilters) {
        this.warnOnEmptySearchFilters = warnOnEmptySearchFilters;
    }
        
    public Class<E> getBeanClass() {
        return (Class<E>)
               ((ParameterizedType)(getClass().getGenericSuperclass()))
               .getActualTypeArguments()[0];
    }        

    private Map<String, Boolean> dateTimeFields = null;
    protected boolean isDateTimeField(String fieldName){
        if(dateTimeFields==null){
            dateTimeFields = new HashMap<String, Boolean>();
            Class a = getBeanClass();
            for(Field f: a.getDeclaredFields()){
                if(f.getType().toString().contains("Date"))
                    dateTimeFields.put(f.getName(), Boolean.TRUE);
            }
        }
        return dateTimeFields.get(fieldName)!=null;
    }
    
    protected void getFilterMatchModes(DataTable table){
        if(filterMatchModes==null){
            filterMatchModes = new HashMap<String, String>();
            for(FilterMeta a: (List<FilterMeta>)table.getFilterMetadata()){
                String fieldName = a.getFilterByVE().getExpressionString();
                if(fieldName==null) continue;
                
                fieldName = fieldName.replace("}", "").substring(fieldName.indexOf(".")+1);
                filterMatchModes.put(fieldName, a.getColumn().getFilterMatchMode());
            }
        }
    }

    protected void addFilter(String fieldName, String matchMode, Object filterValue){
        matchMode = matchMode.toUpperCase();

        if("EQ".equals(matchMode))
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.EQ, filterValue));
        else if("LIKE".equals(matchMode))
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LIKE, filterValue));
        else if("LT".equals(matchMode))
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LT, filterValue));
        else if("LTE".equals(matchMode))
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, filterValue));
        else if("GT".equals(matchMode))
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GT, filterValue));
        else if("GTE".equals(matchMode))
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GTE, filterValue));
        else
            logger.warn("**** filterMatchMode not supported: "+ matchMode);
    }
    
    public void onFilter(FilterEvent event){
        if(searchFilters==null) searchFilters = new ArrayList<SearchFilter>();
        searchFilters.clear();

        if(getFiltersFromDynaColumns()) return;
        
        getFilterMatchModes((DataTable)event.getComponent());
        for(Map.Entry<String, Object> filter: event.getFilters().entrySet()){
            String fieldName = filter.getKey();
            Object filterValue = filter.getValue();
            if(filterValue==null) continue;
            
            if(isDateTimeField(fieldName)){
                // support lte & gte for datetime field
                String[] values = filterValue.toString().split("~");
                if(values.length>0){
                    filterValue = TimeUtil.fromString(values[0]);
                    
                    if(filterValue!=null)
                        searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GTE, ((DateTime)filterValue).toDate()));
                }
                
                if(values.length>1){
                    filterValue = TimeUtil.fromString(values[1]);
                    if(filterValue!=null)
                        searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, ((DateTime)filterValue).toDate()));
                }
                continue;
            }

            String matchMode = filterMatchModes.get(fieldName);
            addFilter(fieldName, matchMode, filterValue);
        }
        
        setSiteFilter();
        setHospitalFilter();
    }
    
    protected Page<E> doLoadData(PageRequest pageRequest){
        setSiteFilter();
        setHospitalFilter();
        
        return loadData(pageRequest);
    }
    
    protected Page<E> loadData(PageRequest pageRequest){
        return getDAO().findBySearchFilter(this.searchFilters, pageRequest);
    }
        
    protected LazyDataModel lazyModel;
    protected E selected;

    protected void init(){};
    
    @PostConstruct
    private void doPostConstruct() {
        init();
        onPrepareDataTableModel();
        
        lazyModel = new GenericDataTableModel<E>() {
            @Override
            protected List<E> load(PageRequest pageRequest) {
                Page<E> pg = doLoadData(pageRequest);
                int rowCount = (int) pg.getTotalElements();
                this.setRowCount(rowCount);
                List<E> listObject = pg.getContent();

                return listObject;
            }
        };
    }

    public LazyDataModel getLazyModel() {
        return lazyModel;
    }

    public boolean isDeleteAction(){
        return "Delete".equals(crudActionName);
    }
    
    public String getCRUDActionName(){
        return WebUtil.getMessage(crudActionName);
    }

    public List<E> getItemList() {
        return getDAO().find();
    }
    
    public E getSelected() {
        return selected;
    }

    public void setSelected(E selectedObj) {
        if (selectedObj == null) {
            return;
        }
        selected = selectedObj;
    }

    protected Class<E> getEntityClass(){
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];   
    }
    
    protected String getActionMessage(String actionName){
        
        return WebUtil.getMessage(getEntityClass().getSimpleName())+" "+WebUtil.getMessage(actionName);
    }
    
    public void onBeforeNewObject(E object) {}
    public void onAfterNewObject(E object, boolean isOK) {}

    public void onBeforeUpdateObject(E object) {}
    public void onAfterUpdateObject(E object, boolean isOK) {}
    public void onBeforeDeleteObject(E object) {}
    public void onAfterDeleteObject(E object, boolean isOK) {}
    public void onBeforeSave(E object) {}
    public void onAfterDataChanged(){};

    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = "Create";
        
        Class<E> entityClass = getEntityClass();
        selected = entityClass.newInstance();
        
        onBeforeNewObject(selected);
        //return selected;
    }

    public void prepareEdit() {
        crudActionName = "Edit";
    }

    public void prepareView() {
        crudActionName = "View";
    }

    public void prepareDelete() {
        crudActionName = "Delete";
    }
    
    public void setSelected(int id) {
        selected = getDAO().findById(id);
    }
    
    public void create() {
        boolean isOK = persist(WebUtil.PersistAction.CREATE, getActionMessage("Created"));
        onAfterNewObject(selected, isOK);
    }

    public void save() {
        onBeforeSave(selected);
        if (selected.hashCode()==0 )
            create();
        else
            update();
    }

    public void update() {
        onBeforeUpdateObject(selected);
        boolean isOK = persist(WebUtil.PersistAction.UPDATE, getActionMessage("Updated"));
        onAfterUpdateObject(selected, isOK);
    }

    public void delete(E obj) {
        this.selected = obj;
        delete();
    }

    public void delete() {
        onBeforeDeleteObject(selected);
        boolean isOK = persist(WebUtil.PersistAction.DELETE, getActionMessage("Deleted"));
        onAfterDeleteObject(selected, isOK);
        
        if (!WebUtil.isValidationFailed()) {
            selected = null; // Remove selection
        }
    }

    public String getErrorMessageForDuplicateKey(E object){
        return null;
    }

    public String getKeyFieldNameValue(E object){
        return "";
    }
    
    protected boolean onPersistException(Exception ex, E object){
        return false;
    }

    protected boolean persist3(WebUtil.PersistAction persistAction, String successMessage) {
        if (selected != null) {
            if (persistAction != WebUtil.PersistAction.DELETE) {
                getDAO().save(selected);
            } else {
                getDAO().delete(selected);
            }

            onAfterDataChanged();
            WebUtil.addSuccessMessage(successMessage);
            return true;
        }
        
        return true;
    }
    
    @SuppressWarnings("ThrowableResultIgnored")
    protected boolean persist(WebUtil.PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != WebUtil.PersistAction.DELETE) {
                    getDAO().save(selected);
                } else {
                    getDAO().delete(selected);
                }
                
                onAfterDataChanged();
                WebUtil.addSuccessMessage(successMessage);
                return true;
            } 
            catch (Exception ex) {
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);

                if(onPersistException(ex, selected)) return false;

                if(ex.getClass().equals(DataIntegrityViolationException.class)){
                    DataIntegrityViolationException exDuplicateKey = (DataIntegrityViolationException) ex;
                    if(exDuplicateKey.getRootCause().getMessage().toLowerCase().contains("duplicate ")){
                        String errMessage = getErrorMessageForDuplicateKey(selected);

                        if(errMessage==null)
                            errMessage = getKeyFieldNameValue(selected)+": "+WebUtil.getMessage("PersistenceErrorDuplicateKey");
                        
                        WebUtil.addErrorMessage(errMessage);
                        return false;
                    }
                }
                
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    WebUtil.addErrorMessage(msg);
                } else {
                    WebUtil.addErrorMessage(ex, WebUtil.getMessage("PersistenceErrorOccured"));
                }
            }
        }
        return false;
    }

    public void setSearchFilter(){
        this.searchFilters = SearchFilter.getSearchFiltersFromHttpRequest(searchFilterPrefix);
        
        if(warnOnEmptySearchFilters && (searchFilters==null || searchFilters.isEmpty()))
            WebUtil.addErrorMessageKey("noSearchCondition");
    }
    DataTable dataTable;
    public DataTable getDataTable() {
        return dataTable;
    }
    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }
    
    private List<FieldInfo> columns;
    public List<FieldInfo> getColumns() {
        return columns;
    }
    
    public String getColumnNameList(){
        return null;
    };
    //protected String getDataTableID(){};
    
    protected void onPrepareDataTableModel(){
        updateTableColumns();
    };

    protected void updateTableColumns() {
        String colNameList = getColumnNameList();
        if(colNameList==null) return;
        
/*
        if(colNameList==null){
            logger.error("******* Dynamic Column error: getColumnNameList() returns null, tableID="+getDataTableID());
            return;
        }

        UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(getDataTableID());
        if(table==null){
            logger.error("******* Dynamic Column error: cannot find dataTable UI component, tableID="+getDataTableID());
        }
        else           
            table.setValueExpression("sortBy", null);
*/

        String[] columnNames = colNameList.split(" ");
        columns = new ArrayList<FieldInfo>();   
         
        for(String columnName : columnNames) {
            columns.add(new FieldInfo(columnName.trim()));
        }
    }
            
    public void onToggleColumn(ToggleEvent e) {
        int i = (Integer) e.getData();
        if(i>=columns.size()){
            logger.error("******** DynaColumn onToggleColumn error, index out of range: "+i);
            return;
        }
        
        columns.get(i).setIsVisible(e.getVisibility() == Visibility.VISIBLE);
    }

    public Object getFieldValue(Object entityBean, String fieldName){
        return BeanUtils.getBeanProperty(entityBean, fieldName);
    }

    protected boolean getFiltersFromDynaColumns(){
        if(columns==null || columns.size()<1) return false;
        
        Map<String, Object> params = HttpRequestUtil.getParametersEndWith(":filter");
        for(Map.Entry<String, Object> param: params.entrySet()){
            Object value = param.getValue();
            if(value==null || "".equals(value.toString())) continue;
                    
            String[] keys = param.getKey().split(":");
            int index;
            try{
                index = Integer.parseInt(keys[keys.length-2]);
            }
            catch(Exception ex){
                continue;
            }
            
            FieldInfo col = this.columns.get(index);
            this.addFilter(col.getFieldName(), col.getFilterMatchMode(), value);
        }
        
        return true;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject){
    }
}
