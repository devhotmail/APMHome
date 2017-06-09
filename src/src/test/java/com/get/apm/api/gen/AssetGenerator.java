package com.get.apm.api.gen;

import com.get.apm.api.db.AbstractDbTest;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple4;
import javaslang.control.Option;
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
    .VALUES("warranty_date", ":warranty_date")
    .VALUES("purchase_price", ":purchase_price")
    .VALUES("asset_owner_id", ":asset_owner_id")
    .VALUES("asset_owner_name", ":asset_owner_name")
    .VALUES("location_name", ":location_name")
    .toString();

  private Map<Integer, String> users;
  private Map<Integer, String> assetGroups;
  private Map<Integer, String> orgs;

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    users = db.select("select id, login_name from user_account where site_id = :site and hospital_id = :hospital").parameter("site", 1).parameter("hospital", 1).getAs(Integer.class, String.class).toMap(Tuple2::_1, Tuple2::_2).toBlocking().single();
    orgs = StreamSupport.stream(findOrgs(1).spliterator(), false).collect(Collectors.toMap(t -> t._1, t -> t._5));
    assetGroups = StreamSupport.stream(findMsgs(-1, "assetGroup").spliterator(), false).collect(Collectors.toMap(t -> Ints.tryParse(t._3), t -> t._4));
    db.update("truncate table asset_info cascade").execute();
  }

  private Tuple4<Integer, Integer, Integer, Integer> gen() {
    return Tuple.of(ThreadLocalRandom.current().nextInt(1, assetGroups.size() + 1), ThreadLocalRandom.current().nextInt(2, 45), ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(4, 9));
  }

  @Ignore
  @Test
  public void testGenerate() {
    Observable.range(1, assetGroups.size() * 5).map(id -> Tuple.of(id, gen(), LocalDate.now().minusYears(2).minusDays(ThreadLocalRandom.current().nextInt(-99, 365)), 1_000_000D - ThreadLocalRandom.current().nextDouble(-200_000D, 200_000D), ThreadLocalRandom.current().nextInt(1, 15)))
      .subscribe(t -> db.update(sql)
        .parameter("site_id", 1)
        .parameter("hospital_id", 1)
        .parameter("id", t._1)
        .parameter("name", String.format("%s-%s", assetGroups.get(Option.of(t._1 % assetGroups.size()).filter(i -> i > 0).getOrElse(assetGroups.size())), t._1))
        .parameter("is_valid", true)
        .parameter("status", 1)
        .parameter("function_group", t._2._2)
        .parameter("asset_group", Option.of(t._1 % assetGroups.size()).filter(i -> i > 0).getOrElse(assetGroups.size()))
        .parameter("clinical_dept_id", t._2._4)
        .parameter("supplier_id", t._2._3)
        .parameter("install_date", Date.valueOf(t._3))
        .parameter("warranty_date", Date.valueOf(t._3.plusYears(ThreadLocalRandom.current().nextInt(5, 11))))
        .parameter("purchase_price", t._4)
        .parameter("asset_owner_id", t._5)
        .parameter("asset_owner_name", users.get(t._5))
        .parameter("location_name", String.format("XX院%s科/室", orgs.get(t._2._4)))
        .returnGeneratedKeys()
        .getAs(Integer.class).toBlocking().single());
  }
}
