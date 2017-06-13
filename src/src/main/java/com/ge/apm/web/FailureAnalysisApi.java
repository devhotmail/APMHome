package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.FailureAnalysisService;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.Tuple4;
import javaslang.Tuple7;
import javaslang.control.Option;
import javaslang.control.Try;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
                                                              @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(name = "pm", required = false, defaultValue = "30") Integer pmv,
                                                              @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(name = "limit", required = false) Integer limit,
                                                              @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start) {
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> types = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> suppliers = commonService.findSuppliers(user.getSiteId());
    Map<Integer, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, String>> assets = commonService.findAssets(user.getSiteId(), user.getHospitalId());
    Observable<Tuple4<Integer, Double, Double, Integer>> report = faService.briefs(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), Match(groupBy).of(Case("dept", "clinical_dept_id"), Case("type", "asset_group"), Case("supplier", "supplier_id"), Case("asset", "id")), dept, type, supplier, asset, pmv);
    Observable<Tuple4<Integer, Double, Double, Integer>> filteredReport = Option.of(Tuple.of(report, keys)).filter(a -> Option.of(a._2).isDefined()).map(b -> Tuple.of(b._1, Arrays.asList(b._2))).map(c -> c._1.filter((t -> c._2.contains(t._1)))).getOrElse(report);
    return Match(Tuple.of(groupBy, filteredReport.sorted((l, r) -> Match(orderby).of(Case("avail", Double.compare(r._2, l._2)), Case("ftfr", Double.compare(r._3, l._3)), Case("fix", r._4 - l._4))).skip(start).limit(Option.of(limit).getOrElse(Integer.MAX_VALUE)).cache(), depts, types, suppliers, assets)).of(
      Case($(t -> "dept".equals(t._1)), t -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", report.count().toBlocking().single())
          .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
          .put("start", Match(Tuple.of(keys, start)).of(Case($(a -> Option.of(a).filter(b -> Try.of(() -> b._1.length).getOrElse(0) > 0).isEmpty()), c -> c._2), Case($(), 0)))
          .build())
        .put("briefs", t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
          .put("key", new ImmutableMap.Builder<String, Object>()
            .put("id", tp._1)
            .put("name", t._3.getOrDefault(tp._1, "")).build())
          .put("val", new ImmutableMap.Builder<String, Object>()
            .put("avail", tp._2)
            .put("ftfr", tp._3)
            .put("fix", tp._4).build()).build()).toBlocking().toIterable())
        .build())),
      Case($(t -> "type".equals(t._1)), t -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", report.count().toBlocking().single())
          .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
          .put("start", Match(Tuple.of(keys, start)).of(Case($(a -> Option.of(a).filter(b -> Try.of(() -> b._1.length).getOrElse(0) > 0).isEmpty()), c -> c._2), Case($(), 0)))
          .build())
        .put("briefs", t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
          .put("key", new ImmutableMap.Builder<String, Object>()
            .put("id", tp._1)
            .put("name", t._4.getOrDefault(tp._1, "")).build())
          .put("val", new ImmutableMap.Builder<String, Object>()
            .put("avail", tp._2)
            .put("ftfr", tp._3)
            .put("fix", tp._4).build()).build()).toBlocking().toIterable())
        .build())),
      Case($(t -> "supplier".equals(t._1)), t -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", report.count().toBlocking().single())
          .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
          .put("start", Match(Tuple.of(keys, start)).of(Case($(a -> Option.of(a).filter(b -> Try.of(() -> b._1.length).getOrElse(0) > 0).isEmpty()), c -> c._2), Case($(), 0)))
          .build())
        .put("briefs", t._2.map(tp -> new ImmutableMap.Builder<String, Object>()
          .put("key", new ImmutableMap.Builder<String, Object>()
            .put("id", tp._1)
            .put("name", t._5.getOrDefault(tp._1, "")).build())
          .put("val", new ImmutableMap.Builder<String, Object>()
            .put("avail", tp._2)
            .put("ftfr", tp._3)
            .put("fix", tp._4).build()).build()).toBlocking().toIterable())
        .build())),
      Case($(t -> "asset".equals(t._1)), t -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", report.count().toBlocking().single())
          .put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE))
          .put("start", Match(Tuple.of(keys, start)).of(Case($(a -> Option.of(a).filter(b -> Try.of(() -> b._1.length).getOrElse(0) > 0).isEmpty()), c -> c._2), Case($(), 0)))
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
            .put("ftfr", tp._3)
            .put("fix", tp._4).build()).build()).toBlocking().toIterable())
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
    Map<Integer, Tuple3<Integer, Integer, String>> reasons = commonService.findFaults();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
      .put("reasons", faService.reasons(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), dept, type, supplier, asset).skip(start).limit(Option.of(limit).getOrElse(Integer.MAX_VALUE))
        .map(t -> new ImmutableMap.Builder<String, Object>().put("id", t._1).put("name", Try.of(() -> reasons.get(t._1)._3).getOrElse("")).put("count", t._2).build()).toBlocking().toIterable()).build());
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<? extends Map<String, Object>> handleValidation(RuntimeException t) {
    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ImmutableMap.of("error", Option.of(t).map(Throwable::getMessage).getOrElse("validation error")));
  }

}

