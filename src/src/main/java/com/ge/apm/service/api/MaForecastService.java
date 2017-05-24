package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple6;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDate;

@Service
public class MaForecastService extends CommonForecastService {
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  /**
   * return costs used for forecast
   *
   * @param siteId     siteId
   * @param hospitalId hospitalId
   * @param startDate  startDate
   * @param endDate    endDate
   * @return first Tuple6 are dimensions: id, name, year_month, dept, type, supplier
   * second Tuple6 are measurements: price, onrate, mt_manpower, mt_accessory, pm_manpower, pm_accessory
   */
  private Observable<Tuple2<Tuple6<Integer, String, LocalDate, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>>
  findDataForForecast(Integer siteId, Integer hospitalId, Date startDate, Date endDate) {
    String sql = new SQL() {{
      SELECT("ai.id", "ai.name", "year_month", "dept", "type", "supplier", "COALESCE(ai.purchase_price,0) as price",
        "down_rate", "mt_manpower", "mt_accessory", "pm_manpower", "pm_accessory");
      FROM("asset_info ai");
      INNER_JOIN("(" + new SQL() {{
        SELECT("distinct asset_id", "date_trunc(:time_unit,asu.created) as year_month",
          "dept_id as dept", "asset_group as type", "supplier_id as supplier", "COALESCE(AVG(asu.down_time),0)/86400 as down_rate",
          "COALESCE(AVG(asu.mt_manpower),0) as mt_manpower", "COALESCE(AVG(asu.mt_accessory),0) as mt_accessory",
          "COALESCE(AVG(asu.pm_manpower),0) as pm_manpower", "COALESCE(AVG(asu.pm_accessory),0) as pm_accessory");
        FROM("asset_summit asu");
        WHERE("hospital_id = :hospital_id");
        WHERE("site_id = :site_id");
        WHERE("created >= :start_day");
        WHERE("created <= :end_day");
      }}.toString() + ") right_table on ai.id = right_table.asset_id");
      ORDER_BY("ai.id");
      ORDER_BY("year_month");
    }}.toString();
    return db.select(sql)
      .parameter("hospital_id", hospitalId)
      .parameter("site_id", siteId)
      .parameter("start_day", startDate)
      .parameter("end_day", endDate)
      .get(rs -> Tuple.of(
        Tuple.of(rs.getInt("id"), rs.getString("name"), rs.getTimestamp("year_month").toLocalDateTime().toLocalDate(),
          rs.getInt("dept"), rs.getInt("type"), rs.getInt("supplier")),
        Tuple.of(rs.getDouble("price"), 1D - rs.getDouble("down_rate"), rs.getDouble("mt_manpower"),
          rs.getDouble("mt_accessory"), rs.getDouble("pm_manpower"), rs.getDouble("pm_accessory"))));
  }


}
