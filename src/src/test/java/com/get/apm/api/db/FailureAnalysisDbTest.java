package com.get.apm.api.db;

import com.github.davidmoten.rx.jdbc.QuerySelect;
import com.google.common.collect.ImmutableMap;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.Tuple7;
import javaslang.collection.List;
import javaslang.control.Option;
import javaslang.control.Try;
import org.apache.ibatis.jdbc.SQL;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Date;

@Ignore
public class FailureAnalysisDbTest extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(FailureAnalysisDbTest.class);

  public Observable<Tuple3<Integer, Integer, Integer>> briefs(int site, int hospital, Date from, Date to, String groupBy, Integer dept, Integer type, Integer supplier, Integer asset) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT(groupBy, "sum(down_time)", "sum(work_order_count)");
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
      .getAs(Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3()))
      .cache();
  }

  public Observable<Tuple3<Integer, Integer, Integer>> details(int site, int hospital, Date from, Date to, String groupBy, Integer key) {
    return db.select(new SQL() {{
      SELECT(groupBy, "sum(down_time)", "sum(work_order_count)");
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
      .getAs(Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3()))
      .cache();
  }

  public Observable<Tuple2<Integer, Integer>> reasons(int site, int hospital, Date from, Date to, Integer dept, Integer type, Integer supplier, Integer asset) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("w.case_type", "sum(w.case_type)");
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
      ORDER_BY("w.case_type");
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


  @Test
  public void testBriefGroupByAssetGroup() {
    briefs(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), "asset_group", null, null, null, null)
      .subscribe(System.out::println);
  }

  @Test
  public void testBriefGroupByDeptId() {
    briefs(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), "dept_id", null, null, null, null)
      .subscribe(System.out::println);
  }

  @Test
  public void testBriefGroupBySupplierId() {
    briefs(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), "supplier_id", null, null, null, null)
      .subscribe(System.out::println);
  }

  @Test
  public void testBriefGroupByAssetId() {
    briefs(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), "asset_id", null, null, null, null)
      .subscribe(System.out::println);
  }

  @Test
  public void testDetailGroupByAssetGroup() {
    System.out.println(List.ofAll(details(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), "asset_group", 1)
      .toBlocking().toIterable()).headOption());
  }

  @Test
  public void testDetailGroupByDeptId() {
    System.out.println(List.ofAll(details(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), "dept_id", 1)
      .toBlocking().toIterable()).headOption());
  }

  @Test
  public void testDetailGroupBySupplierId() {
    System.out.println(List.ofAll(details(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), "supplier_id", 1)
      .toBlocking().toIterable()).headOption());
  }

  @Test
  public void testDetailGroupByAssetId() {
    System.out.println(List.ofAll(details(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), "asset_id", 1)
      .toBlocking().toIterable()).headOption());
  }

  @Test
  public void testReasons() {
    reasons(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), null, null, null, null).subscribe(System.out::println);
  }

  @Test
  public void testReasonsFilterByDept() {
    reasons(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), 1, null, null, null).subscribe(System.out::println);
  }

  @Test
  public void testReasonsFilterByType() {
    reasons(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), null, 1, null, null).subscribe(System.out::println);
  }

  @Test
  public void testReasonsFilterBySupplier() {
    reasons(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), null, null, 1, null).subscribe(System.out::println);
  }

  @Test
  public void testReasonsFilterByAsset() {
    reasons(1, 1, DateTime.parse("2016-1-1").toDate(), DateTime.parse("2016-12-31").toDate(), null, null, null, 1).subscribe(System.out::println);
  }

  @Test
  public void testFindAssets() {
    System.out.println(Try.of(() -> db.select(new SQL().SELECT("site_id", "hospital_id", "id", "clinical_dept_id", "asset_group", "supplier_id", "name").FROM("asset_info").toString())
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7())).sorted((l, r) -> l._3 - r._3)
      .toMap(Tuple7::_3).cache().toBlocking().single()).getOrElse(ImmutableMap.of()));
  }
}
