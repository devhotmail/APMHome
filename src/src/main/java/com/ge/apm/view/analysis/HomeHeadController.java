package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.*;

import java.text.NumberFormat;
import java.time.Year;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.LocalDate;

import org.primefaces.model.chart.*;
import org.slf4j.LoggerFactory;

import webapp.framework.web.mvc.SqlConfigurableChartController;
import webapp.framework.dao.NativeSqlUtil;

@ManagedBean
@ViewScoped
public class HomeHeadController extends SqlConfigurableChartController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HomeHeadController.class);

    private double anualCost = 0.0;
    private int revenue = 0;
    private String logStr;
    private List<Map<String, Object>> profit = new ArrayList<>();

    //UI Params
    private String totalProfit = "";
    private String profitForecast = "";

    private HashMap<String, String> yearList = new HashMap<>();

    private BarChartModel barAnnualRevenue = new BarChartModel();
    private PieChartModel pieAnnualRevenue = new PieChartModel();

    private BarChartModel barMonthlyRevenue = new BarChartModel();
    private BarChartModel barMonthlyForecast = new BarChartModel();

    // chart parameters
    private int targetYear = Year.now().getValue();
    private int hospitalId = 0;
    private String selectedYear = Integer.toString(targetYear);
//    private String selectedYear = "";

    private String sql;
    private HashMap<String, Object> sqlParams = new HashMap<>();

    private boolean isYearly = false;
    private DateTime startMonth = new DateTime();

    private List<Map<String, Object>> monthRevenue = new ArrayList<>();
    private List<Map<String, Object>> monthDep = new ArrayList<>();
    private List<Map<String, Object>> monthMaint = new ArrayList<>();
    private List<Map<String, Object>> monthProfit = new ArrayList<>();

    private DateTime fcStartMonth = new DateTime();
    private List<Map<String, Object>> forecastRevenue = new ArrayList<>();
    private List<Map<String, Object>> forecastDep = new ArrayList<>();
    private List<Map<String, Object>> forecastMaint = new ArrayList<>();
    private List<Map<String, Object>> forecastProfit = new ArrayList<>();

    private List<Map<String, Object>> predictRev = new ArrayList<>();
    private List<Map<String, Object>> predictPro = new ArrayList<>();


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
                    "where extract(year from w.request_time) = :#targetYear " +
                    "and a.id = w.asset_id and w.is_closed = true " +
                    "and a.hospital_id = :#hospitalId " +
                    "group by key order by key;";

    private String forecastMonthRevenue =
            "select sum(r.price_amount) as value, " +
                    "to_char(r.exam_date, 'yyyy-mm') as key " +
                    "from asset_info a, asset_clinical_record r " +
                    "where to_char(r.exam_date, 'yyyy-mm') between to_char(now() - interval '2 year', 'yyyy-mm') " +
                    "and to_char(now(), 'yyyy-mm') " +
                    "and a.id = r.asset_id " +
                    "and a.hospital_id = :#hospitalId " +
                    "group by key order by key;";

    private String forecastMonthMaint =
            "select to_char(w.request_time, 'yyyy-mm') as key, " +
                    "sum(w.total_price) as value " +
                    "from asset_info a, work_order w " +
                    "where to_char(w.request_time, 'yyyy-mm') " +
                    "between to_char(now() - interval '2 year', 'yyyy-mm') " +
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

        queryForecastProfit();

        createAnnualBar();

        createAnnualPie();

        createMonthlyBar();

        createForecastBar();
    }

    private DateTime getStartMonth() {
        // format revenue if there is no perfect 12 month
        DateTime startMonth = new DateTime();
        if (targetYear == startMonth.getYear()) {
            // start from last 12 month
            isYearly = false;
            startMonth = startMonth.withYear(startMonth.getYear() - 1);
        } else {
            isYearly = true;
            startMonth = startMonth.withYear(targetYear).withMonthOfYear(1); // start from first month
        }

        return startMonth;
    }

    private void queryTotalProfit() {
        double pro = 0.0;
        monthRevenue = queryMonthRevenue();
        monthDep = queryMonthDep();
        monthMaint = queryMonthMaint();

        monthProfit = calcProfit(monthRevenue, monthDep, monthMaint);

        for (Map<String, Object> item : monthProfit) {
            pro = pro + (double) item.get("value");
        }

        NumberFormat cf = NumberFormat.getCurrencyInstance();
        totalProfit = cf.format(pro);
    }

    private void queryForecastProfit() {
        double value = 0.0;

        fcStartMonth = fcStartMonth.withYear(fcStartMonth.getYear() - 2);

        forecastRevenue = queryForecastRevenue();
        forecastDep = queryForecastDep();
        forecastMaint = queryForecastMaint();

        forecastProfit = calcProfit(forecastRevenue, forecastDep, forecastMaint);

        predict(forecastRevenue, forecastProfit, fcStartMonth);

        for (Map<String, Object> item : predictPro) {
            value = value + (double) item.get("value");
        }

        NumberFormat cf = NumberFormat.getCurrencyInstance();
        profitForecast = cf.format(value);

        forecastRevenue.addAll(predictRev);
        forecastProfit.addAll(predictPro);

    }

    private void predict(List<Map<String, Object>> revenue, List<Map<String, Object>> profit, DateTime startMonth) {
        int offset = revenue.size() / 2;
        DateTime predictMonth = startMonth;
        predictMonth = predictMonth.plusMonths(revenue.size());

        for (int index = 0; index < offset; index++) {
            Map<String, Object> item = predictNextItem(revenue.get(index), revenue.get(index + offset), predictMonth);
            predictRev.add(item);

            item = predictNextItem(profit.get(index), profit.get(index + offset), predictMonth);
            predictPro.add(item);

            predictMonth = predictMonth.plusMonths(1);
        }
    }

    private Map<String, Object> predictNextItem(Map<String, Object> last, Map<String, Object> recent, DateTime month) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
        Map<String, Object> item    = new HashMap<>();
        double value = 0.0;
        double result = 0.0;

        double lastValue = (double) last.get("value");
        double recentValue = (double) recent.get("value");

        if ((recentValue > - 0.00001) && (recentValue < 0.00001)) {
            result = 0.0;
        } else {
            result = (recentValue - lastValue) / recentValue;
        }

        if (result > 0.05) {

            value = recentValue * (1 + result);

        } else if ((result > 0) && (result < 0.05)) {
            // assume we will be better to reach 5%
            value = recentValue * (1 + 0.05);

        } else if ((result > - 0.05) && (result < 0)) {
            // assume we will be better to reach 5%
            value = recentValue * (1 + 0.05 + result);

        } else if (result < -0.05) {
            // assume little improve
            value = recentValue * (1 + result / 2);
        } else {
            value = recentValue;
        }

        item.put("key", formatter.print(month));
        item.put("value", value);

        return item;
    }


    private void createMonthlyBar() {
        BarChartModel bc = new BarChartModel();
        bc.setLegendPosition("ne");

        Axis xAxis = bc.getAxis(AxisType.X);
        xAxis.setLabel("Assets Type");

        Axis yAxis = bc.getAxis(AxisType.Y);
        yAxis.setLabel("CNY");

        String label = "re";
        drawBar(bc, label, monthRevenue);

        label = "profit";
        drawBar(bc, label, monthProfit);

        barMonthlyRevenue = bc;
        barMonthlyRevenue.setExtender("barMonthlyRevenue");
    }

    private void createForecastBar() {
        barMonthlyForecast.setLegendPosition("ne");
        barMonthlyForecast.setExtender("barMonthlyForecast");

        Axis xAxis = barMonthlyForecast.getAxis(AxisType.X);
        xAxis.setLabel("Forecast");

        Axis yAxis = barMonthlyForecast.getAxis(AxisType.Y);
        yAxis.setLabel("CNY");


        xAxis.setTickAngle(-50);
        xAxis.setTickFormat("%y-%m");
        barMonthlyForecast.setBarWidth(11);
        barMonthlyForecast.setBarMargin(3);
        barMonthlyForecast.getAxes().put(AxisType.X, xAxis);

        String label = "re";
        drawBar(barMonthlyForecast, label, forecastRevenue);

        label = "profit";
        drawBar(barMonthlyForecast, label, forecastProfit);
    }

    private List<Map<String, Object>> queryForecastRevenue() {
        sql = forecastMonthRevenue;
        logger.info("Get 24 months revenue by month");
        forecastRevenue = queryMonthDate(sql, sqlParams, 24, fcStartMonth);

        return forecastRevenue;
    }

    private List<Map<String, Object>> queryForecastMaint() {
        sql = forecastMonthMaint;

        logger.info("Get 24 months maint cost by month");
        forecastMaint = queryMonthDate(sql, sqlParams, 24, fcStartMonth);

        return forecastMaint;
    }

    private List<Map<String, Object>> queryForecastDep() {
        sql = depMonth;
        logger.info("Get 24 months by month");
        monthDep = queryMonthDepDate(sql, sqlParams, 24, fcStartMonth);

        return monthDep;
    }


    private List<Map<String, Object>> queryMonthRevenue() {
        if (isYearly) {
            sql = targetYearMonthRevenue;
        } else {
            sql = last12MonthRevenue;
        }

        logger.info("Get revenue by month.");
        monthRevenue = queryMonthDate(sql, sqlParams, 12, startMonth);

        return monthRevenue;
    }

    private List<Map<String, Object>> queryMonthMaint() {
        if (isYearly) {
            sql = maintTargetYearMonthCost;
        } else {
            sql = maintLast12MonthCost;
        }

        logger.info("Get maintenance cost by month");
        monthMaint = queryMonthDate(sql, sqlParams, 12, startMonth);

        return monthMaint;
    }

    private List<Map<String, Object>> queryMonthDep() {
        sql = depMonth;
        logger.info("Get dep by month");
        monthDep = queryMonthDepDate(sql, sqlParams, 12, startMonth);

        return monthDep;
    }

    private List<Map<String, Object>> queryMonthDepDate(String sql, HashMap<String, Object> sqlParams, int months, DateTime startMonth) {
        logger.info("Get dep by month, sql: {}, sqlParams: {}", sql, sqlParams);
        List<Map<String, Object>> li = NativeSqlUtil.queryForList(sql, sqlParams);
        if (li.size() == 0) {
            li = calcMonthlyDep(0.0, months, startMonth);
        } else {
            li = calcMonthlyDep((double) li.get(0).get("value"), months, startMonth);
        }
        return li;
    }

    private List<Map<String, Object>> queryMonthDate(String sql, HashMap<String, Object> sqlParams, int months, DateTime startMonth) {
        logger.info("Get data by month, sql: {}, sqlParams: {}", sql, sqlParams);
        List<Map<String, Object>> li = NativeSqlUtil.queryForList(sql, sqlParams);
        return formatMonthlyData(li, months, startMonth);
    }

    // Get-Set methods
    public String getTotalProfit() {return totalProfit;}

    public String getProfitForecast() { return profitForecast; }

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
        targetYear = Integer.parseInt(selectedYear);
        sqlParams.put("targetYear", this.targetYear);
        startMonth = getStartMonth();

        logger.debug("target year : {}, " +
                "isYearly: {}, " +
                "startMonth: {}, " +
                "sqlParams: {}", targetYear, isYearly, startMonth, sqlParams);

        queryTotalProfit();

        createAnnualBar();

        createAnnualPie();

        createMonthlyBar();
    }

    private void initYearList() {
        int total = 4;
        int year = targetYear;
        for (int index = 0; index < total; index++) {
            yearList.put(Integer.toString(year),Integer.toString(year));
            year -= 1;
        }
    }

    private List<Map<String, Object>> calcMonthlyDep(double annualDepValue, int months, DateTime startMonth) {
        List<Map<String, Object>> li = new ArrayList<>();
        double value = annualDepValue / 12;

        // calc monthly dep value
        DateTime targetMonth = startMonth;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
        for (int index = 0; index < months; index++) {
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
        logger.debug("startMonth: {}.", startMonth);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
        for (int index = 0; index < size; index++) {
            Map<String, Object> item = new HashMap<>();
            item.put("key", formatter.print(targetMonth));
            item.put("value", 0.0);
            li.add(item);
            targetMonth = targetMonth.plusMonths(1);
        }

        logger.debug("result size: {}", result.size());
        logger.debug("print input result: {}", result);
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
        PieChartModel pc = new PieChartModel();

        for (Map<String, Object> item : data) {
            pc.set(item.get("key").toString(), (Double) item.get("value"));
        }

        pc.setTitle(title);
        pc.setShowDataLabels(true);
        pc.setLegendPosition("w");

        pieAnnualRevenue = pc;
        pieAnnualRevenue.setExtender("pieAnnualRevenue");
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

            HashMap<String, Object> node = new HashMap<>();

            double value = (Double) item.get("value");
            if ((value > -0.00001) && (value < 0.00001)) {
                // revenue equals zero, it should be data input error, therefore, profit will be zero too
                node.put("value", 0.0);
            } else {

                // revenue did not equal to zero, let us calculate profit
                if (dep.get(index).get("key") == null) {
                    logger.debug("Cannot get key: " + key + "in deprecate sql query.");
                } else {
                    value = value - (Double) dep.get(index).get("value");
                }

                if (maintenance.get(index).get("key") == null) {
                    logger.debug("Cannot get key: " + key + "in maintenance sql query.");
                } else {
                    value = value - (Double) maintenance.get(index).get("value");
                }
                node.put("value", value);
            }

            node.put("key", key);
            profit.add(node);
            index++;
        }

        return profit;
    }
    
    private void createAnnualBar() {
        BarChartModel bc = new BarChartModel();

        bc.setLegendPosition("ne");
        Axis xAxis = bc.getAxis(AxisType.X);
        xAxis.setLabel("Assets Type");
         
        Axis yAxis = bc.getAxis(AxisType.Y);
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
        drawBar(bc, label, r);
        
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
        drawBar(bc, label, profit);
        barAnnualRevenue = bc;
        barAnnualRevenue.setExtender("barAnnualRevenue");
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
        ChartSeries cs = new ChartSeries();
        cs.setLabel(label);

        for (Map<String, Object> item : result) {
            cs.set(item.get("key").toString(), (Double) item.get("value"));
        }

        barChart.addSeries(cs);
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
