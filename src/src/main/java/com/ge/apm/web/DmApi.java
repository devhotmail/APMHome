package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.DmService;
import com.google.common.collect.ImmutableMap;
import com.google.common.math.Stats;
import com.google.common.primitives.Ints;
import javaslang.*;
import javaslang.collection.Seq;
import javaslang.control.Option;
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
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javaslang.API.*;


@RestController
@RequestMapping("/dm")
@Validated
public class DmApi {
  private static final Logger log = LoggerFactory.getLogger(DmApi.class);

  @Autowired
  private CommonService commonService;
  @Autowired
  private DmService dmService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> desicionMaking(HttpServletRequest request,
                                                            @Pattern(regexp = "dept|type") @RequestParam(value = "groupby", required = true) String groupBy) {
    log.info("groupby:{}", groupBy);
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = "type".equals(groupBy) ? Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single()
      : commonService.findDepts(user.getSiteId(), user.getHospitalId());
    return serialize(request, groupBy, mapItems(groups, averageUsage(usagePredict(dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now().minusYears(1)), dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now())))));
  }


  /**
   * <p>If usage is null the year before last year and not null last year, the forecast is the value of last year.
   * If the usage is not null in both last year and the year before, the forecast is last year usage + standard deviation</p>
   *
   * @param theYearBeforeLast sql result the year before last year
   * @param lastYear          sql result last year
   * @return forecast of next year
   */
  private Observable<Tuple5<Integer, Double, String, Integer, Double>> usagePredict(Observable<Tuple5<Integer, Double, String, Integer, Double>> theYearBeforeLast, Observable<Tuple5<Integer, Double, String, Integer, Double>> lastYear) {
    return
      Observable.zip(theYearBeforeLast, lastYear, (lstSnd, lstFst) ->
        Tuple.of(lstSnd._1, lstSnd._2, lstSnd._3, lstSnd._4,
          lstSnd._5.equals(0D) ? lstFst._5 : lstFst._5 + Stats.of(lstSnd._5, lstFst._5).sampleStandardDeviation())).cache();
  }


  private String calculateSuggestion(Double maxUsage, Double averageUsage) {
    return Match(Tuple.of(maxUsage, averageUsage)).of(
      Case($(t -> t._2 > 1D), "建议购买新设备"),
      Case($(t -> t._1 > 1D), "建议合理安排使用"),
      Case($(t -> t._2 <= 0.3D), "建议提高使用率"),
      Case($(), "")
    );
  }

  private javaslang.collection.Map<Integer, Tuple3<Double, Double, javaslang.collection.List<Tuple4<Integer, Double, String, Double>>>> averageUsage(Observable<Tuple5<Integer, Double, String, Integer, Double>> p) {
    return
      javaslang.collection.List.ofAll(p.toBlocking().toIterable()).groupBy(t -> t._4).mapValues(lst -> Tuple.of(
        Stats.of(lst.map(t -> t._5).toJavaList()).mean(),
        Stats.of(lst.map(t -> t._5).toJavaList()).max(),
        lst.map(t -> Tuple.of(t._1, t._2, t._3, t._5))));
  }

  private Tuple2<Seq<ImmutableMap<String, Object>>, Double> mapItems(Map<Integer, String> groups, javaslang.collection.Map<Integer, Tuple3<Double, Double, javaslang.collection.List<Tuple4<Integer, Double, String, Double>>>> items) {
    return Tuple.of(
      items.map((k, v) -> Tuple.of(k, new ImmutableMap.Builder<String, Object>()
        .put("id", Option.of(k).getOrElseThrow(() -> new IllegalArgumentException(String.format("group Id should not be %s", k))))
        .put("name", Option.of(groups.get(k)).getOrElseThrow(() -> new IllegalArgumentException(String.format("group name should not be %s", groups.get(k)))))
        .put("usage", v._1)
        .put("suggestion", calculateSuggestion(v._2, v._1))
        .put("items", v._3.map(sub -> new ImmutableMap.Builder<String, Object>()
          .put("id", sub._1)
          .put("name", Option.of(sub._3).getOrElseThrow(() -> new IllegalArgumentException(String.format("Asset name should not be %s", sub._3))))
          .put("size", Option.of(sub._2).getOrElseThrow(() -> new IllegalArgumentException(String.format("Purchase price should not be %s", sub._2))))
          .put("usage", sub._4)
          .build()).toJavaList())
        .build())).values(),
      Stats.of(items.values().map(t -> t._1).toJavaList()).mean());
  }

  private ResponseEntity<Map<String, Object>> serialize(HttpServletRequest request, String groupBy, Tuple2<Seq<ImmutableMap<String, Object>>, Double> items) {
    log.info("got final input items: {}", items._1.toJavaList());
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
      .put("id", "100")
      .put("name", "All Assets")
      .put("usage", items._2)
      .put("items", items._1.toJavaList())
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "self")
        .put("href", String.format("%s?groupby=%s", request.getRequestURL(), groupBy))
        .build())
      .build());
  }
}

