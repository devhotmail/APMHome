package com.ge.apm.service.api;

import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple7;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;

@Service
public class AssetsService {
  private static final Logger log = LoggerFactory.getLogger(AssetsService.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  @Cacheable(cacheNames = "springCache", key = "'assetsService.findAssets.'+#siteId+'.'+#hospitalId+'.orderBy'+#orderBy")
  public Observable<Tuple7<Integer, String, Integer, String, Integer, Money, LocalDate>> findAssets(int siteId, int hospitalId, String orderBy) {
    return db
      .select(new SQL()
        .SELECT("id", "name", "asset_group as type", "clinical_dept_name as dept", "supplier_id as supplier", "purchase_price as price", "arrive_date as yoa")
        .FROM("asset_info")
        .WHERE("site_id = :site_id").WHERE("hospital_id = :hospital_id").ORDER_BY(Option.of(orderBy).getOrElse("id")).toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
      .getAs(Integer.class, String.class, Integer.class, String.class, Integer.class, Double.class, LocalDate.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), CNY.money(Option.of(t._6()).getOrElse(0D)), Option.of(t._7()).getOrElse(LocalDate.now())))
      .cache();
  }
}
