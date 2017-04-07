package com.ge.apm.service.api;

import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import com.github.davidmoten.rx.jdbc.annotations.Column;
import javaslang.Tuple;
import javaslang.Tuple8;
import javaslang.control.Option;
import javaslang.control.Try;
import org.apache.ibatis.jdbc.SQL;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Date;

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

  interface Asset {
    @Column
    int id();

    @Column
    String name();

    @Column
    int group();

    @Column
    int type();

    @Column
    int dept();

    @Column
    int supplier();

    @Column
    double price();

    @Column
    Date yoi();
  }


  @Cacheable(cacheNames = "springCache", key = "'assetsService.findAssets.'+#siteId+'.'+#hospitalId+'.'+#dept+'.orderBy'+#orderBy")
  public Observable<Tuple8<Integer, String, Integer, Integer, Integer, Integer, Money, Integer>> findAssets(Integer siteId, Integer hospitalId, Integer dept, String orderBy) {
    final String sql = new SQL() {{
      SELECT("id", "name", "function_group as group", "asset_group as type", "clinical_dept_id as dept", "supplier_id as supplier", "purchase_price as price", "install_date as yoi");
      FROM("asset_info");
      WHERE("site_id = :site_id");
      WHERE("hospital_id = :hospital_id");
      if (Option.of(dept).isDefined()) {
        WHERE("clinical_dept_id = :dept");
      }
      ORDER_BY(Option.of(orderBy).getOrElse("id"));
    }}.toString();
    QuerySelect.Builder builder = db.select(sql).parameter("site_id", siteId).parameter("hospital_id", hospitalId);
    return Option.of(builder).filter(s -> Option.of(dept).filter(d -> d > 0).isDefined()).map(b -> b.parameter("dept", dept)).orElse(Option.of(builder)).get().autoMap(Asset.class)
      .map(a -> Tuple.of(a.id(), a.name(), Try.of(a::group).getOrElse(0), Try.of(a::type).getOrElse(0), Try.of(a::dept).getOrElse(0), Try.of(a::supplier).getOrElse(0), CNY.money(Option.of(a.price()).getOrElse(-1D)), Option.of(a.yoi()).map(d -> d.toLocalDate().getYear()).getOrElse(-1)))
      .cache();
  }
}

