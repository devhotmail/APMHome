package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.ListService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javaslang.control.Option;
import org.simpleflatmapper.tuple.*;
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

@SuppressWarnings("unused")
@RestController
@RequestMapping("/list")
@Validated
public class ListApi {
  private static final Logger log = LoggerFactory.getLogger(ListApi.class);

  @Autowired
  private ListService ListService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> getList(HttpServletRequest request,
                                                     @RequestParam(value = "from", required = true) Date from,
                                                     @RequestParam(value = "to", required = true) Date to,
                                                     @Pattern(regexp = "rating|scan|exposure|usage|fix|stop|profit") @RequestParam(value = "orderby", required = false, defaultValue = "rating") String orderby,
                                                     @Min(0) @RequestParam(value = "dept", required = false) Integer dept,
                                                     @Min(0) @RequestParam(value = "type", required = false) Integer type,
                                                     @Min(1) @RequestParam(value = "limit", required = false) Integer limit,
                                                     @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {

    UserAccount user = UserContext.getCurrentLoginUser();
    int site_id = user.getSiteId();
    int hospital_id = user.getHospitalId();
    if (Option.of(limit).isEmpty()) limit = Integer.MAX_VALUE;

    if (Option.of(from).isDefined() && Option.of(to).isDefined())
      return createResponseBody(
        request, limit, start,
        getHref(request.getRequestURL().toString(), from, to, dept, type, orderby, limit, start),
        filterQueryData(
          dept, type, orderby,
          mergeQueryData(
            ListService.queryForBasic(site_id, hospital_id, from, to),
            ListService.queryForOp(site_id, hospital_id, from, to),
            ListService.queryForBm(site_id, hospital_id, from, to))),
        queryForTick(
          ListService.queryForMinMax(site_id, hospital_id, dept, type, from, to),
          ListService.queryForBmTick(site_id, hospital_id, dept, type, from, to),
          to.compareTo(from)));
    else
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "Bad Request"));
  }

  private String getHref(String url, Date from, Date to, Integer dept, Integer type, String orderby, Integer limit, Integer start) {
    return String.format("%s?from=%s&to=%s&dept=%s&type=%s&orderby=%s&limit=%s&start=%s", url, from, to, dept, type, orderby, limit, start);
  }

  private ResponseEntity<Map<String, Object>> createResponseBody(
    HttpServletRequest request, Integer limit, Integer start, String href,
    Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info,
    Tuple12<Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double> ticks
  ) {

    return ResponseEntity.ok()
      .cacheControl(
        CacheControl.maxAge(1, TimeUnit.DAYS))
      .body(new ImmutableMap.Builder<String, Object>()
        .put("pages",
          new ImmutableMap.Builder<String, Object>()
            .put("total", asset_info.count().toBlocking().single())
            .put("limit", limit)
            .put("start", start)
            .build())

        .put("ruler", jsonRuler(ticks))

        .put("items", jsonAsset(request, limit, start, asset_info))

        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", "self")
            .put("href", href)
            .build())
        .build());

  }

  public Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>>
  mergeQueryData(
    Observable<Tuple5<Integer, String, Integer, Integer, String>> queryBasic,
    Observable<Tuple9<Double, Double, Integer, Double, Double, Integer, Double, Double, Integer>> queryOp,
    Observable<Tuple6<Double, Double, Double, Double, Double, Double>> queryBm) {

    return Observable.zip(queryBasic, queryOp, queryBm,
      (t1, t2, t3) ->
        new Tuple20<Integer, String, Integer, Integer, String,
          Double, Double, Integer, Double, Double, Integer, Double, Double, Integer,
          Double, Double, Double, Double, Double, Double>(
          t1.getElement0(), t1.getElement1(), t1.getElement2(), t1.getElement3(), t1.getElement4(),
          t2.getElement0(), t2.getElement1(), t2.getElement2(), t2.getElement3(), t2.getElement4(), t2.getElement5(), t2.getElement6(), t2.getElement7(), t2.getElement8(),
          t3.getElement0() * t2.getElement8(), t3.getElement1() * t2.getElement8(), t3.getElement2() * t2.getElement8(), t3.getElement3(), t3.getElement4() * t2.getElement8(), t3.getElement5() * t2.getElement8()))
      .cache();
  }

  public Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>>
  filterQueryData(
    Integer dept, Integer type, String orderby,
    Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info) {

    asset_info = dept == null ? asset_info : asset_info.filter(t19 -> t19.getElement3() == dept);

    asset_info = type == null ? asset_info : asset_info.filter(t19 -> t19.getElement2() == type);

    if (orderby.equals("rating"))
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000 * l.getElement5() - 100000 * r.getElement5()));
    else if (orderby.equals("exposure"))
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000 * l.getElement6() - 100000 * r.getElement6()));
    else if (orderby.equals("scan"))
      return asset_info.sorted((r, l) -> l.getElement7() - r.getElement7());
    else if (orderby.equals("usage"))
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000 * l.getElement8() - 100000 * r.getElement8()));
    else if (orderby.equals("stop"))
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000 * l.getElement9() - 100000 * r.getElement9()));
    else if (orderby.equals("fix"))
      return asset_info.sorted((r, l) -> l.getElement10() - r.getElement10());
    else
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000 * l.getElement12() - 100000 * r.getElement12()));

  }

  public Tuple12<Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double> queryForTick(
    Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double> tick1,
    Tuple12<Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double> tick2,
    Integer day
  ) {

    return new Tuple12<Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double>(

      Math.min(tick1.getElement0(), tick2.getElement0() * day), Math.max(tick1.getElement1(), tick2.getElement1() * day),
      Math.min(tick1.getElement2(), tick2.getElement2() * day), Math.max(tick1.getElement3(), tick2.getElement3() * day),
      Math.min(tick1.getElement4(), tick2.getElement4() * day), Math.max(tick1.getElement5(), tick2.getElement5() * day),
      Math.min(tick1.getElement6(), tick2.getElement6()), Math.max(tick1.getElement7(), tick2.getElement7()),
      Math.min(tick1.getElement8(), tick2.getElement8() * day), Math.max(tick1.getElement9(), tick2.getElement9() * day),
      Math.min(tick1.getElement10(), tick2.getElement10() * day), Math.max(tick1.getElement11(), tick2.getElement11() * day));
  }


  private ImmutableMap<String, Object> jsonRuler(
    Tuple12<Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double> ticks) {

    return new ImmutableMap.Builder<String, Object>()
      .put("rating", new ImmutableMap.Builder<String, Object>()
        .put("text", "设备综合排名")
        .build())

      .put("scan",
        new ImmutableMap.Builder<String, Object>()
          .put("unit", "次")
          .put("text", "扫描量")
          .put("ticks",
            new ImmutableList.Builder<Double>()
              .add(Option.of(ticks.getElement2()).getOrElse(0.0))
              .add(Option.of(ticks.getElement3()).getOrElse(0.0))
              .build())
          .build())

      .put("exposure",
        new ImmutableMap.Builder<String, Object>()
          .put("unit", "次")
          .put("text", "曝光量")
          .put("ticks",
            new ImmutableList.Builder<Double>()
              .add(Option.of(ticks.getElement0()).getOrElse(0.0))
              .add(Option.of(ticks.getElement1()).getOrElse(0.0))
              .build())
          .build())

      .put("usage",
        new ImmutableMap.Builder<String, Object>()
          .put("unit", "小时")
          .put("text", "使用时间")
          .put("ticks",
            new ImmutableList.Builder<Double>()
              .add(Option.of(ticks.getElement4()).getOrElse(0.0))
              .add(Option.of(ticks.getElement5()).getOrElse(0.0))
              .build())
          .build())

      .put("fix",
        new ImmutableMap.Builder<String, Object>().put("unit", "次")
          .put("text", "维修次数")
          .put("ticks",
            new ImmutableList.Builder<Double>()
              .add(Option.of(ticks.getElement8()).getOrElse(0.0))
              .add(Option.of(ticks.getElement9()).getOrElse(0.0))
              .build())
          .build())

      .put("stop",
        new ImmutableMap.Builder<String, Object>()
          .put("unit", "%")
          .put("text", "停机率")
          .put("ticks",
            new ImmutableList.Builder<Double>()
              .add(Option.of(ticks.getElement6()).getOrElse(0.0))
              .add(Option.of(ticks.getElement7()).getOrElse(0.0))
              .build())
          .build())

      .put("profit",
        new ImmutableMap.Builder<String, Object>()
          .put("unit", "元")
          .put("text", "利润")
          .put("ticks",
            new ImmutableList.Builder<Double>()
              .add(Option.of(ticks.getElement10()).getOrElse(0.0))
              .add(Option.of(ticks.getElement11()).getOrElse(0.0))
              .build())
          .build())

      .build();
  }

  private Iterable<ImmutableMap<String, Object>> jsonAsset(HttpServletRequest request, Integer limit, Integer start,
                                                           Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info) {

    return asset_info
      .skip(start)
      .limit(limit)
      .map(asset -> new ImmutableMap.Builder<String, Object>()
        .put("id",
          Option.of(asset.getElement0()).getOrElse(0))
        .put("name",
          Option.of(asset.getElement1()).getOrElse("N/A"))
        .put("type",
          Option.of(asset.getElement2()).getOrElse(0))
        .put("dept",
          Option.of(asset.getElement4()).getOrElse("N/A"))
        .put("rating",
          Option.of(asset.getElement5()).getOrElse(-1.0))
        .put("exposure",
          Option.of(asset.getElement6()).getOrElse(0.0))
        .put("scan",
          Option.of(asset.getElement7()).getOrElse(0))
        .put("usage",
          Option.of(asset.getElement8()).getOrElse(0.0))
        .put("stop",
          Option.of(asset.getElement9()).getOrElse(0.0))
        .put("fix",
          Option.of(asset.getElement10()).getOrElse(0))
        .put("revenue",
          Option.of(asset.getElement11()).getOrElse(0.0))
        .put("profit",
          Option.of(asset.getElement12()).getOrElse(0.0))
        .put("exposure_bm",
          Option.of(asset.getElement14()).getOrElse(0.0))
        .put("scan_bm",
          Option.of(asset.getElement15()).getOrElse(0.0))
        .put("usage_bm",
          Option.of(asset.getElement16()).getOrElse(0.0))
        .put("stop_bm",
          Option.of(asset.getElement17()).getOrElse(0.0))
        .put("fix_bm",
          Option.of(asset.getElement18()).getOrElse(0.0))
        .put("profit_bm",
          Option.of(asset.getElement19()).getOrElse(0.0))
        .build())
      .toBlocking()
      .toIterable();

  }
}