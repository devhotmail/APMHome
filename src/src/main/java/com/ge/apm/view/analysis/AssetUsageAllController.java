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

import org.postgresql.util.PGInterval;
import org.primefaces.model.chart.AxisType;
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
	private static final String devicelb = WebUtil.getMessage("devicelb");
	private static final String deviceScanlg = WebUtil.getMessage("deviceScanlg");
	private static final String deviceExpolg_1 = WebUtil.getMessage("deviceExpolg_1");
	private static final String deviceExpolg_2 = WebUtil.getMessage("deviceExpolg_2");
	private static final String deviceUsagelg_1 = WebUtil.getMessage("deviceUsagelg_1");
	private static final String deviceUsagelg_2 = WebUtil.getMessage("deviceUsagelg_2");
	private static final String deviceDTlg_1 = WebUtil.getMessage("deviceDTlg_1");
	private static final String deviceDTlg_2 = WebUtil.getMessage("deviceDTlg_2");	
	private static final String countlb = WebUtil.getMessage("countlb");
	private static final String hourslb = WebUtil.getMessage("hourslb");
	private static final String pctlb = WebUtil.getMessage("pctlb");
	private static final int HOURS_DAY = 6;
	private static final int SMLTH = 10;

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
		
		Calendar currentCal = Calendar.getInstance();
		Calendar startCal = Calendar.getInstance();
		startCal.add(Calendar.YEAR, -1);
		
		startDate = startCal.getTime();
		endDate = currentCal.getTime();
		currentDate = currentCal.getTime();	
	
		deviceQuery(startDate, endDate, currentDate);
		
	}
	
	public void submit() {

	}

	
	// Chart SQL

	private String SCANTL = 
			"SELECT DISTINCT a.name, COUNT(r.id) count " 
			+ "FROM asset_info a LEFT JOIN asset_clinical_record r "
			+ "ON a.id = r.asset_id " 
			+ "AND r.exam_date BETWEEN :#startDate AND :#endDate " 
			+ "WHERE a.hospital_id = :#hospitalId " //AND a.clinical_dept_id = :#clinical_dept_id "
			+ "GROUP BY a.name ORDER BY a.name";
			//hospitalId, clinical_dept_id, startDateString, endDateString);
	
	private String VALUESCANTL = 
			"SELECT SUM(t.count) FROM ( "
			+ SCANTL
			+ " ) AS t";

			//Double
	private String EXPOTL =
			"SELECT a.name, SUM(r.expose_count) hours " 
			+ "FROM asset_info a LEFT JOIN asset_clinical_record r "
			+ "ON a.id = r.asset_id " 
			+ "AND r.exam_date BETWEEN :#startDate AND :#endDate " 
			+ "WHERE a.hospital_id = :#hospitalId " //AND a.clinical_dept_id = :#clinical_dept_id "
			+ "GROUP BY a.name "
			+ "ORDER BY a.name";
			//hospitalId, clinical_dept_id, startDateString, endDateString);
	
	private String VALUEEXPOTL = 
			"SELECT SUM(t.hours) FROM ( "
			+ EXPOTL 
			+ " ) AS t";

			//Double
	private String EXPOALLTL =
			"SELECT a.name, SUM(r.expose_count) hours " 
			+ "FROM asset_info a LEFT JOIN asset_clinical_record r "
			+ "ON a.id = r.asset_id " 
			+ "WHERE a.hospital_id = :#hospitalId " //AND a.clinical_dept_id = :#clinical_dept_id "
			+ "GROUP BY a.name "
			+ "ORDER BY a.name";

			//Integer
	private String SERVETL = 
			"SELECT DISTINCT a.name, "
			+ "( Date(:#endDate) - CASE WHEN Date(:#startDate) > a.install_date THEN Date(:#startDate) ELSE a.install_date END ) serve_day " 
			+ "FROM asset_info a "
			+ "WHERE a.hospital_id = :#hospitalId "
			+ "ORDER BY a.name";

			//Integer
	private String BENCHSERVETL = 
			"SELECT DISTINCT a.name, "
			+ "current_date - a.install_date bench_day " 
			+ "FROM asset_info a "
			+ "WHERE a.hospital_id = :#hospitalId "
			+ "ORDER BY a.name";			

			//PGInterval
	private String INUSETL = 
			"SELECT DISTINCT a.name, SUM(r.exam_end_time - r.exam_start_time) inuse " 
			+ "FROM asset_info a LEFT JOIN asset_clinical_record r "
			+ "ON a.id = r.asset_id "
			+ "AND r.exam_date BETWEEN :#startDate AND :#endDate " 
			+ "WHERE a.hospital_id = :#hospitalId " //AND a.clinical_dept_id = :#clinical_dept_id "
			+ "GROUP BY a.name ORDER BY a.name";

			 //PGInterval
	private String DTTL = 
			"SELECT a.name, SUM(w.confirmed_up_time - w.confirmed_down_time) dt " 
			+ "FROM asset_info a LEFT JOIN work_order w "
			+ "ON w.asset_id = a.id " // AND w.create_time BETWEEN :#startDate AND :#endDate AND w.is_closed = true "
			+ "AND w.confirmed_up_time BETWEEN :#startDate AND :#endDate " 
			+ "WHERE a.hospital_id = :#hospitalId " //AND a.clinical_dept_id = :#clinical_dept_id "
			+ "GROUP BY a.name ORDER BY a.name";

			 //PGInterval
	private String DTALLTL = 
			"SELECT a.name, SUM(w.confirmed_up_time - w.confirmed_down_time) dt " 
			+ "FROM asset_info a LEFT JOIN work_order w "
			+ "ON w.asset_id = a.id " // AND w.create_time BETWEEN :#startDate AND :#endDate AND w.is_closed = true "
			+ "WHERE a.hospital_id = :#hospitalId " //AND a.clinical_dept_id = :#clinical_dept_id "
			+ "GROUP BY a.name ORDER BY a.name";

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
	
	private void deviceQuery(Date startDate, Date endDate, Date currentDate) {
		
		
		if ( (null==startDate )|| null==endDate | null ==currentDate ) {
			
			Calendar startCal = Calendar.getInstance();
			Calendar endCal = startCal;
			startCal.add(Calendar.YEAR, -1);
			
			startDate = startCal.getTime();
			endDate = endCal.getTime();
		}
		
		
		HashMap<String, Object> sqlParams = new HashMap<>();
		List<Map<String, Object>> rs_panel;
		
		sqlParams.put("hospitalId", hospitalId);
		sqlParams.put("clinical_dept_id", clinical_dept_id);
		sqlParams.put("startDate", startDate);
		sqlParams.put("endDate", endDate);
		sqlParams.put("currentDate", currentDate);
		
		// Panel Components
		rs_panel = NativeSqlUtil.queryForList(VALUESCANTL, sqlParams);
		valueScan = (rs_panel.get(0).get("sum")  != null ? rs_panel.get(0).get("sum").toString() : "");

		rs_panel = NativeSqlUtil.queryForList(VALUEEXPOTL, sqlParams);
		valueExpo = (rs_panel.get(0).get("sum")  != null ? rs_panel.get(0).get("sum").toString().substring(0, rs_panel.get(0).get("sum").toString().indexOf(".")) : "");


		// Chart Components
		{	
			//	*****************************************************************************************************
			deviceScan = new BarChartModel();
			deviceScan.getAxis(AxisType.X).setLabel(devicelb);
			deviceScan.getAxis(AxisType.X).setTickAngle(-60);
			deviceScan.getAxis(AxisType.Y).setLabel(countlb);
			deviceScan.setBarWidth(25);
			deviceScan.setLegendPosition("ne");
            deviceScan.setExtender("deviceScan");			

			ChartSeries cst_scan = new BarChartSeries();
			cst_scan.setLabel(deviceScanlg);

			List<Map<String, Object>> rs_scan = NativeSqlUtil.queryForList(SCANTL, sqlParams);

			for (Map<String, Object> item : rs_scan) {
				cst_scan.set(item.get("name") != null ? item.get("name").toString().substring(0, SMLTH) : "", 
						item.get("count") != null ? (Long) item.get("count") : 0);
			}

			deviceScan.addSeries(cst_scan);

			// ****************************************************************************************************
			deviceExpo = new BarChartModel();
			deviceExpo.getAxis(AxisType.X).setLabel(devicelb);
			deviceExpo.getAxis(AxisType.Y).setLabel(hourslb);
			deviceExpo.getAxis(AxisType.X).setTickAngle(-60);
			deviceExpo.setBarWidth(25);
			deviceExpo.setLegendPosition("ne");
            deviceExpo.setExtender("deviceExpo");

			ChartSeries cst_expo = new BarChartSeries();
			cst_expo.setLabel(deviceExpolg_1);

			List<Map<String, Object>> rs_expo = NativeSqlUtil.queryForList(EXPOTL, sqlParams);
			for (Map<String, Object> item : rs_expo) {
				cst_expo.set(item.get("name") != null ? item.get("name").toString().substring(0, SMLTH) : "",
						item.get("hours") != null ? (Double) item.get("hours") : (Double) 0.0);
			}
	
			ChartSeries cst_expo_bench = new LineChartSeries();
			cst_expo_bench.setLabel(deviceExpolg_2);
			

			List<Map<String, Object>> rs_expo_all = NativeSqlUtil.queryForList(EXPOALLTL, sqlParams);
			List<Map<String, Object>> rs_serve_day = NativeSqlUtil.queryForList(SERVETL, sqlParams);
			List<Map<String, Object>> rs_bench_day = NativeSqlUtil.queryForList(BENCHSERVETL, sqlParams);
	
			Iterator <Map<String, Object>> it_expo_all;	
			Iterator <Map<String, Object>> it_serve_day;
			Iterator <Map<String, Object>> it_bench_day;

			Map<String, Object> item_expo_all;
			Map<String, Object> item_serve_day;
			Map<String, Object> item_bench_day;

			Double expo_all;
			Integer serve_day;
			Integer bench_day;

			for (it_expo_all = rs_expo_all.iterator(), it_serve_day = rs_serve_day.iterator(), it_bench_day = rs_bench_day.iterator(); 
					it_serve_day.hasNext() && it_bench_day.hasNext(); ) {
				
				item_expo_all = it_expo_all.next();
				item_serve_day = it_serve_day.next();
				item_bench_day = it_bench_day.next();

				expo_all = (Double) (item_expo_all.get("hours") != null ? item_expo_all.get("hours") : 0.0);
				serve_day = (Integer) (item_serve_day.get("serve_day") != null ? item_serve_day.get("serve_day") : 0);
				if (serve_day < 0)	serve_day = 0;
				bench_day = (Integer) (item_bench_day.get("bench_day") != null ? item_bench_day.get("bench_day") : 1);
				if (bench_day < 1)	bench_day = 1;
				
				cst_expo_bench.set(item_expo_all.get("name") != null ? item_expo_all.get("name").toString().substring(0, SMLTH) : "",
						expo_all * serve_day / bench_day);
			}

			deviceExpo.addSeries(cst_expo);
			deviceExpo.addSeries(cst_expo_bench);
			
			// **********************************************************************************************************************
			deviceUsage = new BarChartModel();
			deviceUsage.getAxis(AxisType.X).setLabel(devicelb);
			deviceUsage.getAxis(AxisType.Y).setLabel(hourslb);
			deviceUsage.getAxis(AxisType.X).setTickAngle(-60);
			deviceUsage.setBarWidth(25);
			deviceUsage.setLegendPosition("ne");
			deviceUsage.setStacked(true);
			deviceUsage.setExtender("deviceUsage");	

			deviceDT = new BarChartModel();
			deviceDT.getAxis(AxisType.X).setLabel(devicelb);
			deviceDT.getAxis(AxisType.Y).setLabel(pctlb);
			deviceDT.getAxis(AxisType.X).setTickAngle(-60);
			deviceDT.setBarWidth(25);
			deviceDT.setLegendPosition("ne");
			deviceDT.setExtender("deviceDT");


			ChartSeries cst_inuse = new BarChartSeries();
			cst_inuse.setLabel(deviceUsagelg_1);
			ChartSeries cst_wait = new BarChartSeries();
			cst_wait.setLabel(deviceUsagelg_2);
			ChartSeries cst_dt = new BarChartSeries();
			cst_dt.setLabel(deviceDTlg_1);
			ChartSeries cst_dt_bench = new LineChartSeries();
			cst_dt_bench.setLabel(deviceDTlg_2);
			

			List<Map<String, Object>> rs_inuse = NativeSqlUtil.queryForList(INUSETL, sqlParams);
			List<Map<String, Object>> rs_dt = NativeSqlUtil.queryForList(DTTL, sqlParams);
			List<Map<String, Object>> rs_dt_all = NativeSqlUtil.queryForList(DTALLTL, sqlParams);

			Iterator <Map<String, Object>> it_inuse;
			Iterator <Map<String, Object>> it_dt;
			Iterator <Map<String, Object>> it_dt_all;

			Map<String, Object> item_inuse;
			Map<String, Object> item_dt;
			Map<String, Object> item_dt_all;

			// in hours
			Integer serve_hour;
			Integer bench_hour;
			Integer inuse;
			Integer dt;
			Integer dt_all;
			Integer wait; // wait = serve - downtime - inuse

			Long inuse_total = new Long(0);
			Long dt_total= new Long(0);
			Long wait_total = new Long(0); // wait = serve - downtime - inuse


			for ( it_inuse = rs_inuse.iterator(), it_dt = rs_dt.iterator(), it_dt_all = rs_dt_all.iterator(), it_serve_day = rs_serve_day.iterator(), it_bench_day = rs_bench_day.iterator(); 
					it_inuse.hasNext() && it_dt.hasNext() && it_dt_all.hasNext() && it_serve_day.hasNext() && it_bench_day.hasNext(); ) {
				
				item_inuse = it_inuse.next();
				item_dt = it_dt.next();
				item_dt_all = it_dt_all.next();
				item_serve_day = it_serve_day.next();
				item_bench_day = it_bench_day.next();

				inuse = item_inuse.get("inuse") != null ? ((PGInterval)item_inuse.get("inuse")).getHours() : 0;

				if (inuse < 0)	inuse *= -1;

				dt =  item_dt.get("dt") != null ? ((PGInterval)item_dt.get("dt")).getHours() : 0;
				dt_all =  item_dt_all.get("dt") != null ? ((PGInterval)item_dt_all.get("dt")).getHours() : 0;
				serve_hour = (Integer) (item_serve_day.get("serve_day") != null ? item_serve_day.get("serve_day") : 0) * HOURS_DAY;
				if (serve_hour < 0)	serve_hour = 0;
				bench_hour = (Integer) (item_bench_day.get("bench_day") != null ? item_bench_day.get("bench_day") : 1) * HOURS_DAY;
				if (bench_hour < HOURS_DAY)	bench_hour = HOURS_DAY;
				wait = serve_hour - dt - inuse;
				
				cst_inuse.set(item_serve_day.get("name") != null ? item_serve_day.get("name").toString().substring(0, SMLTH) : "",
					 inuse);
				cst_dt.set(item_dt.get("name") != null ? item_dt.get("name").toString().substring(0, SMLTH) : "", 
						dt);
				cst_wait.set(item_serve_day.get("name") != null ? item_serve_day.get("name").toString().substring(0, SMLTH) : "", 
						wait);
				cst_dt_bench.set(item_dt_all.get("name") != null ? item_dt_all.get("name").toString().substring(0, SMLTH) : "",
						dt_all * serve_hour / bench_hour);

				inuse_total += (long) inuse;
				dt_total += (long) dt;
				wait_total += (long) wait;

			}

			valueInuse = inuse_total.toString();
			valueDT = dt_total.toString();
			valueWait = wait_total.toString();

			deviceUsage.addSeries(cst_inuse);
			deviceUsage.addSeries(cst_wait);
			deviceDT.addSeries(cst_dt);
			deviceDT.addSeries(cst_dt_bench);

			// **********************************************************************************************
			deviceStat = new HorizontalBarChartModel();
			deviceStat.setStacked(true);	

			ChartSeries cst_stat_1 = new BarChartSeries();
			cst_stat_1.set("total", wait_total);
			ChartSeries cst_stat_2 = new BarChartSeries();
			cst_stat_2.set("total", inuse_total);
			ChartSeries cst_stat_3 = new BarChartSeries();
			cst_stat_3.set("total", dt_total);

			deviceStat.addSeries(cst_stat_1);
			deviceStat.addSeries(cst_stat_2);
			deviceStat.addSeries(cst_stat_3);

		}
		
	}

}
