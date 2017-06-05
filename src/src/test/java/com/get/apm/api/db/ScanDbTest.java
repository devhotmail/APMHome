package com.get.apm.api.db;

import com.github.davidmoten.rx.jdbc.QuerySelect;
import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.Tuple5;
import javaslang.Tuple7;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Date;
import java.util.Objects;

@Ignore
public class ScanDbTest extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(ScanDbTest.class);

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

  public Observable<Tuple5<Integer, Integer, String, Integer, Integer>> assetDetail(int site, int hospital, Date from, Date to, Integer type, Integer dept, Integer asset, Integer part, int limit, int start) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("m.asset_group", "m.asset_id", "a.name as asset_name", "m.part_id", "sum(m.exam_count) as exam_num");
      FROM("exam_summit as m");
      JOIN("asset_info as a on m.asset_id = a.id");
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
      GROUP_BY("m.asset_group", "m.asset_id", "a.name", "m.part_id");
      ORDER_BY("m.asset_group", "m.asset_id", "m.part_id");
    }}.toString().concat(" limit :limit ").concat(" offset :start ")).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).filter(o -> Option.of(part).isDefined()).map(o -> o.parameter("part", part))
      .orElse(Option.of(builder)).filter(o -> Option.of(limit).isDefined()).map(o -> o.parameter("limit", limit))
      .orElse(Option.of(builder)).filter(o -> Option.of(start).isDefined()).map(o -> o.parameter("start", start))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, String.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5()))
      .cache();
  }

  public Observable<Tuple7<Integer, Integer, String, Integer, Integer, String, Integer>> stepDetail(int site, int hospital, Date from, Date to, Integer type, Integer dept, Integer asset, Integer part, Integer step, int limit, int start) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("m.asset_group", "m.asset_id", "a.name as asset_name", "m.part_id", "m.step_id", "s.name as step_name", "sum(m.exam_count) as exam_num");
      FROM("exam_summit as m");
      JOIN("proc_step as s on m.step_id = s.id");
      JOIN("asset_info as a on m.asset_id = a.id");
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
      GROUP_BY("m.asset_group", "m.asset_id", "a.name", "m.part_id", "m.step_id", "s.name");
      ORDER_BY("m.asset_group", "m.asset_id", "m.part_id", "m.step_id");
    }}.toString().concat(" limit :limit ").concat(" offset :start ")).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).filter(o -> Option.of(part).isDefined()).map(o -> o.parameter("part", part))
      .orElse(Option.of(builder)).filter(o -> Option.of(step).isDefined()).map(o -> o.parameter("step", step))
      .orElse(Option.of(builder)).filter(o -> Option.of(limit).isDefined()).map(o -> o.parameter("limit", limit))
      .orElse(Option.of(builder)).filter(o -> Option.of(start).isDefined()).map(o -> o.parameter("start", start))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, String.class, Integer.class, Integer.class, String.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7()))
      .cache();
  }

  @Test
  public void testBrief() {
    brief(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), null, null, 1)
      .subscribe(System.out::println);
  }

  @Test
  public void testAssetDetail() {
    assetDetail(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), null, null, 1, null, 99, 0)
      .subscribe(System.out::println);
  }

  @Test
  public void testStepDetail() {
    stepDetail(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), null, null, 1, null, null, 99, 0)
      .subscribe(System.out::println);
  }
}
