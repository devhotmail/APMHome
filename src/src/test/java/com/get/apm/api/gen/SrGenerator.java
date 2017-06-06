package com.get.apm.api.gen;

import com.get.apm.api.db.AbstractDbTest;
import javaslang.Tuple;
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
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

@Ignore
public class SrGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(SrGenerator.class);
  private final String sql = new SQL().INSERT_INTO("v2_service_request")
    .VALUES("id", ":id")
    .VALUES("site_id", ":site_id")
    .VALUES("hospital_id", ":hospital_id")
    .VALUES("hospital_name", ":hospital_name")
    .VALUES("asset_id", ":asset_id")
    .VALUES("asset_name", ":asset_name")
    .VALUES("case_priority", ":case_priority")
    .VALUES("request_reason", ":request_reason")
    .VALUES("requestor_id", ":requestor_id")
    .VALUES("requestor_name", ":requestor_name")
    .VALUES("status", ":status")
    .VALUES("request_time", ":request_time")
    .VALUES("confirmed_down_time", ":confirmed_down_time")
    .VALUES("confirmed_up_time", ":confirmed_up_time")
    .VALUES("estimated_close_time", ":estimated_close_time")
    .VALUES("close_time", ":close_time")
    .VALUES("nearest_sr_days", ":nearest_sr_days")
    .VALUES("nearest_sr_id", ":nearest_sr_id")
    .toString();

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    db.update("delete from v2_service_request").execute();
  }


  @Test
  public void genSr() {
    db.select(new SQL().SELECT("id", "install_date").FROM("asset_info").ORDER_BY("id").toString()).getAs(Integer.class, Date.class)
      .flatMap(t -> Observable.from(javaslang.collection.List.fill(ThreadLocalRandom.current().nextInt((int) MONTHS.between(t._2().toLocalDate(), LocalDate.now()) / 2, (int) MONTHS.between(t._2().toLocalDate(), LocalDate.now())), () -> t._2().toLocalDate().plusDays(ThreadLocalRandom.current().nextInt(1, (int) DAYS.between(t._2().toLocalDate(), LocalDate.now())))).map(v -> Tuple.of(t._1(), v))))
      .map(t -> Tuple.of(t._1, t._2, t._2.atTime(9 + ThreadLocalRandom.current().nextInt(0, 3), 0), t._2.atTime(ThreadLocalRandom.current().nextInt(12, 24), 0), ThreadLocalRandom.current().nextInt(0, 99), ""))
      .sorted((l, r) -> l._3.compareTo(r._3))
      .subscribe(t ->
        db.update(sql)
          .parameter("id", UUID.randomUUID().toString().replaceAll("-", ""))
          .parameter("site_id", 1)
          .parameter("hospital_id", 1)
          .parameter("hospital_name", "")
          .parameter("asset_id", t._1)
          .parameter("asset_name", "")
          .parameter("case_priority", 1)
          .parameter("request_reason", "")
          .parameter("requestor_id", 5)
          .parameter("requestor_name", "user")
          .parameter("status", 2)
          .parameter("request_time", Timestamp.valueOf(t._3))
          .parameter("confirmed_down_time", Timestamp.valueOf(t._3))
          .parameter("confirmed_up_time", Timestamp.valueOf(t._4))
          .parameter("estimated_close_time", Timestamp.valueOf(t._4.plusDays(2)))
          .parameter("close_time", Timestamp.valueOf(t._4.plusDays(3)))
          .parameter("nearest_sr_days", t._5)
          .parameter("nearest_sr_id", t._6)
          .returnGeneratedKeys()
          .getAs(String.class).toBlocking().single());
  }

}
