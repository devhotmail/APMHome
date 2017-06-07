package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import com.github.davidmoten.rx.jdbc.annotations.Column;
import javaslang.Tuple;
import javaslang.Tuple4;
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

  interface Props {
    @Column
    int id();

    @Column
    String name();

    @Column
    int dept();

    @Column
    int type();

    @Column
    double use_time();

    @Column
    double deprecation();
  }

  /**
   * find data for each asset
   *
   * @param siteId     siteId
   * @param hospitalId hospitalId
   * @param dept       dept id
   * @param startDate  start time "YYYY-MM-DD"
   * @param endDate    end time "YYYY-MM-DD"
   * @return the types for each field are: id, name, dept id, asset group, usage, deprecation
   */
  @Cacheable(cacheNames = "springCache", key = "'dmService.findAssets.'+#siteId+'.'+#hospitalId+'.dept'+#dept+'.startDate'+#startDate+'.endDate'+#endDate")
  public Observable<Tuple6<Integer, String, Integer, Integer, Double, Double>> findAssets(Integer siteId, Integer hospitalId, Integer dept, LocalDate startDate, LocalDate endDate) {
    log.info("siteId: {}; hospitalId: {}; dept: {}; startdate:{}; endDate:{}", siteId, hospitalId, dept, startDate, endDate);
    QuerySelect.Builder builder = db.select(new SQL() {{
      SELECT("ai.id", "ai.name", "ai.clinical_dept_id as dept", "ai.asset_group as type", "COALESCE(avg(asu.exam_duration),0) as use_time", "COALESCE(sum(asu.deprecation_cost), 0) as deprecation");
      FROM("asset_info ai");
      LEFT_OUTER_JOIN("asset_summit asu on ai.id = asu.asset_id");
      WHERE("ai.is_valid = true");
      WHERE("ai.site_id = :site_id");
      WHERE("ai.hospital_id = :hospital_id");
      if (Option.of(dept).isDefined()) {
        WHERE("ai.clinical_dept_id = :dept");
      }
      WHERE("asu.created >= :start_day");
      WHERE("asu.created <= :end_day");
      WHERE("ai.install_date IS NOT NULL");
      GROUP_BY("ai.id");
      ORDER_BY("ai.id");
    }}.toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
      .parameter("start_day", Date.valueOf(startDate)).parameter("end_day", Date.valueOf(endDate));
    builder = Option.of(builder).filter(s -> Option.of(dept).filter(d -> d > 0).isDefined()).map(b -> b.parameter("dept", dept)).orElse(Option.of(builder)).get();
    return builder
      .get(rs -> Tuple.of(rs.getInt("id"), rs.getString("name"), rs.getInt("dept"), rs.getInt("type"),
        rs.getDouble("use_time") / SECONDS_IN_ONEDAY, rs.getDouble("deprecation")))
      .cache();
  }

  @Cacheable(cacheNames = "springCache", key = "'dmService.findMonthUsage.'+#siteId+'.'+#hospitalId+'.startDate'+#startDate+'.endDate'+#endDate")
  public Observable<Tuple4<Integer, LocalDate, Integer, Double>> findMonthUsage(Integer siteId, Integer hospitalId, LocalDate startDate, LocalDate endDate) {
    log.info("siteId: {}; hospitalId: {}; startdate:{}; endDate:{}", siteId, hospitalId, startDate, endDate);
    return db.select(new SQL() {{
      SELECT("ai.id", "date_trunc(:time_unit,asu.created) as created_date", "ai.asset_group", "COALESCE(avg(asu.exam_duration),0) as use_time");
      FROM("asset_info ai");
      LEFT_OUTER_JOIN("asset_summit asu on ai.id = asu.asset_id");
      WHERE("ai.is_valid = true");
      WHERE("ai.site_id = :site_id");
      WHERE("ai.hospital_id = :hospital_id");
      WHERE("asu.created >= :start_day");
      WHERE("asu.created <= :end_day");
      WHERE("ai.install_date IS NOT NULL");
      GROUP_BY("ai.id");
      GROUP_BY("created_date");
      ORDER_BY("ai.id");
      ORDER_BY("created_date");
    }}.toString())
      .parameter("time_unit", "month").parameter("site_id", siteId).parameter("hospital_id", hospitalId).parameter("start_day", Date.valueOf(startDate)).parameter("end_day", Date.valueOf(endDate))
      .getAs(Integer.class, java.sql.Timestamp.class, Integer.class, Double.class)
      .map(tuple -> Tuple.of(tuple._1(), tuple._2().toLocalDateTime().toLocalDate(), tuple._3(),
        tuple._4() / SECONDS_IN_ONEDAY))
      .cache();
  }
}
