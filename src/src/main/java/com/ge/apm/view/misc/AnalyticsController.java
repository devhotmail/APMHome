package com.ge.apm.view.misc;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import javaslang.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class AnalyticsController {
  private final static Logger log = LoggerFactory.getLogger(AnalyticsController.class);
  private final String WEB_ANALYTICS_CFG;
  private Config gaCfg;

  public AnalyticsController() {
    WEB_ANALYTICS_CFG = Option.of(System.getenv("WEB_ANALYTICS_CFG")).getOrElse("");
  }

  @PostConstruct
  public void init() {
    log.info("WEB_ANALYTICS_CFG = {}", WEB_ANALYTICS_CFG);
    gaCfg = ConfigFactory.parseString(WEB_ANALYTICS_CFG).withFallback(ConfigFactory.empty()
      .withValue("ga.enabled", ConfigValueFactory.fromAnyRef(false))
      .withValue("ga.id", ConfigValueFactory.fromAnyRef("0")));
  }

  public boolean getGaEnabled() {
    return Option.of(gaCfg.getBoolean("ga.enabled")).getOrElse(false);
  }

  public String getGaId() {
    return Option.of(gaCfg.getString("ga.id")).getOrElse("0");
  }
}
