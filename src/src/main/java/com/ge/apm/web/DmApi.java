package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.DmService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.math.Stats;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple4;
import javaslang.Tuple5;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javaslang.API.*;


@RestController
@RequestMapping("/dm")
@Validated
public class DmApi {
  private static final Logger log = LoggerFactory.getLogger(DmApi.class);
  private final String SUGGESTION_BUY = "建议购买";
  private final String SUGGESTION_ADJUST = "建议合理安排使用";
  private final String SUGGESTION_RAISE = "建议提高使用率";

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


  private List<ImmutableMap<String, String>> calculateSuggestion(Integer NumGreaterThan1, Double averageUsage, Integer numOfAssets) {
    return Match(Tuple.of(NumGreaterThan1, averageUsage)).of(
      Case($(t -> t._2 > 1D), javaslang.collection.List.of(ImmutableMap.of("title", SUGGESTION_BUY, "addition", String.format("%s台设备", (int) Math.ceil((averageUsage - 1) * numOfAssets)))).toJavaList()),
      Case($(t -> t._1 > 0), javaslang.collection.List.of(ImmutableMap.of("title", SUGGESTION_ADJUST)).toJavaList()),
      Case($(t -> t._2 <= 0.3D), javaslang.collection.List.of(ImmutableMap.of("title", SUGGESTION_RAISE)).toJavaList()),
      Case($(), javaslang.collection.List.of(ImmutableMap.of("title", "")).toJavaList()));
  }

  private List<Map<String, String>> calculateTotalSuggestion(Seq<ImmutableMap<String, Object>> items, String groupBy) {
    Seq<String> suggestions = items.map(v -> ((List<ImmutableMap<String, String>>) v.get("suggestions")).get(0).get("title"));
    int numBuy = suggestions.count(v -> SUGGESTION_BUY.equals(v));
    int numAjst = suggestions.count(v -> SUGGESTION_ADJUST.equals(v));
    int numRse = suggestions.count(v -> SUGGESTION_RAISE.equals(v));
    ImmutableList.Builder<Map<String, String>> totalSuggestions = new ImmutableList.Builder<Map<String, String>>();
    if (numBuy > 0) {
      totalSuggestions.add(ImmutableMap.of("title", SUGGESTION_BUY, "addition", Option.when(groupBy.equals("dept"), String.format("%s个科室", numBuy)).getOrElse(String.format("%s种类型", numBuy))));
    }
    if (numAjst > 0) {
      totalSuggestions.add(ImmutableMap.of("title", SUGGESTION_ADJUST, "addition", Option.when(groupBy.equals("dept"), String.format("%s个科室", numAjst)).getOrElse(String.format("%s种类型", numAjst))));
    }
    if (numRse > 0) {
      totalSuggestions.add(ImmutableMap.of("title", SUGGESTION_RAISE, "addition", Option.when(groupBy.equals("dept"), String.format("%s个科室", numRse)).getOrElse(String.format("%s种类型", numRse))));
    }
    return totalSuggestions.build();
  }

  private javaslang.collection.Map<Integer, Tuple4<Double, Integer, Integer, javaslang.collection.List<Tuple4<Integer, Double, String, Double>>>> averageUsage(Observable<Tuple5<Integer, Double, String, Integer, Double>> p) {
    return
      javaslang.collection.List.ofAll(p.toBlocking().toIterable()).groupBy(t -> t._4).mapValues(lst -> Tuple.of(
        Stats.of(lst.map(t -> t._5).toJavaList()).mean(),
        lst.map(t -> t._5).count(v -> v > 1D),
        lst.map(t -> t._5).count(v -> true),
        lst.map(t -> Tuple.of(t._1, t._2, t._3, t._5))));
  }

  private Tuple2<Seq<ImmutableMap<String, Object>>, Double> mapItems(Map<Integer, String> groups, javaslang.collection.Map<Integer, Tuple4<Double, Integer, Integer, javaslang.collection.List<Tuple4<Integer, Double, String, Double>>>> items) {
    return Tuple.of(
      items.map((k, v) -> Tuple.of(k, new ImmutableMap.Builder<String, Object>()
        .put("id", Option.of(k).getOrElseThrow(() -> new IllegalArgumentException(String.format("group Id should not be %s", k))))
        .put("name", Option.of(groups.get(k)).getOrElseThrow(() -> new IllegalArgumentException(String.format("group name should not be %s", groups.get(k)))))
        .put("usage", v._1)
        .put("suggestions", calculateSuggestion(v._2, v._1, v._3))
        .put("items", v._4.map(sub -> new ImmutableMap.Builder<String, Object>()
          .put("id", sub._1)
          .put("name", Option.of(sub._3).getOrElseThrow(() -> new IllegalArgumentException(String.format("Asset name should not be %s", sub._3))))
          .put("size", Option.of(sub._2).getOrElseThrow(() -> new IllegalArgumentException(String.format("Purchase price should not be %s", sub._2))))
          .put("usage", sub._4)
          .build()).toJavaList())
        .build())).values(),
      Stats.of(Option.of(items.values().map(t -> t._1).toJavaList()).getOrElse(javaslang.collection.List.of(0D).toJavaList())).mean());
  }

  private ResponseEntity<Map<String, Object>> serialize(HttpServletRequest request, String groupBy, Tuple2<Seq<ImmutableMap<String, Object>>, Double> items) {
    log.info("got final input items: {}", items._1.toJavaList());
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
      .put("id", "100")
      .put("name", "All Assets")
      .put("usage", items._2)
      .put("suggestions", calculateTotalSuggestion(items._1, groupBy))
      .put("items", items._1.toJavaList())
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "self")
        .put("href", String.format("%s?groupby=%s", request.getRequestURL(), groupBy))
        .build())
      .build());
  }
}

