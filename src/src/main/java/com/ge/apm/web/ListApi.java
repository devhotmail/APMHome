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
import rx.math.operators.OperatorMinMax;
import rx.math.operators.OperatorSum;
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
  private static final Map<String, String> env = System.getenv();

  @Autowired
  private ListService ListService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> getList(HttpServletRequest request,
      @RequestParam(value = "from", required = true) Date from,
      @RequestParam(value = "to", required = true) Date to,
      @Pattern(regexp = "rating|exam|usage|rate|fix") @RequestParam(value = "orderby", required = false, defaultValue = "rating") String orderby,
      @Min(0) @RequestParam(value = "dept", required = false) Integer dept,
      @Min(0) @RequestParam(value = "type", required = false) Integer type,
      @Min(1) @RequestParam(value = "limit", required = false ) Integer limit,
      @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {

    UserAccount user = UserContext.getCurrentLoginUser();
    int site_id = user.getSiteId();
    int hospital_id = user.getHospitalId();
    if (Option.of(limit).isEmpty()) limit = Integer.MAX_VALUE;

    if (Option.of(from).isDefined() && Option.of(to).isDefined())
    	if ((to.getTime()-from.getTime())/1000 <= 94694400 )
    		return createResponseBody(
    				request, limit, start,
    				getHref(request),
    				filterQueryData(
    						dept, type, orderby,
    						mergeQueryData(
    								ListService.queryForBasic(site_id, hospital_id, from, to),
    								ListService.queryForOp(site_id, hospital_id, from, to),
    								ListService.queryForBm(site_id, hospital_id, from, to))));
    	else return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "Invalid search interval"));
    else
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "Bad Request"));
  }

  private String getHref(HttpServletRequest request) {
	  if (request.getQueryString()==null)
		  return String.format("%s", request.getRequestURL().toString());
	  else
		  return String.format("%s?%s", request.getRequestURL().toString(), request.getQueryString());
	  }

  private ResponseEntity<Map<String, Object>> createResponseBody(
        HttpServletRequest request, Integer limit, Integer start, String href,
        Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info) {

    return ResponseEntity.ok()
        .cacheControl(
            CacheControl.maxAge(1, TimeUnit.DAYS))
        .body(new ImmutableMap.Builder<String, Object>()
            .put("pages",
                new ImmutableMap.Builder<String, Object>()
                  .put("total", asset_info.count().toBlocking().single() )
                  .put("limit", limit)
                  .put("start", start)
                  .build() )

            .put("ruler", jsonRuler(asset_info) )

            .put("total", jsonTotal(asset_info) )

            .put("items", jsonAsset(request, limit, start, asset_info) )

            .put("link",
                new ImmutableMap.Builder<String, Object>()
                  .put("ref", "self")
                  .put("href", href )
                  .build() )
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
                    t3.getElement0()*t2.getElement8(), t3.getElement1()*t2.getElement8(), t3.getElement2()*t2.getElement8(), t3.getElement3(), t3.getElement4()*t2.getElement8(), t3.getElement5()*t2.getElement8() ) )
              .cache();
  }

  public Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>>
      filterQueryData(
        Integer dept, Integer type, String orderby,
        Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info ) {

    asset_info = dept == null ? asset_info : asset_info.filter(t19 -> t19.getElement3()==dept);

    asset_info = type == null ? asset_info : asset_info.filter(t19 -> t19.getElement2()==type);

    if (orderby.equals("rating"))
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000*l.getElement5() - 100000*r.getElement5()) );
    //else if ( orderby.equals("exposure"))
      //return asset_info.sorted((r, l) -> (int) Math.ceil(100000*l.getElement6() - 100000*r.getElement6()) );
    else if ( orderby.equals("exam"))
      return asset_info.sorted((r, l) -> l.getElement7() - r.getElement7() );
    else if ( orderby.equals("usage"))
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000*l.getElement8() - 100000*r.getElement8()) );
    else if ( orderby.equals("rate"))
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000*l.getElement9() - 100000*r.getElement9()) );
    else if ( orderby.equals("fix"))
      return asset_info.sorted((r, l) ->  r.getElement10() - l.getElement10() );
    else
      return asset_info.sorted((r, l) -> (int) Math.ceil(100000*l.getElement12() - 100000*r.getElement12()) );

  }


  private Double calculateTicks(Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info,
      Integer key){

    if (asset_info.isEmpty().toBlocking().single())
      return 0.0;

    switch(key) {

    case 10:
      return Math.min(
          OperatorMinMax.min( ( Observable<Integer>) asset_info.map(asset -> asset.getElement7())).toBlocking().single() * 1.0,
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement15())).toBlocking().single() );
    case 11:
      return Math.max(
          OperatorMinMax.max( ( Observable<Integer>) asset_info.map(asset -> asset.getElement7())).toBlocking().single() * 1.0,
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement15())).toBlocking().single() );
    case 20:
      return Math.min(
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement6())).toBlocking().single(),
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement14())).toBlocking().single() );
    case 21:
      return Math.max(
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement6())).toBlocking().single(),
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement14())).toBlocking().single() );
    case 30:
      return Math.min(
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement8())).toBlocking().single(),
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement16())).toBlocking().single() );
    case 31:
      return Math.max(
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement8())).toBlocking().single(),
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement16())).toBlocking().single() );
    case 40:
      return Math.min(
          OperatorMinMax.min( ( Observable<Integer>) asset_info.map(asset -> asset.getElement10())).toBlocking().single() * 1.0,
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement18())).toBlocking().single() );
    case 41:
      return Math.max(
          OperatorMinMax.max( ( Observable<Integer>) asset_info.map(asset -> asset.getElement10())).toBlocking().single() * 1.0,
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement18())).toBlocking().single() );
    case 50:
      return Math.min(
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement9())).toBlocking().single(),
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement17())).toBlocking().single() );
    case 51:
      return Math.max(
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement9())).toBlocking().single(),
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement17())).toBlocking().single() );
    case 60:
      return Math.min(
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement12())).toBlocking().single(),
          OperatorMinMax.min( ( Observable<Double>) asset_info.map(asset -> asset.getElement19())).toBlocking().single() );
    case 61:
      return Math.max(
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement12())).toBlocking().single(),
          OperatorMinMax.max( ( Observable<Double>) asset_info.map(asset -> asset.getElement19())).toBlocking().single() );
    default:
      return 0.0;
    }


  }

  private ImmutableMap<String, Object> jsonRuler(
      Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info) {

    return new ImmutableMap.Builder<String, Object>()
      .put("rating", new ImmutableMap.Builder<String, Object>()
        .put("text", "设备综合排名")
        .build())

      .put("exam",
          new ImmutableMap.Builder<String, Object>()
              .put("unit", "次")
              .put("text", "检查量")
              .put("ticks",
                new ImmutableList.Builder<Double>()
                  .add(calculateTicks(asset_info, 10) )
                  .add(calculateTicks(asset_info, 11) )
                  .build())
              .build())

      /*.put("exposure",
          new ImmutableMap.Builder<String, Object>()
              .put("unit", "次")
              .put("text", "曝光量")
              .put("ticks",
                new ImmutableList.Builder<Double>()
                  .add(calculateTicks(asset_info, 20) )
                  .add(calculateTicks(asset_info, 21) )
                  .build())
              .build())*/

      .put("usage",
          new ImmutableMap.Builder<String, Object>()
              .put("unit", "小时")
              .put("text", "使用时间")
              .put("ticks",
                new ImmutableList.Builder<Double>()
                  .add(calculateTicks(asset_info, 30) )
                  .add(calculateTicks(asset_info, 31) )
                  .build())
              .build())

      .put("fix",
          new ImmutableMap.Builder<String, Object>()
              .put("unit", "次")
              .put("text", "维修次数")
              .put("ticks",
                new ImmutableList.Builder<Double>()
                  .add(calculateTicks(asset_info, 40) )
                  .add(calculateTicks(asset_info, 41) )
                  .build())
              .build())

      .put("rate",
          new ImmutableMap.Builder<String, Object>()
              .put("unit", "%")
              .put("text", "开机率")
              .put("ticks",
                new ImmutableList.Builder<Double>()
                  .add(calculateTicks(asset_info, 50) )
                  .add(calculateTicks(asset_info, 51) )
                  .build())
              .build())

      /*
      .put("profit",
          new ImmutableMap.Builder<String, Object>()
              .put("unit", "元" )
              .put("text", "利润")
              .put("ticks",
                new ImmutableList.Builder<Double>()
                  .add(calculateTicks(asset_info, 60) )
                  .add(calculateTicks(asset_info, 61) )
                  .build())
              .build())*/

      .build();
  }


  private ImmutableMap<String, Object> jsonTotal(
        Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info) {

    return new ImmutableMap.Builder<String, Object>()

        .put("exam", calculateTotal(asset_info, 1) )

        //.put("exposure", calculateTotal(asset_info, 2) )

        .put("usage", calculateTotal(asset_info, 3) )

        .put("fix", calculateTotal(asset_info, 4) )

        .put("rate", calculateTotal(asset_info, 5) )

        /*
        .put("profit", calculateTotal(asset_info, 6) )*/

        .build();
  }

  private Double calculateTotal(
        Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info,
        Integer key) {

	if (asset_info.isEmpty().toBlocking().single())
		return 0.0;

    switch(key) {

    case 1:
      return OperatorSum.sumIntegers(asset_info.map(asset -> asset.getElement7()) ).toBlocking().single()*1.0;
    case 2:
      return OperatorSum.sumDoubles(asset_info.map(asset -> asset.getElement6()) ).toBlocking().single();
    case 3:
      return OperatorSum.sumDoubles(asset_info.map(asset -> asset.getElement8()) ).toBlocking().single();
    case 4:
      return OperatorSum.sumIntegers(asset_info.map(asset -> asset.getElement10()) ).toBlocking().single()*1.0;
    case 5:
      return OperatorSum.sumDoubles(asset_info.map(asset -> asset.getElement9()) ).toBlocking().single()/asset_info.count().toBlocking().single();
    default:
      return OperatorSum.sumDoubles(asset_info.map(asset -> asset.getElement12()) ).toBlocking().single();

    }

  }


  private Iterable<ImmutableMap<String, Object>> jsonAsset(HttpServletRequest request, Integer limit, Integer start,
      Observable<Tuple20<Integer, String, Integer, Integer, String, Double, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Double, Double, Double, Double, Double>> asset_info) {

    return asset_info
        .skip(start)
        .limit(limit)
        .map(asset -> new ImmutableMap.Builder<String, Object>()
            .put("id",
                Option.of(asset.getElement0()).getOrElse(0) )
            .put("name",
                Option.of(asset.getElement1()).getOrElse("N/A") )
            .put("type",
                Option.of(asset.getElement2()).getOrElse(0) )
            .put("dept",
                Option.of(asset.getElement4()).getOrElse("N/A") )
            .put("rating",
                Option.of(asset.getElement5()).getOrElse(-1.0) )
            /*.put("exposure",
                Option.of(asset.getElement6()).getOrElse(0.0) )*/
            .put("exam",
                Option.of(asset.getElement7()).getOrElse(0) )
            .put("usage",
                Option.of(asset.getElement8()).getOrElse(0.0) )
            .put("rate",
                Option.of(asset.getElement9()).getOrElse(0.0) )
            .put("fix",
                Option.of(asset.getElement10()).getOrElse(0) )
            /*
            .put("revenue",
                Option.of(asset.getElement11()).getOrElse(0.0) )
            .put("profit",
                Option.of(asset.getElement12()).getOrElse(0.0) )
            .put("exposure_bm",
                Option.of(asset.getElement14()).getOrElse(0.0) )*/
            .put("exam_bm",
                Option.of(asset.getElement15()).getOrElse(0.0) )
            .put("usage_bm",
                Option.of(asset.getElement16()).getOrElse(0.0) )
            .put("rate_bm",
                Option.of(asset.getElement17()).getOrElse(0.0) )
            .put("fix_bm",
                Option.of(asset.getElement18()).getOrElse(0.0) )
            /*
            .put("profit_bm",
                Option.of(asset.getElement19()).getOrElse(0.0) )*/
            .build() )
            .toBlocking()
            .toIterable();

  }

}

