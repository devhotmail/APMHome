package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public final class AssetMaintenanceController {

    protected final static Logger log = LoggerFactory.getLogger(AssetMaintenanceController.class);

    private final Map<String, Object> parameters;

    private int hospitalId;

    private final int knownCaseTypes = 5;
    private final int knownWorkOrderSteps = 6; // TODO:
    private final int knownAssetGroups = 4;

    public AssetMaintenanceController() {
        this.parameters = new HashMap<>();
    }

    @PostConstruct
    public final void init() {
        DateTime today = new DateTime();
        this.setStartDate(today.minusYears(5).toDate());
        this.setEndDate(today.toDate());

        this.hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        this.parameters.put("hospitalId", this.hospitalId);

        this.parameters.put("knownCaseTypes", this.knownCaseTypes);
        this.parameters.put("knownWorkOrderSteps", this.knownWorkOrderSteps);
        this.parameters.put("knownAssetGroups", this.knownAssetGroups);
    }

    // region Parameters

    private Date startDate;

    public final Date getStartDate() {
        return this.startDate;
    }

    public final void setStartDate(Date value) {
        this.startDate = value;
        this.parameters.put("startDate", value);
    }

    public final void onStartDateSelect(SelectEvent event) {
        this.setStartDate((Date)event.getObject());
    }

    private Date endDate;

    public final Date getEndDate() {
        return this.endDate;
    }

    public final void setEndDate(Date value) {
        this.endDate = value;
        this.parameters.put("endDate", value);
    }

    public final void onEndDateSelect(SelectEvent event) {
        this.setEndDate((Date)event.getObject());
    }

    private int assetId;

    public final int getAssetId() {
        return this.assetId;
    }

    public final void setAssetId(int value) {
        this.assetId = value;
        this.parameters.put("assetId", value);
    }

    // endregion

    // region Properties

    public final String getTopErrorReason() {
        Integer id = convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_REASON), Integer.valueOf(0));
        if (id == null || id.intValue() == 0) {
            return "未填"; // TODO: i18n
        }
        if (id > 99) {
            return "其他原因"; // TODO: i18n
        }
        return WebUtil.getFieldValueMessage("caseType", id.toString());
    }

    public final String getTopErrorStep() {
        Integer id = convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_STEP), Integer.valueOf(0));
        if (id == null || id.intValue() == 0) {
            return "未填"; // TODO: i18n
        }
        return WebUtil.getFieldValueMessage("woSteps", id.toString());
    }

    public final String getTopErrorRoom() {
        Integer id = convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_ROOM_ALL), Integer.valueOf(0));
        if (id == null || id.intValue() == 0) {
            return "未填";
        }
        return String.format("第%d科室", id.intValue());
    }

    public final String getTopErrorDeviceType() {
        Integer id = convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_DEVICE_TYPE_ALL), Integer.valueOf(0));
        if (id == null || id.intValue() == 0) {
            return "未填"; // TODO: i18n
        }
        if (id > 99) {
            return "其他设备类型"; // TODO: i18n
        }
        return WebUtil.getFieldValueMessage("assetGroup", id.toString());
    }

    public final String getErrorCount() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Integer.valueOf(0)).toString();
    }

    public final BarChartModel getErrorReasonChart() {
        List<Map<String, Object>> list = this.query(SQL_LIST_ERROR_REASON);
        BarChartModel chart = new BarChartModel();
        ChartSeries series = new ChartSeries();
        for (Map<String, Object> map : list) {
            Integer id = (Integer)map.get("key");
            String key;
            if (id == null || id.intValue() == 0) {
                key = "未填"; // TODO: i18n
            }
            else if (id > 99) {
                key = "其他原因"; // TODO: i18n
            }
            else {
                key = WebUtil.getFieldValueMessage("caseType", id.toString());
            }
            series.set(key, (Number)map.get("value"));
        }
        chart.addSeries(series);
        chart.getAxis(AxisType.X).setLabel(WebUtil.getMessage("maintenanceAnalysis_reasonChart_xAxis"));
        chart.getAxis(AxisType.Y).setLabel(WebUtil.getMessage("maintenanceAnalysis_reasonChart_yAxis"));
        return chart;
    }

    private final static String[] COLORS = new String[] { "5da5da", "faa43a", "60bd68", "f17cb0", "b2912f", "decf13" };

    public final HorizontalBarChartModel getErrorStepChart() {
        List<Map<String, Object>> data = this.query(SQL_LIST_ERROR_STEP);
        int[] raw = new int[this.knownWorkOrderSteps];
        int count = 0;
        int total = 0;
        for (Map<String, Object> map : data) {
            int key = (int)map.get("key");
            int value = (int)map.get("value");
            count++;
            raw[key - 1] = value;
            total += value;
        }
        int[] ext = new int[this.knownWorkOrderSteps];
        int average = 0;
        if (count > 0) {
            average = total / count;
        }
        if (average < 2) {
            average = 2;
        }
        for (int i = 0; i < this.knownWorkOrderSteps; i++) {
            if (raw[i] == 0) {
                ext[i] = average;
            }
            else {
                ext[i] = raw[i];
            }
        }

        HorizontalBarChartModel chart = new HorizontalBarChartModel();
        chart.setStacked(true);
        total = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.knownWorkOrderSteps; i++) {
            ChartSeries series = new ChartSeries();
            String key = WebUtil.getFieldValueMessage("woSteps", Integer.toString(i + 1));
            int value = ext[i];
            key = String.format("%s%d分钟", key, value); // TODO: i18n
            series.set(key, value);
            chart.addSeries(series);
            total += value;
            if (builder.length() != 0) {
                builder.append(",");
            }
            if (raw[i] == 0) {
                builder.append("CCCCCC");
            }
            else {
                builder.append(COLORS[i % 6]);
            }
        }
        Axis axis = chart.getAxis(AxisType.X);
        axis.setMin(0);
        axis.setMax(total);
        chart.setSeriesColors(builder.toString());
        return chart;
    }

    public final PieChartModel[] getErrorTimePerStep() {
        List<Map<String, Object>> list = this.query(SQL_LIST_TOP_ERROR_STEP);
        if (list.size() < this.knownWorkOrderSteps) {
            return new PieChartModel[] { new PieChartModel(), new PieChartModel(), new PieChartModel() };
        }

        PieChartModel[] charts = new PieChartModel[3];
        for (int index = 0; index < 3; index++) {
            int scalar = (int)list.get(index).get("scalar");
            Map<String, Object> extra = new HashMap<>();
            extra.put("stepId", scalar);
            List<Map<String, Object>> innerList = this.query(SQL_LIST_ERROR_TIME_PER_STEP, extra);
            PieChartModel chart = new PieChartModel();
            int total = 0;
            for (Map<String, Object> map : innerList) {
                int value = (int)map.get("value");
                total += value;
            }
            for (Map<String, Object> map : innerList) {
                String key = Integer.toString((int)map.get("key"));
                int value = (int)map.get("value");
                key = WebUtil.getMessage(String.format("maintenanceAnalysis_timeChart_legend_%s", key));
                key = String.format(key, Integer.toString((int)(((double)value)/total*100)));
                chart.set(key, value);
            }
            chart.setTitle(WebUtil.getFieldValueMessage("woSteps", Integer.toString(index + 1)));
            chart.setLegendPosition("w");
            charts[index] = chart;
        }
        return charts;
    }

    public final BarChartModel getErrorRoomChart() {
        return convertToBarChartModel(this.query(SQL_LIST_ERROR_ROOM_ALL),
                                      WebUtil.getMessage("maintenanceAnalysis_distributionChart_room_xAxis"),
                                      WebUtil.getMessage("maintenanceAnalysis_distributionChart_yAxis"));
    }

    public final BarChartModel getErrorDeviceTypeChart() {
        List<Map<String, Object>> list = this.query(SQL_LIST_ERROR_DEVICE_TYPE_ALL);
        BarChartModel chart = new BarChartModel();
        ChartSeries series = new ChartSeries();
        for (Map<String, Object> map : list) {
            Integer id = (Integer)map.get("key");
            String key;
            if (id == null || id.intValue() == 0) {
                key = "未填"; // TODO: i18n
            }
            else if (id > 99) {
                key = "其他设备类型"; // TODO: i18n
            }
            else {
                key = WebUtil.getFieldValueMessage("assetGroup", id.toString());
            }
            series.set(key, (Number)map.get("value"));
        }
        chart.addSeries(series);
        chart.getAxis(AxisType.X).setLabel(WebUtil.getMessage("maintenanceAnalysis_distributionChart_category_xAxis"));
        chart.getAxis(AxisType.Y).setLabel(WebUtil.getMessage("maintenanceAnalysis_distributionChart_yAxis"));
        return chart;
    }

    public final BarChartModel getTopErrorDeviceChart() {
        return convertToBarChartModel(this.query(SQL_LIST_TOP_ERROR_DEVICE_ALL),
                                      WebUtil.getMessage("maintenanceAnalysis_distributionChart_device_xAxis"),
                                      WebUtil.getMessage("maintenanceAnalysis_distributionChart_yAxis"));
    }

    public final double getErrorPercentageInRoomOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Integer.valueOf(0))).doubleValue()
             / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_ROOM_OF_DEVICE_SINGLE), Integer.valueOf(0))).doubleValue();
    }

    public final String getErrorPercentageInRoomOfDeviceString() {
        return String.format(WebUtil.getMessage("%s%%"),
                             Integer.toString((int)Math.round(this.getErrorPercentageInRoomOfDevice()*100)));
    }

    public final String getErrorCountInRoomOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_ROOM_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final String getDeviceCountInRoomOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_DEVICE_COUNT_IN_ROOM_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final String getErrorRankInRoomOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_RANK_IN_ROOM_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final PieChartModel getErrorPercentageInRoomOfDeviceChart() {
        return convertToPieChartModel(this.getErrorPercentageInRoomOfDevice());
    }

    public final double getErrorPercentageInDeviceTypeOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Integer.valueOf(0))).doubleValue()
                / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Integer.valueOf(0))).doubleValue();
    }

    public final String getErrorPercentageInDeviceTypeOfDeviceString() {
        return String.format(WebUtil.getMessage("%s%%"),
                             Integer.toString((int)Math.round(this.getErrorPercentageInDeviceTypeOfDevice()*100)));
    }

    public final String getErrorCountInDeviceTypeOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final String getDeviceCountInDeviceTypeOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_DEVICE_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final String getErrorRankInDeviceTypeOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_RANK_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final PieChartModel getErrorPercentageInDeviceTypeOfDeviceChart() {
        return convertToPieChartModel(this.getErrorPercentageInDeviceTypeOfDevice());
    }

    public final double getErrorPercentageInTotalOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Integer.valueOf(0))).doubleValue()
                / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_TOTAL_OF_DEVICE_SINGLE), Integer.valueOf(0))).doubleValue();
    }

    public final String getErrorPercentageInTotalOfDeviceString() {
        return String.format(WebUtil.getMessage("%s%%"),
                Integer.toString((int)Math.round(this.getErrorPercentageInTotalOfDevice()*100)));
    }

    public final String getErrorCountInTotalOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_TOTAL_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final String getDeviceCountInTotalOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_DEVICE_COUNT_IN_TOTAL_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final String getErrorRankInTotalOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_RANK_IN_TOTAL_OF_DEVICE_SINGLE), Integer.valueOf(0)).toString();
    }

    public final PieChartModel getErrorPercentageInTotalOfDeviceChart() {
        return convertToPieChartModel(this.getErrorPercentageInTotalOfDevice());
    }


    // endregion

    // region SQL

    private final List<Map<String, Object>> query(String template) {
        return this.query(template, null);
    }

    private final List<Map<String, Object>> query(String template, Map<String, Object> extra) {
        // TODO: replace may not be efficient
        template = StringUtils.replace(template, ":#andHospitalFilterForWorkOrder", "AND work.hospital_id = :#hospitalId");
        template = StringUtils.replace(template, ":#andHospitalFilterForAssetInfo", "AND asset.hospital_id = :#hospitalId");
        template = StringUtils.replace(template, ":#andDateFilter", "AND work.request_time BETWEEN :#startDate AND :#endDate");
        if (this.assetId == 0) {
            template = StringUtils.replace(template, ":#andDeviceFilterForWorkOrder", "");
            template = StringUtils.replace(template, ":#andDeviceFilterForAssetInfo", "");
        }
        else {
            template = StringUtils.replace(template, ":#andDeviceFilterForWorkOrder", "AND work.asset_id = :#assetId");
            template = StringUtils.replace(template, ":#andDeviceFilterForAssetInfo", "AND asset.id = :#assetId");
        }
        log.info("=> {}", template);
        if (extra == null) {
            return NativeSqlUtil.queryForList(template, this.parameters);
        }
        else {
            // TODO: better approach?
            Map<String, Object> temporary = new HashMap(this.parameters);
            temporary.putAll(extra);
            return NativeSqlUtil.queryForList(template, temporary);
        }
    }

    // 设备故障最主要原因

    private final static String SQL_SCALAR_TOP_ERROR_REASON = "" +
            "SELECT CASE " +
            "        WHEN work.case_type > 0 AND work.case_type <= :#knownCaseTypes THEN work.case_type " +
            "        WHEN work.case_type > :#knownCaseTypes THEN CAST(100 AS INTEGER) " +
            "        ELSE CAST(0 AS INTEGER) " +
            "END AS scalar " +
            "FROM work_order AS work " +
            "WHERE work.hospital_id = :#hospitalId" +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY scalar " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障处理流程最耗时步骤

    private final static String SQL_SCALAR_TOP_ERROR_STEP = "" +
            "SELECT step.step_id AS scalar " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.step_id > 0 AND step.step_id <= :#knownWorkOrderSteps" +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            "  AND work.hospital_id = :#hospitalId" +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY scalar " +
            "ORDER BY avg(step.end_time - step.start_time) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障主要发生的科室 // TODO: no room info

    private final static String SQL_SCALAR_TOP_ERROR_ROOM_ALL = "" +
            "SELECT asset.clinical_dept_id AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "      AND work.hospital_id = :#hospitalId " +
            "      AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "GROUP BY asset.clinical_dept_id " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障主要发生的设备类型

    private final static String SQL_SCALAR_TOP_ERROR_DEVICE_TYPE_ALL = "" +
            "SELECT CASE " +
            "        WHEN asset.asset_group > 0 AND asset.asset_group <= :#knownAssetGroups THEN asset.asset_group " +
            "        WHEN asset.asset_group > :#knownAssetGroups THEN CAST(100 AS INTEGER) " +
            "        ELSE CAST (0 AS INTEGER)" +
            "END AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "GROUP BY scalar " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障数

    private final static String SQL_SCALAR_ERROR_COUNT_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM work_order AS work " +
            "WHERE TRUE " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            ";";

    // 设备故障原因分析

    private final static String SQL_LIST_ERROR_REASON = "" +
            "SELECT COALESCE(extended.key, partial.key) AS key, COALESCE(partial.value, CAST(0 AS INTEGER)) AS value " +
            "FROM generate_series(0, :#knownCaseTypes) AS extended(key) " +
            "     FULL OUTER JOIN (" +
            "        SELECT CASE " +
            "                WHEN work.case_type > 0 AND work.case_type <= :#knownCaseTypes THEN work.case_type " +
            "                WHEN work.case_type > :#knownCaseTypes THEN CAST(100 AS INTEGER) " +
            "                ELSE CAST(0 AS INTEGER) " +
            "        END AS key, count(*) AS value " +
            "        FROM work_order AS work " +
            "        WHERE work.hospital_id = :#hospitalId " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        :#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "        GROUP BY key " +
            "     ) AS partial " +
            "     ON extended.key = partial.key " +
            "ORDER BY key ASC " +
            ";";

    // 设备故障处理流程响应时间分布

    private final static String SQL_LIST_ERROR_STEP = "" +
            "SELECT step.step_id AS key, CAST(avg(EXTRACT (epoch FROM (step.end_time - step.start_time)) / 60) AS INTEGER) AS value " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.step_id > 0 AND step.step_id <= :#knownWorkOrderSteps" +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY key " +
            "ORDER BY key ASC " +
            ";";

    // （耗时最长的三个）步骤的具体响应时间分布

    private final static String SQL_LIST_TOP_ERROR_STEP = "" +
            "SELECT step.step_id AS scalar " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.step_id > 0 AND step.step_id <= :#knownWorkOrderSteps" +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY scalar " +
            "ORDER BY avg(step.end_time - step.start_time) DESC " +
            ";";

    private final static String SQL_LIST_ERROR_TIME_PER_STEP = "" +
            "SELECT rate AS key, CAST(count(*) AS INTEGER) AS value " +
            "FROM ( " +
            "        SELECT CASE " +
            "                WHEN step.start_time IS NULL OR end_time IS NULL THEN CAST (0 AS integer) " + // 未响应
            "                WHEN (step.end_time - step.start_time) BETWEEN '0 minute' AND '30 minute' THEN CAST (1 AS integer) " + // 小于 30 分钟
            "                WHEN (step.end_time - step.start_time) BETWEEN '30 minute' AND '1 hour' THEN CAST (2 AS integer) " + // 30 分钟到 1 小时
            "                WHEN (step.end_time - step.start_time) BETWEEN '1 hour' AND '1 day' THEN CAST (3 AS integer) " + // 1 小时到 1 天
            "                ELSE CAST (4 AS integer) " + // 1天以上
            "        END AS rate " +
            "        FROM work_order_step AS step, " +
            "             work_order AS work " +
            "        WHERE step.work_order_id = work.id " +
            "          AND step.step_id = :#stepId " +
            "        :#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            "        :#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "        :#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            ") AS temporary " +
            "GROUP BY rate " +
            "ORDER BY rate ASC " +
            ";";

    // 设备故障分布：按科室 // TODO: no room info, possibly ZERO

    private final static String SQL_LIST_ERROR_ROOM_ALL = "" +
            "SELECT asset.clinical_dept_id AS key, count(*) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "GROUP BY asset.clinical_dept_id " +
            "ORDER BY asset.clinical_dept_id ASC " +
            ";";

    // 设备故障分布：按设备类型

    private final static String SQL_LIST_ERROR_DEVICE_TYPE_ALL = "" +
            "SELECT CASE " +
            "        WHEN asset.asset_group > 0 AND asset.asset_group <= :#knownAssetGroups THEN asset.asset_group " +
            "        WHEN asset.asset_group > :#knownAssetGroups THEN CAST(100 AS INTEGER) " +
            "        ELSE CAST (0 AS INTEGER)" +
            "END AS key, count(*) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "GROUP BY key " +
            "ORDER BY key ASC " +
            ";";

    // 设备故障分布：按单台设备（前40台） // TODO: possibly ZERO

    private final static String SQL_LIST_TOP_ERROR_DEVICE_ALL = "" +
            "SELECT asset.name AS key, CAST(count(*) AS INTEGER) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND work.hospital_id = :#hospitalId" +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "GROUP BY asset.id " +
            "ORDER BY count(*) DESC " +
            "LIMIT 40 " +
            ";";

    // 设备故障所占比例（和排名）

    private final static String SQL_SCALAR_ERROR_COUNT_IN_ROOM_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND asset.clinical_dept_id = ( " +
            "              SELECT asset.clinical_dept_id " +
            "              FROM asset_info AS asset " +
            "              WHERE asset.hospital_id = :#hospitalId " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "      ) " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_ROOM_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE asset.clinical_dept_id = ( " +
            "              SELECT asset.clinical_dept_id " +
            "              FROM asset_info AS asset " +
            "              WHERE asset.hospital_id = :#hospitalId " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "      ) " +
            "  AND asset.hospital_id = :#hospitalId " +
            // TODO: include a date filter?
            ";";

    private final static String SQL_SCALAR_ERROR_RANK_IN_ROOM_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(value AS INTEGER) AS scalar " +
            "FROM ( " +
            "        SELECT work.asset_id AS key, rank() OVER (ORDER BY count(*) DESC) AS value " +
            "        FROM work_order AS work, " +
            "             asset_info AS asset " +
            "        WHERE work.asset_id = asset.id " +
            "          AND asset.clinical_dept_id = ( " +
            "                      SELECT asset.clinical_dept_id " +
            "                      FROM asset_info AS asset " +
            "                      WHERE asset.hospital_id = :#hospitalId " +
            "                      :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "              ) " +
            "          AND work.hospital_id = :#hospitalId " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        GROUP BY work.asset_id " +
            ") AS temporary " +
            "WHERE key = :#assetId " +
            ";";

    private final static String SQL_SCALAR_ERROR_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE TRUE " +
            "  AND work.asset_id = asset.id " +
            "  AND asset.asset_group = ( " +
            "              SELECT asset.asset_group " +
            "              FROM asset_info AS asset " +
            "              WHERE asset.hospital_id = :#hospitalId " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "      ) " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE TRUE " +
            "  AND asset.asset_group = ( " +
            "              SELECT asset.asset_group " +
            "              FROM asset_info AS asset " +
            "              WHERE asset.hospital_id = :#hospitalId " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "      ) " +
            "  AND asset.hospital_id = :#hospitalId " +
            // TODO: include a date filter?
            ";";

    private final static String SQL_SCALAR_ERROR_RANK_IN_DEVICE_TYPE_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(value AS INTEGER) AS scalar " +
            "FROM ( " +
            "        SELECT work.asset_id AS key, rank() OVER (ORDER BY count(*) DESC) AS value " +
            "        FROM work_order AS work, " +
            "             asset_info AS asset " +
            "        WHERE work.asset_id = asset.id " +
            "          AND asset.asset_group = ( " +
            "                      SELECT asset.asset_group " +
            "                      FROM asset_info AS asset " +
            "                      WHERE asset.hospital_id = :#hospitalId " +
            "                      :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "              ) " +
            "        AND work.hospital_id = :#hospitalId " +
            "        AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        GROUP BY work.asset_id " +
            ") AS temporary " +
            "WHERE key = :#assetId " +
            ";";

    private final static String SQL_SCALAR_ERROR_COUNT_IN_TOTAL_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND work.hospital_id = :#hospitalId " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_TOTAL_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE asset.hospital_id = :#hospitalId " +
            // TODO: include a date filter?
            ";";

    private final static String SQL_SCALAR_ERROR_RANK_IN_TOTAL_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(value AS INTEGER) AS scalar " +
            "FROM ( " +
            "        SELECT work.asset_id AS key, rank() OVER (ORDER BY count(*) DESC) AS value " +
            "        FROM work_order AS work, " +
            "             asset_info AS asset " +
            "        WHERE TRUE " +
            "          AND work.asset_id = asset.id " +
            "          AND work.hospital_id = :#hospitalId " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        GROUP BY work.asset_id " +
            ") AS temporary " +
            "WHERE key = :#assetId " +
            ";";

    // endregion

    // region Chart

    private final static <T> T convertToScalar(List<Map<String, Object>> list, T fallback) {
        FluentIterable<Map<String, Object>> iterable = FluentIterable.from(list);
        return (T)iterable.first().or(ImmutableMap.of("scalar", (Object)fallback)).get("scalar");
    }

    private final static BarChartModel convertToBarChartModel(List<Map<String, Object>> list, String xLabel, String yLabel) {
        BarChartModel chart = new BarChartModel();

        ChartSeries series = new ChartSeries();
        for (Map<String, Object> map : list) {
            series.set(map.get("key"), (Number)map.get("value"));
        }
        chart.addSeries(series);

        if (xLabel != null) {
            Axis axis = chart.getAxis(AxisType.X);
            axis.setTickAngle(-90);
            axis.setLabel(xLabel);
        }
        if (yLabel != null) {
            Axis axis = chart.getAxis(AxisType.Y);
            axis.setLabel(yLabel);
        }

        return chart;
    }

    private final static PieChartModel convertToPieChartModel(double primary) {
        PieChartModel chart = new PieChartModel();
        chart.set("THIS", primary); // TODO: i18n
        if (primary < 1) {
            chart.set("THAT", 1 - primary); // TODO: i18n
        }
        return chart;
    }

    // endregion
}