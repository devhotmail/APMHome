/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javaslang.Tuple5;
import javaslang.Tuple3;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rx.Observable;

/**
 *
 * @author 212579464
 */
@Service
public class ProcessTimeService {

    private static final Logger log = LoggerFactory.getLogger(CommonService.class);
    private Database db;

    private static final String SQL_PROCESS_TIME_BY_GROUP = "select :groupby ,avg(wst.responseTime) as avg_respond,avg(wst.arrivedTime) as avg_arrived, avg(wst.ETTRTime) as avg_ETTR "
            + "from asset_info ai, work_order wo, (select work_order_id,count(*) as count,sum(case  when step_id in ('1','2') then (ws.end_time-ws.start_time)  end) as responseTime,"
            + "sum(case  when step_id in ('1','2','3') then (ws.end_time-ws.start_time)  end) as arrivedTime,"
            + "sum(case  when step_id in ('1','2','3','4','5') then (ws.end_time-ws.start_time)  end) as ETTRTime "
            + "from work_order_step ws where start_time > :start_time and end_time < :end_time   group by work_order_id ) wst "
            + "where ai.id=wo.asset_id and wo.id=wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id group by :groupby order by :orderBy";

    private static final String SQL_PROCESS_TIME_BY_ASSET = "select ai.id,ai.name ,avg(wst.responseTime) as avg_respond,avg(wst.arrivedTime) as avg_arrived, avg(wst.ETTRTime) as avg_ETTR "
            + "from asset_info ai, work_order wo, (select work_order_id,count(*) as count,sum(case  when step_id in ('1','2') then (ws.end_time-ws.start_time)  end) as responseTime,"
            + "sum(case  when step_id in ('1','2','3') then (ws.end_time-ws.start_time)  end) as arrivedTime,"
            + "sum(case  when step_id in ('1','2','3','4','5') then (ws.end_time-ws.start_time)  end) as ETTRTime "
            + "from work_order_step ws where start_time> :start_time and end_time< :end_time   group by work_order_id ) wst "
            + "where ai.id=wo.asset_id and wo.id=wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id :conditionType :conditionDept group by ai.id order by :orderBy :limit :offset ";

    private static final String SQL_PROCESS_TIME_GROSS = "select avg(wst.responseTime) as avg_respond,avg(wst.arrivedTime) as avg_arrived, avg(wst.ETTRTime) as avg_ETTR "
            + "from asset_info ai, work_order wo, (select work_order_id,count(*) as count,sum(case  when step_id in ('1','2') then (ws.end_time-ws.start_time)  end) as responseTime,"
            + "sum(case  when step_id in ('1','2','3') then (ws.end_time-ws.start_time)  end) as arrivedTime,"
            + "sum(case  when step_id in ('1','2','3','4','5') then (ws.end_time-ws.start_time)  end) as ETTRTime "
            + "from work_order_step ws where start_time> :start_time and end_time< :end_time   group by work_order_id ) wst "
            + "where ai.id=wo.asset_id and wo.id=wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id :conditionType :conditionDept ";

    @Resource(name = "connectionProvider")
    private ConnectionProvider connectionProvider;

    @PostConstruct
    public void init() {
        db = Database.from(connectionProvider);
    }

    @Cacheable(cacheNames = "springCache", key = "'processTimeService.queryListByType.'+#siteId+'.'+#hospitalId+'.'+#fromTime+'.'+#toTime+'.orderBy'+#orderBy+'.groupby'+#groupby")
    public Observable<Tuple5<String, String, String, String, String>> queryListByType(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, String orderBy, String groupby) {

        return db.select(SQL_PROCESS_TIME_BY_GROUP.replace(":groupby", groupby.contains("type") ? "ai.asset_group" : "ai.clinical_dept_name,ai.clinical_dept_id")).parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time",fromTime.toDate()).parameter("end_time", toTime.toDate())
                .parameter("orderBy", "avg_".concat(orderBy))
                .get(rs -> new Tuple5<>(rs.getString(groupby.contains("type") ? "asset_group" : "clinical_dept_id"), groupby.contains("type") ? "" : rs.getString("clinical_dept_name"), rs.getString("avg_respond"), rs.getString("avg_arrived"), rs.getString("avg_ETTR")));
    }

    @Cacheable(cacheNames = "springCache", key = "'processTimeService.queryListByAsset.'+#siteId+'.'+#hospitalId+'.'+#typeId+'.'+#deptId+'.'+#offset+'.'+#limit+'.'+#fromTime+'.'+#toTime+'.orderBy'+#orderBy+'.groupby'+#groupby")
    public Observable<Tuple5<String, String, String, String, String>> queryListByAsset(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, String orderBy, Integer typeId, Integer deptId, Integer offset, Integer limit) {

        return db.select(SQL_PROCESS_TIME_BY_ASSET.replace(":conditionType", typeId == 0 ? "" : "and ai.asset_group='" + typeId.toString() + "'")
                .replace(":conditionDept", deptId == 0 ? "" : "and ai.clinical_dept_id='" + deptId.toString() + "'")
                .replace(":limit", limit == 0 ? "" : " limit ".concat(limit.toString()))
                .replace(":offset", limit == 0 || offset == 0 ? "" : " offset ".concat(offset.toString())))
                .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time", fromTime.toDate()).parameter("end_time", toTime.toDate()).parameter("orderBy", "avg_".concat(orderBy))
                .get(rs -> new Tuple5<>(rs.getString("id"), rs.getString("name"), rs.getString("avg_respond"), rs.getString("avg_arrived"), rs.getString("avg_ETTR")));
    }

    @Cacheable(cacheNames = "springCache", key = "'processTimeService.queryGross.'+#siteId+'.'+#hospitalId+'.'+#typeId+'.'+#deptId+'.'+#fromTime+'.'+#toTime")
    public Observable<Tuple3<String, String, String>> queryGross(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, Integer typeId, Integer deptId) {

        return db.select(SQL_PROCESS_TIME_GROSS.replace(":conditionType", typeId == 0 ? "" : "and ai.asset_group='" + typeId.toString() + "'")
                .replace(":conditionDept", deptId == 0 ? "" : "and ai.clinical_dept_id='" + deptId.toString() + "'"))
                .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time", fromTime.toDate()).parameter("end_time", toTime.toDate())
                .get(rs -> new Tuple3<>(rs.getString("avg_respond"), rs.getString("avg_arrived"), rs.getString("avg_ETTR")));
    }

}
