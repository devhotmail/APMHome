package com.ge.apm.view.analysis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.AxisType;
import org.slf4j.LoggerFactory;

import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.text.NumberFormat;
import java.text.DecimalFormat;

@ManagedBean
@ViewScoped
public class HomeDeptHeadController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HomeDeptHeadController.class);
    
    private static final String deviceScanlg = WebUtil.getMessage("deviceScanlg");
    private static final String deviceExpolg_1 = WebUtil.getMessage("deviceExpolg_1");
    private static final String deviceExpolg_2 = WebUtil.getMessage("deviceExpolg_2");
    private static final String deviceROIlg_1 = WebUtil.getMessage("deviceROIlg_1");
    private static final String deviceROIlg_2 = WebUtil.getMessage("deviceROIlg_2");
    private static final String checkIntervalNotice_1 = WebUtil.getMessage("checkIntervalNotice_1");
    private static final String checkIntervalNotice_2 = WebUtil.getMessage("checkIntervalNotice_2");

    private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    private final int clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();
    
    private HashMap<String, Object> sqlParams = new HashMap<>();

    // Dashboard Parameters
    private String valueScan = null;
    private String valueExpo = null;
    private String valueProfit = null;

    // Chart Components
    private BarChartModel deviceScan = null;
    private BarChartModel deviceExpo = null;
    private BarChartModel deviceProfit = null;
    private Date startDate = null;
    private Date endDate = null;
    private Date currentDate = null;

    private NumberFormat cf = new DecimalFormat(",###.##");
    private NumberFormat cfint = new DecimalFormat(",###");

    @PostConstruct
    public void init() {

        deviceScan = new BarChartModel();
        deviceScan.setBarWidth(50);
        deviceScan.setLegendPosition("ne");
        deviceScan.getAxis(AxisType.Y).setMin(0);
        deviceScan.setExtender("deviceScan");

        deviceExpo = new BarChartModel();
        deviceExpo.setBarWidth(50);
        deviceExpo.setLegendPosition("ne");
        deviceExpo.getAxis(AxisType.Y).setMin(0);
        deviceExpo.setExtender("deviceExposure");

        deviceProfit = new BarChartModel();
        deviceProfit.setBarWidth(50);
        deviceProfit.setLegendPosition("ne");
        deviceProfit.setExtender("deviceProfit");

        valueScan = "";
        valueExpo = "";
        valueProfit = "";

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
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT expose_count, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

            //bigint
    private static final String VALUESCANTL
            = "SELECT COUNT(right_table), SUM(expose_count) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT expose_count, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id ";

            //double
    private static final String REVENUETL
            = "SELECT left_table.name, right_table.sum "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT SUM(price_amount), asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate GROUP BY asset_id) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "ORDER BY left_table.name ";

    private static final String DEPRETL
            = "SELECT left_table.name, deprecate_amount / 365 * (Date(:#endDate) - Date(:#startDate)) depre "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN asset_depreciation "    
            + "ON left_table.id = asset_depreciation.asset_id "    
            + "ORDER BY left_table.name";

    private static final String MAINTAINTL
            = "SELECT left_table.name, right_table.sum "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT SUM(total_price), asset_id FROM work_order WHERE status = 2 AND create_time BETWEEN :#startDate AND :#endDate GROUP BY asset_id) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "ORDER BY left_table.name ";

    private static final String BENCHEXPOTL
            = "SELECT left_table.name, left_table.asset_group, bench "
            + "FROM (SELECT id, name, asset_group FROM asset_info WHERE is_valid = true AND hospital_id = :#hospital_id AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT asset_group, SUM(expose_count) / COUNT(DISTINCT(asset_id)) bench FROM asset_info JOIN asset_clinical_record ON asset_info.id = asset_clinical_record.asset_id WHERE exam_date BETWEEN :#startDate AND :#endDate GROUP BY asset_group) right_table "
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

    public BarChartModel getDeviceProfit() {

        return deviceProfit;
    }

    public String getValueScan() {

        return valueScan;
    }

    public String getValueExpo() {

        return valueExpo;
    }

    public String getValueProfit() {

        return valueProfit;
    }

    private void devicePanel(Date startDate, Date endDate, Date currentDate) {

        List<Map<String, Object>> resultSet;

        // Panel Components
        sqlParams.put("_sql", VALUESCANTL);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
        resultSet = NativeSqlUtil.queryForList(VALUESCANTL, sqlParams);
        

        if(!resultSet.isEmpty()) {
            valueScan = cf.format(resultSet.get(0).get("count") != null ? (long)resultSet.get(0).get("count") : 0);
            valueExpo = cfint.format(resultSet.get(0).get("sum") != null ? (double)resultSet.get(0).get("sum") : 0.0);
        }
        else {
            valueScan = cf.format(0);
            valueExpo = cfint.format(0.0);
        }

    }


    private void deviceChart_12 (Date startDate, Date endDate, Date currentDate) {

            ChartSeries chartSeriesTypeS = new BarChartSeries();
            chartSeriesTypeS.setLabel(deviceScanlg);
            ChartSeries chartSeriesTypeE = new BarChartSeries();
            chartSeriesTypeE.setLabel(deviceExpolg_1);

            sqlParams.put("_sql", SCANTL);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
            List<Map<String, Object>> resultSet = NativeSqlUtil.queryForList(SCANTL, sqlParams);
            
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeS.set(item.get("name").toString(),
                        item.get("count") != null ? (Long) item.get("count") : 0);
                chartSeriesTypeE.set(item.get("name").toString(),
                        item.get("sum") != null ? (Double) item.get("sum") : (Double) 0.0);    
            }

            deviceScan.clear();
            deviceScan.addSeries(chartSeriesTypeS);


            ChartSeries chartSeriesTypeB = new LineChartSeries();
            chartSeriesTypeB.setLabel(deviceExpolg_2);

            sqlParams.put("_sql", BENCHEXPOTL);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
            resultSet = NativeSqlUtil.queryForList(BENCHEXPOTL, sqlParams);
            
            
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeB.set(item.get("name").toString(),
                        item.get("bench") != null ? (Double) item.get("bench") : (Double) 0.0);
            }

            deviceExpo.clear();
            deviceExpo.addSeries(chartSeriesTypeE);
            deviceExpo.addSeries(chartSeriesTypeB);

    }

    private void deviceChart_3 (Date startDate, Date endDate, Date currentDate) {    

            ChartSeries chartSeriesTypeR = new BarChartSeries();
            chartSeriesTypeR.setLabel(deviceROIlg_1);

            sqlParams.put("_sql", REVENUETL);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
            List<Map<String, Object>> resultSet = NativeSqlUtil.queryForList(REVENUETL, sqlParams);
            
            
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeR.set(item.get("name").toString(),
                        item.get("sum") != null ? (Double) item.get("sum") : (Double) 0.0);
            }

            ChartSeries chartSeriesTypeP = new BarChartSeries();
            chartSeriesTypeP.setLabel(deviceROIlg_2);

            sqlParams.put("_sql", DEPRETL);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
            List<Map<String, Object>> sub_x_resultSet = NativeSqlUtil.queryForList(DEPRETL, sqlParams);
            
            sqlParams.put("_sql", MAINTAINTL);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
            List<Map<String, Object>> sub_y_resultSet = NativeSqlUtil.queryForList(MAINTAINTL, sqlParams);
            
            
            Iterator<Map<String, Object>> it_a;
            Iterator<Map<String, Object>> it_x;
            Iterator<Map<String, Object>> it_y;
            Map<String, Object> item_a;
            Map<String, Object> item_x;
            Map<String, Object> item_y;
            double profit_loop;
            double profit_total = 0.0;
            String asset_name;

            for (it_a = resultSet.iterator(), it_x = sub_x_resultSet.iterator(), it_y = sub_y_resultSet.iterator();
                    it_a.hasNext() && it_x.hasNext() && it_y.hasNext();) {

                item_a = it_a.next();
                item_x = it_x.next();
                item_y = it_y.next();

                asset_name = (item_a.get("name") != null ? (String) item_a.get("name") : "");

                profit_loop = (item_a.get("sum") != null ? (double) item_a.get("sum") : (double) 0.0)
                        - (item_x.get("depre") != null ? (double) item_x.get("depre") : (double) 0.0)
                        - (item_y.get("sum") != null ? (double) item_y.get("sum") : (double) 0.0);

                chartSeriesTypeP.set(asset_name, profit_loop);
                profit_total += profit_loop;
            }

            valueProfit = cf.format(profit_total);

            deviceProfit.clear();
            deviceProfit.addSeries(chartSeriesTypeR);
            deviceProfit.addSeries(chartSeriesTypeP);

    }

    private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

        sqlParams.put("hospital_id", hospital_id);
        sqlParams.put("clinical_dept_id", clinical_dept_id);
        sqlParams.put("startDate", startDate);
        sqlParams.put("endDate", endDate);
        sqlParams.put("currentDate", currentDate);

        devicePanel(startDate, endDate, currentDate);

        deviceChart_12(startDate, endDate, currentDate);

        deviceChart_3(startDate, endDate, currentDate);
        

    }

}
