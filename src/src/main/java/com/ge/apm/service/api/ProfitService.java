package com.ge.apm.service.api;

import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.Tuple4;
import org.apache.ibatis.jdbc.SQL;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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

  public Observable<Tuple2<Integer, Money>> findRevenuesGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ai.asset_group as asset_group, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ar.site_id = :site_id and ar.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.asset_group order by asset_group")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2()))).cache();
  }

  public Observable<Tuple2<Integer, Money>> findDeprecationsGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ag.asset_group as asset_group, COALESCE(am.deprecate_amount, 0) as asset_deprecate from (select distinct ai.asset_group from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id) as ag left join (select ai.asset_group, sum(ad.deprecate_amount) as deprecate_amount from asset_depreciation ad join asset_info ai on ad.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ad.deprecate_date) = :year group by ai.asset_group) as am on ag.asset_group = am.asset_group order by ag.asset_group")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2()))).cache();
  }

  public Observable<Tuple2<Integer, Money>> findCostsGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ag.asset_group as asset_group, COALESCE(am.cost, 0) as asset_cost from (select distinct ai.asset_group from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id) as ag left join (select ai.asset_group, sum(wo.total_price) as cost from work_order wo join asset_info ai on wo.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from wo.request_time) = :year and wo.is_closed = true group by ai.asset_group) as am on ag.asset_group = am.asset_group order by ag.asset_group")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findRevenuesByType(int siteId, int hospitalId, int year, int type) {
    return db.select("select ai.id as asset_id, ai.name as asset_name, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.asset_group = :asset_group and extract(year from ar.exam_date) = :year group by ai.id order by ai.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("asset_group", type)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findDeprecationsByType(int siteId, int hospitalId, int year, int type) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.asset_deprecate,0) as deprecation from (select distinct ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.asset_group = :asset_group) as at left join (select ad.asset_id, sum(ad.deprecate_amount) as asset_deprecate from asset_depreciation ad where ad.site_id = :site_id and extract(year from ad.deprecate_date) = :year group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("asset_group", type)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())))
      .cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findCostsByType(int siteId, int hospitalId, int year, int type) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.cost,0) as asset_cost from (select distinct ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.asset_group = :asset_group) as at left join (select asset_id, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year group by asset_id) am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("asset_group", type)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple2<Integer, Money>> findRevenuesGroupByMonth(int siteId, int hospitalId, int year) {
    return db.select("select pl.month, COALESCE(ar.revenue,0) from (select generate_series(1,12) as month) as pl left join (select extract(month from exam_date) as month, sum(price_amount) as revenue from asset_clinical_record where site_id = :site_id and hospital_id = :hospital_id and extract(year from exam_date) = :year group by month) as ar on pl.month = ar.month order by pl.month")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple -> Tuple.of(tuple._1(), CNY.money(tuple._2()))).cache();
  }

  public Observable<Tuple2<Integer, Money>> findDeprecationsGroupByMonth(int siteId, int hospitalId, int year) {
    return db.select("select pl.month, COALESCE(ad.deprecation,0) from (select generate_series(1,12) as month) as pl left join (select extract(month from deprecate_date) as month, sum(deprecate_amount) as deprecation from asset_depreciation where site_id = :site_id and extract(year from deprecate_date) = :year group by month) as ad on pl.month = ad.month order by pl.month")
      .parameter("site_id", siteId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple -> Tuple.of(tuple._1(), CNY.money(tuple._2()))).cache();
  }

  public Observable<Tuple2<Integer, Money>> findCostsGroupByMonth(int siteId, int hospitalId, int year) {
    return db.select("select pl.month, COALESCE(wo.cost,0) from (select generate_series(1,12) as month) as pl left join (select extract(month from request_time) as month, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year group by month) as wo on pl.month = wo.month order by pl.month")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple -> Tuple.of(tuple._1(), CNY.money(tuple._2()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findRevenuesByMonth(int siteId, int hospitalId, int year, int month) {
    return db.select("select ai.id, ai.name, COALESCE(sum(ar.price_amount),0) from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year and extract(month from ar.exam_date) = :month group by ai.id order by ai.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("month", month)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findDeprecationsByMonth(int siteId, int hospitalId, int year, int month) {
    return db.select("select at.id, at.name, COALESCE(am.asset_deprecate,0) as asset_deprecate from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year and extract(month from ar.exam_date) = :month group by ai.id order by ai.id) as at left join (select ad.asset_id, sum(ad.deprecate_amount) as asset_deprecate from asset_depreciation ad where ad.site_id = :site_id and extract(year from ad.deprecate_date) = :year and extract(month from ad.deprecate_date) = :month group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("month", month)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findCostsByMonth(int siteId, int hospitalId, int year, int month) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.cost,0) as asset_cost from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year and extract(month from ar.exam_date) = :month group by ai.id order by ai.id) as at left join (select asset_id, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year and extract(month from request_time) = :month group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("month", month)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple2<Integer, Money>> findRevenuesGroupByDept(int siteId, int hospitalId, int year) {
    return db.select("select ai.clinical_dept_id as dept_id, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.clinical_dept_id order by clinical_dept_id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())))
      .cache();
  }

  public Observable<Tuple2<Integer, Money>> findDeprecationsGroupByDept(int siteId, int hospitalId, int year) {
    return db.select("select at.dept_id, COALESCE(am.asset_deprecate,0) as deprecation from (select ai.clinical_dept_id as dept_id from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.clinical_dept_id order by clinical_dept_id) at left join (select ai.clinical_dept_id as dept_id, sum(ad.deprecate_amount) as asset_deprecate from asset_info ai join asset_depreciation ad on ai.id = ad.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ad.deprecate_date) = :year group by dept_id) am on at.dept_id = am.dept_id order by at.dept_id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2()))).cache();
  }

  public Observable<Tuple2<Integer, Money>> findCostsGroupByDept(int siteId, int hospitalId, int year) {
    return db.select("select at.dept_id, COALESCE(am.cost, 0) as asset_cost from (select ai.clinical_dept_id as dept_id from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.clinical_dept_id order by clinical_dept_id) at left join (select ai.clinical_dept_id as dept_id, sum(wo.total_price) as cost from asset_info ai join work_order wo on ai.id = wo.asset_id where wo.site_id = :site_id and wo.hospital_id = :hospital_id and extract(year from wo.request_time) = :year and wo.is_closed = true group by dept_id) am on at.dept_id = am.dept_id order by at.dept_id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findRevenuesByDept(int siteId, int hospitalId, int year, int dept) {
    return db.select("select ai.id, ai.name, COALESCE(sum(ar.price_amount),0) from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.clinical_dept_id= :dept and extract(year from ar.exam_date) = :year group by ai.id order by ai.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("dept", dept)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findDeprecationsByDept(int siteId, int hospitalId, int year, int dept) {
    return db.select("select at.id, at.name, COALESCE(am.asset_deprecate,0) as asset_deprecate from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.clinical_dept_id= :dept and extract(year from ar.exam_date) = :year group by ai.id order by ai.id) as at left join (select ad.asset_id, sum(ad.deprecate_amount) as asset_deprecate from asset_depreciation ad where ad.site_id = :site_id and extract(year from ad.deprecate_date) = :year group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("dept", dept)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findCostsByDept(int siteId, int hospitalId, int year, int dept) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.cost,0) as asset_cost from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.clinical_dept_id= :dept and extract(year from ar.exam_date) = :year group by ai.id order by ai.id) as at left join (select asset_id, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("dept", dept)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findRevenues(int siteId, int hospitalId, int year) {
    return db
      .select(new SQL()
        .SELECT("ai.id", "ai.name", "COALESCE(sum(ar.price_amount),0)")
        .FROM("asset_info ai")
        .INNER_JOIN("asset_clinical_record ar on ai.id = ar.asset_id")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("extract(year from ar.exam_date) = :year")
        .GROUP_BY("ai.id")
        .ORDER_BY("ai.id").toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findDeprecations(int siteId, int hospitalId, int year) {
    return db
      .select(new SQL()
        .SELECT("at.id", "at.name", "COALESCE(am.deprecation,0)")
        .FROM("(" + new SQL()
          .SELECT("ai.id", "ai.name")
          .FROM(" asset_info ai")
          .INNER_JOIN("asset_clinical_record ar on ai.id = ar.asset_id")
          .WHERE("ai.site_id = :site_id")
          .WHERE("ai.hospital_id = :hospital_id")
          .WHERE("extract(year from ar.exam_date) = :year")
          .GROUP_BY("ai.id")
          .ORDER_BY("ai.id")
          .toString().concat(") as at"))
        .LEFT_OUTER_JOIN("(" + new SQL()
          .SELECT("asset_id", "sum(deprecate_amount) as deprecation")
          .FROM("asset_depreciation")
          .WHERE("site_id = :site_id")
          .WHERE("extract(year from deprecate_date) = :year")
          .GROUP_BY("asset_id")
          .toString().concat(") as am on at.id = am.asset_id"))
        .ORDER_BY("at.id").toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

  public Observable<Tuple3<Integer, String, Money>> findCosts(int siteId, int hospitalId, int year) {
    return db
      .select(new SQL()
        .SELECT("at.id", "at.name", "COALESCE(am.cost,0)")
        .FROM("(" + new SQL()
          .SELECT("ai.id", "ai.name")
          .FROM(" asset_info ai")
          .INNER_JOIN("asset_clinical_record ar on ai.id = ar.asset_id")
          .WHERE("ai.site_id = :site_id")
          .WHERE("ai.hospital_id = :hospital_id")
          .WHERE("extract(year from ar.exam_date) = :year")
          .GROUP_BY("ai.id")
          .ORDER_BY("ai.id")
          .toString().concat(") as at"))
        .LEFT_OUTER_JOIN("(" + new SQL()
          .SELECT("asset_id", "sum(total_price) as cost")
          .FROM("work_order")
          .WHERE("site_id = :site_id")
          .WHERE("hospital_id = :hospital_id")
          .WHERE("extract(year from request_time) = :year")
          .GROUP_BY("asset_id")
          .toString().concat(") as am on at.id = am.asset_id"))
        .ORDER_BY("at.id").toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3()))).cache();
  }

}
