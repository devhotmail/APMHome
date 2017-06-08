package com.get.apm.api.gen;

import com.alibaba.druid.pool.DruidDataSource;
import com.get.apm.api.db.DynamicSqlTest;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromDataSource;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple6;
import javaslang.collection.HashMap;
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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Ignore
public class AsGenerator {
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
    .VALUES("mt_manpower", ":mt_manpower")
    .VALUES("mt_accessory", ":mt_accessory")
    .VALUES("pm_manpower", ":pm_manpower")
    .VALUES("pm_accessory", ":pm_accessory")
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
    assets = db.select(new SQL().SELECT("site_id", "hospital_id", "id", "asset_group", "clinical_dept_id", "supplier_id").FROM("asset_info").ORDER_BY("id").toString()).getAs(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class).map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6())).cache();
    db.update(new SQL().DELETE_FROM("asset_summit").toString()).count().toBlocking().single();
  }

  @After
  public void tearDown() {
    db.close();
  }

  private Map<String, Integer> gen() {
    final int workOrderCount = ThreadLocalRandom.current().nextInt(0, 2) * ThreadLocalRandom.current().nextInt(0, 2);
    final int downTime = workOrderCount * ThreadLocalRandom.current().nextInt(3600, 86400);
    final int mtManpower = downTime * ThreadLocalRandom.current().nextInt(1, 5) / 4;
    final int mtAccessory = downTime * ThreadLocalRandom.current().nextInt(1, 5) / 4;
    final int pmManpower = downTime * ThreadLocalRandom.current().nextInt(1, 5) / 4;
    final int pmAccessory = downTime * ThreadLocalRandom.current().nextInt(1, 5) / 4;
    final int deprecationCost = ThreadLocalRandom.current().nextInt(50, 300);
    final int examCount = ThreadLocalRandom.current().nextInt(60, 200) * Math.abs(86400 - downTime) / 86400;
    final int examDuration = examCount * ThreadLocalRandom.current().nextInt(400, 420);
    final int filmCount = examCount * ThreadLocalRandom.current().nextInt(1, 9);
    final int exposeCount = examCount * ThreadLocalRandom.current().nextInt(1, 9);
    final int injectCount = examCount * ThreadLocalRandom.current().nextInt(1, 9);
    final int revenue = examCount * ThreadLocalRandom.current().nextInt(100, 900);
    final int rating = Math.abs(revenue - (mtAccessory + mtManpower + pmAccessory + pmManpower) - deprecationCost) * examCount / 10000;

    return HashMap.of("revenue", revenue)
      .put("mt_manpower", mtManpower)
      .put("mt_accessory", mtAccessory)
      .put("pm_manpower", pmManpower)
      .put("pm_accessory", pmAccessory)
      .put("deprecation_cost", deprecationCost)
      .put("inject_count", injectCount)
      .put("expose_count", exposeCount)
      .put("film_count", filmCount)
      .put("exam_count", examCount)
      .put("exam_duration", examDuration)
      .put("down_time", downTime)
      .put("work_order_count", workOrderCount)
      .put("rating", rating)
      .toJavaMap();
  }

  @Ignore
  @Test
  public void testGenerate() {
    Observable.from(dates)
      .flatMap(date -> assets.map(t -> Tuple.of(t._1, t._2, t._3, t._4, t._5, t._6, date, gen())))
      .subscribe(t -> db
        .update(sql)
        .parameter("site_id", t._1)
        .parameter("hospital_id", t._2)
        .parameter("asset_id", t._3)
        .parameter("asset_group", t._4)
        .parameter("dept_id", t._5)
        .parameter("supplier_id", t._6)
        .parameter("revenue", t._8.get("revenue"))
        .parameter("mt_manpower", t._8.get("mt_manpower"))
        .parameter("mt_accessory", t._8.get("mt_accessory"))
        .parameter("pm_manpower", t._8.get("pm_manpower"))
        .parameter("pm_accessory", t._8.get("pm_accessory"))
        .parameter("deprecation_cost", t._8.get("deprecation_cost"))
        .parameter("inject_count", t._8.get("inject_count"))
        .parameter("expose_count", t._8.get("expose_count"))
        .parameter("film_count", t._8.get("film_count"))
        .parameter("exam_count", t._8.get("exam_count"))
        .parameter("exam_duration", t._8.get("exam_duration"))
        .parameter("down_time", t._8.get("down_time"))
        .parameter("work_order_count", t._8.get("work_order_count"))
        .parameter("rating", t._8.get("rating"))
        .parameter("created", t._7)
        .parameter("last_modified", t._7)
        .returnGeneratedKeys()
        .getAs(Integer.class)
        .toBlocking()
        .single());
  }
}
