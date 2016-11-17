package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.LocalDate;


import java.time.Month;
import org.slf4j.LoggerFactory;
import webapp.framework.broker.SiBroker;
import webapp.framework.codegen.CodeGenEngine;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;

import webapp.framework.dao.NativeSqlUtil;
import org.apache.log4j.Logger;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@ViewScoped
public class HomeHeadController extends SqlConfigurableChartController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HomeHeadController.class);

    private double anualCost = 0.0;
    private int revenue = 0;
    private String logStr;
    private List<Map<String, Object>> profit = new ArrayList<>();
    
    //UI Params
    private double totalProfit = 0;
    private double profitForecast = 0;

    private HashMap<String, String> yearList = new HashMap<>();

    private BarChartModel barAnnualRevenue = new BarChartModel();
    private PieChartModel pieAnnualRevenue = new PieChartModel();

    private BarChartModel barMonthlyRevenue = new BarChartModel();
    private BarChartModel barMonthlyForecast = new BarChartModel();

    // chart parameters
    private int targetYear = Year.now().getValue();
    private int hospitalId = 0;
    private String selectedYear = Integer.toString(targetYear);

    private String sql;
    private HashMap<String, Object> sqlParams = new HashMap<>();

    private boolean isYearly = false;
    private DateTime startMonth = new DateTime();

    private List<Map<String, Object>> monthRevenue  = new ArrayList<>();
    private List<Map<String, Object>> monthDep      = new ArrayList<>();
    private List<Map<String, Object>> monthMaint    = new ArrayList<>();
    private List<Map<String, Object>> monthProfit   = new ArrayList<>();

    private List<Map<String, Object>> olderRevenue  = new ArrayList<>();
    private List<Map<String, Object>> olderDep      = new ArrayList<>();
    private List<Map<String, Object>> olderMaint    = new ArrayList<>();
    private List<Map<String, Object>> olderProfit   = new ArrayList<>();

    // SQL script
    private String last12MonthRevenue =
            "select sum(r.price_amount) as value, " +
            "to_char(r.exam_date, 'yyyy-mm') as key " +
            "from asset_info a, asset_clinical_record r " +
            "where to_char(r.exam_date, 'yyyy-mm') between to_char(now() - interval '1 year', 'yyyy-mm') " +
            "and to_char(now(), 'yyyy-mm') " +
            "and a.id = r.asset_id " +
            "and a.hospital_id = :#hospitalId " +
            "group by key order by key;";

    private String targetYearMonthRevenue =
            "select sum(r.price_amount) as value, " +
            "to_char(r.exam_date, 'yyyy-mm') as key " +
            "from asset_info a, asset_clinical_record r " +
            "where extract(year from r.exam_date) = :#targetYear " +
            "and a.id = r.asset_id " +
            "and a.hospital_id = :#hospitalId " +
            "group by key order by key;";

    private String depMonth =
            "select a.hospital_id as key, " +
            "sum(d.deprecate_amount) as value " +
            "from asset_info a, asset_depreciation d " +
            "where a.id = d.asset_id " +
            "and a.hospital_id = :#hospitalId " +
            "group by key;";

    private String maintLast12MonthCost =
            "select to_char(w.request_time, 'yyyy-mm') as key, " +
            "sum(w.total_price) as value " +
            "from asset_info a, work_order w " +
            "where to_char(w.request_time, 'yyyy-mm') " +
            "between to_char(now() - interval '1 year', 'yyyy-mm') " +
            "and to_char(now(), 'yyyy-mm') " +
            "and a.id = w.asset_id and w.is_closed = true " +
            "and a.hospital_id = :#hospitalId " +
            "group by key order by key;";

    private String maintTargetYearMonthCost =
            "select to_char(w.request_time, 'yyyy-mm') as key, " +
            "sum(w.total_price) as value " +
            "from asset_info a, work_order w " +
            "where extract(year from w.request_time) = :#targetYear" +
            "and a.id = w.asset_id and w.is_closed = true " +
            "and a.hospital_id = :#hospitalId " +
            "group by key order by key;";

    private String olderMonthRevenue =
            "select sum(r.price_amount) as value, " +
            "to_char(r.exam_date, 'yyyy-mm') as key " +
            "from asset_info a, asset_clinical_record r " +
            "where to_char(r.exam_date, 'yyyy-mm') between to_char(now() - interval '2 year', 'yyyy-mm') " +
            "and to_char(now() - interval '1 year', 'yyyy-mm') " +
            "and a.id = r.asset_id " +
            "and a.hospital_id = :#hospitalId " +
            "group by key order by key;";

    private String olderMaintCost =
            "select to_char(w.request_time, 'yyyy-mm') as key, " +
            "sum(w.total_price) as value " +
            "from asset_info a, work_order w " +
            "where to_char(w.request_time, 'yyyy-mm') " +
            "between to_char(now() - interval '1 year', 'yyyy-mm') " +
            "and to_char(now(), 'yyyy-mm') " +
            "and a.id = w.asset_id and w.is_closed = true " +
            "and a.hospital_id = :#hospitalId " +
            "group by key order by key;";

    @PostConstruct
    protected void init() {
        initYearList();
        logger.debug("target Year: {}", targetYear);

        startMonth = getStartMonth();

        // init sql params
        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("targetYear", this.targetYear);

        queryTotalProfit();

        createAnnualBar();

        createAnnualPie();

        createMonthlyBar();





    }

    private DateTime getStartMonth() {
        // format revenue if there is no perfect 12 month
        DateTime startMonth = new DateTime();
        if (targetYear == startMonth.getYear()) {
            // start from last 12 month
            startMonth = startMonth.withYear(startMonth.getYear() - 1);
        } else {
            startMonth = startMonth.withYear(targetYear).withMonthOfYear(1); // start from first month
        }

        return startMonth;
    }

    private void queryTotalProfit() {
        monthRevenue = queryMonthRevenue();
        monthDep     = queryMonthDep();
        monthMaint   = queryMonthMaint();

        monthProfit = calcProfit(monthRevenue, monthDep, monthMaint);

        for (Map<String, Object> item : monthProfit) {
            totalProfit = totalProfit + (double) item.get("value");
        }
    }

    private List<Map<String, Object>> queryMonthRevenue() {
        if (isYearly) {
            sql = targetYearMonthRevenue;
        } else {
            sql = last12MonthRevenue;
        }

        logger.info("Get revenue by month");
        monthRevenue = queryMonthDate(sql, sqlParams);

        return monthRevenue;
    }

    private List<Map<String, Object>> queryMonthMaint() {
        if (isYearly) {
            sql = maintTargetYearMonthCost;
        } else {
            sql = maintLast12MonthCost;
        }

        logger.info("Get maintenance cost by month");
        monthMaint = queryMonthDate(sql, sqlParams);

        return monthMaint;
    }

    private List<Map<String, Object>> queryMonthDep() {
        sql = depMonth;
        logger.info("Get dep by month");
        monthDep = queryMonthDepDate(sql, sqlParams);

        return monthDep;
    }

    private List<Map<String, Object>> queryMonthDepDate(String sql, HashMap<String, Object> sqlParams) {
        logger.info("Get dep by month, sql: {}, sqlParams: {}", sql, sqlParams);
        List<Map<String, Object>> li = NativeSqlUtil.queryForList(sql, sqlParams);
        if (li.size() == 0) {
            li = calcMonthlyDep(0.0, startMonth);
        } else {
            li = calcMonthlyDep((double) li.get(0).get("value"), startMonth);
        }
        return li;
    }

    private List<Map<String, Object>> queryMonthDate(String sql, HashMap<String, Object> sqlParams) {
        logger.info("Get data by month, sql: {}, sqlParams: {}", sql, sqlParams);
        List<Map<String, Object>> li = NativeSqlUtil.queryForList(sql, sqlParams);
        return formatMonthlyData(li, 12, startMonth);
    }

//    private List<Map<String, Object>> queryMonthRevenue() {
//        if (isYearly) {
//            sql = targetYearMonthRevenue;
//        } else {
//            sql = last12MonthRevenue;
//        }
//
//        // format revenue if there is no perfect 12 month
//        logger.info("Get revenue by month, sql: {}, sqlParams: {}", sql, sqlParams);
//        monthRevenue = NativeSqlUtil.queryForList(sql, sqlParams);
//        monthRevenue = formatMonthlyData(monthRevenue, 12, startMonth);
//
//        return monthRevenue;
//    }

//    private List<Map<String, Object>> queryRevenue



    // Get-Set methods
    public double getTotalProfit() {return totalProfit;}

    public double getProfitForecast() { return profitForecast; }

    public String getSelectedYear() {
        logger.debug("get selectdYear: {}", selectedYear);

        return selectedYear;
    }

    public void setSelectedYear(String selectedYear) {
        logger.debug("set selectedYear: {}", selectedYear);
        this.selectedYear = selectedYear;
    }

    public BarChartModel getBarAnnualRevenue() { return barAnnualRevenue; }
    
    public PieChartModel getPieAnnualRevenue() { return pieAnnualRevenue; }

    public BarChartModel getBarMonthlyRevenue() {
        return barMonthlyRevenue;
    }

    public BarChartModel getBarMonthlyForecast() { return barMonthlyForecast; }

    public Map<String, String> getYearList() {
        return yearList;
    }

    public void setTargetYear() {
        this.targetYear = Integer.parseInt(selectedYear);
    }

    public void onSelectedYearChange() {
        logger.debug("target year : {}", this.selectedYear);
    }

    private void initYearList() {
        int total = 4;
        int year = targetYear;
        for (int index = 0; index < total; index++) {
            yearList.put(Integer.toString(year),Integer.toString(year));
            year -= 1;
        }
    }




    private void createMonthlyBar() {

        barMonthlyRevenue.setLegendPosition("ne");

        Axis xAxis = barMonthlyRevenue.getAxis(AxisType.X);
        xAxis.setLabel("Assets Type");

        Axis yAxis = barMonthlyRevenue.getAxis(AxisType.Y);
        yAxis.setLabel("CNY");

        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        this.logStr = "Current User Account is " +  UserContextService.getCurrentUserAccount().getName() +
                ". Hospital id is " + hospitalId + ".";
        logger.debug(this.logStr);

        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("targetYear", this.targetYear);

        // get revenue
        LocalDate date = LocalDate.now();
        DateTime startMonth = getStartMonth(date);
        if (targetYear == date.getYear()) {
            // query last 12 months record, since cannot get full-size 12 months record for current year.
            sql = "select sum(r.price_amount) as value, " +
                    "to_char(r.exam_date, 'yyyy-mm') as key " +
                    "from asset_info a, asset_clinical_record r " +
                    "where to_char(r.exam_date, 'yyyy-mm') between to_char(now() - interval '1 year', 'yyyy-mm') " +
                    "and to_char(now(), 'yyyy-mm') " +
                    "and a.id = r.asset_id " +
                    "and a.hospital_id = :#hospitalId " +
                    "group by key order by key;";
        } else {
            sql = "select sum(r.price_amount) as value, " +
                    "to_char(r.exam_date, 'yyyy-mm') as key " +
                    "from asset_info a, asset_clinical_record r " +
                    "where extract(year from r.exam_date) = :#targetYear " +
                    "and a.id = r.asset_id " +
                    "and a.hospital_id = :#hospitalId " +
                    "group by key order by key;";
        }

//        List<Map<String, Object>> r = prepareData(sql, sqlParams);

        // format revenue if there is no perfect 12 month
        logger.info("Get revenue by month, sql: {}, sqlParams: {}", sql, sqlParams);
        List<Map<String, Object>> r = NativeSqlUtil.queryForList(sql, sqlParams);
        r = formatMonthlyData(r, 12, startMonth);



        String label = "re";
        drawBar(barMonthlyRevenue, label, r);

        // get asset deprecate value
        sql = "select a.hospital_id as key, " +
                "sum(d.deprecate_amount) as value " +
                "from asset_info a, asset_depreciation d " +
                "where a.id = d.asset_id " +
                "and a.hospital_id = :#hospitalId " +
                "group by key;";

        logger.info("get dep value, sql: {}, sqlParams: {}", sql, sqlParams);
        List<Map<String, Object>> d = NativeSqlUtil.queryForList(sql, sqlParams);
        d = calcMonthlyDep((double) d.get(0).get("value"), startMonth);


        // get maintenance cost
        if (targetYear == date.getYear()) {
            // query last 12 months record, since cannot get full-size 12 months record for current year.
            sql = "select to_char(w.request_time, 'yyyy-mm') as key, " +
                    "sum(w.total_price) as value " +
                    "from asset_info a, work_order w " +
                    "where to_char(w.request_time, 'yyyy-mm') " +
                    "between to_char(now() - interval '1 year 1 month', 'yyyy-mm') " +
                    "and to_char(now() - interval '1 month', 'yyyy-mm') " +
                    "and a.id = w.asset_id and w.is_closed = true " +
                    "and a.hospital_id = :#hospitalId " +
                    "group by key order by key;";
        } else {
            sql = "select to_char(w.request_time, 'yyyy-mm') as key, " +
                    "sum(w.total_price) as value " +
                    "from asset_info a, work_order w " +
                    "where extract(year from w.request_time) = :#targetYear" +
                    "and a.id = w.asset_id and w.is_closed = true " +
                    "and a.hospital_id = :#hospitalId " +
                    "group by key order by key;";
        }

        // format revenue if there is no perfect 12 month
        logger.info("get work order cost, sql: {}, sqlParams: {}", sql, sqlParams);
        List<Map<String, Object>> w = NativeSqlUtil.queryForList(sql, sqlParams);
        w = formatMonthlyData(w, 12, startMonth);

        // Calcuate profile = revenue - depreciation - maintenance cost
        profit = calcProfit(r, d, w);
        label = "profit";
        printList(profit);
        drawBar(barMonthlyRevenue, label, profit);
    }

    private List<Map<String, Object>> calcMonthlyDep(double annualDepValue, DateTime startMonth) {
        List<Map<String, Object>> li = new ArrayList<>();
        double value = annualDepValue / 12;

        // calc monthly dep value
        DateTime targetMonth = startMonth;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
        for (int index = 0; index < 12; index++) {
            Map<String, Object> item = new HashMap<>();
            item.put("key", formatter.print(targetMonth));
            item.put("value", value);
            li.add(item);
            targetMonth = targetMonth.plusMonths(1);
        }

        return li;
    }

    private DateTime getStartMonth(LocalDate date) {
        // format revenue if there is no perfect 12 month
        DateTime startMonth = new DateTime();
        if (targetYear == date.getYear()) {
            // start from last 12 month
            startMonth = startMonth.withYear(startMonth.getYear() - 1);
        } else {
            startMonth = startMonth.withYear(targetYear).withMonthOfYear(1); // start from first month
        }

        return startMonth;
    }

    private List<Map<String, Object>> formatMonthlyData(List<Map<String, Object>> result, int size, DateTime startMonth) {
        List<Map<String, Object>> li = new ArrayList<>();

        if (size <= 0) return li;

        // init expected result list
        DateTime targetMonth = startMonth;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
        for (int index = 0; index < size; index++) {
            Map<String, Object> item = new HashMap<>();
            item.put("key", formatter.print(targetMonth));
            item.put("value", 0.0);
            li.add(item);
            targetMonth = targetMonth.plusMonths(1);
        }

        for (Map<String, Object> item : li) {

            if (result.size() == 0) {
                break; // all items in result list have been removed, therefore, finish for loop
            }

            HashMap<String, Object> node = (HashMap<String, Object>) result.get(0);

            while ((node.get("key") == null)  // remove node when the value of "key" is null
                    // remove node when it is earlier than expected item
                    || (formatter.parseDateTime(node.get("key").toString()).compareTo(
                    formatter.parseDateTime(item.get("key").toString())) < 0)) {
                result.remove(0);
                node = (HashMap<String, Object>) result.get(0);
            }

            node = (HashMap<String, Object>) result.get(0);

            if (node.get("key").toString().equals(item.get("key").toString())) {
                if (node.get("value") != null) {
                    item.put("value", node.get("value"));
                }
                result.remove(0); // since value of node has been put to item, therefore rmove head of list
            }
        }
        return li;
    }
    
    private void createAnnualPie() {
        // get revenue
        sql = "select a.clinical_dept_id as key, " +
                "sum(r.price_amount) as value " +
                "from asset_info a left join asset_clinical_record r " +
                "on r.asset_id = a.id " +
                "and extract(year from exam_date) = :#targetYear " +
                "and a.hospital_id = :#hospitalId " +
                "group by a.clinical_dept_id " +
                "order by a.clinical_dept_id;";
        List<Map<String, Object>> r = prepareData(sql, sqlParams);

        // get asset depreciation value
        sql = "select a.clinical_dept_id as key, " +
                "sum(d.deprecate_amount) as value " +
                "from asset_info a left join asset_depreciation d " +
                "on a.id = d.asset_id " +
                "and a.hospital_id = :#hospitalId " +
                "group by a.clinical_dept_id " +
                "order by a.clinical_dept_id;";
        List<Map<String, Object>> d = prepareData(sql, sqlParams);

        // get maintenance cost
        sql = "select a.clinical_dept_id as key, " +
                "sum(w.total_price) as value " +
                "from asset_info a left join work_order w " +
                "on w.asset_id = a.id " +
                "and a.hospital_id = :#hospitalId " +
                "and extract(year from w.request_time) = :#targetYear " +
                "and w.is_closed = true " +
                "group by a.clinical_dept_id " +
                "order by a.clinical_dept_id asc;";
        List<Map<String, Object>> w = prepareData(sql, sqlParams);

        // Calcuate profile = revenue - depreciation - maintenance cost
        profit = calcProfit(r, d, w);

        String title = new String("Annual Profit Distribution");
        drawPie(title, profit);
    }

    private void drawPie(String title, List<Map<String, Object>> data) {
        for (Map<String, Object> item : data) {
            pieAnnualRevenue.set(item.get("key").toString(), (Double) item.get("value"));
        }

        pieAnnualRevenue.setTitle(title);
        pieAnnualRevenue.setShowDataLabels(true);
        pieAnnualRevenue.setLegendPosition("w");
    }

    private List<Map<String, Object>> calcProfit(List<Map<String, Object>> revenue,
                                                 List<Map<String, Object>> dep,
                                                 List<Map<String, Object>> maintenance
                                                 ) {
        // Calcuate profile = revenue - depreciation - maintenance cost
        int index = 0;
        List<Map<String, Object>> profit = new ArrayList<>();

        for (Map<String, Object> item : revenue) {
            String key = item.get("key").toString();

            double value = (Double) item.get("value");
            if (dep.get(index).get("key") == null) {
                logger.debug("Cannot get key: " + key + "in deprecate sql query.");
            }
            else {
                value = value - (Double) dep.get(index).get("value");
            }

            if (maintenance.get(index).get("key") == null) {
                logger.debug("Cannot get key: " + key + "in maintenance sql query.");
            }
            else {
                value = value - (Double) maintenance.get(index).get("value");
            }

            HashMap<String, Object> node = new HashMap<>();
            node.put("key", key);
            node.put("value", value);
            profit.add(node);
            index++;
        }

        return profit;
    }
    
    private void createAnnualBar() {

        barAnnualRevenue.setLegendPosition("ne");
         
        Axis xAxis = barAnnualRevenue.getAxis(AxisType.X);
        xAxis.setLabel("Assets Type");
         
        Axis yAxis = barAnnualRevenue.getAxis(AxisType.Y);
        yAxis.setLabel("CNY");

        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        this.logStr = "Current User Account is " +  UserContextService.getCurrentUserAccount().getName() +
                ". Hospital id is " + hospitalId + ".";
        logger.debug(this.logStr);

        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("targetYear", this.targetYear);

        // get revenue
        sql = "select a.asset_group as key, "
            + "sum(r.price_amount) as value "
            + "from asset_info a left join asset_clinical_record r "
            + "on a.id = r.asset_id "
            + "and a.hospital_id = :#hospitalId and extract(year from r.exam_date) = :#targetYear "
            + "group by a.asset_group "
            + "order by a.asset_group asc;";
        
        List<Map<String, Object>> r = prepareData(sql, sqlParams);
        String label = "re";
        drawBar(barAnnualRevenue, label, r);
        
        // get asset deprecate value
        sql = "select a.asset_group as key, "
            + "sum(d.deprecate_amount) as value "
            + "from asset_info a left join asset_depreciation d "
            + "on a.id = d.asset_id and a.hospital_id = :#hospitalId "
            + "group by a.asset_group order by a.asset_group asc;";
        
        List<Map<String, Object>> d = prepareData(sql, sqlParams);
        
        // get maintenance cost
        sql = "select a.asset_group as key, "
            + "sum(w.total_price) as value "
            + "from asset_info a left join work_order w "
            + "on w.asset_id = a.id and a.hospital_id = :#hospitalId "
            + "and extract(year from w.request_time) = :#targetYear "
            + "and w.is_closed = true "
            + "group by a.asset_group order by a.asset_group asc;";
        
        List<Map<String, Object>> w = prepareData(sql, sqlParams);
        
        // Calcuate profile = revenue - depreciation - maintenance cost
        List<Map<String, Object>> profit = calcProfit(r, d, w);
        label = "profit";
        printList(profit);
        drawBar(barAnnualRevenue, label, profit);
    }

    private List<Map<String, Object>> prepareData(String sql, HashMap<String, Object> sqlParams) {
        logger.debug(sql);

        List<Map<String, Object>> result = NativeSqlUtil.queryForList(sql, sqlParams);

        if (result.size() == 0) {
            Map<String, Object> item = new HashMap<>();
            item.put("key", 1);
            item.put("value", 0.1);
            result.add(item);
        }
        else {
            for (Map<String, Object> item : result) {
                checkNull(item, "Double");
            }
        }


        return result;
    }

    private void printList (List<Map<String, Object>> result) {
        for (Map<String, Object> item : result) {
            if (item.get("value") == null) {

                System.out.print("Print result, key = " + item.get("key").toString() + ", value is null");
            }
            else {
                System.out.print("Print result, key = " + item.get("key").toString() + ", value = " + item.get("value").toString() + "\n");
            }
        }
    }

    private void drawBar(BarChartModel barChart, String label, List<Map<String, Object>> result) {
        ChartSeries revenue = new ChartSeries();
        revenue.setLabel(label);

        for (Map<String, Object> item : result) {
            revenue.set(item.get("key").toString(), (Double) item.get("value"));
        }

        barChart.addSeries(revenue);
    }

    private void checkNull(Map<String, Object> item, String targetType) {
        if (item.get("value") == null) {

            // add targetType, if needed.
            // convert null to associated empty value for drawing
            switch (targetType) {

                case "Double":
                    item.put("value", 0.1);
                default:
                    ;
            }
        }
    }





}
