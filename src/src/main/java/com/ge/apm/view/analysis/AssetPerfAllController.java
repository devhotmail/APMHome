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

import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

import org.slf4j.LoggerFactory;

import webapp.framework.dao.NativeSqlUtil;
import java.time.Year;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;

import com.ge.apm.view.analysis.Row; 

@ManagedBean
@ViewScoped
public class AssetPerfAllController implements Serializable, ServerEventInterface {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HomeHeadController.class);
    
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
    private int assetId = -1;
    Map<String, String> i18nMessageAsset = new HashMap<String, String>();
    Map<String, String> i18nMessageDept = new HashMap<String, String>();

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

        FieldValueMessageController fieldMsg = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
        List<I18nMessage> messages1 = fieldMsg.getFieldValueList("assetGroup");
        List<I18nMessage> messages2 = fieldMsg.getFieldValueList("clinicalDeptId");

        for (I18nMessage local1 : messages1) {
            i18nMessageAsset.put(local1.getMsgKey(), local1.getValue());
        }

        for (I18nMessage local2 : messages2){
            i18nMessageDept.put(local2.getMsgKey(), local2.getValue());
        }


        deviceQuery(startDate, endDate, currentDate);

    }


    public void submit() {
  
    	logger.debug("On Submit ");
    	
        currentDate = Calendar.getInstance().getTime();
        deviceQuery(startDate, endDate, currentDate);
    }


    public void submit(String targetYear) {

    	logger.debug("On Submit " + targetYear);
    	
        this.targetYear = Integer.parseInt(targetYear);
        currentDate = Calendar.getInstance().getTime();
        deviceQuery(startDate, endDate, currentDate);
    }

            //bigint
    private String DB1TL
            = "SELECT left_table.name, serial_num, clinical_dept_name, SUM(right_table.price_amount) revenue, COUNT(right_table) scan, SUM(expose_count) expo "
            + "FROM (SELECT id, name, serial_num, clinical_dept_name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT expose_count, price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name, serial_num, clinical_dept_name "
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
            = "SELECT clinical_dept_name FROM "
            + "( SELECT left_table.clinical_dept_name, SUM(right_table.price_amount) "
            + "FROM (SELECT id, clinical_dept_name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY clinical_dept_name ) as t1 "
            + "WHERE t1.sum = ( "
            + "SELECT MAX(sum) FROM (SELECT left_table.clinical_dept_name, SUM(right_table.price_amount) "
            + "FROM (SELECT id, clinical_dept_name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT price_amount, asset_id FROM asset_clinical_record WHERE EXTRACT(YEAR FROM exam_date) = :#targetYear) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY clinical_dept_name) as t2 )";

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

    public int getAssetId() {

        return assetId;
    }

    private void devicePanel(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

            topAsset = "";
            topDept = "";

            List<Map<String, Object>> rs_mx1 = NativeSqlUtil.queryForList(MAX1TL, sqlParams);

            for (Map<String, Object> item : rs_mx1)
                if (item.get("asset_group") != null) {
                    topAsset += (getAssetGroup((Integer)item.get("asset_group")) + " ");
                }

            List<Map<String, Object>> rs_mx2 = NativeSqlUtil.queryForList(MAX2TL, sqlParams);
            for (Map<String, Object> item : rs_mx2)
                if (item.get("clinical_dept_name") != null)
                    topDept += ((String)item.get("clinical_dept_name") + " ");
    }
    
    private String getAssetGroup (Integer asset_group) {

        if (asset_group==null)
            return "";
        else if (i18nMessageAsset.containsKey(String.valueOf(asset_group)))
            return i18nMessageAsset.get(String.valueOf(asset_group));
        else
            return String.valueOf(asset_group);
    }


    private String getDeptName (Integer clinical_dept_id) {

        if (clinical_dept_id==null)
            return "";
        else if (i18nMessageDept.containsKey(String.valueOf(clinical_dept_id)))
            return i18nMessageDept.get(String.valueOf(clinical_dept_id));
        else
            return String.valueOf(clinical_dept_id);
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

            assetDashBoard.clear();

            for (it_1 = rs_db1.iterator(), it_2 = rs_db2.iterator(), it_3 = rs_db3.iterator();
                it_1.hasNext() && it_2.hasNext() && it_3.hasNext(); ) {

                item_1 = it_1.next();
                item_2 = it_2.next();
                item_3 = it_3.next();

                name = item_1.get("name")!=null ?(String)item_1.get("name"): "";
                serial_num = item_1.get("serial_num")!=null ? (String)item_1.get("serial_num") : "";
                clinical_dept_name = (String)item_1.get("clinical_dept_name");
                revenue = item_1.get("revenue")!=null ? (double)item_1.get("revenue") : 0.0;
                scan = item_1.get("scan")!=null ? (long)item_1.get("scan") : 0;
                expo = item_1.get("expo")!=null ? (double)item_1.get("expo") : 0.0;
                cost = (item_2.get("depre")!=null ? (double)item_2.get("depre") : 0.0) + (item_3.get("price")!=null ? (double)item_3.get("price") : 0.0);
                profit = revenue - cost;
                repair = item_3.get("repair")!=null ? (long)item_3.get("repair") : 0;
                dt = item_3.get("dt")!=null ? (double)item_3.get("dt") : 0.0;

                assetDashBoard.add(new Row(name, serial_num, clinical_dept_name, revenue, scan, expo, cost, profit, repair, dt));
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

        this.assetId = asset.getId();
        this.assetName = asset.getName();
        
        WebUtil.navigateTo("/portal/analysis/assetPerfSingle.xhtml?faces-redirect=true&asset_id=" + assetId + "&asset_name=" + assetName);

    }



}
