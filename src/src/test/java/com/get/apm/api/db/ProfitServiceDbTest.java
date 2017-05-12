package com.get.apm.api.db;

import com.ge.apm.service.utils.CNY;
import javaslang.Tuple;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;

@Ignore
public class ProfitServiceDbTest extends AbstractDbTest {
  @Test
  public void sqlTest() {
    String groupBy = "type";
    Integer dept = null;
    Integer month = null;
    Integer type = 1;
    LocalDate startDate = LocalDate.now().minusYears(1);
    LocalDate endDate = LocalDate.now();
    Integer siteId = 1;
    Integer hospitalId = 1;

    System.out.println(db.select(new SQL() {{
      SELECT(String.format("%s as group_id", Option.when("dept".equals(groupBy), "ai.clinical_dept_id").getOrElse(Option.when("type".equals(groupBy), "ai.asset_group").getOrElse("extract(month from asu.created)"))),
        "COALESCE(sum(asu.revenue), 0)", "COALESCE(sum(asu.maintenance_cost), 0)", "COALESCE(sum(asu.deprecation_cost), 0)");
      FROM("asset_info as ai");
      INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id");
      WHERE("ai.site_id = :site_id");
      WHERE("ai.hospital_id = :hospital_id");
      WHERE("asu.created > :start_day");
      WHERE("asu.created <= :end_day");
      if (Option.of(dept).filter(v -> v >= 1).isDefined()) {
        WHERE("ai.clinical_dept_id = " + String.format("%s", dept));
      }
      if (Option.of(month).filter(v -> v >= 1 && v <= 12).isDefined()) {
        WHERE("extract(month from asu.created) = " + String.format("%s", month));
      }
      if (Option.of(type).filter(v -> v >= 1).isDefined()) {
        WHERE("ai.asset_group = " + String.format("%s", type));
      }
      GROUP_BY("group_id");
      ORDER_BY("group_id");
    }}.toString())
      .parameter("start_day", Date.valueOf(startDate))
      .parameter("end_day", Date.valueOf(endDate))
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      //.parameter("dept", dept)
      //.parameter("month", month)
      //.parameter("type", type)
      .getAs(Integer.class, Double.class, Double.class, Double.class)
      .map(tuple4 -> Tuple.of(tuple4._1(), CNY.money(tuple4._2()), CNY.money(tuple4._3()), CNY.money(tuple4._4()))).count().toBlocking().single());
  }
}
