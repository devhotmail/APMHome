package com.ge.apm.service.api;

import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple4;
import javaslang.Tuple5;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDate;

@Service
public class ProfitService {
  private static final Logger log = LoggerFactory.getLogger(ProfitService.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  public static double sumRevenue(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._3)).toBlocking().single().getNumber().doubleValue();
  }

  public static double sumProfit(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._4)).toBlocking().single().getNumber().doubleValue();
  }

  public static Tuple2<String, String> descRevenues(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._3)).map(CNY::desc).toBlocking().single();
  }

  public static Tuple2<String, String> descProfits(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._4)).map(CNY::desc).toBlocking().single();
  }

  @Cacheable(cacheNames = "springCache", key = "'profitService.findRvnCstByYear.'+#siteId+'.'+#hospitalId + '.year'+#year")
  public Observable<Tuple5<Integer, String, Money, Money, Money>> findRvnCstByYear(int siteId, int hospitalId, int year) {
    return db
      .select(new SQL()
        .SELECT("ai.id", "ai.name", "COALESCE(sum(asu.revenue), 0)", "COALESCE(sum(asu.maintenance_cost), 0)", "COALESCE(sum(asu.deprecation_cost), 0)")
        .FROM("asset_info as ai")
        .INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("extract(year from asu.created) = :year")
        .GROUP_BY("ai.id")
        .ORDER_BY("ai.id")
        .toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, String.class, Double.class, Double.class, Double.class)
      .map(tuple5 -> Tuple.of(tuple5._1(), tuple5._2(), CNY.money(tuple5._3()), CNY.money(tuple5._4()), CNY.money(tuple5._5()))).cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'profitService.findRvnCstGroupBy.'+#siteId+'.'+#hospitalId + '.year'+#year + '.groupBy'+#groupBy")
  public Observable<Tuple4<Integer, Money, Money, Money>> findRvnCstGroupBy(int siteId, int hospitalId, int year, String groupBy) {
    return db
      .select(new SQL()
        .SELECT(String.format("%s as group_id", Option.when("dept".equals(groupBy), "ai.clinical_dept_id").getOrElse(Option.when("type".equals(groupBy), "ai.asset_group").getOrElse("extract(month from asu.created)"))),
          "COALESCE(sum(asu.revenue), 0)", "COALESCE(sum(asu.maintenance_cost), 0)", "COALESCE(sum(asu.deprecation_cost), 0)")
        .FROM("asset_info as ai")
        .INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("extract(year from asu.created) = :year")
        .GROUP_BY("group_id")
        .ORDER_BY("group_id")
        .toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class, Double.class, Double.class)
      .map(tuple4 -> Tuple.of(tuple4._1(), CNY.money(tuple4._2()), CNY.money(tuple4._3()), CNY.money(tuple4._4()))).cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'profitService.findRvnCstForEachType.'+#siteId+'.'+#hospitalId+'.groupBy'+#groupBy+'.groupId'+#groupId")
  public Observable<Tuple5<Integer, String, Money, Money, Money>> findRvnCstForEachType(int siteId, int hospitalId, int year, String groupBy, int groupId) {
    log.info("groupId:{}, groupBy:{}", groupId, groupBy);
    return db
      .select(new SQL()
        .SELECT("ai.id", "ai.name", "COALESCE(sum(asu.revenue), 0)", "COALESCE(sum(asu.maintenance_cost), 0)", "COALESCE(sum(asu.deprecation_cost), 0)")
        .FROM("asset_info as ai")
        .INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("extract(year from asu.created) = :year")
        .WHERE(String.format("%s = :group_id", Option.when("dept".equals(groupBy), "ai.clinical_dept_id").getOrElse(Option.when("type".equals(groupBy), "ai.asset_group").getOrElse("extract(month from asu.created)"))))
        .GROUP_BY("ai.id")
        .ORDER_BY("ai.id")
        .toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("group_id", groupId)
      .getAs(Integer.class, String.class, Double.class, Double.class, Double.class)
      .map(tuple5 -> Tuple.of(tuple5._1(), tuple5._2(), CNY.money(tuple5._3()), CNY.money(tuple5._4()), CNY.money(tuple5._5()))).cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'profitService.findRvnCstForEachItem.'+#siteId+'.'+#hospitalId+'.endDate'+#endDate+'.startDate'+#startDate+'.dept'+#dept+'.month'+#month+'.type'+#type")
  public Observable<Tuple5<Integer, String, Money, Money, Money>> findRvnCstForEachItem(Integer siteId, Integer hospitalId, LocalDate startDate, LocalDate endDate, Integer dept, Integer month, Integer type) {
    log.info("siteId:{}, hospitalId:{}, startDate:{}, endDate:{}, dept:{}, month:{}, type:{}", siteId, hospitalId, startDate, endDate, dept, month, type);
    return db.select(new SQL() {{
      SELECT("ai.id", "ai.name", "COALESCE(sum(asu.revenue), 0)", "COALESCE(sum(asu.maintenance_cost), 0)", "COALESCE(sum(asu.deprecation_cost), 0)");
      FROM("asset_info as ai");
      INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id");
      WHERE("ai.site_id = :site_id");
      WHERE("ai.hospital_id = :hospital_id");
      WHERE("asu.created >= :start_day");
      WHERE("asu.created <= :end_day");
      if (Option.of(dept).filter(v -> v >= 1).isDefined()) {
        WHERE("ai.clinical_dept_id = "+ String.format("%s", dept));
      }
      if (Option.of(month).filter(v -> v >= 1 && v <= 12).isDefined()) {
        WHERE("extract(month from asu.created) = "+ String.format("%s", month));
      }
      if (Option.of(type).filter(v -> v >= 1).isDefined()) {
        WHERE("ai.asset_group = "+ String.format("%s", type));
      }
      GROUP_BY("ai.id");
      ORDER_BY("ai.id");
    }}.toString())
      .parameter("start_day", Date.valueOf(startDate))
      .parameter("end_day", Date.valueOf(endDate))
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .getAs(Integer.class, String.class, Double.class, Double.class, Double.class)
      .map(tuple5 -> Tuple.of(tuple5._1(), tuple5._2(), CNY.money(tuple5._3()), CNY.money(tuple5._4()), CNY.money(tuple5._5()))).cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'profitService.findRvnCstForEachGroup.'+#siteId+'.'+#hospitalId+'.endDate'+#endDate+'.startDate'+#startDate+'.groupBy'+#groupBy+'.dept'+#dept+'.month'+#month+'.type'+#type")
  public Observable<Tuple4<Integer, Money, Money, Money>> findRvnCstForEachGroup(Integer siteId, Integer hospitalId, LocalDate startDate, LocalDate endDate, String groupBy, Integer dept, Integer month, Integer type) {
    log.info("siteId:{}, hospitalId:{}, startDate:{}, endDate:{}, groupBy:{}, dept:{}, month:{}, type:{}", siteId, hospitalId, startDate, endDate, groupBy, dept, month, type);
    return db.select(new SQL() {{
      SELECT(String.format("%s as group_id", Option.when("dept".equals(groupBy), "ai.clinical_dept_id").getOrElse(Option.when("type".equals(groupBy), "ai.asset_group").getOrElse("extract(month from asu.created)"))),
        "COALESCE(sum(asu.revenue), 0)", "COALESCE(sum(asu.maintenance_cost), 0)", "COALESCE(sum(asu.deprecation_cost), 0)");
      FROM("asset_info as ai");
      INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id");
      WHERE("ai.site_id = :site_id");
      WHERE("ai.hospital_id = :hospital_id");
      WHERE("asu.created >= :start_day");
      WHERE("asu.created <= :end_day");
      if (Option.of(dept).filter(v -> v >= 1).isDefined()) {
        WHERE("ai.clinical_dept_id = "+ String.format("%s", dept));
      }
      if (Option.of(month).filter(v -> v >= 1 && v <= 12).isDefined()) {
        WHERE("extract(month from asu.created) = "+ String.format("%s", month));
      }
      if (Option.of(type).filter(v -> v >= 1).isDefined()) {
        WHERE("ai.asset_group = "+ String.format("%s", type));
      }
      GROUP_BY("group_id");
      ORDER_BY("group_id");
    }}.toString())
      .parameter("start_day", Date.valueOf(startDate))
      .parameter("end_day", Date.valueOf(endDate))
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .getAs(Integer.class, Double.class, Double.class, Double.class)
      .map(tuple4 -> Tuple.of(tuple4._1(), CNY.money(tuple4._2()), CNY.money(tuple4._3()), CNY.money(tuple4._4()))).cache();
  }

}
