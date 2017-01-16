package com.ge.apm.view.analysis;

import com.ge.apm.domain.I18nMessage;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.google.common.math.Stats;
import com.google.common.primitives.Ints;
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
import javax.servlet.http.HttpServletRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@ManagedBean
@ViewScoped
public class DeviceOperationMonitorController extends SqlConfigurableChartController {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(DeviceOperationMonitorController.class);
    
    private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    
    public final static String HEAD = "头部";
    public final static String CHEST = "胸部";
    public final static String ABDOMEN = "腹部";
    public final static String LIMBS = "四肢";
    public final static String OTHER = "其他";
    public final static String NUMBER = "检查次数";
    public final static String DAY_AVERAGE = "日平均总数";
    public final static String WEEK_AVERAGE = "周平均总数";
    public final static String MONTH_AVERAGE = "月平均总数";
    public final static String OVERALL = "选定时间总数";
    private final static ImmutableMap<Integer, String> parts = ImmutableMap.of(1, HEAD, 2, CHEST, 3, ABDOMEN, 4, LIMBS, 5, OTHER);
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final Map<String, String> queries;
    private final JdbcTemplate jdbcTemplate;

    private BarChartModel topBar;
    private ImmutableMap<String, Integer> topBarData;
    private BarChartModel topBarAll;
    private ImmutableMap<String, Integer> topBarAllData;
    private LineChartModel tabAreaModel;
    private ImmutableTable<String, String, Integer> tabAreaModelData;
    private BarChartModel middleBar;
    private ImmutableTable<String, String, Integer> middleBarData;
    private String bottomLeftBarTitle = "";
    private int bottomLeftTotal = 0;
    private BarChartModel bottomLeftBar;
    private String bottomRightBarTitle = "";
    private int bottomRightTotal = 0;
    private BarChartModel bottomRightBar;
    private List<Report> bottomBars;
    private Map<Integer, String> assetGroups;
    private int selectedGroupId;
    private int totalExamCount = 0;
    private int totalExamAllCount = 0;
    private Date startDate = null;
    private Date endDate = null;
    private int activeTabIndex = 2;

    public DeviceOperationMonitorController() {

        queries = Maps.newLinkedHashMap();
        jdbcTemplate = WebUtil.getServiceBean("jdbcTemplate", JdbcTemplate.class);
        queries.put("assetGroups", "select msg_key as assetGroupId, value_zh as assetGroupName from i18n_message where msg_type = ?");
        queries.put("examsPerProcedure", "select a.part_id as exam_id, COALESCE(b.count, 0) as exam_count from (select generate_series(1,5) as part_id) as a left join (select ac.procedure_id, ac.procedure_name, count(*) from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? and ac.exam_date between ? and ? group by ac.procedure_name, ac.procedure_id order by ac.procedure_id) as b on a.part_id = b.procedure_id");
        queries.put("examsPerProcedurePerMonth", "select a.ts_month as exam_time,a.part_id as exam_id, COALESCE(b.exam_count, 0) as exam_count from (select * from  (select generate_series(date_trunc('month',to_date(?,'yyyy-MM-dd')), date_trunc('month',to_date(?,'yyyy-MM-dd')), '1 months') as ts_month) as ts  cross join (select generate_series(1,5) as part_id) as pi order by ts_month, part_id) as a left join (select date_trunc('month', ac.exam_date) as exam_month, ac.procedure_id as procedure_id, ac.procedure_name as procedure_name, count(*) as exam_count from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id  where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? and ac.exam_date between ? and ? group by exam_month, ac.procedure_id, ac.procedure_name order by exam_month, procedure_id) as b on a.ts_month = b.exam_month and a.part_id = b.procedure_id");
        queries.put("examsPerProcedurePerWeek", "select a.ts_week as exam_time,a.part_id as exam_id, COALESCE(b.exam_count, 0) as exam_count from (select * from  (select generate_series(date_trunc('week',to_date(?,'yyyy-MM-dd')), date_trunc('week',to_date(?,'yyyy-MM-dd')), '1 weeks') as ts_week) as ts  cross join (select generate_series(1,5) as part_id) as pi order by ts_week, part_id) as a left join (select date_trunc('week', ac.exam_date) as exam_week, ac.procedure_id as procedure_id, ac.procedure_name as procedure_name, count(*) as exam_count from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id  where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? and ac.exam_date between ? and ? group by exam_week, ac.procedure_id, ac.procedure_name order by exam_week, procedure_id) as b on a.ts_week = b.exam_week and a.part_id = b.procedure_id");
        queries.put("examsPerProcedurePerDay", "select a.ts_day as exam_time,a.part_id as exam_id, COALESCE(b.exam_count, 0) as exam_count from (select * from  (select generate_series(date_trunc('day',to_date(?,'yyyy-MM-dd')), date_trunc('day',to_date(?,'yyyy-MM-dd')), '1 days') as ts_day) as ts  cross join (select generate_series(1,5) as part_id) as pi order by ts_day, part_id) as a left join (select date_trunc('day', ac.exam_date) as exam_day, ac.procedure_id as procedure_id, ac.procedure_name as procedure_name, count(*) as exam_count from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id  where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? and ac.exam_date between ? and ? group by exam_day, ac.procedure_id, ac.procedure_name order by exam_day, procedure_id) as b on a.ts_day = b.exam_day and a.part_id = b.procedure_id");
        queries.put("examsPerProcedureAll", "select a.part_id as exam_id, COALESCE(b.count, 0) as exam_count from (select generate_series(1,5) as part_id) as a left join (select ac.procedure_id, ac.procedure_name, count(*) from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id where ai.site_id = ? and ai.hospital_id = ? and ac.exam_date between ? and ? group by ac.procedure_name, ac.procedure_id order by ac.procedure_id) as b on a.part_id = b.procedure_id");
        queries.put("examsForMiddleBar", "select dm.asset_name as asset_name, dm.exam_id as exam_id, COALESCE(af.exam_count, 0) as exam_count from (select nm.name as asset_name, pid.part_id as exam_id from (select ai.name from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id where ai.site_id = ? and ai.hospital_id = ? and ac.exam_date between ? and ? group by ai.name) as nm cross join (select generate_series(1,5) as part_id) as pid) as dm left join (select ai.name, ac.procedure_id, count(*) as exam_count from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id where ai.site_id = ? and ai.hospital_id = ? and ac.exam_date between ? and ? group by ai.name,ac.procedure_id) as af on dm.asset_name = af.name and dm.exam_id = af.procedure_id");
        queries.put("examsForGroups", "select dm.asset_group as group_id, dm.procedure_id as exam_id, COALESCE(af.exam_count,0) as exam_count from (select * from (select generate_series(1,?) as asset_group) as ag cross join (select generate_series(1,5) as procedure_id) as pi) as dm left join (select ai.asset_group, ac.procedure_id, count(*) as exam_count from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id where ai.site_id = ? and ai.hospital_id = ? and ac.exam_date between ? and ? group by ai.asset_group,ac.procedure_id order by ai.asset_group,ac.procedure_id) as af on dm.asset_group = af.asset_group and dm.procedure_id = af.procedure_id");
    }

    @PostConstruct
    public void init() {
        int parmSelectGroupId = Integer.valueOf(Optional.fromNullable(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedGroupId")).or("0"));
        assetGroups = initAssetGroups();
        selectedGroupId = parmSelectGroupId == 0 ? FluentIterable.from(assetGroups.keySet()).first().or(1) : parmSelectGroupId;
        initStartEndDate();
        initTopBar();
        initTopBarAll();
        initTabView();
        initMiddleBar();
        initBottomView();
        initBottomBars();
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
            initTopBarAll();
            initTabView();
            initMiddleBar();
            initBottomView();
            initBottomBars();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "提示", "选择范围控制最小1个月，最大1年"));
        }

    }

    public void renderTabView(int tabIndex) {
        String query = "";
        if (tabIndex == 0) {
            query = queries.get("examsPerProcedurePerDay");
            bottomLeftBarTitle = DAY_AVERAGE;
        } else if (tabIndex == 1) {
            query = queries.get("examsPerProcedurePerWeek");
            bottomLeftBarTitle = WEEK_AVERAGE;
        } else if (tabIndex == 2) {
            query = queries.get("examsPerProcedurePerMonth");
            bottomLeftBarTitle = MONTH_AVERAGE;
        }
        
        String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, selectedGroupId=%s, startDate=%s, endDate=%s}",
        		query, site_id, hospital_id, selectedGroupId, startDate, endDate);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        List<DeviceOperationInfo> exams = jdbcTemplate.query(query, new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo info = new DeviceOperationInfo();
                info.setProcedureName(parts.get(rs.getInt("exam_id")));
                info.setExamCount(rs.getInt("exam_count"));
                info.setExamDate(rs.getDate("exam_time"));
                return info;
            }
        }, from(startDate), from(endDate), site_id, hospital_id, selectedGroupId, startDate, endDate);

        activeTabIndex = tabIndex;
        tabAreaModelData = initTable(exams);
        tabAreaModel = initLineCharModel(null, true, "ne", "tabAreaSkin", tabIndex == 2 ? "%y-%m" : null, tabAreaModelData);
        ImmutableMap<String, Integer> bottomLeftBarData = ImmutableMap.of(HEAD, average(tabAreaModelData.row(HEAD)), CHEST, average(tabAreaModelData.row(CHEST)), ABDOMEN, average(tabAreaModelData.row(ABDOMEN)), LIMBS, average(tabAreaModelData.row(LIMBS)), OTHER, average(tabAreaModelData.row(OTHER)));
        bottomLeftTotal = (int) Stats.of(bottomLeftBarData.values()).sum();
        bottomLeftBar = initBarModel(new BarChartModel(), bottomLeftBarTitle, true, 50, "ne", "bottomLeftBarSkin", bottomLeftBarData, true);
    }

    public void initMiddleBar() {
        String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, selectedGroupId=%s, startDate=%s, endDate=%s}",
        		queries.get("examsForMiddleBar"), site_id, hospital_id, selectedGroupId, startDate, endDate);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        List<DeviceOperationInfo> exams = jdbcTemplate.query(queries.get("examsForMiddleBar"), new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo info = new DeviceOperationInfo();
                info.setAssetName(rs.getString("asset_name"));
                info.setProcedureName(parts.get(rs.getInt("exam_id")));
                info.setExamCount(rs.getInt("exam_count"));
                return info;
            }
        }, site_id, hospital_id, startDate, endDate, site_id, hospital_id, startDate, endDate);
        ImmutableTable.Builder<String, String, Integer> builder = new ImmutableTable.Builder<>();
        for (DeviceOperationInfo info : exams) {
            builder.put(info.getProcedureName(), info.getAssetName(), info.getExamCount());
        }
        middleBarData = builder.build();
        middleBar = initBarModel(new BarChartModel(), null, true, "ne", "middleBarSkin", middleBarData, true);
    }

    public void initBottomBars() {
        
        bottomBars = Lists.newArrayListWithExpectedSize(assetGroups.size());
        String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, selectedGroupId=%s, startDate=%s, endDate=%s}",
        		queries.get("examsForGroups"), site_id, hospital_id, selectedGroupId, startDate, endDate);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        List<DeviceOperationInfo> exams = jdbcTemplate.query(queries.get("examsForGroups"), new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo deviceOperationInfo = new DeviceOperationInfo();
                deviceOperationInfo.setGroupName(assetGroups.get(rs.getInt("group_id")));
                deviceOperationInfo.setProcedureName(parts.get(rs.getInt("exam_id")));
                deviceOperationInfo.setExamCount(rs.getInt("exam_count"));
                return deviceOperationInfo;
            }
        }, assetGroups.size(), site_id, hospital_id, startDate, endDate);
        ImmutableTable.Builder<String, String, Integer> builder = new ImmutableTable.Builder<>();
        for (DeviceOperationInfo info : exams) {
            if (Optional.fromNullable(info.getGroupName()).isPresent() && Optional.fromNullable(info.getProcedureName()).isPresent() && Optional.fromNullable(info.getExamCount()).isPresent()) {
                builder.put(info.getGroupName(), info.getProcedureName(), info.getExamCount());
            }
        }
        ImmutableTable<String, String, Integer> table = builder.build();
        for (String group : table.rowKeySet()) {
            ImmutableMap<String, Integer> renderData = table.row(group);
            BarChartModel chart = initBarModel(new BarChartModel(), group, true, 50, "ne", "bottomBarSkin", renderData, true);
            bottomBars.add(new Report(group, (int) Stats.of(renderData.values()).sum(), chart, renderData));
        }
    }


    private boolean checkEndDate(Date startDate, Date endDate) {
        DateTime start = new DateTime(startDate);
        Interval interval = new Interval(start.plusMonths(1).minusDays(1), start.plusYears(1).plusDays(1));
        return interval.contains(new DateTime(endDate));
    }

    private Map<Integer, String> initAssetGroups() {
        FieldValueMessageController msgController = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
        TreeMap<Integer, String> assetGroup = Maps.newTreeMap(Ordering.natural());
        for (I18nMessage msg : msgController.getFieldValueList("assetGroup")) {
            if (Optional.fromNullable(Ints.tryParse(msg.getMsgKey())).or(0) >= 1 && Optional.fromNullable(Ints.tryParse(msg.getMsgKey())).or(0) <= 12) {
                assetGroup.put(Integer.parseInt(msg.getMsgKey()), msg.getValue());
            }

        }
        return assetGroup;
    }

    private void initTopBar() {
        String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, selectedGroupId=%s, startDate=%s, endDate=%s}",
        		queries.get("examsPerProcedure"), site_id, hospital_id, selectedGroupId, startDate, endDate);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        List<DeviceOperationInfo> list = jdbcTemplate.query(queries.get("examsPerProcedure"), new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo deviceOperationInfo = new DeviceOperationInfo();
                deviceOperationInfo.setProcedureName(parts.get(rs.getInt("exam_id")));
                deviceOperationInfo.setExamCount(rs.getInt("exam_count"));
                return deviceOperationInfo;
            }
        }, site_id, hospital_id, selectedGroupId, startDate, endDate);
        ImmutableMap.Builder<String, Integer> reportBuilder = new ImmutableMap.Builder<>();
        for (DeviceOperationInfo deviceOperationInfo : list) {
            reportBuilder.put(deviceOperationInfo.getProcedureName(), deviceOperationInfo.getExamCount());
        }
        topBarData = reportBuilder.build();
        totalExamCount = (int) Stats.of(topBarData.values()).sum();
        topBar = initBarModel(new HorizontalBarChartModel(), null, true, 0, "ne", "topBarSkin", topBarData, false);
    }

    private void initTopBarAll() {
        String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, selectedGroupId=%s, startDate=%s, endDate=%s}",
        		queries.get("examsPerProcedureAll"), site_id, hospital_id, selectedGroupId, startDate, endDate);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        List<DeviceOperationInfo> list = jdbcTemplate.query(queries.get("examsPerProcedureAll"), new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo deviceOperationInfo = new DeviceOperationInfo();
                deviceOperationInfo.setProcedureName(parts.get(rs.getInt("exam_id")));
                deviceOperationInfo.setExamCount(rs.getInt("exam_count"));
                return deviceOperationInfo;
            }
        }, site_id, hospital_id, startDate, endDate);
        ImmutableMap.Builder<String, Integer> reportBuilder = new ImmutableMap.Builder<>();
        for (DeviceOperationInfo deviceOperationInfo : list) {
            reportBuilder.put(deviceOperationInfo.getProcedureName(), deviceOperationInfo.getExamCount());
        }
        topBarAllData = reportBuilder.build();
        totalExamAllCount = (int) Stats.of(topBarAllData.values()).sum();
        topBarAll = initBarModel(new HorizontalBarChartModel(), null, true, 0, "ne", "topBarAllSkin", topBarAllData, false);
    }

    private void initStartEndDate() {
        startDate = DateTime.now().minusYears(1).toDate();
        endDate = DateTime.now().toDate();
    }

    private void initTabView() {
        renderTabView(2);
    }

    private int average(ImmutableMap<String, Integer> series) {
        int sum = (int) Stats.of(series.values()).sum();
        return sum / series.size();
    }

    private String seriesStart(ImmutableMap<String, Integer> series) {
        return FluentIterable.from(series.keySet()).first().get();
    }

    private String seriesEnd(ImmutableMap<String, Integer> series) {
        return FluentIterable.from(series.keySet()).last().get();
    }

    private void initBottomView() {
        bottomRightBarTitle = OVERALL;
        bottomRightTotal = totalExamCount;
        bottomRightBar = initBarModel(new BarChartModel(), bottomRightBarTitle, true, 50, "ne", "bottomRightBarSkin", topBarData, true);
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
            table.put(info.getProcedureName(), from(info.getExamDate()), Optional.of(info.getExamCount()).or(0));
        }
        return table.build();
    }

    private LineChartModel initLineCharModel(String title, boolean stacked, String legendPosition, String skin, String tickFormat, ImmutableTable<String, String, Integer> table) {
        LineChartModel areaModel = new LineChartModel();
        areaModel.setTitle(title);
        areaModel.setStacked(stacked);
        areaModel.setLegendPosition(legendPosition);
        areaModel.setExtender(skin);
        areaModel.addSeries(initChartSeries(new LineChartSeries(), OTHER, true, table.row(OTHER)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), LIMBS, true, table.row(LIMBS)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), ABDOMEN, true, table.row(ABDOMEN)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), CHEST, true, table.row(CHEST)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), HEAD, true, table.row(HEAD)));
        DateAxis xAxis = new DateAxis();
        xAxis.setMin(seriesStart(table.row(HEAD)));
        xAxis.setMax(seriesEnd(table.row(HEAD)));
        if (Optional.fromNullable(tickFormat).isPresent()) {
            xAxis.setTickFormat(tickFormat);
            xAxis.setTickCount(table.row(HEAD).size());
        }
        Axis yAxis = areaModel.getAxis(AxisType.Y);
        yAxis.setLabel(NUMBER);
        yAxis.setMin(0);

        areaModel.getAxes().put(AxisType.X, xAxis);

        return areaModel;
    }

    private <T extends BarChartModel> T initBarModel(T barChartModel, String title, boolean stacked, int barWidth, String legendPostion, String skin, Map<String, Integer> renderData, boolean seriesReversed) {
        barChartModel.setTitle(title);
        barChartModel.setStacked(stacked);
        barChartModel.setBarWidth(barWidth);
        barChartModel.setLegendPosition(legendPostion);
        barChartModel.setExtender(skin);
        barChartModel.setShowDatatip(false);
        barChartModel.setShadow(false);
        for (Map.Entry<Integer, String> entry : ImmutableSortedMap.copyOf(parts, seriesReversed ? Ordering.natural().reverse() : Ordering.natural()).entrySet()) {
            barChartModel.addSeries(initChartSeries(new ChartSeries(), entry.getValue(), false, ImmutableMap.of(entry.getValue(), Optional.of(renderData.get(entry.getValue())).or(0))));
        }
        return barChartModel;
    }

    private <T extends BarChartModel> T initBarModel(T barChartModel, String title, boolean stacked, String legendPosition, String skin, ImmutableTable<String, String, Integer> table, boolean seriesReversed) {
        barChartModel.setTitle(title);
        barChartModel.setStacked(stacked);
        barChartModel.setLegendPosition(legendPosition);
        barChartModel.setExtender(skin);
        barChartModel.setShowDatatip(false);
        barChartModel.setShadow(false);
        for (Map.Entry<Integer, String> entry : ImmutableSortedMap.copyOf(parts, seriesReversed ? Ordering.natural().reverse() : Ordering.natural()).entrySet()) {
            barChartModel.addSeries(initChartSeries(new ChartSeries(), entry.getValue(), false, table.row(entry.getValue())));
        }
        Axis xAxis = barChartModel.getAxis(AxisType.X);
        Axis yAxis = barChartModel.getAxis(AxisType.Y);
        xAxis.setTickAngle(-90);
        yAxis.setMin(0);

        return barChartModel;
    }

    private String from(Date date) {
        return new DateTime(date).toString(formatter);
    }

    public BarChartModel getTopBar() {
        return topBar;
    }

    public LineChartModel getTabAreaModel() {
        return tabAreaModel;
    }

    public BarChartModel getMiddleBar() {
        return middleBar;
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

    public List<Report> getBottomBars() {
        return bottomBars;
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

    public BarChartModel getTopBarAll() {
        return topBarAll;
    }

    public int getTotalExamAllCount() {
        return totalExamAllCount;
    }

    public String getBottomLeftBarTitle() {
        return bottomLeftBarTitle;
    }

    public int getBottomLeftTotal() {
        return bottomLeftTotal;
    }

    public String getBottomRightBarTitle() {
        return bottomRightBarTitle;
    }

    public int getBottomRightTotal() {
        return bottomRightTotal;
    }

    public static class Report {
        private String group;
        private int total = 0;
        private BarChartModel chart;
        private ImmutableMap<String, Integer> renderData;

        public Report(String group, int total, BarChartModel chart, ImmutableMap<String, Integer> renderData) {
            this.group = group;
            this.total = total;
            this.chart = chart;
            this.renderData = renderData;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public BarChartModel getChart() {
            return chart;
        }

        public void setChart(BarChartModel chart) {
            this.chart = chart;
        }

        public ImmutableMap<String, Integer> getRenderData() {
            return renderData;
        }

        public void setRenderData(ImmutableMap<String, Integer> renderData) {
            this.renderData = renderData;
        }

        @Override
        public String toString() {
            return "Report{" +
                    "group='" + group + '\'' +
                    ", total=" + total +
                    ", chart=" + chart +
                    ", renderData=" + renderData +
                    '}';
        }
    }

    public static class DeviceOperationInfo {
        private String assetName;
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
                    .add("assetName", assetName)
                    .add("groupId", groupId)
                    .add("groupName", groupName)
                    .add("procedureId", procedureId)
                    .add("procedureName", procedureName)
                    .add("examCount", examCount)
                    .add("examDate", examDate).toString();
        }

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
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
