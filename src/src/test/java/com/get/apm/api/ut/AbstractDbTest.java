package com.get.apm.api.ut;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromDataSource;
import com.github.davidmoten.rx.jdbc.Database;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;

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
}
