package com.get.apm.api.gen;


import com.get.apm.api.db.AbstractDbTest;
import com.google.common.collect.ImmutableMap;
import javaslang.Tuple;
import javaslang.Tuple6;
import javaslang.collection.Stream;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static javaslang.API.*;

@Ignore
public class InspectionGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(InspectionGenerator.class);
  private final Map<Integer, String> inspections = ImmutableMap.of(1, "巡检", 2, "计量");
  private final String ioSql = new SQL().INSERT_INTO("inspection_order")
    .VALUES("site_id", ":site_id")
    .VALUES("hospital_id", ":hospital_id")
    .VALUES("name", ":name")
    .VALUES("order_type", ":order_type")
    .VALUES("creator_id", ":creator_id")
    .VALUES("creator_name", ":creator_name")
    .VALUES("create_time", ":create_time")
    .VALUES("owner_id", ":owner_id")
    .VALUES("owner_name", ":owner_name")
    .VALUES("owner_org_id", ":owner_org_id")
    .VALUES("owner_org_name", ":owner_org_name")
    .VALUES("start_time", ":start_time")
    .VALUES("end_time", ":end_time")
    .VALUES("is_finished", ":is_finished")
    .VALUES("man_hours", ":man_hours")
    .toString();

  private final String iodSql = new SQL().INSERT_INTO("inspection_order_detail")
    .VALUES("site_id", ":site_id")
    .VALUES("order_id", ":order_id")
    .VALUES("asset_id", ":asset_id")
    .VALUES("asset_name", ":asset_name")
    .VALUES("dept_id", ":dept_id")
    .VALUES("dept_name", ":dept_name")
    .VALUES("item_id", ":item_id")
    .VALUES("item_name", ":item_name")
    .VALUES("is_passed", ":is_passed")
    .toString();
  private Observable<Tuple6<Integer, Integer, Integer, String, Date, Integer>> assets;
  private Map<Integer, String> orgs;
  private Map<Integer, Tuple6<Integer, Integer, Integer, String, Date, Integer>> assetsMap;

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    db.update("delete from inspection_order").execute();
    db.update("delete from inspection_order_detail").execute();
    assets = db.select("SELECT site_id, hospital_id, id, name, install_date, clinical_dept_id from asset_info").getAs(Integer.class, Integer.class, Integer.class, String.class, Date.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6())).cache();
    assetsMap = assets.toMap(t -> t._3).toBlocking().single();
    orgs = StreamSupport.stream(findOrgs(1).spliterator(), false).collect(Collectors.toMap(t -> t._1, t -> t._5));
  }

  @Test
  public void gen() {
    assets.flatMap(t -> Observable.from(Stream.iterate(Tuple.of(t._1, t._2, t._3, t._4, t._5, t._5.toLocalDate(), ThreadLocalRandom.current().nextInt(1, 3)), v -> Tuple.of(v._1, v._2, v._3, v._4, v._5, v._6.plusDays(5), v._7)).takeUntil(d -> d._6.isAfter(LocalDate.now().plusYears(2))).toJavaList()))
      .subscribe(r -> db.update(ioSql)
        .parameter("site_id", r._1)
        .parameter("hospital_id", r._2)
        .parameter("name", String.format("%s-%s", inspections.getOrDefault(r._7, ""), r._4))
        .parameter("order_type", r._7)
        .parameter("creator_id", 0)
        .parameter("creator_name", "")
        .parameter("create_time", Timestamp.valueOf(r._6.minusDays(15).atTime(10, 0)))
        .parameter("owner_id", ThreadLocalRandom.current().nextInt(1, 15))
        .parameter("owner_name", "")
        .parameter("owner_org_id", 4)
        .parameter("owner_org_name", "")
        .parameter("start_time", Timestamp.valueOf(r._6.atTime(12 + ThreadLocalRandom.current().nextInt(-3, 3), 0)))
        .parameter("end_time", Match(r._6).of(Case($(a -> a.isBefore(LocalDate.now())), Timestamp.valueOf(r._6.atTime(18 + ThreadLocalRandom.current().nextInt(-2, 2), 0))), Case($(), (Timestamp) null)))
        .parameter("is_finished", Match(r._6).of(Case($(a -> a.isBefore(LocalDate.now())), true), Case($(), false)))
        .parameter("man_hours", ThreadLocalRandom.current().nextInt(1, 4))
        .returnGeneratedKeys().getAs(Integer.class).toBlocking().single());
    db.select("select id from inspection_order").getAs(Integer.class)
      .map(i -> Tuple.of(i, assetsMap.get(ThreadLocalRandom.current().nextInt(1, assetsMap.size()))))
      .subscribe(t ->
        db.update(iodSql)
          .parameter("site_id", t._2._1)
          .parameter("order_id", t._1)
          .parameter("asset_id", t._2._3)
          .parameter("asset_name", t._2._4)
          .parameter("dept_id", t._2._6)
          .parameter("dept_name", orgs.getOrDefault(t._2._6, ""))
          .parameter("item_id", 0)
          .parameter("item_name", "")
          .parameter("is_passed", ThreadLocalRandom.current().nextBoolean())
          .execute()
      );
  }

}
