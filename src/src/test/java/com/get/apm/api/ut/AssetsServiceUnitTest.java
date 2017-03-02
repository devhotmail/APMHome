package com.get.apm.api.ut;

import com.ge.apm.service.utils.CNY;
import javaslang.Tuple;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;

@Ignore
public class AssetsServiceUnitTest extends AbstractDbTest {
  @Test
  public void testFindAssets() {
    db.select(new SQL().SELECT("id", "name", "asset_group as type", "supplier_id as supplier", "purchase_price as price", "arrive_date as yoa")
      .FROM("asset_info")
      .WHERE("site_id = :site_id").WHERE("hospital_id = :hospital_id")
      .ORDER_BY(Option.of("type").getOrElse("id")).toString())
      .parameter("site_id", 1).parameter("hospital_id", 1)
      .getAs(Integer.class, String.class, Integer.class, Integer.class, Double.class, LocalDate.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), CNY.money(Option.of(t._5()).getOrElse(0D)), Option.of(t._6()).getOrElse(LocalDate.now())))
      .cache()
      .subscribe(t -> Assertions.assertThat(t._1).isGreaterThan(0));
  }
}
