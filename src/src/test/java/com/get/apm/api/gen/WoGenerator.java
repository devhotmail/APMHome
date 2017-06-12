package com.get.apm.api.gen;

import com.get.apm.api.db.AbstractDbTest;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple8;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Ignore
public class WoGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(WoGenerator.class);
  private final String woSql = new SQL().INSERT_INTO("v2_work_order")
    .VALUES("id", ":id")
    .VALUES("site_id", ":site_id")
    .VALUES("hospital_id", ":hospital_id")
    .VALUES("asset_id", ":asset_id")
    .VALUES("sr_id", ":sr_id")
    .VALUES("int_ext_type", ":int_ext_type")
    .VALUES("current_person_id", ":current_person_id")
    .VALUES("current_person_name", ":current_person_name")
    .VALUES("current_step_id", ":current_step_id")
    .VALUES("current_step_name", ":current_step_name")
    .VALUES("status", ":status")
    .VALUES("created_date", ":created_date")
    .VALUES("close_time", ":close_time")
    .VALUES("total_man_hour", ":total_man_hour")
    .VALUES("total_price", ":total_price")
    .VALUES("feedback_rating", ":feedback_rating")
    .VALUES("case_type", ":case_type")
    .toString();
  private final String wsSql = new SQL().INSERT_INTO("v2_work_order_step")
    .VALUES("id", ":id")
    .VALUES("site_id", ":site_id")
    .VALUES("wo_id", ":wo_id")
    .VALUES("step_id", ":step_id")
    .VALUES("step_name", ":step_name")
    .VALUES("owner_id", ":owner_id")
    .VALUES("owner_name", ":owner_name")
    .VALUES("start_time", ":start_time")
    .VALUES("end_time", ":end_time")
    .toString();
  private final String upWoSql = new SQL().UPDATE("v2_work_order")
    .SET("current_step_id = :current_step_id")
    .SET("current_step_name = :current_step_name")
    .WHERE("id = :id")
    .toString();
  private List<Tuple2<Integer, String>> users;
  private Map<Integer, String> woSteps;
  private Map<Integer, String> woTypes;
  private Observable<Tuple8<Integer, Integer, Integer, String, Timestamp, Timestamp, Integer, Integer>> srs;

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    db.update("delete from v2_work_order").execute();
    users = db.select("select id, login_name from user_account where site_id = :site and hospital_id = :hospital").parameter("site", 1).parameter("hospital", 1).getAs(Integer.class, String.class).toList().toBlocking().single();
    woSteps = StreamSupport.stream(findMsgs(-1, "woSteps").spliterator(), false).collect(Collectors.toMap(t -> Ints.tryParse(t._3), t -> t._4));
    woTypes = StreamSupport.stream(findMsgs(-1, "intExtType").spliterator(), false).collect(Collectors.toMap(t -> Ints.tryParse(t._3), t -> t._4));
    srs = db.select("SELECT site_id, hospital_id, asset_id, id, confirmed_down_time, confirmed_up_time from v2_service_request")
      .getAs(Integer.class, Integer.class, Integer.class, String.class, Timestamp.class, Timestamp.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), ThreadLocalRandom.current().nextInt(0, users.size()), ThreadLocalRandom.current().nextInt(2, woSteps.size() + 1)))
      .cache();
  }

  @Test
  public void users() {
    System.out.println(users);
  }

  @Test
  public void testDuration() {
    System.out.println(Duration.between(LocalDateTime.now().minusHours(3), LocalDateTime.now().plusDays(1)).toHours());
  }

  @Test
  public void genWo() {
    srs.subscribe(t -> db.update(woSql)
      .parameter("id", UUID.randomUUID().toString().replaceAll("-", ""))
      .parameter("site_id", t._1)
      .parameter("hospital_id", t._2)
      .parameter("asset_id", t._3)
      .parameter("sr_id", t._4)
      .parameter("int_ext_type", ThreadLocalRandom.current().nextInt(1, woTypes.size() + 1))
      .parameter("current_person_id", users.get(t._7)._1())
      .parameter("current_person_name", users.get(t._7)._2())
      .parameter("current_step_id", t._8)
      .parameter("current_step_name", woSteps.get(t._8))
      .parameter("status", 2)
      .parameter("created_date", t._5)
      .parameter("close_time", t._6)
      .parameter("total_man_hour", Duration.between(t._5.toLocalDateTime(), t._6.toLocalDateTime()).toHours())
      .parameter("total_price", Duration.between(t._5.toLocalDateTime(), t._6.toLocalDateTime()).toHours() * 300D)
      .parameter("feedback_rating", ThreadLocalRandom.current().nextInt(1, 6))
      .parameter("case_type", ThreadLocalRandom.current().nextInt(1, 60))
      .returnGeneratedKeys()
      .getAs(String.class).toBlocking().single());

    db.select(new SQL().SELECT("site_id", "id", "created_date", "close_time").FROM("v2_work_order").WHERE("site_id = :site").WHERE("hospital_id = :hospital").toString()).parameter("site", 1).parameter("hospital", 1)
      .getAs(Integer.class, Integer.class, Timestamp.class, Timestamp.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), ThreadLocalRandom.current().nextInt(0, users.size()), ThreadLocalRandom.current().nextInt(2, woSteps.size() + 1)))
      .subscribe((t) -> {
        db.update(wsSql)
          .parameter("id", UUID.randomUUID().toString().replaceAll("-", ""))
          .parameter("site_id", t._1)
          .parameter("wo_id", t._2)
          .parameter("step_id", t._6)
          .parameter("step_name", woSteps.get(t._6))
          .parameter("owner_id", users.get(t._5)._1())
          .parameter("owner_name", users.get(t._5)._2())
          .parameter("start_time", t._3)
          .parameter("end_time", t._4)
          .returnGeneratedKeys()
          .getAs(String.class).toBlocking().single();
        db.update(upWoSql)
          .parameter("current_step_id", t._6)
          .parameter("current_step_name", woSteps.get(t._6))
          .parameter("id", t._2)
          .execute();
      });
  }

}
