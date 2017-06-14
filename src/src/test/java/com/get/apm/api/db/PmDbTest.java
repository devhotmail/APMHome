package com.get.apm.api.db;

import com.github.davidmoten.rx.jdbc.QuerySelect;
import javaslang.Tuple;
import javaslang.Tuple5;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.cache.annotation.Cacheable;
import rx.Observable;

import java.util.Date;

@Ignore
public class PmDbTest extends AbstractDbTest {
  @Cacheable(cacheNames = "springCache", key = "'pmService.findPm.'+#site+'.'+#hospital+'.'+#from+'.'+#to+'.'+#groupBy+'.'+#dept+'.'+#type+'.'+#supplier+'.'+#asset")
  public Observable<Tuple5<Integer, Integer, Integer, Integer, Integer>> findPm(int site, int hospital, Date from, Date to, String agg, Integer dept, Integer type, Integer supplier, Integer asset) {
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT(String.format("sm.%s as agg", agg), "COALESCE(sum(sm.cmp),0) as sum_cmp", "COALESCE(sum(sm.due),0) as sum_due", "COALESCE(sum(sm.rq),0) as sum_rq", "COALESCE(count(*),0) as sum_all");
      FROM("(select ai.id, ai.asset_group, ai.clinical_dept_id as dept_id, ai.supplier_id, case WHEN extract(DAY from pm.planned_time - pm.end_time) > 0 and pm.is_finished = true THEN 1 ELSE 0 END as cmp, case WHEN pm.end_time is NULL and pm.is_finished != true THEN 1 ELSE 0 END as due, case WHEN extract(DAY from pm.nearest_sr_time - pm.end_time) < 30 and pm.is_finished = true THEN 1 ELSE 0 END as rq from pm_order as pm join asset_info as ai on pm.site_id = ai.site_id and pm.hospital_id = ai.hospital_id and pm.asset_id = ai.id where ai.site_id = :site and ai.hospital_id = :hospital and pm.create_time between :from and :to) as sm");
      if (Option.of(type).filter(i -> i > 0).isDefined()) {
        WHERE("sm.asset_group = :type");
      }
      if (Option.of(dept).filter(i -> i > 0).isDefined()) {
        WHERE("sm.dept_id = :dept");
      }
      if (Option.of(supplier).filter(i -> i > 0).isDefined()) {
        WHERE("sm.supplier_id = :supplier");
      }
      if (Option.of(asset).filter(i -> i > 0).isDefined()) {
        WHERE("sm.id = :asset");
      }
      GROUP_BY("agg");
      ORDER_BY("agg");
    }}.toString()).parameter("site", site).parameter("hospital", hospital).parameter("from", from).parameter("to", to);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(supplier).isDefined()).map(o -> o.parameter("supplier", supplier))
      .orElse(Option.of(builder)).filter(o -> Option.of(asset).isDefined()).map(o -> o.parameter("asset", asset))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5()))
      .cache();
  }

  @Test
  public void testGroupByType() {
    findPm(1, 1, DateTime.parse("2015-1-1").toDate(), DateTime.parse("2015-12-31").toDate(), "asset_group", null, null, null, null).subscribe(System.out::println);
  }

  @Test
  public void testGroupByDept() {
    findPm(1, 1, DateTime.parse("2015-1-1").toDate(), DateTime.parse("2015-12-31").toDate(), "dept_id", null, null, null, null).subscribe(System.out::println);
  }

  @Test
  public void testGroupBySupplier() {
    findPm(1, 1, DateTime.parse("2015-1-1").toDate(), DateTime.parse("2015-12-31").toDate(), "supplier_id", null, null, null, null).subscribe(System.out::println);
  }

  @Test
  public void testGroupByAsset() {
    findPm(1, 1, DateTime.parse("2015-1-1").toDate(), DateTime.parse("2015-12-31").toDate(), "id", null, null, null, null).subscribe(System.out::println);
  }

}