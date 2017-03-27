package com.ge.apm.view.analysis;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.view.sysutil.UserContextService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.primefaces.model.chart.*;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;
import webapp.framework.web.mvc.SqlConfigurableChartController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import java.text.NumberFormat;
import java.time.Year;
import java.util.*;

@ManagedBean
@ViewScoped
public class AssetForecastController extends SqlConfigurableChartController implements ServerEventInterface {

	private static final long serialVersionUID = 1L;
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AssetForecastController.class);

    private static final String revenueStr = WebUtil.getMessage("deviceROIlg_1");
    private static final String profitStr = WebUtil.getMessage("deviceROIlg_2");

    private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    private HashMap<String, Object> sqlParams = new HashMap<>(); 

    //UI Params
    private String profitForecast = "";
    private BarChartModel barMonthlyForecast = new BarChartModel();

    // chart parameters
    private int assetId = 1;
    private String assetName = WebUtil.getMessage("preventiveMaintenanceAnalysis_allDevices");
    private boolean isSingleAsset  = false;

    private int targetYear = Year.now().getValue();
    private String selectedYear = Integer.toString(targetYear);

    private String sql;

    private List<Map<String, Object>> monthDep = new ArrayList<>();

    private DateTime fcStartMonth = new DateTime();
    private List<Map<String, Object>> forecastRevenue = new ArrayList<>();
    private List<Map<String, Object>> forecastDep = new ArrayList<>();
    private List<Map<String, Object>> forecastMaint = new ArrayList<>();
    private List<Map<String, Object>> forecastProfit = new ArrayList<>();

    private List<Map<String, Object>> predictRev = new ArrayList<>();
    private List<Map<String, Object>> predictPro = new ArrayList<>();

    // SQL script
    private static final String depMonth =
            "select a.hospital_id as key, " +
                    "sum(d.deprecate_amount) as value " +
                    "from asset_info a, asset_depreciation d " +
                    "where a.id = d.asset_id " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key;";

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
                    "and a.id = w.asset_id and w.status = 2 " +
                    "and a.hospital_id = :#hospital_id " +
                    "group by key order by key;";

    // SQL for single asset
    private static final String assetDep =
            "select a.hospital_id as key, " +
                    "sum(d.deprecate_amount) as value " +
                    "from asset_info a, asset_depreciation d " +
                    "where a.id = d.asset_id " +
                    "and a.hospital_id = :#hospital_id " +
                    "and a.id = :#assetId " +
                    "group by key;";

    private static final String assetRevenue =
            "select sum(r.price_amount) as value, " +
                    "to_char(r.exam_date, 'yyyy-mm') as key " +
                    "from asset_info a, asset_clinical_record r " +
                    "where to_char(r.exam_date, 'yyyy-mm') between to_char(now() - interval '2 year', 'yyyy-mm') " +
                    "and to_char(now(), 'yyyy-mm') " +
                    "and a.id = r.asset_id " +
                    "and a.hospital_id = :#hospital_id " +
                    "and a.id = :#assetId " +
                    "group by key order by key;";

    private static final String assetMaint =
            "select to_char(w.request_time, 'yyyy-mm') as key, " +
                    "sum(w.total_price) as value " +
                    "from asset_info a, work_order w " +
                    "where to_char(w.request_time, 'yyyy-mm') " +
                    "between to_char(now() - interval '2 year', 'yyyy-mm') " +
                    "and to_char(now(), 'yyyy-mm') " +
                    "and a.id = w.asset_id and w.status = 2 " +
                    "and a.hospital_id = :#hospital_id " +
                    "and a.id = :#assetId " +
                    "group by key order by key;";

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String name) {
        assetName = name;
    }

    @PostConstruct
    protected void init() {

        // init sql params
        sqlParams.put("hospital_id", hospital_id);
        sqlParams.put("targetYear", this.targetYear);

        queryForecastProfit();

        createForecastBar();
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

        NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.CHINA);
        profitForecast = cf.format(value);

        forecastRevenue.addAll(predictRev);
        forecastProfit.addAll(predictPro);

    }

    private void predict(List<Map<String, Object>> revenue, List<Map<String, Object>> profit, DateTime startMonth) {
        int offset = revenue.size() / 2;

        List<Map<String, Object>> prdRev = new ArrayList<>();
        List<Map<String, Object>> prdPro = new ArrayList<>();

        DateTime predictMonth = startMonth;
        predictMonth = predictMonth.plusMonths(revenue.size());

        for (int index = 0; index < offset; index++) {
            Map<String, Object> item = predictNextItem(revenue.get(index), revenue.get(index + offset), predictMonth, false);
            prdRev.add(item);

            item = predictNextItem(profit.get(index), profit.get(index + offset), predictMonth, true);
            prdPro.add(item);

            predictMonth = predictMonth.plusMonths(1);
        }

        // evaluate to private parameters every time, in order to avoid dirty data during ajax call
        predictRev = prdRev;
        predictPro = prdPro;
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

    private void createForecastBar() {
        BarChartModel bc = new BarChartModel();

        bc.setLegendPosition("ne");

        Axis xAxis = bc.getAxis(AxisType.X);

        xAxis.setTickAngle(-50);
        xAxis.setTickFormat("%y-%m");

        long interval = 1000 * 60 * 60 * 24 * 30 * 3;
        xAxis.setTickInterval(String.valueOf(interval));
        bc.setBarWidth(11);
        bc.setBarMargin(6);
        bc.getAxes().put(AxisType.X, xAxis);

        String label = revenueStr;
        drawBar(bc, label, forecastRevenue);

        label = profitStr;
        drawBar(bc, label, forecastProfit);

        bc.setExtender("barMonthlyForecast");

        barMonthlyForecast = bc;

    }

    private List<Map<String, Object>> queryForecastRevenue() {
        if (isSingleAsset) {
            sql = assetRevenue;
        } else {
            sql = forecastMonthRevenue;
        }

        forecastRevenue = queryMonthDate(sql, sqlParams, 24, fcStartMonth);

        return forecastRevenue;
    }

    private List<Map<String, Object>> queryForecastMaint() {
        if (isSingleAsset) {
            sql = assetMaint;
        } else {
            sql = forecastMonthMaint;
        }

        forecastMaint = queryMonthDate(sql, sqlParams, 24, fcStartMonth);

        return forecastMaint;
    }

    private List<Map<String, Object>> queryForecastDep() {
        if (isSingleAsset) {
            sql = assetDep;
        } else {
            sql = depMonth;
        }

        monthDep = queryMonthDepDate(sql, sqlParams, 24, fcStartMonth);

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

    public String getProfitForecast() { return profitForecast; }

    public String getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(String selectedYear) {
        this.selectedYear = selectedYear;
    }

    public BarChartModel getBarMonthlyForecast() { return barMonthlyForecast; }

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

    /*
    private List<Map<String, Object>> prepareData(String sql, HashMap<String, Object> sqlParams) {

        sqlParams.put("_sql", sql);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
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
    }*/

    private void drawBar(BarChartModel barChart, String label, List<Map<String, Object>> result) {
        ChartSeries cs = new ChartSeries();
        cs.setLabel(label);

        for (Map<String, Object> item : result) {
            cs.set(item.get("key").toString(), (Double) item.get("value"));
        }

        barChart.addSeries(cs);
    }

    /*
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
    }*/

    private void update() {

        if (isSingleAsset) {
            sqlParams.put("assetId", assetId);
        }

        fcStartMonth = new DateTime();

        queryForecastProfit();

        createForecastBar();
    }

    public void onAssetsChange() {
        isSingleAsset = false;
        this.setAssetName(WebUtil.getMessage("preventiveMaintenanceAnalysis_allDevices"));
        update();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;
        if(asset == null) return;

        this.assetId    = asset.getId();
        this.setAssetName(asset.getName());
        this.isSingleAsset = true;

        update();
    }
}
