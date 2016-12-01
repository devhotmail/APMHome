package com.ge.apm.view.analysis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ge.apm.domain.AssetInfo;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.joda.time.DateTime;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.DateAxis;

import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class AssetUsageSingleController implements ServerEventInterface {

	private static final String deviceScanlg = WebUtil.getMessage("deviceScanlg");
	private static final String deviceExpolg_1 = WebUtil.getMessage("deviceExpolg_1");
	private static final String deviceUsagelg_1 = WebUtil.getMessage("deviceUsagelg_1");
	private static final String deviceUsagelg_2 = WebUtil.getMessage("deviceUsagelg_2");
	private static final String deviceDTlg_3 = WebUtil.getMessage("deviceDTlg_3");	

	private static final String DATA_FORMAT = "yyyy-MM-dd";
	private static final int HOURS_DAY = 24;

	private int hospitalId = -1;
	private int clinical_dept_id = -1;
	private int filter_id = -1; 

	// Dashboard Parameters
	private String valueScan = null;
	private String valueExpo = null;
	private String valueInuse = null;
	private String valueDT = null;
	private String valueWait = null;

	// Chart Components
	private LineChartModel deviceScan = null;
	private LineChartModel deviceExpo = null;
	private LineChartModel deviceUsage = null;
	private HorizontalBarChartModel deviceStat = null;
	private Date startDate = null;
	private Date endDate = null;
	private Date currentDate = null;
	private String assetName = null;
    
	//debug param
	private long a;
	private long b;

	@PostConstruct
	public void init() {

		hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
		clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();
		filter_id = 1;
		assetName = "CT-0-0f861";

		valueScan = "";
		valueExpo = "";
		valueInuse = "";
		valueDT = "";
		valueWait = "";
		
		DateAxis axis = new DateAxis("");
		axis.setTickAngle(-70);
        axis.setTickFormat("%y-%m-%d");
        
		deviceScan = new LineChartModel();
		deviceScan.setLegendPosition("ne");
        deviceScan.getAxes().put(AxisType.X, axis);	
        deviceScan.setZoom(true);
        deviceScan.setExtender("deviceScan");
        
		deviceExpo = new LineChartModel();
		deviceExpo.setLegendPosition("ne");
        deviceExpo.getAxes().put(AxisType.X, axis);	
        deviceExpo.setZoom(true);
        deviceExpo.setExtender("deviceExpo");
		
		deviceUsage = new LineChartModel();
		deviceUsage.setLegendPosition("ne");
		deviceUsage.setStacked(true);
        deviceUsage.getAxes().put(AxisType.X, axis);	
        deviceUsage.setZoom(true);
        deviceUsage.getAxis(AxisType.Y).setMin(0);
        deviceUsage.getAxis(AxisType.Y).setMax(HOURS_DAY);
        deviceUsage.setExtender("deviceUsage");

		deviceStat = new HorizontalBarChartModel();
		deviceStat.setStacked(true);	
		deviceStat.setExtender("deviceStat");

		Calendar currentCal = Calendar.getInstance();
		Calendar startCal = Calendar.getInstance();
		startCal.add(Calendar.MONTH, -6);
		
		startDate = startCal.getTime();
		endDate = currentCal.getTime();
		currentDate = currentCal.getTime();	

		deviceQuery(startDate, endDate, currentDate);
	}

	@Override
    public void onServerEvent(String eventName, Object eventObject)	{
        AssetInfo asset = (AssetInfo) eventObject;
        
       	this.filter_id = asset.getId();
      	this.assetName = asset.getName();
       	//asset.getAssetOwnerId()
        //asset.getAssetOwnerName()
        System.out.println("[ asset = ]" + filter_id + assetName);

       	deviceQuery(startDate, endDate, currentDate);
    }


	public void submit() {

		currentDate = Calendar.getInstance().getTime();	
		deviceQuery(startDate, endDate, currentDate);

	}

	
	// Chart SQL
	private String SCANTL
			= "SELECT to_char(exam_date, 'YYYY-MM-DD') timeline, COUNT(asset_id) count " 
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#filter_id " 
			+ "AND exam_date BETWEEN :#startDate AND :#endDate " 
			+ "GROUP BY exam_date "
			+ "ORDER BY exam_date";
	
	private String VALUESCANTL
			= "SELECT COUNT(asset_id) sum " 
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#filter_id " 
			+ "AND exam_date BETWEEN :#startDate AND :#endDate " 
			//+ "AND hospital_id = :#hospitalId " 
			+ "GROUP BY asset_id";

			//Double
	private String EXPOTL
			= "SELECT to_char(exam_date, 'YYYY-MM-DD') timeline, SUM(expose_count) hours " 
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#filter_id " 
			+ "AND exam_date BETWEEN :#startDate AND :#endDate " 
			//+ "AND a.hospital_id = :#hospitalId " 
			+ "GROUP BY exam_date "
			+ "ORDER BY exam_date";
	
	private String VALUEEXPOTL
			= "SELECT SUM(expose_count) sum " 
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#filter_id " 
			+ "AND exam_date BETWEEN :#startDate AND :#endDate " 
			//+ "AND hospital_id = :#hospitalId " 
			+ "GROUP BY asset_id";

			//Integer
	private String INUSETL 
			= "SELECT to_char(exam_date, 'YYYY-MM-DD') timeline, " 
			+ "SUM(exam_duration)/60 inuse " 
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#filter_id " 
			+ "AND exam_date BETWEEN :#startDate AND :#endDate " 
			+ "GROUP BY exam_date "
			+ "ORDER BY exam_date";


			//Double
	private String DTTL 
			= "SELECT to_char(confirmed_down_time, 'YYYY-MM-DD') timeline,  "
			+ "CASE WHEN EXTRACT (DAY FROM confirmed_up_time) - EXTRACT (DAY FROM confirmed_down_time) >= 1 "
			+ "THEN 24 - EXTRACT (HOUR FROM confirmed_down_time) "
			+ "- ( EXTRACT (MINUTE FROM confirmed_up_time - confirmed_down_time) ) / 60 "
			+ "ELSE EXTRACT (HOUR FROM confirmed_up_time - confirmed_down_time) "
			+ "+ ( EXTRACT (MINUTE FROM confirmed_up_time - confirmed_down_time) ) / 60 END hour_today, "
			+ "24 * EXTRACT (DAY FROM confirmed_up_time - confirmed_down_time) "
			+ "+ EXTRACT (HOUR FROM confirmed_up_time - confirmed_down_time) "
			+ "+ ( EXTRACT (MINUTE FROM confirmed_up_time - confirmed_down_time) ) / 60 hour_total "
			+ "FROM work_order "
			+ "WHERE asset_id = :#filter_id " 
			+ "AND confirmed_down_time > :#startDate AND confirmed_up_time < :#endDate " 
			+ "ORDER BY to_char(confirmed_up_time, 'YYYY-MM-DD')";


	// Getters & Setters

	public String getAssetName() {

		return assetName;
	}

	public void setAssetName(String assetName) {

		this.assetName = assetName;
	}

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

	public LineChartModel getDeviceScan() {

		return deviceScan;
	}

	public LineChartModel getDeviceExpo() {

		return deviceExpo;
	}

	public LineChartModel getDeviceUsage() {

		return deviceUsage;
	}
	
	public HorizontalBarChartModel getDeviceStat() {

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
	

	private void deviceChart_1(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {
		
		DateTime startJoda = new DateTime(startDate);
		DateTime endJoda = new DateTime(endDate);

		ChartSeries cst_scan = new LineChartSeries();
		cst_scan.setLabel(deviceScanlg);
		
		a = System.currentTimeMillis();
		List<Map<String, Object>> rs_scan = NativeSqlUtil.queryForList(SCANTL, sqlParams);
		b = System.currentTimeMillis();
		System.out.println("SQL Query for Chart 1: " + (b-a));
		
		Map<String, Object> rs_scan_map = new HashMap<String, Object>();
		
		if (!rs_scan.isEmpty()) 
			for (Map<String, Object> item : rs_scan) {
				rs_scan_map.put(item.get("timeline").toString(), item.get("count") != null ? (Long) item.get("count") : 0);
		}

		rs_scan.clear();


		String idate;
		
		for (startJoda = new DateTime(startDate); startJoda.isBefore(endJoda); startJoda = startJoda.plusDays(1)) {


			idate = startJoda.toString(DATA_FORMAT);
			if ( rs_scan_map.containsKey(idate)) {	
				//System.out.println(idate + " " + rs_scan_map.get(idate));	
				cst_scan.set(idate, (Long) rs_scan_map.get(idate));
			}
			else {
				//System.out.println(idate +  " " + 0);
				cst_scan.set(idate, 0);
			}
			
		}

		deviceScan.clear();
		deviceScan.addSeries(cst_scan);
		b = System.currentTimeMillis();
		System.out.println("ChartSeries Prepare for Chart 1: " + (b-a));		
	
	}
	
	private void deviceChart_2(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {
		
		DateTime startJoda = new DateTime(startDate);
		DateTime endJoda = new DateTime(endDate);

		ChartSeries cst_expo = new LineChartSeries();
		cst_expo.setLabel(deviceExpolg_1);

		a = System.currentTimeMillis();
		List<Map<String, Object>> rs_expo = NativeSqlUtil.queryForList(EXPOTL, sqlParams);
		b = System.currentTimeMillis();
		System.out.println("SQL Query for Chart 2: " + (b-a));
		
		Map<String, Object> rs_expo_map = new HashMap<String, Object>();
		
		if (!rs_expo.isEmpty()) 
			for (Map<String, Object> item : rs_expo) {
				rs_expo_map.put(item.get("timeline").toString(), item.get("hours") != null ? (Double) item.get("hours") : 0);
		}

		rs_expo.clear();
		
		String idate;
		
		for (startJoda = new DateTime(startDate); startJoda.isBefore(endJoda); startJoda = startJoda.plusDays(1)) {
			idate = startJoda.toString(DATA_FORMAT);
			if ( rs_expo_map.containsKey(idate))		
				cst_expo.set(idate, (Double) rs_expo_map.get(idate));
			else 
				cst_expo.set(idate, 0);
			
		}

		deviceExpo.clear();
		deviceExpo.addSeries(cst_expo);
		b = System.currentTimeMillis();
		System.out.println("ChartSeries Prepare for Chart 2: " + (b-a));
		
	}

	private void deviceChart_3(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {
		
		DateTime startJoda = new DateTime(startDate);
		DateTime endJoda = new DateTime(endDate);

		LineChartSeries cst_inuse = new LineChartSeries();
		cst_inuse.setLabel(deviceUsagelg_1);
		cst_inuse.setFill(true);
		LineChartSeries cst_wait = new LineChartSeries();
		cst_wait.setLabel(deviceUsagelg_2);
		cst_wait.setFill(true);
		LineChartSeries cst_dt = new LineChartSeries();
		cst_dt.setLabel(deviceDTlg_3);
		cst_dt.setFill(true);

		double inuse;
		double dt;
		double wait; // wait = serve - downtime - inuse

		double inuse_total = 0.0;
		double dt_total= 0.0;
		double wait_total = 0.0; // wait = serve - downtime - inuse

		/*
		List<Map<String, Object>> rs_serve_hour = NativeSqlUtil.queryForList(SERVETL, sqlParams);

		if ( !rs_serve_hour.isEmpty() )
			serve_hour = (int) (rs_serve_hour.get(0).get("serve_hour") != null ? rs_serve_hour.get(0).get("serve_hour") : 0);
		else serve_hour = 0;
		*/

		a = System.currentTimeMillis();
		List<Map<String, Object>> rs_inuse = NativeSqlUtil.queryForList(INUSETL, sqlParams);
		List<Map<String, Object>> rs_dt = NativeSqlUtil.queryForList(DTTL, sqlParams);
		b = System.currentTimeMillis();
		System.out.println("SQL Query for Chart 3: " + (b-a));

		Map<String, Object> rs_inuse_map = new HashMap<String, Object>();
		
		if (!rs_inuse.isEmpty())
			for (Map<String, Object> item : rs_inuse) {
				rs_inuse_map.put(item.get("timeline").toString(), item.get("inuse") != null ? ((long)item.get("inuse"))/60.0 : 0.0);

		}

		rs_inuse.clear();

		Map<String, Object> rs_dt_map = new HashMap<String, Object>();
		
		if (!rs_dt.isEmpty())
			for (Map<String, Object> item : rs_dt) {
				rs_dt_map.put(item.get("timeline").toString(), item.get("hour_today") != null ? (double) item.get("hour_today") : 0.0);
				if (item.get("hour_total") != null){
					double hour_total_tmp =  (double) item.get("hour_total") - (double) item.get("hour_today");
					String timeline_tmp = item.get("timeline").toString();
					DateTime currentJoda = new DateTime( Integer.valueOf(timeline_tmp.split("-")[0]),  Integer.valueOf(timeline_tmp.split("-")[1]),  Integer.valueOf(timeline_tmp.split("-")[2]),0, 0);
					while (hour_total_tmp>0.001) {
						currentJoda = currentJoda.plusDays(1);
						System.out.println(currentJoda.toString(DATA_FORMAT));
						System.out.println(hour_total_tmp);
						if (hour_total_tmp < 24) {
						rs_dt_map.put(currentJoda.toString(DATA_FORMAT),hour_total_tmp);
						}
						else {
						rs_dt_map.put(currentJoda.toString(DATA_FORMAT),hour_total_tmp-24);
						}
						hour_total_tmp -= 24;
					}
				}
		}



		rs_dt.clear();
		
		String idate;
		
		for (startJoda = new DateTime(startDate); startJoda.isBefore(endJoda); startJoda = startJoda.plusDays(1)) {

			inuse = 0.0;
			dt = 0.0;
			wait = 0.0;

			idate = startJoda.toString(DATA_FORMAT);

			if ( rs_inuse_map.containsKey(idate)) {	
				inuse = (double) rs_inuse_map.get(idate);
				inuse_total += inuse;
				cst_inuse.set(idate, inuse);
			}
			else {
				cst_inuse.set(idate, 0.0);
			}

			if ( rs_dt_map.containsKey(idate)) {		
				dt = (double) rs_dt_map.get(idate);
				dt_total += dt;
				cst_dt.set(idate, dt);
			}
			else {
				cst_dt.set(idate, 0.0);
			}

			wait = HOURS_DAY - inuse - dt;
			if (wait < 0)	wait = 0.0;
			wait_total += wait;
			cst_wait.set(idate, wait);
		}

		deviceUsage.clear();
		deviceUsage.addSeries(cst_dt);
		deviceUsage.addSeries(cst_inuse);
		deviceUsage.addSeries(cst_wait);


		valueInuse = new Double(inuse_total).toString();
		valueInuse = valueInuse.substring(0, valueInuse.indexOf(".")+2);
		valueDT = new Double(dt_total).toString();
		valueDT = valueDT.substring(0, valueDT.indexOf(".")+2);
		valueWait = new Double(wait_total).toString();
		valueWait = valueWait.substring(0, valueWait.indexOf(".")+2);

		ChartSeries cst_stat_1 = new BarChartSeries();
		cst_stat_1.set("total", wait_total);
		ChartSeries cst_stat_2 = new BarChartSeries();
		cst_stat_2.set("total", inuse_total);
		ChartSeries cst_stat_3 = new BarChartSeries();
		cst_stat_3.set("total", dt_total);

		deviceStat.clear();
		deviceStat.addSeries(cst_stat_3);
		deviceStat.addSeries(cst_stat_1);
		deviceStat.addSeries(cst_stat_2);		

		b = System.currentTimeMillis();
		System.out.println("ChartSeries Prepare for Chart 3: " + (b-a));
	}	

	private void devicePanel(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

		List<Map<String, Object>> rs_panel;
		
		// Panel Components
		rs_panel = NativeSqlUtil.queryForList(VALUESCANTL, sqlParams);
	
		if (!rs_panel.isEmpty())	
			valueScan = (rs_panel.get(0).get("sum")  != null ? rs_panel.get(0).get("sum").toString() : "");

		rs_panel = NativeSqlUtil.queryForList(VALUEEXPOTL, sqlParams);

		if (!rs_panel.isEmpty())	
			valueExpo = (rs_panel.get(0).get("sum")  != null ? rs_panel.get(0).get("sum").toString().substring(0, rs_panel.get(0).get("sum").toString().indexOf(".")) : "");
		
	}

	private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

		HashMap<String, Object> sqlParams = new HashMap<>();
		
		sqlParams.put("hospitalId", hospitalId);
		sqlParams.put("clinical_dept_id", clinical_dept_id);
		sqlParams.put("startDate", startDate);
		sqlParams.put("endDate", endDate);
		sqlParams.put("currentDate", currentDate);
		sqlParams.put("filter_id", filter_id);
		sqlParams.put("HOURS_DAY", HOURS_DAY);

		devicePanel(startDate, endDate, currentDate, sqlParams);

		deviceChart_1(startDate, endDate, currentDate, sqlParams);
		
		deviceChart_2(startDate, endDate, currentDate, sqlParams);

		deviceChart_3(startDate, endDate, currentDate, sqlParams);

	}

}



