package com.get.apm.api.ut;

import au.com.bytecode.opencsv.CSV;
import com.ge.apm.service.api.ProfitService;
import com.ge.apm.service.utils.CNY;
import javaslang.*;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.assertj.core.api.Assertions;
import org.javamoney.moneta.Money;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import rx.observables.MathObservable;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Ignore
public class ProfitForecastDbTest extends AbstractDbTest {
  @Test
  public void testPredictRevenue() {
    Assertions.assertThat(Option.of(LocalDate.parse("2017-12-31")).map(y -> CNY.money(15284.13D * ChronoUnit.DAYS.between(LocalDate.of(2000, 1, 1), y) - 70223735.95D)).getOrElse(CNY.O).getNumber().longValue()).isEqualTo(Double.valueOf(30254134.67D).longValue());
    Assertions.assertThat(ProfitService.predictRevenue()).isGreaterThan(CNY.money(30254134.67D));
  }

  private Observable<Tuple4<Integer, String, Money, Money>> findRvnCstByYear(int year) {
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
      .parameter("site_id", 1).parameter("hospital_id", 1).parameter("year", year)
      .getAs(Integer.class, String.class, Double.class, Double.class, Double.class)
      .map(tuple5 -> Tuple.of(tuple5._1(), tuple5._2(), CNY.money(tuple5._3()), CNY.money(tuple5._4()), CNY.money(tuple5._5())))
      .map(v -> Tuple.of(v._1, v._2, v._3, v._4.add(v._5)))
      .filter(t -> Option.of(t._2).isDefined()).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private Observable<Tuple2<Money, Money>> sumRvnCstByYear(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return Observable.zip(MathObservable.from(items).sumDouble(t -> t._3.getNumber().doubleValue()), MathObservable.from(items).sumDouble(t -> t._4.getNumber().doubleValue()), (r, c) -> Tuple.of(CNY.money(r), CNY.money(c)));
  }


  private Observable<Tuple5<LocalDate, Integer, String, Money, Money>> findAnnualRvnCst(String unit, LocalDate from, LocalDate to) {
    return db
      .select(new SQL()
        .SELECT("date_trunc(:time_unit,asu.created) as created_date", "ai.id as asset_id", "ai.name as asset_name", "COALESCE(sum(asu.revenue), 0) as asset_revenue", "COALESCE(sum(asu.maintenance_cost), 0) as maint_cost", "COALESCE(sum(asu.deprecation_cost), 0) as dep_cost")
        .FROM("asset_info as ai")
        .INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("asu.created between :from and :to")
        .GROUP_BY("created_date")
        .GROUP_BY("ai.id")
        .GROUP_BY("ai.name")
        .ORDER_BY("created_date")
        .ORDER_BY("ai.id")
        .toString())
      .parameter("time_unit", unit).parameter("site_id", 1).parameter("hospital_id", 1).parameter("from", from).parameter("to", to)
      .getAs(Timestamp.class, Integer.class, String.class, Double.class, Double.class, Double.class)
      .map(t -> Tuple.of(t._1().toLocalDateTime().toLocalDate(), t._2(), t._3(), CNY.money(t._4()), CNY.money(t._5() + t._6())))
      .filter(t -> Option.of(t._4).isDefined()).sorted((l, r) -> r._4.getNumber().intValue() - l._4.getNumber().intValue()).cache();
  }

  private Seq<Tuple3<LocalDate, Money, Money>> sumRvnCst(Observable<Tuple5<LocalDate, Integer, String, Money, Money>> items) {
    return List.ofAll(items.toBlocking().toIterable()).groupBy(t -> t._1).mapValues(l -> Tuple.of(CNY.money(l.map(t -> t._4.getNumber().doubleValue()).sum()), CNY.money(l.map(t -> t._5.getNumber().doubleValue()).sum())))
      .map(kv -> Tuple.of(kv._1, kv._2._1, kv._2._2));
  }

  @Test
  public void testFind() {
    findRvnCstByYear(2016).subscribe(t -> System.out.println(t));
    sumRvnCstByYear(findRvnCstByYear(2016)).subscribe(t -> System.out.println(t));
  }

  @Test
  public void testFindAnnualRvnCst() {
    //findAnnualRvnCst("year", LocalDate.now().minusYears(3), LocalDate.now()).subscribe(t -> System.out.println(t));
    System.out.println(sumRvnCst(findAnnualRvnCst("year", LocalDate.now().minusYears(3), LocalDate.now())).toJavaList());
    System.out.println(sumRvnCst(findAnnualRvnCst("month", LocalDate.now().minusYears(3), LocalDate.now())).toJavaList());
  }

  @Test
  public void testWriteCsv() {
    CSV.create().write(new File("./annual.csv"), w -> w.writeAll(sumRvnCst(findAnnualRvnCst("year", LocalDate.now().minusYears(3), LocalDate.now())).map(t -> new String[]{t._1.toString(), CNY.format(t._2), CNY.format(t._3)}).toJavaList()));
    CSV.create().write(new File("./monthly.csv"), w -> w.writeAll(sumRvnCst(findAnnualRvnCst("month", LocalDate.now().minusYears(3), LocalDate.now())).map(t -> new String[]{t._1.toString(), CNY.format(t._2), CNY.format(t._3)}).toJavaList()));
    CSV.create().write(new File("./daily.csv"), w -> w.writeAll(sumRvnCst(findAnnualRvnCst("day", LocalDate.now().minusYears(3), LocalDate.now())).map(t -> new String[]{t._1.toString(), CNY.format(t._2), CNY.format(t._3)}).toJavaList()));
  }
}
