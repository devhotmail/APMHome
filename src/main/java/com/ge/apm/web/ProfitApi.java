package com.ge.apm.web;

import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.Tuple4;
import javaslang.control.Option;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import webapp.framework.web.service.DbMessageSource;
import webapp.framework.web.service.UserContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/profit")
@Validated
public class ProfitApi {
  private static final Logger log = LoggerFactory.getLogger(ProfitApi.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> calcProfit(HttpServletRequest request,
                                                        @Min(2000) @RequestParam(value = "year", required = true) Integer year,
                                                        @Pattern(regexp = "type|dept|month") @RequestParam(value = "groupby", required = false) String groupBy,
                                                        @Min(1) @RequestParam(value = "type", required = false) Integer type,
                                                        @Min(1) @RequestParam(value = "dept", required = false) Integer dept,
                                                        @Min(1) @Max(12) @RequestParam(value = "month", required = false) Integer month,
                                                        @Min(1) @Max(50) @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                                        @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    log.info("year:{}, groupby:{}, type:{}, dept:{}, month:{}, limit:{}, start:{}", year, groupBy, type, dept, month, limit, start);
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = findFields(user.getSiteId(), "assetGroup");
    Map<Integer, String> depts = findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> months = findFields(user.getSiteId(), "month");
    log.debug("groups: {}, depts: {}, month: {}", groups, depts, months);

    if ("type".equals(groupBy) && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcRoot(groups, findRevenuesGroupByType(user.getSiteId(), user.getHospitalId(), year),
          findDeprecationsGroupByType(user.getSiteId(), user.getHospitalId(), year),
          findCostsGroupByType(user.getSiteId(), user.getHospitalId(), year)),
        year, groupBy, type, dept, month, limit, start);
    } else if (Option.of(groupBy).isEmpty() && Option.of(type).isDefined() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcChild(findRevenuesByType(user.getSiteId(), user.getHospitalId(), year, type),
          findDeprecationsByType(user.getSiteId(), user.getHospitalId(), year, type),
          findCostsByType(user.getSiteId(), user.getHospitalId(), year, type)),
        year, groupBy, type, dept, month, limit, start);
    } else if ("dept".equals(groupBy) && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcRoot(depts, findRevenuesGroupByDept(user.getSiteId(), user.getHospitalId(), year),
          findDeprecationsGroupByDept(user.getSiteId(), user.getHospitalId(), year),
          findCostsGroupByDept(user.getSiteId(), user.getHospitalId(), year)),
        year, groupBy, type, dept, month, limit, start);
    } else if (Option.of(groupBy).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isDefined() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcChild(findRevenuesByDept(user.getSiteId(), user.getHospitalId(), year, dept),
          findDeprecationsByDept(user.getSiteId(), user.getHospitalId(), year, dept),
          findCostsByDept(user.getSiteId(), user.getHospitalId(), year, dept)),
        year, groupBy, type, dept, month, limit, start);
    } else if ("month".equals(groupBy) && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcRoot(months, findRevenuesGroupByMonth(user.getSiteId(), user.getHospitalId(), year),
          findDeprecationsGroupByMonth(user.getSiteId(), user.getHospitalId(), year),
          findCostsGroupByMonth(user.getSiteId(), user.getHospitalId(), year)),
        year, groupBy, type, dept, month, limit, start);
    } else if (Option.of(groupBy).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isDefined()) {
      return serialize(request, groups, depts, months,
        calcChild(findRevenuesByMonth(user.getSiteId(), user.getHospitalId(), year, month),
          findDeprecationsByMonth(user.getSiteId(), user.getHospitalId(), year, month),
          findCostsByMonth(user.getSiteId(), user.getHospitalId(), year, month)),
        year, groupBy, type, dept, month, limit, start);
    } else if (Option.of(groupBy).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcChild(findRevenues(user.getSiteId(), user.getHospitalId(), year),
          findDeprecations(user.getSiteId(), user.getHospitalId(), year),
          findCosts(user.getSiteId(), user.getHospitalId(), year)),
        year, groupBy, type, dept, month, limit, start);
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }


  private Map<Integer, String> findFields(int siteId, String type) {
    return Observable.from(Option.of(DbMessageSource.getMessageCache(siteId)).filter(map -> !map.isEmpty()).getOrElse(DbMessageSource.getMessageCache(-1)).entrySet())
      .filter(entry -> entry.getValue().getMsgType().equalsIgnoreCase(type))
      .map(Map.Entry::getValue)
      .toMap(msg -> Ints.tryParse(msg.getMsgKey()), I18nMessage::getValueZh)
      .toBlocking()
      .single();
  }

  private Map<Integer, String> findDepts(int siteId, int hospitalId) {
    return db.select("select distinct clinical_dept_id, clinical_dept_name from asset_info where site_id = :site_id and hospital_id = :hospital_id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
      .getAs(Integer.class, String.class)
      .toMap(com.github.davidmoten.rx.jdbc.tuple.Tuple2::_1, tuple -> Option.of(tuple._2()).getOrElse(""))
      .toBlocking()
      .single();
  }

  private Observable<Tuple2<Integer, Money>> findRevenuesGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ai.asset_group as asset_group, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ar.site_id = :site_id and ar.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.asset_group order by asset_group")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())))
      .cache();
  }

  private Observable<Tuple2<Integer, Money>> findDeprecationsGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ag.asset_group as asset_group, COALESCE(am.deprecate_amount, 0) as asset_deprecate from (select distinct ai.asset_group from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id) as ag left join (select ai.asset_group, sum(ad.deprecate_amount) as deprecate_amount from asset_depreciation ad join asset_info ai on ad.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ad.deprecate_date) = :year group by ai.asset_group) as am on ag.asset_group = am.asset_group order by ag.asset_group")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())))
      .cache();
  }

  private Observable<Tuple2<Integer, Money>> findCostsGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ag.asset_group as asset_group, COALESCE(am.cost, 0) as asset_cost from (select distinct ai.asset_group from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id) as ag left join (select ai.asset_group, sum(wo.total_price) as cost from work_order wo join asset_info ai on wo.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from wo.request_time) = :year and wo.is_closed = true group by ai.asset_group) as am on ag.asset_group = am.asset_group order by ag.asset_group")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())))
      .cache();
  }

  private Observable<Tuple3<Integer, String, Money>> findRevenuesByType(int siteId, int hospitalId, int year, int type) {
    return db.select("select ai.id as asset_id, ai.name as asset_name, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.asset_group = :asset_group and extract(year from ar.exam_date) = :year group by ai.id order by ai.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("asset_group", type)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findDeprecationsByType(int siteId, int hospitalId, int year, int type) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.asset_deprecate,0) as deprecation from (select distinct ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.asset_group = :asset_group) as at left join (select ad.asset_id, sum(ad.deprecate_amount) as asset_deprecate from asset_depreciation ad where ad.site_id = :site_id and extract(year from ad.deprecate_date) = :year group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("asset_group", type)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findCostsByType(int siteId, int hospitalId, int year, int type) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.cost,0) as asset_cost from (select distinct ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.asset_group = :asset_group) as at left join (select asset_id, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year group by asset_id) am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("asset_group", type)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple2<Integer, Money>> findRevenuesGroupByMonth(int siteId, int hospitalId, int year) {
    return db.select("select pl.month, COALESCE(ar.revenue,0) from (select generate_series(1,12) as month) as pl left join (select extract(month from exam_date) as month, sum(price_amount) as revenue from asset_clinical_record where site_id = :site_id and hospital_id = :hospital_id and extract(year from exam_date) = :year group by month) as ar on pl.month = ar.month order by pl.month")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple -> Tuple.of(tuple._1(), CNY.money(tuple._2())));
  }

  private Observable<Tuple2<Integer, Money>> findDeprecationsGroupByMonth(int siteId, int hospitalId, int year) {
    return db.select("select pl.month, COALESCE(ad.deprecation,0) from (select generate_series(1,12) as month) as pl left join (select extract(month from deprecate_date) as month, sum(deprecate_amount) as deprecation from asset_depreciation where site_id = :site_id and extract(year from deprecate_date) = :year group by month) as ad on pl.month = ad.month order by pl.month")
      .parameter("site_id", siteId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple -> Tuple.of(tuple._1(), CNY.money(tuple._2())));
  }

  private Observable<Tuple2<Integer, Money>> findCostsGroupByMonth(int siteId, int hospitalId, int year) {
    return db.select("select pl.month, COALESCE(wo.cost,0) from (select generate_series(1,12) as month) as pl left join (select extract(month from request_time) as month, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year group by month) as wo on pl.month = wo.month order by pl.month")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple -> Tuple.of(tuple._1(), CNY.money(tuple._2())));
  }

  private Observable<Tuple3<Integer, String, Money>> findRevenuesByMonth(int siteId, int hospitalId, int year, int month) {
    return db.select("select ai.id, ai.name, COALESCE(sum(ar.price_amount),0) from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year and extract(month from ar.exam_date) = :month group by ai.id order by ai.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("month", month)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findDeprecationsByMonth(int siteId, int hospitalId, int year, int month) {
    return db.select("select at.id, at.name, COALESCE(am.asset_deprecate,0) as asset_deprecate from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year and extract(month from ar.exam_date) = :month group by ai.id order by ai.id) as at left join (select ad.asset_id, sum(ad.deprecate_amount) as asset_deprecate from asset_depreciation ad where ad.site_id = :site_id and extract(year from ad.deprecate_date) = :year and extract(month from ad.deprecate_date) = :month group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("month", month)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findCostsByMonth(int siteId, int hospitalId, int year, int month) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.cost,0) as asset_cost from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year and extract(month from ar.exam_date) = :month group by ai.id order by ai.id) as at left join (select asset_id, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year and extract(month from request_time) = :month group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("month", month)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple2<Integer, Money>> findRevenuesGroupByDept(int siteId, int hospitalId, int year) {
    return db.select("select ai.clinical_dept_id as dept_id, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.clinical_dept_id order by clinical_dept_id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())))
      .cache();
  }

  private Observable<Tuple2<Integer, Money>> findDeprecationsGroupByDept(int siteId, int hospitalId, int year) {
    return db.select("select at.dept_id, COALESCE(am.asset_deprecate,0) as deprecation from (select ai.clinical_dept_id as dept_id from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.clinical_dept_id order by clinical_dept_id) at left join (select ai.clinical_dept_id as dept_id, sum(ad.deprecate_amount) as asset_deprecate from asset_info ai join asset_depreciation ad on ai.id = ad.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ad.deprecate_date) = :year group by dept_id) am on at.dept_id = am.dept_id order by at.dept_id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())))
      .cache();
  }

  private Observable<Tuple2<Integer, Money>> findCostsGroupByDept(int siteId, int hospitalId, int year) {
    return db.select("select at.dept_id, COALESCE(am.cost, 0) as asset_cost from (select ai.clinical_dept_id as dept_id from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.clinical_dept_id order by clinical_dept_id) at left join (select ai.clinical_dept_id as dept_id, sum(wo.total_price) as cost from asset_info ai join work_order wo on ai.id = wo.asset_id where wo.site_id = :site_id and wo.hospital_id = :hospital_id and extract(year from wo.request_time) = :year and wo.is_closed = true group by dept_id) am on at.dept_id = am.dept_id order by at.dept_id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())))
      .cache();
  }

  private Observable<Tuple3<Integer, String, Money>> findRevenuesByDept(int siteId, int hospitalId, int year, int dept) {
    return db.select("select ai.id, ai.name, COALESCE(sum(ar.price_amount),0) from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.clinical_dept_id= :dept and extract(year from ar.exam_date) = :year group by ai.id order by ai.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("dept", dept)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findDeprecationsByDept(int siteId, int hospitalId, int year, int dept) {
    return db.select("select at.id, at.name, COALESCE(am.asset_deprecate,0) as asset_deprecate from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.clinical_dept_id= :dept and extract(year from ar.exam_date) = :year group by ai.id order by ai.id) as at left join (select ad.asset_id, sum(ad.deprecate_amount) as asset_deprecate from asset_depreciation ad where ad.site_id = :site_id and extract(year from ad.deprecate_date) = :year group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("dept", dept)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findCostsByDept(int siteId, int hospitalId, int year, int dept) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.cost,0) as asset_cost from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and ai.clinical_dept_id= :dept and extract(year from ar.exam_date) = :year group by ai.id order by ai.id) as at left join (select asset_id, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year).parameter("dept", dept)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findRevenues(int siteId, int hospitalId, int year) {
    return db.select("select ai.id, ai.name, COALESCE(sum(ar.price_amount),0) from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.id order by ai.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findDeprecations(int siteId, int hospitalId, int year) {
    return db.select("select at.id, at.name, COALESCE(am.asset_deprecate,0) as asset_deprecate from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.id order by ai.id) as at left join (select ad.asset_id, sum(ad.deprecate_amount) as asset_deprecate from asset_depreciation ad where ad.site_id = :site_id and extract(year from ad.deprecate_date) = :year group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple3<Integer, String, Money>> findCosts(int siteId, int hospitalId, int year) {
    return db.select("select at.id as asset_id, at.name as asset_name, COALESCE(am.cost,0) as asset_cost from (select ai.id, ai.name from asset_info ai join asset_clinical_record ar on ai.id = ar.asset_id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.id order by ai.id) as at left join (select asset_id, sum(total_price) as cost from work_order where site_id = :site_id and hospital_id = :hospital_id and extract(year from request_time) = :year group by asset_id) as am on at.id = am.asset_id order by at.id")
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("year", year)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple4<Integer, String, Money, Money>> calcRoot(Map<Integer, String> map, Observable<Tuple2<Integer, Money>> revenues, Observable<Tuple2<Integer, Money>> deprecations, Observable<Tuple2<Integer, Money>> costs) {
    return Observable.zip(revenues, deprecations, costs, (revenue, deprecation, cost) ->
      Tuple.of(revenue._1, map.get(revenue._1), revenue._2, revenue._2.subtract(deprecation._2).subtract(cost._2))
    ).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private Observable<Tuple4<Integer, String, Money, Money>> calcChild(Observable<Tuple3<Integer, String, Money>> revenues, Observable<Tuple3<Integer, String, Money>> deprecations, Observable<Tuple3<Integer, String, Money>> costs) {
    return Observable.zip(revenues, deprecations, costs, (revenue, deprecation, cost) ->
      Tuple.of(revenue._1, revenue._2, revenue._3, revenue._3.subtract(deprecation._3).subtract(cost._3))
    ).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private double sumRevenue(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._3)).toBlocking().single().getNumber().doubleValue();
  }

  private double sumProfit(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._4)).toBlocking().single().getNumber().doubleValue();
  }

  private Tuple2<String, String> descRevenues(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._3)).map(CNY::desc).toBlocking().single();
  }

  private Tuple2<String, String> descProfits(Observable<Tuple4<Integer, String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._4)).map(CNY::desc).toBlocking().single();
  }

  private Iterable<ImmutableMap<String, Object>> mapItems(HttpServletRequest request, Observable<Tuple4<Integer, String, Money, Money>> items, int year, String groupBy, Integer type, Integer dept, Integer month, Integer limit, Integer start) {
    return items.skip(start).limit(limit).map(item -> new ImmutableMap.Builder<String, Object>()
      .put("id", item._1)
      .put("name", item._2)
      .put("type", Option.of(groupBy).orElse(Option.of(type).map(i -> "type")).orElse(Option.of(dept).map(i -> "dept")).orElse(Option.of(month).map(i -> "month")).getOrElse("asset"))
      .put("revenue", item._3.getNumber().doubleValue())
      .put("profit", item._4.getNumber().doubleValue())
      .put("revenue_label", CNY.desc(item._3)._1)
      .put("revenue_label_unit", CNY.desc(item._3)._2)
      .put("profit_label", CNY.desc(item._4)._1)
      .put("profit_label_unit", CNY.desc(item._4)._2)
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "child")
        .put("href", Option.of(groupBy).map(v -> String.format("%s?year=%s&%s=%s", request.getRequestURL(), year, groupBy, item._1)).getOrElse(""))
        .build())
      .build()).toBlocking().toIterable();
  }

  private ResponseEntity<Map<String, Object>> serialize(HttpServletRequest request, Map<Integer, String> groups, Map<Integer, String> depts, Map<Integer, String> months, Observable<Tuple4<Integer, String, Money, Money>> items, int year, String groupBy, Integer type, Integer dept, Integer month, Integer limit, Integer start) {
    Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
      .put("pages", new ImmutableMap.Builder<String, Object>()
        .put("total", items.count().toBlocking().single())
        .put("limit", limit)
        .put("start", start)
        .build())
      .put("root", new ImmutableMap.Builder<String, Object>()
        .put("name", Option.of(groupBy).map(v -> "total")
          .orElse(Option.of(type).flatMap(v -> Option.of(groups.get(v))))
          .orElse(Option.of(dept).flatMap(v -> Option.of(groups.get(v))))
          .orElse(Option.of(month).flatMap(v -> Option.of(groups.get(v))))
          .getOrElse("asset"))
        .put("type", Option.of(groupBy).orElse(Option.of(type).map(i -> "type")).orElse(Option.of(dept).map(i -> "dept")).orElse(Option.of(month).map(i -> "month")).getOrElse("asset"))
        .put("revenue", sumRevenue(items))
        .put("profit", sumProfit(items))
        .put("revenue_label", descRevenues(items)._1)
        .put("revenue_label_unit", descRevenues(items)._2)
        .put("profit_label", descProfits(items)._1)
        .put("profit_label_unit", descProfits(items)._2)
        .put("revenue_text", "总收入")
        .put("profit_text", "总利润")
        .put("link", new ImmutableMap.Builder<String, Object>()
          .put("ref", "self")
          .put("href", Option.of(groupBy).map(v -> String.format("%s?year=%s&groupby=%s", request.getRequestURL(), year, v))
            .orElse(Option.of(type).map(v -> String.format("%s?year=%s&type=%s", request.getRequestURL(), year, v)))
            .orElse(Option.of(dept).map(v -> String.format("%s?year=%s&dept=%s", request.getRequestURL(), year, v)))
            .orElse(Option.of(dept).map(v -> String.format("%s?year=%s&month=%s", request.getRequestURL(), year, v)))
            .getOrElse(String.format("%s?year=%s", request.getRequestURL(), year)))
          .build())
        .build())
      .put("items", mapItems(request, items, year, groupBy, type, dept, month, limit, start))
      .build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)).body(body);
  }
}
