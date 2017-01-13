import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import javaslang.control.Option;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CfgTest {
  private final static Logger log = LoggerFactory.getLogger(CfgTest.class);

  @Test
  public void testCfg() {
    System.getenv().entrySet().forEach(kv -> log.info("{} = {}", kv.getKey(), kv.getValue()));
    String googleAnalyticsCfg = Optional.ofNullable(System.getenv("VCAP_SERVICES")).orElse("");
    log.info("google analytics: {}", googleAnalyticsCfg);
    Config cfg = ConfigFactory.parseString(Option.of(System.getenv("GA_CFG")).getOrElse("")).withFallback(ConfigFactory.empty().withValue("ga.enabled", ConfigValueFactory.fromAnyRef(false)).withValue("ga.id", ConfigValueFactory.fromAnyRef("0")));
    log.info("ga.enabled = {}", Option.of(cfg.getBoolean("ga.enabled")).getOrElse(false));
    log.info("ga.id = {}", Option.of(cfg.getString("ga.id")).getOrElse("1"));
  }

}
