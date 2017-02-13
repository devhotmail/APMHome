package com.ge.apm.service.utils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.util.stream.StreamSupport;

public class CloudCfg {
  private final static Logger log = LoggerFactory.getLogger(CloudCfg.class);
  private String jdbcUrl;
  private String jdbcUserName;
  private String jdbcPassword;

  public CloudCfg() {
    log.info("VCAP_SERVICES: {}", System.getenv("VCAP_SERVICES"));
    if (Strings.isNullOrEmpty(System.getenv("VCAP_SERVICES"))) {
      loadCfgFromProperties();
    } else {
      loadCfgFromSysEnv();
    }
  }

  private void loadCfgFromSysEnv() {
    Config conf = ConfigFactory.parseString(System.getenv("VCAP_SERVICES"));
    jdbcUrl = StreamSupport.stream(Splitter.on("?").limit(2).split(conf.getConfigList("postgres").stream().findFirst().orElseThrow(() -> new IllegalArgumentException("postgresql service is not existing.")).getString("credentials.jdbc_uri")).spliterator(), false).findFirst().orElse("");
    jdbcUserName = conf.getConfigList("postgres").stream().findFirst().orElseThrow(() -> new IllegalArgumentException("postgresql service is not existing.")).getString("credentials.username");
    jdbcPassword = conf.getConfigList("postgres").stream().findFirst().orElseThrow(() -> new IllegalArgumentException("postgresql service is not existing.")).getString("credentials.password");
  }

  private void loadCfgFromProperties() {
    Config conf = Try.of(() -> ConfigFactory.parseFile(ResourceUtils.getFile("classpath:database.properties")))
      .getOrElse(ConfigFactory.empty()
        .withValue("jdbc.url", ConfigValueFactory.fromAnyRef("jdbc:postgresql://localhost:5432/ge_apm"))
        .withValue("jdbc.user", ConfigValueFactory.fromAnyRef("postgres"))
        .withValue("jdbc.password", ConfigValueFactory.fromAnyRef("root")));
    jdbcUrl = conf.getString("jdbc.url");
    jdbcUserName = conf.getString("jdbc.user");
    jdbcPassword = conf.getString("jdbc.password");
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }

  public String getJdbcUserName() {
    return jdbcUserName;
  }

  public String getJdbcPassword() {
    return jdbcPassword;
  }
}
