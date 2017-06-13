package com.get.apm.api.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromDataSource;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javaslang.Tuple;
import javaslang.Tuple6;
import javaslang.Tuple7;
import javaslang.control.Option;
import javaslang.control.Try;
import org.apache.ibatis.jdbc.SQL;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractDbTest {
  protected Database db;

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
}
