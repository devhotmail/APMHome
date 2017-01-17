package com.ge.apm.web;

import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.control.Option;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import webapp.framework.web.service.DbMessageSource;
import webapp.framework.web.service.UserContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.money.Monetary;
import javax.validation.constraints.*;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping("/profit")
@Validated
public class ProfitApi {
  private static final Logger log = LoggerFactory.getLogger(ProfitApi.class);
  private Database db;

  @Resource(name = "rxJdbcConnectionProvider")
  private ConnectionProvider rxJdbcConnectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(rxJdbcConnectionProvider);
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> calcProfit(@RequestParam(value = "year", required = true) int year,
                                        @Pattern(regexp = "type|dept|month") @RequestParam(value = "groupby", required = false) String groupBy,
                                        @RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "dept", required = false) String dept,
                                        @Min(1) @Max(12) @RequestParam(value = "month", required = false) Integer month,
                                        @Min(1) @Max(50) @RequestParam(value = "limit", required = false) Integer limit,
                                        @Min(0) @RequestParam(value = "start", required = false) Integer start) {
    log.info("year:{}, groupby:{}, type:{}, dept:{}, month:{}, limit:{}, start:{}", year, groupBy, type, dept, month, limit, start);
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = findAssetGroups(user.getSiteId());
    log.info("groups: {}", groups);
    if ("type".equals(groupBy))
      return requestByType(groups, user.getSiteId(), user.getHospitalId(), year);
    else
      return ImmutableMap.of("requestparam", String.format("year: %s, groupby: %s, type: %s, dept: %s, month: %s, limit: %s, start: %s", year, groupBy, type, dept, month, limit, start));
  }


  private Map<Integer, String> findAssetGroups(int siteId) {
    return Observable.from(Option.of(DbMessageSource.getMessageCache(siteId)).filter(map -> !map.isEmpty()).getOrElse(DbMessageSource.getMessageCache(-1)).entrySet())
      .filter(entry -> entry.getValue().getMsgType().equalsIgnoreCase("assetGroup"))
      .map(Map.Entry::getValue)
      .toMap(msg -> Ints.tryParse(msg.getMsgKey()), I18nMessage::getValueZh)
      .toBlocking()
      .single();
  }

  private Observable<Tuple3<Integer, String, Money>> calcProfitGroupByAssets(int siteId, int hospitalId, int year) {
    return db.select("select ai.id as asset_id, ai.name as asset_name, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id and ar.site_id = :site_id and ar.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.id order by revenue desc")
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .parameter("year", year)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3._1(), tuple3._2(), CNY.money(tuple3._3())));
  }

  private Observable<Tuple2<Integer, Money>> findRevenuesGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ai.asset_group as asset_group, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id and ar.site_id = :site_id and ar.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.asset_group order by asset_group")
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())));
  }

  private Observable<Tuple2<Integer, Money>> findDeprecationsGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ag.asset_group as asset_group, COALESCE(am.deprecate_amount, 0) as deprecate_amount from (select distinct ai.asset_group from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ar.site_id = :site_id and ar.hospital_id = :hospital_id) as ag left join (select ai.asset_group, sum(ad.deprecate_amount) as deprecate_amount from asset_depreciation ad join asset_info ai on ad.asset_id = ai.id where ai.site_id = :site_id and ai.hospital_id = :hospital_id and extract(year from ad.deprecate_date) = :year group by ai.asset_group) as am on ag.asset_group = am.asset_group order by ag.asset_group")
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())));
  }

  private Observable<Tuple2<Integer, Money>> findCostsGroupByType(int siteId, int hospitalId, int year) {
    return db.select("select ag.asset_group as asset_group, COALESCE(am.cost, 0) as deprecate_amount from (select distinct ai.asset_group from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id where ar.site_id = :site_id and ar.hospital_id = :hospital_id) as ag left join (select ai.asset_group, sum(wo.total_price) as cost from work_order wo join asset_info ai on wo.asset_id = ai.id where wo.site_id = :site_id and wo.hospital_id = :hospital_id and extract(year from wo.request_time) = :year and wo.is_closed = true group by ai.asset_group) as am on ag.asset_group = am.asset_group order by ag.asset_group")
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .parameter("year", year)
      .getAs(Integer.class, Double.class)
      .map(tuple2 -> Tuple.of(tuple2._1(), CNY.money(tuple2._2())));
  }

  private Observable<Tuple3<String, Money, Money>> calcItemsByType(Map<Integer, String> groups, int siteId, int hospitalId, int year) {
    Observable<Tuple2<Integer, Money>> revenues = findRevenuesGroupByType(siteId, hospitalId, year);
    Observable<Tuple2<Integer, Money>> deprecations = findDeprecationsGroupByType(siteId, hospitalId, year);
    Observable<Tuple2<Integer, Money>> costs = findCostsGroupByType(siteId, hospitalId, year);
    return Observable.zip(revenues, deprecations, costs, (revenue, deprecation, cost) ->
      Tuple.of(groups.get(revenue._1), revenue._2, revenue._2.subtract(deprecation._2).subtract(cost._2))
    ).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private double sumRevenue(Observable<Tuple3<String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._2)).toBlocking().single().getNumber().doubleValue();
  }

  private double sumProfit(Observable<Tuple3<String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._3)).toBlocking().single().getNumber().doubleValue();
  }

  private String descRevenues(Observable<Tuple3<String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._2)).map(CNY::desc).toBlocking().single();
  }

  private String descProfits(Observable<Tuple3<String, Money, Money>> items) {
    return items.reduce(CNY.O, (initial, tuple) -> initial.add(tuple._3)).map(CNY::desc).toBlocking().single();
  }

  private Iterable<ImmutableMap<String, Object>> extractItems(Observable<Tuple3<String, Money, Money>> items, int year) {
    return items.map(item -> new ImmutableMap.Builder<String, Object>()
      .put("name", item._1)
      .put("type", "type")
      .put("revenue", item._2.getNumber().doubleValue())
      .put("profit", item._3.getNumber().doubleValue())
      .put("revenue_label", CNY.desc(item._2))
      .put("profit_label", CNY.desc(item._3))
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "child")
        .put("href", String.format("/api/profit?year=%s&type=%s", year, UrlEscapers.urlPathSegmentEscaper().escape(item._1)))
        .build())
      .build()).toBlocking().toIterable();
  }

  private Map<String, Object> requestByType(Map<Integer, String> groups, int siteId, int hospitalId, int year) {
    Observable<Tuple3<String, Money, Money>> items = calcItemsByType(groups, siteId, hospitalId, year);
    return new ImmutableMap.Builder<String, Object>()
      .put("total", items.count().toBlocking().single())
      .put("root", new ImmutableMap.Builder<String, Object>()
        .put("name", "total")
        .put("type", "type")
        .put("revenue", sumRevenue(items))
        .put("profit", sumProfit(items))
        .put("revenue_label", descRevenues(items))
        .put("profit_label", descProfits(items))
        .put("revenue_text", "总收入")
        .put("profit_text", "总利润")
        .put("link", new ImmutableMap.Builder<String, Object>()
          .put("ref", "self")
          .put("href", String.format("/api/profit?year=%s&groupby=type", year))
          .build())
        .build())
      .put("items", extractItems(items, year))
      .build();
  }


  @RequestMapping(value = "/dummy", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> dummyCall() {
    log.info("dummyResponse: {}", dummyResponse());
    return dummyResponse();
  }

  @RequestMapping(value = "/foo", method = RequestMethod.GET)
  @ResponseBody
  public String foo() {
    return "bar";
  }


  @SuppressWarnings("unchecked")
  private Map<String, Object> dummyResponse() {
    return new ImmutableMap.Builder<String, Object>()
      .put("total", 143)
      .put("items", Stream.of(
        new ImmutableMap.Builder<String, Object>()
          .put("id", "32")
          .put("name", "ct001")
          .put("type", "CT")
          .put("revenue", 1.238654e5d)
          .put("profit", 1.0245322e5d)
          .put("percent", 3)
          .put("revenue_label", "12.3")
          .put("profit_label", "10.2")
          .put("label_unit", "万").build(),
        new ImmutableMap.Builder<String, Object>()
          .put("id", "26")
          .put("name", "mr004")
          .put("type", "MR")
          .put("revenue", 9753.48)
          .put("profit", 9753.48)
          .put("percent", 3)
          .put("revenue_label", "9753.5")
          .put("profit_label", "5962.5")
          .put("label_unit", "元").build()).collect(Collectors.toList()))
      .put("root",
        new ImmutableMap.Builder<String, Object>()
          .put("name", "total")
          .put("type", "asset")
          .put("revenue", 1.838654E8)
          .put("profit", 1.3945322E8)
          .put("revenue_label", "18,386.5")
          .put("profit_label", "13,945.3")
          .put("label_unit", "万")
          .put("revenue_text", "总收入")
          .put("profit_text", "总利润")
          .put("link", new ImmutableMap.Builder<String, Object>()
            .put("ref", "self")
            .put("href", "https://dew.io/api/profit?year=2016")
            .build())
          .build())
      .build();
  }


  public final static class Profit {


  }


}
