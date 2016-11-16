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
        return convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_REASON), Integer.valueOf(0)).toString();
    }

    public final String getTopErrorStep() {
        return convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_STEP), Integer.valueOf(0)).toString();
    }

    public final String getTopErrorRoom() {
        return convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_ROOM_ALL), Integer.valueOf(0)).toString();
    }

    public final String getTopErrorDeviceType() {
        return convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_DEVICE_TYPE_ALL), Integer.valueOf(0)).toString();
    }

    public final String getErrorCount() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Long.valueOf(0)).toString();
    }

    public final BarChartModel getErrorReasonChart() {
        return convertToBarChartModel(this.query(SQL_LIST_ERROR_REASON),
                                      "故障原因", "故障次数"); /* TODO: i18n */
    }

    public final HorizontalBarChartModel getErrorStepChart() {
        List<Map<String, Object>> list = this.query(SQL_LIST_ERROR_STEP);
        return convertToHorizontalBarChartModel(list);
    }

    public final PieChartModel[] getErrorTimePerStep() {
        List<Map<String, Object>> list = this.query(SQL_LIST_TOP_ERROR_STEP);
        PieChartModel[] charts = new PieChartModel[3];
        int index = 0;
        for (Map<String, Object> map : list) {
            Integer scalar = (Integer)map.get("scalar");
            if ("123456".indexOf(scalar.toString()) != -1) { // TODO: this is based on assumption
                Map<String, Object> extra = new HashMap<>();
                extra.put("stepId", scalar);
                charts[index] = convertToPieChartModel(this.query(SQL_LIST_ERROR_TIME_PER_STEP, extra));
                charts[index].setTitle(scalar.toString());
                index++;
            }
        }
        while (index < 3) {
            charts[index] = new PieChartModel();
            index++;
        }
        return charts;
    }

    public final BarChartModel getErrorRoomChart() {
        return convertToBarChartModel(this.query(SQL_LIST_ERROR_ROOM_ALL),
                                      "科室", "故障次数"); /* TODO: i18n */
    }

    public final BarChartModel getErrorDeviceTypeChart() {
        return convertToBarChartModel(this.query(SQL_LIST_ERROR_DEVICE_TYPE_ALL),
                                      "设备类型", "故障次数"); /* TODO: i18n */
    }

    public final BarChartModel getTopErrorDeviceChart() {
        return convertToBarChartModel(this.query(SQL_LIST_TOP_ERROR_DEVICE_ALL),
                                      "设备", "故障次数"); /* TODO: i18n */
    }

    public final double getErrorPercentageInRoomOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Long.valueOf(0))).doubleValue()
             / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_ROOM_OF_DEVICE_SINGLE), Long.valueOf(0))).doubleValue();
    }

    public final String getErrorCountInRoomOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_ROOM_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
    }

    public final String getDeviceCountInRoomOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_DEVICE_COUNT_IN_ROOM_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
    }

    public final String getErrorRankInRoomOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_RANK_IN_ROOM_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
    }

    public final PieChartModel getErrorPercentageInRoomOfDeviceChart() {
        return convertToPieChartModel(this.getErrorPercentageInRoomOfDevice());
    }

    public final double getErrorPercentageInDeviceTypeOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Long.valueOf(0))).doubleValue()
                / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Long.valueOf(0))).doubleValue();
    }

    public final String getErrorCountInDeviceTypeOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
    }

    public final String getDeviceCountInDeviceTypeOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_DEVICE_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
    }

    public final String getErrorRankInDeviceTypeOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_RANK_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
    }

    public final PieChartModel getErrorPercentageInDeviceTypeOfDeviceChart() {
        return convertToPieChartModel(this.getErrorPercentageInDeviceTypeOfDevice());
    }

    public final double getErrorPercentageInTotalOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Long.valueOf(0))).doubleValue()
                / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_TOTAL_OF_DEVICE_SINGLE), Long.valueOf(0))).doubleValue();
    }

    public final String getErrorCountInTotalOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_TOTAL_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
    }

    public final String getDeviceCountInTotalOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_DEVICE_COUNT_IN_TOTAL_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
    }

    public final String getErrorRankInTotalOfDevice() {
        return convertToScalar(this.query(SQL_SCALAR_ERROR_RANK_IN_TOTAL_OF_DEVICE_SINGLE), Long.valueOf(0)).toString();
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
            "SELECT work.case_type AS scalar " +
            "FROM work_order AS work " +
            "WHERE TRUE " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY work.case_type " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障处理流程最耗时步骤

    private final static String SQL_SCALAR_TOP_ERROR_STEP = "" +
            "SELECT step.step_id AS scalar " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY step.step_id " +
            "ORDER BY avg(step.end_time - step.start_time) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障主要发生的科室

    private final static String SQL_SCALAR_TOP_ERROR_ROOM_ALL = "" +
            "SELECT asset.clinical_dept_id AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY asset.clinical_dept_id " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障主要发生的设备类型

    private final static String SQL_SCALAR_TOP_ERROR_DEVICE_TYPE_ALL = "" +
            "SELECT asset.asset_group AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY asset.asset_group " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障数

    private final static String SQL_SCALAR_ERROR_COUNT_SINGLE = "" +
            "SELECT count(*) AS scalar " +
            "FROM work_order AS work " +
            "WHERE TRUE " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            ";";

    // 设备故障原因分析

    private final static String SQL_LIST_ERROR_REASON = "" +
            "SELECT work.case_type AS key, count(*) AS value " +
            "FROM work_order AS work " +
            "WHERE TRUE " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY work.case_type " +
            "ORDER BY work.case_type ASC " +
            ";";

    // 设备故障处理流程响应时间分布

    private final static String SQL_LIST_ERROR_STEP = "" +
            "SELECT step.step_id AS key, avg(EXTRACT (epoch FROM (step.end_time - step.start_time)) / 60) AS value " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY step.step_id " +
            "ORDER BY step.step_id ASC " +
            ";";

    // （耗时最长的三个）步骤的具体响应时间分布

    private final static String SQL_LIST_TOP_ERROR_STEP = "" +
            "SELECT step.step_id AS scalar " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY step.step_id " +
            "ORDER BY avg(step.end_time - step.start_time) DESC " +
            "LIMIT 3 " +
            ";";

    private final static String SQL_LIST_ERROR_TIME_PER_STEP = "" +
            "SELECT rate AS key, count(*) AS value " +
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
            "        WHERE TRUE " +
            "          AND step.work_order_id = work.id " +
            "          AND step.step_id = :#stepId " +
            "        :#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            "        :#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "        :#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            ") AS temporary " +
            "GROUP BY rate " +
            "ORDER BY rate ASC " +
            ";";

    // 设备故障分布：按科室

    private final static String SQL_LIST_ERROR_ROOM_ALL = "" +
            "SELECT asset.clinical_dept_id AS key, count(*) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY asset.clinical_dept_id " +
            "ORDER BY asset.clinical_dept_id ASC " +
            ";";

    // 设备故障分布：按设备类型

    private final static String SQL_LIST_ERROR_DEVICE_TYPE_ALL = "" +
            "SELECT asset.asset_group AS key, count(*) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY asset.asset_group " +
            "ORDER BY asset.asset_group ASC " +
            ";";

    // 设备故障分布：按单台设备（前40台）

    private final static String SQL_LIST_TOP_ERROR_DEVICE_ALL = "" +
            "SELECT work.asset_id AS key, count(*) AS value " +
            "FROM work_order AS work " +
            "WHERE TRUE " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY work.asset_id " +
            "ORDER BY count(*) DESC " +
            "LIMIT 40 " +
            ";";

    // 设备故障所占比例（和排名）

    private final static String SQL_SCALAR_ERROR_COUNT_IN_ROOM_OF_DEVICE_SINGLE = "" +
            "SELECT count(*) AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND asset.clinical_dept_id = ( " +
            "              SELECT asset.clinical_dept_id " +
            "              FROM asset_info AS asset " +
            "              WHERE TRUE " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "              :#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
                           // TODO: include a date filter?
            "      ) " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_ROOM_OF_DEVICE_SINGLE = "" +
            "SELECT count(*) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE asset.clinical_dept_id = ( " +
            "              SELECT asset.clinical_dept_id " +
            "              FROM asset_info AS asset " +
            "              WHERE TRUE " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "              :#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
                           // TODO: include a date filter?
            "      ) " +
            ":#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
            // TODO: include a date filter?
            ";";

    private final static String SQL_SCALAR_ERROR_RANK_IN_ROOM_OF_DEVICE_SINGLE = "" +
            "SELECT value AS scalar " +
            "FROM ( " +
            "        SELECT work.asset_id AS key, rank() OVER (ORDER BY count(*) DESC) AS value " +
            "        FROM work_order AS work, " +
            "             asset_info AS asset " +
            "        WHERE work.asset_id = asset.id " +
            "          AND asset.clinical_dept_id = ( " +
            "                      SELECT asset.clinical_dept_id " +
            "                      FROM asset_info AS asset " +
            "                      WHERE TRUE " +
            "                      :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "                      :#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
                                   // TODO: include a date filter?
            "              ) " +
            "        :#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            "        :#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "        GROUP BY work.asset_id " +
            ") AS temporary " +
            "WHERE key = :#assetId " +
            ";";

    private final static String SQL_SCALAR_ERROR_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE = "" +
            "SELECT count(*) AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE TRUE " +
            "  AND work.asset_id = asset.id " +
            "  AND asset.asset_group = ( " +
            "              SELECT asset.asset_group " +
            "              FROM asset_info AS asset " +
            "              WHERE TRUE " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "              :#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
                           // TODO: include a date filter?
            "      ) " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE = "" +
            "SELECT count(*) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE TRUE " +
            "  AND asset.asset_group = ( " +
            "              SELECT asset.asset_group " +
            "              FROM asset_info AS asset " +
            "              WHERE TRUE " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "              :#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
                           // TODO: include a date filter?
            "      ) " +
            ":#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
            // TODO: include a date filter?
            ";";

    private final static String SQL_SCALAR_ERROR_RANK_IN_DEVICE_TYPE_OF_DEVICE_SINGLE = "" +
            "SELECT value AS scalar " +
            "FROM ( " +
            "        SELECT work.asset_id AS key, rank() OVER (ORDER BY count(*) DESC) AS value " +
            "        FROM work_order AS work, " +
            "             asset_info AS asset " +
            "        WHERE work.asset_id = asset.id " +
            "          AND asset.asset_group = ( " +
            "                      SELECT asset.asset_group " +
            "                      FROM asset_info AS asset " +
            "                      WHERE TRUE " +
            "                      :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "                      :#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
                                   // TODO: include a date filter?
            "              ) " +
            "        :#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            "        :#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "        GROUP BY work.asset_id " +
            ") AS temporary " +
            "WHERE key = :#assetId " +
            ";";

    private final static String SQL_SCALAR_ERROR_COUNT_IN_TOTAL_OF_DEVICE_SINGLE = "" +
            "SELECT count(*) AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_TOTAL_OF_DEVICE_SINGLE = "" +
            "SELECT count(*) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE TRUE " +
            ":#andHospitalFilterForAssetInfo " + // AND asset.hospital_id = :#hospitalId
            // TODO: include a date filter?
            ";";

    private final static String SQL_SCALAR_ERROR_RANK_IN_TOTAL_OF_DEVICE_SINGLE = "" +
            "SELECT value AS scalar " +
            "FROM ( " +
            "        SELECT work.asset_id AS key, rank() OVER (ORDER BY count(*) DESC) AS value " +
            "        FROM work_order AS work, " +
            "             asset_info AS asset " +
            "        WHERE TRUE " +
            "          AND work.asset_id = asset.id " +
            "        :#andHospitalFilterForWorkOrder " + // AND work.hospital_id = :#hospitalId
            "        :#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
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
            axis.setLabel(xLabel);
        }
        if (yLabel != null) {
            Axis axis = chart.getAxis(AxisType.Y);
            axis.setLabel(yLabel);
        }

        return chart;
    }

    private final static HorizontalBarChartModel convertToHorizontalBarChartModel(List<Map<String, Object>> list) {
        HorizontalBarChartModel chart = new HorizontalBarChartModel();
        chart.setStacked(true);

        Double total = 0.0;
        for (Map<String, Object> map: list) {
            ChartSeries series = new ChartSeries();
            series.set(map.get("key"), (Double)map.get("value"));
            chart.addSeries(series);
            total += (Double)map.get("value");
        }

        Axis axis = chart.getAxis(AxisType.X);
        axis.setMin(0.0);
        axis.setMax(total);

        return chart;
    }

    private final static PieChartModel convertToPieChartModel(List<Map<String, Object>> list) {
        PieChartModel chart = new PieChartModel();
        for (Map<String, Object> map : list) {
            chart.set(map.get("key").toString(), (Number)map.get("value"));
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