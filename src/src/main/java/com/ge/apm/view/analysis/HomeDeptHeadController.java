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

import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class HomeDeptHeadController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String deviceScanlg = WebUtil.getMessage("deviceScanlg");
    private static final String deviceExpolg_1 = WebUtil.getMessage("deviceExpolg_1");
    private static final String deviceExpolg_2 = WebUtil.getMessage("deviceExpolg_2");
    private static final String deviceROIlg_1 = WebUtil.getMessage("deviceROIlg_1");
    private static final String deviceROIlg_2 = WebUtil.getMessage("deviceROIlg_2");

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
    private int hospitalId = -1;
    private int clinical_dept_id = -1;

    @PostConstruct
    public void init() {

        deviceScan = new BarChartModel();
        deviceScan.setBarWidth(50);
        deviceScan.setLegendPosition("ne");
        deviceScan.setExtender("deviceScan");

        deviceExpo = new BarChartModel();
        deviceExpo.setBarWidth(50);
        deviceExpo.setLegendPosition("ne");
        deviceExpo.setExtender("deviceExposure");

        deviceProfit = new BarChartModel();
        deviceProfit.setBarWidth(50);
        deviceProfit.setLegendPosition("ne");
        deviceProfit.setExtender("deviceProfit");


        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();

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
        deviceQuery(startDate, endDate, currentDate);

    }

            //bigint
    private String SCANTL
            = "SELECT left_table.name, COUNT(right_table) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

            //bigint
    private String VALUESCANTL
            = "SELECT COUNT(right_table) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT id, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id ";

            //double
    private String EXPOTL
            = "SELECT left_table.name, SUM(right_table.expose_count) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT expose_count, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

            //double
    private String VALUEEXPOTL
            = "SELECT SUM(right_table.expose_count) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT expose_count, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id ";

            //double
    private String REVENUETL
            = "SELECT left_table.name, SUM(right_table.price_amount) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT price_amount, asset_id FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

    private String DEPRETL
            = "SELECT DISTINCT name, salvage_value / 365 * (Date(:#endDate) - Date(:#startDate)) depre "
            + "FROM asset_info "
            + "WHERE is_valid = true AND hospital_id = :#hospitalId AND clinical_dept_id = :#clinical_dept_id "        
            + "ORDER BY a.name";

    private String MAINTAINTL
            = "SELECT left_table.name, SUM(right_table.total_price) "
            + "FROM (SELECT id, name FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT total_price, asset_id FROM work_order WHERE is_closed = true AND create_time BETWEEN :#startDate AND :#endDate) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

    private String BENCHEXPOTL
            = "SELECT left_table.name, SUM(right_table.sum) "
            + "FROM (SELECT id, name, asset_group FROM asset_info WHERE is_valid = true AND hospital_id = :#hospitalId AND clinical_dept_id = :#clinical_dept_id) left_table "
            + "LEFT JOIN (SELECT asset_id, SUM(expose_count) FROM asset_clinical_record WHERE exam_date BETWEEN :#startDate AND :#endDate GROUP BY asset_id) right_table "
            + "ON left_table.id = right_table.asset_id "
            + "GROUP BY left_table.name "
            + "ORDER BY left_table.name ";

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

    private void devicePanel(Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

        List<Map<String, Object>> resultSet;

        // Panel Components
        resultSet = NativeSqlUtil.queryForList(VALUESCANTL, sqlParams);
        if(!resultSet.isEmpty())
            valueScan = (resultSet.get(0).get("sum") != null ? resultSet.get(0).get("sum").toString() : "");

        resultSet = NativeSqlUtil.queryForList(VALUEEXPOTL, sqlParams);
        if(!resultSet.isEmpty())
            valueExpo = (resultSet.get(0).get("sum") != null ? resultSet.get(0).get("sum").toString().substring(0, resultSet.get(0).get("sum").toString().indexOf(".")) : "");

    }


    private void deviceChart_1 (Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

            ChartSeries chartSeriesTypeS = new BarChartSeries();
            chartSeriesTypeS.setLabel(deviceScanlg);

            List<Map<String, Object>> resultSet = NativeSqlUtil.queryForList(SCANTL, sqlParams);

            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeS.set(item.get("name").toString(),
                        item.get("count") != null ? (Long) item.get("count") : 0);
            }

            deviceScan.clear();
            deviceScan.addSeries(chartSeriesTypeS);
    }

    private void deviceChart_2 (Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {

            ChartSeries chartSeriesTypeE = new BarChartSeries();
            chartSeriesTypeE.setLabel(deviceExpolg_1);

            List<Map<String, Object>> resultSet = NativeSqlUtil.queryForList(EXPOTL, sqlParams);
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeE.set(item.get("name").toString(),
                        item.get("hours") != null ? (Double) item.get("hours") : (Double) 0.0);
            }

            ChartSeries chartSeriesTypeB = new LineChartSeries();
            chartSeriesTypeB.setLabel(deviceExpolg_2);

            resultSet = NativeSqlUtil.queryForList(BENCHEXPOTL, sqlParams);
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeB.set(item.get("name").toString(),
                        item.get("sum") != null ? (Double) item.get("sum") : (Double) 0.0);
            }

            deviceExpo.clear();
            deviceExpo.addSeries(chartSeriesTypeE);
            deviceExpo.addSeries(chartSeriesTypeB);

    }

    private void deviceChart_3 (Date startDate, Date endDate, Date currentDate, HashMap<String, Object> sqlParams) {    

            ChartSeries chartSeriesTypeR = new BarChartSeries();
            chartSeriesTypeR.setLabel(deviceROIlg_1);

            List<Map<String, Object>> resultSet = NativeSqlUtil.queryForList(REVENUETL, sqlParams);
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeR.set(item.get("name").toString(),
                        item.get("sum") != null ? (Double) item.get("sum") : (Double) 0.0);
            }

            ChartSeries chartSeriesTypeP = new BarChartSeries();
            chartSeriesTypeP.setLabel(deviceROIlg_2);

            List<Map<String, Object>> sub_x_resultSet = NativeSqlUtil.queryForList(DEPRETL, sqlParams);
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

                asset_name = (item_a.get("name") != null ? (String) item_a.get("sum") : "");

                profit_loop = (item_a.get("sum") != null ? (double) item_a.get("sum") : (double) 0.0)
                        - (item_x.get("depre") != null ? (double) item_x.get("depre") : (double) 0.0)
                        - (item_y.get("sum") != null ? (double) item_y.get("sum") : (double) 0.0);

                chartSeriesTypeP.set(asset_name, profit_loop);
                profit_total += profit_loop;
            }

            valueProfit = new Double(profit_total).toString();
            valueProfit = valueProfit.substring(0, valueProfit.indexOf(".")+2);

            deviceProfit.clear();
            deviceProfit.addSeries(chartSeriesTypeR);
            deviceProfit.addSeries(chartSeriesTypeP);

    }

    private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

        HashMap<String, Object> sqlParams = new HashMap<>();
        
        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("clinical_dept_id", clinical_dept_id);
        sqlParams.put("startDate", startDate);
        sqlParams.put("endDate", endDate);
        sqlParams.put("currentDate", currentDate);

        devicePanel(startDate, endDate, currentDate, sqlParams);

        deviceChart_1(startDate, endDate, currentDate, sqlParams);
        
        deviceChart_2(startDate, endDate, currentDate, sqlParams);

        deviceChart_3(startDate, endDate, currentDate, sqlParams);

    }

}
