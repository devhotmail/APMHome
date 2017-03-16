package com.get.apm.api.ut;


import org.apache.camel.util.StopWatch;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;

import java.time.LocalDate;

public class DataExtractSpeedComparisonTest extends AbstractDbTest {
  @Test
  public void testBigDataOneTime() {
    StopWatch stopWatch = new StopWatch(true);
    int bigData =
      db.select(new SQL()
        .SELECT("ai.id", "ai.purchase_price as size", "ai.name", "ai." + "asset_group" + " as group_name", "ai.purchase_date", "asu.exam_duration as use_time", "asu.created")
        .FROM("asset_info as ai")
        .INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("extract(year from asu.created) >= 2015")
        .WHERE("extract(year from asu.created) <= 2016")
        .ORDER_BY("group_name").toString())
        .parameter("site_id", 1).parameter("hospital_id", 1)
        .getAs(Integer.class, Double.class, String.class, String.class, LocalDate.class, Integer.class, LocalDate.class)
        //.map(tuple6 -> Tuple.of(tuple6._1(), tuple6._2(), tuple6._3(), tuple6._4(), tuple6._5(), tuple6._6()))
        .count()
        .toBlocking()
        .single();
    stopWatch.stop();
    System.out.println("big data one times: " + stopWatch.taken() + "ms");
    System.out.println(bigData);
    //StreamSupport.stream(bigData.spliterator(), true).forEach(System.out::println);
  }

  @Test
  public void testSmallDataTwoTimes() {
    StopWatch stopWatch2 = new StopWatch(true);
    int small_1 =
      db.select(new SQL()
        .SELECT("ai.id", "ai.purchase_price as size", "ai.name", "ai." + "asset_group" + " as group_name", "ai.purchase_date", "sum(asu.exam_duration) as use_time")
        .FROM("asset_info as ai")
        .INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("extract(year from asu.created) = 2015")
        //.WHERE("extract(year from asu.created) <= 2016")
        .GROUP_BY("ai.id")
        .ORDER_BY("group_name").toString())
        .parameter("site_id", 1).parameter("hospital_id", 1)
        .getAs(Integer.class, Double.class, String.class, String.class, LocalDate.class, Integer.class)
        //.map(tuple6 -> Tuple.of(tuple6._1(), tuple6._2(), tuple6._3(), tuple6._4(), tuple6._5(), tuple6._6()))
        .count()
        .toBlocking()
        .single();

    int small_2 =
      db.select(new SQL()
        .SELECT("ai.id", "ai.purchase_price as size", "ai.name", "ai." + "asset_group" + " as group_name", "ai.purchase_date", "sum(asu.exam_duration) as use_time")
        .FROM("asset_info as ai")
        .INNER_JOIN("asset_summit as asu on ai.id = asu.asset_id")
        .WHERE("ai.site_id = :site_id")
        .WHERE("ai.hospital_id = :hospital_id")
        .WHERE("extract(year from asu.created) = 2016")
        //.WHERE("extract(year from asu.created) <= 2016")
        .GROUP_BY("ai.id")
        .ORDER_BY("group_name").toString())
        .parameter("site_id", 1).parameter("hospital_id", 1)
        .getAs(Integer.class, Double.class, String.class, String.class, LocalDate.class, Integer.class)
        //.map(tuple6 -> Tuple.of(tuple6._1(), tuple6._2(), tuple6._3(), tuple6._4(), tuple6._5(), tuple6._6()))
        .count()
        .toBlocking()
        .single();
    stopWatch2.stop();
    System.out.println("Small data two time: " + stopWatch2.taken() + "ms");
    System.out.println("small one: " + small_1);
    System.out.println("small two: " + small_2);
  }

}
