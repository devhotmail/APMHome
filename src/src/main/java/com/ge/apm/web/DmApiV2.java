package com.ge.apm.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.DmService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javaslang.*;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;
import org.apache.commons.math3.stat.regression.SimpleRegression;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;


// Created by Leon on 4/26/17.


@RestController
@RequestMapping("/dmv2")
@Validated
public class DmApiV2 {
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(DmApiV2.class);
  private final String SUGGESTION_BUY = "建议购买";
  private final String SUGGESTION_ADJUST = "建议合理安排使用";
  private final String SUGGESTION_RAISE = "建议提高使用率";
  private final java.util.List<Double> defaultThreshold = new ImmutableList.Builder<Double>().add(0.3D, 1D).build();
  @Autowired
  private CommonService commonService;
  @Autowired
  private DmService dmService;

  public class AssetBsc {
    private int id;
    private String name;
    private int dept;
    private int type;
    private int count;

    public AssetBsc(int id, String name, int dept, int type, int count) {
      this.id = id;
      this.name = name;
      this.dept = dept;
      this.type = type;
      this.count = count;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public int getDept() {
      return dept;
    }

    public int getType() {
      return type;
    }

    public int getCount() {
      return count;
    }
  }

  public class HisData {
    private LocalDate date;
    private double depre;
    private double usage;

    public HisData(LocalDate date, double depre, double usage) {
      this.date = date;
      this.depre = depre;
      this.usage = usage;
    }

    public LocalDate getDate() {
      return date;
    }

    public double getDepre() {
      return depre;
    }

    public double getUsage() {
      return usage;
    }
  }

  public class PdtData {
    private LocalDate date;
    private double depre;
    private java.util.List<Integer> usgSum;
    private java.util.List<Double> usgThr;
    private double usgPdt;
    private double usgPdtIcs;
    private double usgSug;
    private double usgSugIcs;

    public PdtData(LocalDate date, double depre, java.util.List<Integer> usgSum, java.util.List<Double> usgThr, double usgPdt, double usgPdtIcs, double usgSug, double usgSugIcs) {
      this.date = date;
      this.depre = depre;
      this.usgSum = usgSum;
      this.usgThr = usgThr;
      this.usgPdt = usgPdt;
      this.usgPdtIcs = usgPdtIcs;
      this.usgSug = usgSug;
      this.usgSugIcs = usgSugIcs;
    }

    public LocalDate getDate() {
      return date;
    }

    public double getDepre() {
      return depre;
    }

    public java.util.List<Integer> getUsgSum() {
      return usgSum;
    }

    public java.util.List<Double> getUsgThr() {
      return usgThr;
    }

    public double getUsgPdt() {
      return usgPdt;
    }

    public double getUsgPdtIcs() {
      return usgPdtIcs;
    }

    public double getUsgSug() {
      return usgSug;
    }

    public double getUsgSugIcs() {
      return usgSugIcs;
    }
  }

  public class Asset {
    private AssetBsc assetBsc;
    private Tuple3<HisData, HisData, HisData> hisDatas;
    private Tuple1<PdtData> pdtDatas;

    public Asset(AssetBsc assetBsc, Tuple3<HisData, HisData, HisData> hisDatas, Tuple1<PdtData> pdtDatas) {
      this.assetBsc = assetBsc;
      this.hisDatas = hisDatas;
      this.pdtDatas = pdtDatas;
    }

    public AssetBsc getAssetBsc() {
      return assetBsc;
    }

    public Tuple3<HisData, HisData, HisData> getHisDatas() {
      return hisDatas;
    }

    public Tuple1<PdtData> getPdtDatas() {
      return pdtDatas;
    }
  }

  public class TypeBsc {
    private int typeId;
    private String typeName;
    private int count;

    public TypeBsc(int typeId, String typeName, int count) {
      this.typeId = typeId;
      this.typeName = typeName;
      this.count = count;
    }

    public int getTypeId() {
      return typeId;
    }

    public String getTypeName() {
      return typeName;
    }

    public int getCount() {
      return count;
    }
  }

  public class TypePdtData {
    private Seq<Map<String, String>> suggestions;
    private int buyIn;
    private PdtData pdtData;

    public TypePdtData(List<Map<String, String>> suggestions, int buyIn, PdtData pdtData) {
      this.suggestions = suggestions;
      this.buyIn = buyIn;
      this.pdtData = pdtData;
    }

    public Seq<Map<String, String>> getSuggestions() {
      return suggestions;
    }

    public PdtData getPdtData() {
      return pdtData;
    }

    public int getBuyIn() {
      return buyIn;
    }
  }


  public class TypeInfo {
    TypeBsc typeBsc;
    Tuple1<TypePdtData> typePdtDatas;
    Tuple3<HisData, HisData, HisData> hisDatas;//lstsnd,lstfst,thY

    public TypeInfo(TypeBsc typeBsc, Tuple1<TypePdtData> typePdtDatas, Tuple3<HisData, HisData, HisData> hisDatas) {
      this.typeBsc = typeBsc;
      this.typePdtDatas = typePdtDatas;
      this.hisDatas = hisDatas;
    }

    public TypeBsc getTypeBsc() {
      return typeBsc;
    }

    public Tuple1<TypePdtData> getTypePdtDatas() {
      return typePdtDatas;
    }

    public Tuple3<HisData, HisData, HisData> getHisDatas() {
      return hisDatas;
    }
  }

  public class DeptBsc {
    private int deptId;
    private String deptName;
    private int count;

    public DeptBsc(int deptId, String deptName, int count) {
      this.deptId = deptId;
      this.deptName = deptName;
      this.count = count;
    }

    public int getDeptId() {
      return deptId;
    }

    public String getDeptName() {
      return deptName;
    }

    public int getCount() {
      return count;
    }
  }

  public class DeptPdtData {
    private List<Map<String, String>> suggestions;
    private int buyIn;
    private LocalDate date;
    private double depre;
    private java.util.List<Integer> usgSum;
    private double usgPdt;
    private double usgPdtIcs;
    private double usgSug;
    private double usgSugIcs;

    public DeptPdtData(List<Map<String, String>> suggestions, int buyIn, LocalDate date, double depre, java.util.List<Integer> usgSum, double usgPdt, double usgPdtIcs, double usgSug, double usgSugIcs) {
      this.suggestions = suggestions;
      this.buyIn = buyIn;
      this.date = date;
      this.depre = depre;
      this.usgSum = usgSum;
      this.usgPdt = usgPdt;
      this.usgPdtIcs = usgPdtIcs;
      this.usgSug = usgSug;
      this.usgSugIcs = usgSugIcs;
    }

    public List<Map<String, String>> getSuggestions() {
      return suggestions;
    }

    public int getBuyIn() {
      return buyIn;
    }

    public LocalDate getDate() {
      return date;
    }

    public double getDepre() {
      return depre;
    }

    public java.util.List<Integer> getUsgSum() {
      return usgSum;
    }

    public double getUsgPdt() {
      return usgPdt;
    }

    public double getUsgPdtIcs() {
      return usgPdtIcs;
    }

    public double getUsgSug() {
      return usgSug;
    }

    public double getUsgSugIcs() {
      return usgSugIcs;
    }
  }

  public class DeptInfo {
    DeptBsc deptBsc;
    Tuple1<DeptPdtData> deptPdtDatas;
    Tuple3<HisData, HisData, HisData> hisDatas;//lstsnd,lstfst,thY

    public DeptInfo(DeptBsc deptBsc, Tuple1<DeptPdtData> deptPdtDatas, Tuple3<HisData, HisData, HisData> hisDatas) {
      this.deptBsc = deptBsc;
      this.deptPdtDatas = deptPdtDatas;
      this.hisDatas = hisDatas;
    }

    public DeptBsc getDeptBsc() {
      return deptBsc;
    }

    public Tuple1<DeptPdtData> getDeptPdtDatas() {
      return deptPdtDatas;
    }

    public Tuple3<HisData, HisData, HisData> getHisDatas() {
      return hisDatas;
    }
  }

  public class AllAssetBsc {
    private final int id = 100;
    private final String Name = "全部设备";
    private int count;

    public AllAssetBsc(int count) {
      this.count = count;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return Name;
    }

    public int getCount() {
      return count;
    }
  }

  public class AllAssetPdtData {
    private List<Map<String, String>> suggestions;
    private int buyIn;
    private LocalDate date;
    private double depre;
    private java.util.List<Integer> usgSum;
    private double usgPdt;
    private double usgPdtIcs;
    private double usgSug;
    private double usgSugIcs;

    public AllAssetPdtData(List<Map<String, String>> suggestions, int buyIn, LocalDate date, double depre, java.util.List<Integer> usgSum, double usgPdt, double usgPdtIcs, double usgSug, double usgSugIcs) {
      this.suggestions = suggestions;
      this.buyIn = buyIn;
      this.date = date;
      this.depre = depre;
      this.usgSum = usgSum;
      this.usgPdt = usgPdt;
      this.usgPdtIcs = usgPdtIcs;
      this.usgSug = usgSug;
      this.usgSugIcs = usgSugIcs;
    }

    public List<Map<String, String>> getSuggestions() {
      return suggestions;
    }

    public int getBuyIn() {
      return buyIn;
    }

    public LocalDate getDate() {
      return date;
    }

    public double getDepre() {
      return depre;
    }

    public java.util.List<Integer> getUsgSum() {
      return usgSum;
    }

    public double getUsgPdt() {
      return usgPdt;
    }

    public double getUsgPdtIcs() {
      return usgPdtIcs;
    }

    public double getUsgSug() {
      return usgSug;
    }

    public double getUsgSugIcs() {
      return usgSugIcs;
    }
  }

  public class AllAssetInfo {
    AllAssetBsc allAssetBsc;
    Tuple1<AllAssetPdtData> allAssetPdtDatas;
    Tuple3<HisData, HisData, HisData> allAssetHisDatas;

    public AllAssetInfo(AllAssetBsc allAssetBsc, Tuple1<AllAssetPdtData> allAssetPdtDatas, Tuple3<HisData, HisData, HisData> allAssetHisDatas) {
      this.allAssetBsc = allAssetBsc;
      this.allAssetPdtDatas = allAssetPdtDatas;
      this.allAssetHisDatas = allAssetHisDatas;
    }

    public AllAssetBsc getAllAssetBsc() {
      return allAssetBsc;
    }

    public Tuple1<AllAssetPdtData> getAllAssetPdtDatas() {
      return allAssetPdtDatas;
    }

    public Tuple3<HisData, HisData, HisData> getAllAssetHisDatas() {
      return allAssetHisDatas;
    }
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<java.util.Map<String, Object>> desicionMaking(HttpServletRequest request,
                                                                      @Pattern(regexp = "dept|type") @RequestParam(value = "groupby", required = false) String groupBy,
                                                                      @Min(2000) @RequestParam(value = "year", required = true) Integer year,
                                                                      @Min(1) @RequestParam(value = "dept", required = false) Integer dept) throws JsonProcessingException {
    UserAccount user = UserContext.getCurrentLoginUser();
    java.util.Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), java.util.Map.Entry::getValue).toBlocking().single();
    java.util.Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    if (Option.of(groupBy).isDefined() && Option.of(dept).isEmpty()) {
      if ("type".equals(groupBy))
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(getResultByType(user.getHospitalId(), user.getSiteId(), null, ImmutableMap.of(), ImmutableMap.of(), groups, year));
      else
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(getResultByDept(user.getHospitalId(), user.getSiteId(), null, ImmutableMap.of(), ImmutableMap.of(), groups, depts, year));
    } else if (Option.of(groupBy).isEmpty() && Option.of(dept).isDefined()) {
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(getResultByType(user.getHospitalId(), user.getSiteId(), dept, ImmutableMap.of(), ImmutableMap.of(), groups, year));
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }

  @RequestMapping(method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<java.util.Map<String, Object>> desicionMakingPut(HttpServletRequest request,
                                                                         @Pattern(regexp = "dept|type") @RequestParam(value = "groupby", required = false) String groupBy,
                                                                         @Min(2000) @RequestParam(value = "year", required = true) Integer year,
                                                                         @Min(1) @RequestParam(value = "dept", required = false) Integer dept,
                                                                         @RequestBody(required = true) String inputBody) throws JsonProcessingException {
    UserAccount user = UserContext.getCurrentLoginUser();
    java.util.Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), java.util.Map.Entry::getValue).toBlocking().single();
    java.util.Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());
    Config parsedBody = ConfigFactory.parseString(inputBody);
    java.util.Map<Integer, Double> userPredictedUsage = HashMap.ofEntries(
      List.ofAll(Try.of(() -> parsedBody.getConfigList("config")).get())
        .filter(v -> !Try.of(() -> v.getString("change")).getOrElse("").equals(""))
        .map(v2 -> Tuple.of(Ints.tryParse(v2.getString("id")), Doubles.tryParse(v2.getString("change")))))
      .toJavaMap();
    java.util.Map<Integer, java.util.List<Double>> userInputThreshold = HashMap.ofEntries(
      List.ofAll(Try.of(() -> parsedBody.getConfigList("config")).get())
        .filter(v -> !Try.of(() -> v.getStringList("threshold")).getOrElse(ImmutableList.of("")).equals(ImmutableList.of("")))
        .map(v2 -> Tuple.of(Ints.tryParse(v2.getString("id")),
          List.ofAll(ImmutableList.of(Option.of(Doubles.tryParse(v2.getStringList("threshold").get(0))).getOrElse(0.3D),
            Option.of(Doubles.tryParse(v2.getStringList("threshold").get(1))).getOrElse(1D)))
            .toJavaList()))
    ).toJavaMap();
    if (Option.of(groupBy).isDefined() && Option.of(dept).isEmpty()) {
      if ("type".equals(groupBy))
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(getResultByType(user.getHospitalId(), user.getSiteId(), null, userPredictedUsage, userInputThreshold, groups, year));
      else
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(getResultByDept(user.getHospitalId(), user.getSiteId(), null, userPredictedUsage, userInputThreshold, groups, depts, year));
    } else if (Option.of(groupBy).isEmpty() && Option.of(dept).isDefined()) {
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(getResultByType(user.getHospitalId(), user.getSiteId(), dept, ImmutableMap.of(), ImmutableMap.of(), groups, year));
    } else {
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "input data is not supported"));
    }
  }


  /**
   * This function combines data form CalculateHistoryData and predictFutureData to form all information needed for one asset
   * THis function may change if user requires another time slots on his/her page.
   *
   * @param lstSnd  AssetBsc + HisData at the time slot of the year before last
   * @param lstFst  AssetBsc + HisData at the time slot of last year
   * @param tsY     AssetBsc + HisData at the time slot of this year from 1, Jan. up to now.
   * @param pdtData AssetBsc + PdtData for 2017 or 2018
   * @return All information needed for single Asset
   */
  private List<Asset> itemsDataCalculation(Observable<Tuple2<AssetBsc, HisData>> lstSnd,
                                           Observable<Tuple2<AssetBsc, HisData>> lstFst,
                                           Observable<Tuple2<AssetBsc, HisData>> tsY,
                                           Observable<Tuple2<AssetBsc, PdtData>> pdtData) {
    return List.ofAll(Observable.zip(lstSnd, lstFst, tsY, pdtData,
      (t1, t2, t3, t4) -> new Asset(t1._1, Tuple.of(t1._2, t2._2, t3._2), Tuple.of(t4._2)))
      .toBlocking().toIterable());
  }

  /**
   * This function returns a set of Basic information + calculated data for a specific historical time slot
   * THis function may change if user requires another field of data on his/her page
   *
   * @param siteId     siteId
   * @param hospitalId hospitalId
   * @param dept       dept
   * @param start      start of the time slot
   * @param end        end of the time slot
   * @return AssetBsc + HisData for all single Assets
   */
  private Observable<Tuple2<AssetBsc, HisData>> CalculateHistoryData(Integer siteId, Integer hospitalId,
                                                                     Integer dept, LocalDate start, LocalDate end) {
    return dmService.findAssets(siteId, hospitalId, dept, start, end)
      .map(v -> Tuple.of(new AssetBsc(v._1, v._2, v._3, v._4, 1),
        new HisData(start, v._6, v._5)));
  }

  /**
   * This function returns a set of generate Basic information + calculated data for a specific historical time slot
   * This function may change if the predict algorithm changes
   *
   * @param lstsnd             AssetBsc + HisData at the time slot of the year before last
   * @param lstfst             AssetBsc + HisData at the time slot of last year
   * @param userPredictedUsage user predicted value of usage
   * @param userInputThreshold user predicted max/min threshold
   * @param year               year required to show
   * @return AssetBsc + PdtData for all single Assets
   */
  private Observable<Tuple2<AssetBsc, PdtData>> predictFutureData(Observable<Tuple2<AssetBsc, HisData>> lstsnd,
                                                                  Observable<Tuple2<AssetBsc, HisData>> lstfst,
                                                                  java.util.Map<Integer, java.util.Map<Integer, Double>> systemPredictedUsage,
                                                                  java.util.Map<Integer, Double> userPredictedUsage,
                                                                  java.util.Map<Integer, java.util.List<Double>> userInputThreshold,
                                                                  int year) {
    return Observable.zip(lstsnd, lstfst, (snd, fst) -> Tuple.of(snd._1, fst._2.getDepre(),
      Option.when(Option.of(userPredictedUsage.get(snd._1.getId())).isDefined(), (Option.of(userPredictedUsage.get(snd._1.getId())).getOrElse(0D) + 1D) * fst._2.getUsage()).getOrElse(
        systemPredictedUsage.get(snd._1.getType()).get(snd._1.getId())),
      Option.of(userInputThreshold.get(snd._1.getId())).getOrElse(defaultThreshold),
      LocalDate.ofYearDay(year, 1), fst._2.getUsage()
    )).map(v -> Tuple.of(v._1, calculatePdtData(v._2, v._3, v._4, v._5, v._6)));
  }

  private Integer localDateToX(LocalDate input) {
    return (int) LocalDate.ofYearDay(2000, 1).until(input, ChronoUnit.DAYS);
  }

  private LocalDate xToLocaldate(Integer x) {
    return LocalDate.ofYearDay(2000, 1).plusDays(x);
  }

  //id, X, asset_group, usage
  private List<Tuple4<Integer, Integer, Integer, Double>> predictDataPrepare(Observable<Tuple4<Integer, LocalDate, Integer, Double>> items) {
    return List.ofAll(items.toBlocking().toIterable()).map(v -> Tuple.of(v._1, localDateToX(v._2), v._3, v._4));
  }

  //Map<asset_group, Map<id, usage_ratio>>
  private Map<Integer, Map<Integer, Double>> ratios(List<Tuple4<Integer, Integer, Integer, Double>> items) {
    return HashMap.ofEntries(items.filter(v -> localDateToX(LocalDate.of(LocalDate.now().getYear() - 1, 12, 1)).equals(v._2))
      .groupBy(v -> v._3)
      .map((k, v) -> Tuple.of(Tuple.of(k, v.map(sub -> sub._4).average().getOrElse(0D)), v.map(sub -> Tuple.of(sub._1, sub._4))))
      .map(v -> Tuple.of(v._1._1, v._2.map(sub -> Tuple.of(sub._1, Option.when(!v._1._2.equals(0D), sub._2 / v._1._2).getOrElse(0D)))))
      .map(v -> Tuple.of(v._1, HashMap.ofEntries(v._2))));
  }

  private java.util.Map<Integer, java.util.Map<Integer, Double>> predictUsage(List<Tuple4<Integer, Integer, Integer, Double>> items, Map<Integer, Map<Integer, Double>> ratios, int year) {
    Map<Integer, Double> predicts = HashMap.ofEntries(items.groupBy(v -> v._3)
      .map((k, v) -> Tuple.of(k,
        v.groupBy(sub -> sub._2).map(pair -> Tuple.of(pair._1, pair._2))
          .sortBy(sub -> sub._1)
          .map(sub -> Tuple.of(sub._1, sub._2.map(subV -> subV._4).average().getOrElse(0D)))
      )).map(v -> Tuple.of(v._1, predictOneType(v._2, year))));
    return ratios.map((k, v) -> Tuple.of(k, v.map((subK, subV) -> Tuple.of(subK, subV * predicts.get(k).getOrElse(0D))).toJavaMap())).toJavaMap();
  }

  //input: Historical Seq<Tuple2<id,usage>>for one type
  //output: Seq<Tuple2<id,usage>>for one type including future data
  private Double predictOneType(Seq<Tuple2<Integer, Double>> monthlyData, int year) {
    monthlyData = monthlyData.removeLast(v -> true);
    LocalDate lastHisDay = xToLocaldate(monthlyData.last()._1);
    for (int i = 0; i < (int) lastHisDay.until(LocalDate.now().withDayOfMonth(1).minusMonths(1), ChronoUnit.MONTHS); i++) {
      monthlyData = monthlyData.append(Tuple.of(localDateToX(lastHisDay.plusMonths(i + 1)), 0D));
    }
    SimpleRegression simpleRegression = new SimpleRegression();
    monthlyData.forEach(v -> simpleRegression.addData(v._1, v._2));
    lastHisDay = xToLocaldate(monthlyData.last()._1);
    for (int i = 0; i < (int) lastHisDay.until(LocalDate.of(LocalDate.now().getYear() + 1, 12, 1), ChronoUnit.MONTHS); i++) {
      monthlyData = monthlyData.append(Tuple.of(localDateToX(lastHisDay.plusMonths(i + 1)), simpleRegression.predict(localDateToX(lastHisDay.plusMonths(i + 1)))));
    }
    return monthlyData.filter(v -> xToLocaldate(v._1).getYear() == year)
      .map(v -> v._2 * (int) xToLocaldate(v._1).until(xToLocaldate(v._1).plusMonths(1), ChronoUnit.DAYS)).sum().doubleValue()
      / (double) LocalDate.ofYearDay(year, 1).until(LocalDate.ofYearDay(year, 1).plusYears(1), ChronoUnit.DAYS);
  }

  /**
   * This function is used to deal with the suggestion logic of single asset, using predicted usage and some other values to generate PdtData
   * This function may change if the user want something else about future data on his/her screen
   *
   * @param depre        deprecation
   * @param usage        predicted usage
   * @param thre         threshold for suggestion logic
   * @param date         beginning of the required time slot
   * @param usageHistory usage of last year
   * @return PdtData for one single asset
   */
  private PdtData calculatePdtData(Double depre, Double usage, java.util.List<Double> thre, LocalDate date, Double usageHistory) {
    return new PdtData(date, depre,
      new ImmutableList.Builder<Integer>().add(Option.when(usage <= thre.get(0), 1).getOrElse(0), Option.when(usage > thre.get(1), 1).getOrElse(0)).build(),
      thre, usage, Option.when(usageHistory.equals(0D), 0D).getOrElse(usage / usageHistory - 1D),
      Option.when(usage > 1D, 1D).getOrElse(usage),
      Option.when(usageHistory.equals(0D), 0D).getOrElse(Option.when(usage > 1D, 1D).getOrElse(usage) / usageHistory - 1D)
    );
  }

  /**
   * This function is used to calculate information for a type of assets.
   * This function should not change over time
   *
   * @param items  All assets data
   * @param groups a map of asset_group with their ids as keys and names as value
   * @return information for a type of asset
   */
  private TypeInfo calculateTypeInfo(List<Asset> items, java.util.Map<Integer, String> groups) {
    int typeId = items.get(0).getAssetBsc().getType();
    return new TypeInfo(
      new TypeBsc(typeId, groups.get(typeId),
        items.map(v -> v.getAssetBsc().getCount()).sum().intValue()),
      calculateTypePdtData(items.map(Asset::getPdtDatas),
        items.map(v -> v.getAssetBsc().getCount()).sum().intValue(),
        items.map(v -> v.getHisDatas()._2.getUsage()).average().getOrElse(0D)),
      calculateHistoricalData(items.map(Asset::getHisDatas))
    );
  }

  /**
   * This function is used by calculateTypeInfo() to return predicted values about a type of assets.
   * This function may change when the number of time slots the user wants to see changes.
   *
   * @param items         PdtData for single assets in this type
   * @param count         number of assets in this type
   * @param lastYearUsage average usage for this type last year
   * @return predicted data of this type
   */
  private Tuple1<TypePdtData> calculateTypePdtData(List<Tuple1<PdtData>> items, int count, Double lastYearUsage) {
    //for first item of Tuple
    List<PdtData> subItems = items.map(v -> v._1);
    java.util.List<Integer> usageSum = ImmutableList.of(subItems.map(v -> v.getUsgSum().get(0)).sum().intValue(),
      subItems.map(v -> v.getUsgSum().get(1)).sum().intValue());
    double averageUsage = subItems.map(PdtData::getUsgPdt).average().getOrElse(0D);
    int buyIn = (int) Math.ceil((averageUsage / (subItems.get(0).getUsgThr().get(1) + 1e-7D) - 1D) * count);
    return Tuple.of(new TypePdtData(calculateBottomLevelSuggestions(usageSum.get(1), averageUsage, buyIn, subItems.get(0).getUsgThr()), buyIn, new PdtData(
      subItems.get(0).getDate(), subItems.map(PdtData::getDepre).sum().doubleValue(), usageSum, subItems.get(0).getUsgThr(), averageUsage,
      Option.when(lastYearUsage.equals(0D), 0D).getOrElse(averageUsage / lastYearUsage - 1D),
      Option.when(buyIn > 0, averageUsage * count / (count + buyIn)).getOrElse(averageUsage),
      Option.when(lastYearUsage.equals(0D), 0D).getOrElse(Option.when(buyIn > 0, averageUsage * count / (count + buyIn)).getOrElse(averageUsage) / lastYearUsage - 1D)
    )));
  }

  /**
   * This function is used by calculateTypeInfo(),calculateDeptInfo() to return historical values about a type of assets.
   * This function may change when the number of time slots the user wants to see changes.
   *
   * @param items HstData for single assets in this type
   * @return HisData of this type
   */
  private Tuple3<HisData, HisData, HisData> calculateHistoricalData(Seq<Tuple3<HisData, HisData, HisData>> items) {
    return Tuple.of(dealWithHisdata(items.map(v -> v._1)), dealWithHisdata(items.map(v -> v._2)), dealWithHisdata(items.map(v -> v._3)));
  }

  /**
   * This function is used by calculateHistoricalData() to return historical values about a type of assets in a single time slots.
   * This function may change when the historical data user wants on his/her screen changes
   *
   * @param hisDatas HisData for all single assets in a certain time slot
   * @return HisData for all current type of assets in the same time slot
   */
  private HisData dealWithHisdata(Seq<HisData> hisDatas) {
    return new HisData(hisDatas.get(0).getDate(),
      hisDatas.map(HisData::getDepre).sum().doubleValue(),
      hisDatas.map(HisData::getUsage).average().getOrElse(0D));
  }

  /**
   * This function is used to calculate suggestions for a type of assets
   *
   * @param NumGreaterThan1 number of assets with usage greater than upper limit
   * @param averageUsage    average predicted usage of current set of assets
   * @param buyIn           number of assets that should be bought in
   * @return A list of suggestions
   */
  private List<Map<String, String>> calculateBottomLevelSuggestions(Integer NumGreaterThan1, Double averageUsage, Integer buyIn, java.util.List<Double> usageThreshold) {
    if (averageUsage > usageThreshold.get(1)) {
      return List.of(HashMap.of("title", SUGGESTION_BUY, "addition", String.format("%s台设备", buyIn)));
    } else if (NumGreaterThan1 > 0) {
      return List.of(HashMap.of("title", SUGGESTION_ADJUST));
    } else if (averageUsage <= usageThreshold.get(0)) {
      return List.of(HashMap.of("title", SUGGESTION_RAISE));
    } else {
      return List.empty();
    }
  }

  /**
   * This function is used to calculate information for a type of assets.
   * This function should not change overtime
   *
   * @param items  information of types in current dept
   * @param depts  depts map
   * @param deptId current dept Id
   * @return information of current dept
   */
  private DeptInfo calculateDeptInfo(Seq<TypeInfo> items, java.util.Map<Integer, String> depts, int deptId) {
    return new DeptInfo(new DeptBsc(deptId, depts.get(deptId), items.map(v -> v.getTypeBsc().getCount()).sum().intValue()),
      calculateDeptPdtData(items,
        items.map(v -> v.getTypeBsc().getCount()).sum().intValue(),
        items.map(v -> v.getHisDatas()._2.getUsage()).average().getOrElse(0D)),
      calculateHistoricalData(items.map(TypeInfo::getHisDatas)));
  }

  /**
   * Calculate predicted data for a dept
   * This function may change when the number of time slots the user wants to see changes.
   *
   * @param items         list of TypePdtData in current dept
   * @param count         number of assets in current dept
   * @param lastYearUsage usage of last year in current dept
   * @return DeptPdtData
   */
  private Tuple1<DeptPdtData> calculateDeptPdtData(Seq<TypeInfo> items, int count, Double lastYearUsage) {
    Seq<TypePdtData> subItems = items.map(v -> v.getTypePdtDatas()._1);
    int buyIn = subItems.map(TypePdtData::getBuyIn).sum().intValue();
    double averageUsage = subItems.map(v -> v.getPdtData().getUsgPdt()).average().getOrElse(0D);
    java.util.List<Integer> usageSum = ImmutableList.of(subItems.map(v -> v.getPdtData().getUsgSum().get(0)).sum().intValue(),
      subItems.map(v -> v.getPdtData().getUsgSum().get(1)).sum().intValue());
    return Tuple.of(new DeptPdtData(
      calculateHighLevelSuggestions(items.map(v -> Tuple.of(v.getTypeBsc().getTypeName(), v.getTypePdtDatas()._1.getSuggestions())), buyIn, "type"), buyIn,
      subItems.get(0).getPdtData().getDate(),
      subItems.map(v -> v.getPdtData().getDepre()).sum().doubleValue(), usageSum,
      averageUsage,
      Option.when(lastYearUsage.equals(0D), 0D).getOrElse(averageUsage / lastYearUsage - 1D),
      Option.when(buyIn > 0, averageUsage * count / (count + buyIn)).getOrElse(averageUsage),
      Option.when(lastYearUsage.equals(0D), 0D).getOrElse(Option.when(buyIn > 0, averageUsage * count / (count + buyIn)).getOrElse(averageUsage) / lastYearUsage - 1D)
    ));
  }


  /**
   * Calculate High level suggestions using low level suggestions
   * THis function may change when suggestion logic changes
   *
   * @param lowerLevel a list of low level suggestions and the corresponding dept or type name
   * @param buyIn      number of buyIn assets
   * @param groupBy    dept|type
   * @return High level suggestions
   */
  private List<Map<String, String>> calculateHighLevelSuggestions(Seq<Tuple2<String, Seq<Map<String, String>>>> lowerLevel, Integer buyIn, String groupBy) {
    Seq<Tuple2<String, Seq<Map<String, String>>>> sugBuy = lowerLevel.filter(v -> v._2.exists(sub -> sub.get("title").getOrElse("").contains(SUGGESTION_BUY)));
    Seq<Tuple2<String, Seq<Map<String, String>>>> sugAjst = lowerLevel.filter(v -> v._2.exists(sub -> sub.get("title").getOrElse("").contains(SUGGESTION_ADJUST)));
    Seq<Tuple2<String, Seq<Map<String, String>>>> sugRse = lowerLevel.filter(v -> v._2.exists(sub -> sub.get("title").getOrElse("").contains(SUGGESTION_RAISE)));
    ImmutableList.Builder<Map<String, String>> totalSuggestions = new ImmutableList.Builder<Map<String, String>>();
    if (!sugBuy.isEmpty()) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_BUY.concat(Option.when("dept".equals(groupBy), "的科室").getOrElse("的类型")), "addition", sugBuy.reduce((prev, next) -> Tuple.of(prev._1 + ", " + next._1, prev._2))._1.concat(String.format(" (共%s台设备)", buyIn))));
    }
    if (!sugAjst.isEmpty()) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_ADJUST.concat(Option.when("dept".equals(groupBy), "的科室").getOrElse("的类型")), "addition", sugAjst.reduce((prev, next) -> Tuple.of(prev._1 + ", " + next._1, prev._2))._1));
    }
    if (!sugRse.isEmpty()) {
      totalSuggestions.add(HashMap.of("title", SUGGESTION_ADJUST.concat(Option.when("dept".equals(groupBy), "的科室").getOrElse("的类型")), "addition", sugRse.reduce((prev, next) -> Tuple.of(prev._1 + ", " + next._1, prev._2))._1));
    }
    return List.ofAll(totalSuggestions.build());
  }

  private AllAssetInfo calculateAllAssetInfoDept(Seq<DeptInfo> items) {
    return new AllAssetInfo(new AllAssetBsc(items.map(v -> v.getDeptBsc().getCount()).sum().intValue()),
      calculateAllAssetPdtDataDept(items,
        items.map(v -> v.getDeptBsc().getCount()).sum().intValue(),
        items.map(v -> v.getHisDatas()._2.getUsage()).average().getOrElse(0D)),
      calculateHistoricalData(items.map(DeptInfo::getHisDatas))
    );
  }

  private AllAssetInfo calculateAllAssetInfoType(Seq<TypeInfo> items) {
    return new AllAssetInfo(new AllAssetBsc(items.map(v -> v.getTypeBsc().getCount()).sum().intValue()),
      calculateAllAssetPdtDataType(items,
        items.map(v -> v.getTypeBsc().getCount()).sum().intValue(),
        items.map(v -> v.getHisDatas()._2.getUsage()).average().getOrElse(0D)),
      calculateHistoricalData(items.map(TypeInfo::getHisDatas)));
  }

  private Tuple1<AllAssetPdtData> calculateAllAssetPdtDataType(Seq<TypeInfo> items, int count, Double lastYearUsage) {
    Seq<TypePdtData> subItems = items.map(v -> v.getTypePdtDatas()._1);
    int buyIn = subItems.map(TypePdtData::getBuyIn).sum().intValue();
    double averageUsage = subItems.map(v -> v.getPdtData().getUsgPdt()).average().getOrElse(0D);
    java.util.List<Integer> usageSum = ImmutableList.of(subItems.map(v -> v.getPdtData().getUsgSum().get(0)).sum().intValue(),
      subItems.map(v -> v.getPdtData().getUsgSum().get(1)).sum().intValue());
    return Tuple.of(new AllAssetPdtData(
      calculateHighLevelSuggestions(items.map(v -> Tuple.of(v.getTypeBsc().getTypeName(), v.getTypePdtDatas()._1.getSuggestions())), buyIn, "type"), buyIn,
      subItems.get(0).getPdtData().getDate(),
      subItems.map(v -> v.getPdtData().getDepre()).sum().doubleValue(), usageSum,
      averageUsage,
      Option.when(lastYearUsage.equals(0D), 0D).getOrElse(averageUsage / lastYearUsage - 1D),
      Option.when(buyIn > 0, averageUsage * count / (count + buyIn)).getOrElse(averageUsage),
      Option.when(lastYearUsage.equals(0D), 0D).getOrElse(Option.when(buyIn > 0, averageUsage * count / (count + buyIn)).getOrElse(averageUsage) / lastYearUsage - 1D)
    ));
  }

  private Tuple1<AllAssetPdtData> calculateAllAssetPdtDataDept(Seq<DeptInfo> items, int count, Double lastYearUsage) {
    Seq<DeptPdtData> subItems = items.map(v -> v.getDeptPdtDatas()._1);
    int buyIn = subItems.map(DeptPdtData::getBuyIn).sum().intValue();
    double averageUsage = subItems.map(DeptPdtData::getUsgPdt).average().getOrElse(0D);
    java.util.List<Integer> usageSum = ImmutableList.of(subItems.map(v -> v.getUsgSum().get(0)).sum().intValue(),
      subItems.map(v -> v.getUsgSum().get(1)).sum().intValue());
    return Tuple.of(new AllAssetPdtData(
      calculateHighLevelSuggestions(items.map(v -> Tuple.of(v.getDeptBsc().getDeptName(), v.getDeptPdtDatas()._1.getSuggestions())), buyIn, "dept"), buyIn,
      subItems.get(0).getDate(),
      subItems.map(DeptPdtData::getUsgPdt).sum().doubleValue(), usageSum,
      averageUsage,
      Option.when(lastYearUsage.equals(0D), 0D).getOrElse(averageUsage / lastYearUsage - 1D),
      Option.when(buyIn > 0, averageUsage * count / (count + buyIn)).getOrElse(averageUsage),
      Option.when(lastYearUsage.equals(0D), 0D).getOrElse(Option.when(buyIn > 0, averageUsage * count / (count + buyIn)).getOrElse(averageUsage) / lastYearUsage - 1D)
    ));
  }

  private java.util.Map<String, Object> getResultByType(Integer hospitalId, Integer siteId, Integer dept,
                                                        java.util.Map<Integer, Double> userPredictedUsage,
                                                        java.util.Map<Integer, java.util.List<Double>> userInputThreshold,
                                                        java.util.Map<Integer, String> groups,
                                                        int year) {
    List<Tuple4<Integer, Integer, Integer, Double>> predictData = predictDataPrepare(dmService.findMonthUsage(siteId, hospitalId, LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now()));
    Seq<Tuple2<TypeInfo, List<Asset>>> tmp =
      itemsDataCalculation(CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now().withDayOfYear(1).minusYears(1).minusDays(1)),
        CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1).minusYears(1), LocalDate.now().withDayOfYear(1).minusDays(1)),
        CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1), LocalDate.now()),
        predictFutureData(CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now().withDayOfYear(1).minusYears(1).minusDays(1)),
          CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1).minusYears(1), LocalDate.now().withDayOfYear(1).minusDays(1)),
          predictUsage(predictData, ratios(predictData), year),
          userPredictedUsage, userInputThreshold,
          year)
      ).groupBy(v -> v.getAssetBsc().getType()).values().map(v -> Tuple.of(calculateTypeInfo(v, groups), v));
    Tuple2<AllAssetInfo, Seq<Tuple2<TypeInfo, List<Asset>>>> initResult = Tuple.of(calculateAllAssetInfoType(tmp.map(v -> v._1)), tmp);
    return new ImmutableMap.Builder<String, Object>()
      .putAll(mapAllAssets(initResult._1))
      .put("items", initResult._2.map(v ->
        new ImmutableMap.Builder<String, Object>()
          .putAll(mapTypeInfo(v._1, Option.none()))
          .put("items", v._2.map(sub ->
            new ImmutableMap.Builder<String, Object>()
              .putAll(mapAssetInfo(sub))
              .build()
          ).toJavaList())
          .build()
      ).toJavaList())
      .build();
  }

  private java.util.Map<String, Object> getResultByDept(Integer hospitalId, Integer siteId, Integer dept,
                                                        java.util.Map<Integer, Double> userPredictedUsage,
                                                        java.util.Map<Integer, java.util.List<Double>> userInputThreshold,
                                                        java.util.Map<Integer, String> groups,
                                                        java.util.Map<Integer, String> depts,
                                                        int year) {
    List<Tuple4<Integer, Integer, Integer, Double>> predictData = predictDataPrepare(dmService.findMonthUsage(siteId, hospitalId, LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now()));
    Seq<Tuple2<DeptInfo, Seq<Tuple2<TypeInfo, List<Asset>>>>> tmp =
      itemsDataCalculation(CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now().withDayOfYear(1).minusYears(1).minusDays(1)),
        CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1).minusYears(1), LocalDate.now().withDayOfYear(1).minusDays(1)),
        CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1), LocalDate.now()),
        predictFutureData(CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1).minusYears(2), LocalDate.now().withDayOfYear(1).minusYears(1).minusDays(1)),
          CalculateHistoryData(hospitalId, siteId, dept, LocalDate.now().withDayOfYear(1).minusYears(1), LocalDate.now().withDayOfYear(1).minusDays(1)),
          predictUsage(predictData, ratios(predictData), year),
          userPredictedUsage, userInputThreshold,
          year)
      ).groupBy(v -> v.getAssetBsc().getDept()).values().map(v ->
        v.groupBy(v2 -> v2.getAssetBsc().getType()).values()
          .map(v3 -> Tuple.of(calculateTypeInfo(v3, groups), v3))
      ).map(v4 -> Tuple.of(calculateDeptInfo(v4.map(v5 -> v5._1), depts, v4.get(0)._2.get(0).getAssetBsc().getDept()), v4));
    Tuple2<AllAssetInfo, Seq<Tuple2<DeptInfo, Seq<Tuple2<TypeInfo, List<Asset>>>>>> initResult = Tuple.of(calculateAllAssetInfoDept(tmp.map(v -> v._1)), tmp);
    return new ImmutableMap.Builder<String, Object>()
      .putAll(mapAllAssets(initResult._1))
      .put("items", initResult._2.map(v ->
        new ImmutableMap.Builder<String, Object>()
          .putAll(mapDeptInfo(v._1))
          .put("items", v._2.map(v2 ->
            new ImmutableMap.Builder<String, Object>()
              .putAll(mapTypeInfo(v2._1, Option.of(v._1.getDeptBsc().getDeptId())))
              .put("items", v2._2.map(v3 ->
                new ImmutableMap.Builder<String, Object>()
                  .putAll(mapAssetInfo(v3))
                  .build()
              ).toJavaList())
              .build()
          ).toJavaList())
          .build()
      ).toJavaList())
      .build();
  }

  private java.util.Map<String, Object> mapAssetInfo(Asset item) {
    return new ImmutableMap.Builder<String, Object>()
      .put("id", item.getAssetBsc().getId())
      .put("name", item.getAssetBsc().getName())
      .put("size", item.getHisDatas()._2.getDepre())
      .put("usage_sum", item.getPdtDatas()._1.getUsgSum())
      .put("usage_threshold", item.getPdtDatas()._1.getUsgThr())
      .put("historical_data", ImmutableList.of(
        ImmutableMap.of("year", item.getHisDatas()._1.getDate().getYear(), "usage", item.getHisDatas()._1.getUsage()),
        ImmutableMap.of("year", item.getHisDatas()._2.getDate().getYear(), "usage", item.getHisDatas()._2.getUsage()),
        ImmutableMap.of("year", item.getHisDatas()._3.getDate().getYear(), "usage", item.getHisDatas()._3.getUsage())))
      .put("usage_predict", item.getPdtDatas()._1.getUsgPdt())
      .put("usage_predict_increase", item.getPdtDatas()._1.getUsgPdtIcs())
      .put("usage_sug", item.getPdtDatas()._1.getUsgSug())
      .put("usage_sug_increase", item.getPdtDatas()._1.getUsgSugIcs())
      .build();
  }

  private java.util.Map<String, Object> mapTypeInfo(TypeInfo item, Option<Integer> dept) {
    return new ImmutableMap.Builder<String, Object>()
      .put("id", dept.map(v -> String.format("%s-%s", v, item.getTypeBsc().getTypeId())).getOrElse(String.format("%s", item.getTypeBsc().getTypeId())))
      .put("name", item.getTypeBsc().getTypeName())
      .put("size", item.getHisDatas()._2.getDepre())
      .put("usage_sum", item.getTypePdtDatas()._1.getPdtData().getUsgSum())
      .put("usage_threshold", item.getTypePdtDatas()._1.getPdtData().getUsgThr())
      .put("historical_data", ImmutableList.of(
        ImmutableMap.of("year", item.getHisDatas()._1.getDate().getYear(), "usage", item.getHisDatas()._1.getUsage()),
        ImmutableMap.of("year", item.getHisDatas()._2.getDate().getYear(), "usage", item.getHisDatas()._2.getUsage()),
        ImmutableMap.of("year", item.getHisDatas()._3.getDate().getYear(), "usage", item.getHisDatas()._3.getUsage())))
      .put("usage_predict", item.getTypePdtDatas()._1.getPdtData().getUsgPdt())
      .put("usage_predict_increase", item.getTypePdtDatas()._1.getPdtData().getUsgPdtIcs())
      .put("usage_sug", item.getTypePdtDatas()._1.getPdtData().getUsgSug())
      .put("usage_sug_increase", item.getTypePdtDatas()._1.getPdtData().getUsgSugIcs())
      .put("suggestions", item.getTypePdtDatas()._1.getSuggestions().map(Map::toJavaMap).toJavaList())
      .build();
  }

  private java.util.Map<String, Object> mapDeptInfo(DeptInfo item) {
    return new ImmutableMap.Builder<String, Object>()
      .put("id", item.getDeptBsc().getDeptId())
      .put("name", item.getDeptBsc().getDeptName())
      .put("size", item.getHisDatas()._2.getDepre())
      .put("usage_sum", item.getDeptPdtDatas()._1.getUsgSum())
      .put("historical_data", ImmutableList.of(
        ImmutableMap.of("year", item.getHisDatas()._1.getDate().getYear(), "usage", item.getHisDatas()._1.getUsage()),
        ImmutableMap.of("year", item.getHisDatas()._2.getDate().getYear(), "usage", item.getHisDatas()._2.getUsage()),
        ImmutableMap.of("year", item.getHisDatas()._3.getDate().getYear(), "usage", item.getHisDatas()._3.getUsage())))
      .put("usage_predict", item.getDeptPdtDatas()._1.getUsgPdt())
      .put("usage_predict_increase", item.getDeptPdtDatas()._1.getUsgPdtIcs())
      .put("usage_sug", item.getDeptPdtDatas()._1.getUsgSug())
      .put("usage_sug_increase", item.getDeptPdtDatas()._1.getUsgSugIcs())
      .put("suggestions", item.getDeptPdtDatas()._1.getSuggestions().map(Map::toJavaMap).toJavaList())
      .build();
  }

  private java.util.Map<String, Object> mapAllAssets(AllAssetInfo item) {
    return new ImmutableMap.Builder<String, Object>()
      .put("id", item.getAllAssetBsc().getId())
      .put("name", item.getAllAssetBsc().getName())
      .put("size", item.getAllAssetHisDatas()._2.getDepre())
      .put("usage_sum", item.getAllAssetPdtDatas()._1.getUsgSum())
      .put("historical_data", ImmutableList.of(
        ImmutableMap.of("year", item.getAllAssetHisDatas()._1.getDate().getYear(), "usage", item.getAllAssetHisDatas()._1.getUsage()),
        ImmutableMap.of("year", item.getAllAssetHisDatas()._2.getDate().getYear(), "usage", item.getAllAssetHisDatas()._2.getUsage()),
        ImmutableMap.of("year", item.getAllAssetHisDatas()._3.getDate().getYear(), "usage", item.getAllAssetHisDatas()._3.getUsage())))
      .put("usage_predict", item.getAllAssetPdtDatas()._1.getUsgPdt())
      .put("usage_predict_increase", item.getAllAssetPdtDatas()._1.getUsgPdtIcs())
      .put("usage_sug", item.getAllAssetPdtDatas()._1.getUsgSug())
      .put("usage_sug_increase", item.getAllAssetPdtDatas()._1.getUsgSugIcs())
      .put("suggestions", item.getAllAssetPdtDatas()._1.getSuggestions().map(Map::toJavaMap).toJavaList())
      .build();
  }
}
