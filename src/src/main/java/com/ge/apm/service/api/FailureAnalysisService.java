package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple4;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

@Service
public class FailureAnalysisService {
  private static final Logger log = LoggerFactory.getLogger(FailureAnalysisService.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  @Cacheable(cacheNames = "springCache", key = "'failureAnalysisService.briefs.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#groupBy+'.'+#dept+'.'+#type+'.'+#supplier+'.'+#asset+'.'+#pmv")
  public Observable<Tuple4<Integer, Double, Double, Integer>> briefs(int site, int hospital, Date from, Date to, String groupBy, Integer dept, Integer type, Integer supplier, Integer asset, Integer pmv) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT(String.format("ai.%s as agg", groupBy), "(1-(sum(extract(epoch from (confirmed_up_time-confirmed_down_time))) / (count(distinct sr.asset_id) * 86400 * (date (:to) - date (:from))))) as up_rate", "sum(case WHEN sr.nearest_sr_days > :pmv THEN 0 ELSE 1 END) as ftfr_num", "count(*) as wo_num");
      FROM("asset_info ai");
      JOIN("v2_service_request sr on ai.site_id = sr.site_id and ai.hospital_id =sr.hospital_id and ai.id=sr.asset_id");
      WHERE("ai.site_id = :site");
      WHERE("ai.hospital_id = :hospital");
      WHERE("sr.request_time between :from and :to");
      WHERE("sr.status=2");
      WHERE("sr.confirmed_down_time IS NOT NULL");
      WHERE("sr.confirmed_up_time IS NOT NULL");
      if (Option.of(type).filter(i -> i > 0).isDefined()) {
        WHERE("ai.asset_group = :type");
      }
      if (Option.of(dept).filter(i -> i > 0).isDefined()) {
        WHERE("ai.clinical_dept_id = :dept");
      }
      if (Option.of(supplier).filter(i -> i > 0).isDefined()) {
        WHERE("ai.supplier_id = :supplier");
      }
      if (Option.of(asset).filter(i -> i > 0).isDefined()) {
        WHERE("ai.id = :asset");
      }
      GROUP_BY("agg");
      ORDER_BY("agg");
    }}.toString()).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(supplier).isDefined()).map(o -> o.parameter("supplier", supplier))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).filter(o -> Option.of(pmv).isDefined()).map(o -> o.parameter("pmv", pmv))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Double.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3().doubleValue() / t._4().doubleValue(), t._4()))
      .cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'failureAnalysisService.reasons.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#dept+'.'+#type+'.'+#supplier+'.'+#asset")
  public Observable<Tuple2<Integer, Integer>> reasons(int site, int hospital, Date from, Date to, Integer dept, Integer type, Integer supplier, Integer asset) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("w.case_type", "count(w.case_type) as reason_count");
      FROM("asset_info a");
      JOIN("v2_work_order w on a.site_id = w.site_id and a.hospital_id = w.hospital_id and a.id = w.asset_id");
      WHERE("a.site_id = :site");
      WHERE("a.hospital_id = :hospital");
      WHERE("w.created_date between :from and :to");
      WHERE("w.case_type is not null");
      if (Option.of(type).filter(i -> i > 0).isDefined()) {
        WHERE("a.asset_group = :type");
      }
      if (Option.of(dept).filter(i -> i > 0).isDefined()) {
        WHERE("a.clinical_dept_id = :dept");
      }
      if (Option.of(supplier).filter(i -> i > 0).isDefined()) {
        WHERE("a.supplier_id = :supplier");
      }
      if (Option.of(asset).filter(i -> i > 0).isDefined()) {
        WHERE("a.id = :asset");
      }
      GROUP_BY("w.case_type");
      ORDER_BY("reason_count desc");

    }}.toString()).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(supplier).isDefined()).map(o -> o.parameter("supplier", supplier))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2()))
      .cache();
  }

}
