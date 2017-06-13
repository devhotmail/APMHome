package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import org.apache.ibatis.jdbc.SQL;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple7;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Date;

@Service
public class StaffService {

  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  // #1
  private static final String staff_wo
    = " SELECT asset_owner_id, asset_owner_name, SUM(wo_hour) as wo_hour, AVG(score) as score, SUM(count) as count, SUM(closed) as closed, SUM(open) as open "
    + " FROM "
    + " ( (SELECT DISTINCT asset_owner_id, id, asset_owner_name FROM asset_info WHERE site_id = :site_id AND hospital_id = :hospital_id ) left_table "
    + " LEFT JOIN "
    + " (SELECT asset_id, SUM(total_man_hour) as wo_hour, COUNT(*) as count, SUM(CASE WHEN status != 1 THEN 1 ELSE 0 END) as closed, SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as open FROM v2_work_order WHERE site_id = :site_id AND hospital_id = :hospital_id AND created_date >= :from AND created_date <= :to GROUP BY asset_id) right_table "
    + " ON left_table.id = right_table.asset_id ) left_table "
    + " LEFT JOIN "
    + " (SELECT asset_id, AVG(feedback_rating) as score FROM v2_work_order WHERE feedback_rating > 0 AND site_id = :site_id AND hospital_id = :hospital_id AND created_date >= :from AND created_date <= :to GROUP BY asset_id) right_table "
    + " ON left_table.id = right_table.asset_id "
    + " GROUP BY asset_owner_id, asset_owner_name "
    + " ORDER BY asset_owner_id ";

  public Observable<Tuple7<Integer, String, Integer, Double, Integer, Integer, Integer>> queryForWo(Integer site_id, Integer hospital_id, Date from, Date to) {

    return db.select(staff_wo)
      .parameter("site_id", site_id)
      .parameter("hospital_id", hospital_id)
      .parameter("from", from)
      .parameter("to", to)
      .get(rs ->
        new Tuple7<Integer, String, Integer, Double, Integer, Integer, Integer>(
          rs.getInt("asset_owner_id"), rs.getString("asset_owner_name"), rs.getInt("wo_hour"), rs.getDouble("score"), rs.getInt("count"), rs.getInt("closed"), rs.getInt("open")));
  }

  // #2
  private static final String staff_other
    = " SELECT asset_owner_id, pm_hour, insp_hour, meter_hour "
    + " FROM "
    + " ( (SELECT DISTINCT asset_owner_id FROM asset_info WHERE site_id = :site_id AND hospital_id = :hospital_id ) left_table "
    + " LEFT JOIN "
    + " (SELECT owner_id, SUM(man_hours) as pm_hour FROM pm_order WHERE site_id = :site_id AND hospital_id = :hospital_id AND create_time >= :from AND create_time <= :to GROUP BY owner_id) right_table "
    + " ON left_table.asset_owner_id = right_table.owner_id ) left_table "
    + " LEFT JOIN "
    + " (SELECT owner_id, SUM(CASE WHEN order_type = 1 THEN man_hours ELSE 0 END) as insp_hour, SUM(CASE WHEN order_type = 2 THEN man_hours ELSE 0 END) as meter_hour FROM inspection_order WHERE site_id = :site_id AND hospital_id = :hospital_id AND create_time >= :from AND create_time <= :to GROUP BY owner_id) right_table "
    + " ON left_table.asset_owner_id = right_table.owner_id "
    + " ORDER BY asset_owner_id ";

  public Observable<Tuple4<Integer, Integer, Integer, Integer>> queryForOther(Integer site_id, Integer hospital_id, Date from, Date to) {

    return db.select(staff_other)
      .parameter("site_id", site_id)
      .parameter("hospital_id", hospital_id)
      .parameter("from", from)
      .parameter("to", to)
      .get(rs ->
        new Tuple4<Integer, Integer, Integer, Integer>(
          rs.getInt("asset_owner_id"), rs.getInt("pm_hour"), rs.getInt("insp_hour"), rs.getInt("meter_hour")));
  }

  @Cacheable(cacheNames = "springCache", key = "'staffService.queryForRole'")
  public Observable<Tuple2<Integer, Integer>> queryForRole() {
    return db.select(new SQL() {{
      SELECT("user_id", "role_id");
      FROM("user_role");
    }}.toString())
      .getAs(Integer.class, Integer.class)
      .map(v -> new Tuple2<Integer, Integer>(v._1(), v._2())).cache();
  }

}














