package com.get.apm.api.gen;


import com.get.apm.api.db.AbstractDbTest;
import javaslang.Tuple;
import javaslang.Tuple5;
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
import java.util.concurrent.ThreadLocalRandom;

import static javaslang.API.*;

@Ignore
public class PmGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(AssetGenerator.class);
  private final String sql = new SQL().INSERT_INTO("pm_order")
    //.VALUES("id", ":id")
    .VALUES("site_id", ":site_id")
    .VALUES("hospital_id", ":hospital_id")
    .VALUES("asset_id", ":asset_id")
    .VALUES("asset_name", ":asset_name")
    .VALUES("name", ":name")
    .VALUES("creator_id", ":creator_id")
    .VALUES("creator_name", ":creator_name")
    .VALUES("create_time", ":create_time")
    .VALUES("planned_time", ":planned_time")
    .VALUES("start_time", ":start_time")
    .VALUES("end_time", ":end_time")
    .VALUES("is_finished", ":is_finished")
    .VALUES("man_hours", ":man_hours")
    .VALUES("nearest_sr_days", ":nearest_sr_days")
    .VALUES("nearest_sr_id", ":nearest_sr_id")
    .toString();
  private Observable<Tuple5<Integer, Integer, Integer, String, Date>> assets;

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    db.update("delete from pm_order").execute();
    assets = db.select("SELECT site_id, hospital_id, id, name, install_date from asset_info").getAs(Integer.class, Integer.class, Integer.class, String.class, Date.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5())).cache();
  }

  @Test
  public void gen() {
    assets.flatMap(t -> Observable.from(Stream.iterate(Tuple.of(t._1, t._2, t._3, t._4, t._5, t._5.toLocalDate()), v -> Tuple.of(v._1, v._2, v._3, v._4, v._5, v._6.plusDays(10))).takeUntil(d -> d._6.isAfter(LocalDate.now().plusYears(2))).toJavaList()))
      .subscribe(r -> db.update(sql)
        .parameter("site_id", r._1)
        .parameter("hospital_id", r._2)
        .parameter("asset_id", r._3)
        .parameter("asset_name", r._4)
        .parameter("name", String.format("pm-%s", r._4))
        .parameter("creator_id", 0)
        .parameter("creator_name", "")
        .parameter("create_time", Timestamp.valueOf(r._6.minusDays(15).atTime(10, 0)))
        .parameter("planned_time", Timestamp.valueOf(r._6.atTime(18, 0)))
        .parameter("start_time", Timestamp.valueOf(r._6.atTime(12 + ThreadLocalRandom.current().nextInt(-3, 3), 0)))
        .parameter("end_time", Timestamp.valueOf(r._6.atTime(18 + ThreadLocalRandom.current().nextInt(-2, 2), 0)))
        .parameter("is_finished", Match(r._6).of(Case($(a -> a.isBefore(LocalDate.now())), true), Case($(), false)))
        .parameter("man_hours", ThreadLocalRandom.current().nextInt(4, 9))
        .parameter("nearest_sr_days", ThreadLocalRandom.current().nextInt(1, 90))
        .parameter("nearest_sr_id", "")
        .returnGeneratedKeys().getAs(Integer.class).toBlocking().single());
  }

}
