package com.get.apm.api.ut;


import com.ge.apm.service.utils.CNY;
import javaslang.Tuple;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;

import java.time.LocalDate;

public class AssetServiceUnitTest extends AbstractDbTest {
  @Test
  public void testFindAssets() {
    db.select(new SQL()
      .SELECT("id", "name", "asset_group", "supplier_id", "purchase_price", "arrive_date")
      .FROM("asset_info")
      .WHERE("site_id = :site_id")
      .WHERE("hospital_id = :hospital_id")
      .ORDER_BY("id")
      .toString())
      .parameter("site_id", 1)
      .parameter("hospital_id", 1)
      .getAs(Integer.class, String.class, Integer.class, Integer.class, Double.class, LocalDate.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), CNY.money(t._5()), t._6()))
      .subscribe(t -> System.out.println(t));
  }

}
