package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.ListService;
import com.ge.apm.service.api.ProfitService;
import com.ge.apm.service.utils.CNY;
import com.ge.apm.view.sysutil.UserContextService;
import com.github.davidmoten.rx.jdbc.tuple.Tuple5;
import com.github.davidmoten.rx.jdbc.tuple.Tuple7;
import com.github.davidmoten.rx.jdbc.tuple.Tuple6;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.github.davidmoten.rx.jdbc.tuple.Tuple4;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.collect.ImmutableList;

import javaslang.control.Option;
import org.simpleflatmapper.tuple.Tuple18;
import org.simpleflatmapper.tuple.Tuple19;
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
import javax.validation.constraints.Max;
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
			@Pattern(regexp = "id|scan|exposure|usage|fix|stop|profit") @RequestParam(value = "orderby", required = false, defaultValue = "id") String orderby,
			@Min(0) @RequestParam(value = "dept", required = false) Integer dept,
			@Min(0) @RequestParam(value = "type", required = false) Integer type,
			@Min(1) @RequestParam(value = "limit", required = false ) Integer limit,
			@Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {

		UserAccount user = UserContext.getCurrentLoginUser();

		if (Option.of(dept).isEmpty())	dept = Integer.MIN_VALUE;
		if (Option.of(type).isEmpty())	type = Integer.MIN_VALUE;
		if (Option.of(limit).isEmpty())	limit = Integer.MAX_VALUE;

		if (Option.of(from).isDefined() && Option.of(to).isDefined())
			return createResponseBody(request, from, to, orderby, user.getSiteId(), user.getHospitalId(), dept, type, limit, start);
		else
			return ResponseEntity.badRequest().body(ImmutableMap.of("msg", "Parameter unmatched"));
	}

	private ResponseEntity<Map<String, Object>> createResponseBody(HttpServletRequest request, Date from, Date to,
			String orderby, int site_id, int hospital_id, int dept, int type, int limit, int start) {

		return ResponseEntity.ok()
				.cacheControl(
						CacheControl.maxAge(1, TimeUnit.HOURS))
				.body(new ImmutableMap.Builder<String, Object>().put("pages",
						new ImmutableMap.Builder<String, Object>()
								.put("total", ListService.queryForCount(site_id, hospital_id, from, to))
								.put("limit", limit).put("start", start).build())

						.put("ruler", jsonRuler(
								ListService.queryForTickMin(site_id, hospital_id, dept, type, from, to),
								ListService.queryForTickMax(site_id, hospital_id, dept, type, from, to)))

						.put("items",
								jsonAsset(request, limit, start,
										queryMergeData(
												dept, type, orderby,
												ListService.queryForBasic(site_id, hospital_id, from, to),
												ListService.queryForOp(site_id, hospital_id, from, to),
												ListService.queryForRoi(site_id, hospital_id, from, to),
												ListService.queryForBm(site_id, hospital_id, from, to))))

						.build());

	}

	public Observable<Tuple19<Integer, String, Integer, Integer, String, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Integer, Double, Double, Integer, Double>>
			queryMergeData(
					Integer dept, Integer type, String orderby,
					Observable<Tuple5<Integer, String, Integer, Integer, String>> queryBasic,
					Observable<Tuple6<Integer, Double, Integer, Double, Integer, Integer>> queryOp,
					Observable<Tuple4<Integer, Double, Double, Integer>> queryRoi,
					Observable<Tuple7<Integer, Double, Integer, Double, Double, Integer, Double>> queryBm) {

		Observable<Tuple19<Integer, String, Integer, Integer, String,
							Double, Integer, Double, Double, Integer,
							Double, Double, Integer,
							Double, Integer, Double, Double, Integer, Double>> result =
				Observable.zip(queryBasic, queryOp, queryRoi, queryBm,
					(t1, t2, t3, t4) ->
							new Tuple19<Integer, String, Integer, Integer, String,
										Double, Integer, Double, Double, Integer,
										Double, Double, Integer,
										Double, Integer, Double, Double, Integer, Double>(
										t1._1(), t1._2(), t1._3(), t1._4(), t1._5(),
										t2._2(), t2._3(), t2._4(), 1.0*t2._5()/t3._4()/24/36, t2._6(),
										t3._2(), t3._3(), t3._4(),
										t4._2()*t3._4(), t4._3()*t3._4(), t4._4()*t3._4(), t4._5()*t3._4(), t4._6()*t3._4(), t4._7()*t3._4() ) );

		result = dept == Integer.MIN_VALUE ? result : result.filter(t19 -> t19.getElement3()==dept);

		result = type == Integer.MIN_VALUE ? result : result.filter(t19 -> t19.getElement2()==type);

		if (orderby.equals("id"))
			return result;
		else if ( orderby.equals("scan"))
			return result.sorted((l, r) -> l.getElement6() - r.getElement6() );
		else if ( orderby.equals("exposure"))
			return result.sorted((l, r) -> (int) Math.ceil(100000*l.getElement5() - 100000*r.getElement5()) );
		else if ( orderby.equals("usage"))
			return result.sorted((l, r) -> (int) Math.ceil(100000*l.getElement7() - 100000*r.getElement7()) );
		else if ( orderby.equals("fix"))
			return result.sorted((l, r) -> l.getElement9() - r.getElement9() );
		else if ( orderby.equals("stop"))
			return result.sorted((l, r) -> (int) Math.ceil(100000*l.getElement8() - 100000*r.getElement8()) );
		else
			return result.sorted((l, r) -> (int) Math.ceil(100000*l.getElement11() - 100000*r.getElement11()) );

	}

	private ImmutableMap<String, Object> jsonRuler(
			Tuple6<Double, Integer, Double, Integer, Integer, Double> tickMin,
			Tuple6<Double, Integer, Double, Integer, Integer, Double> tickMax) {

		return new ImmutableMap.Builder<String, Object>()
				.put("rating", new ImmutableMap.Builder<String, Object>()
				.put("text", "设备综合排名")
				.build())

				.put("scan",
						new ImmutableMap.Builder<String, Object>()
								.put("unit", "次")
								.put("text", "扫描量")
								.put("ticks",
								new ImmutableList.Builder<Integer>()
									.add(Option.of(tickMin._2()).getOrElse(0) )
									.add(Option.of(tickMax._2()).getOrElse(0) )
									.build())
								.build())

				.put("exposure",
						new ImmutableMap.Builder<String, Object>()
								.put("unit", "次")
								.put("text", "曝光量")
								.put("ticks",
										new ImmutableList.Builder<Double>()
											.add(Option.of(tickMin._1()).getOrElse(0.0) )
											.add(Option.of(tickMax._1()).getOrElse(0.0) )
											.build())
								.build())

				.put("usage",
						new ImmutableMap.Builder<String, Object>()
								.put("unit", "小时")
								.put("text", "使用时间")
								.put("ticks",
										new ImmutableList.Builder<Double>()
											.add(Option.of(tickMin._3()).getOrElse(0.0) )
											.add(Option.of(tickMax._3()).getOrElse(0.0) )
											.build())
								.build())

				.put("fix",
						new ImmutableMap.Builder<String, Object>().put("unit", "次")
								.put("text", "维修次数")
								.put("ticks",
										new ImmutableList.Builder<Integer>()
											.add(Option.of(tickMin._5()).getOrElse(0) )
											.add(Option.of(tickMax._5()).getOrElse(0) )
											.build())
								.build())

				.put("stop",
						new ImmutableMap.Builder<String, Object>()
								.put("unit", "%")
								.put("text", "停机率")
								.put("ticks",
										new ImmutableList.Builder<Double>()
											.add(0.0)
											.add(100.0)
											.build())
								.build())

				.put("profit",
						new ImmutableMap.Builder<String, Object>()
								.put("unit", "元" )
								.put("text", "利润")
								.put("ticks",
										new ImmutableList.Builder<Double>()
											.add(Option.of(tickMin._6()).getOrElse(0.0) )
											.add(Option.of(tickMax._6()).getOrElse(0.0) )
											.build())
								.build())

				.build();
	}

	private Iterable<ImmutableMap<String, Object>> jsonAsset(HttpServletRequest request, Integer limit, Integer start,
			Observable<Tuple19<Integer, String, Integer, Integer, String, Double, Integer, Double, Double, Integer, Double, Double, Integer, Double, Integer, Double, Double, Integer, Double>> asset_info) {

		return asset_info.
				skip(start).
				limit(limit)
				.map(asset -> new ImmutableMap.Builder<String, Object>()
						.put("id",
								Option.of(asset.getElement0()).getOrElse(0) )
						.put("type",
								Option.of(asset.getElement2()).getOrElse(0) )
						.put("dept",
								Option.of(asset.getElement4()).getOrElse("N/A") )
						.put("exposure",
								Option.of(asset.getElement5()).getOrElse(0.0) )
						.put("scan",
								Option.of(asset.getElement6()).getOrElse(0) )
						.put("usage",
								Option.of(asset.getElement7()).getOrElse(0.0) )
						.put("stop",
								Option.of(asset.getElement8()).getOrElse(0.0) )
						.put("fix",
								Option.of(asset.getElement9()).getOrElse(0) )
						.put("revenue",
								Option.of(asset.getElement10()).getOrElse(0.0) )
						.put("profit",
								Option.of(asset.getElement11()).getOrElse(0.0) )
						.put("exposure_bm",
								Option.of(asset.getElement13()).getOrElse(0.0) )
						.put("scan_bm",
								Option.of(asset.getElement14()).getOrElse(0) )
						.put("usage_bm",
								Option.of(asset.getElement15()).getOrElse(0.0) )
						.put("stop_bm",
								Option.of(asset.getElement16()).getOrElse(0.0) )
						.put("fix_bm",
								Option.of(asset.getElement17()).getOrElse(0) )
						.put("profit_bm",
								Option.of(asset.getElement18()).getOrElse(0.0) )
						.build())
						.toBlocking()
						.toIterable();

	}
}