package com.ge.apm.view.analysis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

import webapp.framework.dao.NativeSqlUtil;

@ManagedBean
@ViewScoped
public class HomeDeptHeadController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static String ASST = "Assets Type";
    private static String TIMES = "Time(s)";
    private static String HOURS = "Hour(s)";
    private static String CNY = "CNY";
    private static String DVSC = "Device Scan";
    private static String DVEP = "Device Exposure";
    private static String DVEB = "Device Exposure (baseline)";
    private static String DVRN = "Device Revenue";
    private static String DVPF = "Device Profit";

    // Dashboard Parameters
    private String valueDeviceScan = null;
    private String valueDeviceExposure = null;
    private String valueDeviceProfit = null;

    // Chart Components
    private BarChartModel deviceScan = null;
    private BarChartModel deviceExposure = null;
    private BarChartModel deviceProfit = null;
    private Date startDate = null;
    private Date endDate = null;
    private Date currentDate = null;
    private boolean start_bool = false;
    private boolean end_bool = false;
    //private String DATE_FORMAT = "yyyy-MM-dd";
    private int hospitalId = -1;
    private int clinical_dept_id = -1;

    //debug param
    @PostConstruct
    public void init() {

        Calendar currentCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.YEAR, -1);

        startDate = startCal.getTime();
        endDate = currentCal.getTime();
        currentDate = currentCal.getTime();

        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        clinical_dept_id = UserContextService.getCurrentUserAccount().getOrgInfoId();

        deviceQuery(startDate, endDate, currentDate);

    }

    public void submit() {

    }

    // Chart SQL
    private String SCANTL
            = "SELECT DISTINCT a.name, COUNT(r.id) count "
            + "FROM asset_info a LEFT JOIN asset_clinical_record r "
            + "ON r.asset_id = a.id "
            + "WHERE a.hospital_id = :#hospitalId AND a.clinical_dept_id = :#clinical_dept_id "
            + "AND r.exam_date BETWEEN :#startDate AND :#endDate "
            + "GROUP BY a.name ORDER BY a.name";
    //hospitalId, clinical_dept_id, startDateString, endDateString);

    private String VALUESCANTL
            = "SELECT SUM(t.count) FROM ( "
            + SCANTL
            + " ) AS t";

    private String EXPOTL
            = "SELECT DISTINCT a.name, SUM(r.expose_count) hours "
            + "FROM asset_info a LEFT JOIN asset_clinical_record r "
            + "ON a.id = r.asset_id "
            + "WHERE a.hospital_id = :#hospitalId AND a.clinical_dept_id = :#clinical_dept_id "
            + "AND r.exam_date BETWEEN :#startDate AND :#endDate "
            + "GROUP BY a.name ORDER BY a.name";
    //hospitalId, clinical_dept_id, startDateString, endDateString);

    private String VALUEEXPOTL
            = "SELECT SUM(t.hours) FROM ( "
            + EXPOTL
            + " ) AS t";

    private String BENCHEXPOTL
            = "SELECT DISTINCT a.name, SUM(r.expose_count) * (Date(:#endDate) - Date(:#startDate)) / (Date(:#currentDate) - MIN(r.exam_date)) bench "
            + "FROM asset_info a LEFT JOIN asset_clinical_record r "
            + "ON a.id = r.asset_id "
            + "WHERE a.hospital_id = :#hospitalId AND a.clinical_dept_id = :#clinical_dept_id "
            + "GROUP BY a.name ORDER BY a.name";
    //endDateString, startDateString, hospitalId, clinical_dept_id);

    private String REVENUETL
            = "SELECT DISTINCT a.name, SUM(r.price_amount) revenue "
            + "FROM asset_info a LEFT JOIN asset_clinical_record r "
            + "ON r.asset_id = a.id "
            + "WHERE a.hospital_id = :#hospitalId AND a.clinical_dept_id = :#clinical_dept_id "
            + "AND r.exam_date BETWEEN :#startDate AND :#endDate "
            + "GROUP BY a.name ORDER BY a.name";
    //hospitalId, clinical_dept_id, startDateString, endDateString);

    /*
	private String deviceRevenueSQLValue = String.format(
			"SELECT SUM(t.revenue) FROM ( "
			+ deviceRevenueSQL
			+ " ) AS t;"
			);	*/
    private String DEPRETL
            = "SELECT DISTINCT a.name, a.purchase_price / a.lifecycle * (Date(:#endDate) - Date(:#startDate)) / 365 depre "
            + "FROM asset_info a "
            + "WHERE a.hospital_id = :#hospitalId AND a.clinical_dept_id = :#clinical_dept_id "
            + "ORDER BY a.name";
    //endDateString, startDateString, hospitalId, clinical_dept_id);

    private String MAINTAINTL
            = "SELECT DISTINCT a.name, w.total_price "
            + "FROM asset_info a LEFT JOIN work_order w "
            + "ON w.asset_id = a.id AND w.create_time BETWEEN :#startDate AND :#endDate AND w.is_closed = true "
            + "WHERE a.hospital_id = :#hospitalId AND a.clinical_dept_id = :#clinical_dept_id "
            + "ORDER BY a.name";
    //startDateString, endDateString, hospitalId, clinical_dept_id);

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

    public BarChartModel getDeviceExposure() {

        return deviceExposure;
    }

    public BarChartModel getDeviceProfit() {

        return deviceProfit;
    }

    public String getValueDeviceScan() {

        return valueDeviceScan;
    }

    public String getValueDeviceExposure() {

        return valueDeviceExposure;
    }

    public String getValueDeviceProfit() {

        return valueDeviceProfit;
    }

    private void deviceQuery(Date startDate, Date endDate, Date currentDate) {

        if ((null == startDate) || null == endDate | null == currentDate) {

            System.out.println("NULL");
            System.exit(0);
            Calendar startCal = Calendar.getInstance();
            Calendar endCal = startCal;
            startCal.add(Calendar.YEAR, -1);

            startDate = startCal.getTime();
            endDate = endCal.getTime();
        }

        HashMap<String, Object> sqlParams = new HashMap<>();

        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("clinical_dept_id", clinical_dept_id);
        sqlParams.put("startDate", startDate);
        sqlParams.put("endDate", endDate);
        sqlParams.put("currentDate", currentDate);

        List<Map<String, Object>> resultSet;

        // Panel Components
        System.out.println(startDate.toString() + endDate.toString() + currentDate.toString());
        resultSet = NativeSqlUtil.queryForList(VALUESCANTL, sqlParams);
        valueDeviceScan = (resultSet.get(0).get("sum") != null ? resultSet.get(0).get("sum").toString() : "");

        resultSet = NativeSqlUtil.queryForList(VALUEEXPOTL, sqlParams);
        valueDeviceExposure = (resultSet.get(0).get("sum") != null ? resultSet.get(0).get("sum").toString().substring(0, resultSet.get(0).get("sum").toString().indexOf(".")) : "");

        // Chart Components
        {
            //	---
            deviceScan = new BarChartModel();
            deviceScan.getAxis(AxisType.X).setLabel(ASST);
            deviceScan.getAxis(AxisType.Y).setLabel(TIMES);
            deviceScan.setBarWidth(50);
            deviceScan.setLegendPosition("ne");
            deviceScan.setExtender("deviceScan");


            ChartSeries chartSeriesTypeS = new BarChartSeries();
            chartSeriesTypeS.setLabel(DVSC);

            resultSet = NativeSqlUtil.queryForList(SCANTL, sqlParams);

            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeS.set(item.get("name").toString(),
                        item.get("count") != null ? (Long) item.get("count") : 0);
            }

            deviceScan.addSeries(chartSeriesTypeS);

            // ---
            deviceExposure = new BarChartModel();
            deviceExposure.getAxis(AxisType.X).setLabel(ASST);
            deviceExposure.getAxis(AxisType.Y).setLabel(HOURS);
            deviceExposure.setBarWidth(50);
            deviceExposure.setLegendPosition("ne");
            deviceExposure.setExtender("deviceExposure");

            ChartSeries chartSeriesTypeE = new BarChartSeries();
            chartSeriesTypeE.setLabel(DVEP);

            resultSet = NativeSqlUtil.queryForList(EXPOTL, sqlParams);
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeE.set(item.get("name").toString(),
                        item.get("hours") != null ? (Double) item.get("hours") : (Double) 0.0);
            }

            ChartSeries chartSeriesTypeB = new LineChartSeries();
            chartSeriesTypeB.setLabel(DVEB);

            resultSet = NativeSqlUtil.queryForList(BENCHEXPOTL, sqlParams);
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeB.set(item.get("name").toString(),
                        item.get("bench") != null ? (Double) item.get("bench") : (Double) 0.0);
            }

            deviceExposure.addSeries(chartSeriesTypeE);
            deviceExposure.addSeries(chartSeriesTypeB);

            // ---
            deviceProfit = new BarChartModel();
            deviceProfit.getAxis(AxisType.X).setLabel(ASST);
            deviceProfit.getAxis(AxisType.Y).setLabel(CNY);
            deviceProfit.setBarWidth(50);
            deviceProfit.setLegendPosition("ne");
            deviceProfit.setExtender("deviceProfit");

            ChartSeries chartSeriesTypeR = new BarChartSeries();
            chartSeriesTypeR.setLabel(DVRN);

            resultSet = NativeSqlUtil.queryForList(REVENUETL, sqlParams);
            for (Map<String, Object> item : resultSet) {
                chartSeriesTypeR.set(item.get("name").toString(),
                        item.get("revenue") != null ? (Double) item.get("revenue") : (Double) 0.0);
            }

            ChartSeries chartSeriesTypeP = new BarChartSeries();
            chartSeriesTypeP.setLabel(DVPF);

            List<Map<String, Object>> sub_x_resultSet = NativeSqlUtil.queryForList(DEPRETL, sqlParams);
            List<Map<String, Object>> sub_y_resultSet = NativeSqlUtil.queryForList(MAINTAINTL, sqlParams);
            Iterator<Map<String, Object>> it_a;
            Iterator<Map<String, Object>> it_x;
            Iterator<Map<String, Object>> it_y;
            Map<String, Object> item_a;
            Map<String, Object> item_x;
            Map<String, Object> item_y;
            Double tmpDouble;
            Double valueDeviceProfitDouble = 0.0;

            for (it_a = resultSet.iterator(), it_x = sub_x_resultSet.iterator(), it_y = sub_y_resultSet.iterator();
                    it_a.hasNext() && it_x.hasNext() && it_y.hasNext();) {

                item_a = it_a.next();
                item_x = it_x.next();
                item_y = it_y.next();

                tmpDouble = (item_a.get("revenue") != null ? (Double) item_a.get("revenue") : (Double) 0.0)
                        - (item_x.get("depre") != null ? (Double) item_x.get("depre") : (Double) 10000.0)
                        - (item_y.get("total_price") != null ? (Double) item_y.get("total_price") : (Double) 2000.0);

                chartSeriesTypeP.set(item_a.get("name").toString(), tmpDouble);
                valueDeviceProfitDouble += tmpDouble;
            }
            valueDeviceProfit = valueDeviceProfitDouble.toString();

            deviceProfit.addSeries(chartSeriesTypeR);
            deviceProfit.addSeries(chartSeriesTypeP);

        }

    }

}
