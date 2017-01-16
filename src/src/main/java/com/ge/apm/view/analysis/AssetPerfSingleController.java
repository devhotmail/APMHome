package com.ge.apm.view.analysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.ChartSeries;
import org.slf4j.LoggerFactory;

import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import java.time.Year;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ge.apm.view.analysis.Row; 

@ManagedBean
@ViewScoped
public class AssetPerfSingleController implements ServerEventInterface {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AssetPerfSingleController.class);
    
    private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    private final int clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();
    
    private HashMap<String, Object> sqlParams = new HashMap<>(); 

    private static final String deviceROIlg_1 = WebUtil.getMessage("deviceROIlg_1");
    private static final String deviceROIlg_2 = WebUtil.getMessage("deviceROIlg_2");

    // Dashboard Parameters
    private String valueProfit = null;
    private String valueRevenue = null;
    private String valueCost = null;
    private BarChartModel bcProfit = null;

    // Table Components
    private List<Row> assetDashBoard = null;
    private HashMap<String, String> yearList = new HashMap<>();

    private int MAX_INTERVAL=4;
    private static final String MONTH_FORMAT = "yyyy-MM";
    private static final String DAY_FORMAT = "yyyy-MM-dd";

    private Date startDate;
    private Date endDate;
    private Date currentDate;

    private int targetYear = Year.now().getValue();
    private String selectedYear = Integer.toString(targetYear);

    private String assetName = WebUtil.getMessage("preventiveMaintenanceAnalysis_allDevices");
    private int assetId = -1;
    private int activeTab = 1;

    Map<Integer, String> i18nMessageDept = new HashMap<Integer, String>();

    @PostConstruct
    public void init() {

        valueProfit = "";
        valueRevenue = "";
        valueCost = "";

        bcProfit = new BarChartModel();
        bcProfit.setBarWidth(50);
        bcProfit.setLegendPosition("ne");
        bcProfit.setExtender("barAnnualRevenue");
        assetDashBoard = new ArrayList<Row>();

        Calendar currentCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.YEAR, -1);

        startDate = startCal.getTime();
        endDate = currentCal.getTime();
        Date currentDate = currentCal.getTime();

        int year = targetYear;
        for (int index = 0; index < MAX_INTERVAL; index++) {
            yearList.put(Integer.toString(year),Integer.toString(year));
            year -= 1;
        }

        FieldValueMessageController fieldMsg = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
        List<I18nMessage> messages = fieldMsg.getFieldValueList("clinicalDeptId");
        int i = 0;
        for (I18nMessage local : messages)
            i18nMessageDept.put(++i, local.getValue());


        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("asset_id") != null 
            && FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("asset_name") != null) {

        	assetId = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("asset_id"));
            assetName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("asset_name");
            deviceQuery(startDate, endDate, currentDate);
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;

        if(asset == null) return;

        this.assetId = asset.getId();
        this.assetName = asset.getName();
        
        WebUtil.navigateTo("/portal/analysis/assetPerfSingle.xhtml?faces-redirect=true&asset_id=" + assetId + "&asset_name=" + assetName);

    }


    public void submit() {
    	
        currentDate = Calendar.getInstance().getTime();
        deviceQuery(startDate, endDate, currentDate);
    }


    public void submit(String targetYear) {
    	
        this.targetYear = Integer.parseInt(targetYear);
        currentDate = Calendar.getInstance().getTime();
        deviceQuery(startDate, endDate, currentDate);
    }

    public void onSelect (int i) {

        activeTab = i;
        deviceTable(startDate, endDate, currentDate, activeTab);

    }

    private String DB0TL
            = "SELECT name, serial_num, clinical_dept_name "
            + "FROM asset_info "
            + "WHERE id = :#assetId ";

           //bigint
    private String DB1TLYEAR
            = "SELECT key, SUM(right_table.price_amount) revenue, COUNT(right_table) scan, SUM(expose_count) expo "
            + "FROM (SELECT id, name, serial_num FROM asset_info WHERE id = :#assetId) left_table "
            + "LEFT JOIN (SELECT TO_CHAR(exam_date, 'yyyy') AS key, expose_count, price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY key "
            + "ORDER BY key ";

    private String DB2TLYEAR
            = "SELECT deprecate_amount depre "
            + "FROM asset_depreciation "
            + "WHERE asset_id = :#assetId ";

    private String DB3TLYEAR
            = "SELECT key, COUNT(right_table) repair, SUM(right_table.total_price) price, SUM(diff)/60/60 dt "
            + "FROM (SELECT id FROM asset_info WHERE id = :#assetId) left_table "
            + "LEFT JOIN (SELECT TO_CHAR(create_time, 'yyyy') AS key, total_price, EXTRACT(EPOCH FROM confirmed_up_time-confirmed_down_time) diff, asset_id FROM work_order WHERE is_closed = true AND EXTRACT(YEAR FROM create_time) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY key "
            + "ORDER BY key ";

           //bigint
    private String DB1TLMONTH
            = "SELECT key, SUM(right_table.price_amount) revenue, COUNT(right_table) scan, SUM(expose_count) expo "
            + "FROM (SELECT id, name, serial_num FROM asset_info WHERE id = :#assetId) left_table "
            + "LEFT JOIN (SELECT TO_CHAR(exam_date, 'yyyy-mm') AS key, expose_count, price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY key "
            + "ORDER BY key ";

    private String DB2TLMONTH
            = "SELECT deprecate_amount/12 depre "
            + "FROM asset_depreciation "
            + "WHERE asset_id = :#assetId ";

    private String DB3TLMONTH
            = "SELECT key, COUNT(right_table) repair, SUM(right_table.total_price) price, SUM(diff)/60/60 dt "
            + "FROM (SELECT id FROM asset_info WHERE id = :#assetId) left_table "
            + "LEFT JOIN (SELECT TO_CHAR(create_time, 'yyyy-mm') AS key, total_price, EXTRACT(EPOCH FROM confirmed_up_time-confirmed_down_time) diff, asset_id FROM work_order WHERE is_closed = true AND EXTRACT(YEAR FROM create_time) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY key "
            + "ORDER BY key ";

            //bigint
    private String DB1TLDAY
            = "SELECT key, SUM(right_table.price_amount) revenue, COUNT(right_table) scan, SUM(expose_count) expo "
            + "FROM (SELECT id, name, serial_num FROM asset_info WHERE id = :#assetId) left_table "
            + "LEFT JOIN (SELECT TO_CHAR(exam_date, 'yyyy-mm-dd') AS key, expose_count, price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY key "
            + "ORDER BY key ";

    private String DB2TLDAY
            = "SELECT deprecate_amount/365 depre "
            + "FROM asset_depreciation "
            + "WHERE asset_id = :#assetId ";

    private String DB3TLDAY
            = "SELECT key, COUNT(right_table) repair, SUM(right_table.total_price) price, SUM(diff)/60/60 dt "
            + "FROM (SELECT id FROM asset_info WHERE id = :#assetId) left_table "
            + "LEFT JOIN (SELECT TO_CHAR(create_time, 'yyyy-mm-dd') AS key, total_price, EXTRACT(EPOCH FROM confirmed_up_time-confirmed_down_time) diff, asset_id FROM work_order WHERE is_closed = true AND EXTRACT(YEAR FROM create_time) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY key "
            + "ORDER BY key ";

    // Getters & Setters
    public Date getStartDate() {

        return startDate;
    }

    public void setStartDate(Date startDate) {

        this.startDate = startDate;
        sqlParams.put("startDate", startDate);
    }

    public Date getEndDate() {

        return endDate;
    }

    public void setEndDate(Date endDate) {

        this.endDate = endDate;
        sqlParams.put("endDate", endDate);
    }

    public List<Row> getAssetDashboard () {

        return assetDashBoard;
    }

    public String getAssetName() {

        return assetName;
    }

    public int getAssetId() {

        return assetId;
    }

    public String getSelectedYear() {

        return selectedYear;
    }

    public void setSelectedYear(String selectedYear) {

        this.selectedYear = selectedYear;
    }
    
    public Map<String, String> getYearList() {
        return yearList;
    }

    public void onSelectedYearChange() {

        targetYear = Integer.parseInt(selectedYear);
        //sqlParams.put("targetYear", this.targetYear);
        //startMonth = getStartMonth();

    }

    public String getValueProfit() {

        return valueProfit;
    }

    public String getValueRevenue() {

        return valueRevenue;
    }

    public String getValueCost() {

        return valueCost;
    }

    public BarChartModel getBcProfit() {

        return bcProfit;
    }

    public int getActiveTab() {

        return activeTab;
    }

    private void deviceChart_1(Date startDate, Date endDate, Date currentDate) {

        ChartSeries cst_1 = new BarChartSeries();
        cst_1.setLabel(deviceROIlg_1);
        ChartSeries cst_2 = new BarChartSeries();
        cst_2.setLabel(deviceROIlg_2);

        sqlParams.put("_sql", DB1TLMONTH);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
        List<Map<String, Object>> rs_1 = NativeSqlUtil.queryForList(DB1TLMONTH, sqlParams);

        sqlParams.put("_sql", DB2TLMONTH);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);        
        List<Map<String, Object>> rs_2 = NativeSqlUtil.queryForList(DB2TLMONTH, sqlParams);

        sqlParams.put("_sql", DB3TLMONTH);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);        
        List<Map<String, Object>> rs_3 = NativeSqlUtil.queryForList(DB3TLMONTH, sqlParams);
        
        double revenue;
        double cost;
        double price;
        double depre = 0.0;
        double profit;
        String date;

        if (!rs_2.isEmpty())
            depre = rs_2.get(0).get("depre")!=null ? (double)rs_2.get(0).get("depre") : 0.0;

        Map<String, Object> rs_1_map = new HashMap<String, Object>();
        Map<String, Object> rs_3_map = new HashMap<String, Object>();

        if (!rs_1.isEmpty())
            for (Map<String, Object> item : rs_1) {
                rs_1_map.put((String)item.get("key"), item.get("revenue") != null ? (Double) item.get("revenue") : 0);
            }

        if (!rs_3.isEmpty())
            for (Map<String, Object> item : rs_3) {
                rs_3_map.put((String)item.get("key"), item.get("price") != null ? (Double) item.get("price") : 0);
            }


        DateTime startJoda = new DateTime(targetYear+ "-1");
        DateTime endJoda;

        for (endJoda = startJoda.plusYears(1); startJoda.isBefore(endJoda); startJoda = startJoda.plusMonths(1)) {
                
                date = startJoda.toString(MONTH_FORMAT);

                if (rs_1_map.containsKey(date))
                    revenue = rs_1_map.get(date)!=null ? (double)rs_1_map.get(date) : 0.0;
                else   
                    revenue = 0.0;

                if (rs_3_map.containsKey(date))
                    price = rs_3_map.get(date)!=null ? (double)rs_3_map.get(date) : 0.0;
                else   
                    price = 0.0;

                cost = depre + price;

                profit = revenue - cost;

                cst_1.set(date, revenue);
                cst_2.set(date, profit);

        }

        bcProfit.clear();
        bcProfit.addSeries(cst_1);
        bcProfit.addSeries(cst_2);

    }


    private void deviceTable (Date startDate, Date endDate, Date currentDate, int i) {

        List<Map<String, Object>> rs_0;
        List<Map<String, Object>> rs_1;
        List<Map<String, Object>> rs_2;
        List<Map<String, Object>> rs_3;

        sqlParams.put("_sql", DB0TL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        rs_0 = NativeSqlUtil.queryForList(DB0TL, sqlParams);

        switch (i) {

        case 0:

            sqlParams.put("_sql", DB1TLDAY);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
            rs_1 = NativeSqlUtil.queryForList(DB1TLDAY, sqlParams);

            sqlParams.put("_sql", DB2TLDAY);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);             
            rs_2 = NativeSqlUtil.queryForList(DB2TLDAY, sqlParams);

            sqlParams.put("_sql", DB3TLDAY);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);             
            rs_3 = NativeSqlUtil.queryForList(DB3TLDAY, sqlParams);

            break;

        case 3:
        case 2:

            sqlParams.put("_sql", DB1TLYEAR);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
            rs_1 = NativeSqlUtil.queryForList(DB1TLYEAR, sqlParams);

            sqlParams.put("_sql", DB2TLYEAR);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);             
            rs_2 = NativeSqlUtil.queryForList(DB2TLYEAR, sqlParams);

            sqlParams.put("_sql", DB3TLYEAR);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);             
            rs_3 = NativeSqlUtil.queryForList(DB3TLYEAR, sqlParams);

            break;

        default:

            sqlParams.put("_sql", DB1TLMONTH);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
            rs_1 = NativeSqlUtil.queryForList(DB1TLMONTH, sqlParams);

            sqlParams.put("_sql", DB2TLMONTH);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);             
            rs_2 = NativeSqlUtil.queryForList(DB2TLMONTH, sqlParams);

            sqlParams.put("_sql", DB3TLMONTH);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);             
            rs_3 = NativeSqlUtil.queryForList(DB3TLMONTH, sqlParams);

            break;
        }

        double depre = 0.0;
        String name = "";
        String serial_num = "";
        String clinical_dept_name = "";

        if (!rs_0.isEmpty()) {
            name = rs_0.get(0).get("name")!=null ? (String)rs_0.get(0).get("name") : "";
            serial_num = rs_0.get(0).get("serial_num")!=null ? (String)rs_0.get(0).get("serial_num") : "";
            clinical_dept_name = (String)rs_0.get(0).get("clinical_dept_name");
        }

        if (!rs_2.isEmpty())
            depre = rs_2.get(0).get("depre")!=null ? (double)rs_2.get(0).get("depre") : 0.0;

        Map<String, Row> rs_1_map = new HashMap<String, Row>();
        Map<String, Row> rs_3_map = new HashMap<String, Row>();
        
        if (!rs_1.isEmpty())
            for (Map<String, Object> item : rs_1) {
                rs_1_map.put((String)item.get("key"), new Row(item));
            }

        if (!rs_3.isEmpty())
            for (Map<String, Object> item : rs_3) 
                rs_3_map.put((String)item.get("key"), new Row(item, depre));



        switch (i) {

        case 3: 
            deviceValue(name, serial_num, clinical_dept_name, depre, rs_1_map, rs_3_map);
            break;

        case 0:

            deviceTableDay(name, serial_num, clinical_dept_name, depre, rs_1_map, rs_3_map);   
            break;

        case 2:

            deviceTableYear(name, serial_num, clinical_dept_name, depre, rs_1_map, rs_3_map);  
            break;

        default:

            deviceTableMonth(name, serial_num, clinical_dept_name, depre, rs_1_map, rs_3_map);  
            break;
        }
        


    }


    private void deviceTableMonth (String name, String serial_num, String clinical_dept_name, double depre, 
        Map<String, Row> rs_1_map, Map<String, Row> rs_3_map) {

        String date;
        DateTime startJoda = new DateTime(targetYear + "-1");
        DateTime endJoda;

        assetDashBoard.clear();

        for (endJoda = startJoda.plusYears(1); startJoda.isBefore(endJoda); startJoda = startJoda.plusMonths(1)) {

            date = startJoda.toString(MONTH_FORMAT);
            assetDashBoard.add( new Row(name, serial_num, clinical_dept_name, date, rs_1_map.get(date), rs_3_map.get(date), depre) );

        }

    }

    private void deviceTableDay (String name, String serial_num, String clinical_dept_name, double depre, 
        Map<String, Row> rs_1_map, Map<String, Row> rs_3_map) {

        String date;
        DateTime startJoda = new DateTime(targetYear + "-1-1");
        DateTime endJoda;

        assetDashBoard.clear();
        for (endJoda = startJoda.plusYears(1); startJoda.isBefore(endJoda); startJoda = startJoda.plusDays(1)) {

            date = startJoda.toString(DAY_FORMAT);
            assetDashBoard.add( new Row(name, serial_num, clinical_dept_name, date, rs_1_map.get(date), rs_3_map.get(date), depre) );

        }

    }

    private void deviceTableYear (String name, String serial_num, String clinical_dept_name, double depre, 
        Map<String, Row> rs_1_map, Map<String, Row> rs_3_map) {

        String date = String.valueOf(targetYear);
        Row tableRow = new Row(name, serial_num, clinical_dept_name, date, rs_1_map.get(date), rs_3_map.get(date), depre);

        assetDashBoard.clear();
        assetDashBoard.add(tableRow);

    }

    private void deviceValue (String name, String serial_num, String clinical_dept_name, double depre, 
        Map<String, Row> rs_1_map, Map<String, Row> rs_3_map) {

        String date = String.valueOf(targetYear);
        Row tableRow = new Row(name, serial_num, clinical_dept_name, date, rs_1_map.get(date), rs_3_map.get(date), depre);

        valueProfit = tableRow.getProfit();
        valueRevenue = tableRow.getRevenue();
        valueCost = tableRow.getCost();

    }



    private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

        sqlParams.put("hospital_id", hospital_id);
        sqlParams.put("clinical_dept_id", clinical_dept_id);
        sqlParams.put("startDate", startDate);
        sqlParams.put("endDate", endDate);
        sqlParams.put("currentDate", currentDate);
        sqlParams.put("targetYear", targetYear);
        sqlParams.put("assetId", assetId);

        deviceChart_1(startDate, endDate, currentDate);
        deviceTable(startDate, endDate, currentDate, 3);
        deviceTable(startDate, endDate, currentDate, activeTab);

    }

}
