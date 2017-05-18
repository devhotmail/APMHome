package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.FailureAnalysisService;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.Tuple7;
import javaslang.control.Option;
import javaslang.control.Try;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.Map;

import static javaslang.API.*;

@RestController
@RequestMapping("/fa")
@Validated
public class FailureAnalysisApi {
  private static final Logger log = LoggerFactory.getLogger(FailureAnalysisApi.class);
  @Autowired
  private CommonService commonService;

  @Autowired
  private FailureAnalysisService faService;

  @RequestMapping(path = "/briefs", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> briefs(HttpServletRequest request,
                                                              @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime from,
                                                              @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime to,
                                                              @Pattern(regexp = "dept|type|supplier|asset") @RequestParam(name = "groupby", required = true) String groupBy,
                                                              @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                              @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                              @Min(1) @RequestParam(name = "supplier", required = false) Integer supplier,
                                                              @Min(1) @RequestParam(name = "asset", required = false) Integer asset,
                                                              @RequestParam(name = "key", required = false) Integer[] keys,
                                                              @Pattern(regexp = "avail|ftfr|fix") @RequestParam(name = "orderby", required = true) String orderby,
                                                              @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(name = "limit", required = false) Integer limit,
                                                              @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start) {
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> types = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> suppliers = commonService.findSuppliers(user.getSiteId());
    Map<Integer, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, String>> assets = commonService.findAssets(user.getSiteId(), user.getHospitalId());
    Map<Integer, Integer> assetsByType = commonService.groupAssetsByType(user.getSiteId(), user.getHospitalId());
    Map<Integer, Integer> assetsByDept = commonService.groupAssetsByDept(user.getSiteId(), user.getHospitalId());
    Map<Integer, Integer> assetsBySupplier = commonService.groupAssetsBySupplier(user.getSiteId(), user.getHospitalId());
    Observable<Tuple3<Integer, Double, Integer>> report = faService.briefs(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), Match(groupBy).of(Case("dept", "dept_id"), Case("type", "asset_group"), Case("supplier", "supplier_id"), Case("asset", "asset_id")), dept, type, supplier, asset);
    Observable<Tuple3<Integer, Double, Integer>> filteredReport = Option.of(Tuple.of(report, keys)).filter(a -> Option.of(a._2).isDefined()).map(b -> Tuple.of(b._1, Arrays.asList(b._2))).map(c -> c._1.filter((t -> c._2.contains(t._1)))).getOrElse(report);
    return Match(Tuple.of(groupBy, filteredReport.sorted((l, r) -> Match(orderby).of(Case("avail", (int) (r._2 * 1000) - (int) (l._2 * 1000)), Case("ftfr", (int) (r._2 * 1000) - (int) (l._2 * 1000)), Case("fix", r._3 - l._3))).skip(start).limit(Option.of(limit).getOrElse(Integer.MAX_VALUE)).cache(), depts, types, suppliers, assets)).of(
      Case($(t -> "dept".equals(t._1)), t -> ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", report.count().toBlocking().single())
          .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
          .put("start", start)
          .build())
        .put("briefs", t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
          .put("key", new ImmutableMap.Builder<String, Object>()
            .put("id", tp._1)
            .put("name", t._3.getOrDefault(tp._1, "")).build())
          .put("val", new ImmutableMap.Builder<String, Object>()
            .put("avail", tp._2)
            .put("ftfr", tp._2 * 0.95D)
            .put("fix", tp._3).build()).build()).toBlocking().toIterable())
        .build())),
      Case($(t -> "type".equals(t._1)), t -> ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", report.count().toBlocking().single())
          .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
          .put("start", start)
          .build())
        .put("briefs", t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
          .put("key", new ImmutableMap.Builder<String, Object>()
            .put("id", tp._1)
            .put("name", t._4.getOrDefault(tp._1, "")).build())
          .put("val", new ImmutableMap.Builder<String, Object>()
            .put("avail", tp._2)
            .put("ftfr", tp._2 * 0.95D)
            .put("fix", tp._3).build()).build()).toBlocking().toIterable())
        .build())),
      Case($(t -> "supplier".equals(t._1)), t -> ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", report.count().toBlocking().single())
          .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
          .put("start", start)
          .build())
        .put("briefs", t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
          .put("key", new ImmutableMap.Builder<String, Object>()
            .put("id", tp._1)
            .put("name", t._5.getOrDefault(tp._1, "")).build())
          .put("val", new ImmutableMap.Builder<String, Object>()
            .put("avail", tp._2)
            .put("ftfr", tp._2 * 0.95D)
            .put("fix", tp._3).build()).build()).toBlocking().toIterable())
        .build())),
      Case($(t -> "asset".equals(t._1)), t -> ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", report.count().toBlocking().single())
          .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
          .put("start", start)
          .build())
        .put("briefs", t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
          .put("key", new ImmutableMap.Builder<String, Object>()
            .put("id", tp._1)
            .put("name", Try.of(() -> t._6.get(tp._1)._7).getOrElse(""))
            .put("type", Try.of(() -> t._6.get(tp._1)._5).getOrElse(0))
            .put("dept", Try.of(() -> t._6.get(tp._1)._4).getOrElse(0))
            .put("supplier", Try.of(() -> t._6.get(tp._1)._6).getOrElse(0)).build())
          .put("val", new ImmutableMap.Builder<String, Object>()
            .put("avail", tp._2)
            .put("ftfr", tp._2 * 0.95D)
            .put("fix", tp._3).build()).build()).toBlocking().toIterable())
        .build())),
      Case($(), ResponseEntity.badRequest().body(ImmutableMap.of()))
    );
  }


  @RequestMapping(path = "/details", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> details(HttpServletRequest request,
                                                               @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime from,
                                                               @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime to,
                                                               @Pattern(regexp = "dept|type|supplier|asset") @RequestParam(name = "groupby", required = true) String groupBy,
                                                               @Min(1) @RequestParam(name = "key", required = true) Integer key) {
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> types = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> suppliers = commonService.findSuppliers(user.getSiteId());
    Map<Integer, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, String>> assets = commonService.findAssets(user.getSiteId(), user.getHospitalId());
    Map<Integer, Integer> assetsByType = commonService.groupAssetsByType(user.getSiteId(), user.getHospitalId());
    Map<Integer, Integer> assetsByDept = commonService.groupAssetsByDept(user.getSiteId(), user.getHospitalId());
    Map<Integer, Integer> assetsBySupplier = commonService.groupAssetsBySupplier(user.getSiteId(), user.getHospitalId());
    Observable<Tuple3<Integer, Double, Integer>> report = faService.details(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), Match(groupBy).of(Case("dept", "dept_id"), Case("type", "asset_group"), Case("supplier", "supplier_id"), Case("asset", "asset_id")), key);
    return Match(Tuple.of(groupBy, report, depts, types, suppliers, assets)).of(
      Case($(t -> "dept".equals(t._1)), t -> ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>().put("briefs", Try.of(() -> t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
        .put("key", new ImmutableMap.Builder<String, Object>()
          .put("id", tp._1)
          .put("name", t._3.getOrDefault(tp._1, "")).build())
        .put("val", new ImmutableMap.Builder<String, Object>()
          .put("avail", tp._2)
          .put("ftfr", tp._2 * 0.95D)
          .put("fix", tp._3).build()).build()).toBlocking().single()).getOrElse(ImmutableMap.of()))
        .build())),
      Case($(t -> "type".equals(t._1)), t -> ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>().put("briefs", Try.of(() -> t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
        .put("key", new ImmutableMap.Builder<String, Object>()
          .put("id", tp._1)
          .put("name", t._4.getOrDefault(tp._1, "")).build())
        .put("val", new ImmutableMap.Builder<String, Object>()
          .put("avail", tp._2)
          .put("ftfr", tp._2 * 0.95D)
          .put("fix", tp._3).build()).build()).toBlocking().single()).getOrElse(ImmutableMap.of()))
        .build())),
      Case($(t -> "supplier".equals(t._1)), t -> ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>().put("briefs", Try.of(() -> t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
        .put("key", new ImmutableMap.Builder<String, Object>()
          .put("id", tp._1)
          .put("name", t._5.getOrDefault(tp._1, "")).build())
        .put("val", new ImmutableMap.Builder<String, Object>()
          .put("avail", tp._2)
          .put("ftfr", tp._2 * 0.95D)
          .put("fix", tp._3).build()).build()).toBlocking().single()).getOrElse(ImmutableMap.of()))
        .build())),
      Case($(t -> "asset".equals(t._1)), t -> ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>().put("briefs", Try.of(() -> t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
        .put("key", new ImmutableMap.Builder<String, Object>()
          .put("id", tp._1)
          .put("name", Try.of(() -> t._6.get(tp._1)._7).getOrElse(""))
          .put("type", Try.of(() -> t._6.get(tp._1)._5).getOrElse(0))
          .put("dept", Try.of(() -> t._6.get(tp._1)._4).getOrElse(0))
          .put("supplier", Try.of(() -> t._6.get(tp._1)._6).getOrElse(0)).build())
        .put("val", new ImmutableMap.Builder<String, Object>()
          .put("avail", tp._2)
          .put("ftfr", tp._2 * 0.95D)
          .put("fix", tp._3).build()).build()).toBlocking().single()).getOrElse(ImmutableMap.of()))
        .build())),
      Case($(), ResponseEntity.badRequest().body(ImmutableMap.of()))
    );
  }


  @RequestMapping(path = "/reasons", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> reasons(HttpServletRequest request,
                                                               @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime from,
                                                               @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime to,
                                                               @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                               @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                               @Min(1) @RequestParam(name = "supplier", required = false) Integer supplier,
                                                               @Min(1) @RequestParam(name = "asset", required = false) Integer asset,
                                                               @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(name = "limit", required = false) Integer limit,
                                                               @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start) {
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> reasons = Observable.from(commonService.findFields(user.getSiteId(), "caseType").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    return ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>()
      .put("reasons", faService.reasons(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), dept, type, supplier, asset).skip(start).limit(Option.of(limit).getOrElse(Integer.MAX_VALUE))
        .map(t -> new ImmutableMap.Builder<String, Object>().put("id", t._1).put("name", reasons.getOrDefault(t._1, "")).put("count", t._2).build()).toBlocking().toIterable()).build());
  }

}

