package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.UserContextService;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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
public final class AssetProactiveMaintenanceController {

    protected final static Logger log = LoggerFactory.getLogger(AssetMaintenanceController.class);

    private final Map<String, Object> parameters;

    private int hospitalId;

    private DateTime today;
    private DateTime firstDayOfThisYear;
    private DateTime firstDayOfThisYearMinus2;
    private DateTime firstDayOfThisYearPlus1;

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
        this.firstDayOfThisYearMinus2 = this.firstDayOfThisYear.minusYears(2);
        this.firstDayOfThisYearPlus1 = this.firstDayOfThisYear.plusYears(1);
        this.parameters.put("today", this.today.toDate());
        this.parameters.put("firstDayOfThisYear", this.firstDayOfThisYear.toDate());
        this.parameters.put("firstDayOfThisYearMinus2", this.firstDayOfThisYearMinus2.toDate());
        this.parameters.put("firstDayOfThisYearPlus1", this.firstDayOfThisYearPlus1.toDate());
    }

    // region Parameters

    // endregion

    // region Properties

    public final String getMaintenanceOnDateTable() {
        List<Map<String, Object>> list = this.query(SQL_LIST_MAINTENANCE_ON_DATE_1);

        StringBuilder builder = new StringBuilder();
        builder.append("<table>");
        builder.append(/* TODO: DEMO only */ "<style> table { table-layout: fixed; } td { width: 3%; height: 10; margin: 1; background-color: gray; } td.future-date { background-color: red; } td.past-date {background-color: blue; } </style>");
        builder.append("<tr><td/>");
        for (int i = 1; i <= 31; i++) {
            builder.append("<td>");
            builder.append(i);
            builder.append("</td>");
        }

        int index = 0;
        int currentMonth = 0;
        for (DateTime currentDate = this.firstDayOfThisYear;
             currentDate.isBefore(this.firstDayOfThisYearPlus1);
             currentDate = currentDate.plusDays(1)) {
            // month
            if (currentDate.getMonthOfYear() > currentMonth) {
                currentMonth++;
                builder.append("</tr>");
                builder.append("<tr><td>");
                builder.append(currentMonth);
                builder.append("</td>");
            }
            // day
            if (index < list.size()
                && ((int)list.get(index).get("key")) == currentDate.getDayOfYear()) {
                if ((boolean)list.get(index).get("value")) {
                    builder.append("<td class=\"future-date\"/>");
                }
                else {
                    builder.append("<td class=\"past-date\"/>");
                }
                index++;
            }
            else {
                builder.append("<td/>");
            }
        }
        builder.append("</tr></table>");
        return builder.toString();
    }

    public final String getMaintenanceOnDateForDeviceTable() {
        List<Map<String, Object>> listOfScalar = this.query(SQL_LIST_MAINTENANCE_DEVICE_ON_DATE_2);

        StringBuilder builder = new StringBuilder();
        builder.append("<table>");
        builder.append(/* TODO: DEMO only */ "<style> table { table-layout: fixed; } td { width: 0.09%; height: 10; margin: 0; background-color: gray; } td.future-date { background-color: red; } td.past-date {background-color: blue; } </style>");

        for(Map<String, Object> scalar : listOfScalar) {
            Map<String, Object> extra = new HashMap<>();
            extra.put("assetId", scalar.get("scalar"));
            List<Map<String, Object>> listOfMap = this.query(SQL_LIST_MAINTENANCE_ON_DATE_2_FOR_DEVICE, extra);

            builder.append("<tr>");
            int index = 0;
            DateTime currentDate = this.firstDayOfThisYearMinus2;
            int days = 0;
            while (currentDate.isBefore(this.firstDayOfThisYearPlus1)) {
                if (index < listOfMap.size()
                    && ((int)listOfMap.get(index).get("key")) == days) {
                    if ((boolean)listOfMap.get(index).get("value")) {
                        builder.append("<td class=\"future-date\"/>");
                    }
                    else {
                        builder.append("<td class=\"past-date\"/>");
                    }
                    index++;
                }
                else {
                    builder.append("<td/>");
                }
                currentDate = currentDate.plusDays(1);
                days++;
            }
            builder.append("</tr>");
        }

        return builder.toString();

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
        }
        else {
            // TODO: better approach?
            Map<String, Object> temporary = new HashMap(this.parameters);
            temporary.putAll(extra);
            return NativeSqlUtil.queryForList(template, temporary);
        }
    }

    private final static String SQL_LIST_MAINTENANCE_ON_DATE_1 = "" +
            "SELECT DISTINCT CAST (EXTRACT(DOY FROM plan.start_time) AS integer) AS key, plan.start_time > :#today AS value " +
            "FROM pm_order AS plan, " +
            "     asset_info AS asset " +
            "WHERE plan.asset_id = asset.id " +
            "  AND asset.hospital_id = :#hospitalId " +
            "  AND plan.start_time BETWEEN :#firstDayOfThisYear AND :#firstDayOfThisYearPlus1 " +
            "ORDER BY key ASC " +
            ";";

    private final static String SQL_LIST_MAINTENANCE_DEVICE_ON_DATE_2 = "" +
            "SELECT DISTINCT asset.id AS scalar " +
            "FROM pm_order AS plan, " +
            "     asset_info AS asset " +
            "WHERE plan.asset_id = asset.id " +
            "  AND asset.hospital_id = :#hospitalId " +
            "  AND plan.start_time BETWEEN :#firstDayOfThisYearMinus2 AND :#firstDayOfThisYearPlus1 " +
            "ORDER BY scalar ASC " +
            ";";

    private final static String SQL_LIST_MAINTENANCE_ON_DATE_2_FOR_DEVICE = "" +
            "SELECT DISTINCT CAST (EXTRACT (epoch FROM (plan.start_time - :#firstDayOfThisYearMinus2)) / 60 / 60 / 24 AS integer) AS key, plan.start_time > :#today AS value " +
            "FROM pm_order AS plan, " +
            "     asset_info AS asset " +
            "WHERE plan.asset_id = asset.id " +
            "  AND asset.id = :#assetId " +
            "  AND asset.hospital_id = :#hospitalId " +
            "  AND plan.start_time BETWEEN :#firstDayOfThisYearMinus2 AND :#firstDayOfThisYearPlus1 " +
            "ORDER BY key ASC " +
            ";";

    // endregion

    // region Chart

    // endregion
}

