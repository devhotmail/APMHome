/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.google.common.collect.ImmutableMap;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javaslang.Tuple1;
import javaslang.Tuple4;
import javaslang.Tuple5;
import javaslang.Tuple7;
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

    private static final String SQL_PROCESS_TIME_BY_GROUP = "select :groupby as id,cast(avg(wst.responseTime)as integer) as avg_respond,cast(avg(wst.arrivedTime)as integer) as avg_arrived, cast(avg(wst.ETTRTime)as integer) as avg_ETTR "
            + "from asset_info ai, work_order wo, (select work_order_id,count(*) as count,sum(case  when step_id <=2 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as responseTime,"
            + "sum(case  when step_id <=3 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as arrivedTime,"
            + "sum(case  when step_id <=5 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as ETTRTime "
            + "from work_order_step ws where start_time > :start_time and end_time < :end_time   group by work_order_id ) wst "
            + "where ai.id=wo.asset_id and wo.id=wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id :conditionType :conditionDept :conditionSupplier group by :groupby order by :orderBy :limit :offset";

    private static final String SQL_PROCESS_TIME_BY_ASSET = "select ai.id,ai.name ,cast(avg(wst.responseTime)as integer) as avg_respond,cast(avg(wst.arrivedTime)as integer) as avg_arrived, cast(avg(wst.ETTRTime)as integer) as avg_ETTR, "
            + "cast(avg(wst.dispatchTime)as integer) as dispatchTime, cast(avg(wst.workingTime)as integer) as workingTime "
            + "from asset_info ai, work_order wo, (select work_order_id,count(*) as count,sum(case  when step_id <=1 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as dispatchTime,"
            + "sum(case  when step_id <=2 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as responseTime,"
            + "sum(case  when step_id <=3 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as arrivedTime,"
            + "sum(case  when step_id <=4 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as workingTime,"
            + "sum(case  when step_id <=5 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as ETTRTime "
            + "from work_order_step ws where start_time> :start_time and end_time< :end_time   group by work_order_id ) wst "
            + "where ai.id=wo.asset_id and wo.id=wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id :conditionType :conditionDept :conditionSupplier group by ai.id order by :orderBy :limit :offset ";

    private static final String SQL_PROCESS_TIME_GROSS = "select cast(avg(wst.responseTime)as integer) as avg_respond,cast(avg(wst.arrivedTime)as integer) as avg_arrived, cast(avg(wst.ETTRTime)as integer) as avg_ETTR ,"
            + "cast(avg(wst.dispatchTime)as integer) as dispatchTime, cast(avg(wst.workingTime)as integer) as workingTime "
            + "from asset_info ai, work_order wo, (select work_order_id,count(*) as count,sum(case  when step_id <=1 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as dispatchTime,"
            + "sum(case  when step_id <=2 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as responseTime,"
            + "sum(case  when step_id <=3 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as arrivedTime,"
            + "sum(case  when step_id <=4 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as workingTime,"
            + "sum(case  when step_id <=5 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as ETTRTime "
            + "from work_order_step ws where start_time> :start_time and end_time< :end_time   group by work_order_id ) wst "
            + "where ai.id=wo.asset_id and wo.id=wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id :conditionType :conditionDept :conditionSupplier ";

    private static final String SQL_PROCESS_TIME_COUNT = "select count(*) from (select :groupby  from asset_info ai, work_order wo, (select work_order_id"
            + "  from work_order_step ws where start_time> :start_time and end_time< :end_time  group by work_order_id ) wst "
            + "  where ai.id=wo.asset_id and wo.id=wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id :conditions group by :groupby) t1";

    private static final String SQL_PROCESS_TIME_DISTRIBUTION ="select ETTRTime from asset_info ai, work_order wo, (select work_order_id, sum(case when step_id <= 5 then extract (EPOCH from ws.end_time - ws.start_time) end) as ETTRTime "
                + " from work_order_step ws where start_time>:start_time and end_time<:end_time  "
                + " group by work_order_id )wst where ai.id = wo.asset_id and wo.id = wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id"
                + " :conditionType :conditionDept :conditionSupplier order by ETTRTime limit 1 offset :offset";
    
    private static final String SQL_PROCESS_TIME_PHASE_COUNTS="select sum(case when :timePhase<:time1 then 1 end) as countT1,sum(case when :timePhase<:time2 then 1 end) as countT2,"
                + "sum(case when :timePhase<:timeMax then 1 end) as countTMax,count(*) as countAll from asset_info ai, work_order wo, (select work_order_id,"
                + "sum(case  when step_id <=2 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as responseTime,"
                + "sum(case  when step_id <=3 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as arrivedTime,"
                + "sum(case  when step_id <=5 then extract(epoch FROM(ws.end_time-ws.start_time))  end) as ETTRTime "
                + "from work_order_step ws where start_time> :start_time and end_time< :end_time   group by work_order_id ) wst "
                + "where ai.id=wo.asset_id and wo.id=wst.work_order_id and ai.site_id=:site_id and ai.hospital_id=:hospital_id :conditionType :conditionDept :conditionSupplier :conditionAsset";
    
    
    @Resource(name = "connectionProvider")
    private ConnectionProvider connectionProvider;

    @PostConstruct
    public void init() {
        db = Database.from(connectionProvider);
    }

    @Cacheable(cacheNames = "springCache", key = "'processTimeService.queryCount.'+#siteId+'.'+#hospitalId+'.'+#typeId+'.'+#deptId+'.'+#supplier+'.'+#fromTime+'.'+#toTime+'.groupby'+#groupby")
    public Observable<Integer> queryCount(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, String groupby, Integer typeId, Integer deptId, Integer supplier) {
        return db.select(SQL_PROCESS_TIME_COUNT.replace(":groupby", ImmutableMap.of("type", "ai.asset_group", "dept", "ai.clinical_dept_name,ai.clinical_dept_id", "supplier", "ai.supplier_id", "assetId", "ai.id","WorkOrder","work_order_id").get(groupby))
                .replace(":conditions", "".concat(typeId > 0 ? " and ai.asset_group=" + typeId : "").concat(deptId > 0 ? "and ai.clinical_dept_id=" + deptId : "").concat(supplier > 0 ? "and ai.supplier_id=" + supplier : "")))
                .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time", fromTime.toDate()).parameter("end_time", toTime.toDate())
                .getAs(Integer.class);
    }

    @Cacheable(cacheNames = "springCache", key = "'processTimeService.queryListByType.'+#siteId+'.'+#hospitalId+'.'+#typeId+'.'+#deptId+'.'+#supplier+'.'+#offset+'.'+#limit+'.'+#fromTime+'.'+#toTime+'.orderBy'+#orderBy+'.groupby'+#groupby")
    public Observable<Tuple5<String, String, Integer, Integer, Integer>> queryListByType(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, String orderBy, String groupby, Integer typeId, Integer deptId, Integer supplier, Integer offset, Integer limit) {

        return db.select(SQL_PROCESS_TIME_BY_GROUP.replace(":groupby", ImmutableMap.of("type", "ai.asset_group", "dept", "ai.clinical_dept_name,ai.clinical_dept_id", "supplier", "ai.supplier_id").get(groupby))
                .replace(":conditionType", typeId == 0 ? "" : "and ai.asset_group=".concat(typeId.toString()))
                .replace(":conditionDept", deptId == 0 ? "" : "and ai.clinical_dept_id=".concat(deptId.toString()))
                .replace(":conditionSupplier", supplier == 0 ? "" : "and ai.supplier_id=".concat(supplier.toString()))
                .replace(":limit", limit == 0 ? "" : " limit ".concat(limit.toString()))
                .replace(":offset", limit == 0 || offset == 0 ? "" : " offset ".concat(offset.toString())))
                .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time", fromTime.toDate()).parameter("end_time", toTime.toDate())
                .parameter("orderBy", "avg_".concat(orderBy))
                .get(rs -> new Tuple5<>(rs.getString("id"), groupby.contains("dept") ? rs.getString("clinical_dept_name") : "", rs.getInt("avg_respond"), rs.getInt("avg_arrived"), rs.getInt("avg_ETTR")));
    }

    @Cacheable(cacheNames = "springCache", key = "'processTimeService.queryListByAsset.'+#siteId+'.'+#hospitalId+'.'+#typeId+'.'+#deptId+'.'+#supplier+'.'+#offset+'.'+#limit+'.'+#fromTime+'.'+#toTime+'.orderBy'+#orderBy+'.groupby'+#groupby")
    public Observable<Tuple7<String, String, Integer, Integer, Integer, Integer, Integer>> queryListByAsset(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, String orderBy, Integer typeId, Integer deptId, Integer supplier, Integer offset, Integer limit) {

        return db.select(SQL_PROCESS_TIME_BY_ASSET.replace(":conditionType", typeId == 0 ? "" : "and ai.asset_group=".concat(typeId.toString()))
                .replace(":conditionDept", deptId == 0 ? "" : "and ai.clinical_dept_id=".concat(deptId.toString()))
                .replace(":conditionSupplier", supplier == 0 ? "" : "and ai.supplier_id=".concat(supplier.toString()))
                .replace(":limit", limit == 0 ? "" : " limit ".concat(limit.toString()))
                .replace(":offset", limit == 0 || offset == 0 ? "" : " offset ".concat(offset.toString())))
                .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time", fromTime.toDate()).parameter("end_time", toTime.toDate()).parameter("orderBy", "avg_".concat(orderBy))
                .get(rs -> new Tuple7<>(rs.getString("id"), rs.getString("name"), rs.getInt("avg_respond"), rs.getInt("avg_arrived"), rs.getInt("avg_ETTR"), rs.getInt("dispatchTime"), rs.getInt("workingTime")));
    }

    @Cacheable(cacheNames = "springCache", key = "'processTimeService.queryGross.'+#siteId+'.'+#hospitalId+'.'+#typeId+'.'+#deptId+'.'+#fromTime+'.'+#toTime")
    public Observable<Tuple5<Integer, Integer, Integer, Integer, Integer>> queryGross(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, Integer typeId, Integer deptId, Integer supplier) {

        return db.select(SQL_PROCESS_TIME_GROSS.replace(":conditionType", typeId == 0 ? "" : "and ai.asset_group=".concat(typeId.toString()))
                .replace(":conditionDept", deptId == 0 ? "" : "and ai.clinical_dept_id=".concat(deptId.toString()))
                .replace(":conditionSupplier", supplier == 0 ? "" : "and ai.supplier_id=".concat(supplier.toString())))
                .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time", fromTime.toDate()).parameter("end_time", toTime.toDate())
                .get(rs -> new Tuple5<>(rs.getInt("avg_respond"), rs.getInt("avg_arrived"), rs.getInt("avg_ETTR"), rs.getInt("dispatchTime"), rs.getInt("workingTime")));
    }

    public Observable<Tuple4<Integer, Integer, Integer, Integer>> queryCountsOfPhase(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, Integer typeId, Integer deptId, Integer supplier, Integer assetId, String timeType, Integer t1, Integer t2, Integer tMax) {

        return db.select(SQL_PROCESS_TIME_PHASE_COUNTS.replace(":timePhase", ImmutableMap.of("ETTR", "ETTRTime", "arrived", "arrivedTime", "respond", "responseTime").get(timeType))
                .replace(":conditionType", typeId == 0 ? "" : "and ai.asset_group=".concat(typeId.toString()))
                .replace(":conditionDept", deptId == 0 ? "" : "and ai.clinical_dept_id=".concat(deptId.toString()))
                .replace(":conditionSupplier", supplier == 0 ? "" : "and ai.supplier_id=".concat(supplier.toString()))
                .replace(":conditionAsset", supplier == 0 ? "" : "and ai.id=".concat(assetId.toString())))
                .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time", fromTime.toDate()).parameter("end_time", toTime.toDate())
                .parameter("time1", t1).parameter("time2", t2).parameter("timeMax", tMax)
                .get(rs -> new Tuple4<>(rs.getInt("countT1"), rs.getInt("countT2"), rs.getInt("countTMax"), rs.getInt("countAll")));

    }

    public Observable<Integer> queryDistribution(Integer siteId, Integer hospitalId, DateTime fromTime, DateTime toTime, Integer typeId, Integer deptId, Integer supplier, Integer count) {
        
        return db.select(SQL_PROCESS_TIME_DISTRIBUTION.replace(":conditionType", typeId == 0 ? "" : "and ai.asset_group=".concat(typeId.toString()))
                .replace(":conditionDept", deptId == 0 ? "" : "and ai.clinical_dept_id=".concat(deptId.toString()))
                .replace(":conditionSupplier", supplier == 0 ? "" : "and ai.supplier_id=".concat(supplier.toString())))
                .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
                .parameter("start_time", fromTime.toDate()).parameter("end_time", toTime.toDate()).parameter("offset", count)
                .getAs(Integer.class);
    }
    
    
    

}
