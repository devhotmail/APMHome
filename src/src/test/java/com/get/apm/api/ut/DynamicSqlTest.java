package com.get.apm.api.ut;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromDataSource;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.sql.SQLException;

@Ignore
public class DynamicSqlTest {
  private final Logger log = LoggerFactory.getLogger(DynamicSqlTest.class);
  private Database db;

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
  }

  @After
  public void tearDown() {
    db.close();
  }

  @Ignore
  @Test
  public void testConnection() {
    db.select("select id, msg_type, msg_key, value_zh, value_en from i18n_message where msg_type = :type")
      .parameter("type", "month").parameters(Observable.never())
      .getAs(Integer.class, String.class, String.class, String.class, String.class)
      .subscribe(t -> log.info(t.toString()));
  }

  private Observable<Tuple3<Integer, String, Double>> dynamicQuery(Integer type, Integer dept, Integer month) {
    String sql = new SQL() {{
      SELECT("ai.id", "ai.name", "COALESCE(sum(ar.price_amount),0)");
      FROM("asset_info ai");
      INNER_JOIN("asset_clinical_record ar on ai.id = ar.asset_id");
      WHERE("ai.site_id = :site_id");
      WHERE("ai.hospital_id = :hospital_id");
      WHERE("extract(year from ar.exam_date) = :year");
      if (Option.of(type).isDefined()) {
        WHERE("ai.asset_group = :type");
      }
      if (Option.of(dept).isDefined()) {
        WHERE("ai.clinical_dept_id= :dept");
      }
      if (Option.of(month).isDefined()) {
        WHERE("extract(month from ar.exam_date) = :month");
      }
      GROUP_BY("ai.id");
      ORDER_BY("ai.id");
    }}.toString();
    QuerySelect.Builder builder = db.select(sql).parameter("site_id", 1).parameter("hospital_id", 1).parameter("year", 2016);
    return Option.of(builder).filter(s -> Option.of(type).isDefined()).map(o -> o.parameter("type", type))
      .orElse(Option.of(builder)).filter(o -> Option.of(dept).isDefined()).map(o -> o.parameter("dept", dept))
      .orElse(Option.of(builder)).filter(o -> Option.of(month).isDefined()).map(o -> o.parameter("month", month))
      .orElse(Option.of(builder)).get()
      .getAs(Integer.class, String.class, Double.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3()));
  }

  @Ignore
  @Test
  public void testSqlBuilder() {
    Assertions.assertThat(dynamicQuery(1, 2, 3).count().toBlocking().single()).isGreaterThan(0);
    Assertions.assertThat(dynamicQuery(null, 2, 3).count().toBlocking().single()).isGreaterThan(0);
    Assertions.assertThat(dynamicQuery(1, null, 3).count().toBlocking().single()).isGreaterThan(0);
    Assertions.assertThat(dynamicQuery(1, 2, null).count().toBlocking().single()).isGreaterThan(0);
    Assertions.assertThat(dynamicQuery(null, null, 3).count().toBlocking().single()).isGreaterThan(0);
    Assertions.assertThat(dynamicQuery(1, null, null).count().toBlocking().single()).isGreaterThan(0);
    Assertions.assertThat(dynamicQuery(null, 2, null).count().toBlocking().single()).isGreaterThan(0);
    Assertions.assertThat(dynamicQuery(null, null, null).count().toBlocking().single()).isGreaterThan(0);
  }
}
