package com.get.apm.api.gen;

import com.get.apm.api.db.AbstractDbTest;
import javaslang.Tuple;
import javaslang.collection.Stream;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.sql.Date;

@Ignore
public class DepreciationGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(DepreciationGenerator.class);
  private final String sql = new SQL().INSERT_INTO("asset_depreciation")
    .VALUES("site_id", ":site_id")
    .VALUES("asset_id", ":asset_id")
    .VALUES("deprecate_date", ":deprecate_date")
    .VALUES("deprecate_amount", ":deprecate_amount")
    .toString();

  @Test
  public void gen() {
    db.select(new SQL().SELECT("id", "install_date", "purchase_price").FROM("asset_info").ORDER_BY("id").toString()).getAs(Integer.class, Date.class, Double.class)
      .flatMap(t -> Observable.from(Stream.iterate(t._2().toLocalDate(), d -> d.plusMonths(1)).takeUntil(d -> d.isAfter(t._2().toLocalDate().plusYears(3)))).map(d -> Tuple.of(t._1(), d, t._3() / (12 * 3D))))
      .subscribe(t -> db.update(sql)
        .parameter("site_id", 1)
        .parameter("asset_id", t._1)
        .parameter("deprecate_date", t._2)
        .parameter("deprecate_amount", t._3)
        .returnGeneratedKeys()
        .getAs(Integer.class)
        .toBlocking()
        .single());
  }

}
