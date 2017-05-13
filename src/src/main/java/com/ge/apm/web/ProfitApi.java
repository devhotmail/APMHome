package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.ProfitService;
import com.ge.apm.service.utils.CNY;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import javaslang.*;
import javaslang.collection.List;
import javaslang.collection.Seq;
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
                                                        @Min(1) @RequestParam(value = "month", required = false) Integer month,
                                                        @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false) Integer limit,
                                                        @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    log.info("year:{}, groupby:{}, type:{}, dept:{}, month:{}, limit:{}, start:{}, from:{}, to:{}", year, groupBy, type, dept, month, limit, start, from, to);
    LocalDate startDate;
    LocalDate endDate;
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> months = Observable.from(commonService.findFields(user.getSiteId(), "month").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
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
    months = ProfitService.calculateMonth(startDate, endDate, months);
    log.info("groups: {}, depts: {}, month: {}", groups, depts, months);
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
    return rvnCsts.map(v -> Tuple.of(v._1, map.get(v._1), v._2, v._3.add(v._4)))
      .filter(t -> Option.of(t._2).isDefined()).sorted((l, r) -> r._3.getNumber().intValue() - l._3.getNumber().intValue()).cache();
  }

  private Observable<Tuple4<Integer, String, Money, Money>> calcChild(Observable<Tuple5<Integer, String, Money, Money, Money>> rvnCsts) {
    return rvnCsts.map(v -> Tuple.of(v._1, v._2, v._3, v._4.add(v._5)))
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
      .put("cost", child._4.getNumber().doubleValue())
      .put("revenue_label", CNY.desc(child._3)._1)
      .put("revenue_label_unit", CNY.desc(child._3)._2)
      .put("cost_label", CNY.desc(child._4)._1)
      .put("cost_label_unit", CNY.desc(child._4)._2)
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
        .put("cost", ProfitService.sumProfit(children))
        .put("revenue_label", ProfitService.descRevenues(children)._1)
        .put("revenue_label_unit", ProfitService.descRevenues(children)._2)
        .put("cost_label", ProfitService.descProfits(children)._1)
        .put("cost_label_unit", ProfitService.descProfits(children)._2)
        .put("revenue_text", "总收入")
        .put("cost_text", "总成本")
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

  //forecast
  @RequestMapping(value = "/forecast", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> calcFutureProfit(HttpServletRequest request,
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
    if (!(Option.of(from).isDefined() && Option.of(to).isDefined() && LocalDate.parse(from).compareTo(LocalDate.now().minusYears(3)) >= 0)) {
      if (Range.closed(DateTime.now().plusYears(1).getYear() - 3, DateTime.now().plusYears(1).getYear()).contains(year)) {
        startDate = LocalDate.of(year, 1, 1);
        endDate = LocalDate.of(year, 12, 31);
      } else {
        return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "you must offer a valid time period"));
      }
    } else {
      startDate = LocalDate.parse(from);
      endDate = LocalDate.parse(to);
    }
    log.info("groups: {}, depts: {}, month: {}", groups, depts, months);
    if (Option.of(groupBy).isDefined() && Option.of(type).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        aggregateCstRvnByGroup(profitService.predict(user.getSiteId(), user.getHospitalId(), LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now(), startDate.getYear()), groups, depts, months, groupBy)
          .map(v -> Tuple.of(v._1, v._2, CNY.money(v._3), CNY.money(v._4))),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else if ((Option.of(groupBy).isEmpty() && List.of(type, month).count(v -> Option.of(v).isDefined()) == 1) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isDefined()) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty())) {
      return serialize(request, groups, depts, months,
        filterCstRvnByGroup(profitService.predict(user.getSiteId(), user.getHospitalId(), LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now(), startDate.getYear()), type, dept, month),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }

  @RequestMapping(value = "/forecast", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> calcFutureProfitPut(HttpServletRequest request,
                                                                 @Min(2000) @RequestParam(value = "year", required = false) Integer year,
                                                                 @RequestParam(value = "from", required = false) String from,
                                                                 @RequestParam(value = "to", required = false) String to,
                                                                 @Pattern(regexp = "type|dept|month") @RequestParam(value = "groupby", required = false) String groupBy,
                                                                 @Min(1) @RequestParam(value = "type", required = false) Integer type,
                                                                 @Min(1) @RequestParam(value = "dept", required = false) Integer dept,
                                                                 @Min(1) @Max(12) @RequestParam(value = "month", required = false) Integer month,
                                                                 @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false) Integer limit,
                                                                 @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                                 @RequestBody(required = true) String inputBody) {
    log.info("year:{}, groupby:{}, type:{}, dept:{}, month:{}, limit:{}, start:{}, from:{}, to:{}", year, groupBy, type, dept, month, limit, start, from, to);
    LocalDate startDate;
    LocalDate endDate;
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> months = Observable.from(commonService.findFields(user.getSiteId(), "month").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Seq<Tuple5<Option<Integer>, Option<Integer>, Option<Integer>, Option<Double>, Option<Double>>> userPre = ProfitService.parseJson(inputBody);
    if (!(Option.of(from).isDefined() && Option.of(to).isDefined() && LocalDate.parse(from).compareTo(LocalDate.now().minusYears(3)) >= 0)) {
      if (Range.closed(DateTime.now().getYear() - 3, DateTime.now().plusYears(1).getYear()).contains(year)) {
        startDate = LocalDate.of(year, 1, 1);
        endDate = LocalDate.of(year, 12, 31);
      } else {
        return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "you must offer a valid time period"));
      }
    } else {
      startDate = LocalDate.parse(from);
      endDate = LocalDate.parse(to);
    }
    log.info("groups: {}, depts: {}, month: {}", groups, depts, months);
    if (Option.of(groupBy).isDefined() && Option.of(type).isEmpty() && Option.of(month).isEmpty()) {
      return serialize(request, groups, depts, months,
        aggregateCstRvnByGroup(userPredict(profitService.predict(user.getSiteId(), user.getHospitalId(), LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now(), startDate.getYear()),
          profitService.lastYearData(user.getSiteId(), user.getHospitalId()), userPre),
          groups, depts, months, groupBy)
          .map(v -> Tuple.of(v._1, v._2, CNY.money(v._3), CNY.money(v._4))),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else if ((Option.of(groupBy).isEmpty() && List.of(type, month).count(v -> Option.of(v).isDefined()) == 1) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isDefined()) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty())) {
      return serialize(request, groups, depts, months,
        filterCstRvnByGroup(userPredict(profitService.predict(user.getSiteId(), user.getHospitalId(), LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now(), startDate.getYear()),
          profitService.lastYearData(user.getSiteId(), user.getHospitalId()), userPre),
          type, dept, month),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }


  //input: Tuple7<id, name, month, type, dept, revenue, cost>
  private Observable<Tuple4<Integer, String, Double, Double>> aggregateCstRvnByGroup(Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> items,
                                                                                     Map<Integer, String> groups, Map<Integer, String> depts, Map<Integer, String> months,
                                                                                     String groupBy) {
    return Observable.from(
      items.groupBy(v -> Option.when("type".equals(groupBy), v._4).getOrElse(() -> Option.when("dept".equals(groupBy), v._5).getOrElse(() -> v._3)))
        .map(v -> Tuple.of(v._1,
          Option.when("type".equals(groupBy), groups.get(v._1)).getOrElse(() -> Option.when("dept".equals(groupBy), depts.get(v._1)).getOrElse(() -> months.get(v._1))),
          v._2.map(sub -> sub._6).sum().doubleValue(), v._2.map(sub -> sub._7).sum().doubleValue()
        )));
  }

  private Observable<Tuple4<Integer, String, Money, Money>> filterCstRvnByGroup(Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> items,
                                                                                Integer type, Integer dept, Integer month) {
    return Observable.from(
      items.filter(v -> Option.of(type).map(sub -> sub.equals(v._4)).orElse(Option.of(dept).map(sub -> sub.equals(v._5))).orElse(Option.of(month).map(sub -> sub.equals(v._3))).getOrElse(true))
        .groupBy(v -> v._1)
        .map(v -> Tuple.of(v._1, v._2.get(0)._2, CNY.money(v._2.map(sub -> sub._6).sum().doubleValue()), CNY.money(v._2.map(sub -> sub._7).sum().doubleValue())))
    );
  }


  //forecast rate
  @RequestMapping(value = "/forecastrate", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> calcFutureRate(HttpServletRequest request,
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
    if (!(Option.of(from).isDefined() && Option.of(to).isDefined() && LocalDate.parse(from).compareTo(LocalDate.now().minusYears(3)) >= 0)) {
      if (Range.closed(DateTime.now().getYear() - 3, DateTime.now().plusYears(1).getYear()).contains(year)) {
        startDate = LocalDate.of(year, 1, 1);
        endDate = LocalDate.of(year, 12, 31);
      } else {
        return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "you must offer a valid time period"));
      }
    } else {
      startDate = LocalDate.parse(from);
      endDate = LocalDate.parse(to);
    }
    log.info("groups: {}, depts: {}, month: {}", groups, depts, months);
    if (Option.of(groupBy).isDefined() && Option.of(type).isEmpty() && Option.of(month).isEmpty()) {
      return serializeRate(request, groups, depts, months,
        cstRvnRateByGroup(profitService.predict(user.getSiteId(), user.getHospitalId(), LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now(), startDate.getYear()),
          profitService.lastYearData(user.getSiteId(), user.getHospitalId()),
          groups, depts, months, groupBy),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else if ((Option.of(groupBy).isEmpty() && List.of(type, month).count(v -> Option.of(v).isDefined()) == 1) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isDefined()) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty())) {
      return serializeRate(request, groups, depts, months,
        cstRvnRateBySubGroup(profitService.predict(user.getSiteId(), user.getHospitalId(), LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now(), startDate.getYear()),
          profitService.lastYearData(user.getSiteId(), user.getHospitalId()),
          type, dept, month, groups),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }

  @RequestMapping(value = "/forecastrate", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> calcFutureRatePut(HttpServletRequest request,
                                                               @Min(2000) @RequestParam(value = "year", required = false) Integer year,
                                                               @RequestParam(value = "from", required = false) String from,
                                                               @RequestParam(value = "to", required = false) String to,
                                                               @Pattern(regexp = "type|dept|month") @RequestParam(value = "groupby", required = false) String groupBy,
                                                               @Min(1) @RequestParam(value = "type", required = false) Integer type,
                                                               @Min(1) @RequestParam(value = "dept", required = false) Integer dept,
                                                               @Min(1) @Max(12) @RequestParam(value = "month", required = false) Integer month,
                                                               @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false) Integer limit,
                                                               @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                               @RequestBody(required = true) String inputBody) {
    log.info("year:{}, groupby:{}, type:{}, dept:{}, month:{}, limit:{}, start:{}, from:{}, to:{}", year, groupBy, type, dept, month, limit, start, from, to);
    LocalDate startDate;
    LocalDate endDate;
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> months = Observable.from(commonService.findFields(user.getSiteId(), "month").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Seq<Tuple5<Option<Integer>, Option<Integer>, Option<Integer>, Option<Double>, Option<Double>>> userPre = ProfitService.parseJson(inputBody);
    if (!(Option.of(from).isDefined() && Option.of(to).isDefined() && LocalDate.parse(from).compareTo(LocalDate.now().minusYears(3)) >= 0)) {
      if (Range.closed(DateTime.now().getYear() - 3, DateTime.now().plusYears(1).getYear()).contains(year)) {
        startDate = LocalDate.of(year, 1, 1);
        endDate = LocalDate.of(year, 12, 31);
      } else {
        return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "you must offer a valid time period"));
      }
    } else {
      startDate = LocalDate.parse(from);
      endDate = LocalDate.parse(to);
    }
    log.info("groups: {}, depts: {}, month: {}", groups, depts, months);
    if (Option.of(groupBy).isDefined() && Option.of(type).isEmpty() && Option.of(month).isEmpty()) {
      return serializeRate(request, groups, depts, months,
        cstRvnRateByGroup(userPredict(profitService.predict(user.getSiteId(), user.getHospitalId(), LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now(), startDate.getYear()),
          profitService.lastYearData(user.getSiteId(), user.getHospitalId()), userPre),
          profitService.lastYearData(user.getSiteId(), user.getHospitalId()),
          groups, depts, months, groupBy),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else if ((Option.of(groupBy).isEmpty() && List.of(type, month).count(v -> Option.of(v).isDefined()) == 1) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isDefined()) ||
      (Option.of(groupBy).isEmpty() && Option.of(month).isEmpty() && Option.of(type).isEmpty() && Option.of(dept).isEmpty())) {
      return serializeRate(request, groups, depts, months,
        cstRvnRateBySubGroup(userPredict(profitService.predict(user.getSiteId(), user.getHospitalId(), LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now(), startDate.getYear()),
          profitService.lastYearData(user.getSiteId(), user.getHospitalId()), userPre),
          profitService.lastYearData(user.getSiteId(), user.getHospitalId()),
          type, dept, month, groups),
        startDate, endDate, groupBy, type, dept, month, limit, start);
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }


  private Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> userPredict(Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> predicts,
                                                                                              Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> lstY,
                                                                                              Seq<Tuple5<Option<Integer>, Option<Integer>, Option<Integer>, Option<Double>, Option<Double>>> userPre) {
    return predicts.zip(lstY).map(v -> Tuple.of(
      v._1._1, v._1._2, v._1._3, v._1._4, v._1._5,
      userPre.find(sub -> sub._1.map(opt -> opt.equals(v._1._4)).getOrElse(true) &&
        sub._2.map(opt -> opt.equals(v._1._5)).getOrElse(true) &&
        sub._3.map(opt -> opt.equals(v._1._3)).getOrElse(true) &&
        sub._4.isDefined()
      ).map(sub -> v._2._6 * (sub._4.get() + 1D)).getOrElse(v._1._6),
      userPre.find(sub -> sub._1.map(opt -> opt.equals(v._1._4)).getOrElse(true) &&
        sub._2.map(opt -> opt.equals(v._1._5)).getOrElse(true) &&
        sub._3.map(opt -> opt.equals(v._1._3)).getOrElse(true) &&
        sub._5.isDefined()
      ).map(sub -> v._2._7 * (sub._5.get() + 1D)).getOrElse(v._1._7)
    ));
  }


  //input: Tuple7<id, name, month, type, dept, revenue, cost>
  //output: Tuple4<id, name, Tuple3<pred_revenue, lst_revenue_rate>, Tuple3<pred_cost, lst_cost, cost_rate>>
  private Observable<Tuple4<Integer, String, Tuple3<Double, Double, Double>, Tuple3<Double, Double, Double>>> cstRvnRateByGroup(Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> predicts,
                                                                                                                                Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> lstY,
                                                                                                                                Map<Integer, String> groups, Map<Integer, String> depts, Map<Integer, String> months,
                                                                                                                                String groupBy) {
    return aggregateCstRvnByGroup(predicts, groups, depts, months, groupBy)
      .zipWith(aggregateCstRvnByGroup(lstY, groups, depts, months, groupBy), (pred, lst) ->
        Tuple.of(pred._1, pred._2,
          Tuple.of(pred._3, lst._3, Option.when(lst._3.equals(0D), 0D).getOrElse(() -> pred._3 / lst._3 - 1D)),
          Tuple.of(pred._4, lst._4, Option.when(lst._4.equals(0D), 0D).getOrElse(() -> pred._4 / lst._4 - 1D))
        ));
  }

  //input: Tuple7<id, name, month, type, dept, revenue, cost>
  //output: Tuple4<id, name, revenue_rate, cost_rate>
  private Observable<Tuple4<Integer, String, Double, Double>> aggregateCstRvnBySubGroup(Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> items,
                                                                                        Integer type, Integer dept, Integer month, Map<Integer, String> groups) {
    return Observable.from(
      items.filter(v -> Option.of(type).map(sub -> sub.equals(v._4)).orElse(Option.of(dept).map(sub -> sub.equals(v._5))).getOrElse(() -> month.equals(v._3)))
        .groupBy(v -> v._4)
        .map(v -> Tuple.of(v._1, groups.get(v._1), v._2.map(sub -> sub._6).sum().doubleValue(), v._2.map(sub -> sub._7).sum().doubleValue()))
    );
  }

  //input: Tuple7<id, name, month, type, dept, revenue, cost>
  //output: Tuple4<id, name, Tuple3<pred_revenue, lst_revenue_rate>, Tuple3<pred_cost, lst_cost, cost_rate>>
  private Observable<Tuple4<Integer, String, Tuple3<Double, Double, Double>, Tuple3<Double, Double, Double>>> cstRvnRateBySubGroup(Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> predicts,
                                                                                                                                   Seq<Tuple7<Integer, String, Integer, Integer, Integer, Double, Double>> lstY,
                                                                                                                                   Integer type, Integer dept, Integer month, Map<Integer, String> groups) {
    return aggregateCstRvnBySubGroup(predicts, type, dept, month, groups)
      .zipWith(aggregateCstRvnBySubGroup(lstY, type, dept, month, groups), (pred, lst) ->
        Tuple.of(pred._1, pred._2,
          Tuple.of(pred._3, lst._3, Option.when(lst._3.equals(0D), 0D).getOrElse(() -> pred._3 / lst._3 - 1D)),
          Tuple.of(pred._4, lst._4, Option.when(lst._4.equals(0D), 0D).getOrElse(() -> pred._4 / lst._4 - 1D))
        ));
  }

  private Iterable<ImmutableMap<String, Object>> mapItemsRate(HttpServletRequest request, Observable<Tuple4<Integer, String, Tuple3<Double, Double, Double>, Tuple3<Double, Double, Double>>> children, LocalDate from, LocalDate to, String groupBy, Integer type, Integer dept, Integer month, Integer limit, Integer start) {
    return children.skip(start).limit(Option.of(limit).getOrElse(Integer.MAX_VALUE)).map(child -> new ImmutableMap.Builder<String, Object>()
      .put("id", child._1)
      .put("name", Option.of(child._2).getOrElseThrow(() -> new IllegalArgumentException(String.format("snd value of %s is null", child))))
      .put("type", Option.of(groupBy).orElse(Option.of(type).map(i -> "type")).orElse(Option.of(dept).map(i -> "dept")).orElse(Option.of(month).map(i -> "month")).getOrElse("asset"))
      .put("revenue_increase", child._3._3)
      .put("cost_increase", child._4._3)
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "child")
        .put("href", Option.of(groupBy).map(v -> String.format("%s?from=%s&to=%s&%s=%s", request.getRequestURL().toString(), from, to, groupBy, child._1)).getOrElse(""))
        .build())
      .build()).cache().toBlocking().toIterable();
  }

  private ResponseEntity<Map<String, Object>> serializeRate(HttpServletRequest request, Map<Integer, String> groups, Map<Integer, String> depts, Map<Integer, String> months, Observable<Tuple4<Integer, String, Tuple3<Double, Double, Double>, Tuple3<Double, Double, Double>>> children, LocalDate from, LocalDate to, String groupBy, Integer type, Integer dept, Integer month, Integer limit, Integer start) {
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
        .put("revenue_increase", ProfitService.calcIncRate(children.map(v -> Tuple.of(v._3._1, v._3._2))))
        .put("cost_increase", ProfitService.calcIncRate(children.map(v -> Tuple.of(v._4._1, v._4._2))))
        .put("link", new ImmutableMap.Builder<String, Object>()
          .put("ref", "self")
          .put("href", Option.of(groupBy).map(v -> String.format("%s?from=%s&to=%s&groupby=%s", request.getRequestURL().toString(), from, to, v))
            .orElse(Option.of(type).map(v -> String.format("%s?from=%s&to=%s&groupby=%s", request.getRequestURL().toString(), from, to, v)))
            .orElse(Option.of(dept).map(v -> String.format("%s?from=%s&to=%s&groupby=%s", request.getRequestURL().toString(), from, to, v)))
            .orElse(Option.of(dept).map(v -> String.format("%s?from=%s&to=%s&groupby=%s", request.getRequestURL().toString(), from, to, v)))
            .getOrElse(String.format("%s?from=%s&to=%s", request.getRequestURL(), from, to)))
          .build())
        .build())
      .put("items", mapItemsRate(request, children, from, to, groupBy, type, dept, month, limit, start))
      .build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)).body(body);
  }
}
