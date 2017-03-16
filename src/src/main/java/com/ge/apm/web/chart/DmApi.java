package com.ge.apm.web.chart;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.DmService;
import com.google.common.collect.ImmutableMap;
import com.google.common.math.Stats;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.Tuple4;
import javaslang.Tuple5;
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
    Map<Integer, String> groups = Observable.from(commonService.findFields(user.getSiteId(), "dept".equals(groupBy) ? "clinicalDeptId" : "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> Ints.tryParse(e.getKey()), Map.Entry::getValue).toBlocking().single();
    return serialize(request, groups, groupBy, averageUsage(usagePredict(dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now().minusYears(1)),
      dmService.findAssets(user.getSiteId(), user.getHospitalId(), groupBy, LocalDate.now()))));
  }


  /**
   * <p>If the usage is null last year, this machine will not be included into the result
   * If usage is null the year before last year and not null last year, the forecast is the value of last year.
   * If the usage is not null in both last year and the year before, the forecast is last year usage + standard deviation</p>
   *
   * @param theYearBeforeLast sql result the year before last year
   * @param lastYear          sql result last year
   * @return forecast of next year
   */
  private Observable<Tuple5<Integer, Double, String, Integer, Double>> usagePredict(Observable<Tuple5<Integer, Double, String, Integer, Double>> theYearBeforeLast, Observable<Tuple5<Integer, Double, String, Integer, Double>> lastYear) {
    log.info("got sql result: {}", theYearBeforeLast.count().toBlocking().single());
    return
      Observable.zip(theYearBeforeLast, lastYear, (lstSnd, lstFst) ->
        Tuple.of(lstSnd._1, lstSnd._2, lstSnd._3, lstSnd._4,
          lstSnd._5.equals(0D) ? lstFst._5 : lstFst._5 + Stats.of(lstSnd._5, lstFst._5).sampleStandardDeviation())).cache();
  }

  private Observable<Tuple3<Integer, Double, Observable<Tuple4<Integer, Double, String, Double>>>> averageUsage(Observable<Tuple5<Integer, Double, String, Integer, Double>> predictResult) {
    return
      predictResult.groupBy(k -> k._4).map(g -> Tuple.of(g.getKey(), Stats.of(g.map(v -> v._5).toBlocking().toIterable()).mean(),
        g.map(v -> Tuple.of(v._1, v._2, v._3, v._5)).cache()));
  }


  private String calculateSuggestion(Observable<Tuple3<Integer, Double, Observable<Tuple4<Integer, Double, String, Double>>>> usages) {
    log.info("got averageUsage result: {}", usages.count().toBlocking().single());
    return
      usages.map(values -> {
        if (values._2 > 1D) return "建议购买新设备";
        else if (Stats.of(values._3.map(parameters -> parameters._4).toBlocking().toIterable()).max() > 1D)
          return "建议合理安排使用";
        else if (values._2 <= 0.3D) return "建议提高使用率";
        else return "";
      }).toBlocking().single();
  }


  private ResponseEntity<Map<String, Object>> serialize(HttpServletRequest request, Map<Integer, String> groups, String groupBy, Observable<Tuple3<Integer, Double, Observable<Tuple4<Integer, Double, String, Double>>>> items) {
    log.info("got final input items: {}", items.count().toBlocking().single());
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(new ImmutableMap.Builder<String, Object>()
      .put("id", "100")
      .put("name", "All Assets")
      //.put("usage", Stats.meanOf(items.flatMap(t -> t._2).toBlocking().toIterable()))
      .put("items", items.map(v -> new ImmutableMap.Builder<String, Object>()
        .put("id", v._1)
        .put("name", groups.get(v._1))
        .put("usage", "")
        .put("items", v._3.map(sub -> new ImmutableMap.Builder<String, Object>()
          .put("id", sub._1)
          .put("name", sub._3)
          .put("size", sub._2)
          .put("usage", sub._4)
          .build()).cache().toBlocking().toIterable())
        .build()).cache().toBlocking().toIterable())
      .put("link", new ImmutableMap.Builder<String, Object>()
        .put("ref", "self")
        .put("href", String.format("%s?groupby=%s", request.getRequestURL(), groupBy))
        .build())
      .build());
  }
}
