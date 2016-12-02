package com.ge.apm.view.analysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

import webapp.framework.dao.NativeSqlUtil;
import java.time.Year;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;
import com.ge.apm.domain.AssetInfo;

@ManagedBean
@ViewScoped
public class AssetPerfAllController implements Serializable, ServerEventInterface {

    private static final long serialVersionUID = 1L;

    // Dashboard Parameters
    private String topAsset = null;
    private String topDept = null;

    // Table Components
    private List<Row> assetDashBoard = null; 
    
	private int hospitalId = -1;
	private int clinical_dept_id = -1;
	private Date startDate;
	private Date endDate;
	private Date currentDate;
    private int targetYear = Year.now().getValue();

    private String assetName = WebUtil.getMessage("preventiveMaintenanceAnalysis_allDevices"); 

    public class Row {

        private String name;
        private String serial_num;
        private String clinical_dept_name;
        private String revenue;
        private String scan;
        private String expo;
        private String cost;
        private String profit;
        private String repair;
        private String dt;


        public Row (String name, String serial_num, String clinical_dept_name, String revenue, String scan,
                    String expo, String cost, String profit, String repair, String dt) {

            this.name = name;
            this.serial_num = serial_num;
            this.clinical_dept_name = clinical_dept_name;
            this.revenue = revenue;
            this.scan = scan;
            this.expo = expo;
            this.cost = cost;
            this.profit = profit;
            this.repair = repair;
            this.dt = dt;
        }

        public String getName() {

            return name;
        }

        public String getSerial_num() {
            
            return serial_num;
        }

        public String getclinical_dept_name() {
            
            return clinical_dept_name;
        }

        public String getRevenue() {
            
            return revenue;
        }

        public String getScan() {
            
            return scan;
        }

        public String getExpo() {
            
            return expo;
        }

        public String getCost() {
            
            return cost;
        }

        public String getProfit() {
            
            return profit;
        }

        public String getRepair() {
            
            return repair;
        }

        public String getDt() {
            
            return dt;
        }
        
    }


    @PostConstruct
    public void init() {

        assetDashBoard = new ArrayList<Row>();

        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();

        Calendar currentCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.YEAR, -1);

        startDate = startCal.getTime();
        endDate = currentCal.getTime();
        Date currentDate = currentCal.getTime();

        deviceQuery(startDate, endDate, currentDate);

    }

    
    public void submit() {

        currentDate = Calendar.getInstance().getTime(); 
        deviceQuery(startDate, endDate, currentDate);
    }

            //bigint
    private String DB1TL
            = "SELECT left_table.name, serial_num, clinical_dept_id, SUM(right_table.price_amount) revenue, COUNT(right_table) scan, SUM(expose_count) expo "
            + "FROM (SELECT id, name, serial_num, clinical_dept_id FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT expose_count, price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name, serial_num, clinical_dept_id "
            + "ORDER BY left_table.name ";

    private String DB2TL 
            = "SELECT left_table.name, deprecate_amount depre "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN asset_depreciation "    
            + "ON left_table.id = asset_depreciation.asset_id "    
            + "ORDER BY left_table.name";

    private String DB3TL 
            = "SELECT left_table.name, COUNT(right_table) repair, SUM(right_table.total_price) price, SUM(diff)/60/60 dt "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT total_price, EXTRACT(EPOCH FROM confirmed_up_time-confirmed_down_time) diff, asset_id FROM work_order WHERE is_closed = true AND EXTRACT(YEAR FROM create_time) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

    private String MAX1TL
            = "SELECT asset_group FROM "
            + "( SELECT left_table.asset_group, SUM(right_table.price_amount) "
            + "FROM (SELECT id, asset_group FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY asset_group ) as t1 "
            + "WHERE t1.sum = ( "
            + "SELECT MAX(sum) FROM (SELECT left_table.asset_group, SUM(right_table.price_amount) "
            + "FROM (SELECT id, asset_group FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY asset_group) as t2 )";

    private String MAX2TL
            = "SELECT clinical_dept_id FROM "
            + "( SELECT left_table.clinical_dept_id, SUM(right_table.price_amount) "
            + "FROM (SELECT id, clinical_dept_id FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY clinical_dept_id ) as t1 "
            + "WHERE t1.sum = ( "
            + "SELECT MAX(sum) FROM (SELECT left_table.clinical_dept_id, SUM(right_table.price_amount) "
            + "FROM (SELECT id, clinical_dept_id FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY clinical_dept_id) as t2 )";

    // Getters & Setters
    public Date getStartDate() {

        return startDate;
    }

    public void setStartDate(Date startDate) {

        this.startDate = startDate;
    }

    public Date getEndDate() {

        return endDate;
    }

    public void setEndDate(Date endDate) {

        this.endDate = endDate;
    }

    public String getTopAsset() {

        return topAsset;
    }

    public String getTopDept() {

        return topDept;
    }

    public List<Row> getAssetDashboard () {

        return assetDashBoard;
    }

    public String getAssetName() {

        return assetName;
    }

    public void setAssetName(String assetName) {

        this.assetName = assetName;
    }


    private void devicePanel(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

            topAsset = "";
            topDept = "";

            List<Map<String, Object>> rs_mx1 = NativeSqlUtil.queryForList(MAX1TL, sqlParams);
            for (Map<String, Object> item : rs_mx1)
                if (item.get("asset_group") != null)
                    topAsset += ((Integer) item.get("asset_group")).toString();

            List<Map<String, Object>> rs_mx2 = NativeSqlUtil.queryForList(MAX2TL, sqlParams);
            for (Map<String, Object> item : rs_mx2)
                if (item.get("clinical_dept_id") != null)
                    topDept += ((Integer) item.get("clinical_dept_id")).toString();
    }


    private void deviceTable (Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

            List<Map<String, Object>> rs_db1 = NativeSqlUtil.queryForList(DB1TL, sqlParams);
            List<Map<String, Object>> rs_db2 = NativeSqlUtil.queryForList(DB2TL, sqlParams);
            List<Map<String, Object>> rs_db3 = NativeSqlUtil.queryForList(DB3TL, sqlParams);

            Iterator<Map<String, Object>> it_1;
            Iterator<Map<String, Object>> it_2;
            Iterator<Map<String, Object>> it_3;
            Map<String, Object> item_1;
            Map<String, Object> item_2;
            Map<String, Object> item_3;

            String name;
            String serial_num;
            String clinical_dept_name;
            double revenue;
            long scan;
            double expo;
            double cost;
            double profit;
            long repair;
            double dt;

            NumberFormat cf = new DecimalFormat(",###.##");

            assetDashBoard.clear();

            for (it_1 = rs_db1.iterator(), it_2 = rs_db2.iterator(), it_3 = rs_db3.iterator();
                it_1.hasNext() && it_2.hasNext() && it_3.hasNext(); ) {

                item_1 = it_1.next();
                item_2 = it_2.next();
                item_3 = it_3.next();

                name = item_1.get("name")!=null ?(String)item_1.get("name"): "";
                serial_num = item_1.get("serial_num")!=null ? (String)item_1.get("serial_num") : "";
                clinical_dept_name = item_1.get("clinical_dept_name")!=null ? (String)item_1.get("clinical_dept_name") : "";
                revenue = item_1.get("revenue")!=null ? (double)item_1.get("revenue") : 0.0;
                scan = item_1.get("scan")!=null ? (long)item_1.get("scan") : 0;
                expo = item_1.get("expo")!=null ? (double)item_1.get("expo") : 0.0;
                cost = (item_2.get("depre")!=null ? (double)item_2.get("depre") : 0.0) + (item_3.get("price")!=null ? (double)item_3.get("price") : 0.0);
                profit = revenue - cost;
                repair = item_3.get("repair")!=null ? (long)item_3.get("repair") : 0;
                dt = item_3.get("dt")!=null ? (double)item_3.get("dt") : 0.0;

                assetDashBoard.add(new Row(name, serial_num, clinical_dept_name, cf.format(revenue), cf.format(scan), cf.format(expo), cf.format(cost), cf.format(profit), cf.format(repair), cf.format(dt)));   
            }

    }

    private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

        HashMap<String, Object> sqlParams = new HashMap<>();
        
        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("clinical_dept_id", clinical_dept_id);
        sqlParams.put("startDate", startDate);
        sqlParams.put("endDate", endDate);
        sqlParams.put("currentDate", currentDate);
        sqlParams.put("targetYear", targetYear);

        devicePanel(startDate, endDate, currentDate, sqlParams);

        deviceTable(startDate, endDate, currentDate, sqlParams);

    }

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;

        if(asset == null) return;
        /*
        this.assetId    = asset.getId();
        this.setAssetName(asset.getName());
        this.isSingleAsset = true;
        logger.debug("Selected asset Id: {}, asset name: {}", assetId, assetName);
        update();
        */
    }



}