package com.ge.apm.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.DmService;
import com.ge.apm.service.utils.CNY;
import com.google.common.collect.ImmutableList;
import com.google.common.math.Stats;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple6;
import javaslang.Tuple8;
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

import javax.money.MonetaryAmount;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;


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
  public ResponseEntity<byte[]> desicionMaking(HttpServletRequest request,
                                               @Pattern(regexp = "dept|type") @RequestParam(value = "groupby", required = false) String groupBy,
                                               @Min(1) @RequestParam(value = "dept", required = false) Integer dept) throws JsonProcessingException {
    UserAccount user = UserContext.getCurrentLoginUser();
    java.util.Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), java.util.Map.Entry::getValue).toBlocking().single();
    java.util.Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    if (Option.of(groupBy).isDefined() && Option.of(dept).isEmpty()) {
      Map<String, Object> body = recursivelyCalculateSuggestions(groupCalculations(calculateValuesEachItem(usagePredict(dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now().minusYears(1)), dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now()), HashMap.empty())), groupBy, groups, depts));
      return ResponseEntity.ok().header("Content-Type", "application/json;charset=UTF-8").cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ObjectMapper().registerModule(new JavaslangModule()).writer().writeValueAsBytes(body));
    } else if (Option.of(groupBy).isEmpty() && Option.of(dept).isDefined()) {
      Map<String, Object> body = recursivelyCalculateSuggestions(groupCalculations(calculateValuesEachItem(usagePredict(dmService.findAssets(user.getSiteId(), user.getHospitalId(), dept, LocalDate.now().minusYears(1)), dmService.findAssets(user.getSiteId(), user.getHospitalId(), dept, LocalDate.now()), HashMap.empty())), "type", groups, depts));
      return ResponseEntity.ok().header("Content-Type", "application/json;charset=UTF-8").cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ObjectMapper().registerModule(new JavaslangModule()).writer().writeValueAsBytes(body));
    } else {
      return ResponseEntity.badRequest().header("Content-Type", "application/json;charset=UTF-8").body(new ObjectMapper().registerModule(new JavaslangModule()).writer().writeValueAsBytes(HashMap.of("msg", "input data is not supported")));
    }
  }

  @RequestMapping(method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<byte[]> desicionMakingUserPredict(HttpServletRequest request,
                                                          @Pattern(regexp = "dept|type") @RequestParam(value = "groupby", required = false) String groupBy,
                                                          @Min(1) @RequestParam(value = "dept", required = false) Integer dept,
                                                          @RequestBody(required = true) String inputBody) throws IOException {
    UserAccount user = UserContext.getCurrentLoginUser();
    java.util.Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), java.util.Map.Entry::getValue).toBlocking().single();
    java.util.Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Map<String, List<Map<String, String>>> inputs = Try.of(() -> (Map<String, List<Map<String, String>>>) new ObjectMapper().registerModule(new JavaslangModule()).readValue(inputBody, new TypeReference<Map<String, List<Map<String, String>>>>() {
    })).getOrElseThrow(t -> new IllegalStateException("Format incorrect", t));
    Map<Integer, Double> userPredict = HashMap.ofEntries(inputs.values().get(0).map(v -> Tuple.of(Ints.tryParse(v.get("id").get()), Doubles.tryParse(v.get("change").get()))));
    if (Option.of(groupBy).isDefined() && Option.of(dept).isEmpty()) {
      Map<String, Object> body = recursivelyCalculateSuggestions(groupCalculations(calculateValuesEachItem(usagePredict(dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now().minusYears(1)), dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now()), userPredict)), groupBy, groups, depts));
      return ResponseEntity.ok().header("Content-Type", "application/json;charset=UTF-8").cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ObjectMapper().registerModule(new JavaslangModule()).writer().writeValueAsBytes(body));
    } else if (Option.of(groupBy).isEmpty() && Option.of(dept).isDefined()) {
      Map<String, Object> body = recursivelyCalculateSuggestions(groupCalculations(calculateValuesEachItem(usagePredict(dmService.findAssets(user.getSiteId(), user.getHospitalId(), dept, LocalDate.now().minusYears(1)), dmService.findAssets(user.getSiteId(), user.getHospitalId(), dept, LocalDate.now()), userPredict)), "type", groups, depts));
      return ResponseEntity.ok().header("Content-Type", "application/json;charset=UTF-8").cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ObjectMapper().registerModule(new JavaslangModule()).writer().writeValueAsBytes(body));
    } else {
      return ResponseEntity.badRequest().header("Content-Type", "application/json;charset=UTF-8").body(new ObjectMapper().registerModule(new JavaslangModule()).writer().writeValueAsBytes(HashMap.of("msg", "input data is not supported")));
    }
  }

  /**
   * <p>If the user put in a predict value, use user's predicts.
   * If usage is null the year before last year and not null last year, the forecast is the value of last year.
   * If the usage is not null in both last year and the year before, the forecast is last year usage + standard deviation</p>
   *
   * @param theYearBeforeLast sql result the year before last year
   * @param lastYear          sql result last year
   * @return forecast of next year
   */
  private Observable<Tuple8<Integer, String, Integer, Integer, Double, Double, Double, Double>> usagePredict(Observable<Tuple6<Integer, String, Integer, Integer, Double, Double>> theYearBeforeLast, Observable<Tuple6<Integer, String, Integer, Integer, Double, Double>> lastYear, Map<Integer, Double> userPredict) {
    return Observable.zip(theYearBeforeLast, lastYear, (lstSnd, lstFst) ->
      Tuple.of(lstSnd._1, lstSnd._2, lstSnd._3, lstSnd._4, lstFst._5,
        (userPredict.get(lstFst._1).getOrElse(Option.when(lstSnd._5.equals(0D), 0D).getOrElse(Stats.of(lstSnd._5, lstFst._5).sampleStandardDeviation())) + 1D) * lstFst._5,
        lstSnd._6, lstFst._6)).cache();
  }

  private List<Map<String, Object>> calculateValuesEachItem(Observable<Tuple8<Integer, String, Integer, Integer, Double, Double, Double, Double>> predictedData) {
    return List.ofAll(predictedData.toBlocking().toIterable()).map(v -> HashMap.of(
      "id", v._1,
      "name", v._2,
      "clinical_dept_id", v._3,
      "asset_group", v._4,
      "usage", v._6,
      "usage_sug", Option.when(v._6 >= 1D, 1D).getOrElse(v._6),
      "revenue_predict_sug_raw", Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5),
      "revenue_predict_sug", formatMoney(CNY.money(Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5)), CNY.desc(CNY.money(v._7))._2)._1,
      "revenue_last_year_raw", v._8,
      "revenue_last_year", formatMoney(CNY.money(v._8), CNY.desc(CNY.money(v._7))._2)._1,
      "revenue_year_before_last_raw", v._7,
      "revenue_year_before_last", formatMoney(CNY.money(v._7), CNY.desc(CNY.money(v._7))._2)._1,
      "last_year_date", LocalDate.now().minusYears(1).toString(),
      "year_before_last_date", LocalDate.now().minusYears(2).toString(),
      "revenue_increase_sug", Option.when(v._8.equals(0D), 0D).getOrElse(Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5) / v._8 - 1D),
      "revenue_unit", CNY.desc(CNY.money(v._7))._2,
      "revenue_predict_raw", Option.when(v._6 > 1D, Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5) / v._6).getOrElse(Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5)),
      "revenue_predict", formatMoney(CNY.money(Option.when(v._6 > 1D, Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5) / v._6).getOrElse(Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5))), CNY.desc(CNY.money(v._7))._2)._1,
      "revenue_increase", Option.when(v._8.equals(0D), 0D).getOrElse(Option.when(v._6 > 1D, Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5) / v._6).getOrElse(Option.when(v._5.equals(0D), 0D).getOrElse(v._8 * v._6 / v._5)) / v._8 - 1D)
    ));
  }

  private Map<String, Object> groupCalculations(List<Map<String, Object>> items, String groupBy, java.util.Map<Integer, String> groups, java.util.Map<Integer, String> depts) {
    if ("dept".equals(groupBy)) {
      return calculateHigherLevelValues(items, 100, "全部设备")
        .put("groupby", "dept")
        .put("items", items.groupBy(v -> (Integer) v.get("clinical_dept_id").get())
          .map(tuple2 -> calculateHigherLevelValues(tuple2._2, tuple2._1, depts.get(tuple2._1))
            .put("groupby", "type")
            .put("items", tuple2._2.groupBy(sub -> (Integer) sub.get("asset_group").get())
              .map(subTuple2 -> calculateHigherLevelValues(subTuple2._2, subTuple2._1, groups.get(subTuple2._1))
                .put("items", subTuple2._2)))));
    } else {
      return calculateHigherLevelValues(items, 100, "全部设备")
        .put("groupby", "type")
        .put("items", items.groupBy(v -> (Integer) v.get("asset_group").get())
          .map(tuple2 -> calculateHigherLevelValues(tuple2._2, tuple2._1, groups.get(tuple2._1))
            .put("items", tuple2._2)));
    }
  }

  private Map<String, Object> calculateHigherLevelValues(List<Map<String, Object>> items, Integer id, String name) {
    List<Double> usages = items.map(v -> (Double) v.get("usage").get());
    Double revenuePredictedSugRaw = Try.of(() -> Stats.of(items.map(v -> (Double) v.get("revenue_predict_sug_raw").get())).sum()).getOrElse(0D);
    Double revenuePredictedRaw = Try.of(() -> Stats.of(items.map(v -> (Double) v.get("revenue_predict_raw").get())).sum()).getOrElse(0D);
    Double revenueLastYearRaw = Try.of(() -> Stats.of(items.map(v -> (Double) v.get("revenue_last_year_raw").get())).sum()).getOrElse(0D);
    Double revenueYearBeforeLastRaw = Try.of(() -> Stats.of(items.map(v -> (Double) v.get("revenue_year_before_last_raw").get())).sum()).getOrElse(0D);
    String label = CNY.desc(CNY.money(revenueYearBeforeLastRaw))._2;
    return HashMap.of(
      "id", id,
      "name", name,
      "usage", Try.of(() -> Stats.of(usages).mean()).getOrElse(0D),
      "usage_sum", Tuple.of(usages.count(v -> v <= 0.3D), usages.count(v -> v > 1D)),
      "revenue_year_before_last", formatMoney(CNY.money(revenueYearBeforeLastRaw), label)._1,
      "revenue_last_year", formatMoney(CNY.money(revenueLastYearRaw), label)._1,
      "revenue_year_before_last_raw", revenueYearBeforeLastRaw,
      "revenue_last_year_raw", revenueLastYearRaw,
      "last_year_date", LocalDate.now().minusYears(1).toString(),
      "year_before_last_date", LocalDate.now().minusYears(2).toString(),
      "revenue_predict", formatMoney(CNY.money(revenuePredictedRaw), label)._1,
      "revenue_predict_raw", revenuePredictedRaw,
      "revenue_increase", Option.when(revenueLastYearRaw.equals(0D), 0D).getOrElse(revenuePredictedRaw / revenueLastYearRaw - 1D),
      "revenue_predict_sug", formatMoney(CNY.money(revenuePredictedSugRaw), label)._1,
      "revenue_predict_sug_raw", revenuePredictedSugRaw,
      "revenue_increase_sug", Option.when(revenueLastYearRaw.equals(0D), 0D).getOrElse(revenuePredictedSugRaw / revenueLastYearRaw - 1D),
      "revenue_unit", label,
      "total", items.count(t -> true)
    );
  }

  private List<Map<String, String>> calculateBottomLevelSuggestions(Integer NumGreaterThan1, Double averageUsage, Integer numOfAssets) {
    if (averageUsage > 1D) {
      return List.of(HashMap.of("title", SUGGESTION_BUY, "addition", String.format("%s台设备", (int) Math.ceil((averageUsage - 1D) * numOfAssets))));
    } else if (NumGreaterThan1 > 0) {
      return List.of(HashMap.of("title", SUGGESTION_ADJUST));
    } else if (averageUsage <= 0.3D) {
      return List.of(HashMap.of("title", SUGGESTION_RAISE));
    } else {
      return List.empty();
    }
  }

  private List<Map<String, String>> calculateHigherLevelSuggestions(Seq<Seq<Map<String, String>>> lowerLevelSuggestions, String groupBy) {
    Seq<Seq<String>> suggestionLists = lowerLevelSuggestions.map(v -> v.map(sub -> sub.get("title").getOrElse("")));
    int numBuy = suggestionLists.count(v -> v.exists(v2 -> v2.contains(SUGGESTION_BUY)));
    Integer numBuyAssets = lowerLevelSuggestions
      .filter(v -> v.map(sub -> sub.get("title").getOrElse("")).exists(v2 -> v2.contains(SUGGESTION_BUY)))
      .map(v -> v.filter(sub -> sub.get("addition").isDefined()).headOption().map(v2 -> v2.get("addition").get()).get())
      .map(v -> Ints.tryParse(Option.when(v.contains("（"), v.substring(v.indexOf("（") + 1, v.indexOf("台设备"))).getOrElse(v.substring(0, v.indexOf("台设备")))))
      .sum().intValue();
    int numAjst = suggestionLists.count(v -> v.exists(v2 -> v2.contains(SUGGESTION_ADJUST)));
    int numRse = suggestionLists.count(v -> v.exists(v2 -> v2.contains(SUGGESTION_RAISE)));
    ImmutableList.Builder<Map<String, String>> totalSuggestions = new ImmutableList.Builder<Map<String, String>>();
    if (numBuy > 0) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_BUY.concat(Option.when("dept".equals(groupBy), "的科室").getOrElse("的类型")), "addition", Option.when(groupBy.equals("dept"), String.format("%s个（%s台设备）", numBuy, numBuyAssets)).getOrElse(String.format("%s种（%s台设备）", numBuy, numBuyAssets))));
    }
    if (numAjst > 0) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_ADJUST.concat(Option.when("dept".equals(groupBy), "的科室").getOrElse("的类型")), "addition", Option.when(groupBy.equals("dept"), String.format("%s个", numAjst)).getOrElse(String.format("%s种", numAjst))));
    }
    if (numRse > 0) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_RAISE.concat(Option.when("dept".equals(groupBy), "的科室").getOrElse("的类型")), "addition", Option.when(groupBy.equals("dept"), String.format("%s个", numRse)).getOrElse(String.format("%s种", numRse))));
    }
    return List.ofAll(totalSuggestions.build());
  }

  private Map<String, Object> recursivelyCalculateSuggestions(Map<String, Object> currentMap) {
    Seq<Map<String, Object>> currentItems = (Seq<Map<String, Object>>) currentMap.get("items").get();
    if (currentItems.headOption().filter(m -> m.containsKey("items")).isDefined()) {
      Map<String, Object> newMap = currentMap.put("items", currentItems.map(this::recursivelyCalculateSuggestions));
      return calculateUsageAfterSuggestion(newMap.put("suggestions", calculateHigherLevelSuggestions(((Seq<Map<String, Object>>) newMap.get("items").get()).map(v -> (Seq<Map<String, String>>) v.get("suggestions").get()), (String) newMap.get("groupby").get())));
    } else {
      return calculateUsageAfterSuggestion(currentMap.put("suggestions", calculateBottomLevelSuggestions(
        ((Tuple2<Integer, Integer>) currentMap.get("usage_sum").get())._2,
        (Double) currentMap.get("usage").get(),
        currentItems.count(v -> true)
      )));
    }
  }

  private Map<String, Object> calculateUsageAfterSuggestion(Map<String, Object> currentMap) {
    String suggestion = ((List<Map<String, String>>) currentMap.get("suggestions").get())
      .filter(v -> v.get("title").isDefined() && v.get("title").get().contains(SUGGESTION_BUY))
      .headOption().getOrElse(HashMap.of("addition", "0台设备")).get("addition").get();
    int numBuyAssets = Option.of(Ints.tryParse(Option.when(suggestion.contains("（"), suggestion.substring(suggestion.indexOf("（") + 1, suggestion.indexOf("台设备"))).getOrElse(suggestion.substring(0, suggestion.indexOf("台设备"))))).getOrElse(0);
    return currentMap.put("usage_sug", Option.when((int) currentMap.get("total").get() + numBuyAssets == 0, 0D).getOrElse((double) currentMap.get("usage").get() * (int) currentMap.get("total").get() / ((int) currentMap.get("total").get() + numBuyAssets)));
  }

  private Tuple2<String, String> formatMoney(MonetaryAmount amount, String label) {
    if ("亿".equals(label)) {
      return Tuple.of(CNY.format(amount.divide(100_000_000D)), "亿");
    } else if ("万".equals(label)) {
      return Tuple.of(CNY.format(amount.divide(10_000D)), "万");
    } else if ("元".equals(label)) {
      return Tuple.of(CNY.format(amount), "元");
    } else {
      throw new IllegalArgumentException(String.format("Input data not supported:amount %s, label %s", amount, label));
    }
  }
}

