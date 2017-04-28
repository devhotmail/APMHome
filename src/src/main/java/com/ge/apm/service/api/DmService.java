package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple6;
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


  @Cacheable(cacheNames = "springCache", key = "'dmService.findAssets.'+#siteId+'.'+#hospitalId+'.dept'+#dept+'.startDate'+#startDate+'.endDate'+#endDate")
  public Observable<Tuple6<Integer, String, Integer, Integer, Double, Double>> findAssets(Integer siteId, Integer hospitalId, Integer dept, LocalDate startDate, LocalDate endDate) {
    log.info("siteId: {}; hospitalId: {}; dept: {}; startdate:{}; endDate:{}", siteId, hospitalId, dept, startDate, endDate);
    return db.select(new SQL() {{
      SELECT("ai.id", "ai.name", "ai.clinical_dept_id", "ai.asset_group", "ai.install_date", "COALESCE(sum(asu.exam_duration),0) as use_time", "COALESCE(sum(asu.revenue),0) as revenue");
      FROM("asset_info ai");
      LEFT_OUTER_JOIN("asset_summit asu on ai.id = asu.asset_id");
      WHERE("ai.is_valid = true");
      WHERE("ai.site_id = :site_id");
      WHERE("ai.hospital_id = :hospital_id");
      if (Option.of(dept).isDefined()) {
        WHERE(String.format("ai.clinical_dept_id = %s", dept));
      }
      WHERE("asu.created >= :start_day");
      WHERE("asu.created <= :end_day");
      WHERE("ai.install_date IS NOT NULL");
      GROUP_BY("ai.id");
      ORDER_BY("ai.id");
    }}.toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
      .parameter("start_day", Date.valueOf(startDate)).parameter("end_day", Date.valueOf(endDate))
      .getAs(Integer.class, String.class, Integer.class, Integer.class, Date.class, Integer.class, Double.class)
      .map(tuple7 -> Tuple.of(tuple7._1(), tuple7._2(), tuple7._3(), tuple7._4(),
        tuple7._6() / ((tuple7._5().toLocalDate().compareTo(startDate) > 0 ? tuple7._5().toLocalDate() : startDate).until(endDate, ChronoUnit.DAYS) * SECONDS_IN_ONEDAY), tuple7._7()))
      .cache();
  }

}
