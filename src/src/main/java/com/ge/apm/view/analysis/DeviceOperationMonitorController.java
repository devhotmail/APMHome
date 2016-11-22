package com.ge.apm.view.analysis;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.*;
import com.google.common.math.Stats;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
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
import java.util.Random;

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
    private final Map<String, String> queries;
    private final Map<String, Object> parameters;
    private final JdbcTemplate jdbcTemplate;
    private final int siteId;
    private final int hospitalId;
    private BarChartModel headerBarModel;
    private LineChartModel tabAreaModel;
    private BarChartModel tabBarModel;
    private BarChartModel selectedBarModel;
    private Map<Integer, String> assetGroups = ImmutableMap.of();
    private int selectedGroupId = 1;
    private int totalExamCount = 0;
    private Date startDate = null;
    private Date endDate = null;

    public DeviceOperationMonitorController() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        siteId = user.getSiteId();
        hospitalId = user.getHospitalId();
        queries = Maps.newLinkedHashMap();
        parameters = Maps.newLinkedHashMap();
        jdbcTemplate = WebUtil.getServiceBean("jdbcTemplate", JdbcTemplate.class);
        queries.put("assetGroups", "select msg_key as assetGroupId, value_zh as assetGroupName from i18n_message where msg_type = ?");
        queries.put("examsPerProcedure", "select ac.procedure_id, ac.procedure_name, count(*) from asset_info ai join asset_clinical_record ac on ai.id = ac.asset_id where ai.site_id = ? and ai.hospital_id = ? and ai.asset_group = ? group by ac.procedure_name, ac.procedure_id order by ac.procedure_id");
    }

    @PostConstruct
    public void init() {
        parameters.put("hospitalId", hospitalId);
        tabAreaModel = initLineCharModel(null, true, "ne", "tabAreaSkin", initTable());
        tabBarModel = initBarModel(new BarChartModel(), null, true, 50, "ne", "tabBarSkin", ImmutableMap.of(HEAD, 800, CHEST, 900, ABDOMEN, 2700, LIMBS, 900, OTHER, 900));
        selectedBarModel = initBarModel(new BarChartModel(), null, true, 50, "ne", "selectedBarSkin", ImmutableMap.of(HEAD, 800, CHEST, 900, ABDOMEN, 2700, LIMBS, 900, OTHER, 900));
        assetGroups = initAssetGroups();
        selectedGroupId = FluentIterable.from(assetGroups.keySet()).first().or(1);
        initExamsPerProcedure();
    }

    public void renderFormBySelectGroup(int groupId) {
        selectedGroupId = groupId;
        initExamsPerProcedure();
    }

    public void renderFormBySelectDate() {
        if(checkEndDate(startDate,endDate)){
            System.out.println("correct");
        }else{
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "提示", "选择范围控制最小1个月，最大1年");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public int getSiteId() {
        return siteId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public BarChartModel getHeaderBarModel() {
        return headerBarModel;
    }

    public LineChartModel getTabAreaModel() {
        return tabAreaModel;
    }

    public BarChartModel getTabBarModel() {
        return tabBarModel;
    }

    public BarChartModel getSelectedBarModel() {
        return selectedBarModel;
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

    private void initExamsPerProcedure() {
        List<DeviceOperationInfo> reports = jdbcTemplate.query(queries.get("examsPerProcedure"), new RowMapper<DeviceOperationInfo>() {
            @Override
            public DeviceOperationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceOperationInfo deviceOperationInfo = new DeviceOperationInfo();
                deviceOperationInfo.setProcedureId(rs.getInt("procedure_id"));
                deviceOperationInfo.setProcedureName(rs.getString("procedure_name"));
                deviceOperationInfo.setExamCount(rs.getInt("count"));
                return deviceOperationInfo;
            }
        }, siteId, hospitalId, selectedGroupId);
        ImmutableMap.Builder<String, Integer> reportBuilder = new ImmutableMap.Builder<>();
        for (DeviceOperationInfo deviceOperationInfo : reports) {
            reportBuilder.put(deviceOperationInfo.getProcedureName(), deviceOperationInfo.getExamCount());
        }
        totalExamCount = (int) Stats.of(reportBuilder.build().values()).sum();
        headerBarModel = initBarModel(new HorizontalBarChartModel(), null, true, 0, "ne", "skinHeaderBar", reportBuilder.build());
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

    private LineChartModel initLineCharModel(String title, boolean stacked, String legendPosition, String skin, ImmutableTable<String, String, Integer> table) {
        LineChartModel areaModel = new LineChartModel();
        areaModel.setTitle(title);
        areaModel.setStacked(stacked);
        areaModel.setExtender(skin);
        areaModel.setLegendPosition(legendPosition);
        areaModel.addSeries(initChartSeries(new LineChartSeries(), HEAD, true, table.row(HEAD)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), CHEST, true, table.row(CHEST)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), CHEST, true, table.row(ABDOMEN)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), LIMBS, true, table.row(LIMBS)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), OTHER, true, table.row(OTHER)));
        Axis xAxis = areaModel.getAxis(AxisType.X);
        Axis yAxis = areaModel.getAxis(AxisType.Y);
        xAxis.setLabel(TIME);
        yAxis.setLabel(NUMBER);

        return areaModel;
    }

    private ImmutableTable<String, String, Integer> initTable() {
        ImmutableTable.Builder<String, String, Integer> table = new ImmutableTable.Builder<>();
        Random random = new Random();
        for (String part : ImmutableList.of(HEAD, CHEST, ABDOMEN, LIMBS, OTHER)) {
            for (int i = 1; i <= 12; i++)
                table.put(part, String.valueOf(i), random.nextInt(1000) + 500);
        }
        return table.build();
    }


    private <T extends BarChartModel> T initBarModel(T barChartModel, String title, boolean stacked, int barWidth, String legendPostion, String skin, Map<String, Integer> renderData) {
        barChartModel.setTitle(title);
        barChartModel.setStacked(stacked);
        barChartModel.setBarWidth(barWidth);
        barChartModel.setLegendPosition(legendPostion);
        barChartModel.setExtender(skin);
        barChartModel.setShowDatatip(false);
        barChartModel.setShadow(false);
        barChartModel.addSeries(initChartSeries(new ChartSeries(), HEAD, false, ImmutableMap.of(HEAD, renderData.get(HEAD))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), CHEST, false, ImmutableMap.of(CHEST, renderData.get(CHEST))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), ABDOMEN, false, ImmutableMap.of(ABDOMEN, renderData.get(ABDOMEN))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), LIMBS, false, ImmutableMap.of(LIMBS, renderData.get(LIMBS))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), OTHER, false, ImmutableMap.of(OTHER, renderData.get(OTHER))));
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


    public static class DeviceOperationInfo {
        private int groupId;
        private String groupName;
        private int procedureId;
        private String procedureName;
        private int examCount;
        private LocalDate examDate;

        @Override
        public String toString() {
            return "DeviceOperationInfo{" +
                    "groupId='" + groupId + '\'' +
                    ", groupName='" + groupName + '\'' +
                    ", procedureId='" + procedureId + '\'' +
                    ", procedureName='" + procedureName + '\'' +
                    ", examCount=" + examCount +
                    ", examDate=" + examDate +
                    '}';
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

        public LocalDate getExamDate() {
            return examDate;
        }

        public void setExamDate(LocalDate examDate) {
            this.examDate = examDate;
        }
    }
}
