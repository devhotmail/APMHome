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
        builder.append("</tr>");

        int index = 0;
        int currentMonth = 0;
        for (DateTime currentDate = this.firstDayOfThisYear;
             currentDate.isBefore(this.firstDayOfThisYearPlus1);
             currentDate = currentDate.plusDays(1)) {
            // month
            if (currentDate.getMonthOfYear() > currentMonth) {
                currentMonth++;
                if (builder.length() != 0) {
                    builder.append("</tr>");
                }
                builder.append("<tr><td>");
                builder.append(currentMonth);
                builder.append("</td>");
            }
            // day
            if (index < list.size()
                && ((Double)list.get(index).get("key")).intValue() == currentDate.getDayOfYear()) {
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

    // endregion

    // region SQL
    
    private final List<Map<String, Object>> query(String template) {
        log.info("=> {}", template);
        return NativeSqlUtil.queryForList(template, this.parameters);
    }

    private final static String SQL_LIST_MAINTENANCE_ON_DATE_1 = "" +
            "SELECT DISTINCT EXTRACT(DOY FROM plan.start_time) AS key, plan.start_time > :#today AS value " +
            "FROM pm_order AS plan, " +
            "     asset_info AS asset " +
            "WHERE plan.asset_id = asset.id " +
            "  AND asset.hospital_id = :#hospitalId " +
            "  AND plan.start_time BETWEEN :#firstDayOfThisYear AND :#firstDayOfThisYearPlus1 " +
            "ORDER BY key ASC " +
            ";";

    // endregion

    // region Chart

    // endregion
}

