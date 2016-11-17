package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.FluentIterable;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.postgresql.jdbc4.Jdbc4Array;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.NativeSqlUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        DateMidnight midnight = new DateMidnight().minusYears(1); // TODO: the minus is for DEMO
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

    public List<Object> getMaintenanceOnDateTable() {
        List<Map<String, Object>> list = this.query(SQL_LIST_MAINTENANCE_ON_DATE_1);

        List<Object> ret = new ArrayList<>(13);
        for (Map<String, Object> map : list) {
            try {
                ret.add(((Jdbc4Array) map.get("scalar")).getArray());
            }
            catch (SQLException exception) {
                ret.add(new String[31]);
            }
        }
        return ret;
    }


    // endregion

    // region SQL

    private final List<Map<String, Object>> query(String template) {
        return this.query(template, null);
    }

    private final List<Map<String, Object>> query(String template, Map<String, Object> extra) {
        log.info("=> {}", template);
        if (extra == null) {
            return NativeSqlUtil.queryForList(template, this.parameters);
        } else {
            // TODO: better approach?
            Map<String, Object> temporary = new HashMap(this.parameters);
            temporary.putAll(extra);
            return NativeSqlUtil.queryForList(template, temporary);
        }
    }

    private final static String SQL_LIST_MAINTENANCE_ON_DATE_1 = "" +
            "SELECT array_prepend(to_char(temporary.month, '99'), array_agg(temporary.hint)) AS scalar " +
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
            "                              '+' AS hint " +
            "              FROM pm_order AS plan, " +
            "                   asset_info AS asset " +
            "              WHERE plan.asset_id = asset.id " +
            "                AND asset.hospital_id = :#hospitalId " +
            "                AND plan.start_time BETWEEN :#firstDayOfThisYear AND :#firstDayOfThisYearPlus1 " +
            "             ) AS temporary " +
            "             ON twelve.month = temporary.month AND thirty_one.day = temporary.day " +
            "             ORDER BY twelve.month ASC, thirty_one.day ASC " +
            ") AS temporary " +
            "GROUP BY temporary.month " +
            ";";

    // endregion
}