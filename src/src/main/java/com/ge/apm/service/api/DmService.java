package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple5;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class DmService {
  private static final Logger log = LoggerFactory.getLogger(DmService.class);
  private static final double SECONDS_IN_ONEDAY = 13 * 3600D;
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }


  @Cacheable(cacheNames = "springCache", key = "'dmService.findAssets.'+#siteId+'.'+#hospitalId+'.groupBy'+#groupBy+'.endDay'+#endDay")
  public Observable<Tuple5<Integer, Double, String, Integer, Double>> findAssets(int siteId, int hospitalId, String groupBy, LocalDate endDay) {
    log.info("siteId: {}; hospitalId: {} groupBy: {} endDay:{}", siteId, hospitalId, groupBy, endDay.toString());
    Observable<Tuple5<Integer, Double, String, Integer, Double>> result =
      db.select(new SQL()
        .SELECT("ai.id", "ai.purchase_price as size", "ai.name", "ai." + Option.when(groupBy.equals("dept"), "clinical_dept_id").getOrElse("asset_group") + " as group_id", "ai.purchase_date", "COALESCE(sum(asu.exam_duration),0) as use_time")
        .FROM("asset_info ai")
        .LEFT_OUTER_JOIN("asset_summit asu on ai.id = asu.asset_id")
        .WHERE("ai.is_valid = true")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("asu.created > :start_day")
        .WHERE("asu.created <= :end_day")
        .GROUP_BY("ai.id")
        .ORDER_BY("group_id").toString())
        .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
        .parameter("start_day", Date.valueOf(endDay.minusYears(1))).parameter("end_day", Date.valueOf(endDay))
        .getAs(Integer.class, Double.class, String.class, Integer.class, Date.class, Integer.class)
        .map(tuple6 -> Tuple.of(tuple6._1(), tuple6._2(), tuple6._3(), tuple6._4(),
          tuple6._6() / ((tuple6._5().toLocalDate().compareTo(endDay.minusYears(1)) > 0 ? tuple6._5().toLocalDate() : endDay.minusYears(1)).until(endDay, ChronoUnit.DAYS) * SECONDS_IN_ONEDAY)))
        .cache();
    log.info("sql success! result count: {}", result.count().toBlocking().single());
    return result;
  }

}
