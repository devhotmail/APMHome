package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.ProfitService;
import com.ge.apm.service.utils.CNY;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple4;
import javaslang.Tuple5;
import javaslang.collection.List;
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
import rx.observables.MathObservable;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
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
                                                        @Min(2000) @RequestParam(value = "year", required = false) Integer year,
                                                        @RequestParam(value = "from", required = false) String from,
                                                        @RequestParam(value = "to", required = false) String to,
                                                        @Pattern(regexp = "type|dept|month") @RequestParam(value = "groupby", required = false) String groupBy,
                                                        @Min(1) @RequestParam(value = "type", required = false) Integer type,
                                                        @Min(1) @RequestParam(value = "dept", required = false) Integer dept,
                                                        @Min(1) @Max(12) @RequestParam(value = "month", required = false) Integer month,
                                                        @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false) Integer limit,
                                                        @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    log.info("year:{}, groupby:{}, type:{}, dept:{}, month:{}, limit:{}, start:{}, from:{}, to:{}", year, groupBy, type, dept, month, limit, start, from, to);
    LocalDate startDate;
    LocalDate endDate;
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> months = Observable.from(commonService.findFields(user.getSiteId(), "month").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    log.info("groups: {}, depts: {}, month: {}", groups, depts, months);
    if (!(Option.of(from).isDefined() && Option.of(to).isDefined() && LocalDate.parse(from).compareTo(LocalDate.now().minusYears(3)) >= 0 && LocalDate.parse(to).compareTo(LocalDate.now()) <= 0)) {
      if (Range.closed(DateTime.now().getYear() - 3, DateTime.now().getYear()).contains(year)) {
        startDate = LocalDate.of(year, 1, 1);
        endDate = LocalDate.of(year, 12, 31);
      } else {
        return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "you must offer a valid time period"));
      }
    } else {
      startDate = LocalDate.parse(from);
      endDate = LocalDate.parse(to);
    }
    if (Option.of(groupBy).isDefined() && Option.of(type).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        calcRoot(Option.when("type".equals(groupBy), groups).getOrElse(Option.when("dept".equals(groupBy), depts).getOrElse(months)),
          profitService.findRvnCstForEachGroup(user.getSiteId(), user.getHospitalId(), startDate, endDate, groupBy, dept, month, type)),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else if ((Option.of(groupBy).isEmpty() && List.of(type, month).count(v -> Option.of(v).isDefined()) == 1) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isDefined()) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty())) {
      return serialize(request, groups, depts, months,
        calcChild(profitService.findRvnCstForEachItem(user.getSiteId(), user.getHospitalId(), startDate, endDate, dept, month, type)),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }

  private Observable<Tuple4<Integer, String, Money, Money>> calcRoot(Map<Integer, String> map, Observable<Tuple4<Integer, Money, Money, Money>> rvnCsts) {
    return rvnCsts.map(v -> Tuple.of(v._1, map.get(v._1), v._2, v._2.subtract(v._3).subtract(v._4)))
      .filter(t -> Option.of(t._2).isDefined()).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private Observable<Tuple4<Integer, String, Money, Money>> calcChild(Observable<Tuple5<Integer, String, Money, Money, Money>> rvnCsts) {
    return rvnCsts.map(v -> Tuple.of(v._1, v._2, v._3, v._3.subtract(v._4).subtract(v._5)))
      .filter(t -> Option.of(t._2).isDefined()).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private Observable<Tuple4<Integer, String, Money, Money>> forecastRvnCst(Observable<Tuple4<Integer, String, Money, Money>> rvnCsts) {
    return Observable.zip(rvnCsts, Observable.zip(MathObservable.sumDouble(rvnCsts.map(t -> t._3.getNumber().doubleValue())).cache(), MathObservable.sumDouble(rvnCsts.map(t -> t._4.getNumber().doubleValue())).cache(), (r, p) -> Tuple.of(ProfitService.predictRevenue().getNumber().doubleValue(), r, p)).repeat(),
      (l, r) -> Tuple.of(l._1, l._2, CNY.money(r._1 * l._3.getNumber().doubleValue() / r._2), CNY.money((r._1 * r._3 / r._2) * l._4.getNumber().doubleValue() / r._3)));
  }

  private Iterable<ImmutableMap<String, Object>> mapItems(HttpServletRequest request, Observable<Tuple4<Integer, String, Money, Money>> children, LocalDate from, LocalDate to, String groupBy, Integer type, Integer dept, Integer month, Integer limit, Integer start) {
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
        .put("href", Option.of(groupBy).map(v -> String.format("%s?from=%s&to=%s&%s=%s", request.getRequestURL().toString(), from, to, groupBy, child._1)).getOrElse(""))
        .build())
      .build()).cache().toBlocking().toIterable();
  }

  private ResponseEntity<Map<String, Object>> serialize(HttpServletRequest request, Map<Integer, String> groups, Map<Integer, String> depts, Map<Integer, String> months, Observable<Tuple4<Integer, String, Money, Money>> children, LocalDate from, LocalDate to, String groupBy, Integer type, Integer dept, Integer month, Integer limit, Integer start) {
    Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
      .put("pages", new ImmutableMap.Builder<String, Object>()
        .put("total", children.count().toBlocking().single())
        .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
        .put("start", start)
        .build())
      .put("root", new ImmutableMap.Builder<String, Object>()
        .put("name", Option.of(groupBy).map(v -> "total")
          .orElse(Option.of(type).flatMap(v -> Option.of(groups.get(v))))
          .orElse(Option.of(dept).flatMap(v -> Option.of(depts.get(v))))
          .orElse(Option.of(month).flatMap(v -> Option.of(months.get(v))))
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
          .put("href", Option.of(groupBy).map(v -> String.format("%s?from=%s&to=%s&groupby=%s", request.getRequestURL().toString(), from, to, v))
            .orElse(Option.of(type).map(v -> String.format("%s?from=%s&to=%s&groupby=%s", request.getRequestURL().toString(), from, to, v)))
            .orElse(Option.of(dept).map(v -> String.format("%s?from=%s&to=%s&groupby=%s", request.getRequestURL().toString(), from, to, v)))
            .orElse(Option.of(dept).map(v -> String.format("%s?from=%s&to=%s&groupby=%s", request.getRequestURL().toString(), from, to, v)))
            .getOrElse(String.format("%s?from=%s&to=%s", request.getRequestURL(), from, to)))
          .build())
        .build())
      .put("items", mapItems(request, children, from, to, groupBy, type, dept, month, limit, start))
      .build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)).body(body);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/forecast")
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> forecastProfit(HttpServletRequest request,
//                                                                    @Future @RequestParam(name = "year", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime year,
//                                                                    @Future @RequestParam(name = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime from,
//                                                                    @Future @RequestParam(name = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime to,
                                                                      @Pattern(regexp = "type|dept|month") @RequestParam(name = "groupby", required = false) String groupBy,
                                                                      @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                                      @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                                      @Min(1) @Max(12) @RequestParam(name = "month", required = false) Integer month,
                                                                      @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(name = "limit", required = false) Integer limit,
                                                                      @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start) {
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> months = Observable.from(commonService.findFields(user.getSiteId(), "month").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
//    return Match(Tuple.of(request, groupBy, type, dept, month, limit, start)).of(
////      Case($(t -> List.of(t._2, t._3, t._4).forAll(p -> Option.of(p).isEmpty())), t -> serialize(request, groups, depts, months,
////        calcChild(profitService.findRvnCstByYear(user.getSiteId(), user.getHospitalId(), year)),
////        year, groupBy, type, dept, month, limit, start)),
//      Case($(), ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported")))
//    );
    final LocalDate startDate = LocalDate.now().minusYears(1);
    final LocalDate endDate = LocalDate.now().minusDays(1);
    if (Option.of(groupBy).isDefined() && Option.of(type).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        forecastRvnCst(calcRoot(Option.when("type".equals(groupBy), groups).getOrElse(Option.when("dept".equals(groupBy), depts).getOrElse(months)),
          profitService.findRvnCstForEachGroup(user.getSiteId(), user.getHospitalId(), startDate, endDate, groupBy, dept, month, type))),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else if ((Option.of(groupBy).isEmpty() && List.of(type, month).count(v -> Option.of(v).isDefined()) == 1) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isDefined()) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty())) {
      return serialize(request, groups, depts, months,
        forecastRvnCst(calcChild(profitService.findRvnCstForEachItem(user.getSiteId(), user.getHospitalId(), startDate, endDate, dept, month, type))),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }


}
