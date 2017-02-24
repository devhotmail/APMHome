package com.ge.apm.service.api;

import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple6;
import org.apache.ibatis.jdbc.SQL;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;

public class AssetsService {
  private static final Logger log = LoggerFactory.getLogger(AssetsService.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  @Cacheable(cacheNames = "springCache", key = "'assetsService.findAssets.'+#siteId+'.'+#hospitalId")
  public Observable<Tuple6<Integer, String, Integer, Integer, Money, LocalDate>> findAssets(int siteId, int hospitalId, String orderBy) {
    return db.select(new SQL()
      .SELECT("id", "name", "asset_group", "supplier_id", "purchase_price", "arrive_date")
      .FROM("asset_info")
      .WHERE("site_id = :site_id")
      .WHERE("hospital_id = :hospital_id")
      .ORDER_BY("id")
      .toString())
      .parameter("site_id", 1)
      .parameter("hospital_id", 1)
      .getAs(Integer.class, String.class, Integer.class, Integer.class, Double.class, LocalDate.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), CNY.money(t._5()), t._6()));
  }
}
