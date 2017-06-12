package com.ge.apm.service.api;


import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import javaslang.Tuple;
import javaslang.Tuple3;
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
import java.util.Objects;

@Service
public class ScanService {
  private static final Logger log = LoggerFactory.getLogger(ScanService.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  @Cacheable(cacheNames = "springCache", key = "'ScanService.brief.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#type+'.'+#dept+'.'+#asset")
  public Observable<Tuple3<Integer, Integer, Integer>> brief(int site, int hospital, Date from, Date to, Integer type, Integer dept, Integer asset) {
    Objects.requireNonNull(from);
    Objects.requireNonNull(to);
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("l.asset_group", "l.part_id", "COALESCE(r.exam_num,0)");
      FROM("(".concat(new SQL() {{
        SELECT_DISTINCT("asset_group", "part_id");
        FROM("proc_map");
        if (Option.of(type).filter(i -> i > 0).isDefined()) {
          WHERE("asset_group = :type");
        }
      }}.toString()).concat(") as l"));
      LEFT_OUTER_JOIN("(".concat(new SQL() {{
        SELECT("asset_group", "part_id", "sum(exam_count) as exam_num");
        FROM("exam_summit");
        WHERE("site_id = :site");
        WHERE("hospital_id = :hospital");
        WHERE("created between :from and :to");
        if (Option.of(type).filter(i -> i > 0).isDefined()) {
          WHERE("asset_group = :type");
        }
        if (Option.of(dept).filter(i -> i > 0).isDefined()) {
          WHERE("dept_id = :dept");
        }
        if (Option.of(asset).filter(i -> i > 0).isDefined()) {
          WHERE("asset_id = :asset");
        }
        GROUP_BY("asset_group", "part_id");
      }}.toString()).concat(") as r on l.asset_group = r.asset_group and l.part_id = r.part_id"));
      ORDER_BY("l.asset_group", "l.part_id");
    }}.toString()).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3()))
      .cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'ScanService.assetDetail.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#type+'.'+#dept+'.'+#asset+'.'+#part")
  public Observable<Tuple4<Integer, Integer, Integer, Integer>> assetDetail(int site, int hospital, Date from, Date to, Integer type, Integer dept, Integer asset, Integer part) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("l.asset_group", "l.asset_id", "l.part_id", "COALESCE(r.exam_num,0)");
      FROM("(".concat(new SQL() {{
        SELECT(" ai.id as asset_id", "pm.asset_group", "ai.clinical_dept_id as dept_id", "pm.part_id");
        FROM("asset_info ai");
        JOIN("(select distinct asset_group, part_id from proc_map) as pm on ai.asset_group = pm.asset_group");
        WHERE("ai.site_id = :site");
        WHERE("ai.hospital_id = :hospital");
        if (Option.of(type).filter(i -> i > 0).isDefined()) {
          WHERE("ai.asset_group = :type");
        }
        if (Option.of(dept).filter(i -> i > 0).isDefined()) {
          WHERE("ai.clinical_dept_id = :dept");
        }
        if (Option.of(asset).filter(i -> i > 0).isDefined()) {
          WHERE("ai.id = :asset");
        }
        if (Option.of(part).filter(i -> i > 0).isDefined()) {
          WHERE("pm.part_id = :part");
        }
      }}.toString()).concat(") as l"));
      LEFT_OUTER_JOIN("(".concat(new SQL() {{
        SELECT("asset_group", "asset_id", "part_id", "sum(exam_count) as exam_num");
        FROM("exam_summit");
        WHERE("site_id = :site");
        WHERE("hospital_id = :hospital");
        WHERE("created between :from and :to");
        if (Option.of(type).filter(i -> i > 0).isDefined()) {
          WHERE("asset_group = :type");
        }
        if (Option.of(dept).filter(i -> i > 0).isDefined()) {
          WHERE("dept_id = :dept");
        }
        if (Option.of(asset).filter(i -> i > 0).isDefined()) {
          WHERE("asset_id = :asset");
        }
        if (Option.of(part).filter(i -> i > 0).isDefined()) {
          WHERE("part_id = :part");
        }
        GROUP_BY("asset_group", "asset_id", "part_id");
      }}.toString()).concat(") as r on l.asset_group = r.asset_group and l.asset_id = r.asset_id and l.part_id = r.part_id"));
      ORDER_BY("l.asset_group", "l.part_id");
    }}.toString()).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).filter(o -> Option.of(part).isDefined()).map(o -> o.parameter("part", part))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4()))
      .cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'ScanService.stepDetail.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#type+'.'+#dept+'.'+#asset+'.'+#part+'.'+#step")
  public Observable<Tuple4<Integer, Integer, Integer, Integer>> stepDetail(int site, int hospital, Date from, Date to, Integer type, Integer dept, Integer asset, Integer part, Integer step) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("l.asset_group", "l.part_id", "l.step_id", "COALESCE(r.exam_num,0)");
      FROM("(".concat(new SQL() {{
        SELECT("ai.asset_group", "ai.clinical_dept_id as dept_id", " ai.id as asset_id", "pm.part_id", "pm.step_id");
        FROM("asset_info ai");
        JOIN("(select distinct asset_group, part_id, step_id from proc_map) as pm on ai.asset_group = pm.asset_group");
        if (Option.of(type).filter(i -> i > 0).isDefined()) {
          WHERE("ai.asset_group = :type");
        }
        if (Option.of(dept).filter(i -> i > 0).isDefined()) {
          WHERE("ai.clinical_dept_id = :dept");
        }
        if (Option.of(asset).filter(i -> i > 0).isDefined()) {
          WHERE("ai.id = :asset");
        }
        if (Option.of(part).filter(i -> i > 0).isDefined()) {
          WHERE("pm.part_id = :part");
        }
        if (Option.of(step).filter(i -> i > 0).isDefined()) {
          WHERE("pm.step_id = :step");
        }
      }}.toString()).concat(") as l"));
      LEFT_OUTER_JOIN("(".concat(new SQL() {{
        SELECT("asset_group", "part_id", "step_id", "sum(exam_count) as exam_num");
        FROM("exam_summit");
        WHERE("site_id = :site");
        WHERE("hospital_id = :hospital");
        WHERE("created between :from and :to");
        if (Option.of(type).filter(i -> i > 0).isDefined()) {
          WHERE("asset_group = :type");
        }
        if (Option.of(dept).filter(i -> i > 0).isDefined()) {
          WHERE("dept_id = :dept");
        }
        if (Option.of(asset).filter(i -> i > 0).isDefined()) {
          WHERE("asset_id = :asset");
        }
        if (Option.of(part).filter(i -> i > 0).isDefined()) {
          WHERE("part_id = :part");
        }
        if (Option.of(step).filter(i -> i > 0).isDefined()) {
          WHERE("step_id = :step");
        }
        GROUP_BY("asset_group", "part_id", "step_id");
      }}.toString()).concat(") as r on l.asset_group = r.asset_group and l.part_id = r.part_id and l.step_id = r.step_id"));
      ORDER_BY("l.asset_group", "l.part_id", "l.step_id");
    }}.toString()).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).filter(o -> Option.of(part).isDefined()).map(o -> o.parameter("part", part))
      .orElse(Option.of(builder)).filter(o -> Option.of(step).isDefined()).map(o -> o.parameter("step", step))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4()))
      .cache();
  }
}