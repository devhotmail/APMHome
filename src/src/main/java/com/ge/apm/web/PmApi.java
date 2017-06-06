package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.PmService;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple5;
import javaslang.control.Option;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static javaslang.API.*;

@RestController
@RequestMapping("/pm")
@Validated
public class PmApi {
  private static final Logger log = LoggerFactory.getLogger(PmApi.class);
  @Autowired
  private CommonService commonService;

  @Autowired
  private PmService pmService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> handle(HttpServletRequest request,
                                                    @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime from,
                                                    @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime to,
                                                    @Pattern(regexp = "dept|type|supplier|asset") @RequestParam(name = "groupby", required = true) String groupBy,
                                                    @Min(1) @RequestParam(name = "dept", required = false) Integer dept,
                                                    @Min(1) @RequestParam(name = "type", required = false) Integer type,
                                                    @Min(1) @RequestParam(name = "supplier", required = false) Integer supplier,
                                                    @Min(1) @RequestParam(name = "asset", required = false) Integer asset,
                                                    @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(name = "pm", required = false, defaultValue = "30") Integer pmv,
                                                    @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(name = "limit", required = false, defaultValue = "30") Integer limit,
                                                    @Min(0) @RequestParam(name = "start", required = false, defaultValue = "0") Integer start) {
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> types = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> suppliers = commonService.findSuppliers(user.getSiteId());
    Map<Integer, String> assets = commonService.findAssets(user.getSiteId(), user.getHospitalId()).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, kv -> kv.getValue()._7));
    Iterable<Tuple5<Integer, Integer, Integer, Integer, Integer>> report = pmService.findPm(user.getSiteId(), user.getHospitalId(), from.toDate(), to.toDate(), Match(groupBy).of(Case("dept", "dept_id"), Case("type", "asset_group"), Case("supplier", "supplier_id"), Case("asset", "id")), dept, type, supplier, asset, pmv).toBlocking().toIterable();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
      .put("pages", new ImmutableMap.Builder<String, Object>().put("total", StreamSupport.stream(report.spliterator(), false).count()).put("start", start).put("limit", limit).build())
      .put("root", new ImmutableMap.Builder<String, Object>()
        .put("completion", new ImmutableMap.Builder<String, Object>()
          .put("due", StreamSupport.stream(report.spliterator(), false).mapToInt(t -> t._3).sum())
          .put("completed", StreamSupport.stream(report.spliterator(), false).mapToInt(t -> t._2).sum())
          .put("all", StreamSupport.stream(report.spliterator(), false).mapToInt(t -> t._5).sum()).build())
        .put("quality", new ImmutableMap.Builder<String, Object>()
          .put("repair", StreamSupport.stream(report.spliterator(), false).mapToInt(t -> t._4).sum())
          .put("all", StreamSupport.stream(report.spliterator(), false).mapToInt(t -> t._5).sum()).build())
        .build())
      .put("items", StreamSupport.stream(report.spliterator(), false).skip(start).limit(limit).map(t -> new ImmutableMap.Builder<String, Object>()
        .put("key", new ImmutableMap.Builder<String, Object>()
          .put("id", t._1)
          .put("name", Match(Tuple.of(groupBy, t._1)).of(Case($(a -> "type".equals(a._1)), b -> types.getOrDefault(b._2, "")), Case($(a -> "dept".equals(a._1)), b -> depts.getOrDefault(b._2, "")), Case($(a -> "supplier".equals(a._1)), b -> suppliers.getOrDefault(b._2, "")), Case($(a -> "asset".equals(a._1)), b -> assets.getOrDefault(b._2, "")))).build())
        .put("val", new ImmutableMap.Builder<String, Object>()
          .put("completion", ImmutableMap.of("due", t._3, "completed", t._2, "all", t._5))
          .put("quality", ImmutableMap.of("repair", t._4, "all", t._5))
          .build())
        .build()).collect(Collectors.toList()))
      .build());

  }


}
