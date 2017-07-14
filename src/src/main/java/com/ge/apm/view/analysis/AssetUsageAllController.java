package com.ge.apm.view.analysis;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.slf4j.LoggerFactory;

import webapp.framework.web.mvc.ServerEventInterface;
import com.ge.apm.domain.AssetInfo;

import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;

import java.text.NumberFormat;
import java.text.DecimalFormat;

@ManagedBean
@ViewScoped
public class AssetUsageAllController implements Serializable, ServerEventInterface {

	private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AssetUsageAllController.class);
    
	private static final String deviceScanlg = WebUtil.getMessage("deviceScanlg");
	private static final String deviceExpolg_1 = WebUtil.getMessage("deviceExpolg_1");
	private static final String deviceExpolg_2 = WebUtil.getMessage("deviceExpolg_2");
	private static final String deviceUsagelg_1 = WebUtil.getMessage("deviceUsagelg_1");
	private static final String deviceUsagelg_2 = WebUtil.getMessage("deviceUsagelg_2");
	private static final String deviceDTlg_1 = WebUtil.getMessage("deviceDTlg_1");
	private static final String deviceDTlg_2 = WebUtil.getMessage("deviceDTlg_2");
    private static final String checkIntervalNotice_1 = WebUtil.getMessage("checkIntervalNotice_1");
    private static final String checkIntervalNotice_2 = WebUtil.getMessage("checkIntervalNotice_2");
	private static final int HOURS_DAY = 24;

	private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    private final int clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();
    
	private HashMap<String, Object> sqlParams = new HashMap<>();  

	private String assetName = WebUtil.getMessage("preventiveMaintenanceAnalysis_allDevices");
	private int assetId = -1;
	

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

	private NumberFormat cf = new DecimalFormat(",###.##");
    private NumberFormat cfint = new DecimalFormat(",###");

	//debug param

	@PostConstruct
	public void init() {

		valueScan = "";
		valueExpo = "";
		valueInuse = "";
		valueDT = "";
		valueWait = "";

		deviceScan = new BarChartModel();
		deviceScan.setBarWidth(50);
		deviceScan.setLegendPosition("ne");
		deviceScan.getAxis(AxisType.Y).setMin(0);
        deviceScan.setExtender("deviceScan");

		deviceExpo = new BarChartModel();
		deviceExpo.setBarWidth(50);
		deviceExpo.setLegendPosition("ne");
		deviceExpo.getAxis(AxisType.Y).setMin(0);
        deviceExpo.setExtender("deviceExpo");

		deviceUsage = new BarChartModel();
		deviceUsage.setBarWidth(50);
		deviceUsage.setLegendPosition("ne");
		deviceUsage.setStacked(true);
		deviceUsage.getAxis(AxisType.Y).setMin(0);
		deviceUsage.setExtender("deviceUsage");

		deviceDT = new BarChartModel();
		deviceDT.setBarWidth(50);
		deviceDT.setLegendPosition("ne");
		deviceDT.getAxis(AxisType.Y).setMin(0);
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

        if (!checkInterval(startDate, endDate)) {
        	sqlParams.put("_sql", "");
            logger.error("{} {} {} {} \"{}\" {} \"{}\"", remote_addr, site_id, hospital_id, username, page_uri, sqlParams, "Invalid query interval");
        }
		else
			deviceQuery(startDate, endDate, currentDate);
	}

    private boolean checkInterval(Date startDate, Date endDate) {
        DateTime start = new DateTime(startDate);
        Interval interval = new Interval(start.plusMonths(1).plusDays(-1), start.plusYears(3).plusDays(1));
        boolean flag = interval.contains(new DateTime(endDate));
        if (!flag) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, checkIntervalNotice_1, checkIntervalNotice_2);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return flag;
    }

            //bigint
    private static final String SCANTL
            = "SELECT left_table.name, COUNT(right_table), SUM(expose_count) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND asset_group != 13 AND hospital_id = :#hospital_id) left_table "
            + "LEFT JOIN (SELECT asset_id, expose_count FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

            //bigint
    private static final String VALUESCANTL
            = "SELECT COUNT(right_table), SUM(expose_count) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id) left_table "
            + "LEFT JOIN (SELECT expose_count, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id ";

			//Double
    private static final String BENCHEXPOTL
            = "SELECT left_table.name, left_table.asset_group, bench "
            + "FROM (SELECT id, name, asset_group FROM asset_info WHERE is_valid = true AND asset_group != 13 AND hospital_id = :#hospital_id) left_table "
            + "LEFT JOIN (SELECT asset_group, SUM(expose_count) / COUNT(DISTINCT(asset_id)) bench FROM asset_info JOIN asset_clinical_record ON asset_info.id = asset_clinical_record.asset_id WHERE exam_date BETWEEN :#startDate AND :#endDate GROUP BY asset_group) right_table "
            + "ON left_table.asset_group = right_table.asset_group "
            + "ORDER BY left_table.name ";

			//Integer
	private static final String SERVETL
			= "SELECT DISTINCT name, "
			+ ":#HOURS_DAY * ( "
			+ "CASE WHEN terminate_date IS NULL THEN Date(:#endDate) WHEN Date(:#endDate) < terminate_date THEN Date(:#endDate) ELSE terminate_date END "
			+ "- CASE WHEN Date(:#startDate) > install_date THEN Date(:#startDate) ELSE install_date END ) serve "
			+ "FROM asset_info "
			+ "WHERE hospital_id = :#hospital_id "
			+ "AND is_valid = true "
			+ "ORDER BY name";

			//bigint
	private static final String INUSETL
            = "SELECT left_table.name, right_table.sum inuse "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id) left_table "
            + "LEFT JOIN (SELECT SUM(exam_duration), asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate GROUP BY asset_id) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "ORDER BY left_table.name ";

			 //double
	private static final String DTTL
            = "SELECT left_table.name, right_table.diff/3600 dt "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id) left_table "
            + "LEFT JOIN (SELECT SUM(EXTRACT(EPOCH FROM confirmed_up_time-confirmed_down_time)) diff, asset_id FROM work_order WHERE work_order.status = 2 AND work_order.create_time BETWEEN :#startDate AND :#endDate GROUP BY asset_id) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "ORDER BY left_table.name ";

			 //double
	private static final String BENCHDTTL
            = "SELECT left_table.name, left_table.asset_group, right_table.diff/36/(date(:#endDate) - date(:#startDate))/24 dtbench "
            + "FROM (SELECT id, name, asset_group FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id) left_table "
            + "LEFT JOIN (SELECT asset_group, SUM(EXTRACT(EPOCH FROM(confirmed_up_time-confirmed_down_time))) / COUNT(DISTINCT(asset_id)) diff FROM asset_info JOIN work_order ON asset_info.id = work_order.asset_id WHERE work_order.status = 2 AND work_order.create_time BETWEEN :#startDate AND :#endDate GROUP BY asset_group) right_table "
            + "ON left_table.asset_group = right_table.asset_group "
            + "ORDER BY left_table.name ";


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

	public String getAssetName() {

		return assetName;
	}

	public int getAssetId() {

		return assetId;
	}


	private void devicePanel(Date startDate, Date endDate, Date currentDate) {

		List<Map<String, Object>> rs_panel;

		// Panel Components
        sqlParams.put("_sql", VALUESCANTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);  		
		rs_panel = NativeSqlUtil.queryForList(VALUESCANTL, sqlParams);

		if (!rs_panel.isEmpty()) {
			valueScan = cf.format(rs_panel.get(0).get("count")  != null ? (long)rs_panel.get(0).get("count") : 0);
			valueExpo = cfint.format(rs_panel.get(0).get("sum")  != null ? (double)rs_panel.get(0).get("sum") : 0.0);
		}
		else{
			valueScan = cf.format(0);
			valueExpo = cfint.format(0.0);
		}



	}

	private void deviceChart_12(Date startDate, Date endDate, Date currentDate) {

		ChartSeries cst_scan = new BarChartSeries();
		cst_scan.setLabel(deviceScanlg);
		ChartSeries cst_expo = new BarChartSeries();
		cst_expo.setLabel(deviceExpolg_1);

        sqlParams.put("_sql", SCANTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);  
		List<Map<String, Object>> rs_scan = NativeSqlUtil.queryForList(SCANTL, sqlParams);

		for (Map<String, Object> item : rs_scan) {
			cst_scan.set(item.get("name") != null ? item.get("name").toString() : "",
					item.get("count") != null ? (Long) item.get("count") : 0);
			cst_expo.set(item.get("name") != null ? item.get("name").toString() : "",
					item.get("sum") != null ? (Double) item.get("sum") : (Double) 0.0);
		}

		deviceScan.clear();
		deviceScan.addSeries(cst_scan);

		ChartSeries cst_expo_bench = new LineChartSeries();
		cst_expo_bench.setLabel(deviceExpolg_2);

        sqlParams.put("_sql", BENCHEXPOTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);  
		List<Map<String, Object>> rs_expo_bench = NativeSqlUtil.queryForList(BENCHEXPOTL, sqlParams);

		for (Map<String, Object> item : rs_expo_bench) {
			cst_expo_bench.set(item.get("name") != null ? item.get("name").toString() : "",
					item.get("bench") != null ? (Double) item.get("bench") : (Double) 0.0);
		}

		deviceExpo.clear();
		deviceExpo.addSeries(cst_expo);
		deviceExpo.addSeries(cst_expo_bench);
	}


	private void deviceChart_34(Date startDate, Date endDate, Date currentDate) {

		ChartSeries cst_inuse = new BarChartSeries();
		cst_inuse.setLabel(deviceUsagelg_1);
		ChartSeries cst_wait = new BarChartSeries();
		cst_wait.setLabel(deviceUsagelg_2);
		ChartSeries cst_dt = new BarChartSeries();
		cst_dt.setLabel(deviceDTlg_1);
		ChartSeries cst_dt_bench = new LineChartSeries();
		cst_dt_bench.setLabel(deviceDTlg_2);

        sqlParams.put("_sql", SERVETL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);  
		List<Map<String, Object>> rs_serve = NativeSqlUtil.queryForList(SERVETL, sqlParams);

        sqlParams.put("_sql", INUSETL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);  		
		List<Map<String, Object>> rs_inuse = NativeSqlUtil.queryForList(INUSETL, sqlParams);

        sqlParams.put("_sql", DTTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);  		
		List<Map<String, Object>> rs_dt = NativeSqlUtil.queryForList(DTTL, sqlParams);

        sqlParams.put("_sql", BENCHDTTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);  		
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
		long serve;
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

			asset_name = (item_serve.get("name") != null ? (String) item_serve.get("name") : "");
			serve = item_serve.get("serve") != null ? (int)item_serve.get("serve") : 0;

			inuse = item_inuse.get("inuse") != null ? ((long)item_inuse.get("inuse"))/3600.0 : 0;
			dt =  item_dt.get("dt") != null ? (double)item_dt.get("dt") : 0;
			dt_bench = item_dt_bench.get("dtbench") != null ? (double)item_dt_bench.get("dtbench") : 0;
			wait = serve - dt - inuse;
			if (wait < 0)	wait = 0;

			cst_inuse.set(asset_name, inuse);
			cst_wait.set(asset_name, wait);

			inuse_total += inuse;
			dt_total += dt;
			wait_total += wait;

			dt /= serve;
			dt *= 100;

			if (dt > 100) dt = 100;
			if (dt_bench > 100) dt_bench = 100;

			cst_dt.set(asset_name, dt);
			cst_dt_bench.set(asset_name, dt_bench);

		}

		deviceUsage.clear();
		deviceUsage.addSeries(cst_inuse);
		deviceUsage.addSeries(cst_wait);

		deviceDT.clear();
		deviceDT.addSeries(cst_dt);
		deviceDT.addSeries(cst_dt_bench);

		valueWait = cf.format(wait_total);
		valueInuse =cf.format(inuse_total);
		valueDT = cf.format(dt_total);

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


	private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

		sqlParams.put("hospital_id", hospital_id);
		sqlParams.put("clinical_dept_id", clinical_dept_id);
		sqlParams.put("startDate", startDate);
		sqlParams.put("endDate", endDate);
		sqlParams.put("currentDate", currentDate);
		sqlParams.put("HOURS_DAY", HOURS_DAY);

		devicePanel(startDate, endDate, currentDate);

		deviceChart_12(startDate, endDate, currentDate);

		deviceChart_34(startDate, endDate, currentDate);

	}

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;

        if(asset == null) return;

        this.assetId = asset.getId();
        this.assetName = asset.getName();
        
		WebUtil.navigateTo("/portal/analysis/assetUsageSingle.xhtml?faces-redirect=true&asset_id=" + assetId + "&asset_name=" + assetName);

    }


}
