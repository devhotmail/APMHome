package com.ge.apm.view.analysis;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.MoreObjects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.math.Stats;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.primefaces.model.chart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class DeviceOperationMonitorController extends SqlConfigurableChartController {
    private static final Logger log = LoggerFactory.getLogger(DeviceOperationMonitorController.class);
    public final static String HEAD = "头部";
    public final static String CHEST = "胸部";
    public final static String ABDOMEN = "腹部";
    public final static String LIMBS = "四肢";
    public final static String OTHER = "其他";
    public final static String TIME = "时间";
    public final static String NUMBER = "检查次数";
    private final static ImmutableMap<Integer, String> parts = ImmutableMap.of(1, HEAD, 2, CHEST, 3, ABDOMEN, 4, LIMBS, 5, OTHER);
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final Map<String, String> queries;
    private final Map<String, Object> parameters;
    private final JdbcTemplate jdbcTemplate;
    private final int siteId;
    private final int hospitalId;
    private BarChartModel topBar;
    private ImmutableMap<String, Integer> topBarData;
    private LineChartModel tabAreaModel;
    private ImmutableTable<String, String, Integer> tabAreaModelData;
    private BarChartModel bottomLeftBar;
    private BarChartModel bottomRightBar;
    private Map<Integer, String> assetGroups = ImmutableMap.of();
    private int selectedGroupId = 1;
    private int totalExamCount = 0;
    private Date startDate = null;
    private Date endDate = null;
    private int activeTabIndex = 2;

    public DeviceOperationMonitorController() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        siteId = user.getSiteId();
        hospitalId = user.getHospitalId();
        queries = Maps.newLinkedHashMap();
        parameters = Maps.newLinkedHashMap();
        jdbcTemplate = WebUtil.getServiceBean("jdbcTemplate", JdbcTemplate.class);
        queries.put("assetGroups", "select msg_key as assetGroupId, value_zh as assetGroupName from i18n_message where msg_type = ?");
        queries.put("examsPerProcedure", "select ac.procedure_id, ac.procedure_name, count(*) from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? and ac.exam_date between ? and ? group by ac.procedure_name, ac.procedure_id order by ac.procedure_id");
        queries.put("examsPerProcedurePerMonth", "select a.ts_month as exam_time,a.part_id as exam_id, COALESCE(b.exam_count, 0) as exam_count from (select * from  (select generate_series(date_trunc('month',to_date(?,'yyyy-MM-dd')), date_trunc('month',to_date(?,'yyyy-MM-dd')), '1 months') as ts_month) as ts  cross join (select generate_series(1,5) as part_id) as pi order by ts_month, part_id) as a left join (select date_trunc('month', ac.exam_date) as exam_month, ac.procedure_id as procedure_id, ac.procedure_name as procedure_name, count(*) as exam_count from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id  where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? and ac.exam_date between ? and ? group by exam_month, ac.procedure_id, ac.procedure_name order by exam_month, procedure_id) as b on a.ts_month = b.exam_month and a.part_id = b.procedure_id");
        queries.put("examsPerProcedurePerWeek", "select a.ts_week as exam_time,a.part_id as exam_id, COALESCE(b.exam_count, 0) as exam_count from (select * from  (select generate_series(date_trunc('week',to_date(?,'yyyy-MM-dd')), date_trunc('week',to_date(?,'yyyy-MM-dd')), '1 weeks') as ts_week) as ts  cross join (select generate_series(1,5) as part_id) as pi order by ts_week, part_id) as a left join (select date_trunc('week', ac.exam_date) as exam_week, ac.procedure_id as procedure_id, ac.procedure_name as procedure_name, count(*) as exam_count from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id  where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? and ac.exam_date between ? and ? group by exam_week, ac.procedure_id, ac.procedure_name order by exam_week, procedure_id) as b on a.ts_week = b.exam_week and a.part_id = b.procedure_id");
        queries.put("examsPerProcedurePerDay", "select a.ts_day as exam_time,a.part_id as exam_id, COALESCE(b.exam_count, 0) as exam_count from (select * from  (select generate_series(date_trunc('day',to_date(?,'yyyy-MM-dd')), date_trunc('day',to_date(?,'yyyy-MM-dd')), '1 days') as ts_day) as ts  cross join (select generate_series(1,5) as part_id) as pi order by ts_day, part_id) as a left join (select date_trunc('day', ac.exam_date) as exam_day, ac.procedure_id as procedure_id, ac.procedure_name as procedure_name, count(*) as exam_count from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id  where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? and ac.exam_date between ? and ? group by exam_day, ac.procedure_id, ac.procedure_name order by exam_day, procedure_id) as b on a.ts_day = b.exam_day and a.part_id = b.procedure_id");
    }

    @PostConstruct
    public void init() {
        parameters.put("hospitalId", hospitalId);
        assetGroups = initAssetGroups();
        selectedGroupId = FluentIterable.from(assetGroups.keySet()).first().or(1);
        initStartEndDate();
        initTopBar();
        initTabView();
        initBottomView();
    }

    public void renderFormBySelectGroup(int groupId) {
        selectedGroupId = groupId;
        initTopBar();
        initTabView();
        initBottomView();
    }

    public void renderFormBySelectDate() {
        if (checkEndDate(startDate, endDate)) {
            initTopBar();
            initTabView();
            initBottomView();
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "提示", "选择范围控制最小1个月，最大1年");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public void renderTabView(int tabIndex) {
        String query = "";
        if (tabIndex == 0) {
            query = queries.get("examsPerProcedurePerDay");
        } else if (tabIndex == 1) {
            query = queries.get("examsPerProcedurePerWeek");
        } else if (tabIndex == 2) {
            query = queries.get("examsPerProcedurePerMonth");
        }
        List<DeviceOperationInfo> exams = jdbcTemplate.query(query, new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo info = new DeviceOperationInfo();
                info.setProcedureName(parts.get(rs.getInt("exam_id")));
                info.setExamCount(rs.getInt("exam_count"));
                info.setExamDate(rs.getDate("exam_time"));
                return info;
            }
        }, from(startDate), from(endDate), siteId, hospitalId, selectedGroupId, startDate, endDate);

        activeTabIndex = tabIndex;
        tabAreaModelData = initTable(exams);
        log.info("tabAreaModelData: {}", tabAreaModelData);
        tabAreaModel = initLineCharModel(null, true, "ne", "tabAreaSkin", tabIndex == 2 ? "%y-%m" : null, tabAreaModelData);
        bottomLeftBar = initBarModel(new BarChartModel(), null, true, 50, "ne", "bottomLeftBarSkin", ImmutableMap.of(HEAD, average(tabAreaModelData.row(HEAD)), CHEST, average(tabAreaModelData.row(CHEST)), ABDOMEN, average(tabAreaModelData.row(ABDOMEN)), LIMBS, average(tabAreaModelData.row(LIMBS)), OTHER, average(tabAreaModelData.row(OTHER))));
    }


    private boolean checkEndDate(Date startDate, Date endDate) {
        DateTime start = new DateTime(startDate);
        Interval interval = new Interval(start.plusMonths(1), start.plusYears(1));
        return interval.contains(new DateTime(endDate));
    }

    private Map<Integer, String> initAssetGroups() {
        List<DeviceOperationInfo> list = jdbcTemplate.query(queries.get("assetGroups"), new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo deviceOperationInfo = new DeviceOperationInfo();
                deviceOperationInfo.setGroupId(rs.getInt("assetGroupId"));
                deviceOperationInfo.setGroupName(rs.getString("assetGroupName"));
                return deviceOperationInfo;
            }
        }, "assetGroup");
        Map<Integer, String> assetGroups = Maps.newHashMapWithExpectedSize(list.size());
        for (DeviceOperationInfo info : list) {
            assetGroups.put(info.getGroupId(), info.getGroupName());
        }
        return assetGroups;
    }


    private String initSelectedGroup(int groupId) {
        return assetGroups.get(groupId);
    }

    private void initTopBar() {
        List<DeviceOperationInfo> list = jdbcTemplate.query(queries.get("examsPerProcedure"), new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo deviceOperationInfo = new DeviceOperationInfo();
                deviceOperationInfo.setProcedureId(rs.getInt("procedure_id"));
                deviceOperationInfo.setProcedureName(rs.getString("procedure_name"));
                deviceOperationInfo.setExamCount(rs.getInt("count"));
                return deviceOperationInfo;
            }
        }, siteId, hospitalId, selectedGroupId, startDate, endDate);
        ImmutableMap.Builder<String, Integer> reportBuilder = new ImmutableMap.Builder<>();
        for (DeviceOperationInfo deviceOperationInfo : list) {
            reportBuilder.put(deviceOperationInfo.getProcedureName(), deviceOperationInfo.getExamCount());
        }
        topBarData = reportBuilder.build();
        totalExamCount = (int) Stats.of(topBarData.values()).sum();
        topBar = initBarModel(new HorizontalBarChartModel(), null, true, 0, "ne", "topBarSkin", reportBuilder.build());
    }

    private void initStartEndDate() {
        startDate = DateTime.now().minusYears(1).plusDays(1).toDate();
        endDate = DateTime.now().toDate();
    }

    private void initTabView() {
        renderTabView(2);
    }

    private int average(ImmutableMap<String, Integer> series) {
        return (int) Stats.of(series.values()).sum() / series.size();
    }

    private String seriesStart(ImmutableMap<String, Integer> series) {
        return FluentIterable.from(series.keySet()).first().get();
    }

    private String seriesEnd(ImmutableMap<String, Integer> series) {
        return FluentIterable.from(series.keySet()).last().get();
    }

    private void initBottomView() {
        bottomLeftBar = initBarModel(new BarChartModel(), null, true, 50, "ne", "bottomLeftBarSkin", ImmutableMap.of(HEAD, average(tabAreaModelData.row(HEAD)), CHEST, average(tabAreaModelData.row(CHEST)), ABDOMEN, average(tabAreaModelData.row(ABDOMEN)), LIMBS, average(tabAreaModelData.row(LIMBS)), OTHER, average(tabAreaModelData.row(OTHER))));
        bottomRightBar = initBarModel(new BarChartModel(), null, true, 50, "ne", "bottomRightBarSkin", topBarData);
    }


    private <T extends ChartSeries> T initChartSeries(T chartSeries, String label, boolean fill, ImmutableMap<String, Integer> pairs) {
        for (Map.Entry<String, Integer> entry : pairs.entrySet()) {
            chartSeries.set(entry.getKey(), entry.getValue());
        }
        if (chartSeries instanceof LineChartSeries) {
            ((LineChartSeries) chartSeries).setFill(fill);
        }
        chartSeries.setLabel(label);
        return chartSeries;
    }

    private ImmutableTable<String, String, Integer> initTable(List<DeviceOperationInfo> list) {
        ImmutableTable.Builder<String, String, Integer> table = new ImmutableTable.Builder<>();
        for (DeviceOperationInfo info : list) {
            table.put(info.getProcedureName(), info.getExamDate().toString(), info.getExamCount());
        }
        return table.build();
    }

    private LineChartModel initLineCharModel(String title, boolean stacked, String legendPosition, String skin, String tickFormat, ImmutableTable<String, String, Integer> table) {
        LineChartModel areaModel = new LineChartModel();
        areaModel.setTitle(title);
        areaModel.setStacked(stacked);
        areaModel.setLegendPosition(legendPosition);
        areaModel.addSeries(initChartSeries(new LineChartSeries(), OTHER, true, table.row(OTHER)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), LIMBS, true, table.row(LIMBS)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), ABDOMEN, true, table.row(ABDOMEN)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), CHEST, true, table.row(CHEST)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), HEAD, true, table.row(HEAD)));
        DateAxis xAxis = new DateAxis(TIME);
        xAxis.setMin(seriesStart(table.row(HEAD)));
        xAxis.setMax(seriesEnd(table.row(HEAD)));
        xAxis.setTickFormat(tickFormat);
        Axis yAxis = areaModel.getAxis(AxisType.Y);
        yAxis.setLabel(NUMBER);
        yAxis.setMin(0);

        areaModel.getAxes().put(AxisType.X, xAxis);

        return areaModel;
    }

    private <T extends BarChartModel> T initBarModel(T barChartModel, String title, boolean stacked, int barWidth, String legendPostion, String skin, Map<String, Integer> renderData) {
        barChartModel.setTitle(title);
        barChartModel.setStacked(stacked);
        barChartModel.setBarWidth(barWidth);
        barChartModel.setLegendPosition(legendPostion);
        barChartModel.setExtender(skin);
        barChartModel.setShowDatatip(false);
        barChartModel.setShadow(false);
        barChartModel.addSeries(initChartSeries(new ChartSeries(), OTHER, false, ImmutableMap.of(OTHER, renderData.get(OTHER))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), LIMBS, false, ImmutableMap.of(LIMBS, renderData.get(LIMBS))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), ABDOMEN, false, ImmutableMap.of(ABDOMEN, renderData.get(ABDOMEN))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), CHEST, false, ImmutableMap.of(CHEST, renderData.get(CHEST))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), HEAD, false, ImmutableMap.of(HEAD, renderData.get(HEAD))));
        Axis xAxis = barChartModel.getAxis(AxisType.X);
        Axis yAxis = barChartModel.getAxis(AxisType.Y);
        if (barChartModel instanceof HorizontalBarChartModel) {
            xAxis.setMin(0);
            xAxis.setMax((int) Stats.of(renderData.values()).sum());
        } else {
            yAxis.setMin(0);
            yAxis.setMax((int) Stats.of(renderData.values()).sum());
        }
        return barChartModel;
    }

    private String from(Date date) {
        return new DateTime(date).toString(formatter);
    }

    private DateTime from(String date) {
        return formatter.parseDateTime(date);
    }

    public int getSiteId() {
        return siteId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public BarChartModel getTopBar() {
        return topBar;
    }

    public LineChartModel getTabAreaModel() {
        return tabAreaModel;
    }

    public BarChartModel getBottomRightBar() {
        return bottomRightBar;
    }

    public BarChartModel getBottomLeftBar() {
        return bottomLeftBar;
    }

    public Map<Integer, String> getAssetGroups() {
        return assetGroups;
    }

    public int getSelectedGroupId() {
        return selectedGroupId;
    }

    public int getTotalExamCount() {
        return totalExamCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getActiveTabIndex() {
        return activeTabIndex;
    }

    public static class DeviceOperationInfo {
        private int groupId;
        private String groupName;
        private int procedureId;
        private String procedureName;
        private int examCount;
        private Date examDate;

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(DeviceOperationInfo.class)
                    .omitNullValues()
                    .add("groupId", groupId)
                    .add("groupName", groupName)
                    .add("procedureId", procedureId)
                    .add("procedureName", procedureName)
                    .add("examCount", examCount)
                    .add("examDate", examDate).toString();
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public int getProcedureId() {
            return procedureId;
        }

        public void setProcedureId(int procedureId) {
            this.procedureId = procedureId;
        }

        public String getProcedureName() {
            return procedureName;
        }

        public void setProcedureName(String procedureName) {
            this.procedureName = procedureName;
        }

        public int getExamCount() {
            return examCount;
        }

        public void setExamCount(int examCount) {
            this.examCount = examCount;
        }

        public Date getExamDate() {
            return examDate;
        }

        public void setExamDate(Date examDate) {
            this.examDate = examDate;
        }
    }
}
