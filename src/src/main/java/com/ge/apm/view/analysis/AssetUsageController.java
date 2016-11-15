package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.postgresql.util.PGInterval;
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
public class AssetUsageController {

    protected static final Logger log = LoggerFactory.getLogger(AssetUsageController.class);

    private final Map<String, Object> parameters;

    private int hospitalId;
    private int assetId;

    public AssetUsageController() {
        parameters = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        DateTime today = new DateTime();
        this.startDate = today.minusYears(5).toDate();
        this.endDate = today.toDate();
        parameters.put("startDate", this.startDate);
        parameters.put("endDate", this.endDate);

        this.hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        parameters.put("hospitalId", this.hospitalId);

        this.assetId = 0;
    }

    // region Properties

    private Date startDate;

    public final Date getStartDate() {
        return this.startDate;
    }

    public final void setStartDate(Date value) {
        this.startDate = value;
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
    }

    public final void onEndDateSelect(SelectEvent event) {
        this.setEndDate((Date)event.getObject());
    }


    public final String getTopErrorReason() {
        return convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_REASON)).toString();
    }

    public final String getTopErrorStep() {
        return convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_STEP)).toString();
    }

    public final String getTopErrorRoom() {
        return convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_ROOM_ALL)).toString();
    }

    public final String getTopErrorDeviceType() {
        return convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_DEVICE_TYPE_ALL)).toString();
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

    // endregion

    // region SQL

    private final List<Map<String, Object>> query(String template) {
        return this.query(template, null);
    }

    private final List<Map<String, Object>> query(String template, Map<String, Object> extra) {
        // TODO: replace may not be efficient
        template = StringUtils.replace(template, ":#andHospitalFiler", "AND work.hospital_id = :#hospitalId");
        template = StringUtils.replace(template, ":#andDateFilter", "AND work.request_time BETWEEN :#startDate AND :#endDate");
        if (this.assetId == 0) {
            template = StringUtils.replace(template, ":#andDeviceFilter", "");
        }
        else {
            template = StringUtils.replace(template, ":#andDeviceFilter", "AND work.asset_id = :#assetId");
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

    private static final String SQL_SCALAR_TOP_ERROR_REASON = "" +
            "SELECT work.case_type AS scalar " +
            "FROM work_order AS work " +
            "WHERE TRUE " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilter " +  // AND work.asset_id = :#assetId
            "GROUP BY work.case_type " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障处理流程最耗时步骤

    private static final String SQL_SCALAR_TOP_ERROR_STEP = "" +
            "SELECT step.step_id AS scalar " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilter " +  // AND work.asset_id = :#assetId
            "GROUP BY step.step_id " +
            "ORDER BY avg(step.end_time - step.start_time) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障主要发生的科室

    private static final String SQL_SCALAR_TOP_ERROR_ROOM_ALL = "" +
            "SELECT asset.clinical_dept_id AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY asset.clinical_dept_id " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障主要发生的设备类型

    private static final String SQL_SCALAR_TOP_ERROR_DEVICE_TYPE_ALL = "" +
            "SELECT asset.asset_group AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY asset.asset_group " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障数

    private static final String SQL_SCALAR_ERROR_COUNT_SINGLE = "" +
            "SELECT count(*) AS scalar " +
            "FROM work_order AS work " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilter " +  // AND work.asset_id = :#assetId
            ";";

    // 设备故障原因分析

    private static final String SQL_LIST_ERROR_REASON = "" +
            "SELECT work.case_type AS key, count(*) AS value " +
            "FROM work_order AS work " +
            "WHERE TRUE " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilter " +  // AND work.asset_id = :#assetId
            "GROUP BY work.case_type " +
            ";";

    // 设备故障处理流程响应时间分布

    private static final String SQL_LIST_ERROR_STEP = "" +
            "SELECT step.step_id AS key, avg(EXTRACT (epoch FROM (step.end_time - step.start_time)) / 60) AS value " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilter " +  // AND work.asset_id = :#assetId
            "GROUP BY step.step_id " +
            "ORDER BY step.step_id ASC " +
            ";";

    // （耗时最长的三个）步骤的具体响应时间分布

    private static final String SQL_LIST_TOP_ERROR_STEP = "" +
            "SELECT step.step_id AS scalar " +
            "FROM work_order_step AS step, " +
            "     work_order AS work " +
            "WHERE step.work_order_id = work.id " +
            "  AND step.start_time IS NOT NULL " +
            "  AND step.end_time IS NOT NULL " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            ":#andDeviceFilter " +  // AND work.asset_id = :#assetId
            "GROUP BY step.step_id " +
            "ORDER BY avg(step.end_time - step.start_time) DESC " +
            "LIMIT 3 " +
            ";";

    private static final String SQL_LIST_ERROR_TIME_PER_STEP = "" +
            "SELECT rate AS key, count(*) AS value " +
            "FROM (" +
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
            "        :#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            "        :#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "        :#andDeviceFilter " +  // AND work.asset_id = :#assetId
            ") AS temporary " +
            "GROUP BY rate " +
            "ORDER BY rate ASC " +
            ";";

    // 设备故障分布：按科室

    private static final String SQL_LIST_ERROR_ROOM_ALL = "" +
            "SELECT asset.clinical_dept_id AS key, count(*) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY asset.clinical_dept_id " +
            ";";

    // 设备故障分布：按设备类型

    private static final String SQL_LIST_ERROR_DEVICE_TYPE_ALL = "" +
            "SELECT asset.asset_group AS key, count(*) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY asset.asset_group " +
            ";";

    // 设备故障分布：按单台设备（前40台）

    private static final String SQL_LIST_TOP_ERROR_DEVICE_ALL = "" +
            "SELECT work.asset_id AS key, count(*) AS value " +
            "FROM work_order AS work " +
            "WHERE TRUE " +
            ":#andHospitalFiler " + // AND work.hospital_id = :#hospitalId
            ":#andDateFilter " +    // AND work.request_time BETWEEN :#startDate AND :#endDate
            "GROUP BY work.asset_id " +
            "ORDER BY count(*) DESC " +
            "LIMIT 40 " +
            ";";

    // 设备故障所占比例（和排名）

    // endregion

    // region Chart

    private final static Object convertToScalar(List<Map<String, Object>> list) {
        return list.get(0).get("scalar");
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

    // endregion
}