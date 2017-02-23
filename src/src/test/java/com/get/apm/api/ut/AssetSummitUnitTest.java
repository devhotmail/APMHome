package com.get.apm.api.ut;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromDataSource;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple6;
import javaslang.collection.Stream;
import org.apache.ibatis.jdbc.SQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AssetSummitUnitTest {
  private final Logger log = LoggerFactory.getLogger(DynamicSqlTest.class);
  private final List<LocalDate> dates = Stream.iterate(LocalDate.now().minusYears(3), d -> d.plusDays(1)).takeUntil(date -> date.isAfter(LocalDate.now())).toJavaList();
  private final String sql = new SQL().INSERT_INTO("asset_summit")
    .VALUES("site_id", ":site_id")
    .VALUES("hospital_id", ":hospital_id")
    .VALUES("asset_id", ":asset_id")
    .VALUES("asset_group", ":asset_group")
    .VALUES("dept_id", ":dept_id")
    .VALUES("supplier_id", ":supplier_id")
    .VALUES("revenue", ":revenue")
    .VALUES("maintenance_cost", ":maintenance_cost")
    .VALUES("deprecation_cost", ":deprecation_cost")
    .VALUES("inject_count", ":inject_count")
    .VALUES("expose_count", ":expose_count")
    .VALUES("film_count", ":film_count")
    .VALUES("exam_count", ":exam_count")
    .VALUES("exam_duration", ":exam_duration")
    .VALUES("down_time", ":down_time")
    .VALUES("work_order_count", ":work_order_count")
    .VALUES("rating", ":rating")
    .VALUES("created", ":created")
    .VALUES("last_modified", ":last_modified")
    .toString();
  private Database db;
  private Observable<Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> assets;


  @Before
  public void setUp() throws SQLException {
    DruidDataSource dataSource = new DruidDataSource();
    dataSource.setUrl("jdbc:postgresql://localhost:5432/ge_apm");
    dataSource.setUsername("postgres");
    dataSource.setPassword("root");
    dataSource.addFilters("stat");
    dataSource.addFilters("slf4j");
    dataSource.setValidationQuery("SELECT 1");
    db = Database.from(new ConnectionProviderFromDataSource(dataSource));
    assets = db.select(new SQL().SELECT("site_id", "hospital_id", "id", "asset_group", "asset_dept_id").FROM("asset_info").ORDER_BY("id").toString()).getAs(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class).map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), ThreadLocalRandom.current().nextInt(1, 5))).cache();
    db.update(new SQL().DELETE_FROM("asset_summit").toString()).count().toBlocking().single();
  }

  @After
  public void tearDown() {
    db.close();
  }


  @Ignore
  @Test
  public void testFlatMap() {
    Observable.from(dates)
      .flatMap(date -> assets.map(t -> Tuple.of(t._1, t._2, t._3, t._4, t._5, t._6, date)))
      .subscribe(t -> db
        .update(sql)
        .parameter("site_id", t._1)
        .parameter("hospital_id", t._2)
        .parameter("asset_id", t._3)
        .parameter("asset_group", t._4)
        .parameter("dept_id", t._5)
        .parameter("supplier_id", t._6)
        .parameter("revenue", ThreadLocalRandom.current().nextInt(1, 999))
        .parameter("maintenance_cost", ThreadLocalRandom.current().nextInt(1, 500))
        .parameter("deprecation_cost", ThreadLocalRandom.current().nextInt(1, 500))
        .parameter("inject_count", ThreadLocalRandom.current().nextInt(1, 200))
        .parameter("expose_count", ThreadLocalRandom.current().nextInt(0, 200))
        .parameter("film_count", ThreadLocalRandom.current().nextInt(0, 100))
        .parameter("exam_count", ThreadLocalRandom.current().nextInt(0, 100))
        .parameter("exam_duration", ThreadLocalRandom.current().nextInt(1, 80000))
        .parameter("down_time", ThreadLocalRandom.current().nextInt(1, 500))
        .parameter("work_order_count", ThreadLocalRandom.current().nextInt(0, 1))
        .parameter("rating", ThreadLocalRandom.current().nextInt(1, 999))
        .parameter("created", t._7)
        .parameter("last_modified", t._7)
        .returnGeneratedKeys()
        .getAs(Integer.class)
        .toBlocking()
        .single());
  }
}
