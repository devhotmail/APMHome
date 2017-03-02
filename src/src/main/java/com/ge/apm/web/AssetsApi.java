package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.AssetsService;
import com.ge.apm.service.api.CommonService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javaslang.Tuple;
import javaslang.Tuple7;
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
import java.time.LocalDate;
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
                                                           @Pattern(regexp = "type|dept|supplier|price|yoa") @RequestParam(value = "orderby", required = false, defaultValue = "type") String orderBy,
                                                           @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false) Integer limit,
                                                           @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    log.info("orderBy:{}, limit:{}, start:{}", orderBy, limit, start);
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> types = commonService.findFields(user.getSiteId(), "assetGroup");
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<Integer, String> suppliers = commonService.findSuppliers(user.getSiteId());
    Observable<Tuple7<Integer, String, Integer, String, Integer, Money, LocalDate>> assets = assetsService.findAssets(user.getSiteId(), user.getHospitalId(), orderBy);
    return serialize(request, types, depts, suppliers, assets, orderBy, limit, start);
  }

  private ResponseEntity<Map<String, Object>> serialize(HttpServletRequest request, Map<Integer, String> types, Map<Integer, String> depts, Map<Integer, String> suppliers, Observable<Tuple7<Integer, String, Integer, String, Integer, Money, LocalDate>> assets, String orderBy, Integer limit, Integer start) {
    final Observable<Tuple7<Integer, String, String, String, String, Double, LocalDate>> items = assets.map(t -> Tuple.of(t._1, t._2, types.get(t._3), t._4, suppliers.get(t._5), t._6.getNumber().doubleValue(), t._7));
    final Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
      .put("pages", new ImmutableMap.Builder<String, Object>().put("total", assets.count().toBlocking().single()).put("limit", Option.of(limit).getOrElse(Integer.MAX_VALUE)).put("start", start).build())
      .put("items", items.skip(start).limit(Option.of(limit).getOrElse(Integer.MAX_VALUE)).map(t -> new ImmutableMap.Builder<String, Object>()
        .put("id", t._1)
        .put("name", t._2)
        .put("type", Option.of(t._3).getOrElseThrow(() -> new IllegalArgumentException(String.format("type should not be %s", t._3))))
        .put("dept", Option.of(t._4).getOrElseThrow(() -> new IllegalArgumentException(String.format("dept should not be %s", t._4))))
        .put("supplier", Option.of(t._5).getOrElseThrow(() -> new IllegalArgumentException(String.format("supplier should not be %s", t._5))))
        .put("price", Option.of(t._6).getOrElseThrow(() -> new IllegalArgumentException(String.format("price should not be %s", t._6))))
        .put("yoa", Option.of(t._7).getOrElseThrow(() -> new IllegalArgumentException(String.format("yoa should not be %s", t._7)))).build()).toBlocking().toIterable())
      .put("schema", ImmutableList.of(
        ImmutableMap.of("type", ImmutableMap.of("text", "按类型")),
        ImmutableMap.of("dept", ImmutableMap.of("text", "按科室")),
        ImmutableMap.of("supplier", ImmutableMap.of("text", "按品牌")),
        ImmutableMap.of("price", ImmutableMap.of("text", "按价值", "ticks", ImmutableList.of(items.map(t -> t._6).sorted().first().toBlocking().single(), items.map(t -> t._6).sorted().last().toBlocking().single()))),
        ImmutableMap.of("yoa", ImmutableMap.of("text", "按时间"))))
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "self")
        .put("href", String.format("%s?orderby=%s&limit=%s&start=%s", request.getRequestURL(), orderBy, limit, start)).build())
      .build();
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(body);
  }

}
