package webapp.framework.web.mvc;

import com.ge.apm.dao.DataTableConfigRepository;
import com.ge.apm.domain.DataTableConfig;
import com.ge.apm.view.sysutil.AppContextService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Order;
import webapp.framework.broker.SiBroker;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

public abstract class SqlConfigurableDataController implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final Logger logger = Logger.getLogger(SqlConfigurableDataController.class);

    protected static Page<Map<String, Object>> emptyPage = new PageImpl<Map<String, Object>>(new ArrayList<Map<String, Object>>(), new PageRequest(0,1), 0);    

    public static Map<String, DataTableConfig> dataTableConfig = new HashMap<>();
    public static void reLoadDataTableConfig(){
        DataTableConfigRepository daoDataTableConfig = WebUtil.getBean(DataTableConfigRepository.class);
        List<DataTableConfig> dataTableConfigData = daoDataTableConfig.loadDataTableConfigData();

        dataTableConfig.clear();
        for(DataTableConfig item: dataTableConfigData){
            dataTableConfig.put(item.getDataTableName(), item);
        }
    }

    protected Map<String, Object> sqlParams = new HashMap<String, Object>();
    
    protected Map<String, LazyDataModel> lazyModels = new HashMap<String, LazyDataModel>();
    protected Map<String, List<Map<String, Object>>> listModels = new HashMap<String, List<Map<String, Object>>>();

    public void init(){
    }

    private static final String defaultDataModel = "__default";
    
    public LazyDataModel getLazyModel() {
        return getLazyModel(defaultDataModel);
    }
    
    public List<Map<String, Object>> getListModel() {
        return getListModel(defaultDataModel);
    }

    protected Map<String, Object> selected;

    public Map<String, Object> getSelected() {
        return selected;
    }

    public void setSelected(Map<String, Object> row) {
        this.selected = row;
    }

    protected List<Map<String, Object>> buildListModel(String dataTableName){
        return null;
    }
    
    public List<Map<String, Object>> getListModel(String dataTableName) {
        List<Map<String, Object>> list = listModels.get(dataTableName);
        if(list==null){
            list = buildListModel(dataTableName);
            listModels.put(dataTableName, list);
        }
        return list;
    }
    
    protected LazyDataModel buildLazyModel(String dataTableName){
        NativeSQLDataTableModel lazyModel = new NativeSQLDataTableModel<Map<String, Object>>() {
            @Override
            protected List<Map<String, Object>> load(PageRequest pageRequest) {
                Map<String, Object> params = getSearchFilterParams();

                String serchFilterSQL = getSearchFilterSQL(this.dataTableName);
                String orderBySQL = getOrderBySQL(pageRequest);
                Page<Map<String, Object>> page = loadData(this.dataTableName, pageRequest, serchFilterSQL, orderBySQL, params);
                if(page==null){
                    page = emptyPage;
                    this.setRowCount(0);
                    return page.getContent();
                }
                else{
                    int rowCount = (int) page.getTotalElements();
                    this.setRowCount(rowCount);
                    return page.getContent();
                }
            }
        };

        lazyModel.setDataTableName(dataTableName);
        return lazyModel;
    }
    
    public LazyDataModel getLazyModel(String dataTableName) {
        LazyDataModel lazyDataModel = lazyModels.get(dataTableName);
        if(lazyDataModel==null){
            lazyDataModel = buildLazyModel(dataTableName);
            lazyModels.put(dataTableName, lazyDataModel);
        }
        return lazyDataModel;
    }

    public void setSelected(String dataTableName) {
    }
    
    protected List<SearchFilter> searchFilterList;

    public void setSearchFilter() {
        searchFilterList = SearchFilter.getSearchFiltersFromHttpRequest(SearchFilter.searchFilterPrefix);
    }

    public String getColumnNameList(){
        return "";
    };

    protected boolean getFiltersFromDynaColumns(){
        return false;
    }
    
    public Map<String, String> filterMatchModes;

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
    
    protected boolean isDateTimeField(String matchMode){
        return matchMode.contains("-DATETIME");
    }
    
    protected SearchFilter createSearchFilter(String fieldName, String matchMode, Object filterValue){
        matchMode = matchMode.toUpperCase();
        
        if("EQ".equals(matchMode))
            return new SearchFilter(fieldName, SearchFilter.Operator.EQ, filterValue);
        else if("LIKE".equals(matchMode))
            return new SearchFilter(fieldName, SearchFilter.Operator.LIKE, filterValue);
        else if("LT".equals(matchMode))
            return new SearchFilter(fieldName, SearchFilter.Operator.LT, filterValue);
        else if("LTE".equals(matchMode))
            return new SearchFilter(fieldName, SearchFilter.Operator.LTE, filterValue);
        else if("GT".equals(matchMode))
            return new SearchFilter(fieldName, SearchFilter.Operator.GT, filterValue);
        else if("GTE".equals(matchMode))
            return new SearchFilter(fieldName, SearchFilter.Operator.GTE, filterValue);
        else
            logger.warn("**** filterMatchMode not supported: "+ matchMode);
        
        return null;
    }
    
    public void onFilter(FilterEvent event){
        if(searchFilterList==null) searchFilterList = new ArrayList<SearchFilter>();

        if(getFiltersFromDynaColumns()) return;
        
        getFilterMatchModes((DataTable)event.getComponent());
        for(Map.Entry<String, Object> filter: event.getFilters().entrySet()){
            String fieldName = filter.getKey();
            Object filterValue = filter.getValue();
            if(filterValue==null) continue;
            
            String matchMode = filterMatchModes.get(fieldName);

            if(isDateTimeField(matchMode)){
                // support lte & gte for datetime field
                String[] values = filterValue.toString().split(" ");
                if(values.length>0){
                    try{
                        filterValue = TimeUtil.fromString("yyyy-MM-dd", values[0]);
                        if(filterValue!=null)
                            searchFilterList.add(new SearchFilter(fieldName, SearchFilter.Operator.GTE, ((DateTime)filterValue).toDate()));
                    }
                    catch(Exception ex){
                        logger.error(ex.getMessage(), ex);
                    }
                    
                }
                
                if(values.length>1){
                    try{
                        filterValue = TimeUtil.fromString("yyyy-MM-dd", values[2]);

                        if(filterValue!=null)
                            searchFilterList.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, ((DateTime)filterValue).toDate()));
                    }
                    catch(Exception ex){
                        logger.error(ex.getMessage(), ex);
                    }
                }
                continue;
            }

            searchFilterList.add(createSearchFilter(fieldName, matchMode, filterValue));
        }
    }
    
    protected void onPrepareDataTableModel(){
        updateTableColumns();
    };

    protected void updateTableColumns() {
        String colNameList = getColumnNameList();
        if(colNameList==null){
            logger.error("******* Dynamic Column error: getColumnNameList() returns null");
            return;
        }

        if(fieldList==null) fieldList = new ArrayList<FieldInfo>();
        
        String[] columnNames = colNameList.split(" ");
        for(String columnName : columnNames) {
            fieldList.add(new FieldInfo(columnName.trim()));
        }
    }
            
    public void onToggleColumn(ToggleEvent e) {
        int i = (Integer) e.getData();
        if(i>=fieldList.size()){
            logger.error("******** DynaColumn onToggleColumn error, index out of range: "+i);
            return;
        }
        
        fieldList.get(i).setIsVisible(e.getVisibility() == Visibility.VISIBLE);
    }
    
    public Object getFieldValue(Map<String, Object> rowData, String fieldName){
        return rowData.get(fieldName);
    }

    public String getSearchFilterSQL(String dataTableName) {
        String sqlWhere = "";
        
        if (this.searchFilterList != null) {
            sqlWhere = "";
            for (SearchFilter filter : searchFilterList) {
                sqlWhere = sqlWhere + " and " + filter.getConditionSql();
            }
        }
        
        if(dataTableConfig.size()<1) reLoadDataTableConfig();
        DataTableConfig config= dataTableConfig.get(dataTableName);
        
        if(config.getIsDefaultEmptyResult() && "".equals(sqlWhere))
            return " and false";
        else
            return sqlWhere;
    }

    protected Map<String, Object> getSearchFilterParams() {
        if (this.searchFilterList != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.putAll(sqlParams);

            for (SearchFilter filter : searchFilterList) {
                if (filter.operator.equals(SearchFilter.Operator.LIKE)) {
                    params.put(filter.getSqlParamName(), "%" + filter.value + "%");
                } else {
                    params.put(filter.getSqlParamName(), filter.value);
                }
            }
            return params;
        } else {
            return this.sqlParams;
        }
    }

    protected List<String> getSortInfo(PageRequest pageRequest) {
        List<String> sortInfoList = new ArrayList<String>();
        if(pageRequest.getSort()==null) return sortInfoList;
        
        Iterator<Order> iteratorOrderBy = pageRequest.getSort().iterator();
        while(iteratorOrderBy.hasNext()){
            Order order = iteratorOrderBy.next();
            String sortBy = order.getProperty();
            if(order.isAscending())
                sortBy = sortBy + " ASC";
            else
                sortBy = sortBy + " DESC";
                
            sortInfoList.add(sortBy);
        }
        
        return sortInfoList;
    }
    
    protected String getOrderBySQL(PageRequest pageRequest) {
        List<String> sortInfoList = getSortInfo(pageRequest);
        if(sortInfoList.size()<1)
            return "";
        else
            return "order by " + sortInfoList.get(0); //currently only support order by one field
    }

    private List<FieldInfo> fieldList;
    public List<FieldInfo> getFieldList() {
        return fieldList;
    }
    
    @PostConstruct
    private void doPostConstruct() {
        init();
        onPrepareDataTableModel();

    }

    public String getSQL(String dataTableName, String sql){
        return sql;
    }

    private String getSQL(String dataTableName){
        if(dataTableConfig.size()<1) reLoadDataTableConfig();

        DataTableConfig config= dataTableConfig.get(dataTableName);
        return getSQL(dataTableName, config.getDataSql());
    }

    public String getCountSQL(String dataTableName, String countSQL){
        return countSQL;
    }
    
    private String getCountSQL(String dataTableName){
        if(dataTableConfig.size()<1) reLoadDataTableConfig();

        DataTableConfig config= dataTableConfig.get(dataTableName);
        return getCountSQL(dataTableName, config.getDataCountSql());
    }
    
    public void getSqlParam(String dataTableName, Map<String, Object> params){
    }
    
    protected Page<Map<String, Object>> loadData(String dataTableName, PageRequest pageRequest, String searchFilterSQL, String orderBySQL, Map<String, Object> params) {
        String sql = getSQL(dataTableName);
        String countSql = getCountSQL(dataTableName);
        getSqlParam(dataTableName, params);
        //update baseSQL: to fill in :#searchFilter and :#orderBy parts.
        String theSQL = sql.replace(":#searchFilter", searchFilterSQL).replace(":#orderBy", orderBySQL);
        
        //update countSQL: to fill in :#searchFilter part. there should be no :#orderBy in countSQL
        String theCountSQL = (countSql == null) ? "select count(*) from (" + theSQL + ") _count" : countSql;
        theCountSQL = theCountSQL.replace(":#searchFilter", searchFilterSQL);
        params.put("_sql", theCountSQL);

        //get count of rows: execute the countSQL
        List<Map<String, Object>> result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
        int rowCount = Integer.parseInt(result.get(0).values().toArray()[0].toString());

        // add Page limit to the sql
        theSQL = theSQL + AppContextService.getSqlPagingTemplate();
        params.put("_sql", theSQL);
        params.put("_pageStart", pageRequest.getPageNumber() * pageRequest.getPageSize());
        params.put("_pageSize", pageRequest.getPageSize());
        result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);

        return new PageImpl<Map<String, Object>>(result, pageRequest, rowCount);
    }
    
}
