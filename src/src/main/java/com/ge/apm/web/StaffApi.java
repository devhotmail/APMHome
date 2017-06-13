package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.StaffService;
import com.google.common.collect.ImmutableMap;
import javaslang.Tuple;
import javaslang.control.Option;
import org.simpleflatmapper.tuple.Tuple11;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple7;
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
import java.sql.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/staff")
@Validated
public class StaffApi {

  private static final Logger log = LoggerFactory.getLogger(StaffApi.class);

  @Autowired
  private StaffService StaffService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> getStaff(HttpServletRequest request,
                                                      @RequestParam(value = "from", required = true) Date from,
                                                      @RequestParam(value = "to", required = true) Date to,
                                                      @Min(1) @RequestParam(value = "limit", required = false) Integer limit,
                                                      @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {

    UserAccount user = UserContext.getCurrentLoginUser();
    int site_id = user.getSiteId();
    int hospital_id = user.getHospitalId();

    if (Option.of(limit).isEmpty()) limit = Integer.MAX_VALUE;

    if (Option.of(from).isDefined() && Option.of(to).isDefined())
      if ((to.getTime() - from.getTime()) / 1000 <= 94694400)
        return createResponseBody(request, limit, start, calculateHours(from, to),
          mergeQueryData(StaffService.queryForWo(site_id, hospital_id, from, to),
            StaffService.queryForOther(site_id, hospital_id, from, to),
            StaffService.queryForRole()));
      else return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "Invalid search interval"));
    else
      return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "Bad Request"));
  }

  private ResponseEntity<Map<String, Object>> createResponseBody(
    HttpServletRequest request, Integer limit, Integer start, Integer hours,
    Observable<Tuple11<Integer, String, Integer, Double, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> staff_info) {

    return ResponseEntity

      .ok()

      .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))

      .body(new ImmutableMap.Builder<String, Object>()

        .put("pages", new ImmutableMap.Builder<String, Object>()
          .put("total", staff_info.count().toBlocking().single())
          .put("limit", limit)
          .put("start", start)
          .build())

        .put("range", jsonRange(hours, staff_info))

        .put("summary", jsonTotal(staff_info))

        .put("items", jsonAsset(request, limit, start, staff_info))

        .put("link", new ImmutableMap.Builder<String, Object>()
          .put("ref", "self")
          .put("href", getHref(request))
          .build())

        .build());

  }

  public Observable<Tuple11<Integer, String, Integer, Double, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> mergeQueryData(
    Observable<Tuple7<Integer, String, Integer, Double, Integer, Integer, Integer>> queryWo,
    Observable<Tuple4<Integer, Integer, Integer, Integer>> queryOther,
    Observable<Tuple2<Integer, Integer>> staffRole) {

    Map<Integer, Integer> staffs = javaslang.collection.List.ofAll(staffRole.toBlocking().toIterable()).toJavaMap(v -> Tuple.of(v.getElement0(), v.getElement1()));

    return Observable
      .zip(queryWo, queryOther, (t1, t2) ->
        new Tuple11<Integer, String, Integer, Double, Integer, Integer, Integer, Integer, Integer, Integer, Integer>(
          t1.getElement0(), t1.getElement1(), t1.getElement2(), t1.getElement3(), t1.getElement4(), t1.getElement5(), t1.getElement6(),
          t2.getElement1(), t2.getElement2(), t2.getElement3(), t1.getElement2() + t2.getElement1() + t2.getElement2() + t2.getElement3()))
      .filter(v -> staffs.getOrDefault(v.getElement0(), 0).equals(3)) //3 means user
      .cache();
  }


  private ImmutableMap<String, Object> jsonRange(Integer hours,
                                                 Observable<Tuple11<Integer, String, Integer, Double, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> staff_info) {

    return new ImmutableMap.Builder<String, Object>()

      .put("man_hour", hours)

      .put("hour_total", hours * staff_info.count().toBlocking().single())

      .put("score", 5)

      .put("work_order", calculateRange(staff_info))

      .build();
  }


  private int calculateRange(Observable<Tuple11<Integer, String, Integer, Double, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> staff_info) {

    if (staff_info.isEmpty().toBlocking().single())
      return 0;
    else
      return OperatorMinMax.max(staff_info.map(staff -> staff.getElement4())).toBlocking().single();

  }

  private int calculateHours(Date dfrom, Date dto) {

    Calendar from = Calendar.getInstance();
    from.setTime(dfrom);

    Calendar to = Calendar.getInstance();
    to.setTime(dto);

    int weekdays = 0;

    while (!from.after(to)) {
      int day = from.get(Calendar.DAY_OF_WEEK);
      if ((day != Calendar.SATURDAY) && (day != Calendar.SUNDAY))
        weekdays++;
      from.add(Calendar.DATE, 1);
    }

    return weekdays * 8;
  }

  private ImmutableMap<String, Object> jsonTotal(
    Observable<Tuple11<Integer, String, Integer, Double, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> staff_info) {

    return new ImmutableMap.Builder<String, Object>()

      .put("man_hour", calculateTotal(staff_info, 10))

      .put("repair", calculateTotal(staff_info, 2))

      .put("maintenance", calculateTotal(staff_info, 7))

      .put("meter", calculateTotal(staff_info, 9))

      .put("inspection", calculateTotal(staff_info, 8))

      .put("work_order", calculateTotal(staff_info, 4))

      .put("closed", calculateTotal(staff_info, 5))

      .put("open", calculateTotal(staff_info, 6))

      .put("score", calculateTotal(staff_info, 3))

      .build();
  }

  private Iterable<ImmutableMap<String, Object>> jsonAsset(HttpServletRequest request, Integer limit, Integer start,
                                                           Observable<Tuple11<Integer, String, Integer, Double, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> staff_info) {

    return staff_info

      .skip(start)

      .limit(limit)

      .map(staff -> new ImmutableMap.Builder<String, Object>()

        .put("owner_id", Option.of(staff.getElement0()).getOrElse(0))

        .put("owner_name", Option.of(staff.getElement1()).getOrElse("N/A"))

        .put("man_hour", Option.of(staff.getElement10()).getOrElse(0))

        .put("repair", Option.of(staff.getElement2()).getOrElse(0))

        .put("maintenance", Option.of(staff.getElement7()).getOrElse(0))

        .put("meter", Option.of(staff.getElement9()).getOrElse(0))

        .put("inspection", Option.of(staff.getElement8()).getOrElse(0))

        .put("work_order", Option.of(staff.getElement4()).getOrElse(0))

        .put("closed", Option.of(staff.getElement5()).getOrElse(0))

        .put("open", Option.of(staff.getElement6()).getOrElse(0))

        .put("score", Option.of(staff.getElement3()).getOrElse(0.0))

        .build())

      .toBlocking()

      .toIterable();

  }


  private double calculateTotal(
    Observable<Tuple11<Integer, String, Integer, Double, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> staff_info,
    Integer key) {

    if (staff_info.isEmpty().toBlocking().single())
      return 0.0;

    switch (key) {

      case 2:
        return OperatorSum.sumIntegers(staff_info.map(staff -> staff.getElement2())).toBlocking().single().doubleValue();

      case 3:
        return OperatorSum.sumDoubles(staff_info.map(staff -> staff.getElement3())).toBlocking().single().doubleValue();

      case 4:
        return OperatorSum.sumIntegers(staff_info.map(staff -> staff.getElement4())).toBlocking().single().doubleValue();

      case 5:
        return OperatorSum.sumIntegers(staff_info.map(staff -> staff.getElement5())).toBlocking().single();

      case 6:
        return OperatorSum.sumIntegers(staff_info.map(staff -> staff.getElement6())).toBlocking().single().doubleValue();

      case 7:
        return OperatorSum.sumIntegers(staff_info.map(staff -> staff.getElement7())).toBlocking().single().doubleValue();

      case 8:
        return OperatorSum.sumIntegers(staff_info.map(staff -> staff.getElement8())).toBlocking().single().doubleValue();

      case 9:
        return OperatorSum.sumIntegers(staff_info.map(staff -> staff.getElement9())).toBlocking().single().doubleValue();

      default:
        return OperatorSum.sumIntegers(staff_info.map(staff -> staff.getElement10())).toBlocking().single().doubleValue();

    }
  }

  private String getHref(HttpServletRequest request) {

    if (request.getQueryString() == null)
      return String.format("%s", request.getRequestURL().toString());
    else
      return String.format("%s?%s", request.getRequestURL().toString(), request.getQueryString());
  }

}

