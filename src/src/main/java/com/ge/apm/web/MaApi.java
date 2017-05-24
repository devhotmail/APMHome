package com.ge.apm.web;


import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.MaForecastService;
import com.ge.apm.service.api.MaService;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.typesafe.config.ConfigFactory;
import javaslang.*;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/ma")
@Validated
public class MaApi {
  private static final Logger log = LoggerFactory.getLogger(MaApi.class);

  @Autowired
  private MaService maService;

  @Autowired
  private CommonService commonService;

  @Autowired
  private MaForecastService maForecastService;

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<Map<String, Object>> findMaintenanceCosts(HttpServletRequest request,
                                                                  @RequestParam(name = "from", required = true) Date from,
                                                                  @RequestParam(name = "to", required = true) Date to,
                                                                  @Pattern(regexp = "type|dept|supplier") @RequestParam(name = "groupby", required = false) String groupBy,
                                                                  @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                                  @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                                  @Min(1) @RequestParam(name = "supplier", required = false) Integer supplier,
                                                                  @Pattern(regexp = "acyman|mtpm") @RequestParam(name = "rltgrp", required = true) String rltGrp,
                                                                  @Min(1) @RequestParam(name = "limit", required = false, defaultValue = "" + Integer.MAX_VALUE) Integer limit,
                                                                  @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start) {
    log.info("inputs: from {}, to {}, groupBy {}, dept {}, type {}, supplier {}", from, to, groupBy, dept, type, supplier);
    UserAccount user = UserContext.getCurrentLoginUser();
    int siteId = user.getSiteId();
    int hospitalId = user.getHospitalId();
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(siteId, hospitalId);
    Map<Integer, String> suppliers = commonService.findSuppliers(siteId);
    log.info("groups: {}, depts: {}, suppliers: {}", groups, depts, suppliers);
    if (Option.of(groupBy).isDefined()) {
      Observable<Tuple2<Integer, Tuple3<Double, Double, Double>>> items = maService.findAstMtGroups(siteId, hospitalId, from, to, dept, type, supplier, groupBy, rltGrp);
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
        .body(new ImmutableMap.Builder<String, Object>()
          .put("root", mapRoot(items.map(v -> Tuple.of(v._2._2, v._2._3)), dept, type, supplier, groupBy, rltGrp, start, limit))
          .put("items", mapGroups(items, "type".equals(groupBy) ? groups : ("dept".equals(groupBy) ? depts : suppliers), rltGrp, start, limit))
          .build());
    } else {
      Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> items = maService.findAstMtItems(siteId, hospitalId, from, to, dept, type, supplier, rltGrp);
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
        .body(new ImmutableMap.Builder<String, Object>()
          .put("root", mapRoot(items.map(v -> Tuple.of(v._2._3, v._2._4)), dept, type, supplier, groupBy, rltGrp, start, limit))
          .put("items", mapAssets(items, rltGrp, start, limit))
          .build());
    }
  }

  @RequestMapping(value = "/asset/{id}", method = RequestMethod.GET)
  public ResponseEntity<Iterable<ImmutableMap<String, Object>>> findMtForSingleAsset(HttpServletRequest request,
                                                                                     @RequestParam(name = "from", required = true) Date from,
                                                                                     @RequestParam(name = "to", required = true) Date to,
                                                                                     @Pattern(regexp = "acyman|mtpm") @RequestParam(name = "rltgrp", required = true) String rltGrp,
                                                                                     @Min(1) @PathVariable(value = "id") Integer id) {
    log.info("inputs: from {}, to {}, rltGrp {}, id {}", from, to, rltGrp, id);
    Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> items = maService.findMtSingleAsset(from, to, id, rltGrp);
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
      .body(mapAssets(items, rltGrp, 0, 1));
  }

  @RequestMapping(value = "/suggestion/{condition}", method = RequestMethod.PUT)
  public ResponseEntity<Map<String, Object>> groupSuggestions(HttpServletRequest request,
                                                              @RequestParam(name = "year", required = true) Integer year,
                                                              @Pattern(regexp = "type|dept|supplier") @RequestParam(name = "groupby", required = true) String groupBy,
                                                              @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                              @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                              @Min(1) @RequestParam(name = "supplier", required = false) Integer supplier,
                                                              @Pattern(regexp = "lowonrate|highcost|bindonratecost") @PathVariable(value = "condition") String condition,
                                                              @RequestBody(required = true) String inputJson) {
    log.info("inputs: year {}, groupBy {}, dept {}, type {}, supplier {}, condition {}, inputJson {}", year, groupBy, dept, type, supplier, condition, inputJson);
    if (!List.of(LocalDate.now().getYear(), LocalDate.now().plusYears(1).getYear()).contains(year)) {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "year must be this year or next year"));
    }
    UserAccount user = UserContext.getCurrentLoginUser();
    int siteId = user.getSiteId();
    int hospitalId = user.getHospitalId();
    java.util.List<Double> threshold = Try.of(() -> ConfigFactory.parseString(inputJson).getStringList("threshold")).getOrElse(List.of("").toJavaList())
      .stream().map(Doubles::tryParse).filter(v -> v != null).collect(Collectors.toList());
    if (threshold.size() != 4) {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input threshold not supported"));
    }
    Map<Integer, Double> onrate = CommonService.parseInputJson(inputJson, "items", "id", "onrate_increase");
    Map<Integer, Double> cost1 = CommonService.parseInputJson(inputJson, "items", "id", "cost1_increase");
    Map<Integer, Double> cost2 = CommonService.parseInputJson(inputJson, "items", "id", "cost2_increase");
    Seq<Integer> sugItems = MaForecastService.userPredict(MaForecastService.virtualSqlItems(dept, type, supplier, "acyman",
      maForecastService.predict(siteId, hospitalId, Date.valueOf(LocalDate.now().minusYears(2).withDayOfYear(1)), Date.valueOf(LocalDate.now()), year)),
      MaForecastService.virtualSqlItems(dept, type, supplier, "acyman", maForecastService.lastYearData(siteId, hospitalId)), onrate, cost1, cost2)
      .filter(v -> "lowonrate".equals(condition) ? MaForecastService.sugLowBound(v, threshold.get(0)) :
        ("highcost".equals(condition) ? MaForecastService.sugHighCost(v, threshold.get(2)) :
          MaForecastService.sugBindOnrateCost(v, threshold.get(0), threshold.get(1), threshold.get(2), threshold.get(3))))
      .map(v -> v._1._1);
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(
      new ImmutableMap.Builder<String, Object>()
        .put("condition", condition)
        .put("count", sugItems.count(v -> true))
        .put("items", sugItems.map(sub -> ImmutableMap.of("id", sub)).toJavaList())
        .build()
    );
  }

  @RequestMapping(value = "/forecast", method = RequestMethod.PUT)
  public ResponseEntity<Map<String, Object>> forecast(HttpServletRequest request,
                                                      @RequestParam(name = "year", required = true) Integer year,
                                                      @Pattern(regexp = "type|dept|supplier") @RequestParam(name = "groupby", required = false) String groupBy,
                                                      @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                      @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                      @Min(1) @RequestParam(name = "supplier", required = false) Integer supplier,
                                                      @Pattern(regexp = "acyman|mtpm") @RequestParam(name = "rltgrp", required = true) String rltGrp,
                                                      @Pattern(regexp = "yes|no") @RequestParam(name = "msa", required = true) String msa,
                                                      @Min(1) @RequestParam(name = "limit", required = false, defaultValue = "" + Integer.MAX_VALUE) Integer limit,
                                                      @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
                                                      @RequestBody(required = true) String inputJson) {
    log.info("inputs: year {}, groupBy {}, dept {}, type {}, rltGrp {}, mas {}, limit {}, start {} inputJson {}", year, groupBy, dept, type, rltGrp, msa, limit, start, inputJson);
    if (!List.of(LocalDate.now().getYear(), LocalDate.now().plusYears(1).getYear()).contains(year)) {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "year must be this year or next year"));
    }
    UserAccount user = UserContext.getCurrentLoginUser();
    int siteId = user.getSiteId();
    int hospitalId = user.getHospitalId();
    java.util.List<Double> threshold = Try.of(() -> ConfigFactory.parseString(inputJson).getStringList("threshold")).getOrElse(List.of("").toJavaList())
      .stream().map(Doubles::tryParse).filter(v -> v != null).collect(Collectors.toList());
    if (threshold.size() != 4) {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input threshold not supported"));
    }
    Map<Integer, Double> onrate = CommonService.parseInputJson(inputJson, "items", "id", "onrate_increase");
    Map<Integer, Double> cost1 = CommonService.parseInputJson(inputJson, "items", "id", "cost1_increase");
    Map<Integer, Double> cost2 = CommonService.parseInputJson(inputJson, "items", "id", "cost2_increase");
    Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> assets =
      MaForecastService.userPredict(MaForecastService.virtualSqlItems(dept, type, supplier, rltGrp,
        maForecastService.predict(siteId, hospitalId, Date.valueOf(LocalDate.now().minusYears(2).withDayOfYear(1)), Date.valueOf(LocalDate.now()), year)),
        MaForecastService.virtualSqlItems(dept, type, supplier, rltGrp, maForecastService.lastYearData(siteId, hospitalId)), onrate, cost1, cost2);
    if ("yes".equals(msa)) {
      assets = assets.filter(v -> MaForecastService.sugLowBound(v, threshold.get(0)) || MaForecastService.sugHighCost(v, threshold.get(2))
        || MaForecastService.sugBindOnrateCost(v, threshold.get(0), threshold.get(1), threshold.get(2), threshold.get(3)));
    } else if ("no".equals(msa)) {
      assets = assets.filter(v -> !(MaForecastService.sugLowBound(v, threshold.get(0)) || MaForecastService.sugHighCost(v, threshold.get(2))
        || MaForecastService.sugBindOnrateCost(v, threshold.get(0), threshold.get(1), threshold.get(2), threshold.get(3))));
    }
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(siteId, hospitalId);
    Map<Integer, String> suppliers = commonService.findSuppliers(siteId);
    if (Option.of(groupBy).isDefined()) {
      Observable<Tuple2<Integer, Tuple3<Double, Double, Double>>> items = Observable.from(MaForecastService.virtualSqlGroups(groupBy, assets));
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
        .body(new ImmutableMap.Builder<String, Object>()
          .put("root", mapRoot(items.map(v -> Tuple.of(v._2._2, v._2._3)), dept, type, supplier, groupBy, rltGrp, start, limit))
          .put("items", mapGroups(items, "type".equals(groupBy) ? groups : ("dept".equals(groupBy) ? depts : suppliers), rltGrp, start, limit))
          .build());
    } else {
      Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> items = Observable.from(assets);
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
        .body(new ImmutableMap.Builder<String, Object>()
          .put("root", mapRoot(items.map(v -> Tuple.of(v._2._3, v._2._4)), dept, type, supplier, groupBy, rltGrp, start, limit))
          .put("items", mapAssets(items, rltGrp, start, limit))
          .build());
    }
  }

  @RequestMapping(value = "/forecastrate", method = RequestMethod.PUT)
  public ResponseEntity<Map<String, Object>> forecastrate(HttpServletRequest request,
                                                          @RequestParam(name = "year", required = true) Integer year,
                                                          @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                          @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                          @Min(1) @RequestParam(name = "supplier", required = false) Integer supplier,
                                                          @Pattern(regexp = "acyman|mtpm") @RequestParam(name = "rltgrp", required = true) String rltGrp,
                                                          @Pattern(regexp = "yes|no") @RequestParam(name = "msa", required = true) String msa,
                                                          @Min(1) @RequestParam(name = "limit", required = false, defaultValue = "" + Integer.MAX_VALUE) Integer limit,
                                                          @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
                                                          @RequestBody(required = true) String inputJson) {
    log.info("inputs: year {}, dept {}, type {}, rltGrp {}, mas {}, limit {}, start {} inputJson {}", year, dept, type, rltGrp, msa, limit, start, inputJson);
    if (!List.of(LocalDate.now().getYear(), LocalDate.now().plusYears(1).getYear()).contains(year)) {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "year must be this year or next year"));
    }
    UserAccount user = UserContext.getCurrentLoginUser();
    int siteId = user.getSiteId();
    int hospitalId = user.getHospitalId();
    java.util.List<Double> threshold = Try.of(() -> ConfigFactory.parseString(inputJson).getStringList("threshold")).getOrElse(List.of("").toJavaList())
      .stream().map(Doubles::tryParse).filter(v -> v != null).collect(Collectors.toList());
    if (threshold.size() != 4) {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input threshold not supported"));
    }
    Map<Integer, Double> onrate = CommonService.parseInputJson(inputJson, "items", "id", "onrate_increase");
    Map<Integer, Double> cost1 = CommonService.parseInputJson(inputJson, "items", "id", "cost1_increase");
    Map<Integer, Double> cost2 = CommonService.parseInputJson(inputJson, "items", "id", "cost2_increase");
    Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> assets =
      MaForecastService.userPredict(MaForecastService.virtualSqlItems(dept, type, supplier, rltGrp,
        maForecastService.predict(siteId, hospitalId, Date.valueOf(LocalDate.now().minusYears(2).withDayOfYear(1)), Date.valueOf(LocalDate.now()), year)),
        MaForecastService.virtualSqlItems(dept, type, supplier, rltGrp, maForecastService.lastYearData(siteId, hospitalId)), onrate, cost1, cost2);
    Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> assetsRate =
      MaForecastService.getForecastRate(MaForecastService.virtualSqlItems(dept, type, supplier, rltGrp,
        maForecastService.predict(siteId, hospitalId, Date.valueOf(LocalDate.now().minusYears(2).withDayOfYear(1)), Date.valueOf(LocalDate.now()), year)),
        MaForecastService.virtualSqlItems(dept, type, supplier, rltGrp, maForecastService.lastYearData(siteId, hospitalId)), onrate, cost1, cost2);
    if ("yes".equals(msa)) {
      assetsRate = assets.zip(assetsRate).filter(v -> MaForecastService.sugLowBound(v._1, threshold.get(0)) || MaForecastService.sugHighCost(v._1, threshold.get(2))
        || MaForecastService.sugBindOnrateCost(v._1, threshold.get(0), threshold.get(1), threshold.get(2), threshold.get(3)))
        .map(v -> v._2);
    } else if ("no".equals(msa)) {
      assetsRate = assets.zip(assetsRate).filter(v -> !(MaForecastService.sugLowBound(v._1, threshold.get(0)) || MaForecastService.sugHighCost(v._1, threshold.get(2))
        || MaForecastService.sugBindOnrateCost(v._1, threshold.get(0), threshold.get(1), threshold.get(2), threshold.get(3))))
        .map(v -> v._2);
    }
    Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> items = Observable.from(assetsRate);
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
      .body(new ImmutableMap.Builder<String, Object>()
        .put("threshold", mapRootRate(items.map(v -> Tuple.of(v._2._3, v._2._4)), dept, type, supplier, rltGrp, start, limit))
        .put("items", mapAssetsRate(items, rltGrp, start, limit))
        .build());
  }

  @RequestMapping(value = "/forecast/asset/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Iterable<ImmutableMap<String, Object>>> forecastSingleAsset(HttpServletRequest request,
                                                                                    @RequestParam(name = "year", required = true) Integer year,
                                                                                    @Pattern(regexp = "acyman|mtpm") @RequestParam(name = "rltgrp", required = true) String rltGrp,
                                                                                    @Min(1) @PathVariable(value = "id") Integer id,
                                                                                    @RequestBody(required = true) String inputJson) {
    log.info("inputs: year {}, rltGrp {}, id {}, inputJson {}", year, rltGrp, id, inputJson);
    if (!List.of(LocalDate.now().getYear(), LocalDate.now().plusYears(1).getYear()).contains(year)) {
      return ResponseEntity.badRequest().body(List.of(ImmutableMap.of("msg", "year must be this year or next year")));
    }
    UserAccount user = UserContext.getCurrentLoginUser();
    int siteId = user.getSiteId();
    int hospitalId = user.getHospitalId();
    java.util.List<Double> threshold = Try.of(() -> ConfigFactory.parseString(inputJson).getStringList("threshold")).getOrElse(List.of("").toJavaList())
      .stream().map(Doubles::tryParse).filter(v -> v != null).collect(Collectors.toList());
    if (threshold.size() != 4) {
      return ResponseEntity.badRequest().body(List.of(ImmutableMap.of("msg", "year must be this year or next year")));
    }
    Map<Integer, Double> onrate = CommonService.parseInputJson(inputJson, "items", "id", "onrate_increase");
    Map<Integer, Double> cost1 = CommonService.parseInputJson(inputJson, "items", "id", "cost1_increase");
    Map<Integer, Double> cost2 = CommonService.parseInputJson(inputJson, "items", "id", "cost2_increase");
    Seq<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> assets =
      MaForecastService.virtualSqlSingle(id, rltGrp,
        maForecastService.predict(siteId, hospitalId, Date.valueOf(LocalDate.now().minusYears(2).withDayOfYear(1)), Date.valueOf(LocalDate.now()), year));
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
      .body(mapAssets(Observable.from(assets), rltGrp, 0, 1));
  }

  //id, name, dept, type, supplier
  //price, onrate, labor/repair,parts/PM
  private Iterable<ImmutableMap<String, Object>> mapAssets(Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> items, String rltGrp, Integer start, Integer limit) {
    return items.skip(start).limit(limit).map(v -> new ImmutableMap.Builder<String, Object>()
      .put("id", v._1._1)
      .put("name", Option.of(v._1._2).getOrElse(""))
      .put("dept", Option.of(v._1._3).getOrElse(0))
      .put("type", Option.of(v._1._4).getOrElse(0))
      .put("supplier", Option.of(v._1._5).getOrElse(0))
      .put("price", v._2._1)
      .put("onrate", v._2._2)
      .put("acyman".equals(rltGrp) ? "labor" : "repair", v._2._3)
      .put("acyman".equals(rltGrp) ? "parts" : "PM", v._2._4)
      .build()).toBlocking().toIterable();
  }

  //id, name, dept, type, supplier
  //price, onrate, labor/repair_increase,parts/PM_increase
  private Iterable<ImmutableMap<String, Object>> mapAssetsRate(Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> items, String rltGrp, Integer start, Integer limit) {
    return items.skip(start).limit(limit).map(v -> new ImmutableMap.Builder<String, Object>()
      .put("id", v._1._1)
      .put("name", Option.of(v._1._2).getOrElse(""))
      .put("dept", Option.of(v._1._3).getOrElse(0))
      .put("type", Option.of(v._1._4).getOrElse(0))
      .put("supplier", Option.of(v._1._5).getOrElse(0))
      .put("price", v._2._1)
      .put("onrate_increase", v._2._2)
      .put("acyman".equals(rltGrp) ? "labor_increase" : "repair_increase", v._2._3)
      .put("acyman".equals(rltGrp) ? "parts_increase" : "PM_increase", v._2._4)
      .build()).toBlocking().toIterable();
  }

  //group_id
  //onrate, labor/repair,parts/PM
  private Iterable<ImmutableMap<String, Object>> mapGroups(Observable<Tuple2<Integer, Tuple3<Double, Double, Double>>> items, Map<Integer, String> groups, String rltGrp, Integer start, Integer limit) {
    return items.skip(start).limit(limit).map(v -> new ImmutableMap.Builder<String, Object>()
      .put("id", v._1)
      .put("name", Option.of(groups.get(v._1)).getOrElse(""))
      .put("onrate", v._2._1)
      .put("acyman".equals(rltGrp) ? "labor" : "repair", v._2._2)
      .put("acyman".equals(rltGrp) ? "parts" : "PM", v._2._3)
      .build()).toBlocking().toIterable();
  }

  //id for group: dept-type-supplier
  private ImmutableMap<String, Object> mapRoot(Observable<Tuple2<Double, Double>> costs, Integer dept, Integer type, Integer supplier, String groupBy, String rltGrp, Integer start, Integer limit) {
    return new ImmutableMap.Builder<String, Object>()
      .put("id", Option.of(groupBy).map(v -> "100")
        .getOrElse(String.format("%s-%s-%s", Option.of(dept).getOrElse(0), Option.of(type).getOrElse(0), Option.of(supplier).getOrElse(0))))
      .put("total", costs.count().toBlocking().single())
      .put("start", start)
      .put("limit", limit)
      .put("acyman".equals(rltGrp) ? "labor" : "repair", Try.of(() -> costs.map(v -> v._1).reduce((a, b) -> a + b).toBlocking().single()).getOrElse(0D))
      .put("acyman".equals(rltGrp) ? "parts" : "PM", Try.of(() -> costs.map(v -> v._2).reduce((a, b) -> a + b).toBlocking().single()).getOrElse(0D))
      .build();
  }

  //id for group: dept-type-supplier
  private ImmutableMap<String, Object> mapRootRate(Observable<Tuple2<Double, Double>> costs, Integer dept, Integer type, Integer supplier, String rltGrp, Integer start, Integer limit) {
    return new ImmutableMap.Builder<String, Object>()
      .put("id", String.format("%s-%s-%s", Option.of(dept).getOrElse(0), Option.of(type).getOrElse(0), Option.of(supplier).getOrElse(0)))
      .put("total", costs.count().toBlocking().single())
      .put("start", start)
      .put("limit", limit)
      .put("acyman".equals(rltGrp) ? "labor_increase" : "repair_increase", List.ofAll(costs.map(v -> v._1).toBlocking().toIterable()).average().getOrElse(0D))
      .put("acyman".equals(rltGrp) ? "parts_increase" : "PM_increase", List.ofAll(costs.map(v -> v._2).toBlocking().toIterable()).average().getOrElse(0D))
      .build();
  }

}
