package com.get.apm.api.gen;

import com.get.apm.api.db.AbstractDbTest;
import javaslang.Tuple;
import javaslang.Tuple6;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Ignore
public class WoGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(WoGenerator.class);
  private final String sql = new SQL().INSERT_INTO("v2_work_order")
    .VALUES("id", ":id")
    .VALUES("site_id", ":site_id")
    .VALUES("hospital_id", ":hospital_id")
    .VALUES("asset_id", ":asset_id")
    .VALUES("sr_id", ":sr_id")
    .VALUES("current_person_id", ":current_person_id")
    .VALUES("current_step_id", ":current_step_id")
    .VALUES("current_step_name", ":current_step_name")
    .VALUES("status", ":status")
    .VALUES("close_time", ":close_time")
    .VALUES("total_man_hour", ":total_man_hour")
    .VALUES("total_price", ":total_price")
    .toString();

  private Observable<Tuple6<Integer, Integer, Integer, String, Timestamp, Timestamp>> srs;

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    db.update("delete from v2_work_order").execute();
    srs = db.select("SELECT site_id, hospital_id, asset_id, id, confirmed_down_time, confirmed_up_time from v2_service_request")
      .getAs(Integer.class, Integer.class, Integer.class, String.class, Timestamp.class, Timestamp.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6()))
      .cache();
  }

  @Test
  public void testDuration() {
    System.out.println(Duration.between(LocalDateTime.now().minusHours(3), LocalDateTime.now().plusDays(1)).toHours());
  }

  @Test
  public void gen() {
    srs.subscribe(t -> db.update(sql)
      .parameter("id", UUID.randomUUID().toString().replaceAll("-", ""))
      .parameter("site_id", t._1)
      .parameter("hospital_id", t._2)
      .parameter("asset_id", t._3)
      .parameter("sr_id", t._4)
      .parameter("current_person_id", 0)
      .parameter("current_step_id", 0)
      .parameter("current_step_name", "")
      .parameter("status", 1)
      .parameter("close_time", t._6)
      .parameter("total_man_hour", Duration.between(t._5.toLocalDateTime(), t._6.toLocalDateTime()).toHours())
      .parameter("total_price", Duration.between(t._5.toLocalDateTime(), t._6.toLocalDateTime()).toHours() * 300D)
      .returnGeneratedKeys()
      .getAs(String.class).toBlocking().single());
  }


}
