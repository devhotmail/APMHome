package com.ge.apm.service.utils;


import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import javaslang.control.Try;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;

@Service("rxJdbcConnectionProvider")
public class RxJdbcConnectionProvider implements ConnectionProvider {
  @Resource(name = "risDataSource")
  private DataSource dataSource;

  @Override
  public Connection get() {
    return Try.of(() -> dataSource.getConnection()).getOrElseThrow(() -> new RuntimeException("database connection failed"));
  }

  @Override
  public void close() {
    //do nothing, because connection pool should be closed by spring
  }
}
