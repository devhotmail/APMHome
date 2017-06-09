package com.get.apm.api.db;


import com.ge.apm.view.analysis.WoScheduleController;
import com.google.common.collect.Maps;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

@Ignore
public class HomeAssetStaffDbTest extends AbstractDbTest {
  private final Map<String, String> queries = Maps.newLinkedHashMap();
  private final Date start = new Date(2016, 1, 1);
  private final Date end = new Date(2016, 12, 31);
  private final int siteId = 1;
  private final int hospital_id = 1;
  private final int userId = 5;

  String mtSql = new SQL() {{
    SELECT("sr.from_dept_name || '设备维修' as name", "sr.created_date as start_time", "sr.close_time as end_time", "sr.requestor_name", "ai.location_name", "CASE WHEN sr.status=1 THEN 1 ELSE 0 END as is_closed");
    FROM("v2_service_request sr join asset_info ai on sr.asset_id = ai.id");
    WHERE("sr.created_date between :start and :end");
    WHERE("ai.is_valid = true");
    WHERE("ai.site_id = :site_id");
    WHERE("ai.hospital_id = :hospital_id");
    WHERE("sr.requestor_id= :user_id");
    WHERE("sr.created_date is not null");
    ORDER_BY("sr.created_date");
  }}.toString();

  @Test
  public void testMtQuery() {
    db.select(mtSql)
      .parameter("start", start)
      .parameter("end", end)
      .parameter("hospital_id", hospital_id)
      .parameter("site_id", siteId)
      .parameter("user_id", userId)
      .autoMap(WoScheduleController.Event.class)
      .subscribe(System.out::println);
  }

  String mtNum = new SQL() {{
    SELECT("count(*)");
    FROM("v2_service_request sr join asset_info ai on sr.asset_id = ai.id");
    WHERE("ai.site_id = :site_id");
    WHERE("ai.hospital_id = :hospital_id");
    WHERE("sr.requestor_id = :user_id");
  }}.toString();

  @Test
  public void testMtNum() {
    db.select(mtNum)
      .parameter("hospital_id", hospital_id)
      .parameter("site_id", siteId)
      .parameter("user_id", userId)
      .getAs(Integer.class)
      .subscribe(System.out::println);
  }
}
