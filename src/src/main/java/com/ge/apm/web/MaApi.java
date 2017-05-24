package com.ge.apm.web;


import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.MaService;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.*;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/ma")
@Validated
public class MaApi {
  private static final Logger log = LoggerFactory.getLogger(MaApi.class);

  @Autowired
  private MaService maService;

  @Autowired
  private CommonService commonService;

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
    UserAccount user = UserContext.getCurrentLoginUser();
    int siteId = user.getSiteId();
    int hospitalId = user.getHospitalId();
    Map<Integer, String> groups = javaslang.collection.HashMap.ofAll(commonService.findFields(siteId, "asset_group"))
      .map(v -> Tuple.of(Ints.tryParse(v._1), v._2)).filter(v -> v._1 != null).toJavaMap(v -> Tuple.of(v._1, v._2));
    Map<Integer, String> depts = commonService.findDepts(siteId, hospitalId);
    Map<Integer, String> suppliers = commonService.findSuppliers(siteId);
    log.info("inputs: from {}, to {}, groupBy {}, dept {}, type {}, supplier {}", from, to, groupBy, dept, type, supplier);
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
}
