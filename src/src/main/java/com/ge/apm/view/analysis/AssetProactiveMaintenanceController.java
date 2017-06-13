package com.ge.apm.view.analysis;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public final class AssetProactiveMaintenanceController implements ServerEventInterface {

    private static final Logger logger = LoggerFactory.getLogger(AssetProactiveMaintenanceController.class);

    private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private final String remote_addr = request.getRemoteAddr();
    private final String page_uri = request.getRequestURI();
    private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();

    private HashMap<String, Object> sqlParams = new HashMap<>();

    @PostConstruct
    public final void init() {

        sqlParams.put("hospital_id", hospital_id);

        DateMidnight midnight = new DateMidnight().minusYears(0);
        this.today = midnight.toDateTime();
        this.firstDayOfThisYear = midnight.withDayOfYear(1).toDateTime();
        this.firstDayOfThisYearPlus1 = this.firstDayOfThisYear.plusYears(1);
        this.firstDayOfThisYearMinus1 = this.firstDayOfThisYear.minusYears(1);
        this.firstDayOfThisYearPlus2 = this.firstDayOfThisYear.plusYears(2);
        sqlParams.put("today", this.today.toDate());
        sqlParams.put("firstDayOfThisYear", this.firstDayOfThisYear.toDate());
        sqlParams.put("firstDayOfThisYearPlus1", this.firstDayOfThisYearPlus1.toDate());
        sqlParams.put("firstDayOfThisYearMinus1", this.firstDayOfThisYearMinus1.toDate());
        sqlParams.put("firstDayOfThisYearPlus2", this.firstDayOfThisYearPlus2.toDate());
    }

    private DateTime today;
    private DateTime firstDayOfThisYear;
    private DateTime firstDayOfThisYearPlus1;
    private DateTime firstDayOfThisYearMinus1;
    private DateTime firstDayOfThisYearPlus2;

    private int assetId;

    public final int getAssetId() {
        return this.assetId;
    }

    public final void setAssetId(int value) {
        this.assetId = value;
        sqlParams.put("assetId", value);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;

        if (asset == null) {
            return;
        }

        Integer assetId = asset.getId();

        WebUtil.navigateTo("/portal/analysis/assetProactiveMaintenanceSingle.xhtml?faces-redirect=true&assetId=" + assetId);
    }

    // endregion

    // region Properties

    public final String getAssetName() {
        if (this.assetId == 0) {
            return WebUtil.getMessage("preventiveMaintenanceAnalysis_allDevices");
        }
        else {
            FluentIterable<Map<String, Object>> iterable = FluentIterable.from(this.query(SQL_SCALAR_DEVICE_NAME_SINGLE));
            return (String) iterable.first().or(ImmutableMap.of("scalar", "")).get("scalar");
        }
    }

    public final List<String> getGeneratedMonth() {
        List<String> list = new ArrayList<>();
        list.add("");
        /* if (this.assetId == 0) {
            list.add(WebUtil.getMessage("preventiveMaintenanceAnalysis_allDevices"));
        }
        else {
            FluentIterable<Map<String, Object>> iterable = FluentIterable.from(this.query(SQL_SCALAR_DEVICE_NAME_SINGLE));
            list.add((String)iterable.first().or(ImmutableMap.of("scalar", (Object)"")).get("scalar"));
        } */
        for (int i = 1; i <= 31; i++) {
            list.add(Integer.toString(i));
        }
        return list;
    }

    public final List<Object> getMaintenanceSchedule() {
        List<Map<String, Object>> list = this.query(SQL_LIST_MAINTENANCE_SCHEDULE);

        List<Object> ret = new ArrayList<>(13);
        for (Map<String, Object> map : list) {
            try {
                String[] a = (String[]) ((Array) map.get("scalar")).getArray();
                a[0] = WebUtil.getFieldValueMessage("month", a[0].trim());
                ret.add(a);
            }
            catch (SQLException exception) {
                ret.add(new String[31 + 1]);
            }
        }
        return ret;
    }

    public final List<String> getGeneratedYear() {
        List<String> list = new ArrayList<>();
        list.add(WebUtil.getMessage("preventiveMaintenanceAnalysis_deviceName"));
        for (int i = 0; i < 157; i++) {
            DateTime current = this.firstDayOfThisYearMinus1.plusWeeks(i);
            DateTime previous = current.minusWeeks(1);
            if (current.getYear() != previous.getYear()) {
                list.add(String.format(WebUtil.getMessage("preventiveMaintenanceAnalysis_year"), current.getYear()));
            }
            else if (current.getMonthOfYear() != previous.getMonthOfYear() && (current.getMonthOfYear() == 4 || current.getMonthOfYear() == 7 || current.getMonthOfYear() == 10)) {
                list.add(String.format(WebUtil.getMessage("preventiveMaintenanceAnalysis_month"), current.getMonthOfYear()));
            }
            else {
                list.add("");
            }
        }
        return list;
    }

    public final class MegaColumn {
        private String name;
        private int span;

        public MegaColumn(String name, int span) {
            this.name = name;
            this.span = span;
        }

        public String getName() {
            return this.name;
        }

        public int getSpan() {
            return this.span;
        }
    }

    public final List<MegaColumn> getMegaColumns() {
        List<MegaColumn> list = new ArrayList<>();
        int mega = 1;
        String last = WebUtil.getMessage("preventiveMaintenanceAnalysis_deviceName");
        for (int i = 0; i < 157; i++) {
            DateTime current = this.firstDayOfThisYearMinus1.plusWeeks(i);
            DateTime previous = current.minusWeeks(1);
            if (current.getYear() != previous.getYear()) {
                list.add(new MegaColumn(last, mega));
                mega = 1;
                last = String.format(WebUtil.getMessage("preventiveMaintenanceAnalysis_year"), current.getYear());

            }
            else if (current.getMonthOfYear() != previous.getMonthOfYear() && (current.getMonthOfYear() == 4 || current.getMonthOfYear() == 7 || current.getMonthOfYear() == 10)) {
                list.add(new MegaColumn(last, mega));
                mega = 1;
                last = WebUtil.getFieldValueMessage("month", Integer.toString(current.getMonthOfYear()));
            }
            else {
                mega++;
            }
        }
        list.add(new MegaColumn(last, mega));
        return list;
    }

    public final List<Object> getMaintenanceForecast() {
        List<Map<String, Object>> list = this.query(SQL_LIST_MAINTENANCE_FORECAST);

        List<Object> ret = new ArrayList<>();
        for (Map<String, Object> map : list) {
            try {
                String[] array = (String[]) ((Array) map.get("scalar")).getArray();
                // backward
                int l2 = array.length - 1;
                while (l2 >= 0 && array[l2].length() == 0) {
                    l2--;
                }
                int l1 = l2 - 1;
                while (l1 >= 0 && array[l1].length() == 0) {
                    l1--;
                }
                int interval = Math.max(l2 - l1,
                                        Weeks.weeksBetween(this.firstDayOfThisYearMinus1, this.today).getWeeks() + 1 - l2);
                // forward
                l2 += interval;
                DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
                while (l2 < array.length) {
                    String date = format.print(this.firstDayOfThisYearMinus1.plusDays(7 * l2));
                    array[l2] = date + " *";
                    l2 += interval;
                }
                ret.add(array);
            }
            catch (SQLException exception) {
                ret.add(new String[157 + 1]);
            }
        }
        return ret;
    }

    // endregion

    // region SQL

    private final List<Map<String, Object>> query(String template) {
        if (this.assetId == 0) {
            template = StringUtils.replace(template, ":#andDeviceFilter", "");
        }
        else {
            template = StringUtils.replace(template, ":#andDeviceFilter", "AND asset.id = :#assetId");
        }

        sqlParams.put("_sql", template);
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
        return NativeSqlUtil.queryForList(template, sqlParams);
    }

    private final static String SQL_SCALAR_DEVICE_NAME_SINGLE = "" +
            "SELECT asset.name AS scalar " +
            "FROM asset_info AS asset " +
            "WHERE asset.id = :#assetId " +
            ";";

    private final static String SQL_LIST_MAINTENANCE_SCHEDULE = "" +
            "SELECT array_prepend(to_char(temporary.month, '99'), " +
            "array_agg(temporary.hint) " +
            ") AS scalar " +
            "FROM ( " +
            "SELECT twelve.month AS month, thirty_one.day AS day, COALESCE(temporary.hint, '') AS hint " +
            "FROM generate_series(1, 12) AS twelve(month) " +
            "CROSS JOIN " +
            "generate_series(1, 31) AS thirty_one(day) " +
            "LEFT OUTER JOIN " +
            "(SELECT EXTRACT (MONTH FROM plan.start_time) AS month, " +
            "EXTRACT (DAY FROM plan.start_time) AS day, " +
            "array_to_string(array_agg(asset.name), ',,,,') AS hint " +
            "FROM pm_order AS plan, " +
            "asset_info AS asset " +
            "WHERE plan.asset_id = asset.id " +
            "AND asset.hospital_id = :#hospital_id " +
            " AND plan.start_time BETWEEN :#firstDayOfThisYear AND :#firstDayOfThisYearPlus1 " +
            ":#andDeviceFilter " +  // AND asset.id = :#assetId
            "GROUP BY month, day " +
            ") AS temporary " +
            "ON twelve.month = temporary.month " +
            "AND thirty_one.day = temporary.day " +
            "ORDER BY twelve.month ASC, " +
            "thirty_one.day ASC " +
            ") AS temporary " +
            "GROUP BY temporary.month " +
            ";";

    private final static String SQL_LIST_MAINTENANCE_FORECAST = "" +
            "WITH " +
            "maintenance_schedule AS ( " +
            "SELECT asset.id AS asset_id, " +
            "CAST (EXTRACT (EPOCH FROM (plan.start_time - :#firstDayOfThisYearMinus1)) / 60 / 60 / 24 / 7 AS integer) AS start_time, " +
            "to_char(plan.start_time, 'YYYY-MM-DD') AS hint " +
            "FROM pm_order AS plan, " +
            "asset_info AS asset " +
            "WHERE plan.asset_id = asset.id " +
            "AND asset.hospital_id = :#hospital_id " +
            "AND asset.is_valid = true " +
            "AND plan.start_time BETWEEN :#firstDayOfThisYearMinus1 AND :#firstDayOfThisYearPlus2 " +
            "), " +
            "maintenance_schedule_asset_list AS ( " +
            "SELECT DISTINCT asset_id " +
            "FROM maintenance_schedule " +
            "), " +
            "maintenance_schedule_time_list AS ( " +
            "SELECT * FROM generate_series(0, 156) AS temporary(week) " +
            "), " +
            "temporary AS ( " +
            "SELECT maintenance_schedule_asset_list.asset_id AS asset_id, " +
            "maintenance_schedule_time_list.week AS start_time, " +
            "COALESCE(maintenance_schedule.hint, '') AS hint " +
            "FROM maintenance_schedule_asset_list " +
            "CROSS JOIN maintenance_schedule_time_list " +
            "LEFT OUTER JOIN maintenance_schedule " +
            "ON maintenance_schedule_asset_list.asset_id = maintenance_schedule.asset_id " +
            "AND maintenance_schedule_time_list.week = maintenance_schedule.start_time " +
            "ORDER BY maintenance_schedule_asset_list.asset_id ASC, " +
            "maintenance_schedule_time_list.week ASC " +
            ") " +
            "SELECT array_prepend(text(asset.name), array_agg(temporary.hint)) AS scalar " +
            "FROM temporary," +
            "asset_info AS asset " +
            "WHERE temporary.asset_id = asset.id " +
            "GROUP BY asset.id " +
            ";";

}