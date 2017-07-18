package com.ge.apm.service.api;

import com.ge.apm.service.utils.CNY;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import javaslang.Tuple;
import javaslang.Tuple8;
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

  @Cacheable(cacheNames = "springCache", key = "'assetsService.findAssets.'+#siteId+'.'+#hospitalId+'.'+#dept+'.orderBy'+#orderBy")
  public Observable<Tuple8<Integer, String, Integer, Integer, Integer, Integer, Money, Integer>> findAssets(Integer siteId, Integer hospitalId, Integer dept, String orderBy) {
    final String sql = new SQL() {{
      SELECT("id", "name", "function_group as func", "asset_group as type", "clinical_dept_id as dept", "supplier_id as supplier", "purchase_price as price", "install_date as yoi");
      FROM("asset_info");
      WHERE("site_id = :site_id");
      WHERE("hospital_id = :hospital_id");
      if (Option.of(dept).filter(d -> d > 0).isDefined()) {
        WHERE("clinical_dept_id = :dept");
      }
      ORDER_BY(Option.of(orderBy).getOrElse("id"));
    }}.toString();
    QuerySelect.Builder builder = db.select(sql).parameter("site_id", siteId).parameter("hospital_id", hospitalId);
    return Option.of(builder).filter(s -> Option.of(dept).filter(d -> d > 0).isDefined()).map(b -> b.parameter("dept", dept)).orElse(Option.of(builder)).get()
      .get(rs -> Tuple.of(rs.getInt("id"), rs.getString("name"), Option.of(rs.getInt("func")).getOrElse(0), Option.of(rs.getInt("type")).getOrElse(0), Option.of(rs.getInt("dept")).getOrElse(0), Option.of(rs.getInt("supplier")).getOrElse(0), CNY.money(Option.of(rs.getDouble("price")).getOrElse(-1D)), Option.of(rs.getDate("yoi")).map(d -> d.toLocalDate().getYear()).getOrElse(-1)))
      .cache();
  }
}

