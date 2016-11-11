package com.ge.apm.view.analysis;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.print.DocFlavor;

import org.postgresql.util.PGInterval;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.*;
import webapp.framework.dao.NativeSqlUtil;

@ManagedBean
@ViewScoped
public final class AssetUsageController {
    @PostConstruct
    protected void init() {
        // TODO: Development Decision, Default Time
        LocalDate now = LocalDate.now();
        this.toDate = java.sql.Date.valueOf(now);
        this.fromDate = java.sql.Date.valueOf(now.minusYears(5));
    }

    // （时间区间）

    private Date fromDate;

    private Date toDate;

    public Date getFromDate() {
        return this.fromDate;
    }

    public Date getToDate() {
        return this.toDate;
    }

    public void onFromDateSelect(SelectEvent event) {
        this.fromDate = (Date)event.getObject();
        // TODO: Refresh
    }

    public void onToDateSelect(SelectEvent event) {
        this.toDate = (Date)event.getObject();
        // TODO: Refresh
    }

    private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    // 设备故障最主要原因#

    private String majorReason;

    public String getMajorReason() {
        this.createMajorReason();
        return this.majorReason;
    }

    private void createMajorReason() {
        String template = "" +
                "SELECT request_reason AS key, COUNT(*) AS value " +
                "FROM work_order " +
                "WHERE request_time BETWEEN '%s' AND '%s' " +
                "GROUP BY key " +
                "ORDER BY value DESC " +
                "LIMIT 1" +
                ";";
        String query = String.format(template,
                                     DATE_FORMATTER.format(this.fromDate),
                                     DATE_FORMATTER.format(this.toDate));
        List<Map<String, Object>> list = NativeSqlUtil.queryForList(query, null);
        this.majorReason = (String)list.get(0).get("key");
    }


    // 设备故障处理流程最耗时步骤#

    private String majorStep;

    public String getMajorStep() {
        this.createMajorStep();
        return this.majorStep;
    }

    private void createMajorStep() {
        String template = "" +
                "SELECT step_name AS key, avg (end_time - start_time) AS value " +
                "FROM work_order_step " +
                "WHERE start_time BETWEEN '%s' AND '%s' " +
                "GROUP BY key " +
                "ORDER BY value DESC " +
                "LIMIT 1" +
                ";";
        String query = String.format(template,
                DATE_FORMATTER.format(this.fromDate),
                DATE_FORMATTER.format(this.toDate));
        List<Map<String, Object>> list = NativeSqlUtil.queryForList(query, null);
        this.majorStep = (String)list.get(0).get("key");
    }

    // 设备故障主要发生的科室#

    private String majorRoom;

    public String getMajorRoom() {
        this.createMajorRoom();
        return this.majorRoom;
    }

    private void createMajorRoom() {
        String template = "" +
                "SELECT requestor_name AS key, COUNT(*) AS value " +
                "FROM work_order " +
                "WHERE request_time BETWEEN '%s' AND '%s' " +
                "GROUP BY key " +
                "ORDER BY value DESC " +
                "LIMIT 1" +
                ";";
        String query = String.format(template,
                DATE_FORMATTER.format(this.fromDate),
                DATE_FORMATTER.format(this.toDate));
        List<Map<String, Object>> list = NativeSqlUtil.queryForList(query, null);
        this.majorRoom = (String)list.get(0).get("key");
    }

    // 设备故障主要发生的设备类型#

    private String majorDevice;

    public String getMajorDevice() {
        this.createMajorDevice();
        return this.majorDevice;
    }

    private void createMajorDevice() {
        String template = "" +
                "SELECT function_type AS key, COUNT(*) AS value " +
                "FROM work_order " +
                "LEFT OUTER JOIN asset_info ON (work_order.asset_id = asset_info.id) " +
                "WHERE request_time BETWEEN '%s' AND '%s' " +
                "GROUP BY key " +
                "ORDER BY value DESC " +
                "LIMIT 1" +
                ";";
        String query = String.format(template,
                DATE_FORMATTER.format(this.fromDate),
                DATE_FORMATTER.format(this.toDate));
        List<Map<String, Object>> list = NativeSqlUtil.queryForList(query, null);
        this.majorDevice = ((Integer)list.get(0).get("key")).toString();
    }

    // 设备故障原因分布#
    // X 故障原因
    // Y 故障次数

    private BarChartModel reasonDistributionChart;

    public BarChartModel getReasonDistributionChart() {
        this.createReasonDistributionChart();
        return this.reasonDistributionChart;
    }

    private void createReasonDistributionChart() {
        this.reasonDistributionChart = this.createDistributionChart(this.createDistribution("request_reason", 0), // TODO: DC, request_reason
                                                                    "故障原因", "故障次数",
                                                                    "扫描量", "ne"); // TODO: i18n
    }

    // 设备故障流程处理响应时间分布#
    // 步骤、时间

    private HorizontalBarChartModel timeSequenceChart;

    public HorizontalBarChartModel getTimeSequenceChart() {
        this.createTimeSequenceChart();
        return this.timeSequenceChart;
    }

    private void createTimeSequenceChart() {
        HorizontalBarChartModel model = new HorizontalBarChartModel();
        model.setStacked(true);

        String template = "" +
                "SELECT step_name AS key, " +
                "       avg (end_time - start_time) AS value " +
                "FROM work_order_step " +
                "WHERE start_time BETWEEN '%s' AND '%s' " + // TODO: DC, start_time
                "GROUP BY key " +
                "ORDER BY key " +
                ";";
        String query = String.format(template,
                                     DATE_FORMATTER.format(this.fromDate),
                                     DATE_FORMATTER.format(this.toDate));

        for (Map<String, Object> m : NativeSqlUtil.queryForList(query, null)) {
            ChartSeries series = new ChartSeries();
            series.set(m.get("key"), ((PGInterval)m.get("value")).getMinutes());
            model.addSeries(series);
        }

        /*String query2 = new StringBuilder(query).insert(0, "SELECT sum(value) AS sum FROM (").replace(query.length() + 24 - 1, query.length() + 24, ") AS t;").toString();
        List<Map<String, Object>> result2 = NativeSqlUtil.queryForList(query2, null);

        Axis x = model.getAxis(AxisType.X);
        x.setMin(0);
        x.setMax((Long)result2.get(0).get("sum"));
*/
        this.timeSequenceChart = model;
    }

    // 耗时最长的三个步骤的具体响应时间分布#
    // 小于半小时、半小时到一小时、一小时到一天、一天以上、未响应

    private PieChartModel top1TimeChart;

    public PieChartModel getTop1TimeChart() {
        this.createTop1TimeChart();
        return this.top1TimeChart;
    }

    private void createTop1TimeChart() {
        String template = "" +
                "SELECT range AS key, count(*) AS value FROM (" +
                "SELECT CASE " +
                "WHEN (end_time - start_time) BETWEEN '0 minute' AND '30 minute' THEN '0-30 分钟' " +
                "WHEN (end_time - start_time) BETWEEN '30 minute' AND '60 minute' THEN '30-60 分钟' " +
                "ELSE '1 小时以上' END AS range " +
                "FROM work_order_step " +
                "WHERE work_order_step.step_id = (" +
                "  SELECT step_id AS key " +
                "  FROM work_order_step " +
                "  GROUP BY key " +
                "  ORDER BY avg (end_time - start_time) DESC " +
                "  LIMIT 1 " +
                "  )" +
                ") AS t GROUP BY range;";
        String query = String.format(template,
                                     DATE_FORMATTER.format(this.fromDate),
                                     DATE_FORMATTER.format(this.toDate));
        List<Map<String, Object>> result = NativeSqlUtil.queryForList(query, null);

        PieChartModel model = new PieChartModel();
        for(Map<String, Object> m : result) {
            model.set((String)m.get("key"), ((Long)m.get("value")).intValue());
        }
        this.top1TimeChart = model;
    }

    private PieChartModel top2TimeChart;

    public PieChartModel getTop2TimeChart() {
        this.createTop2TimeChart();
        return this.top2TimeChart;
    }

    private void createTop2TimeChart() {
        String template = "" +
                "SELECT range AS key, count(*) AS value FROM (" +
                "SELECT CASE " +
                "WHEN (end_time - start_time) BETWEEN '0 minute' AND '30 minute' THEN '0-30 分钟' " +
                "WHEN (end_time - start_time) BETWEEN '30 minute' AND '60 minute' THEN '30-60 分钟' " +
                "ELSE '1 小时以上' END AS range " +
                "FROM work_order_step " +
                "WHERE work_order_step.step_id = (" +
                "  SELECT step_id AS key " +
                "  FROM work_order_step " +
                "  GROUP BY key " +
                "  ORDER BY avg (end_time - start_time) DESC " +
                "  LIMIT 1 " +
                "  OFFSET 1 " +
                "  )" +
                ") AS t GROUP BY range;";
        String query = String.format(template,
                DATE_FORMATTER.format(this.fromDate),
                DATE_FORMATTER.format(this.toDate));
        List<Map<String, Object>> result = NativeSqlUtil.queryForList(query, null);

        PieChartModel model = new PieChartModel();
        for(Map<String, Object> m : result) {
            model.set((String)m.get("key"), ((Long)m.get("value")).intValue());
        }
        this.top2TimeChart = model;
    }

    private PieChartModel top3TimeChart;

    public PieChartModel getTop3TimeChart() {
        this.createTop3TimeChart();
        return this.top3TimeChart;
    }

    private void createTop3TimeChart() {
        String template = "" +
                "SELECT range AS key, count(*) AS value FROM (" +
                "SELECT CASE " +
                "WHEN (end_time - start_time) BETWEEN '0 minute' AND '30 minute' THEN '0-30 分钟' " +
                "WHEN (end_time - start_time) BETWEEN '30 minute' AND '60 minute' THEN '30-60 分钟' " +
                "ELSE '1 小时以上' END AS range " +
                "FROM work_order_step " +
                "WHERE work_order_step.step_id = (" +
                "  SELECT step_id AS key " +
                "  FROM work_order_step " +
                "  GROUP BY key " +
                "  ORDER BY avg (end_time - start_time) DESC " +
                "  LIMIT 1 " +
                "  OFFSET 2" +
                "  )" +
                ") AS t GROUP BY range;";
        String query = String.format(template,
                DATE_FORMATTER.format(this.fromDate),
                DATE_FORMATTER.format(this.toDate));
        List<Map<String, Object>> result = NativeSqlUtil.queryForList(query, null);

        PieChartModel model = new PieChartModel();
        for(Map<String, Object> m : result) {
            model.set((String)m.get("key"), ((Long)m.get("value")).intValue());
        }
        this.top3TimeChart = model;
    }

    // （与原因分布类似）

    // 设备故障分布（按科室）#
    // X 科室
    // Y 故障次数

    private BarChartModel roomDistributionChart;

    public BarChartModel getRoomDistributionChart() {
        this.createRoomDistributionChart();
        return this.roomDistributionChart;
    }

    private void createRoomDistributionChart() {
        this.roomDistributionChart = this.createDistributionChart(this.createDistribution("requestor_name", 0), // TODO: DC, requestor_name
                                                                  "科室", "故障次数",
                                                                  "扫描量", null); // TODO: i18n
    }


    // 设备故障分布（按设备类型）#
    // X 设备类型
    // Y 故障次数

    private BarChartModel functionDistributionChart;

    public BarChartModel getFunctionDistributionChart() {
        this.createFunctionDistributionChart();
        return this.functionDistributionChart;
    }

    private void createFunctionDistributionChart() {
        this.functionDistributionChart = this.createDistributionChart(this.createDistribution("function_type", "asset_info", "asset_id"), // TODO: DC, function_type
                                                                      "设备类型", "故障次数",
                                                                      "扫描量", null); // TODO: i18n
    }

    // 设备故障分布（按单台设备）#
    // X 单台设备
    // Y 故障次数

    private BarChartModel deviceDistributionChart;

    public BarChartModel getDeviceDistributionChart() {
        this.createDeviceDistributionChart();
        return this.deviceDistributionChart;
    }

    private void createDeviceDistributionChart() {
        this.deviceDistributionChart = this.createDistributionChart(this.createDistribution("asset_name", 40), // TODO: DC, asset_name
                                                                      "设备", "故障次数",
                                                                      "扫描量", null); // TODO: i18n
    }

    // 公

    private List<Map<String, Object>> createDistribution(String key, int limit) {
        String template = "" +
                "SELECT %s AS key, " +
                "       COUNT(*) AS value " +
                "FROM work_order " +
                "WHERE request_time BETWEEN '%s' AND '%s' " + // TODO: DC, request_time
                "GROUP BY key " +
                (limit > 0 ? "ORDER BY VALUE LIMIT %d " : "ORDER BY key ") +
                ";";
        String query = String.format(template, key,
                                     DATE_FORMATTER.format(this.fromDate),
                                     DATE_FORMATTER.format(this.toDate),
                                     limit);
        return NativeSqlUtil.queryForList(query, null);
    }

    private List<Map<String, Object>> createDistribution(String key, String foreignTable, String foreignKey) {
        String template = "" +
                "SELECT %s AS key, " +
                "COUNT(*) AS value " +
                "FROM work_order " +
                "LEFT OUTER JOIN %s ON (work_order.%s = %s.id) " +
                "WHERE request_time BETWEEN '%s' AND '%s' " + // TODO: DC, request_time
                "GROUP BY key " +
                "ORDER BY key " + // TODO: Development Decision
                ";";
        String query = String.format(template, key,
                                     foreignTable, foreignKey, foreignTable,
                                     DATE_FORMATTER.format(this.fromDate),
                                     DATE_FORMATTER.format(this.toDate));
        return NativeSqlUtil.queryForList(query, null);
    }

    private BarChartModel createDistributionChart(List<Map<String, Object>> distribution,
                                         String xLabel, String yLabel,
                                         String legend, String legendPosition) {
        BarChartModel model = new BarChartModel();
        if (legendPosition != null) {
            model.setLegendPosition(legendPosition);
        }

        ChartSeries series = new ChartSeries();
        series.setLabel(legend);
        for (Map<String, Object> m : distribution) {
            series.set(m.get("key"), ((Long)m.get("value")).intValue());
        }
        model.addSeries(series);

        Axis x = model.getAxis(AxisType.X);
        x.setLabel(xLabel);
        Axis y = model.getAxis(AxisType.Y);
        y.setLabel(yLabel);

        return model;
    }
}