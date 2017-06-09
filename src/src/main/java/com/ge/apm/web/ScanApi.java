package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.ScanService;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.*;
import javaslang.control.Option;
import javaslang.control.Try;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/scan")
@Validated
public class ScanApi {
  private static final Logger log = LoggerFactory.getLogger(AssetsApi.class);
  @Autowired
  private CommonService commonService;

  @Autowired
  private ScanService scanService;

  @RequestMapping(path = "/brief", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> brief(HttpServletRequest request,
                                                             @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime from,
                                                             @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime to,
                                                             @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                             @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                             @Min(1) @RequestParam(name = "asset", required = false) Integer asset) {
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> types = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    return serializeBrief(request, types, commonService.findParts(), scanService.brief(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), type, dept, asset));
  }

  private ResponseEntity<Map<String, Object>> serializeBrief(HttpServletRequest request, Map<Integer, String> types, Map<Integer, String> parts, Observable<Tuple3<Integer, Integer, Integer>> report) {
    final Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
      .put("brief", report.toMultimap(t -> t._1).toBlocking().single().entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).map(kv -> new ImmutableMap.Builder<String, Object>()
        .put("type", ImmutableMap.of("id", kv.getKey(), "name", types.getOrDefault(kv.getKey(), "")))
        .put("items", new ImmutableMap.Builder<String, Object>()
          .put("desc", kv.getValue().stream().sorted(Comparator.comparingInt(Tuple3::_2)).map(t -> ImmutableMap.of("id", t._2, "name", parts.getOrDefault(t._2, ""))).collect(Collectors.toList()))
          .put("data", kv.getValue().stream().sorted(Comparator.comparingInt(Tuple3::_2)).map(t -> ImmutableMap.of("id", t._2, "count", t._3)).collect(Collectors.toList())).build())
        .build()).collect(Collectors.toList())).build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(body);
  }


  @RequestMapping(path = "/detail", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> detail(HttpServletRequest request,
                                                              @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime from,
                                                              @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime to,
                                                              @Pattern(regexp = "asset|step") @RequestParam(value = "groupby", required = true) String groupBy,
                                                              @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                              @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                              @Min(1) @RequestParam(name = "asset", required = false) Integer asset,
                                                              @Min(1) @RequestParam(name = "part", required = false) Integer part,
                                                              @Min(1) @RequestParam(name = "step", required = false) Integer step,
                                                              @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(name = "limit", required = false, defaultValue = "50") Integer limit,
                                                              @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start) {
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, String>> assets = commonService.findAssets(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> types = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> parts = commonService.findParts();
    Map<Integer, Tuple3<Integer, Integer, String>> steps = commonService.findSteps();
    if ("asset".equals(groupBy)) {
      return serializeAsset(request, assets, types, parts, scanService.assetDetail(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), type, dept, asset, part), start, limit);
    } else if ("step".equals(groupBy)) {
      return serializeStep(request, types, parts, steps, scanService.stepDetail(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), type, dept, asset, part, step), start, Option.of(limit).getOrElse(Integer.MAX_VALUE));
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of());
    }
  }

  private ResponseEntity<Map<String, Object>> serializeAsset(HttpServletRequest request, Map<Integer, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, String>> assets, Map<Integer, String> types, Map<Integer, String> parts, Observable<Tuple4<Integer, Integer, Integer, Integer>> report, Integer start, Integer limit) {
    final Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
      .put("detail", report.skip(start).limit(limit).map(t -> Tuple.of(t._1, t._2, Try.of(() -> assets.get(t._2)._7).getOrElse(""), t._3, t._4)).toMultimap(t -> Tuple.of(t._1, t._2, t._3)).toBlocking().single().entrySet().stream().sorted(Comparator.<Map.Entry<Tuple3<Integer, Integer, String>, Collection<Tuple5<Integer, Integer, String, Integer, Integer>>>, Integer>comparing(kv -> kv.getKey()._1).thenComparing(kv -> kv.getKey()._2)).map(kv -> new ImmutableMap.Builder<String, Object>()
        .put("type", ImmutableMap.of("id", kv.getKey()._1, "name", types.getOrDefault(kv.getKey()._1, "")))
        .put("asset", ImmutableMap.of("id", kv.getKey()._2, "name", kv.getKey()._3))
        .put("items", new ImmutableMap.Builder<String, Object>()
          .put("desc", kv.getValue().stream().sorted(Comparator.comparingInt(Tuple5::_4)).map(t -> ImmutableMap.of("id", t._4, "name", parts.getOrDefault(t._4, ""))).collect(Collectors.toList()))
          .put("data", kv.getValue().stream().sorted(Comparator.comparingInt(Tuple5::_4)).map(t -> ImmutableMap.of("id", t._4, "count", t._5)).collect(Collectors.toList())).build())
        .build()).collect(Collectors.toList()))
      .build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(body);
  }

  private ResponseEntity<Map<String, Object>> serializeStep(HttpServletRequest request, Map<Integer, String> types, Map<Integer, String> parts, Map<Integer, Tuple3<Integer, Integer, String>> steps, Observable<Tuple4<Integer, Integer, Integer, Integer>> report, Integer start, Integer limit) {
    final Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
      .put("detail", StreamSupport.stream(report.skip(start).limit(limit).map(t -> Tuple.of(t._1, t._2, t._3, Try.of(() -> steps.get(t._3)._3).getOrElse(""), t._4)).toBlocking().toIterable().spliterator(), false).sorted(Comparator.<Tuple5<Integer, Integer, Integer, String, Integer>, Integer>comparing(Tuple5::_1).thenComparing(Tuple5::_2).thenComparing(Tuple5::_3)).map(t -> new ImmutableMap.Builder<String, Object>()
        .put("type", ImmutableMap.of("id", t._1, "name", types.getOrDefault(t._1, "")))
        .put("part", ImmutableMap.of("id", t._2, "name", parts.getOrDefault(t._2, "")))
        .put("step", ImmutableMap.of("id", t._3, "name", t._4, "count", t._5)).build()).collect(Collectors.toList())).build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(body);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<? extends Map<String, Object>> handleValidation(Throwable t) {
    return ResponseEntity.badRequest().body(ImmutableMap.of("error", Option.of(t).map(Throwable::getMessage).getOrElse("validation error")));
  }

}
