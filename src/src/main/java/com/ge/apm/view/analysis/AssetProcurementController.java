package com.ge.apm.view.analysis;

import com.ge.apm.domain.I18nMessage;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.google.common.math.Stats;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.primefaces.model.chart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Math.ceil;


@ManagedBean
@ViewScoped
public class AssetProcurementController {
	private static final Logger logger = LoggerFactory.getLogger(AssetProcurementController.class);
    
    private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    
    private final static ImmutableMap<Integer, String> parts = ImmutableMap.of(1, "头部", 2, "胸部", 3, "腹部", 4, "四肢", 5, "其他");
    private final static int DAILY_UTIL_HOURS = 8;
    private final static int DAILY_UTIL_BENCHMARK = DAILY_UTIL_HOURS * 60 * 60;
    private final Map<String, String> queries;
    private final JdbcTemplate jdbcTemplate;
    private final Date lastSndYearStart;
    private final Date lastSndYearEnd;
    private final Date lastFstYearStart;
    private final Date lastFstYearEnd;
    private final Date forecastStart;
    private final Date forecastEnd;
    private ImmutableMap<Integer, String> assetGroups;
    private Map<Integer, Detail> details;


    public AssetProcurementController() {

        jdbcTemplate = WebUtil.getServiceBean("jdbcTemplate", JdbcTemplate.class);
        queries = Maps.newLinkedHashMap();
        queries.put("utilization", "select asi.asset_group, asi.name as asset_name, asi.install_date, asi.part_id, COALESCE(acr.exam_count, 0) as exam_count, COALESCE(acr.duration, 0) as exam_duration, COALESCE(acr.amount, 0) as exam_charge from (select ai.site_id, ai.hospital_id, ai.id, ai.name, ai.asset_group, ai.install_date, pid.part_id from asset_info ai cross join (select generate_series(1,5) as part_id) as pid where ai.site_id = ? and ai.hospital_id = ? order by ai.asset_group, ai.name, pid.part_id) as asi left join (select asset_id, procedure_id, count(procedure_id) as exam_count, sum(exam_duration) as duration, sum(price_amount) as amount from asset_clinical_record where exam_date between ? and ? group by asset_id, procedure_id, procedure_name) as acr on asi.id = acr.asset_id and asi.part_id = acr.procedure_id order by asi.asset_group, asi.name, asi.part_id");
        forecastStart = DateTime.now().plusDays(1).toDate();
        forecastEnd = DateTime.now().plusYears(1).toDate();
        lastFstYearEnd = DateTime.now().minusDays(1).toDate();
        lastFstYearStart = DateTime.now().minusDays(1).minusYears(1).toDate();
        lastSndYearEnd = DateTime.now().minusDays(1).minusYears(1).minusDays(1).toDate();
        lastSndYearStart = DateTime.now().minusDays(1).minusYears(1).minusDays(1).minusYears(1).toDate();
    }

    @PostConstruct
    public void init() {
        assetGroups = initAssetGroups();
        details = Maps.newHashMapWithExpectedSize(assetGroups.size());
        initLastFstYearReport();
        initLastSndYearReport();
        initForecastReport();
        initSuggestion();
        initIncome();
    }

    private void initLastFstYearReport() {
        final Table<Integer, String, LinkedHashMap<Integer, Report>> table = HashBasedTable.create();
        String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, lastFstYearStart=%s, lastFstYearEnd=%s}", 
        		queries.get("utilization"), site_id, hospital_id, lastFstYearStart, lastFstYearEnd);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        jdbcTemplate.query(queries.get("utilization"), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Report report = new Report(rs.getInt("asset_group"), rs.getString("asset_name"), rs.getDate("install_date"), rs.getInt("part_id"), rs.getInt("exam_count"), rs.getInt("exam_duration"), rs.getDouble("exam_charge"));
                LinkedHashMap<Integer, Report> map = Optional.fromNullable(table.get(report.getGroup(), report.getName())).or(new LinkedHashMap<Integer, Report>());
                map.put(report.getExamPart(), report);
                table.put(report.getGroup(), report.getName(), map);
            }
        }, site_id, hospital_id, lastFstYearStart, lastFstYearEnd);
        
        for (int group : table.rowKeySet()) {
            if (Optional.fromNullable(assetGroups.get(group)).isPresent()) {
                Detail detail = Optional.fromNullable(details.get(group)).or(new Detail());
                detail.setGroupId(group);
                detail.setGroupName(assetGroups.get(group));
                detail.setLastFstYearIncome(calcAnnualIncome(table.row(group)));
                ImmutableTable.Builder<String, String, Integer> builder = new ImmutableTable.Builder<>();
                for (String assetName : table.columnKeySet()) {
                    LinkedHashMap<Integer, Report> map = table.get(group, assetName);
                    if (map != null) {
                        for (Map.Entry<Integer, String> entry : parts.entrySet()) {
                            Report report = map.get(entry.getKey());
                            builder.put(entry.getValue(), assetName, calcLastFstYearUtilPartPercent(report));
                        }
                    }
                }
                detail.setLastFstYearChartData(builder.build());
                //detail.setLastFstYearChart(initBarModel(new BarChartModel(), String.format("%s Last First Year BarChart", detail.getGroupName()), true, "ne", String.format("js%sLstFstYearBarChartSkin", detail.getGroupName()), ImmutableTable.copyOf(detail.getLastFstYearChartData()), true));
                detail.setLastFstYearChart(initBarModel(new BarChartModel(), String.format("%s Last First Year BarChart", detail.getGroupName()), true, "ne", "detailSkin", ImmutableTable.copyOf(detail.getLastFstYearChartData()), true));
                detail.setLastFstYearAvgUtilPercent((int) (Stats.of(detail.getLastFstYearChartData().values()).sum() / (double) detail.getLastFstYearChartData().columnKeySet().size()));
                details.put(detail.getGroupId(), detail);
            }
        }
    }

    private void initLastSndYearReport() {
        final Table<Integer, String, LinkedHashMap<Integer, Report>> table = HashBasedTable.create();
        String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, lastFstYearStart=%s, lastFstYearEnd=%s}", 
        		queries.get("utilization"), site_id, hospital_id, lastFstYearStart, lastFstYearEnd);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        jdbcTemplate.query(queries.get("utilization"), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Report report = new Report(rs.getInt("asset_group"), rs.getString("asset_name"), rs.getDate("install_date"), rs.getInt("part_id"), rs.getInt("exam_count"), rs.getInt("exam_duration"), rs.getDouble("exam_charge"));
                LinkedHashMap<Integer, Report> map = Optional.fromNullable(table.get(report.getGroup(), report.getName())).or(new LinkedHashMap<Integer, Report>());
                map.put(report.getExamPart(), report);
                table.put(report.getGroup(), report.getName(), map);
            }
        }, site_id, hospital_id, lastSndYearStart, lastSndYearEnd);
        
        for (int group : table.rowKeySet()) {
            if (Optional.fromNullable(assetGroups.get(group)).isPresent()) {
                Detail detail = Optional.fromNullable(details.get(group)).or(new Detail());
                detail.setGroupId(group);
                detail.setGroupName(assetGroups.get(group));
                detail.setLastSndYearIncome(calcAnnualIncome(table.row(group)));
                ImmutableTable.Builder<String, String, Integer> builder = new ImmutableTable.Builder<>();
                for (String assetName : table.columnKeySet()) {
                    LinkedHashMap<Integer, Report> map = table.get(group, assetName);
                    if (map != null) {
                        for (Map.Entry<Integer, String> entry : parts.entrySet()) {
                            Report report = map.get(entry.getKey());
                            builder.put(entry.getValue(), assetName, calcLastSndYearUtilsPartPercent(report));
                        }
                    }
                }
                detail.setLastSndYearChartData(builder.build());
                //detail.setLastSndYearChart(initBarModel(new BarChartModel(),String.format("%s Last Second Year BarChart", detail.getGroupName()), true, "ne", String.format("js%sLstSndYearBarChartSkin", detail.getGroupName()), ImmutableTable.copyOf(detail.getLastSndYearChartData()), true));
                detail.setLastSndYearChart(initBarModel(new BarChartModel(), String.format("%s Last Second Year BarChart", detail.getGroupName()), true, "ne", "detailSkin", ImmutableTable.copyOf(detail.getLastSndYearChartData()), true));
                detail.setLastSndYearAvgUtilPercent((int) (Stats.of(detail.getLastSndYearChartData().values()).sum() / (double) detail.getLastSndYearChartData().columnKeySet().size()));
                details.put(detail.getGroupId(), detail);
            }
        }
    }

    private void initForecastReport() {
        for (Detail detail : details.values()) {
            Table<String, String, Integer> lastSndYearChartData = detail.getLastSndYearChartData();
            Table<String, String, Integer> lastFstYearChartData = detail.getLastFstYearChartData();
            ImmutableTable.Builder<String, String, Integer> builder = new ImmutableTable.Builder<>();
            for (String part : lastFstYearChartData.rowKeySet()) {
                for (String assetName : lastFstYearChartData.columnKeySet()) {
                    builder.put(part, assetName, (int) predictNext((double) lastSndYearChartData.get(part, assetName), (double) lastFstYearChartData.get(part, assetName)));
                }
            }
            detail.setForecastChartData(builder.build());
            detail.setForecastChart(initBarModel(new BarChartModel(), String.format("%s Forecast BarChart", detail.getGroupName()), true, "ne", "detailSkin", ImmutableTable.copyOf(detail.getForecastChartData()), true));
            detail.setForecastBriefChart(initBarModel(new BarChartModel(), String.format("%s Forecast Brief BarChart", detail.getGroupName()), true, "ne", "briefSkin", ImmutableTable.copyOf(detail.getForecastChartData()), true));
            detail.setForecastAvgUtilPercent((int) (Stats.of(detail.getForecastChartData().values()).sum() / (double) detail.getForecastChartData().columnKeySet().size()));
            detail.setAssetUtilForecasts(ImmutableMap.copyOf(Maps.transformValues(detail.getForecastChartData().columnMap(), new Function<Map<String, Integer>, Integer>() {
                @Override
                public Integer apply(Map<String, Integer> input) {
                    return (int) Stats.of(input.values()).sum();
                }
            })));
            details.put(detail.getGroupId(), detail);
        }
    }

    private void initSuggestion() {
        for (Detail detail : details.values()) {
            int numOfMatchedAssets = numOfAssetsUtilExceedsThreshold(detail);
            detail.setForecastResult(String.format("%s台设备使用率>100%%", numOfMatchedAssets > 0 ? numOfMatchedAssets : "无单"));
            if (numOfMatchedAssets > 0 && detail.getForecastAvgUtilPercent() > 100) {
                detail.setNumPurchase((int) (ceil(Stats.of(detail.getAssetUtilForecasts().values()).sum() / 100d) - (double) detail.getAssetUtilForecasts().keySet().size()));
                detail.setSuggestion(String.format("建议购买%s台新设备", detail.getNumPurchase()));
                detail.setForecastUtilAfterAction((int) (Stats.of(detail.getAssetUtilForecasts().values()).sum() / (double) (detail.getAssetUtilForecasts().keySet().size() + detail.getNumPurchase())));
            } else if (numOfMatchedAssets > 0 && detail.getForecastAvgUtilPercent() <= 100) {
                detail.setNumPurchase(0);
                detail.setNumImprove(numOfMatchedAssets);
                detail.setSuggestion("建议安排合理化");
            } else {
                detail.setNumPurchase(0);
                detail.setNumImprove(0);
                detail.setSuggestion("无");
            }
        }
    }

    private void initIncome() {
        for (Detail detail : details.values()) {
            if (detail.getNumPurchase() > 0) {
                detail.setForecastIncomeNoneAction(detail.lastFstYearAvgUtilPercent == 0 ? 0d : detail.lastFstYearAvgUtilPercent > 100 ? detail.getLastFstYearIncome() : (detail.lastFstYearIncome / (detail.lastFstYearAvgUtilPercent / 100d)));
                detail.setForecastIncomeAfterAction(detail.lastFstYearAvgUtilPercent == 0 ? 0d : detail.lastFstYearAvgUtilPercent > 100 ? detail.getLastFstYearIncome() * (detail.forecastAvgUtilPercent / 100d) : (detail.lastFstYearIncome / (detail.lastFstYearAvgUtilPercent / 100d)) * (detail.forecastAvgUtilPercent / 100d));
                detail.setForecastIncreaseAfterAction(detail.forecastIncomeAfterAction - detail.forecastIncomeNoneAction);
            } else {
                detail.setForecastIncomeNoneAction(detail.lastFstYearAvgUtilPercent == 0 ? 0d : detail.lastFstYearAvgUtilPercent > 100 ? detail.getLastFstYearIncome() : (detail.lastFstYearIncome / (detail.lastFstYearAvgUtilPercent / 100d)));
                detail.setForecastIncomeAfterAction(detail.lastFstYearAvgUtilPercent == 0 ? 0d : detail.lastFstYearAvgUtilPercent > 100 ? detail.getLastFstYearIncome() : (detail.lastFstYearIncome / (detail.lastFstYearAvgUtilPercent / 100d)));
                detail.setForecastIncreaseAfterAction(0d);
            }
        }
    }

    private int numOfAssetsUtilExceedsThreshold(Detail detail) {
        Table<String, String, Integer> forecast = detail.getForecastChartData();
        Objects.requireNonNull(forecast);
        return FluentIterable.from(Maps.transformValues(forecast.columnMap(), new Function<Map<String, Integer>, Integer>() {
            @Override
            public Integer apply(Map<String, Integer> input) {
                return (int) Stats.of(input.values()).sum();
            }
        }).values()).filter(new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input > 100;
            }
        }).size();
    }

    private Double calcAnnualIncome(Map<String, LinkedHashMap<Integer, Report>> assetReports) {
        Double sum = 0D;
        for (LinkedHashMap<Integer, Report> map : assetReports.values()) {
            for (Report report : map.values()) {
                sum = sum + report.getCharge();
            }
        }
        return sum;
    }

    private int calcLastSndYearUtilsPartPercent(Report report) {
        Objects.requireNonNull(report);
        if (new DateTime(report.getInstalled()).isBefore(new DateTime(lastSndYearStart))) {
            long days = new Interval(new DateTime(lastSndYearStart), new DateTime(lastSndYearEnd)).toDuration().getStandardDays();
            return (int) (((double) report.getExamSeconds()) / ((double) (days * DAILY_UTIL_BENCHMARK)) * 100D);
        } else if (new Interval(new DateTime(lastSndYearStart), new DateTime(lastSndYearEnd)).contains(new DateTime(report.getInstalled()))) {
            long days = new Interval(new DateTime(report.getInstalled()), new DateTime(lastSndYearEnd)).toDuration().getStandardDays();
            return (int) (((double) report.getExamSeconds()) / ((double) (days * DAILY_UTIL_BENCHMARK)) * 100D);
        } else {
            return 0;
        }
    }

    private int calcLastFstYearUtilPartPercent(Report report) {
        Objects.requireNonNull(report);
        if (new DateTime(report.getInstalled()).isBefore(new DateTime(lastFstYearStart))) {
            long days = new Interval(new DateTime(lastFstYearStart), new DateTime(lastFstYearEnd)).toDuration().getStandardDays();
            return (int) (((double) report.getExamSeconds()) / ((double) (days * DAILY_UTIL_BENCHMARK)) * 100D);
        } else if (new Interval(new DateTime(lastFstYearStart), new DateTime(lastFstYearEnd)).contains(new DateTime(report.getInstalled()))) {
            long days = new Interval(new DateTime(report.getInstalled()), new DateTime(lastFstYearEnd)).toDuration().getStandardDays();
            return (int) (((double) report.getExamSeconds()) / ((double) (days * DAILY_UTIL_BENCHMARK)) * 100D);
        } else {
            return 0;
        }
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
        //yAxis.setMax(150);

        return barChartModel;
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

    private ImmutableMap<Integer, String> initAssetGroups() {
        FieldValueMessageController msgController = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
        ImmutableMap.Builder<Integer, String> builder = new ImmutableMap.Builder<>();
        for (I18nMessage msg : msgController.getFieldValueList("assetGroup")) {
            builder.put(Integer.parseInt(msg.getMsgKey()), msg.getValue());
        }

        return builder.build();
    }

    private static double predictNext(double lastSnd, double lastFst) {
        if (lastSnd > 0d && lastFst > 0d) {
            return lastFst + Stats.of(lastSnd, lastFst).sampleStandardDeviation();
        } else if (lastSnd <= 0d && lastFst > 0d) {
            return lastFst;
        } else if (lastSnd > 0d && lastFst <= 0d) {
            return lastSnd;
        } else {
            return 0d;
        }
    }

    private static String from(Date date) {
        return from(date, "yyyy.MM");
    }

    private static String from(Date date, String format) {
        return new DateTime(date).toString(DateTimeFormat.forPattern(format));
    }

    public String getLastSndYearStart() {
        return from(lastSndYearStart);
    }

    public String getLastSndYearEnd() {
        return from(lastSndYearEnd);
    }

    public String getLastFstYearStart() {
        return from(lastFstYearStart);
    }

    public String getLastFstYearEnd() {
        return from(lastFstYearEnd);
    }

    public String getForecastStart() {
        return from(forecastStart);
    }

    public String getForecastEnd() {
        return from(forecastEnd);
    }

    public Detail[] getDetails() {
        return details.values().toArray(new Detail[0]);
    }

    public int getDetailsSize() {
        return getDetails().length;
    }

    public Detail detail(int i) {
        return getDetails()[i];
    }

    public int getRowSize() {
        return 6;
    }

    public int getNumPurchase() {
        return (int) Stats.of(FluentIterable.from(getDetails()).transform(new Function<Detail, Integer>() {
            @Override
            public Integer apply(Detail input) {
                return input.getNumPurchase();
            }
        })).sum();
    }

    public int getNumImprove() {
        return (int) Stats.of(FluentIterable.from(getDetails()).transform(new Function<Detail, Integer>() {
            @Override
            public Integer apply(Detail input) {
                return input.getNumImprove();
            }
        })).sum();
    }

    public int getNumIncrease() {
        return (int) Stats.of(FluentIterable.from(getDetails()).transform(new Function<Detail, Integer>() {
            @Override
            public Integer apply(Detail input) {
                return input.getForecastIncreaseAfterAction();
            }
        })).sum();
    }

    public int getDailyUtilHours() {
        return DAILY_UTIL_HOURS;
    }

    private static class Report {
        private int group;
        private String name;
        private Date installed;
        private int examPart;
        private int examCount;
        private int examSeconds;
        private double charge;

        public Report(int group, String name, Date installed, int examPart, int examCount, int examSeconds, double charge) {
            this.group = group;
            this.name = name;
            this.installed = installed;
            this.examPart = examPart;
            this.examCount = examCount;
            this.examSeconds = examSeconds;
            this.charge = charge;
        }

        public int getGroup() {
            return group;
        }

        public String getName() {
            return name;
        }

        public Date getInstalled() {
            return installed;
        }

        public int getExamPart() {
            return examPart;
        }

        public int getExamSeconds() {
            return examSeconds;
        }

        public double getCharge() {
            return charge;
        }

        @Override
        public String toString() {
            return "Report{" +
                    "group=" + group +
                    ", name='" + name + '\'' +
                    ", installed=" + installed +
                    ", examPart=" + examPart +
                    ", examCount=" + examCount +
                    ", examSeconds=" + examSeconds +
                    ", charge=" + charge +
                    '}';
        }
    }

    public static class Detail implements Comparable<Detail> {
        private int groupId;
        private String groupName;
        private int lastSndYearAvgUtilPercent;
        private BarChartModel lastSndYearChart;
        private Table<String, String, Integer> lastSndYearChartData;
        private int lastFstYearAvgUtilPercent;
        private BarChartModel lastFstYearChart;
        private Table<String, String, Integer> lastFstYearChartData;
        private int forecastAvgUtilPercent;
        private BarChartModel forecastChart;
        private BarChartModel forecastBriefChart;
        private Table<String, String, Integer> forecastChartData;
        private String forecastResult;
        private String suggestion;
        private int numPurchase;
        private int numImprove;
        private ImmutableMap<String, Integer> assetUtilForecasts;
        private int forecastUtilAfterAction;
        private Double lastFstYearIncome;
        private Double lastSndYearIncome;
        private Double forecastIncomeNoneAction;
        private Double forecastIncomeAfterAction;
        private Double forecastIncreaseAfterAction;

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


        public int getLastSndYearAvgUtilPercent() {
            return lastSndYearAvgUtilPercent;
        }

        public void setLastSndYearAvgUtilPercent(int lastSndYearAvgUtilPercent) {
            this.lastSndYearAvgUtilPercent = lastSndYearAvgUtilPercent;
        }

        public BarChartModel getLastSndYearChart() {
            return lastSndYearChart;
        }

        public Table<String, String, Integer> getLastSndYearChartData() {
            return lastSndYearChartData;
        }

        public void setLastSndYearChartData(Table<String, String, Integer> lastSndYearChartData) {
            this.lastSndYearChartData = lastSndYearChartData;
        }

        public void setLastSndYearChart(BarChartModel lastSndYearChart) {
            this.lastSndYearChart = lastSndYearChart;
        }

        public int getLastFstYearAvgUtilPercent() {
            return lastFstYearAvgUtilPercent;
        }

        public void setLastFstYearAvgUtilPercent(int lastFstYearAvgUtilPercent) {
            this.lastFstYearAvgUtilPercent = lastFstYearAvgUtilPercent;
        }

        public BarChartModel getLastFstYearChart() {
            return lastFstYearChart;
        }

        public void setLastFstYearChart(BarChartModel lastFstYearChart) {
            this.lastFstYearChart = lastFstYearChart;
        }

        public Table<String, String, Integer> getLastFstYearChartData() {
            return lastFstYearChartData;
        }

        public void setLastFstYearChartData(Table<String, String, Integer> lastFstYearChartData) {
            this.lastFstYearChartData = lastFstYearChartData;
        }

        public int getForecastAvgUtilPercent() {
            Objects.requireNonNull(forecastChartData);
            return (int) (Stats.of(forecastChartData.values()).sum() / (double) forecastChartData.columnKeySet().size());
        }

        public void setForecastAvgUtilPercent(int forecastAvgUtilPercent) {
            this.forecastAvgUtilPercent = forecastAvgUtilPercent;
        }

        public BarChartModel getForecastChart() {
            return forecastChart;
        }

        public void setForecastChart(BarChartModel forecastChart) {
            this.forecastChart = forecastChart;
        }

        public BarChartModel getForecastBriefChart() {
            return forecastBriefChart;
        }

        public void setForecastBriefChart(BarChartModel forecastBriefChart) {
            this.forecastBriefChart = forecastBriefChart;
        }

        public Table<String, String, Integer> getForecastChartData() {
            return forecastChartData;
        }

        public void setForecastChartData(Table<String, String, Integer> forecastChartData) {
            this.forecastChartData = forecastChartData;
        }

        public String getForecastResult() {
            return forecastResult;
        }

        public void setForecastResult(String forecastResult) {
            this.forecastResult = forecastResult;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }

        public int getNumPurchase() {
            return numPurchase;
        }

        public void setNumPurchase(int numPurchase) {
            this.numPurchase = numPurchase;
        }

        public int getNumImprove() {
            return numImprove;
        }

        public void setNumImprove(int numImprove) {
            this.numImprove = numImprove;
        }

        public ImmutableMap<String, Integer> getAssetUtilForecasts() {
            Objects.requireNonNull(forecastChartData);
            return ImmutableMap.copyOf(Maps.transformValues(forecastChartData.columnMap(), new Function<Map<String, Integer>, Integer>() {
                @Override
                public Integer apply(Map<String, Integer> input) {
                    return (int) Stats.of(input.values()).sum();
                }
            }));
        }

        public void setAssetUtilForecasts(ImmutableMap<String, Integer> assetUtilForecasts) {
            this.assetUtilForecasts = assetUtilForecasts;
        }

        public int getForecastUtilAfterAction() {
            return this.forecastUtilAfterAction;
        }

        public void setForecastUtilAfterAction(int forecastUtilAfterAction) {
            this.forecastUtilAfterAction = forecastUtilAfterAction;
        }

        public Double getLastFstYearIncome() {
            return lastFstYearIncome;
        }

        public void setLastFstYearIncome(Double lastFstYearIncome) {
            this.lastFstYearIncome = lastFstYearIncome;
        }

        public Double getLastSndYearIncome() {
            return lastSndYearIncome;
        }

        public void setLastSndYearIncome(Double lastSndYearIncome) {
            this.lastSndYearIncome = lastSndYearIncome;
        }

        public int getForecastIncomeNoneAction() {
            return (int) ceil(forecastIncomeNoneAction / 10000d);
        }

        public void setForecastIncomeNoneAction(Double forecastIncomeNoneAction) {
            this.forecastIncomeNoneAction = forecastIncomeNoneAction;
        }

        public int getForecastIncomeAfterAction() {
            return (int) ceil(forecastIncomeAfterAction / 10000d);
        }

        public void setForecastIncomeAfterAction(Double forecastIncomeAfterAction) {
            this.forecastIncomeAfterAction = forecastIncomeAfterAction;
        }

        public int getForecastIncreaseAfterAction() {
            return (int) ceil(forecastIncreaseAfterAction / 10000d);
        }

        public void setForecastIncreaseAfterAction(Double forecastIncreaseAfterAction) {
            this.forecastIncreaseAfterAction = forecastIncreaseAfterAction;
        }

        @Override
        public int compareTo(Detail that) {
            return this.groupId - that.groupId;
        }

        @Override
        public String toString() {
            return "Detail{" +
                    "groupId=" + groupId +
                    ", groupName='" + groupName + '\'' +
                    ", lastSndYearAvgUtilPercent=" + lastSndYearAvgUtilPercent +
                    ", lastSndYearChart=" + lastSndYearChart +
                    ", lastSndYearChartData=" + lastSndYearChartData +
                    ", lastFstYearAvgUtilPercent=" + lastFstYearAvgUtilPercent +
                    ", lastFstYearChart=" + lastFstYearChart +
                    ", lastFstYearChartData=" + lastFstYearChartData +
                    ", forecastAvgUtilPercent=" + forecastAvgUtilPercent +
                    ", forecastChart=" + forecastChart +
                    ", forecastChartData=" + forecastChartData +
                    ", forecastResult='" + forecastResult + '\'' +
                    ", suggestion='" + suggestion + '\'' +
                    ", assetUtilForecasts=" + assetUtilForecasts +
                    ", forecastUtilAfterAction=" + forecastUtilAfterAction +
                    ", lastFstYearIncome=" + lastFstYearIncome +
                    ", lastSndYearIncome=" + lastSndYearIncome +
                    ", forecastIncomeNoneAction=" + forecastIncomeNoneAction +
                    ", forecastIncomeAfterAction=" + forecastIncomeAfterAction +
                    ", forecastIncreaseAfterAction=" + forecastIncreaseAfterAction +
                    '}';
        }
    }

}
