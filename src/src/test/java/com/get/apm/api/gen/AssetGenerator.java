package com.get.apm.api.gen;

import com.get.apm.api.db.AbstractDbTest;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple4;
import javaslang.Tuple6;
import javaslang.Tuple7;
import javaslang.control.Option;
import javaslang.control.Try;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Ignore
public class AssetGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(AssetGenerator.class);
  private final String sql = new SQL().INSERT_INTO("asset_info")
    .VALUES("site_id", ":site_id")
    .VALUES("hospital_id", ":hospital_id")
    .VALUES("id", ":id")
    .VALUES("name", ":name")
    .VALUES("is_valid", ":is_valid")
    .VALUES("status", ":status")
    .VALUES("function_group", ":function_group")
    .VALUES("asset_group", ":asset_group")
    .VALUES("clinical_dept_id", ":clinical_dept_id")
    .VALUES("supplier_id", ":supplier_id")
    .VALUES("install_date", ":install_date")
    .VALUES("purchase_price", ":purchase_price")
    .toString();


  public Iterable<Tuple7<Integer, String, String, String, String, String, Integer>> findMsgs(Integer site, String msgType) {
    return Try.of(() -> db.select(new SQL().SELECT("id", "msg_type", "msg_key", "value_zh", "value_en", "value_tw", "site_id").FROM("i18n_message").WHERE("site_id = :site").WHERE("msg_type = :msgType").toString())
      .parameter("site", site).parameter("msgType", msgType)
      .getAs(Integer.class, String.class, String.class, String.class, String.class, String.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7())).sorted((l, r) -> l._1 - r._1)
      .cache().toBlocking().toIterable()).getOrElse(ImmutableList.of());
  }

  public Iterable<Tuple6<Integer, Integer, Integer, Integer, String, String>> findOrgs(Integer site) {
    return db.select("select id, parent_id as parent, site_id as site, hospital_id as hospital, name as cn, name_en as en from org_info where site_id = :site and parent_id is not NULL").parameter("site", site)
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class)
      .map(t -> Tuple.of(t._1(), Option.of(t._2()).getOrElse(0), Option.of(t._3()).getOrElse(0), Option.of(t._4()).getOrElse(0), Option.of(t._5()).getOrElse(""), Option.of(t._6()).getOrElse("")))
      .cache().toBlocking().toIterable();
  }

  public Map<Integer, String> findSuppliers(int siteId) {
    return Try.of(() -> db.select(new SQL().SELECT("id", "name").FROM("supplier").WHERE("site_id = :site_id").toString())
      .parameter("site_id", siteId).getAs(Integer.class, String.class)
      .toMap(Tuple2::_1, Tuple2::_2)
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  private Map<Integer, String> assetGroups;

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    assetGroups = StreamSupport.stream(findMsgs(-1, "assetGroup").spliterator(), false).collect(Collectors.toMap(t -> Ints.tryParse(t._3), t -> t._4));
    db.update("truncate table asset_info cascade").execute();
  }

  private Tuple4<Integer, Integer, Integer, Integer> gen() {
    return Tuple.of(ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(2, 45), ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(4, 9));
  }

  @Ignore
  @Test
  public void testGenerate() {
    Observable.range(1, 90).map(id -> Tuple.of(id, gen(), LocalDate.now().minusYears(2).minusDays(ThreadLocalRandom.current().nextInt(-99, 365)), 1_000_000D - ThreadLocalRandom.current().nextDouble(-200_000D, 200_000D)))
      .subscribe(t -> db.update(sql)
        .parameter("site_id", 1)
        .parameter("hospital_id", 1)
        .parameter("id", t._1)
        .parameter("name", String.format("%s-%s", assetGroups.get(t._2._1), t._1))
        .parameter("is_valid", true)
        .parameter("status", 1)
        .parameter("function_group", t._2._2)
        .parameter("asset_group", t._2._1)
        .parameter("clinical_dept_id", t._2._4)
        .parameter("supplier_id", t._2._3)
        .parameter("install_date", Date.valueOf(t._3))
        .parameter("purchase_price", t._4)
        .returnGeneratedKeys()
        .getAs(Integer.class).toBlocking().single());
  }
}
