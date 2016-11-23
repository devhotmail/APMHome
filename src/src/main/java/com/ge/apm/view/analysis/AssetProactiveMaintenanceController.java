package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.UserContextService;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.postgresql.jdbc4.Jdbc4Array;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.NativeSqlUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.sql.SQLException;
import java.util.*;

@ManagedBean
@ViewScoped
public final class AssetProactiveMaintenanceController {

    protected final static Logger log = LoggerFactory.getLogger(AssetMaintenanceController.class);

    public AssetProactiveMaintenanceController() {
        this.parameters = new HashMap<>();
    }

    @PostConstruct
    public final void init() {
        this.hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        this.parameters.put("hospitalId", this.hospitalId);

        DateMidnight midnight = new DateMidnight().minusYears(0);
        this.today = midnight.toDateTime();
        this.firstDayOfThisYear = midnight.withDayOfYear(1).toDateTime();
        this.firstDayOfThisYearPlus1 = this.firstDayOfThisYear.plusYears(1);
        this.firstDayOfThisYearMinus1 = this.firstDayOfThisYear.minusYears(1);
        this.firstDayOfThisYearPlus2 = this.firstDayOfThisYear.plusYears(2);
        this.parameters.put("today", this.today.toDate());
        this.parameters.put("firstDayOfThisYear", this.firstDayOfThisYear.toDate());
        this.parameters.put("firstDayOfThisYearPlus1", this.firstDayOfThisYearPlus1.toDate());
        this.parameters.put("firstDayOfThisYearMinus1", this.firstDayOfThisYearMinus1.toDate());
        this.parameters.put("firstDayOfThisYearPlus2", this.firstDayOfThisYearPlus2.toDate());
    }

    // region Parameters

    private final Map<String, Object> parameters;

    private int hospitalId;

    private DateTime today;
    private DateTime firstDayOfThisYear;
    private DateTime firstDayOfThisYearPlus1;
    private DateTime firstDayOfThisYearMinus1;
    private DateTime firstDayOfThisYearPlus2;

    // endregion

    // region Properties

    public final List<String> getGeneratedMonth() {
        List<String> list = new ArrayList<>();
        list.add("全部设备"); // TODO: i18n
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
                ret.add(((Jdbc4Array) map.get("scalar")).getArray());
            }
            catch (SQLException exception) {
                ret.add(new String[31 + 1]);
            }
        }
        return ret;
    }

    public final List<String> getGeneratedYear() {
        List<String> list = new ArrayList<>();
        list.add("设备名称");
        for (int i = 0; i < 157; i++) {
            DateTime current = this.firstDayOfThisYearMinus1.plusWeeks(i);
            DateTime previous = current.minusWeeks(1);
            if (current.getYear() != previous.getYear()) {
                list.add(String.format("%d年", current.getYear()));
            }
            else if (current.getMonthOfYear() != previous.getMonthOfYear() && (current.getMonthOfYear() == 4 || current.getMonthOfYear() == 7 || current.getMonthOfYear() == 10)) {
                list.add(String.format("%d月", current.getMonthOfYear()));
            }
            else {
                list.add("");
            }
        }
        return list;
    }

    public final List<Object> getMaintenanceForecast() {
        List<Map<String, Object>> list = this.query(SQL_LIST_MAINTENANCE_FORECAST);

        List<Object> ret = new ArrayList<>();
        for (Map<String, Object> map : list) {
            try {
                String[] array =(String[])((Jdbc4Array) map.get("scalar")).getArray();
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
                                        Weeks.weeksBetween(this.today, this.firstDayOfThisYearMinus1).getWeeks() - l2);
                // forward
                l2 += interval;
                while (l2 < array.length) {
                    array[l2] = "tinted lightly-tinted";
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
        return NativeSqlUtil.queryForList(template, this.parameters);
    }

    private final static String SQL_LIST_MAINTENANCE_SCHEDULE = "" +
            "SELECT array_prepend(to_char(temporary.month, '99'), " +
            "                     array_agg(temporary.hint) " +
            "                    ) AS scalar " +
            "FROM ( " +
            "        SELECT twelve.month AS month, thirty_one.day AS day, COALESCE(temporary.hint, '') AS hint " +
            "        FROM (VALUES  (1),  (2),  (3),  (4),  (5),  (6),  (7),  (8),  (9), (10), " +
            "                     (11), (12) " +
            "             ) AS twelve(month) " +
            "             CROSS JOIN " +
            "             (VALUES  (1),  (2),  (3),  (4),  (5),  (6),  (7),  (8),  (9), (10), " +
            "                     (11), (12), (13), (14), (15), (16), (17), (18), (19), (20), " +
            "                     (21), (22), (23), (24), (25), (26), (27), (28), (29), (30), " +
            "                     (31) " +
            "             ) AS thirty_one(day) " +
            "             LEFT OUTER JOIN " +
            "             (SELECT DISTINCT EXTRACT (MONTH FROM plan.start_time) AS month, " +
            "                              EXTRACT (DAY FROM plan.start_time) AS day, " +
            "                              'tinted' AS hint " +
            "              FROM pm_order AS plan, " +
            "                   asset_info AS asset " +
            "              WHERE plan.asset_id = asset.id " +
            "                AND asset.hospital_id = :#hospitalId " +
            "                AND plan.start_time BETWEEN :#firstDayOfThisYear AND :#firstDayOfThisYearPlus1 " +
            "             ) AS temporary " +
            "             ON twelve.month = temporary.month " +
            "            AND thirty_one.day = temporary.day " +
            "        ORDER BY twelve.month ASC, " +
            "                 thirty_one.day ASC " +
            ") AS temporary " +
            "GROUP BY temporary.month " +
            ";";

    private final static String SQL_LIST_MAINTENANCE_FORECAST = "" +
            "WITH " +
            "maintenance_schedule AS ( " +
            "        SELECT asset.id AS asset_id, " +
            "               CAST (EXTRACT (EPOCH FROM (plan.start_time - :#firstDayOfThisYearMinus1)) / 60 / 60 / 24 / 7 AS integer) AS start_time, " +
            "               text('tinted') AS hint " +
            "        FROM pm_order AS plan, " +
            "             asset_info AS asset " +
            "        WHERE plan.asset_id = asset.id " +
            "          AND asset.hospital_id = :#hospitalId " +
            "          AND plan.start_time BETWEEN :#firstDayOfThisYearMinus1 AND :#firstDayOfThisYearPlus2 " +
            "), " +
            "maintenance_schedule_asset_list AS ( " +
            "        SELECT DISTINCT asset_id " +
            "        FROM maintenance_schedule " +
            "), " +
            "maintenance_schedule_time_list AS ( " +
            "        SELECT * FROM generate_series(0, 156) AS temporary(week) " +
            "), " +
            "temporary AS ( " +
            "        SELECT maintenance_schedule_asset_list.asset_id AS asset_id, " +
            "               maintenance_schedule_time_list.week AS start_time, " +
            "               COALESCE(maintenance_schedule.hint, '') AS hint " +
            "        FROM maintenance_schedule_asset_list " +
            "        CROSS JOIN maintenance_schedule_time_list " +
            "        LEFT OUTER JOIN maintenance_schedule " +
            "        ON maintenance_schedule_asset_list.asset_id = maintenance_schedule.asset_id " +
            "        AND maintenance_schedule_time_list.week = maintenance_schedule.start_time " +
            "        ORDER BY maintenance_schedule_asset_list.asset_id ASC, " +
            "                 maintenance_schedule_time_list.week ASC " +
            ") " +
            "SELECT array_prepend(text(asset.name), array_agg(temporary.hint)) AS scalar " +
            "FROM temporary," +
            "     asset_info AS asset " +
            "WHERE temporary.asset_id = asset.id " +
            "GROUP BY asset.id " +
            ";";

    // endregion
}