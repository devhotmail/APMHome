package webapp.framework.web.mvc;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.SearchFilterList;
import com.ge.apm.service.utils.SortInfo;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
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
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.service.UserContext;

public abstract class NativeSQLController implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final Logger logger = Logger.getLogger(NativeSQLController.class);

    protected static Page<Map<String, Object>> emptyPage = new PageImpl<Map<String, Object>>(new ArrayList<Map<String, Object>>(), new PageRequest(0,1), 0);    

    protected static final String searchFilterPrefix = ":search_";

    protected String sql;
    protected String countSql;
    protected Map<String, Object> sqlParams = new HashMap<String, Object>();

    protected LazyDataModel lazyModel;

    public LazyDataModel getLazyModel() {
        return lazyModel;
    }

    protected Map<String, Object> selected;

    public Map<String, Object> getSelected() {
        return selected;
    }

    public void setSelected(Map<String, Object> row) {
        this.selected = row;
    }

    protected SearchFilterList searchFilterList;

    public void setSearchFilter() {
        if(searchFilterList==null) searchFilterList = new SearchFilterList();
        
        searchFilterList.setAndFilter(SearchFilter.getSearchFiltersFromHttpRequest(searchFilterPrefix));
    }

    protected void init() {
    }

    private String getDataTableID(){
        return "";
    }
    
    private void clearDataTableSortBy(){
        UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(getDataTableID());
        if(table==null){
            logger.error("******* Dynamic Column error: cannot find dataTable UI component, tableID="+getDataTableID());
        }
        else           
            table.setValueExpression("sortBy", null);
    }
    
    public String getColumnNameList(){
        logger.error("************** WARNING: getColumnNameList() not overrided");
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
        if(searchFilterList==null) searchFilterList = new SearchFilterList();

        if(getFiltersFromDynaColumns()) return;
        
        ArrayList<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
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
                            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GTE, ((DateTime)filterValue).toDate()));
                    }
                    catch(Exception ex){
                        logger.error(ex.getMessage(), ex);
                    }
                    
                }
                
                if(values.length>1){
                    try{
                        filterValue = TimeUtil.fromString("yyyy-MM-dd", values[2]);

                        if(filterValue!=null)
                            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, ((DateTime)filterValue).toDate()));
                    }
                    catch(Exception ex){
                        logger.error(ex.getMessage(), ex);
                    }
                }
                continue;
            }

            searchFilters.add(createSearchFilter(fieldName, matchMode, filterValue));
        }
        
        searchFilterList.setAndFilter(searchFilters);
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

    public String getSearchFilterSQL() {
        if (this.searchFilterList != null && this.searchFilterList.getAndFilter()!=null) {
            String sqlWhere = "";
            for (SearchFilter filter : searchFilterList.getAndFilter()) {
                sqlWhere = sqlWhere + " and " + filter.getConditionSql();
            }

            return sqlWhere;
        } else {
            return "";
        }
    }

    protected Map<String, Object> getSqlParams() {
        if (this.searchFilterList != null && this.searchFilterList.getAndFilter()!=null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.putAll(sqlParams);

            for (SearchFilter filter : searchFilterList.getAndFilter()) {
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

    protected void onBeforeExecSQL() {
    }
    protected List<SortInfo> getSortInfo(PageRequest pageRequest) {
        List<SortInfo> sortInfoList = new ArrayList<SortInfo>();
        if(pageRequest.getSort()==null) return sortInfoList;
        
        Iterator<Order> iteratorOrderBy = pageRequest.getSort().iterator();
        while(iteratorOrderBy.hasNext()){
            Order order = iteratorOrderBy.next();
            SortInfo sortInfo = new SortInfo(order.getProperty(), order.isAscending());
            sortInfoList.add(sortInfo);
        }
        
        return sortInfoList;
    }
    
    protected String getOrderBySQL(PageRequest pageRequest) {
        List<SortInfo> sortInfoList = getSortInfo(pageRequest);
        if(sortInfoList.size()<1)
            return "";
        else
            return "order by " + sortInfoList.get(0).getOrderBySql();
        
    }

    private List<FieldInfo> fieldList;
    public List<FieldInfo> getFieldList() {
        return fieldList;
    }
    
    
    @PostConstruct
    private void doPostConstruct() {
        init();
        onPrepareDataTableModel();

        lazyModel = new NativeSQLDataTableModel<Map<String, Object>>() {
            @Override
            protected List<Map<String, Object>> load(PageRequest pageRequest) {
                onBeforeExecSQL();
                
                Map<String, Object> params = getSqlParams();

/*
                String serchFilterSQL = getSearchFilterSQL();
                String orderBySQL = getOrderBySQL(pageRequest);
                Page<Map<String, Object>> page = loadData(pageRequest, serchFilterSQL, orderBySQL, params);
*/
                List<SortInfo> sortInfo = getSortInfo(pageRequest);
                UserAccount user = UserContextService.getCurrentUserAccount();
                String lang = UserContext.getLocale().getLanguage();
                
                Page<Map<String, Object>> page = loadData(pageRequest, user, lang, fieldList, searchFilterList, sortInfo, params);
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
    }

/*
    protected Page<Map<String, Object>> loadData(PageRequest pageRequest, String searchFilterSQL, String orderBySQL, Map<String, Object> params) {
        //update baseSQL: to fill in :#searchFilter and :#orderBy parts.
        String theSQL = sql.replace(":#searchFilter", searchFilterSQL).replace(":#orderBy", orderBySQL);
        
        //update countSQL: to fill in :#searchFilter part. there should be no :#orderBy in countSQL
        String theCountSQL = (countSql == null) ? "select count(*) from (" + theSQL + ") _count" : countSql;
        params.put("_sql", theCountSQL);

        //get count of rows: execute the countSQL
        List<Map<String, Object>> result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
        int rowCount = Integer.parseInt(result.get(0).values().toArray()[0].toString());

        // add Page limit to the sql
        theSQL = theSQL + " limit :#_pageStart,:#_pageSize";
        params.put("_sql", theSQL);
        params.put("_pageStart", pageRequest.getPageNumber() * pageRequest.getPageSize());
        params.put("_pageSize", pageRequest.getPageSize());
        result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);

        return new PageImpl<Map<String, Object>>(result, pageRequest, rowCount);
    }
*/
    
    protected Page<Map<String, Object>> loadData(PageRequest pageRequest,UserAccount user, String lang, List<FieldInfo> fieldList, SearchFilterList searchInfo, List<SortInfo> sortInfo, Map<String, Object> params) {
/*
        logger.warn("***** WARNING: loadData method in NativeSQLController not overrided.");

        //update baseSQL: to fill in :#searchFilter and :#orderBy parts.
        String theSQL = sql.replace(":#searchFilter", this.getSearchFilterSQL()).replace(":#orderBy", this.getOrderBySQL(pageRequest));
        
        //update countSQL: to fill in :#searchFilter part. there should be no :#orderBy in countSQL
        String theCountSQL = (countSql == null) ? "select count(*) from (" + theSQL + ") _count" : countSql;
        params.put("_sql", theCountSQL);

        //get count of rows: execute the countSQL
        List<Map<String, Object>> result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
        int rowCount = Integer.parseInt(result.get(0).values().toArray()[0].toString());

        // add Page limit to the sql
        theSQL = theSQL + " limit :#_pageStart,:#_pageSize";
        params.put("_sql", theSQL);
        params.put("_pageStart", pageRequest.getPageNumber() * pageRequest.getPageSize());
        params.put("_pageSize", pageRequest.getPageSize());
        result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
*/
        return null;
    }
    
}
