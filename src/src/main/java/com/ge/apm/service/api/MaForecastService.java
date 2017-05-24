package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.*;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
  private Seq<Tuple2<Tuple6<Integer, String, LocalDate, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>>
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
    return List.ofAll(db.select(sql)
      .parameter("hospital_id", hospitalId)
      .parameter("site_id", siteId)
      .parameter("start_day", startDate)
      .parameter("end_day", endDate)
      .get(rs -> Tuple.of(
        Tuple.of(rs.getInt("id"), rs.getString("name"), rs.getTimestamp("year_month").toLocalDateTime().toLocalDate(),
          rs.getInt("dept"), rs.getInt("type"), rs.getInt("supplier")),
        Tuple.of(rs.getDouble("price"), 1D - rs.getDouble("down_rate"), rs.getDouble("mt_manpower"),
          rs.getDouble("mt_accessory"), rs.getDouble("pm_manpower"), rs.getDouble("pm_accessory"))))
      .toBlocking().toIterable());
  }


  private Map<Integer, Seq<Tuple2<Tuple6<Integer, String, LocalDate, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>>> ratios
    (Seq<Tuple2<Tuple6<Integer, String, LocalDate, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>> items) {
    return items.filter(v -> LocalDate.of(LocalDate.now().getYear() - 1, 12, 1).equals(v._1._3))
      .groupBy(v -> v._1._5)
      .map((k, v) -> Tuple.of(Tuple.of(k, v.map(sub -> sub._2._2).sum().doubleValue(),
        v.map(sub -> sub._2._3).sum().doubleValue(),
        v.map(sub -> sub._2._4).sum().doubleValue(),
        v.map(sub -> sub._2._5).sum().doubleValue(),
        v.map(sub -> sub._2._6).sum().doubleValue()), v))
      .map((k, v) -> Tuple.of(k._1, v.map(sub -> Tuple.of(sub._1, Tuple.of(sub._2._1,
        Option.when(k._2.equals(0D), 0D).getOrElse(sub._2._2 / k._2),
        Option.when(k._3.equals(0D), 0D).getOrElse(sub._2._3 / k._3),
        Option.when(k._4.equals(0D), 0D).getOrElse(sub._2._4 / k._4),
        Option.when(k._5.equals(0D), 0D).getOrElse(sub._2._5 / k._5),
        Option.when(k._6.equals(0D), 0D).getOrElse(sub._2._6 / k._6))
      ))));
  }

  /**
   * predict future costs, get the predicted data for each asset each month
   *
   * @param ratios ratios based on Dec. data last year. Used to do mapping
   * @param items  single asset data
   * @param year   this year or next year
   * @return first Tuple6 are dimensions: id, name, month, dept, type, supplier
   * second Tuple6 are measurements: price, onrate, mt_manpower, mt_accessory, pm_manpower, pm_accessory
   */
  private Seq<Tuple2<Tuple6<Integer, String, Integer, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>>
  predictCosts(Map<Integer, Seq<Tuple2<Tuple6<Integer, String, LocalDate, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>>> ratios,
               Seq<Tuple2<Tuple6<Integer, String, LocalDate, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>> items,
               int year) {
    Map<Integer, Tuple5<Seq<Tuple2<Integer, Double>>, Seq<Tuple2<Integer, Double>>, Seq<Tuple2<Integer, Double>>, Seq<Tuple2<Integer, Double>>, Seq<Tuple2<Integer, Double>>>>
      predicts = HashMap.ofEntries(items.groupBy(v -> v._1._5)
      .map((k, v) -> Tuple.of(k,
        v.groupBy(sub -> sub._1._3).map(pair -> Tuple.of(localDateToX(pair._1), pair._2))
          .sortBy(sub -> sub._1)
          .map(sub -> Tuple.of(sub._1, sub._2.map(subV -> subV._2._2).sum().doubleValue(), sub._2.map(subV -> subV._2._3).sum().doubleValue(),
            sub._2.map(subV -> subV._2._4).sum().doubleValue(), sub._2.map(subV -> subV._2._5).sum().doubleValue(),
            sub._2.map(subV -> subV._2._6).sum().doubleValue()))
      )).map(v -> Tuple.of(v._1, Tuple.of(predictOneType(v._2.map(sub -> Tuple.of(sub._1, sub._2)), year),
        predictOneType(v._2.map(sub -> Tuple.of(sub._1, sub._3)), year),
        predictOneType(v._2.map(sub -> Tuple.of(sub._1, sub._4)), year),
        predictOneType(v._2.map(sub -> Tuple.of(sub._1, sub._5)), year),
        predictOneType(v._2.map(sub -> Tuple.of(sub._1, sub._6)), year)
      ))));
    return ratios.map((k, v) -> Tuple.of(k, v.map(sub -> Tuple.of(sub._1,
      Tuple.of(sub._2._1,
        predicts.get(k).get()._1.map(monPre -> Tuple.of(monPre._1, monPre._2 * sub._2._2)),
        predicts.get(k).get()._2.map(monPre -> Tuple.of(monPre._1, monPre._2 * sub._2._3)),
        predicts.get(k).get()._3.map(monPre -> Tuple.of(monPre._1, monPre._2 * sub._2._4)),
        predicts.get(k).get()._4.map(monPre -> Tuple.of(monPre._1, monPre._2 * sub._2._5)),
        predicts.get(k).get()._5.map(monPre -> Tuple.of(monPre._1, monPre._2 * sub._2._6)))))))
      .flatMap(v -> v._2)
      .flatMap(v -> v._2._2.zip(v._2._3).zip(v._2._4).zip(v._2._5).zip(v._2._6)
        .map(sub -> Tuple.of(Tuple.of(v._1._1, v._1._2, xToLocaldate(sub._2._1).getMonthValue(), v._1._4, v._1._5, v._1._6),
          Tuple.of(v._2._1, sub._1._1._1._1._2, sub._1._1._1._2._2, sub._1._1._2._2, sub._1._2._2, sub._2._2))))
      .map(v -> Tuple.of(v._1, Tuple.of(v._2._1, v._2._2,
        v._2._3 * (int) LocalDate.of(year, v._1._3, 1).until(LocalDate.of(year, v._1._3, 1).plusMonths(1), ChronoUnit.DAYS),
        v._2._4 * (int) LocalDate.of(year, v._1._3, 1).until(LocalDate.of(year, v._1._3, 1).plusMonths(1), ChronoUnit.DAYS),
        v._2._5 * (int) LocalDate.of(year, v._1._3, 1).until(LocalDate.of(year, v._1._3, 1).plusMonths(1), ChronoUnit.DAYS),
        v._2._6 * (int) LocalDate.of(year, v._1._3, 1).until(LocalDate.of(year, v._1._3, 1).plusMonths(1), ChronoUnit.DAYS))))
      .sortBy(v -> v._1._1 * 10000 + v._1._3);
  }

  /**
   * predict future costs, get the predicted data for each asset each month. this function can be used by MaApi and the result can be cached
   *
   * @param siteId     siteId
   * @param hospitalId hospitalId
   * @param startDate  startDate of historical data used for predicting
   * @param endDate    endDate of historical data used for predicting
   * @param year       this year or next year
   * @return first Tuple6 are dimensions: id, name, month, dept, type, supplier
   * second Tuple6 are measurements: price, onrate, mt_manpower, mt_accessory, pm_manpower, pm_accessory
   */
  @Cacheable(cacheNames = "springCache", key = "'maService.predict'+#siteId+'.'+#hospitalId+'.startDate'+#startDate+'.endDate'+#endDate+'.year'+#year")
  public Seq<Tuple2<Tuple6<Integer, String, Integer, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>>
  predict(Integer siteId, Integer hospitalId, Date startDate, Date endDate, Integer year) {
    Seq<Tuple2<Tuple6<Integer, String, LocalDate, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>>
      items = findDataForForecast(siteId, hospitalId, startDate, endDate);
    return predictCosts(ratios(items), items, year);
  }

  /**
   * Another function that can be used by MaApi. this function is used to get data of last year. The data is used to calculate increase rate of measurements
   *
   * @param siteId     siteId
   * @param hospitalId hospitalId
   * @return first Tuple6 are dimensions: id, name, month, dept, type, supplier
   * second Tuple6 are measurements: price, onrate, mt_manpower, mt_accessory, pm_manpower, pm_accessory
   */
  @Cacheable(cacheNames = "springCache", key = "'maService.lastYearData'+#siteId+'.'+#hospitalId")
  public Seq<Tuple2<Tuple6<Integer, String, Integer, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>>
  lastYearData(Integer siteId, Integer hospitalId) {
    return findDataForForecast(siteId, hospitalId, Date.valueOf(LocalDate.now().minusYears(1).withDayOfYear(1)), Date.valueOf(LocalDate.now().withDayOfYear(1).minusDays(1)))
      .map(v -> Tuple.of(Tuple.of(v._1._1, v._1._2, localDateToX(v._1._3), v._1._4, v._1._5, v._1._6), v._2));
  }

  /**
   * a virtual sql based on predicted data. this virtual sql return data for each asset
   * Do not cache the result of this function. If you really want to do that, put siteId and hospitalId into your cache key, or you'll get logical error
   *
   * @param dept     dept
   * @param type     type
   * @param supplier supplier
   * @param rltGrp   rltGrp
   * @param items    first Tuple6 are dimensions: id, name, month, dept, type, supplier
   *                 second Tuple6 are measurements: price, onrate, mt_manpower, mt_accessory, pm_manpower, pm_accessory
   * @return first Tuple5 are dimensions: id, name, dept, type, supplier
   * second Tuple4 are measurements: price, onrate, labor/repair,parts/PM
   */
  public static Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> virtualSqlItems
  (Integer dept, Integer type, Integer supplier, String rltGrp,
   Seq<Tuple2<Tuple6<Integer, String, Integer, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>> items) {
    return items.filter(v -> Option.of(dept).map(sub -> sub.equals(v._1._4)).getOrElse(true)
      && Option.of(type).map(sub -> sub.equals(v._1._5)).getOrElse(true)
      && Option.of(supplier).map(sub -> sub.equals(v._1._6)).getOrElse(true))
      .groupBy(v -> v._1._1)
      .map((k, v) -> Tuple.of(Tuple.of(k, v.get(0)._1._2, v.get(0)._1._4, v.get(0)._1._5, v.get(0)._1._6),
        Tuple.of(v.get(0)._2._1, v.map(sub -> sub._2._2).average().getOrElse(0D), v.map(sub -> sub._2._3).sum().doubleValue(),
          v.map(sub -> sub._2._4).sum().doubleValue(), v.map(sub -> sub._2._5).sum().doubleValue(), v.map(sub -> sub._2._6).sum().doubleValue())))
      .map(v -> Tuple.of(v._1, Tuple.of(v._2._1, v._2._2,
        "acyman".equals(rltGrp) ? v._2._3 + v._2._5 : v._2._3 + v._2._4,
        "acyman".equals(rltGrp) ? v._2._4 + v._2._6 : v._2._5 + v._2._6)))
      .sortBy(v -> v._1._1);
  }

  public static Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>>virtualSqlSingle
    (Integer id,String rltGrp,
     Seq<Tuple2<Tuple6<Integer, String, Integer, Integer, Integer, Integer>, Tuple6<Double, Double, Double, Double, Double, Double>>> items){
    return items.filter(v->v._1._1.equals(id))
      .groupBy(v -> v._1._1)
      .map((k, v) -> Tuple.of(Tuple.of(k, v.get(0)._1._2, v.get(0)._1._4, v.get(0)._1._5, v.get(0)._1._6),
        Tuple.of(v.get(0)._2._1, v.map(sub -> sub._2._2).average().getOrElse(0D), v.map(sub -> sub._2._3).sum().doubleValue(),
          v.map(sub -> sub._2._4).sum().doubleValue(), v.map(sub -> sub._2._5).sum().doubleValue(), v.map(sub -> sub._2._6).sum().doubleValue())))
      .map(v -> Tuple.of(v._1, Tuple.of(v._2._1, v._2._2,
        "acyman".equals(rltGrp) ? v._2._3 + v._2._5 : v._2._3 + v._2._4,
        "acyman".equals(rltGrp) ? v._2._4 + v._2._6 : v._2._5 + v._2._6)));
  }

  /**
   * ajust the predicted result with user's assumption
   *
   * @param future  predicted data by system
   * @param history data of last year
   * @param onrate  onrate
   * @param cost1   labor/repair
   * @param cost2   parts/PM
   * @return first Tuple5 are dimensions: id, name, dept, type, supplier
   * second Tuple4 are measurements: price, onrate, labor/repair, parts/PM
   */
  public static Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> userPredict
  (Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> future,
   Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> history,
   java.util.Map<Integer, Double> onrate, java.util.Map<Integer, Double> cost1, java.util.Map<Integer, Double> cost2) {
    return future.zip(history)
      .map(v -> Tuple.of(v._1._1, Tuple.of(v._1._2._1,
        Option.of(onrate.get(v._1._1._1)).map(sub -> v._2._2._2 * (1D + sub)).getOrElse(v._1._2._2),
        Option.of(cost1.get(v._1._1._1)).map(sub -> v._2._2._3 * (1D + sub)).getOrElse(v._1._2._3),
        Option.of(cost2.get(v._1._1._1)).map(sub -> v._2._2._4 * (1D + sub)).getOrElse(v._1._2._4)
      )));
  }

  /**
   * ajust the predicted result with user's assumption and return the result in the form of increase rate
   *
   * @param future  predicted data by system
   * @param history data of last year
   * @param onrate  onrate
   * @param cost1   labor/repair
   * @param cost2   parts/PM
   * @return first Tuple5 are dimensions: id, name, dept, type, supplier
   * second Tuple4 are measurements: price, onrate_increase, labor/repair_increase, parts/PM_increase
   */
  public static Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> getForecastRate
  (Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> future,
   Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> history,
   java.util.Map<Integer, Double> onrate, java.util.Map<Integer, Double> cost1, java.util.Map<Integer, Double> cost2) {
    return future.zip(history)
      .map(v -> Tuple.of(v._1._1, Tuple.of(v._1._2._1,
        Option.of(onrate.get(v._1._1._1)).getOrElse(Option.when(v._2._2._2.equals(0D), 0D).getOrElse(v._1._2._2 / v._2._2._2 - 1D)),
        Option.of(cost1.get(v._1._1._1)).getOrElse(Option.when(v._2._2._3.equals(0D), 0D).getOrElse(v._1._2._3 / v._2._2._3 - 1D)),
        Option.of(cost2.get(v._1._1._1)).getOrElse(Option.when(v._2._2._4.equals(0D), 0D).getOrElse(v._1._2._4 / v._2._2._4 - 1D))
      )));
  }

  public static boolean sugLowBound(Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>> asset, Double lowBound) {
    return asset._2._2 < lowBound;
  }

  public static boolean sugHighCost(Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>> asset, Double rate) {
    return asset._2._3 + asset._2._4 >= rate * asset._2._1;
  }

  public static boolean sugBindOnrateCost(Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>> asset,
                                          Double lowBound, Double highBound, Double rate, Double accuracy) {
    return asset._2._2 >= lowBound && asset._2._2 < highBound
      && asset._2._3 + asset._2._4 >= (rate - accuracy) * asset._2._1 && asset._2._3 + asset._2._4 < (rate + accuracy) * asset._2._1;
  }

  /**
   * a virtual sql based on the result of {@code virtualSqlItems()}. this virtual sql return data for each group
   * Do not cache the result of this function. If you really want to do that, put siteId and hospitalId into your cache key, or you'll get logical error
   *
   * @param groupBy groupBy
   * @param items   first Tuple5 are dimensions: id, name, dept, type, supplier
   *                second Tuple4 are measurements: price, onrate, labor/repair,parts/PM
   * @return 1st param: group_id
   * Tuple3: onrate, labor/repair,parts/PM
   */
  public static Seq<Tuple2<Integer, Tuple3<Double, Double, Double>>> virtualSqlGroups
  (String groupBy,
   Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> items) {
    return items.groupBy(v -> "dept".equals(groupBy) ? v._1._3 : ("type".equals(groupBy) ? v._1._4 : v._1._5))
      .map(v -> Tuple.of(v._1, Tuple.of(v._2.map(sub -> sub._2._2).average().getOrElse(0D),
        v._2.map(sub -> sub._2._3).sum().doubleValue(), v._2.map(sub -> sub._2._4).sum().doubleValue())));
  }

}
