package com.ge.apm.view.analysis;

import com.ge.apm.domain.I18nMessage;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.time.Year;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
import org.primefaces.model.chart.*;
import org.slf4j.LoggerFactory;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;
import webapp.framework.dao.NativeSqlUtil;

@ManagedBean
@ViewScoped
public class HomeHeadController extends SqlConfigurableChartController {

	private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HomeHeadController.class);
    
    private static final String assetTypeStr = WebUtil.getMessage("assetType");
    private static final String revenueStr = WebUtil.getMessage("deviceROIlg_1");
    private static final String profitStr = WebUtil.getMessage("deviceROIlg_2");

    private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    
    private HashMap<String, Object> sqlParams = new HashMap<>();  
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
    private String selectedYear = Integer.toString(targetYear);
    private String sql;

    //private boolean isYearly = true;
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
    /*
    private String last12MonthRevenue =
            "select sum(r.price_amount) as value, " +
                    "to_char(r.exam_date, 'yyyy-mm') as key " +
                    "from asset_info a, asset_clinical_record r " +
                    "where to_char(r.exam_date, 'yyyy-mm') between to_char(now() - interval '1 year', 'yyyy-mm') " +
                    "and to_char(now(), 'yyyy-mm') " +
                    "and a.id = r.asset_id " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key order by key;";*/

    private static final String targetYearMonthRevenue =
            "select sum(r.price_amount) as value, " +
                    "to_char(r.exam_date, 'yyyy-mm') as key " +
                    "from asset_info a, asset_clinical_record r " +
                    "where extract(year from r.exam_date) = :#targetYear " +
                    "and a.id = r.asset_id " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key order by key;";

    private static final String depMonth =
            "select a.hospital_id as key, " +
                    "sum(d.deprecate_amount) as value " +
                    "from asset_info a, asset_depreciation d " +
                    "where a.id = d.asset_id " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key;";

    /*
    private String maintLast12MonthCost =
            "select to_char(w.request_time, 'yyyy-mm') as key, " +
                    "sum(w.total_price) as value " +
                    "from asset_info a, work_order w " +
                    "where to_char(w.request_time, 'yyyy-mm') " +
                    "between to_char(now() - interval '1 year', 'yyyy-mm') " +
                    "and to_char(now(), 'yyyy-mm') " +
                    "and a.id = w.asset_id and w.is_closed = true " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key order by key;";*/

    private static final String maintTargetYearMonthCost =
            "select to_char(w.request_time, 'yyyy-mm') as key, " +
                    "sum(w.total_price) as value " +
                    "from asset_info a, work_order w " +
                    "where extract(year from w.request_time) = :#targetYear " +
                    "and a.id = w.asset_id and w.is_closed = true " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key order by key;";

    private static final String forecastMonthRevenue =
            "select sum(r.price_amount) as value, " +
                    "to_char(r.exam_date, 'yyyy-mm') as key " +
                    "from asset_info a, asset_clinical_record r " +
                    "where to_char(r.exam_date, 'yyyy-mm') between to_char(now() - interval '2 year', 'yyyy-mm') " +
                    "and to_char(now(), 'yyyy-mm') " +
                    "and a.id = r.asset_id " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key order by key;";

    private static final String forecastMonthMaint =
            "select to_char(w.request_time, 'yyyy-mm') as key, " +
                    "sum(w.total_price) as value " +
                    "from asset_info a, work_order w " +
                    "where to_char(w.request_time, 'yyyy-mm') " +
                    "between to_char(now() - interval '2 year', 'yyyy-mm') " +
                    "and to_char(now(), 'yyyy-mm') " +
                    "and a.id = w.asset_id and w.is_closed = true " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key order by key;";

    @PostConstruct
    protected void init() {
        initYearList();
        startMonth = getStartMonth();

        // init sql params
        sqlParams.put("hospital_id", hospital_id);
        sqlParams.put("targetYear", this.targetYear);

        queryTotalProfit();

        queryForecastProfit();

        createAnnualBar();

        createAnnualPie();

        createMonthlyBar();

        createForecastBar();
    }

    private DateTime getStartMonth() {

        startMonth = startMonth.withYear(targetYear).withMonthOfYear(1); // start from first month
        
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

        NumberFormat cf = new DecimalFormat(",###.##");//NumberFormat.getCurrencyInstance(Locale.CHINA);
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

        NumberFormat cf = new DecimalFormat(",###.##");//NumberFormat.getCurrencyInstance(Locale.CHINA);
        profitForecast = cf.format(value);

        forecastRevenue.addAll(predictRev);
        forecastProfit.addAll(predictPro);

    }

    private void predict(List<Map<String, Object>> revenue, List<Map<String, Object>> profit, DateTime startMonth) {
        int offset = revenue.size() / 2;
        DateTime predictMonth = startMonth;
        predictMonth = predictMonth.plusMonths(revenue.size());

        for (int index = 0; index < offset; index++) {
            Map<String, Object> item = predictNextItem(revenue.get(index), revenue.get(index + offset), predictMonth, false);
            predictRev.add(item);

            item = predictNextItem(profit.get(index), profit.get(index + offset), predictMonth, true);
            predictPro.add(item);

            predictMonth = predictMonth.plusMonths(1);
        }
    }

    private Map<String, Object> predictNextItem(Map<String, Object> last, Map<String, Object> recent, DateTime month, boolean allowNeg) {
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

        // logically revenue cannot be negative value
        if ((!allowNeg) && (value < 0.0)) { value = 0.0; }

        item.put("key", formatter.print(month));
        item.put("value", value);

        return item;
    }


    private void createMonthlyBar() {
        BarChartModel bc = new BarChartModel();
        bc.setLegendPosition("ne");

        String label = revenueStr;
        drawBar(bc, label, monthRevenue);

        label = profitStr;
        drawBar(bc, label, monthProfit);

        barMonthlyRevenue = bc;
        barMonthlyRevenue.setExtender("barMonthlyRevenue");
    }

    private void createForecastBar() {
        barMonthlyForecast.setLegendPosition("ne");
        barMonthlyForecast.setExtender("barMonthlyForecast");

        Axis xAxis = barMonthlyForecast.getAxis(AxisType.X);
        xAxis.setTickAngle(-50);
        xAxis.setTickFormat("%y-%m");

        long interval = 1000 * 60 * 60 * 24 * 30 * 3;
        xAxis.setTickInterval(String.valueOf(interval));
        barMonthlyForecast.setBarWidth(11);
        barMonthlyForecast.setBarMargin(6);
        barMonthlyForecast.getAxes().put(AxisType.X, xAxis);

        String label = revenueStr;
        drawBar(barMonthlyForecast, label, forecastRevenue);

        label = profitStr;
        drawBar(barMonthlyForecast, label, forecastProfit);
    }

    private List<Map<String, Object>> queryForecastRevenue() {
        sql = forecastMonthRevenue;
        forecastRevenue = queryMonthDate(sql, sqlParams, 24, fcStartMonth);

        return forecastRevenue;
    }

    private List<Map<String, Object>> queryForecastMaint() {
        sql = forecastMonthMaint;
        forecastMaint = queryMonthDate(sql, sqlParams, 24, fcStartMonth);

        return forecastMaint;
    }

    private List<Map<String, Object>> queryForecastDep() {
        sql = depMonth;
        monthDep = queryMonthDepDate(sql, sqlParams, 24, fcStartMonth);

        return monthDep;
    }


    private List<Map<String, Object>> queryMonthRevenue() {

        sql = targetYearMonthRevenue;
        monthRevenue = queryMonthDate(sql, sqlParams, 12, startMonth);

        return monthRevenue;
    }

    private List<Map<String, Object>> queryMonthMaint() {

        sql = maintTargetYearMonthCost;
        monthMaint = queryMonthDate(sql, sqlParams, 12, startMonth);

        return monthMaint;
    }

    private List<Map<String, Object>> queryMonthDep() {
        sql = depMonth;
        monthDep = queryMonthDepDate(sql, sqlParams, 12, startMonth);

        return monthDep;
    }

    private List<Map<String, Object>> queryMonthDepDate(String sql, HashMap<String, Object> sqlParams, int months, DateTime startMonth) {

        sqlParams.put("_sql", sql);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        List<Map<String, Object>> li = NativeSqlUtil.queryForList(sql, sqlParams);

        if (li.size() == 0) {
            li = calcMonthlyDep(0.0, months, startMonth);
        } else {
            li = calcMonthlyDep((double) li.get(0).get("value"), months, startMonth);
        }
        return li;
    }

    private List<Map<String, Object>> queryMonthDate(String sql, HashMap<String, Object> sqlParams, int months, DateTime startMonth) {

        sqlParams.put("_sql", sql);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        List<Map<String, Object>> li = NativeSqlUtil.queryForList(sql, sqlParams);

        return formatMonthlyData(li, months, startMonth);
    }

    // Get-Set methods
    public String getTotalProfit() {return totalProfit;}

    public String getProfitForecast() { return profitForecast; }

    public String getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(String selectedYear) {
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

        targetYear = Integer.parseInt(selectedYear);
        sqlParams.put("targetYear", this.targetYear);
        startMonth = getStartMonth();

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

    /*
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
    }*/

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
        sql = "select a.clinical_dept_name as key, " +
                "sum(r.price_amount) as value " +
                "from asset_info a, asset_clinical_record r " +
                "where r.asset_id = a.id " +
                "and extract(year from exam_date) = :#targetYear " +
                "and a.hospital_id = :#hospital_id " +
                "group by a.clinical_dept_name " +
                "order by a.clinical_dept_name;";
        List<Map<String, Object>> r = prepareData(sql, sqlParams);

        // get asset depreciation value
        sql = "select a.clinical_dept_name as key, " +
                "sum(d.deprecate_amount) as value " +
                "from asset_info a, asset_depreciation d " +
                "where a.id = d.asset_id " +
                "and a.hospital_id = :#hospital_id " +
                "group by a.clinical_dept_name " +
                "order by a.clinical_dept_name;";
        List<Map<String, Object>> d = prepareData(sql, sqlParams);

        // get maintenance cost
        sql = "select a.clinical_dept_name as key, " +
                "sum(w.total_price) as value " +
                "from asset_info a, work_order w " +
                "where w.asset_id = a.id " +
                "and a.hospital_id = :#hospital_id " +
                "and extract(year from w.request_time) = :#targetYear " +
                "and w.is_closed = true " +
                "group by a.clinical_dept_name " +
                "order by a.clinical_dept_name asc;";
        List<Map<String, Object>> w = prepareData(sql, sqlParams);

        // Calcuate profile = revenue - depreciation - maintenance cost
        profit = calcProfit(r, d, w);
        //localizeAxis(profit, "clinicalDeptId");

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
            
            String key = item.get("key")!=null ? item.get("key").toString() : "";

            HashMap<String, Object> node = new HashMap<>();

            double value = (Double) item.get("value");
            if ((value > -0.00001) && (value < 0.00001)) {
                // revenue equals zero, it should be data input error, therefore, profit will be zero too
                node.put("value", 0.0);
            } else {
                // revenue did not equal to zero, let us calculate profit
                if (dep.get(index).get("key") != null) {
                    value = value - (Double) dep.get(index).get("value");
                }

                if (maintenance.get(index).get("key") != null) {
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
        xAxis.setLabel(assetTypeStr);

        sqlParams.put("hospital_id", hospital_id);
        sqlParams.put("targetYear", this.targetYear);

        // get revenue
        sql = "select a.asset_group as key, "
            + "sum(r.price_amount) as value "
            + "from asset_info a left join asset_clinical_record r "
            + "on a.id = r.asset_id "
            + "and a.hospital_id = :#hospital_id and extract(year from r.exam_date) = :#targetYear "
            + "group by a.asset_group "
            + "order by a.asset_group asc;";
        
        List<Map<String, Object>> r = prepareData(sql, sqlParams);
        localizeAxis(r, "assetGroup");

        String label = revenueStr;

        drawBar(bc, label, r);


        // get asset deprecate value
        sql = "select a.asset_group as key, "
            + "sum(d.deprecate_amount) as value "
            + "from asset_info a left join asset_depreciation d "
            + "on a.id = d.asset_id and a.hospital_id = :#hospital_id "
            + "group by a.asset_group order by a.asset_group asc;";
        
        List<Map<String, Object>> d = prepareData(sql, sqlParams);
        
        // get maintenance cost
        sql = "select a.asset_group as key, "
            + "sum(w.total_price) as value "
            + "from asset_info a left join work_order w "
            + "on w.asset_id = a.id and a.hospital_id = :#hospital_id "
            + "and extract(year from w.request_time) = :#targetYear "
            + "and w.is_closed = true "
            + "group by a.asset_group order by a.asset_group asc;";
        
        List<Map<String, Object>> w = prepareData(sql, sqlParams);
        
        // Calcuate profile = revenue - depreciation - maintenance cost
        List<Map<String, Object>> profit = calcProfit(r, d, w);
        label = profitStr;
        drawBar(bc, label, profit);
        barAnnualRevenue = bc;
        barAnnualRevenue.setExtender("barAnnualRevenue");
    }

    private void localizeAxis(List<Map<String, Object>> results, String key) {

        FieldValueMessageController fieldMsg = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
        List<I18nMessage> messages = fieldMsg.getFieldValueList(key);

        Map<String, String> messages_map = new HashMap<String, String>();

        for (I18nMessage local : messages) {
                messages_map.put(local.getMsgKey(), local.getValue());
        }

        for (Map<String, Object> res : results) 
            if (messages_map.containsKey(res.get("key").toString())) 
                    res.put("key", messages_map.get(res.get("key").toString()));
                

    }

    private List<Map<String, Object>> prepareData(String sql, HashMap<String, Object> sqlParams) {

        sqlParams.put("_sql", sql);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        List<Map<String, Object>> result = NativeSqlUtil.queryForList(sql, sqlParams);

        if (result.size() == 0) {
            Map<String, Object> item = new HashMap<>();
            item.put("key", "");
            item.put("value", 0.0);
            result.add(item);
        }
        else {
            for (Map<String, Object> item : result) {
                checkNull(item, "Double");
            }
        }


        return result;
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
                    item.put("value", 0.0);
                default:
                    ;
            }
        }
    }





}
