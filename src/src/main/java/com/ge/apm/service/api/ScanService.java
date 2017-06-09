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
      ORDER_BY("asset_group", "part_id");
    }}.toString()).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3()))
      .cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'ScanService.assetDetail.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#type+'.'+#dept+'.'+#asset+'.'+#part+'.'+#limit+'.'+#start")
  public Observable<Tuple4<Integer, Integer, Integer, Integer>> assetDetail(int site, int hospital, Date from, Date to, Integer type, Integer dept, Integer asset, Integer part, int limit, int start) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("m.asset_group", "m.asset_id", "m.part_id", "sum(m.exam_count) as exam_num");
      FROM("exam_summit as m");
      WHERE("m.site_id = :site");
      WHERE("m.hospital_id = :hospital");
      WHERE("m.created between :from and :to");
      if (Option.of(type).filter(i -> i > 0).isDefined()) {
        WHERE("m.asset_group = :type");
      }
      if (Option.of(dept).filter(i -> i > 0).isDefined()) {
        WHERE("m.dept_id = :dept");
      }
      if (Option.of(asset).filter(i -> i > 0).isDefined()) {
        WHERE("m.asset_id = :asset");
      }
      if (Option.of(part).filter(i -> i > 0).isDefined()) {
        WHERE("m.part_id = :part");
      }
      GROUP_BY("m.asset_group", "m.asset_id", "m.part_id");
      ORDER_BY("m.asset_group", "m.asset_id", "m.part_id");
    }}.toString().concat(" limit :limit ").concat(" offset :start ")).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).filter(o -> Option.of(part).isDefined()).map(o -> o.parameter("part", part))
      .orElse(Option.of(builder)).filter(o -> Option.of(limit).isDefined()).map(o -> o.parameter("limit", limit))
      .orElse(Option.of(builder)).filter(o -> Option.of(start).isDefined()).map(o -> o.parameter("start", start))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4()))
      .cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'ScanService.stepDetail.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#type+'.'+#dept+'.'+#asset+'.'+#part+'.'+#step+'.'+#limit+'.'+#start")
  public Observable<Tuple4<Integer, Integer, Integer, Integer>> stepDetail(int site, int hospital, Date from, Date to, Integer type, Integer dept, Integer asset, Integer part, Integer step, int limit, int start) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("m.asset_group", "m.part_id", "m.step_id", "sum(m.exam_count) as exam_num");
      FROM("exam_summit as m");
      WHERE("m.site_id = :site");
      WHERE("m.hospital_id = :hospital");
      WHERE("m.created between :from and :to");
      if (Option.of(type).filter(i -> i > 0).isDefined()) {
        WHERE("m.asset_group = :type");
      }
      if (Option.of(dept).filter(i -> i > 0).isDefined()) {
        WHERE("m.dept_id = :dept");
      }
      if (Option.of(asset).filter(i -> i > 0).isDefined()) {
        WHERE("m.asset_id = :asset");
      }
      if (Option.of(part).filter(i -> i > 0).isDefined()) {
        WHERE("m.part_id = :part");
      }
      if (Option.of(step).filter(i -> i > 0).isDefined()) {
        WHERE("m.step_id = :step");
      }
      GROUP_BY("m.asset_group", "m.part_id", "m.step_id");
      ORDER_BY("m.asset_group", "m.part_id", "m.step_id");
    }}.toString().concat(" limit :limit ").concat(" offset :start ")).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).filter(o -> Option.of(part).isDefined()).map(o -> o.parameter("part", part))
      .orElse(Option.of(builder)).filter(o -> Option.of(step).isDefined()).map(o -> o.parameter("step", step))
      .orElse(Option.of(builder)).filter(o -> Option.of(limit).isDefined()).map(o -> o.parameter("limit", limit))
      .orElse(Option.of(builder)).filter(o -> Option.of(start).isDefined()).map(o -> o.parameter("start", start))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4()))
      .cache();
  }
}