package com.ge.apm.service.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudCfg {
  private final static Logger log = LoggerFactory.getLogger(CloudCfg.class);
  private final String jdbcHost;
  private final int jdbcPort;
  private final String jdbcDatabase;
  private final String jdbcUserName;
  private final String jdbcPassword;
  private final String jdbcUrl;

  public CloudCfg() {
    Config conf = Try.of(() -> (Config) ConfigFactory.parseString(System.getenv("VCAP_SERVICES")).getConfigList("postgres").get(0))
      //.orElse(Try.of(() -> ConfigFactory.parseResources(this.getClass().getClassLoader(), "database.properties")))
      .getOrElse(ConfigFactory.empty()
        .withValue("credentials.host", ConfigValueFactory.fromAnyRef("localhost"))
        .withValue("credentials.port", ConfigValueFactory.fromAnyRef(5432))
        .withValue("credentials.database", ConfigValueFactory.fromAnyRef("ge_apm"))
        .withValue("credentials.username", ConfigValueFactory.fromAnyRef("postgres"))
        .withValue("credentials.password", ConfigValueFactory.fromAnyRef("root"))
        .withValue("credentials.jdbc_uri", ConfigValueFactory.fromAnyRef("jdbc:postgresql://localhost:5432/ge_apm")));
    jdbcHost = conf.getString("credentials.host");
    jdbcPort = conf.getInt("credentials.port");
    jdbcDatabase = conf.getString("credentials.database");
    jdbcUserName = conf.getString("credentials.username");
    jdbcPassword = conf.getString("credentials.password");
    jdbcUrl = conf.getString("credentials.jdbc_uri");
  }

  public String getJdbcHost() {
    return jdbcHost;
  }

  public int getJdbcPort() {
    return jdbcPort;
  }

  public String getJdbcDatabase() {
    return jdbcDatabase;
  }

  public String getJdbcUserName() {
    return jdbcUserName;
  }

  public String getJdbcPassword() {
    return jdbcPassword;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }
}
