package com.ge.apm.view.analysis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.view.sysutil.UrlEncryptController;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.slf4j.LoggerFactory;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.DateAxis;

import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@ViewScoped
public class AssetUsageSingleController implements ServerEventInterface {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AssetUsageSingleController.class);
    
	private static final String deviceScanlg = WebUtil.getMessage("deviceScanlg");
	private static final String deviceExpolg_1 = WebUtil.getMessage("deviceExpolg_1");
	private static final String deviceUsagelg_1 = WebUtil.getMessage("deviceUsagelg_1");
	private static final String deviceUsagelg_2 = WebUtil.getMessage("deviceUsagelg_2");
	private static final String deviceUsagelg_3 = WebUtil.getMessage("deviceUsagelg_3");
    private static final String checkIntervalNotice_1 = WebUtil.getMessage("checkIntervalNotice_1");
    private static final String checkIntervalNotice_2 = WebUtil.getMessage("checkIntervalNotice_2");
	private final int HOURS_DAY = 24;

	private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    private final int clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();
    
	private HashMap<String, Object> sqlParams = new HashMap<>();  

	private static final String DATA_FORMAT = "yyyy-MM-dd";
	
    
	private int assetId = -1;

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

	private NumberFormat cf = new DecimalFormat(",###.##");
	private NumberFormat cfint = new DecimalFormat(",###");

	@PostConstruct
	public void init() {

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
        deviceScan.getAxis(AxisType.Y).setMin(0);
        deviceScan.setExtender("deviceScan");

		deviceExpo = new LineChartModel();
		deviceExpo.setLegendPosition("ne");
        deviceExpo.getAxes().put(AxisType.X, axis);
        deviceExpo.setZoom(true);
        deviceExpo.getAxis(AxisType.Y).setMin(0);
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
		startCal.add(Calendar.YEAR, -1);

		startDate = startCal.getTime();
		endDate = currentCal.getTime();
		currentDate = currentCal.getTime();

		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("asset_id") != null 
			&& FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("asset_name") != null) {

			assetId = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("asset_id"));
			assetName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("asset_name");
			deviceQuery(startDate, endDate, currentDate);
		}

        //It's for AssetInfoDetail page, using decrypt Url parameters
        String encodeStr = WebUtil.getRequestParameter("str");
        if (null != encodeStr) {
            assetId = Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid"));
            assetName = (String) UrlEncryptController.getValueFromMap(encodeStr, "asset_name");
            deviceQuery(startDate, endDate, currentDate);
	}
    }

	@Override
    public void onServerEvent(String eventName, Object eventObject)	{
        AssetInfo asset = (AssetInfo) eventObject;

        if(asset == null) return;

        this.assetId = asset.getId();
        this.assetName = asset.getName();
        
		WebUtil.navigateTo("/portal/analysis/assetUsageSingle.xhtml?faces-redirect=true&asset_id=" + assetId + "&asset_name=" + assetName);

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

	// Chart SQL
	private static final String SCANTL
			= "SELECT to_char(exam_date, 'YYYY-MM-DD') timeline, COUNT(asset_id) count "
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#assetId "
			+ "AND exam_date BETWEEN :#startDate AND :#endDate "
			+ "GROUP BY exam_date "
			+ "ORDER BY exam_date";

	private static final String VALUESCANTL
			= "SELECT COUNT(asset_id) sum "
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#assetId "
			+ "AND exam_date BETWEEN :#startDate AND :#endDate "
			+ "GROUP BY asset_id";

			//Double
	private static final String EXPOTL
			= "SELECT to_char(exam_date, 'YYYY-MM-DD') timeline, SUM(expose_count) hours "
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#assetId "
			+ "AND exam_date BETWEEN :#startDate AND :#endDate "
			+ "GROUP BY exam_date "
			+ "ORDER BY exam_date";

	private static final String VALUEEXPOTL
			= "SELECT SUM(expose_count) sum "
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#assetId "
			+ "AND exam_date BETWEEN :#startDate AND :#endDate "
			+ "GROUP BY asset_id";

			//Integer
	private static final String SERVETL
			= "SELECT CASE WHEN Date(:#startDate) > install_date THEN Date(:#startDate) ELSE install_date END serve1, "
			+ "CASE WHEN terminate_date IS NULL THEN Date(:#endDate) WHEN Date(:#endDate) < terminate_date THEN Date(:#endDate) ELSE terminate_date END serve2, "
			+ ":#HOURS_DAY * ( "
			+ "CASE WHEN terminate_date IS NULL THEN Date(:#endDate) WHEN Date(:#endDate) < terminate_date THEN Date(:#endDate) ELSE terminate_date END "
			+ "- CASE WHEN Date(:#startDate) > install_date THEN Date(:#startDate) ELSE install_date END ) serve "
			+ "FROM asset_info "
			+ "WHERE id = :#assetId ";

			//Integer
	private static final String INUSETL
			= "SELECT to_char(exam_date, 'YYYY-MM-DD') timeline, "
			+ "SUM(exam_duration)/60 inuse "
			+ "FROM asset_clinical_record "
			+ "WHERE asset_id = :#assetId "
			+ "AND exam_date BETWEEN :#startDate AND :#endDate "
			+ "GROUP BY exam_date "
			+ "ORDER BY exam_date";

			//Double
	private static final String DTTL
			= "SELECT to_char(confirmed_down_time, 'YYYY-MM-DD') timeline, "
			+ "EXTRACT(EPOCH FROM confirmed_up_time-confirmed_down_time) diff_total, "
			+ "EXTRACT(EPOCH FROM confirmed_down_time) today "
			+ "FROM work_order "
			+ "WHERE asset_id = :#assetId "
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
		sqlParams.put("startDate", startDate);

    }

    public Date getEndDate() {

        return endDate;
    }

    public void setEndDate(Date endDate) {

        this.endDate = endDate;
		sqlParams.put("endDate", endDate);

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

    public int getAssetId() {

        return assetId;
    }


	private void deviceChart_1(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

		DateTime startJoda = new DateTime(startDate);
		DateTime endJoda = new DateTime(endDate);

		ChartSeries cst_scan = new LineChartSeries();
		cst_scan.setLabel(deviceScanlg);

        sqlParams.put("_sql", SCANTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);  
		List<Map<String, Object>> rs_scan = NativeSqlUtil.queryForList(SCANTL, sqlParams);

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
				cst_scan.set(idate, (Long) rs_scan_map.get(idate));
			}
			else {
				cst_scan.set(idate, 0);
			}
		}

		deviceScan.clear();
		deviceScan.addSeries(cst_scan);

	}

	private void deviceChart_2(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

		DateTime startJoda = new DateTime(startDate);
		DateTime endJoda = new DateTime(endDate);

		ChartSeries cst_expo = new LineChartSeries();
		cst_expo.setLabel(deviceExpolg_1);

        sqlParams.put("_sql", EXPOTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
		List<Map<String, Object>> rs_expo = NativeSqlUtil.queryForList(EXPOTL, sqlParams);

		Map<String, Object> rs_expo_map = new HashMap<String, Object>();
		if (!rs_expo.isEmpty())
			for (Map<String, Object> item : rs_expo) {
				rs_expo_map.put((String)item.get("timeline"), item.get("hours") != null ? (Double) item.get("hours") : 0);
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
		cst_dt.setLabel(deviceUsagelg_3);
		cst_dt.setFill(true);

		double inuse;
		double dt;
		double wait; // wait = serve - downtime - inuse

		double serve_total;
		double inuse_total = 0.0;
		double dt_total= 0.0;
		double wait_total = 0.0; // wait = serve - downtime - inuse

		DateTime serve1;
		DateTime serve2;

		double diff_total;
		double today;
		String timeline;
		DateTime currentJoda;
		
        sqlParams.put("_sql", SERVETL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 		
		List<Map<String, Object>> rs_serve_hour = NativeSqlUtil.queryForList(SERVETL, sqlParams);

		if (!rs_serve_hour.isEmpty()) {
			serve1 = new DateTime(rs_serve_hour.get(0).get("serve1") != null ? (Date) rs_serve_hour.get(0).get("serve1") : startDate);
			serve2 = new DateTime(rs_serve_hour.get(0).get("serve2") != null ? (Date) rs_serve_hour.get(0).get("serve2") : endDate);
			serve_total = rs_serve_hour.get(0).get("serve") != null ? (int) rs_serve_hour.get(0).get("serve") : 0;
		}
		else {
			serve1 = new DateTime(startDate);
			serve2 = new DateTime(endDate);
			serve_total = 0;
		}
		
        sqlParams.put("_sql", INUSETL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 			
		List<Map<String, Object>> rs_inuse = NativeSqlUtil.queryForList(INUSETL, sqlParams);

        sqlParams.put("_sql", DTTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 			
		List<Map<String, Object>> rs_dt = NativeSqlUtil.queryForList(DTTL, sqlParams);

		Map<String, Object> rs_inuse_map = new HashMap<String, Object>();

		if (!rs_inuse.isEmpty())
			for (Map<String, Object> item : rs_inuse) {
				inuse = item.get("inuse") != null ? ((long)item.get("inuse"))/60.0 : 0.0;
				inuse_total += inuse;
				rs_inuse_map.put(item.get("timeline").toString(), inuse);
		}

		rs_inuse.clear();

		Map<String, Object> rs_dt_map = new HashMap<String, Object>();

		if (!rs_dt.isEmpty())
			for (Map<String, Object> item : rs_dt) {
				
				if (item.get("diff_total") != null) {

					timeline = item.get("timeline").toString();
					currentJoda = new DateTime( Integer.valueOf(timeline.split("-")[0]), Integer.valueOf(timeline.split("-")[1]), Integer.valueOf(timeline.split("-")[2]), 0, 0, 0, 0, DateTimeZone.forOffsetHours(0));
			
					diff_total =  item.get("diff_total") != null ? (double) item.get("diff_total")/3600 : 0.0;
					dt_total += diff_total;
					today = item.get("today") != null ? (double) item.get("today") : 0;

					dt = (currentJoda.plusDays(1).getMillis()/1000L - today)/3600;

					rs_dt_map.put(timeline, dt);

					diff_total -= dt;

					while ( diff_total>0.0001 ) {
						currentJoda = currentJoda.plusDays(1);
						if (diff_total < 24) {
							rs_dt_map.put(currentJoda.toString(DATA_FORMAT),diff_total);
						}
						else {
							rs_dt_map.put(currentJoda.toString(DATA_FORMAT), (double)24.0);
						}
						diff_total -= 24;
					}
				}
			}

		rs_dt.clear();

		wait_total += serve_total - inuse_total - dt_total;

		String idate;

		for (startJoda = new DateTime(startDate); startJoda.isBefore(endJoda.plusDays(1)); startJoda = startJoda.plusDays(1)) {

			inuse = 0.0;
			dt = 0.0;
			wait = 0.0;

			idate = startJoda.toString(DATA_FORMAT);

			if ( rs_inuse_map.containsKey(idate)) {
				inuse = (double) rs_inuse_map.get(idate);
				cst_inuse.set(idate, inuse);
			}
			else {
				cst_inuse.set(idate, 0.0);
			}

			if ( rs_dt_map.containsKey(idate)) {
				dt = (double) rs_dt_map.get(idate);
				cst_dt.set(idate, dt);
			}
			else {
				cst_dt.set(idate, 0.0);
			}

			if (startJoda.isBefore(serve2.plusDays(1)) && startJoda.isAfter(serve1))
				wait = HOURS_DAY - inuse - dt;
			else
				wait = 0;

			if (wait < 0)	wait = 0.0;
			cst_wait.set(idate, wait);
		}

		deviceUsage.clear();
		deviceUsage.addSeries(cst_dt);
		deviceUsage.addSeries(cst_inuse);
		deviceUsage.addSeries(cst_wait);

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

	private void devicePanel(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

		List<Map<String, Object>> rs_panel;

		// Panel Components
        sqlParams.put("_sql", VALUESCANTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 		
		rs_panel = NativeSqlUtil.queryForList(VALUESCANTL, sqlParams);

		if (!rs_panel.isEmpty())
			valueScan = cf.format(rs_panel.get(0).get("sum")  != null ? (long)rs_panel.get(0).get("sum") : 0);
		else
			valueScan = cf.format(0);

        sqlParams.put("_sql", VALUEEXPOTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
		rs_panel = NativeSqlUtil.queryForList(VALUEEXPOTL, sqlParams);

		if (!rs_panel.isEmpty())
			valueExpo = cfint.format(rs_panel.get(0).get("sum")  != null ? (double)rs_panel.get(0).get("sum") : 0.0);
		else
			valueExpo = cfint.format(0.0);

	}

	private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

		HashMap<String, Object> sqlParams = new HashMap<>();

		sqlParams.put("hospital_id", hospital_id);
		sqlParams.put("clinical_dept_id", clinical_dept_id);
		sqlParams.put("startDate", startDate);
		sqlParams.put("endDate", endDate);
		sqlParams.put("currentDate", currentDate);
		sqlParams.put("assetId", assetId);
		sqlParams.put("HOURS_DAY", HOURS_DAY);

		devicePanel(startDate, endDate, currentDate, sqlParams);

		deviceChart_1(startDate, endDate, currentDate, sqlParams);

		deviceChart_2(startDate, endDate, currentDate, sqlParams);

		deviceChart_3(startDate, endDate, currentDate, sqlParams);

	}

}
