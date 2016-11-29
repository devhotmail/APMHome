package com.ge.apm.view.analysis;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartSeries;

import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetUsageAllController implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final String deviceScanlg = WebUtil.getMessage("deviceScanlg");
	private static final String deviceExpolg_1 = WebUtil.getMessage("deviceExpolg_1");
	private static final String deviceExpolg_2 = WebUtil.getMessage("deviceExpolg_2");
	private static final String deviceUsagelg_1 = WebUtil.getMessage("deviceUsagelg_1");
	private static final String deviceUsagelg_2 = WebUtil.getMessage("deviceUsagelg_2");
	private static final String deviceDTlg_1 = WebUtil.getMessage("deviceDTlg_1");
	private static final String deviceDTlg_2 = WebUtil.getMessage("deviceDTlg_2");
	private static final int HOURS_DAY = 24;

	private int hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
	private int clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();
	
	// Dashboard Parameters
	private String valueScan = null;
	private String valueExpo = null;
	private String valueInuse = null;
	private String valueDT = null;
	private String valueWait = null;

	// Chart Components
	private BarChartModel deviceScan = null;
	private BarChartModel deviceExpo = null;
	private BarChartModel deviceUsage = null;
	private BarChartModel deviceDT = null;
	private BarChartModel deviceStat = null;
	private Date startDate = null;
	private Date endDate = null;
	private Date currentDate = null;
	private boolean start_bool = false;
	private boolean end_bool = false;

	
	//debug param

	@PostConstruct
	public void init() {
		
		hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
		clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();

		valueScan = "";
		valueExpo = "";
		valueInuse = "";
		valueDT = "";
		valueWait = "";

		deviceScan = new BarChartModel();
		deviceScan.setBarWidth(50);
		deviceScan.setLegendPosition("ne");
        deviceScan.setExtender("deviceScan");		

		deviceExpo = new BarChartModel();
		deviceExpo.setBarWidth(50);
		deviceExpo.setLegendPosition("ne");
        deviceExpo.setExtender("deviceExpo");

		deviceUsage = new BarChartModel();
		deviceUsage.setBarWidth(50);
		deviceUsage.setLegendPosition("ne");
		deviceUsage.setStacked(true);
		deviceUsage.setExtender("deviceUsage");	

		deviceDT = new BarChartModel();
		deviceDT.setBarWidth(50);
		deviceDT.setLegendPosition("ne");
		deviceDT.setExtender("deviceDT");

		deviceStat = new HorizontalBarChartModel();
		deviceStat.setStacked(true);	
		deviceStat.setExtender("deviceStat");

		Calendar currentCal = Calendar.getInstance();
		Calendar startCal = Calendar.getInstance();
		startCal.add(Calendar.YEAR, -1);
		
		startDate = startCal.getTime();
		endDate = currentCal.getTime();
		currentDate = currentCal.getTime();	
	
		deviceQuery(startDate, endDate, currentDate);
		
	}
	
	public void submit() {

		currentDate = Calendar.getInstance().getTime();	
		deviceQuery(startDate, endDate, currentDate);
	}

	
            //bigint
    private String SCANTL
            = "SELECT left_table.name, COUNT(right_table) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

            //bigint
    private String VALUESCANTL
            = "SELECT COUNT(right_table) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT id, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id ";

            //double
    private String EXPOTL
            = "SELECT left_table.name, SUM(right_table.expose_count) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT expose_count, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

            //double
    private String VALUEEXPOTL
            = "SELECT SUM(right_table.expose_count) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT expose_count, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id ";

			//Double
    private String BENCHEXPOTL
            = "SELECT left_table.name, SUM(right_table.sum) "
            + "FROM (SELECT id, name, asset_group FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT asset_id, SUM(expose_count) FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate GROUP BY asset_id) right_table "
            + "WHERE a.hospital_id = :#hospitalId AND a.clinical_dept_id = :#clinical_dept_id "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

			//Integer
	private String SERVETL = 
			"SELECT DISTINCT a.name, "
			+ ":#HOURS_DAY * ( "
			+ "CASE WHEN a.terminate_date IS NULL THEN Date(:#endDate) WHEN Date(:#endDate) < a.terminate_date THEN Date(:#startDate) ELSE a.terminate_date END "
			+ "- CASE WHEN Date(:#startDate) > a.install_date THEN Date(:#startDate) ELSE a.install_date END ) serve " 
			+ "FROM asset_info a "
			+ "WHERE a.hospital_id = :#hospitalId "
			+ "AND a.is_valid = true "
			+ "ORDER BY a.name";

			//PGInterval
	private String INUSETL = 
			"SELECT DISTINCT a.name, "
			+ "365 * 24 * EXTRACT ( YEAR FROM SUM(exam_end_time - exam_start_time) )  "
			+ "+ 24 * EXTRACT ( DAY FROM SUM(exam_end_time - exam_start_time) ) " 
			+ "+ EXTRACT ( HOUR FROM SUM(exam_end_time - exam_start_time) ) "
			+ "+ ( EXTRACT ( MINUTE FROM SUM(exam_end_time - exam_start_time) ) ) / 60 inuse "  
			+ "FROM asset_info a LEFT JOIN asset_clinical_record r "
			+ "ON a.id = r.asset_id "
			+ "AND r.exam_date BETWEEN :#startDate AND :#endDate " 
			+ "WHERE a.hospital_id = :#hospitalId "
			+ "GROUP BY a.name ORDER BY a.name";

			 //PGInterval
	private String DTTL = 
            "SELECT left_table.name, SUM(right_table.diff) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT confirmed_up_time-confirmed_down_time diff, asset_id FROM work_order WHERE is_closed = true AND create_time BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

			 //PGInterval
	private String BENCHDTTL = 
            "SELECT left_table.name, SUM(right_table.sum) "
            + "FROM (SELECT id, name, asset_group FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId) left_table "
            + "LEFT JOIN (SELECT asset_id, SUM(confirmed_up_time-confirmed_down_time) FROM work_order WHERE is_closed = true AND create_time BETWEEN :#startDate AND :#endDate GROUP BY asset_id) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

	// Getters & Setters
	
    public Date getStartDate() { 	

        return startDate;
    }
 
    public void setStartDate(Date startDate) {

        this.startDate = startDate;
        
    	start_bool = true;
    	
    	if (start_bool && end_bool) {
    		deviceQuery(startDate, endDate, currentDate);
    	}
        
    }
    
    public Date getEndDate() {
	
        return endDate;
    }
 
    public void setEndDate(Date endDate) {
    		
        this.endDate = endDate;
        
    	end_bool = true;
    	
    	if (start_bool && end_bool) {
    		deviceQuery(startDate, endDate, currentDate);
    	}
    }

    
	public BarChartModel getDeviceScan() {

		return deviceScan;
	}

	public BarChartModel getDeviceExpo() {

		return deviceExpo;
	}

	public BarChartModel getDeviceUsage() {

		return deviceUsage;
	}

	public BarChartModel getDeviceDT() {

		return deviceDT;
	}
	
	public BarChartModel getDeviceStat() {

		return deviceStat;
	}

	public String getValueScan() {

		return valueScan;
	}

	public String getValueExpo() {

		return valueExpo;
	}

	public String getValueInuse() {

		return valueInuse;
	}

	public String getValueDT() {

		return valueDT;
	}
	
	public String getValueWait() {

		return valueWait;
	}
	
	private void devicePanel(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {
		
		List<Map<String, Object>> rs_panel;
		
		// Panel Components
		rs_panel = NativeSqlUtil.queryForList(VALUESCANTL, sqlParams);
		valueScan = (rs_panel.get(0).get("sum")  != null ? rs_panel.get(0).get("sum").toString() : "");

		rs_panel = NativeSqlUtil.queryForList(VALUEEXPOTL, sqlParams);
		valueExpo = (rs_panel.get(0).get("sum")  != null ? rs_panel.get(0).get("sum").toString().substring(0, rs_panel.get(0).get("sum").toString().indexOf(".")) : "");


	}

	private void deviceChart_1(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {	

			ChartSeries cst_scan = new BarChartSeries();
			cst_scan.setLabel(deviceScanlg);

			List<Map<String, Object>> rs_scan = NativeSqlUtil.queryForList(SCANTL, sqlParams);

			for (Map<String, Object> item : rs_scan) {
				cst_scan.set(item.get("name") != null ? item.get("name").toString() : "", 
						item.get("count") != null ? (Long) item.get("count") : 0);
			}

			deviceScan.clear();
			deviceScan.addSeries(cst_scan);
	}


	private void deviceChart_2(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

			ChartSeries cst_expo = new BarChartSeries();
			cst_expo.setLabel(deviceExpolg_1);

			List<Map<String, Object>> rs_expo = NativeSqlUtil.queryForList(EXPOTL, sqlParams);
			for (Map<String, Object> item : rs_expo) {
				cst_expo.set(item.get("name") != null ? item.get("name").toString() : "",
						item.get("sum") != null ? (Double) item.get("sum") : (Double) 0.0);
			}
	
			ChartSeries cst_expo_bench = new LineChartSeries();
			cst_expo_bench.setLabel(deviceExpolg_2);
			List<Map<String, Object>> rs_expo_bench = NativeSqlUtil.queryForList(BENCHEXPOTL, sqlParams);
			for (Map<String, Object> item : rs_expo_bench) {
				cst_expo.set(item.get("name") != null ? item.get("name").toString() : "",
						item.get("sum") != null ? (Double) item.get("sum") : (Double) 0.0);
			}
			
			deviceExpo.clear();
			deviceExpo.addSeries(cst_expo);
			deviceExpo.addSeries(cst_expo_bench);
	}			


	private void deviceChart_3(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

			ChartSeries cst_inuse = new BarChartSeries();
			cst_inuse.setLabel(deviceUsagelg_1);
			ChartSeries cst_wait = new BarChartSeries();
			cst_wait.setLabel(deviceUsagelg_2);
			ChartSeries cst_dt = new BarChartSeries();
			cst_dt.setLabel(deviceDTlg_1);
			ChartSeries cst_dt_bench = new LineChartSeries();
			cst_dt_bench.setLabel(deviceDTlg_2);
			

			List<Map<String, Object>> rs_serve = NativeSqlUtil.queryForList(SERVETL, sqlParams);
			List<Map<String, Object>> rs_inuse = NativeSqlUtil.queryForList(INUSETL, sqlParams);
			List<Map<String, Object>> rs_dt = NativeSqlUtil.queryForList(DTTL, sqlParams);
			List<Map<String, Object>> rs_dt_bench = NativeSqlUtil.queryForList(BENCHDTTL, sqlParams);

			Iterator <Map<String, Object>> it_serve;
			Iterator <Map<String, Object>> it_inuse;
			Iterator <Map<String, Object>> it_dt;
			Iterator <Map<String, Object>> it_dt_bench;

			Map<String, Object> item_serve;
			Map<String, Object> item_inuse;
			Map<String, Object> item_dt;
			Map<String, Object> item_dt_bench;


			String asset_name;
			int serve;
			double inuse;
			double dt;
			double dt_bench;
			double wait; // wait = serve - downtime - inuse

			double inuse_total = 0.0;
			double dt_total= 0.0;
			double wait_total = 0.0; // wait = serve - downtime - inuse


			for ( it_inuse = rs_inuse.iterator(), it_dt = rs_dt.iterator(), it_serve = rs_serve.iterator(), it_dt_bench = rs_dt_bench.iterator(); 
					it_inuse.hasNext() && it_dt.hasNext() && it_serve.hasNext() && it_dt_bench.hasNext(); ) {
				
				item_inuse = it_inuse.next();
				item_dt = it_dt.next();
				item_serve = it_serve.next();
				item_dt_bench = it_dt_bench.next();

				asset_name = (item_serve.get("name") != null ? (String) item_serve.get("sum") : "");
				serve = item_serve.get("serve") != null ? (int)item_serve.get("serve") : 0;
				if (serve<0) 	serve = 0;
				inuse = item_inuse.get("inuse") != null ? (double)item_inuse.get("inuse") : 0;
				dt =  item_dt.get("sum") != null ? (double)item_dt.get("sum") : 0;
				dt_bench = item_dt_bench.get("sum") != null ? (double)item_dt_bench.get("sum") : 0;
				wait = serve - dt - inuse;				

				cst_inuse.set(asset_name, inuse);
				cst_wait.set(asset_name, wait);
				
				inuse_total += inuse;
				dt_total += dt;
				wait_total += wait;
				
				dt /= serve;
				dt_bench /= dt_bench;
				
				cst_dt.set(asset_name, dt);
				cst_dt_bench.set(asset_name, dt_bench);				
				

			}

			valueInuse = new Double(inuse_total).toString();
			valueInuse = valueInuse.substring(0, valueInuse.indexOf(".")+2);
			valueDT = new Double(dt_total).toString();
			valueDT = valueDT.substring(0, valueDT.indexOf(".")+2);
			valueWait = new Double(wait_total).toString();
			valueWait = valueWait.substring(0, valueWait.indexOf(".")+2);

			deviceUsage.clear();
			deviceUsage.addSeries(cst_inuse);
			deviceUsage.addSeries(cst_wait);

			deviceDT.clear();
			deviceDT.addSeries(cst_dt);
			deviceDT.addSeries(cst_dt_bench);

			ChartSeries cst_stat_1 = new BarChartSeries();
			cst_stat_1.set("total", wait_total);
			ChartSeries cst_stat_2 = new BarChartSeries();
			cst_stat_2.set("total", inuse_total);
			ChartSeries cst_stat_3 = new BarChartSeries();
			cst_stat_3.set("total", dt_total);

			deviceStat.clear();
			deviceStat.addSeries(cst_stat_1);
			deviceStat.addSeries(cst_stat_2);
			deviceStat.addSeries(cst_stat_3);

	}

	private void deviceChart_5(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {	

			/*
			deviceScan.clear();
			ChartSeries cst_scan = new BarChartSeries();
			cst_scan.setLabel(deviceScanlg);

			List<Map<String, Object>> rs_scan = NativeSqlUtil.queryForList(SCANTL, sqlParams);

			for (Map<String, Object> item : rs_scan) {
				cst_scan.set(item.get("name") != null ? item.get("name").toString() : "", 
						item.get("count") != null ? (Long) item.get("count") : 0);
			}

			deviceScan.addSeries(cst_scan);
			*/
	}

	private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

		HashMap<String, Object> sqlParams = new HashMap<>();
		
		sqlParams.put("hospitalId", hospitalId);
		sqlParams.put("clinical_dept_id", clinical_dept_id);
		sqlParams.put("startDate", startDate);
		sqlParams.put("endDate", endDate);
		sqlParams.put("currentDate", currentDate);
		sqlParams.put("HOURS_DAY", HOURS_DAY);

		devicePanel(startDate, endDate, currentDate, sqlParams);

		deviceChart_1(startDate, endDate, currentDate, sqlParams);
		
		deviceChart_2(startDate, endDate, currentDate, sqlParams);

		deviceChart_3(startDate, endDate, currentDate, sqlParams);

		deviceChart_5(startDate, endDate, currentDate, sqlParams);
	}



}
