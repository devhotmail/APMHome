package com.get.apm.api.db;

import com.google.common.collect.ImmutableMap;
import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.collection.List;
import javaslang.collection.Seq;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;

/**
 * Created by user14 on 5/11/17.
 */
public class FutureDeprecationDbTest extends AbstractDbTest {

  public Seq<Tuple3<Integer, Integer, Double>> getFutureDeprecation(Integer siteId, Integer hospitalId, Integer year) {
    return List.ofAll(
      db.select(new SQL() {{
        SELECT("index.id", "index.month", "COALESCE(sum(ad.deprecate_amount), 0) as deprecation");
        FROM("(Select* FROM ((SELECT id From asset_info ai where ai.site_id = :site_id and ai.hospital_id = :hospital_id) as ids cross join generate_series(1,12) as month)) as index");
        LEFT_OUTER_JOIN("asset_depreciation as ad on index.id=ad.asset_id and extract(month from ad.deprecate_date) = index.month and extract(year from ad.deprecate_date) = :year");
        GROUP_BY("index.id");
        GROUP_BY("index.month");
        ORDER_BY("index.id");
        ORDER_BY("index.month");
      }}.toString())
        .parameter("site_id", siteId)
        .parameter("hospital_id", hospitalId)
        .parameter("year", year)
        .getAs(Integer.class, Integer.class, Double.class)
        .map(tuple -> Tuple.of(tuple._1(), tuple._2(), tuple._3()))
        .toBlocking().toIterable()
    );
  }

  @Test
  public void testFutureDep() {
    System.out.println(getFutureDeprecation(1, 1, 2016).map(v -> ImmutableMap.of(v._1, String.format("m:%s, v:%s", v._2, v._3))).toJavaList());
  }
}
