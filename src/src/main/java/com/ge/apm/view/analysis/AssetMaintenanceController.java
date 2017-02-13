package com.ge.apm.view.analysis;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.primefaces.model.chart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import java.util.*;

@ManagedBean
@ViewScoped
public final class AssetMaintenanceController implements ServerEventInterface {

    private static final Logger logger = LoggerFactory.getLogger(AssetMaintenanceController.class);

    private static final String checkIntervalNotice_1 = WebUtil.getMessage("checkIntervalNotice_1");
    private static final String checkIntervalNotice_2 = WebUtil.getMessage("checkIntervalNotice_2");

    private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();

    private HashMap<String, Object> sqlParams = new HashMap<>(); 

    private final int knownCaseTypes = 5;
    private final int knownWorkOrderSteps = 6;
    private final int knownAssetGroups = 13;
//    private final int knownRooms = 5;

    private Date startDate;
	private Date endDate;
	
    @PostConstruct
    public final void init() {
        DateTime today = new DateTime();
        startDate = today.minusYears(1).toDate();
        sqlParams.put("startDate", startDate);
        endDate = today.toDate();
        sqlParams.put("endDate", endDate);

        sqlParams.put("hospital_id", hospital_id);
        sqlParams.put("knownCaseTypes", this.knownCaseTypes);
        sqlParams.put("knownWorkOrderSteps", this.knownWorkOrderSteps);
        sqlParams.put("knownAssetGroups", this.knownAssetGroups);
//        sqlParams.put("knownRooms", this.knownRooms);

        this.setAll();
    }

    public final void submit() {
    	
        if (!checkInterval(startDate, endDate)) {
        	sqlParams.put("_sql", "");
            logger.error("{} {} {} {} \"{}\" {} \"{}\"", remote_addr, site_id, hospital_id, username, page_uri, sqlParams, "Invalid query interval");
        }
        else
            this.setAll();
    }

    public final Date getStartDate() {
        return this.startDate;
    }

    public final void setStartDate(Date value) {
        this.startDate = value;
        sqlParams.put("startDate", this.startDate);
    }

    private boolean checkInterval(Date startDate, Date endDate) {
        DateTime start = new DateTime(startDate);
        Interval interval = new Interval(start.plusMonths(1).plusDays(-1), start.plusYears(3).plusDays(1));
        boolean flag = interval.contains(new DateTime(endDate));
        if (!flag) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, checkIntervalNotice_1, checkIntervalNotice_2);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return flag;
    }
    
    public final Date getEndDate() {
        return this.endDate;
    }

    public final void setEndDate(Date value) {
            this.endDate = value;
            sqlParams.put("endDate", this.endDate);
    }

    private static final boolean validateDate(Date startDate, Date endDate) {
        if (new DateTime(startDate).plusMonths(1).isBefore(new DateTime(endDate)) &&
            new DateTime(startDate).plusYears(3).plusDays(1).isAfter(new DateTime(endDate))) {
            return true;
        }
        else {
            return false;
        }
    }

    private int assetId;

    public final int getAssetId() {
        return this.assetId;
    }

    public final void setAssetId(int value) {
        this.assetId = value;
        sqlParams.put("assetId", value);

        this.setAll();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;

        if (asset == null) {
            return;
        }

        Integer assetId = asset.getId();

        WebUtil.navigateTo("/portal/analysis/assetMaintenanceSingle.xhtml?faces-redirect=true&assetId=" + assetId);
    }

    // endregion

    // region Properties

    private final void setAll() {
        this.setAssetName();
        this.setTopErrorReason();
        this.setTopErrorStep();
        if (this.assetId == 0) {
            this.setTopErrorRoom();
            this.setTopErrorDeviceType();
        }
        if (this.assetId != 0) {
            this.setErrorCount();
        }
        this.setErrorReasonChart();
        this.setErrorStepChart();
        this.setErrorTimePerStep();
        if (this.assetId == 0) {
            this.setErrorRoomChart();
            this.setErrorDeviceTypeChart();
            this.setTopErrorDeviceChart();
        }
    }

    private String assetName;

    public final String getAssetName() {
        return this.assetName;
    }

    private final void setAssetName() {
        if (this.assetId == 0) {
            this.assetName = WebUtil.getMessage("preventiveMaintenanceAnalysis_allDevices");
        }
        else {
            this.assetName = convertToScalar(this.query(SQL_SCALAR_DEVICE_NAME_SINGLE), WebUtil.getMessage("maintenanceAnalysis_empty"));
        }
    }

    private String topErrorReason;

    public final String getTopErrorReason() {
        return this.topErrorReason;
    }

    private final void setTopErrorReason() {
        Integer id = convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_REASON), Integer.valueOf(0));
        if (id == null || id.intValue() == 0) {
            this.topErrorReason = WebUtil.getMessage("maintenanceAnalysis_empty");
            return;
        }
        if (id > 99) {
            this.topErrorReason = WebUtil.getMessage("maintenanceAnalysis_otherReason");
            return;
        }
        this.topErrorReason = WebUtil.getFieldValueMessage("caseType", id.toString());
    }

    private String topErrorStep;

    public final String getTopErrorStep() {
        return this.topErrorStep;
    }

    private final void setTopErrorStep() {
        Integer id = convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_STEP), Integer.valueOf(0));
        if (id == null || id.intValue() == 0) {
            this.topErrorStep = WebUtil.getMessage("maintenanceAnalysis_empty");
            return;
        }
        this.topErrorStep = WebUtil.getFieldValueMessage("woSteps", id.toString());
    }

    private String topErrorRoom;

    public final String getTopErrorRoom() {
        return this.topErrorRoom;
    }

    private final void setTopErrorRoom() {
        String name = convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_ROOM_ALL), "");
        if (name == null || name.isEmpty()) {
            this.topErrorRoom = WebUtil.getMessage("maintenanceAnalysis_empty");
            return;
        }
        this.topErrorRoom = name;
    }

    private String topErrorDeviceType;

    public final String getTopErrorDeviceType() {
        return this.topErrorDeviceType;
    }

    private final void setTopErrorDeviceType() {
        Integer id = convertToScalar(this.query(SQL_SCALAR_TOP_ERROR_DEVICE_TYPE_ALL), Integer.valueOf(0));
        if (id == null || id.intValue() == 0) {
            this.topErrorDeviceType = WebUtil.getMessage("maintenanceAnalysis_empty");
            return;
        }
        if (id > 99) {
            this.topErrorDeviceType = WebUtil.getMessage("maintenanceAnalysis_otherCategory");
            return;
        }
        this.topErrorDeviceType = WebUtil.getFieldValueMessage("assetGroup", id.toString());
    }

    private String errorCount;

    public final String getErrorCount() {
        return this.errorCount;
    }

    private final void setErrorCount() {
        this.errorCount = convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Integer.valueOf(0)).toString();
    }

    private BarChartModel errorReasonChart;

    public final BarChartModel getErrorReasonChart() {
        return this.errorReasonChart;
    }

    public final void setErrorReasonChart() {
        List<Map<String, Object>> list = this.query(SQL_LIST_ERROR_REASON);
        BarChartModel chart = new BarChartModel();
        ChartSeries series = new ChartSeries();
        for (Map<String, Object> map : list) {
            Integer id = (Integer)map.get("key");
            String key;
            if (id == null || id.intValue() == 0) {
                key = WebUtil.getMessage("maintenanceAnalysis_empty");
            }
            else if (id > 99) {
                key = WebUtil.getMessage("maintenanceAnalysis_otherReason");
            }
            else {
                key = WebUtil.getFieldValueMessage("caseType", id.toString());
            }
            series.set(key, (Number)map.get("value"));
        }
        chart.addSeries(series);
        chart.getAxis(AxisType.X).setLabel(WebUtil.getMessage("maintenanceAnalysis_reasonChart_xAxis"));
        chart.getAxis(AxisType.Y).setLabel(WebUtil.getMessage("maintenanceAnalysis_reasonChart_yAxis"));
        chart.setExtender("maintenanceE2");
        this.errorReasonChart = chart;
    }

    private HorizontalBarChartModel errorStepChart;

    public final HorizontalBarChartModel getErrorStepChart() {
        return this.errorStepChart;
    }

    private final void setErrorStepChart() {
        List<Map<String, Object>> data = this.query(SQL_LIST_ERROR_STEP);
        int[] raw = new int[this.knownWorkOrderSteps];

        for (Map<String, Object> map : data) {
            int key = (int)map.get("key");
            int value = (int)map.get("value");
            raw[key - 1] = value;
        }

        HorizontalBarChartModel chart = new HorizontalBarChartModel();
        chart.setStacked(true);
        for (int i = 0; i < this.knownWorkOrderSteps; i++) {
            ChartSeries series = new ChartSeries();
            String key = WebUtil.getFieldValueMessage("woSteps", Integer.toString(i + 1));
            int value = raw[i];
            series.set(key, value);
            series.setLabel(key);
            chart.addSeries(series);
        }
        chart.getAxis(AxisType.X).setMin(0);
        chart.setExtender("maintenanceE3");
        this.errorStepChart = chart;
    }

    private List<AbstractMap.SimpleImmutableEntry<String, PieChartModel>> errorTimePerStep;

    public final List<AbstractMap.SimpleImmutableEntry<String, PieChartModel>> getErrorTimePerStep() {
        return this.errorTimePerStep;
    }

    private final void setErrorTimePerStep() {
        List<Map<String, Object>> list = this.query(SQL_LIST_TOP_ERROR_STEP);
        List<AbstractMap.SimpleImmutableEntry<String, PieChartModel>> charts = new ArrayList<>();
        int index = 0;
        for (; index < 3 && index < list.size(); index++) {
            int scalar = (int)list.get(index).get("scalar");
            Map<String, Object> extra = new HashMap<>();
            extra.put("stepId", scalar);
            List<Map<String, Object>> innerList = this.query(SQL_LIST_ERROR_TIME_PER_STEP, extra);
            PieChartModel chart = new PieChartModel();

            int[] raw = new int[4];
            for (Map<String, Object> map : innerList) {
                int key = (int)map.get("key");
                int value = (int)map.get("value");
                raw[key - 1] = value;
            }
            for (int i = 1; i <= 4; i++) {
                String key = WebUtil.getMessage(String.format("maintenanceAnalysis_timeChart_legend_%s", i));
                key = String.format(key, "" /*Integer.toString((int)(((double)value)/total*100))*/);
                key = key.replace("：", "");
                key = key.replace("%", "");
                chart.set(key, raw[i - 1]);
            }
            chart.setTitle(WebUtil.getFieldValueMessage("woSteps", Integer.toString(scalar)));
            chart.setLegendPosition("e");
            chart.setShowDataLabels(true);
            chart.setExtender("maintenanceE4" + Integer.toString(index + 1));
            charts.add(new AbstractMap.SimpleImmutableEntry<String, PieChartModel>("chart-for-step-" + scalar, chart));
        }
        while (index < 3) {
            charts.add(null);
            index++;
        }
        this.errorTimePerStep = charts;
    }

    private BarChartModel errorRoomChart;

    public final BarChartModel getErrorRoomChart() {
        return this.errorRoomChart;
    }

    private final void setErrorRoomChart() {
        List<Map<String, Object>> list = this.query(SQL_LIST_ERROR_ROOM_ALL);
        BarChartModel chart = new BarChartModel();
        ChartSeries series = new ChartSeries();
        for (Map<String, Object> map : list) {
            String key = (String)map.get("key");
            if (key == null || key.isEmpty()) {
                key = WebUtil.getMessage("maintenanceAnalysis_empty");
            }
            series.set(key, (Number)map.get("value"));
        }
        chart.addSeries(series);
        chart.getAxis(AxisType.X).setLabel(WebUtil.getMessage("maintenanceAnalysis_distributionChart_room_xAxis"));
        chart.getAxis(AxisType.Y).setLabel(WebUtil.getMessage("maintenanceAnalysis_distributionChart_yAxis"));
        chart.setExtender("maintenanceE51");
        this.errorRoomChart = chart;
    }

    private BarChartModel errorDeviceTypeChart;

    public final BarChartModel getErrorDeviceTypeChart() {
        return this.errorDeviceTypeChart;
    }

    private final void setErrorDeviceTypeChart() {
        List<Map<String, Object>> list = this.query(SQL_LIST_ERROR_DEVICE_TYPE_ALL);
        BarChartModel chart = new BarChartModel();
        ChartSeries series = new ChartSeries();
        for (Map<String, Object> map : list) {
            Integer id = (Integer)map.get("key");
            String key;
            if (id == null || id.intValue() == 0) {
                key = WebUtil.getMessage("maintenanceAnalysis_empty");
            }
            else if (id > 99) {
                key = WebUtil.getMessage("maintenanceAnalysis_otherCategory");
            }
            else {
                key = WebUtil.getFieldValueMessage("assetGroup", id.toString());
            }
            series.set(key, (Number)map.get("value"));
        }
        chart.addSeries(series);
        chart.getAxis(AxisType.X).setLabel(WebUtil.getMessage("maintenanceAnalysis_distributionChart_category_xAxis"));
        chart.getAxis(AxisType.Y).setLabel(WebUtil.getMessage("maintenanceAnalysis_distributionChart_yAxis"));
        chart.setExtender("maintenanceE52");
        this.errorDeviceTypeChart = chart;
    }

    private BarChartModel topErrorDeviceChart;

    public final BarChartModel getTopErrorDeviceChart() {
        return this.topErrorDeviceChart;
    }

    private final void setTopErrorDeviceChart() {
        BarChartModel chart = convertToBarChartModel(this.query(SQL_LIST_TOP_ERROR_DEVICE_ALL),
                WebUtil.getMessage("maintenanceAnalysis_distributionChart_device_xAxis"),
                WebUtil.getMessage("maintenanceAnalysis_distributionChart_yAxis"));
        chart.setExtender("maintenanceE53");
        this.topErrorDeviceChart = chart;
    }

    public final double getErrorPercentageInRoomOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Integer.valueOf(0))).doubleValue()
             / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_ROOM_OF_DEVICE_SINGLE), Integer.valueOf(0))).doubleValue();
    }

    public final boolean getErrorPercentageInRoomOfDeviceBoolean() {
        return !(Double.isInfinite(this.getErrorPercentageInRoomOfDevice()) || Double.isNaN(this.getErrorPercentageInRoomOfDevice()));
    }

    public final String getErrorPercentageInRoomOfDeviceString() {
        int value = (int)Math.round(this.getErrorPercentageInRoomOfDevice()*100);
        value = (value < 0 || value > 100) ? 0 : value;
        return String.format(WebUtil.getMessage("%s%%"),
                             Integer.toString(value));
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
        PieChartModel chart = convertToPieChartModel(this.getErrorPercentageInRoomOfDevice());
        chart.setExtender("maintenanceE62");
        return chart;
    }

    public final double getErrorPercentageInDeviceTypeOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Integer.valueOf(0))).doubleValue()
                / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE), Integer.valueOf(0))).doubleValue();
    }

    public final boolean getErrorPercentageInDeviceTypeOfDeviceBoolean() {
        return !(Double.isInfinite(this.getErrorPercentageInDeviceTypeOfDevice()) || Double.isNaN(this.getErrorPercentageInDeviceTypeOfDevice()));
    }

    public final String getErrorPercentageInDeviceTypeOfDeviceString() {
        int value = (int)Math.round(this.getErrorPercentageInDeviceTypeOfDevice()*100);
        value = (value < 0 || value > 100) ? 0 : value;
        return String.format(WebUtil.getMessage("%s%%"),
                Integer.toString(value));
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
        PieChartModel chart = convertToPieChartModel(this.getErrorPercentageInDeviceTypeOfDevice());
        chart.setExtender("maintenanceE64");
        return chart;
    }

    public final double getErrorPercentageInTotalOfDevice() {
        return (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_SINGLE), Integer.valueOf(0))).doubleValue()
                / (convertToScalar(this.query(SQL_SCALAR_ERROR_COUNT_IN_TOTAL_OF_DEVICE_SINGLE), Integer.valueOf(0))).doubleValue();
    }

    public final boolean getErrorPercentageInTotalOfDeviceBoolean() {
        return !(Double.isInfinite(this.getErrorPercentageInTotalOfDevice()) || Double.isNaN(this.getErrorPercentageInTotalOfDevice()));
    }

    public final String getErrorPercentageInTotalOfDeviceString() {
        int value = (int)Math.round(this.getErrorPercentageInTotalOfDevice()*100);
        value = (value < 0 || value > 100) ? 0 : value;
        return String.format(WebUtil.getMessage("%s%%"),
                Integer.toString(value));
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
        PieChartModel chart = convertToPieChartModel(this.getErrorPercentageInTotalOfDevice());
        chart.setExtender("maintenanceE66");
        return chart;
    }


    // endregion

    // region SQL

    private final List<Map<String, Object>> query(String template) {
        return this.query(template, null);
    }

    private final List<Map<String, Object>> query(String template, Map<String, Object> extra) {
        // TODO: replace may not be efficient
        template = StringUtils.replace(template, ":#andHospitalFilterForWorkOrder", "AND work.hospital_id = :#hospital_id");
        template = StringUtils.replace(template, ":#andHospitalFilterForAssetInfo", "AND asset.hospital_id = :#hospital_id");
        template = StringUtils.replace(template, ":#andDateFilter", "AND work.request_time BETWEEN :#startDate AND :#endDate");
        if (this.assetId == 0) {
            template = StringUtils.replace(template, ":#andDeviceFilterForWorkOrder", "");
            template = StringUtils.replace(template, ":#andDeviceFilterForAssetInfo", "");
        }
        else {
            template = StringUtils.replace(template, ":#andDeviceFilterForWorkOrder", "AND work.asset_id = :#assetId");
            template = StringUtils.replace(template, ":#andDeviceFilterForAssetInfo", "AND asset.id = :#assetId");
        }

        if (extra == null) {
            sqlParams.put("_sql", template);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);             
            return NativeSqlUtil.queryForList(template, sqlParams);
        }
        else {
            // TODO: better approach?
            Map<String, Object> temporary = new HashMap<String, Object>(sqlParams);
            temporary.putAll(extra);

            sqlParams.put("_sql", temporary);
            logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, temporary);  
            return NativeSqlUtil.queryForList(template, temporary);
        }
    }

    private final static String SQL_SCALAR_DEVICE_NAME_SINGLE = "" +
            "SELECT asset.name AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE asset.id = :#assetId " +
            ";";

    // 设备故障最主要原因

    private final static String SQL_SCALAR_TOP_ERROR_REASON = "" +
            "SELECT CASE " +
            "        WHEN work.case_type > 0 AND work.case_type <= :#knownCaseTypes THEN work.case_type " +
            "        WHEN work.case_type > :#knownCaseTypes THEN CAST(100 AS INTEGER) " +
            "        ELSE CAST(0 AS INTEGER) " +
            "END AS scalar " +
            "FROM work_order AS work " +
            "WHERE work.hospital_id = :#hospital_id" +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "GROUP BY scalar " +
            "ORDER BY count(*) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障处理流程最耗时步骤

    private final static String SQL_SCALAR_TOP_ERROR_STEP = "" +
            "WITH " + // same with step clause
            "cumulative AS ( " +
            "        SELECT work.id AS work_id, step.step_id AS step_id, sum(EXTRACT (epoch FROM (step.end_time - step.start_time)) / 60) AS minutes " +
            "        FROM work_order_step AS step, " +
            "             work_order AS work " +
            "        WHERE step.work_order_id = work.id " +
            "          AND step.step_id > 0 AND step.step_id <= :#knownWorkOrderSteps" +
            "          AND step.start_time IS NOT NULL " +
            "          AND step.end_time IS NOT NULL " +
//            "          AND work.is_closed = true " +
            "          AND work.hospital_id = :#hospital_id " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        :#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "        GROUP BY step.step_id, work.id " +
            ") " +
            "SELECT step_id AS scalar " +
            "FROM cumulative " +
            "GROUP BY scalar " +
            "ORDER BY avg(minutes) DESC " +
            "LIMIT 1 " +
            ";";

    // 设备故障主要发生的科室

    private final static String SQL_SCALAR_TOP_ERROR_ROOM_ALL = "" +
            "SELECT COALESCE(asset.clinical_dept_name, text(asset.clinical_dept_id)) AS scalar " +
            "FROM work_order AS work," +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND asset.hospital_id = :#hospital_id " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "GROUP BY scalar " +
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
            "  AND asset.hospital_id = :#hospital_id " +
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
            "  AND work.hospital_id = :#hospital_id " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ":#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            ";";

    // 设备故障原因分析

    private final static String SQL_LIST_ERROR_REASON = "" +
            "SELECT COALESCE(extended.key, partial.key) AS key, COALESCE(partial.value, CAST(0 AS INTEGER)) AS value " +
            "FROM generate_series(1, :#knownCaseTypes) AS extended(key) " +
            "     FULL OUTER JOIN (" +
            "        SELECT CASE " +
            "                WHEN work.case_type > 0 AND work.case_type <= :#knownCaseTypes THEN work.case_type " +
            "                WHEN work.case_type > :#knownCaseTypes THEN CAST(100 AS INTEGER) " +
            "                ELSE CAST(0 AS INTEGER) " +
            "        END AS key, count(*) AS value " +
            "        FROM work_order AS work " +
            "        WHERE work.hospital_id = :#hospital_id " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        :#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "        GROUP BY key " +
            "     ) AS partial " +
            "     ON extended.key = partial.key " +
            "ORDER BY key ASC " +
            ";";

    // 设备故障处理流程响应时间分布

    private final static String SQL_LIST_ERROR_STEP = "" +
            "WITH " + // same with step clause
            "cumulative AS ( " +
            "        SELECT work.id AS work_id, step.step_id AS step_id, sum(EXTRACT (epoch FROM (step.end_time - step.start_time)) / 60) AS minutes " +
            "        FROM work_order_step AS step, " +
            "             work_order AS work " +
            "        WHERE step.work_order_id = work.id " +
            "          AND step.step_id > 0 AND step.step_id <= :#knownWorkOrderSteps" +
            "          AND step.start_time IS NOT NULL " +
            "          AND step.end_time IS NOT NULL " +
 //           "          AND work.is_closed = true " +
            "          AND work.hospital_id = :#hospital_id " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        :#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "        GROUP BY step.step_id, work.id " +
            ") " +
            "SELECT step_id AS key, CAST(avg(minutes) AS INTEGER) AS value " +
            "FROM cumulative " +
            "GROUP BY key " +
            "ORDER BY key ASC " +
            ";";

    // （耗时最长的三个）步骤的具体响应时间分布

    private final static String SQL_LIST_TOP_ERROR_STEP = "" +
            "WITH " + // same with step clause
            "cumulative AS ( " +
            "        SELECT work.id AS work_id, step.step_id AS step_id, sum(EXTRACT (epoch FROM (step.end_time - step.start_time)) / 60) AS minutes " +
            "        FROM work_order_step AS step, " +
            "             work_order AS work " +
            "        WHERE step.work_order_id = work.id " +
            "          AND step.step_id > 0 AND step.step_id <= :#knownWorkOrderSteps" +
            "          AND step.start_time IS NOT NULL " +
            "          AND step.end_time IS NOT NULL " +
 //           "          AND work.is_closed = true " +
            "          AND work.hospital_id = :#hospital_id " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        :#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "        GROUP BY step.step_id, work.id " +
            ") " +
            "SELECT step_id AS scalar " +
            "FROM cumulative " +
            "GROUP BY scalar " +
            "ORDER BY avg(minutes) DESC " +
            ";";

    private final static String SQL_LIST_ERROR_TIME_PER_STEP = "" +
            "WITH " + // same with step clause
            "cumulative AS ( " +
            "        SELECT work.id AS work_id, step.step_id AS step_id, sum(EXTRACT (epoch FROM (step.end_time - step.start_time)) / 60) AS minutes " +
            "        FROM work_order_step AS step, " +
            "             work_order AS work " +
            "        WHERE step.work_order_id = work.id " +
            "          AND step.step_id = :#stepId " +
            "          AND step.start_time IS NOT NULL " +
            "          AND step.end_time IS NOT NULL " +
            "          AND work.hospital_id = :#hospital_id " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        :#andDeviceFilterForWorkOrder " +  // AND work.asset_id = :#assetId
            "        GROUP BY step.step_id, work.id " +
            ") " +
            "SELECT rate AS key, CAST(count(*) AS INTEGER) AS value " +
            "FROM ( " +
            "        SELECT CASE " +
            "                WHEN minutes BETWEEN 0 AND 30 THEN CAST (1 AS INTEGER) " + // 小于 30 分钟
            "                WHEN minutes BETWEEN 30 AND 60 THEN CAST (2 AS INTEGER) " + // 30 分钟到 1 小时
            "                WHEN minutes BETWEEN 60 AND 1440 THEN CAST (3 AS INTEGER) " + // 1 小时到 1 天
            "                ELSE CAST (4 AS INTEGER) " + // 1天以上
            "        END AS rate " +
            "        FROM cumulative " +
            ") AS temporary " +
            "GROUP BY rate " +
            "ORDER BY rate ASC " +
            ";";

    // 设备故障分布：按科室

    private final static String SQL_LIST_ERROR_ROOM_ALL = "" +
            "SELECT COALESCE(asset.clinical_dept_name, text(asset.clinical_dept_id)) AS key, count(*) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND work.hospital_id = :#hospital_id " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "GROUP BY key " +
            "ORDER BY key ASC " +
            ";";

    // 设备故障分布：按设备类型

    private final static String SQL_LIST_ERROR_DEVICE_TYPE_ALL = "" +
            "SELECT CASE " +
            "        WHEN asset.asset_group > 0 AND asset.asset_group <= :#knownAssetGroups THEN asset.asset_group " +
            "        WHEN asset.asset_group > :#knownAssetGroups THEN CAST(100 AS INTEGER) " +
            "        ELSE CAST (0 AS INTEGER) " +
            "END AS key, count(*) AS value " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND asset.hospital_id = :#hospital_id " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "GROUP BY key " +
            "ORDER BY key ASC " +
            ";";

    // 设备故障分布：按单台设备（前40台）

    private final static String SQL_LIST_TOP_ERROR_DEVICE_ALL = "" +
            "SELECT asset.name AS key, COALESCE(temporary.value, 0) AS value " +
            "FROM ( " +
            "        SELECT * " +
            "        FROM asset_info AS asset " +
            "        WHERE asset.hospital_id = :#hospital_id " +
            ") AS asset " +
            "LEFT OUTER JOIN ( " +
            "        SELECT asset.id AS key, CAST(count(*) AS INTEGER) AS value " +
            "        FROM work_order AS work, " +
            "             asset_info AS asset " +
            "        WHERE work.asset_id = asset.id " +
            "          AND asset.hospital_id = :#hospital_id" +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        GROUP BY asset.id " +
            ") AS temporary " +
            "ON asset.id = temporary.key " +
            "ORDER BY value DESC " +
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
            "              WHERE asset.hospital_id = :#hospital_id " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "      ) " +
            "  AND asset.hospital_id = :#hospital_id " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_ROOM_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE asset.clinical_dept_id = ( " +
            "              SELECT asset.clinical_dept_id " +
            "              FROM asset_info AS asset " +
            "              WHERE asset.hospital_id = :#hospital_id " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "      ) " +
            "  AND asset.hospital_id = :#hospital_id " +
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
            "                      WHERE asset.hospital_id = :#hospital_id " +
            "                      :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "              ) " +
            "          AND asset.hospital_id = :#hospital_id " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        GROUP BY work.asset_id " +
            ") AS temporary " +
            "WHERE key = :#assetId " +
            ";";

    private final static String SQL_SCALAR_ERROR_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND asset.asset_group = ( " +
            "              SELECT asset.asset_group " +
            "              FROM asset_info AS asset " +
            "              WHERE asset.hospital_id = :#hospital_id " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "      ) " +
            "  AND asset.hospital_id = :#hospital_id " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_DEVICE_TYPE_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE asset.asset_group = ( " +
            "              SELECT asset.asset_group " +
            "              FROM asset_info AS asset " +
            "              WHERE asset.hospital_id = :#hospital_id " +
            "              :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "      ) " +
            "  AND asset.hospital_id = :#hospital_id " +
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
            "                      WHERE asset.hospital_id = :#hospital_id " +
            "                      :#andDeviceFilterForAssetInfo " +  // AND asset.id = :#assetId
            "              ) " +
            "          AND asset.hospital_id = :#hospital_id " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        GROUP BY work.asset_id " +
            ") AS temporary " +
            "WHERE key = :#assetId " +
            ";";

    private final static String SQL_SCALAR_ERROR_COUNT_IN_TOTAL_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM work_order AS work, " +
            "     asset_info AS asset " +
            "WHERE work.asset_id = asset.id " +
            "  AND asset.hospital_id = :#hospital_id " +
            "  AND work.request_time BETWEEN :#startDate AND :#endDate " +
            ";";

    private final static String SQL_SCALAR_DEVICE_COUNT_IN_TOTAL_OF_DEVICE_SINGLE = "" +
            "SELECT CAST(count(*) AS INTEGER) AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE asset.hospital_id = :#hospital_id " +
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
            "          AND asset.hospital_id = :#hospital_id " +
            "          AND work.request_time BETWEEN :#startDate AND :#endDate " +
            "        GROUP BY work.asset_id " +
            ") AS temporary " +
            "WHERE key = :#assetId " +
            ";";

    // endregion

    // region Chart

    @SuppressWarnings("unchecked")
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
        if (primary < 0.02) {
            primary = 0.02;
        }
        chart.set(WebUtil.getMessage("maintenanceAnalysis_this"), primary);
        if (primary < 1) {
            chart.set(WebUtil.getMessage("maintenanceAnalysis_that"), 1 - primary);
        }
        return chart;
    }

    // endregion
}