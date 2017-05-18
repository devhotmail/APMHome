package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
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

  @Cacheable(cacheNames = "springCache", key = "'failureAnalysisService.briefs.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#groupBy+'.'+#dept+'.'+#type+'.'+#supplier+'.'+#asset")
  public Observable<Tuple3<Integer, Double, Integer>> briefs(int site, int hospital, Date from, Date to, String groupBy, Integer dept, Integer type, Integer supplier, Integer asset) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT(groupBy, "1-avg(down_time)/86400", "sum(work_order_count)");
      FROM("asset_summit");
      WHERE("site_id = :site");
      WHERE("hospital_id = :hospital");
      WHERE("created between :from and :to");
      if (Option.of(type).filter(i -> i > 0).isDefined()) {
        WHERE("asset_group = :type");
      }
      if (Option.of(dept).filter(i -> i > 0).isDefined()) {
        WHERE("dept_id = :dept");
      }
      if (Option.of(supplier).filter(i -> i > 0).isDefined()) {
        WHERE("supplier_id = :supplier");
      }
      if (Option.of(asset).filter(i -> i > 0).isDefined()) {
        WHERE("asset_id = :asset");
      }
      GROUP_BY(groupBy);
      ORDER_BY(groupBy);
    }}.toString()).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(supplier).isDefined()).map(o -> o.parameter("supplier", supplier))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Double.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3()))
      .cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'failureAnalysisService.details.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#groupBy+'.'+#key")
  public Observable<Tuple3<Integer, Double, Integer>> details(int site, int hospital, Date from, Date to, String groupBy, Integer key) {
    return db.select(new SQL() {{
      SELECT(groupBy, "1-avg(down_time)/86400", "sum(work_order_count)");
      FROM("asset_summit");
      WHERE("site_id = :site");
      WHERE("hospital_id = :hospital");
      WHERE("created between :from and :to");
      WHERE(String.format("%s = :key", groupBy));
      GROUP_BY(groupBy);
      ORDER_BY(groupBy);
    }}.toString())
      .parameter("site", site)
      .parameter("hospital", hospital)
      .parameter("from", from)
      .parameter("to", to)
      .parameter("key", key)
      .getAs(Integer.class, Double.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3()))
      .cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'failureAnalysisService.reasons.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#dept+'.'+#type+'.'+#supplier+'.'+#asset")
  public Observable<Tuple2<Integer, Integer>> reasons(int site, int hospital, Date from, Date to, Integer dept, Integer type, Integer supplier, Integer asset) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("w.case_type", "sum(w.case_type) as reason_count");
      FROM("asset_info a");
      JOIN("work_order w on a.site_id = w.site_id and a.hospital_id = w.hospital_id and a.id = w.asset_id");
      WHERE("a.site_id = :site");
      WHERE("a.hospital_id = :hospital");
      WHERE("w.create_time between :from and :to");
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
