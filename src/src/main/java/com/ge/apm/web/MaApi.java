package com.ge.apm.web;


import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.MaService;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.Tuple5;
import javaslang.control.Option;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
                                                                  @Min(1) @RequestParam(name = "supplier", required = false) Integer supplier) {
    UserAccount user = UserContext.getCurrentLoginUser();
    int siteId = user.getSiteId();
    int hospitalId = user.getHospitalId();
    Map<Integer, String> groups = javaslang.collection.HashMap.ofAll(commonService.findFields(siteId, "asset_group"))
      .map(v -> Tuple.of(Ints.tryParse(v._1), v._2)).filter(v -> v._1 != null).toJavaMap(v -> Tuple.of(v._1, v._2));
    Map<Integer, String> depts = commonService.findDepts(siteId, hospitalId);
    Map<Integer, String> suppliers = commonService.findSuppliers(siteId);
    log.info("inputs: from {}, to {}, groupBy {}, dept {}, type {}, supplier {}", from, to, groupBy, dept, type, supplier);
    if (Option.of(groupBy).isDefined()) {
      Observable<Tuple2<Integer, Tuple2<Double, Double>>> items = maService.findAstMtGroups(siteId, hospitalId, from, to, dept, type, supplier, groupBy);
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
        .body(new ImmutableMap.Builder<String, Object>()
          .put("root", mapRoot(items.map(v -> v._2._2), dept, type, supplier, groupBy))
          .put("items", mapGroups(items, "type".equals(groupBy) ? groups : ("dept".equals(groupBy) ? depts : suppliers)))
          .build());
    } else {
      Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple3<Double, Double, Double>>> items = maService.findAstMtItems(siteId, hospitalId, from, to, dept, type, supplier);
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
        .body(new ImmutableMap.Builder<String, Object>()
          .put("root", mapRoot(items.map(v -> v._2._3), dept, type, supplier, groupBy))
          .put("items", mapAssets(items))
          .build());
    }
  }

  //id, name, dept, type, supplier
  //price, onrate, maintenanceCost
  private Iterable<ImmutableMap<String, Object>> mapAssets(Observable<Tuple2<Tuple5<Integer, String, Integer, Integer, Integer>, Tuple3<Double, Double, Double>>> items) {
    return items.map(v -> new ImmutableMap.Builder<String, Object>()
      .put("id", v._1._1)
      .put("name", Option.of(v._1._2).getOrElse(""))
      .put("dept", Option.of(v._1._3).getOrElse(0))
      .put("type", Option.of(v._1._4).getOrElse(0))
      .put("supplier", Option.of(v._1._5).getOrElse(0))
      .put("price", v._2._1)
      .put("onrate", v._2._2)
      .put("maintenance_cost", v._2._3)
      .build()).toBlocking().toIterable();
  }

  //group_id
  //onrate, maintenanceCost
  private Iterable<ImmutableMap<String, Object>> mapGroups(Observable<Tuple2<Integer, Tuple2<Double, Double>>> items, Map<Integer, String> groups) {
    return items.map(v -> new ImmutableMap.Builder<String, Object>()
      .put("id", v._1)
      .put("name", Option.of(groups.get(v._1)).getOrElse(""))
      .put("onrate", v._2._1)
      .put("maintenance_cost", v._2._2)
      .build()).toBlocking().toIterable();
  }

  //id for group: dept-type-supplier
  private ImmutableMap<String, Object> mapRoot(Observable<Double> costs, Integer dept, Integer type, Integer supplier, String groupBy) {
    return new ImmutableMap.Builder<String, Object>()
      .put("id", Option.of(groupBy)
        .map(v -> "100")
        .getOrElse(String.format("%s-%s-%s", Option.of(dept).getOrElse(0), Option.of(type).getOrElse(0), Option.of(supplier).getOrElse(0))))
      .put("maintenance_cost", Try.of(() -> costs.reduce((a, b) -> a + b).toBlocking().single()).getOrElse(0D))
      .build();
  }
}
