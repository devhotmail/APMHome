package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.ProfitService;
import com.ge.apm.service.utils.CNY;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.Tuple4;
import javaslang.control.Option;
import org.javamoney.moneta.Money;
import org.joda.time.DateTime;
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
  @Autowired
  private CommonService commonService;
  @Autowired
  private ProfitService profitService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> calcProfit(HttpServletRequest request,
                                                        @Min(2000) @RequestParam(value = "year", required = true) Integer year,
                                                        @Pattern(regexp = "type|dept|month") @RequestParam(value = "groupby", required = false) String groupBy,
                                                        @Min(1) @RequestParam(value = "type", required = false) Integer type,
                                                        @Min(1) @RequestParam(value = "dept", required = false) Integer dept,
                                                        @Min(1) @Max(12) @RequestParam(value = "month", required = false) Integer month,
                                                        @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false) Integer limit,
                                                        @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    log.info("year:{}, groupby:{}, type:{}, dept:{}, month:{}, limit:{}, start:{}", year, groupBy, type, dept, month, limit, start);
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> months = Observable.from(commonService.findFields(user.getSiteId(), "month").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, Tuple3<Integer, String, String>> hospitals = commonService.findHospitals();
    log.info("groups: {}, depts: {}, month: {}", groups, depts, months);
    if (!Range.closed(DateTime.now().getYear() - 3, DateTime.now().getYear()).contains(year)) {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    } else if ("type".equals(groupBy) && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcRoot(groups, profitService.findRevenuesGroupByType(user.getSiteId(), user.getHospitalId(), year),
          profitService.findDeprecationsGroupByType(user.getSiteId(), user.getHospitalId(), year),
          profitService.findCostsGroupByType(user.getSiteId(), user.getHospitalId(), year)),
        year, groupBy, type, dept, month, limit, start);
    } else if (Option.of(groupBy).isEmpty() && Option.of(type).isDefined() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcChild(profitService.findRevenuesByType(user.getSiteId(), user.getHospitalId(), year, type),
          profitService.findDeprecationsByType(user.getSiteId(), user.getHospitalId(), year, type),
          profitService.findCostsByType(user.getSiteId(), user.getHospitalId(), year, type)),
        year, groupBy, type, dept, month, limit, start);
    } else if ("dept".equals(groupBy) && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcRoot(depts, profitService.findRevenuesGroupByDept(user.getSiteId(), user.getHospitalId(), year),
          profitService.findDeprecationsGroupByDept(user.getSiteId(), user.getHospitalId(), year),
          profitService.findCostsGroupByDept(user.getSiteId(), user.getHospitalId(), year)),
        year, groupBy, type, dept, month, limit, start);
    } else if (Option.of(groupBy).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isDefined() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcChild(profitService.findRevenuesByDept(user.getSiteId(), user.getHospitalId(), year, dept),
          profitService.findDeprecationsByDept(user.getSiteId(), user.getHospitalId(), year, dept),
          profitService.findCostsByDept(user.getSiteId(), user.getHospitalId(), year, dept)),
        year, groupBy, type, dept, month, limit, start);
    } else if ("month".equals(groupBy) && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcRoot(months, profitService.findRevenuesGroupByMonth(user.getSiteId(), user.getHospitalId(), year),
          profitService.findDeprecationsGroupByMonth(user.getSiteId(), user.getHospitalId(), year),
          profitService.findCostsGroupByMonth(user.getSiteId(), user.getHospitalId(), year)),
        year, groupBy, type, dept, month, limit, start);
    } else if (Option.of(groupBy).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isDefined()) {
      return serialize(request, groups, depts, months,
        calcChild(profitService.findRevenuesByMonth(user.getSiteId(), user.getHospitalId(), year, month),
          profitService.findDeprecationsByMonth(user.getSiteId(), user.getHospitalId(), year, month),
          profitService.findCostsByMonth(user.getSiteId(), user.getHospitalId(), year, month)),
        year, groupBy, type, dept, month, limit, start);
    } else if (Option.of(groupBy).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcChild(profitService.findRevenues(user.getSiteId(), user.getHospitalId(), year),
          profitService.findDeprecations(user.getSiteId(), user.getHospitalId(), year),
          profitService.findCosts(user.getSiteId(), user.getHospitalId(), year)),
        year, groupBy, type, dept, month, limit, start);
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }

  private Observable<Tuple4<Integer, String, Money, Money>> calcRoot(Map<Integer, String> map, Observable<Tuple2<Integer, Money>> revenues, Observable<Tuple2<Integer, Money>> deprecations, Observable<Tuple2<Integer, Money>> costs) {
    return Observable.zip(revenues, deprecations, costs, (revenue, deprecation, cost) ->
      Tuple.of(revenue._1, map.get(revenue._1), revenue._2, revenue._2.subtract(deprecation._2).subtract(cost._2))
    ).filter(t -> Option.of(t._2).isDefined()).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private Observable<Tuple4<Integer, String, Money, Money>> calcChild(Observable<Tuple3<Integer, String, Money>> revenues, Observable<Tuple3<Integer, String, Money>> deprecations, Observable<Tuple3<Integer, String, Money>> costs) {
    return Observable.zip(revenues, deprecations, costs, (revenue, deprecation, cost) ->
      Tuple.of(revenue._1, revenue._2, revenue._3, revenue._3.subtract(deprecation._3).subtract(cost._3))
    ).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private Iterable<ImmutableMap<String, Object>> mapItems(HttpServletRequest request, Observable<Tuple4<Integer, String, Money, Money>> children, int year, String groupBy, Integer type, Integer dept, Integer month, Integer limit, Integer start) {
    return children.skip(start).limit(Option.of(limit).getOrElse(Integer.MAX_VALUE)).map(child -> new ImmutableMap.Builder<String, Object>()
      .put("id", child._1)
      .put("name", Option.of(child._2).getOrElseThrow(() -> new IllegalArgumentException(String.format("snd value of %s is null", child))))
      .put("type", Option.of(groupBy).orElse(Option.of(type).map(i -> "type")).orElse(Option.of(dept).map(i -> "dept")).orElse(Option.of(month).map(i -> "month")).getOrElse("asset"))
      .put("revenue", child._3.getNumber().doubleValue())
      .put("profit", child._4.getNumber().doubleValue())
      .put("revenue_label", CNY.desc(child._3)._1)
      .put("revenue_label_unit", CNY.desc(child._3)._2)
      .put("profit_label", CNY.desc(child._4)._1)
      .put("profit_label_unit", CNY.desc(child._4)._2)
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "child")
        .put("href", Option.of(groupBy).map(v -> String.format("%s?year=%s&%s=%s", request.getRequestURL(), year, groupBy, child._1)).getOrElse(""))
        .build())
      .build()).cache().toBlocking().toIterable();
  }

  private ResponseEntity<Map<String, Object>> serialize(HttpServletRequest request, Map<Integer, String> groups, Map<Integer, String> depts, Map<Integer, String> months, Observable<Tuple4<Integer, String, Money, Money>> children, int year, String groupBy, Integer type, Integer dept, Integer month, Integer limit, Integer start) {
    Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
      .put("pages", new ImmutableMap.Builder<String, Object>()
        .put("total", children.count().toBlocking().single())
        .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
        .put("start", start)
        .build())
      .put("root", new ImmutableMap.Builder<String, Object>()
        .put("name", Option.of(groupBy).map(v -> "total")
          .orElse(Option.of(type).flatMap(v -> Option.of(groups.get(v))))
          .orElse(Option.of(dept).flatMap(v -> Option.of(groups.get(v))))
          .orElse(Option.of(month).flatMap(v -> Option.of(groups.get(v))))
          .getOrElse("asset"))
        .put("type", Option.of(groupBy).orElse(Option.of(type).map(i -> "type")).orElse(Option.of(dept).map(i -> "dept")).orElse(Option.of(month).map(i -> "month")).getOrElse("asset"))
        .put("revenue", ProfitService.sumRevenue(children))
        .put("profit", ProfitService.sumProfit(children))
        .put("revenue_label", ProfitService.descRevenues(children)._1)
        .put("revenue_label_unit", ProfitService.descRevenues(children)._2)
        .put("profit_label", ProfitService.descProfits(children)._1)
        .put("profit_label_unit", ProfitService.descProfits(children)._2)
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
      .put("items", mapItems(request, children, year, groupBy, type, dept, month, limit, start))
      .build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)).body(body);
  }
}
