package com.ge.apm.service.api;

import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.annotations.Column;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javaslang.*;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;
import org.apache.commons.math3.stat.regression.SimpleRegression;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

  public static Money predictRevenue() {
    return Option.of(LocalDate.now()).map(y -> CNY.money(2165.29491254D * ChronoUnit.DAYS.between(LocalDate.of(2000, 1, 1), y.plusYears(1)) + 1859154525.8260288D)).getOrElse(CNY.O);
  }



  //output:type,dept,month
  public static Seq<Tuple5<Option<Integer>, Option<Integer>, Option<Integer>, Option<Double>, Option<Double>>> parseJson(String s) {
    Config parsedBody = ConfigFactory.parseString(s);
    return List.ofAll(Try.of(() -> parsedBody.getConfigList("config")).get())
      .map(v -> Tuple.of(
        Option.of(Ints.tryParse(Try.of(() -> v.getString("type")).getOrElse(""))),
        Option.of(Ints.tryParse(Try.of(() -> v.getString("dept")).getOrElse(""))),
        Option.of(Ints.tryParse(Try.of(() -> v.getString("month")).getOrElse(""))),
        Option.of(Doubles.tryParse(Try.of(() -> v.getString("revenue_rate")).getOrElse(""))),
        Option.of(Doubles.tryParse(Try.of(() -> v.getString("cost_rate")).getOrElse("")))
      ));
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
        WHERE("ai.clinical_dept_id = " + String.format("%s", dept));
      }
      if (Option.of(month).filter(v -> v >= 1 && v <= 12).isDefined()) {
        WHERE("extract(month from asu.created) = " + String.format("%s", month));
      }
      if (Option.of(type).filter(v -> v >= 1).isDefined()) {
        WHERE("ai.asset_group = " + String.format("%s", type));
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
        WHERE("ai.clinical_dept_id = " + String.format("%s", dept));
      }
      if (Option.of(month).filter(v -> v >= 1 && v <= 12).isDefined()) {
        WHERE("extract(month from asu.created) = " + String.format("%s", month));
      }
      if (Option.of(type).filter(v -> v >= 1).isDefined()) {
        WHERE("ai.asset_group = " + String.format("%s", type));
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

  interface Asset {
    @Column
    int id();

    @Column
    String name();

    @Column
    Timestamp created_date();

    @Column
    int type();

    @Column
    int dept();

    @Column
    double revenue();

    @Column
    double costs();
  }

  public Observable<Tuple7<Integer, String, LocalDate, Integer, Integer, Double, Double>> findRvnCstForForecast(Integer siteId, Integer hospitalId, LocalDate startDate, LocalDate endDate) {
    log.info("siteId:{}, hospitalId:{}, startDate:{}, endDate:{}", siteId, hospitalId, startDate, endDate);
    return db.select(new SQL() {{
      SELECT("ai.id", "ai.name", "date_trunc(:time_unit,asu.created) as created_date", "ai.asset_group as type", "ai.clinical_dept_id as dept", "COALESCE(sum(asu.revenue), 0) as revenue", "COALESCE(sum(asu.maintenance_cost), 0) as costs");
      FROM("asset_info as ai");
      LEFT_OUTER_JOIN("asset_summit as asu on ai.id = asu.asset_id");
      WHERE("ai.site_id = :site_id");
      WHERE("ai.hospital_id = :hospital_id");
      WHERE("asu.created >= :start_day");
      WHERE("asu.created <= :end_day");
      WHERE("ai.is_valid = true");
      GROUP_BY("ai.id");
      GROUP_BY("created_date");
      ORDER_BY("ai.id");
      ORDER_BY("created_date");
    }}.toString())
      .parameter("start_day", Date.valueOf(startDate))
      .parameter("end_day", Date.valueOf(endDate))
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .parameter("time_unit", "month")
      .autoMap(Asset.class)
      .map(v -> Tuple.of(v.id(), v.name(), v.created_date().toLocalDateTime().toLocalDate(), v.type(), v.dept(), v.revenue(), v.costs()))
      .cache();
  }

  //forecast
  private Integer localDateToX(LocalDate input) {
    return (int) LocalDate.ofYearDay(2000, 1).until(input, ChronoUnit.DAYS);
  }

  private LocalDate xToLocaldate(Integer x) {
    return LocalDate.ofYearDay(2000, 1).plusDays(x);
  }

  private Seq<Tuple7<Integer, String, LocalDate, Integer, Integer, Double, Double>> dataTransform(Observable<Tuple7<Integer, String, LocalDate, Integer, Integer, Double, Double>> items) {
    return List.ofAll(items.toBlocking().toIterable());
  }

  //output:Map<type_id,Tuple7<id, name, time, type, dept, revenue_rate, cost_rate>>
  private javaslang.collection.Map<Integer, Seq<Tuple7<Integer, String, LocalDate, Integer, Integer, Double, Double>>> ratios(Seq<Tuple7<Integer, String, LocalDate, Integer, Integer, Double, Double>> items) {
    return
      items.filter(v -> LocalDate.of(LocalDate.now().getYear() - 1, 12, 1).equals(v._3))
        .groupBy(v -> v._4)
        .map((k, v) -> Tuple.of(Tuple.of(k, v.map(sub -> sub._6).sum().doubleValue(), v.map(sub -> sub._7).sum().doubleValue()), v))
        .map((k, v) -> Tuple.of(k._1, v.map(sub -> Tuple.of(sub._1, sub._2, sub._3, sub._4, sub._5, Option.when(k._2.equals(0D), 0D).getOrElse(sub._6 / k._2), Option.when(k._3.equals(0D), 0D).getOrElse(sub._7 / k._3)))));
  }

  //output: Tuple7<id, name, time, type, dept, revenue, cost>
  private Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> predictRvnCst(javaslang.collection.Map<Integer, Seq<Tuple7<Integer, String, LocalDate, Integer, Integer, Double, Double>>> ratios,
                                                                                                Seq<Tuple7<Integer, String, LocalDate, Integer, Integer, Double, Double>> items, int year) {
    javaslang.collection.Map<Integer, Tuple2<Seq<Tuple2<Integer, Double>>, Seq<Tuple2<Integer, Double>>>> predicts = HashMap.ofEntries(items.groupBy(v -> v._4)
      .map((k, v) -> Tuple.of(k,
        v.groupBy(sub -> sub._3).map(pair -> Tuple.of(localDateToX(pair._1), pair._2))
          .sortBy(sub -> sub._1)
          .map(sub -> Tuple.of(sub._1, sub._2.map(subV -> subV._6).sum().doubleValue(), sub._2.map(subV -> subV._7).sum().doubleValue()))
      )).map(v -> Tuple.of(v._1, Tuple.of(predictOneType(v._2.map(sub -> Tuple.of(sub._1, sub._2)), year), predictOneType(v._2.map(sub -> Tuple.of(sub._1, sub._3)), year)))));
    return ratios.map((k, v) -> Tuple.of(k, v.map(sub -> Tuple.of(sub._1, sub._2, sub._3.getMonthValue(), sub._4, sub._5,
      predicts.get(k).get()._1.map(monPre -> Tuple.of(monPre._1, monPre._2 * sub._6)),
      predicts.get(k).get()._2.map(monPre -> Tuple.of(monPre._1, monPre._2 * sub._7))))))
      .flatMap(v -> v._2)
      .flatMap(v -> v._6.zip(v._7).map(sub -> Tuple.of(v._1, v._2, xToLocaldate(sub._1._1).getMonthValue(), v._4, v._5, sub._1._2, sub._2._2)))
      .sortBy(v -> v._1 * 10000 + v._3);
  }

  //input: Historical Seq<Tuple2<id,revenue/cost>>for one type
  //output: Seq<Tuple2<id,revenue/cost>>for one type including future data
  private Seq<Tuple2<Integer, Double>> predictOneType(Seq<Tuple2<Integer, Double>> monthlyData, int year) {
    monthlyData = monthlyData.removeLast(v -> true);
    LocalDate lastHisDay = xToLocaldate(monthlyData.last()._1);
    for (int i = 0; i < (int) lastHisDay.until(LocalDate.now().withDayOfMonth(1).minusMonths(1), ChronoUnit.MONTHS); i++) {
      monthlyData = monthlyData.append(Tuple.of(localDateToX(lastHisDay.plusMonths(i + 1)), 0D));
    }
    SimpleRegression simpleRegression = new SimpleRegression();
    monthlyData.forEach(v -> simpleRegression.addData(v._1, v._2));
    lastHisDay = xToLocaldate(monthlyData.last()._1);
    for (int i = 0; i < (int) lastHisDay.until(LocalDate.of(LocalDate.now().getYear() + 1, 12, 1), ChronoUnit.MONTHS); i++) {
      monthlyData = monthlyData.append(Tuple.of(localDateToX(lastHisDay.plusMonths(i + 1)), simpleRegression.predict(localDateToX(lastHisDay.plusMonths(i + 1)))));
    }
    return monthlyData.filter(v -> xToLocaldate(v._1).getYear() == year);
  }

  @Cacheable(cacheNames = "springCache", key = "'profitService.predict'+#siteId+'.'+#hospitalId+'.startDate'+#startDate+'.endDate'+#endDate+'.year'+#year")
  public Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> predict(Integer siteId, Integer hospitalId, LocalDate startDate, LocalDate endDate, Integer year) {
    Seq<Tuple7<Integer, String, LocalDate, Integer, Integer, Double, Double>> items = dataTransform(findRvnCstForForecast(siteId, hospitalId, startDate, endDate));
    return predictRvnCst(ratios(items), items, year)
      .zip(getFutureDeprecation(siteId, hospitalId, year))
      .map(v -> Tuple.of(v._1._1, v._1._2, v._1._3, v._1._4, v._1._5, v._1._6, v._1._7 + v._2._3));
  }

  //for forecast rate
  @Cacheable(cacheNames = "springCache", key = "'profitService.lastYearData'+#siteId+'.'+#hospitalId")
  public Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> lastYearData(Integer siteId, Integer hospitalId) {
    return List.ofAll(findRvnCstForForecast(siteId, hospitalId, LocalDate.now().minusYears(1).withDayOfYear(1), LocalDate.now().withDayOfYear(1).minusDays(1))
      .map(v -> Tuple.of(v._1, v._2, v._3.getMonthValue(), v._4, v._5, v._6, v._7))
      .toBlocking().toIterable());
  }

  public Seq<Tuple3<Integer, Integer, Double>> getFutureDeprecation(Integer siteId, Integer hospitalId, Integer year) {
    return List.ofAll(
      db.select(new SQL() {{
        SELECT("index.id", "index.month", "COALESCE(sum(ad.deprecate_amount), 0) as deprecation");
        FROM("(Select* FROM ((SELECT id From asset_info ai where ai.site_id = :site_id and ai.hospital_id = :hospital_id) as ids cross join generate_series(1,12) as month)) as index");
        LEFT_OUTER_JOIN("asset_depreciation as ad on index.id=ad.asset_id and extract(month from ad.deprecate_date) = index.month and extract(year from ad.deprecate_date) = :year");
        GROUP_BY("index.id");
        GROUP_BY("index.month");
        ORDER_BY("index.id");
        ORDER_BY("index.month");
      }}.toString())
        .parameter("site_id", siteId)
        .parameter("hospital_id", hospitalId)
        .parameter("year", year)
        .getAs(Integer.class, Integer.class, Double.class)
        .map(tuple -> Tuple.of(tuple._1(), tuple._2(), tuple._3()))
        .toBlocking().toIterable()
    );
  }

}
