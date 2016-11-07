package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.*;
import java.io.Serializable;
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
    private double anualCost = 0.0;
    private int revenue = 0; 
    private static final Logger logger = Logger.getLogger(HomeHeadController.class);
    private String logStr;

    
    // chart parameters
    private int targetYear = 2015;
    private int hospitalId = 0;

    private String sql;
    private HashMap<String, Object> sqlParams = new HashMap<>();
    
    private BarChartModel barAnnualRevenue = new BarChartModel();
    private PieChartModel pieAnnualRevenue = new PieChartModel();

    
    public BarChartModel getBarAnnualRevenue() {
        createAnnualBar();
        return barAnnualRevenue;
    }
    
    public PieChartModel getPieAnnualRevenue() {
        createAnnualPie();
        return pieAnnualRevenue;
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
        List<Map<String, Object>> profit = calcProfit(r, d, w);

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
        drawBar(label, r);
        
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
        drawBar(label, profit);
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

    private void drawBar(String label, List<Map<String, Object>> result) {
        ChartSeries revenue = new ChartSeries();
        revenue.setLabel(label);

        for (Map<String, Object> item : result) {
            revenue.set(item.get("key").toString(), (Double) item.get("value"));
        }

        barAnnualRevenue.addSeries(revenue);
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

    
    @PostConstruct
    protected void init() {
    }

}
