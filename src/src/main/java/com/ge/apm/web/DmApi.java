package com.ge.apm.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.DmService;
import com.ge.apm.service.utils.CNY;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.math.Stats;
import com.google.common.primitives.Ints;
import javaslang.*;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;
import javaslang.jackson.datatype.JavaslangModule;
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
  public ResponseEntity<byte[]> desicionMaking (HttpServletRequest request,
                                                                      @Pattern(regexp = "dept|type") @RequestParam(value = "groupby", required = true) String groupBy) throws JsonProcessingException {
    log.info("groupby:{}", groupBy);
    UserAccount user = UserContext.getCurrentLoginUser();
//    java.util.Map<Integer, String> groupsOld = "type".equals(groupBy) ? Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), java.util.Map.Entry::getValue).toBlocking().single()
//      : commonService.findDepts(user.getSiteId(), user.getHospitalId());
    java.util.Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), java.util.Map.Entry::getValue).toBlocking().single();
    java.util.Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    //return serialize(request, groupBy, mapItems(groups, averageUsage(usagePredict(dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now().minusYears(1)), dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now())))));
    Map<String,Object>body=recursivelyCalculateSuggestions(groupCalculations(calculateValuesEachItem(usagePredict(dmService.findAssets(user.getSiteId(),user.getHospitalId(),groupBy,LocalDate.now().minusYears(1)),dmService.findAssets(user.getSiteId(),user.getHospitalId(),groupBy,LocalDate.now()),HashMap.empty())),groupBy,groups,depts));
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1,TimeUnit.DAYS)).body(new ObjectMapper().registerModule(new JavaslangModule()).writer().writeValueAsBytes(body));
  }


  /**
   * <p>If usage is null the year before last year and not null last year, the forecast is the value of last year.
   * If the usage is not null in both last year and the year before, the forecast is last year usage + standard deviation</p>
   *
   * @param theYearBeforeLast sql result the year before last year
   * @param lastYear          sql result last year
   * @return forecast of next year
   */
  private Observable<Tuple8<Integer, String, Integer, Integer, Double, Double, Double, Double>> usagePredict(Observable<Tuple6<Integer, String, Integer, Integer, Double, Double>> theYearBeforeLast, Observable<Tuple6<Integer, String, Integer, Integer, Double, Double>> lastYear, Map<Integer, Double> userPredict) {
    return Observable.zip(theYearBeforeLast, lastYear, (lstSnd, lstFst) ->
      Tuple.of(lstSnd._1, lstSnd._2, lstSnd._3, lstSnd._4, lstFst._5,
        userPredict.get(lstSnd._1).getOrElse(Option.when(lstSnd._5.equals(0D), lstFst._5).getOrElse(lstFst._5 + Stats.of(lstSnd._5, lstFst._5).sampleStandardDeviation())),
        lstSnd._6, lstFst._6)).cache();
  }

  private List<Map<String, Object>> calculateValuesEachItem(Observable<Tuple8<Integer, String, Integer, Integer, Double, Double, Double, Double>> predictedData) {
    return List.ofAll(predictedData.toBlocking().toIterable()).map(v -> HashMap.of(
      "id", v._1,
      "name", v._2,
      "clinical_dept_id", v._3,
      "asset_group", v._4,
      "usage", v._6,
      "revenue_predicted_raw", v._8 * (1 + v._6 - v._5),
      "revenue_last_year_raw", v._8,
      "revenue_year_before_last_raw", v._7
    ));
  }

  private Map<String, Object>groupCalculations(List<Map<String, Object>>items,String groupBy,java.util.Map<Integer,String>groups,java.util.Map<Integer,String>depts){
    if ("dept".equals(groupBy)){
      return calculateHigherLevelValues(items,100,"全部设备")
        .put("groupby","dept")
        .put("items",items.groupBy(v->(Integer)v.get("clinical_dept_id").get())
          .map(tuple2->calculateHigherLevelValues(tuple2._2,tuple2._1,depts.get(tuple2._1))
            .put("groupby","type")
            .put("items",tuple2._2.groupBy(sub->(Integer)sub.get("asset_group").get())
              .map(subTuple2->calculateHigherLevelValues(subTuple2._2,subTuple2._1,groups.get(subTuple2._1))
                .put("items",subTuple2._2)))));
    }else {
      return calculateHigherLevelValues(items,100,"全部设备")
        .put("groupby","type")
        .put("items",items.groupBy(v->(Integer)v.get("asset_group").get())
          .map(tuple2->calculateHigherLevelValues(tuple2._2,tuple2._1,groups.get(tuple2._1))
            .put("items",tuple2._2)));
    }
  }

  private Map<String, Object> calculateHigherLevelValues(List<Map<String, Object>> items, Integer id, String name) {
    List<Double> usages = items.map(v -> (Double) v.get("usage").get());
    Double revenuePredictedSugRaw = Try.of(() -> Stats.of(items.map(v -> (Double) v.get("revenue_predicted_raw").get())).sum()).getOrElse(0D);
    Double revenuePredictedRaw = Try.of(() -> Stats.of(items.map(v -> Option.when((Double) v.get("usage").get() > 1D, (Double) v.get("revenue_predicted_raw").get() / (Double) v.get("usage").get()).getOrElse((Double) v.get("revenue_predicted_raw").get())).toJavaList()).sum()).getOrElse(0D);
    Double revenueLastYearRaw = Try.of(() -> Stats.of(items.map(v -> (Double) v.get("revenue_last_year_raw").get()).toJavaList()).sum()).getOrElse(0D);
    Double revenueYearBeforeLastRaw = Try.of(() -> Stats.of(items.map(v -> (Double) v.get("revenue_year_before_last_raw").get()).toJavaList()).sum()).getOrElse(0D);
    String label = CNY.desc(CNY.money(revenueYearBeforeLastRaw))._2;
    return HashMap.of(
      "id", id,
      "name", name,
      "usage", Try.of(() -> Stats.of(usages.toJavaList()).mean()).getOrElse(0D),
      "usage_sum", Tuple.of(usages.count(v -> v <= 0.3D), usages.count(v -> v > 1D)),
      "revenue_year_before_last", CNY.desc(CNY.money(revenueYearBeforeLastRaw), label)._1,
      "revenue_last_year", CNY.desc(CNY.money(revenueLastYearRaw), label)._1,
      "revenue_year_before_last_raw", revenueYearBeforeLastRaw,
      "revenue_last_year_raw", revenueLastYearRaw,
      "last_year_date", LocalDate.now().minusYears(1).toString(),
      "year_before_last_date", LocalDate.now().minusYears(2).toString(),
      "revenue_predict", CNY.desc(CNY.money(revenuePredictedRaw), label)._1,
      "revenue_predict_raw", revenuePredictedRaw,
      "revenue_increase", Option.when(revenueLastYearRaw.equals(0D), 0D).getOrElse(revenuePredictedRaw / revenueLastYearRaw - 1),
      "revenue_predict_sug", CNY.desc(CNY.money(revenuePredictedSugRaw), label)._1,
      "revenue_predict_sug_raw", revenuePredictedSugRaw,
      "revenue_increase_sug", Option.when(revenueLastYearRaw.equals(0D), 0D).getOrElse(revenuePredictedSugRaw / revenueLastYearRaw - 1),
      "revenue_unit", label
    );
  }

  private List<Map<String, String>> calculateBottomLevelSuggestions(Integer NumGreaterThan1, Double averageUsage, Integer numOfAssets) {
    if (averageUsage > 1D) {
      return List.of(HashMap.of("title", SUGGESTION_BUY, "addition", String.format("%s台设备", (int) Math.ceil((averageUsage - 1) * numOfAssets))));
    } else if (NumGreaterThan1 > 0) {
      return List.of(HashMap.of("title", SUGGESTION_ADJUST));
    } else if (averageUsage <= 0.3D) {
      return List.of(HashMap.of("title", SUGGESTION_RAISE));
    } else {
      return List.of(HashMap.empty());
    }
  }

  private List<Map<String, String>> calculateHigherLevelSuggestions(Seq<Seq<Map<String, String>>> lowerLevelSuggestions, String groupBy) {
    Seq<Seq<String>>suggestionLists=lowerLevelSuggestions.map(v->v.map(sub->sub.get("title").getOrElse("")));
    int numBuy=suggestionLists.count(v->v.contains(SUGGESTION_BUY));
    int numAjst=suggestionLists.count(v->v.contains(SUGGESTION_ADJUST));
    int numRse=suggestionLists.count(v->v.contains(SUGGESTION_RAISE));
    ImmutableList.Builder<Map<String, String>> totalSuggestions = new ImmutableList.Builder<Map<String, String>>();
    if (numBuy > 0) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_BUY, "addition", Option.when(groupBy.equals("dept"), String.format("%s个科室", numBuy)).getOrElse(String.format("%s种类型", numBuy))));
    }
    if (numAjst > 0) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_ADJUST, "addition", Option.when(groupBy.equals("dept"), String.format("%s个科室", numAjst)).getOrElse(String.format("%s种类型", numAjst))));
    }
    if (numRse > 0) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_RAISE, "addition", Option.when(groupBy.equals("dept"), String.format("%s个科室", numRse)).getOrElse(String.format("%s种类型", numRse))));
    }
    return List.ofAll(totalSuggestions.build());
  }

  private Map<String, Object> recursivelyCalculateSuggestions(Map<String, Object> currentMap) {
    Seq<Map<String,Object>>currentItems=(Seq<Map<String ,Object>>)currentMap.get("items").get();
    if(currentItems.get(0).containsKey("items")){
      Map<String, Object>newMap=currentMap.put("items",currentItems.map(this::recursivelyCalculateSuggestions));
      return newMap.put("suggestions",calculateHigherLevelSuggestions(((Seq<Map<String ,Object>>)newMap.get("items").get()).map(v->(Seq<Map<String, String>>)v.get("suggestions").get()),(String) newMap.get("groupby").get()));
    }else {
      return currentMap.put("suggestions",calculateBottomLevelSuggestions(
        ((Tuple2<Integer,Integer>)currentMap.get("usage_sum").get())._2,
        (Double)currentMap.get("usage").get(),
        currentItems.count(v->true)
      ));
    }
  }

  private javaslang.collection.Map<Integer, Tuple4<Double, Integer, Integer, javaslang.collection.List<Tuple4<Integer, Double, String, Double>>>> averageUsage(Observable<Tuple5<Integer, Double, String, Integer, Double>> p) {
    return
      javaslang.collection.List.ofAll(p.toBlocking().toIterable()).groupBy(t -> t._4).mapValues(lst -> Tuple.of(
        Stats.of(lst.map(t -> t._5).toJavaList()).mean(),
        lst.map(t -> t._5).count(v -> v > 1D),
        lst.map(t -> t._5).count(v -> true),
        lst.map(t -> Tuple.of(t._1, t._2, t._3, t._5))));
  }


  private java.util.List<ImmutableMap<String, String>> calculateSuggestion(Integer NumGreaterThan1, Double averageUsage, Integer numOfAssets) {
    return Match(Tuple.of(NumGreaterThan1, averageUsage)).of(
      Case($(t -> t._2 > 1D), javaslang.collection.List.of(ImmutableMap.of("title", SUGGESTION_BUY, "addition", String.format("%s台设备", (int) Math.ceil((averageUsage - 1) * numOfAssets)))).toJavaList()),
      Case($(t -> t._1 > 0), javaslang.collection.List.of(ImmutableMap.of("title", SUGGESTION_ADJUST)).toJavaList()),
      Case($(t -> t._2 <= 0.3D), javaslang.collection.List.of(ImmutableMap.of("title", SUGGESTION_RAISE)).toJavaList()),
      Case($(), javaslang.collection.List.of(new ImmutableMap.Builder<String, String>().build()).toJavaList()));
  }

  private java.util.List<java.util.Map<String, String>> calculateTotalSuggestion(Seq<ImmutableMap<String, Object>> items, String groupBy) {
    Seq<String> suggestions = items.map(v -> ((java.util.List<ImmutableMap<String, String>>) v.get("suggestions")).get(0).get("title"));
    int numBuy = suggestions.count(v -> SUGGESTION_BUY.equals(v));
    int numAjst = suggestions.count(v -> SUGGESTION_ADJUST.equals(v));
    int numRse = suggestions.count(v -> SUGGESTION_RAISE.equals(v));
    ImmutableList.Builder<java.util.Map<String, String>> totalSuggestions = new ImmutableList.Builder<java.util.Map<String, String>>();
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


  private Tuple2<Seq<ImmutableMap<String, Object>>, Double> mapItems(java.util.Map<Integer, String> groups, javaslang.collection.Map<Integer, Tuple4<Double, Integer, Integer, javaslang.collection.List<Tuple4<Integer, Double, String, Double>>>> items) {
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
      Stats.of(Option.of(items.values().map(t -> t._1).toJavaList()).filter(sub -> !sub.isEmpty()).getOrElse(javaslang.collection.List.of(0D).toJavaList())).mean());
  }

  private ResponseEntity<java.util.Map<String, Object>> serialize(HttpServletRequest request, String groupBy, Tuple2<Seq<ImmutableMap<String, Object>>, Double> items) {
    log.info("got final input items: {}", items._1.toJavaList());
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
      .put("id", "100")
      .put("name", "全部设备")
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

