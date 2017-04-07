package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.AssetsService;
import com.ge.apm.service.api.CommonService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple8;
import javaslang.control.Option;
import org.javamoney.moneta.Money;
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
@RequestMapping("/assets")
@Validated
public class AssetsApi {
  private static final Logger log = LoggerFactory.getLogger(AssetsApi.class);
  @Autowired
  private CommonService commonService;
  @Autowired
  private AssetsService assetsService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> handleRequest(HttpServletRequest request,
                                                           @Pattern(regexp = "type|dept|supplier|price|yoi") @RequestParam(value = "orderby", required = false, defaultValue = "type") String orderBy,
                                                           @Min(1) @RequestParam(value = "dept", required = false, defaultValue = "0") Integer dept,
                                                           @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false) Integer limit,
                                                           @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    log.info("orderBy:{}, limit:{}, start:{}", orderBy, limit, start);
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetFunctionType").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> types = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> suppliers = commonService.findSuppliers(user.getSiteId());
    Observable<Tuple8<Integer, String, Integer, Integer, Integer, Integer, Money, Integer>> assets = assetsService.findAssets(user.getSiteId(), user.getHospitalId(), dept, orderBy);
    return serialize(request, groups, types, depts, suppliers, assets, orderBy, limit, start);
  }

  private ResponseEntity<Map<String, Object>> serialize(HttpServletRequest request, Map<Integer, String> groups, Map<Integer, String> types, Map<Integer, String> depts, Map<Integer, String> suppliers, Observable<Tuple8<Integer, String, Integer, Integer, Integer, Integer, Money, Integer>> assets, String orderBy, Integer limit, Integer start) {
    final Observable<Tuple8<Integer, String, String, String, String, String, Double, Integer>> items = assets.map(t -> Tuple.of(t._1, t._2, groups.getOrDefault(t._3, ""), types.getOrDefault(t._4, ""), Option.of(depts.get(t._5)).getOrElse(Option.of(t._5).map(Object::toString).getOrElse("")), Option.of(suppliers.get(t._6)).getOrElse(Option.of(t._5).map(Object::toString).getOrElse("")), t._7.getNumber().doubleValue(), t._8));
    final Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
      .put("pages", new ImmutableMap.Builder<String, Object>().put("total", assets.count().toBlocking().single()).put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE)).put("start", start).build())
      .put("items", items.skip(start).limit(Option.of(limit).getOrElse(Integer.MAX_VALUE)).map(t -> new ImmutableMap.Builder<String, Object>()
        .put("id", t._1)
        .put("name", t._2)
        .put("group", t._3)
        .put("type", t._4)
        .put("dept", t._5)
        .put("supplier", t._6)
        .put("price", Option.of(t._7).getOrElse(-1d))
        .put("yoi", Option.of(t._8).getOrElse(-1)).build()).toBlocking().toIterable())
      .put("schema", ImmutableList.of(
        ImmutableMap.of("type", ImmutableMap.of("text", "按类型")),
        ImmutableMap.of("dept", ImmutableMap.of("text", "按科室")),
        ImmutableMap.of("supplier", ImmutableMap.of("text", "按品牌")),
        ImmutableMap.of("price", ImmutableMap.of("text", "按价值", "ticks", ImmutableList.of(items.map(t -> t._7).sorted().first().toBlocking().single(), items.map(t -> t._7).sorted().last().toBlocking().single()))),
        ImmutableMap.of("yoi", ImmutableMap.of("text", "按时间"))))
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "self")
        .put("href", String.format("%s?orderby=%s&limit=%s&start=%s", request.getRequestURL(), orderBy, limit, start)).build())
      .build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(body);
  }

}
