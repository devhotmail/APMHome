package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.Tuple5;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Date;


@Service
public class MaService {
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }


  //return values in Tuple5 are attributes of assets: id, name, dept, type, supplier
  //return values in Tuple3 are measurements of assets: price, onrate, maintenanceCost
  @Cacheable(cacheNames = "springCache", key = "'MaService.findAstMtItems.'+#siteId+'.'+#hospitalId+'.'+#startDate+'.'+#endDate+'.'+#dept+'.'+#type+'.'+#supplier")
  public Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple3<Double, Double, Double>>> findAstMtItems
  (Integer siteId, Integer hospitalId, Date startDate, Date endDate, Integer dept, Integer type, Integer supplier) {
    String astMtSQL = new SQL() {{
      SELECT("ai.id", "ai.name", "dept", "type", "supplier", "COALESCE(ai.purchase_price,0) as price", "down_rate", "maintenance_cost");
      FROM("asset_info ai");
      INNER_JOIN("(" + new SQL() {{
        SELECT("distinct asset_id", "dept_id as dept", "asset_group as type", "supplier_id as supplier", "COALESCE(AVG(asu.down_time),0)/86400 as down_rate", "COALESCE(SUM(asu.maintenance_cost),0) as maintenance_cost");
        FROM("asset_summit asu");
        WHERE("hospital_id = :hospital_id");
        WHERE("site_id = :site_id");
        WHERE("created >= :start_day");
        WHERE("created <= :end_day");
        GROUP_BY("asset_id, dept, type, supplier");
        if (dept != null) {
          WHERE("dept_id = :dept");
        }
        if (type != null) {
          WHERE("asset_group = :type");
        }
        if (supplier != null) {
          WHERE("supplier_id = :supplier");
        }
      }}.toString() + ") right_table on ai.id = right_table.asset_id");
      ORDER_BY("ai.id");
    }}.toString();

    QuerySelect.Builder dbBuilder = db.select(astMtSQL)
      .parameter("start_day", startDate)
      .parameter("end_day", endDate)
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId);
    if (dept != null) {
      dbBuilder = dbBuilder.parameter("dept", dept);
    }
    if (type != null) {
      dbBuilder = dbBuilder.parameter("type", type);
    }
    if (supplier != null) {
      dbBuilder = dbBuilder.parameter("supplier", supplier);
    }

    return dbBuilder.get(rs -> Tuple.of(
      Tuple.of(rs.getInt("id"), rs.getString("name"), rs.getInt("dept"), rs.getInt("type"), rs.getInt("supplier")),
      Tuple.of(rs.getDouble("price"), 1D - rs.getDouble("down_rate"), rs.getDouble("maintenance_cost"))));
  }


  //the 1st parameter (Integer) of Tuple2 is group_id
  //return values in Tuple2 are measurements of assets: onrate, maintenanceCost
  @Cacheable(cacheNames = "springCache", key = "'MaService.findAstMtGroups.'+#siteId+'.'+#hospitalId+'.'+#startDate+'.'+#endDate+'.'+#dept+'.'+#type+'.'+#supplier+'.'+#groupBy")
  public Observable<Tuple2<Integer, Tuple2<Double, Double>>> findAstMtGroups
  (Integer siteId, Integer hospitalId, Date startDate, Date endDate, Integer dept, Integer type, Integer supplier, String groupBy) {
    String astMtSQL = new SQL() {{
      SELECT("dept".equals(groupBy) ? "dept_id as group_id" : ("type".equals(groupBy) ? "asset_group as group_id" : "supplier_id as group_id"),
        "COALESCE(AVG(down_time),0)/86400 as down_rate", "COALESCE(SUM(maintenance_cost),0) as maintenance_cost");
      FROM("asset_summit");
      WHERE("hospital_id = :hospital_id");
      WHERE("site_id = :site_id");
      WHERE("created >= :start_day");
      WHERE("created <= :end_day");
      if (dept != null) {
        WHERE("dept_id = :dept");
      }
      if (type != null) {
        WHERE("asset_group = :type");
      }
      if (supplier != null) {
        WHERE("supplier_id = :supplier");
      }
      GROUP_BY("group_id");
      ORDER_BY("group_id");
    }}.toString();

    QuerySelect.Builder dbBuilder = db.select(astMtSQL)
      .parameter("start_day", startDate)
      .parameter("end_day", endDate)
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId);
    if (dept != null) {
      dbBuilder = dbBuilder.parameter("dept", dept);
    }
    if (type != null) {
      dbBuilder = dbBuilder.parameter("type", type);
    }
    if (supplier != null) {
      dbBuilder = dbBuilder.parameter("supplier", supplier);
    }

    return dbBuilder.get(rs -> Tuple.of(rs.getInt("group_id"),
      Tuple.of(1D - rs.getDouble("down_rate"), rs.getDouble("maintenance_cost"))));
  }

}
