package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.HealthService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javaslang.control.Option;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuple6;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/health")
@Validated
public class HealthApi {
  private static final Logger log = LoggerFactory.getLogger(HealthApi.class);

  @Autowired
  private HealthService healthService;

  private static final String redirect_1 = "/portal/wo/woList.xhtml?isClosed=false";
  private static final String redirect_2 = "/portal/asset/info/List.xhtml?assetStatus=2";
  private static final String redirect_3 = "/portal/asset/info/List.xhtml?model=1";
  private static final String redirect_4 = "/portal/pm/List.xhtml?model=1";
  private static final String redirect_5 = "/portal/insp/MetrologyOrderList.xhtml?model=1";
  private static final String redirect_6 = "/portal/insp/InspOrderList.xhtml?model=1";
  private static final String categorized = "?category=";


  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> getAssetHealth(HttpServletRequest request,
                                                            @Min(1) @Max(6) @RequestParam(value = "category", required = false) Integer category,
                                                            @Min(0) @RequestParam(value = "dept", required = false) Integer dept,
                                                            @Min(1) @RequestParam(value = "limit", required = false) Integer limit,
                                                            @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {

    UserAccount user = UserContext.getCurrentLoginUser();
    int site_id = user.getSiteId();
    int hospital_id = user.getHospitalId();

    if (Option.of(category).isEmpty())
      category = 0;
    if (Option.of(limit).isEmpty()) limit = Integer.MAX_VALUE;

    if (Option.of(dept).isEmpty())

    switch (category) {

      case 0:
        return createResponseBody(category, request,
          healthService.queryForMaintain(site_id, hospital_id),
          healthService.queryForOutage(site_id, hospital_id),
          healthService.queryForWarranty(site_id, hospital_id),
          healthService.queryForPm(site_id, hospital_id),
          healthService.queryForMeterqa(site_id, hospital_id, 2),
          healthService.queryForMeterqa(site_id, hospital_id, 1));
      case 1:
        return createResponseBody(category, request, healthService.queryForMaintain(site_id, hospital_id), start, limit);

      case 2:
        return createResponseBody(category, healthService.queryForOutage(site_id, hospital_id), request, start, limit);

      case 3:
        return createResponseBody(healthService.queryForWarranty(site_id, hospital_id), category, request, start, limit);

      case 4:
        return createResponseBody(healthService.queryForPm(site_id, hospital_id), category, request, start, limit);

      case 5:
        return createResponseBody(healthService.queryForMeterqa(site_id, hospital_id, 2), category, request, start, limit);

      case 6:
        return createResponseBody(healthService.queryForMeterqa(site_id, hospital_id, 1), category, request, start, limit);

      default:
        return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "Bad Request"));

    }

    else

    switch (category) {

      case 0:
        return createResponseBody(category, request,
          healthService.queryForMaintain(site_id, hospital_id, dept),
          healthService.queryForOutage(site_id, hospital_id, dept),
          healthService.queryForWarranty(site_id, hospital_id, dept),
          healthService.queryForPm(site_id, hospital_id, dept),
          healthService.queryForMeterqa(site_id, hospital_id, 2, dept),
          healthService.queryForMeterqa(site_id, hospital_id, 1, dept));
      case 1:
        return createResponseBody(category, request, healthService.queryForMaintain(site_id, hospital_id, dept), start, limit);

      case 2:
        return createResponseBody(category, healthService.queryForOutage(site_id, hospital_id, dept), request, start, limit);

      case 3:
        return createResponseBody(healthService.queryForWarranty(site_id, hospital_id, dept), category, request, start, limit);

      case 4:
        return createResponseBody(healthService.queryForPm(site_id, hospital_id, dept), category, request, start, limit);

      case 5:
        return createResponseBody(healthService.queryForMeterqa(site_id, hospital_id, 2, dept), category, request, start, limit);

      case 6:
        return createResponseBody(healthService.queryForMeterqa(site_id, hospital_id, 1, dept), category, request, start, limit);

      default:
        return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "Bad Request"));

    }

  }

  private String getHref(Integer category, HttpServletRequest request, Integer count) {

	  if (count==0)
		  return "";

	  String baseurl = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("/api/health"));

	  switch (category) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
    	  if (request.getQueryString()==null)
    		  return String.format("%s", request.getRequestURL().toString());
    	  else
    		  return String.format("%s?%s", request.getRequestURL().toString(), request.getQueryString());

      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
        if (request.getQueryString()==null)
          return String.format("%s?%s%s", request.getRequestURL().toString(), "category=", category-10);
        else
          return String.format("%s?%s%s&%s", request.getRequestURL().toString(), "category=", category-10, request.getQueryString());

      case 20:
        return String.format("%s%s", baseurl, redirect_2);

      case 30:
        return String.format("%s%s", baseurl, redirect_3);

      case 40:
        return String.format("%s%s", baseurl, redirect_4);

      case 50:
        return String.format("%s%s", baseurl, redirect_5);

      case 60:
        return String.format("%s%s", baseurl, redirect_6);

      default:
        return "";

    }

  }

  private String getRef(String node) {

    switch (node) {

      case "root":
        return "self";

      case "branch":
        return "child";

      case "leaf":
        return "redirect";

      default:
        return "";
    }

  }

  private String getDisplay(Integer category) {

    switch (category) {

      case 0:
        return "待处理总设备";

      case 1:
        return "维修中";

      case 2:
        return "停机中";

      case 3:
        return "保修期到期";

      case 4:
        return "预防性维护";

      case 5:
        return "设备计量";

      case 6:
        return "设备巡检";

      case 11:
        return "申请";

      case 12:
        return "审核";

      case 13:
        return "派工";

      case 14:
        return "领工";

      case 15:
        return "维修";

      case 16:
        return "关单";

      default:
        return "";

    }

  }

  private String getUnit() {

    return "台";

  }

  private Integer queryForCount(
    Observable<Tuple6<Integer, String, Integer, String, String, Integer>> asset_health_1,
    Observable<Tuple5<Integer, String, String, Integer, Integer>> asset_health_2,
    Observable<Tuple4<Integer, String, String, Integer>> asset_health_3,
    Observable<Tuple4<Integer, String, String, Integer>> asset_health_4,
    Observable<Tuple4<Integer, String, String, Integer>> asset_health_5,
    Observable<Tuple4<Integer, String, String, Integer>> asset_health_6
  ) {

    return asset_health_1.count().toBlocking().single()
      + asset_health_2.count().toBlocking().single()
      + asset_health_3.count().toBlocking().single()
      + asset_health_4.count().toBlocking().single()
      + asset_health_5.count().toBlocking().single()
      + asset_health_6.count().toBlocking().single();
  }

  private Integer queryForCount(Observable<? extends Tuple2> asset_health) {

    return asset_health.count().toBlocking().single();
  }

  private Integer queryForCount(Integer legend, Observable<Tuple6<Integer, String, Integer, String, String, Integer>> asset_health) {

    return asset_health.filter(t5 -> t5.getElement2() == legend).count().toBlocking().single();

  }

  private ResponseEntity<Map<String, Object>> createResponseBody(Integer category, HttpServletRequest request,
                                                                 Observable<Tuple6<Integer, String, Integer, String, String, Integer>> asset_health_1,
                                                                 Observable<Tuple5<Integer, String, String, Integer, Integer>> asset_health_2,
                                                                 Observable<Tuple4<Integer, String, String, Integer>> asset_health_3,
                                                                 Observable<Tuple4<Integer, String, String, Integer>> asset_health_4,
                                                                 Observable<Tuple4<Integer, String, String, Integer>> asset_health_5,
                                                                 Observable<Tuple4<Integer, String, String, Integer>> asset_health_6) {

    return ResponseEntity.ok()

      .cacheControl(
        CacheControl.maxAge(1, TimeUnit.DAYS))

      .body(new ImmutableMap.Builder<String, Object>()
        .put("pages",
          new ImmutableMap.Builder<String, Object>()
            .put("total", 6)
            .build())

        .put("items", jsonCategories(request, asset_health_1, asset_health_2, asset_health_3, asset_health_4, asset_health_5, asset_health_6))

        .put("root",
          new ImmutableMap.Builder<String, Object>()
            .put("category", category)
            .put("display", getDisplay(category))
            .put("count",
              queryForCount(asset_health_1, asset_health_2, asset_health_3, asset_health_4, asset_health_5, asset_health_6))
            .put("unit", getUnit())
            .put("link",
              new ImmutableMap.Builder<String, Object>()
                .put("ref", getRef("root"))
                .put("href", getHref(category, request, 1))
                .build())
            .build())
        .build());
  }

  private ResponseEntity<Map<String, Object>> createResponseBody(Integer category, HttpServletRequest request,
                                                                 Observable<Tuple6<Integer, String, Integer, String, String, Integer>> asset_health_1, Integer start, Integer limit) {

    return ResponseEntity.ok()

      .cacheControl(
        CacheControl.maxAge(1, TimeUnit.DAYS))

      .body(new ImmutableMap.Builder<String, Object>()

        .put("pages",
          new ImmutableMap.Builder<String, Object>()
            .put("total", queryForCount(asset_health_1))
            .build())

        .put("stages", jsonLegends(asset_health_1))

        .put("items", jsonAssets(category, request, asset_health_1, start, limit))

        .put("root",
          new ImmutableMap.Builder<String, Object>()
            .put("category", category)
            .put("display", getDisplay(category))
            .put("count", queryForCount(asset_health_1))
            .put("unit", getUnit())
            .put("link",
              new ImmutableMap.Builder<String, Object>()
                .put("ref", getRef("root"))
                .put("href", getHref(category, request, 1))
                .build())
            .build())
        .build());
  }

  private ResponseEntity<Map<String, Object>> createResponseBody(Integer category,
                                                                 Observable<Tuple5<Integer, String, String, Integer, Integer>> asset_health_2, HttpServletRequest request, Integer start, Integer limit) {

    return ResponseEntity.ok()

      .cacheControl(
        CacheControl.maxAge(1, TimeUnit.DAYS))

      .body(new ImmutableMap.Builder<String, Object>()

        .put("pages",
          new ImmutableMap.Builder<String, Object>()
            .put("total", queryForCount(asset_health_2))
            .build())

        .put("items", jsonAssets(category, asset_health_2, request, start, limit))

        .put("root",
          new ImmutableMap.Builder<String, Object>()
            .put("category", category)
            .put("display", getDisplay(category))
            .put("count", queryForCount(asset_health_2))
            .put("unit", getUnit())
            .put("link",
              new ImmutableMap.Builder<String, Object>()
                .put("ref", getRef("root"))
                .put("href", getHref(category, request, 1))
                .build())
            .build())
        .build());
  }

  private ResponseEntity<Map<String, Object>> createResponseBody(Observable<Tuple4<Integer, String, String, Integer>> asset_health_3456,
                                                                 Integer category, HttpServletRequest request, Integer start, Integer limit) {

    return ResponseEntity.ok()

      .cacheControl(
        CacheControl.maxAge(1, TimeUnit.DAYS))

      .body(new ImmutableMap.Builder<String, Object>()

        .put("pages",
          new ImmutableMap.Builder<String, Object>()
            .put("total", queryForCount(asset_health_3456))
            .build())

        .put("items", jsonAssets(asset_health_3456, category, request, start, limit))

        .put("root",
          new ImmutableMap.Builder<String, Object>()
            .put("category", category)
            .put("display", getDisplay(category))
            .put("count", queryForCount(asset_health_3456))
            .put("unit", getUnit())
            .put("link",
              new ImmutableMap.Builder<String, Object>()
                .put("ref", getRef("branch"))
                .put("href", getHref(category, request, 1))
                .build())
            .build())
        .build());
  }

  private ImmutableList<ImmutableMap<String, Object>> jsonCategories(
    HttpServletRequest request,
    Observable<Tuple6<Integer, String, Integer, String, String, Integer>> asset_health_1,
    Observable<Tuple5<Integer, String, String, Integer, Integer>> asset_health_2,
    Observable<Tuple4<Integer, String, String, Integer>> asset_health_3,
    Observable<Tuple4<Integer, String, String, Integer>> asset_health_4,
    Observable<Tuple4<Integer, String, String, Integer>> asset_health_5,
    Observable<Tuple4<Integer, String, String, Integer>> asset_health_6) {

    return new ImmutableList.Builder<ImmutableMap<String, Object>>()

      .add(new ImmutableMap.Builder<String, Object>()
        .put("category", 1)
        .put("display", getDisplay(1))
        .put("count", queryForCount(asset_health_1))
        .put("unit", getUnit())
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("branch"))
            .put("href", getHref(11, request, queryForCount(asset_health_1)))
            .build())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("category", 2)
        .put("display", getDisplay(2))
        .put("count", queryForCount(asset_health_2))
        .put("unit", getUnit())
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("branch"))
            .put("href", getHref(12, request, queryForCount(asset_health_2)))
            .build())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("category", 3)
        .put("display", getDisplay(3))
        .put("count", queryForCount(asset_health_3))
        .put("unit", getUnit())
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("branch"))
            .put("href", getHref(13, request, queryForCount(asset_health_3)))
            .build())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("category", 4)
        .put("display", getDisplay(4))
        .put("count", queryForCount(asset_health_4))
        .put("unit", getUnit())
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("branch"))
            .put("href", getHref(14, request, queryForCount(asset_health_4)))
            .build())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("category", 5)
        .put("display", getDisplay(5))
        .put("count", queryForCount(asset_health_5))
        .put("unit", getUnit())
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("branch"))
            .put("href", getHref(15, request, queryForCount(asset_health_5)))
            .build())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("category", 6)
        .put("display", getDisplay(6))
        .put("count", queryForCount(asset_health_6))
        .put("unit", getUnit())
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("branch"))
            .put("href", getHref(16, request, queryForCount(asset_health_6)))
            .build())
        .build())

      .build();
  }


  private ImmutableList<ImmutableMap<String, Object>> jsonLegends(Observable<Tuple6<Integer, String, Integer, String, String, Integer>> asset_health) {

    return new ImmutableList.Builder<ImmutableMap<String, Object>>()

      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", 1)
        .put("display", getDisplay(11))
        .put("count", queryForCount(1, asset_health))
        .put("unit", getUnit())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", 2)
        .put("display", getDisplay(12))
        .put("count", queryForCount(2, asset_health))
        .put("unit", getUnit())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", 3)
        .put("display", getDisplay(13))
        .put("count", queryForCount(3, asset_health))
        .put("unit", getUnit())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", 4)
        .put("display", getDisplay(14))
        .put("count", queryForCount(4, asset_health))
        .put("unit", getUnit())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", 5)
        .put("display", getDisplay(15))
        .put("count", queryForCount(5, asset_health))
        .put("unit", getUnit())
        .build())

      .add(new ImmutableMap.Builder<String, Object>()
        .put("id", 6)
        .put("display", getDisplay(16))
        .put("count", queryForCount(6, asset_health))
        .put("unit", getUnit())
        .build())

      .build();
  }

  private Iterable<ImmutableMap<String, Object>> jsonAssets(Integer category, HttpServletRequest request, Observable<Tuple6<Integer, String, Integer, String, String, Integer>> asset_health_1, Integer start, Integer limit) {

    return asset_health_1
      .skip(start)
      .limit(limit)
      .map(asset -> new ImmutableMap.Builder<String, Object>()
        .put("id",
          Option.of(asset.getElement0()).getOrElse(-1))
        .put("name",
          Option.of(asset.getElement1()).getOrElse("N/A"))
        .put("owner",
          Option.of(asset.getElement4()).getOrElse("N/A"))
        .put("stage",
          Option.of(asset.getElement2()).getOrElse(-1))
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("leaf"))
            .put("href", getHref(category * 10 + asset.getElement2(), request, 1))
            .build())
        .build())
      .toBlocking()
      .toIterable();
  }


  private Iterable<ImmutableMap<String, Object>> jsonAssets(Integer category, Observable<Tuple5<Integer, String, String, Integer, Integer>> asset_health_2, HttpServletRequest request, Integer start, Integer limit) {

    return asset_health_2
      .skip(start)
      .limit(limit)
      .map(asset -> new ImmutableMap.Builder<String, Object>()
        .put("id",
          Option.of(asset.getElement0()).getOrElse(0))
        .put("name",
          Option.of(asset.getElement1()).getOrElse("N/A"))
        .put("date",
          Option.of(asset.getElement2()).getOrElse("N/A"))
        .put("desc",
          Option.of(WebUtil.getFieldValueMessage("caseType", asset.getElement3().toString())).getOrElse("N/A"))
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("leaf"))
            .put("href", getHref(category * 10, request, 1))
            .build())
        .build())
      .toBlocking()
      .toIterable();
  }


  private Iterable<ImmutableMap<String, Object>> jsonAssets(Observable<Tuple4<Integer, String, String, Integer>> asset_health_3, Integer category, HttpServletRequest request, Integer start, Integer limit) {

    return asset_health_3
      .skip(start)
      .limit(limit)
      .map(asset -> new ImmutableMap.Builder<String, Object>()
        .put("id",
          Option.of(asset.getElement0()).getOrElse(0))
        .put("name",
          Option.of(asset.getElement1()).getOrElse("N/A"))
        .put("date",
          Option.of(asset.getElement2()).getOrElse(""))
        .put("link",
          new ImmutableMap.Builder<String, Object>()
            .put("ref", getRef("leaf"))
            .put("href", getHref(category * 10, request, 1))
            .build())
        .build())
      .toBlocking()
      .toIterable();
  }
}
