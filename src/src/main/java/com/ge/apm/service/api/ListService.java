package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.tuple.Tuple4;
import com.github.davidmoten.rx.jdbc.tuple.Tuple5;
import com.github.davidmoten.rx.jdbc.tuple.Tuple6;
import com.github.davidmoten.rx.jdbc.tuple.Tuple7;

import org.springframework.stereotype.Service;
import rx.Observable;

import java.sql.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class ListService {

  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

// #1
  private static final String asset_basic

    = " SELECT asset_id, name, asset_group as type, clinical_dept_id as dept_id, clinical_dept_name as dept "
    + " FROM "
        + " (SELECT DISTINCT asset_id FROM asset_summit WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to ) left_table "
        + " LEFT JOIN "
        + " (SELECT id, name, asset_group, clinical_dept_id, clinical_dept_name FROM asset_info ) right_table "
        + " ON left_table.asset_id = right_table.id "
    + " ORDER BY asset_id ";


  public Observable<Tuple5<Integer, String, Integer, Integer, String>> queryForBasic(int site_id, int hospital_id, Date from, Date to) {

	  return db.select(asset_basic)
			  .parameter("site_id", site_id)
			  .parameter("hospital_id", hospital_id)
			  .parameter("from", from)
			  .parameter("to", to)
			  //       id         |   name      | asset_group  |   dept_id    |  dept_name
			  .getAs(Integer.class, String.class, Integer.class, Integer.class, String.class);
  }

// #2
  private static final String asset_op

    = " SELECT asset_id as id, SUM(expose_count) as exposure, SUM(exam_count) as scan, SUM(exam_duration)/3600.0 as usage, SUM(down_time) as stop, SUM(work_order_count) as fix "
    + " FROM asset_summit "
    + " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to "
    + " GROUP BY asset_id "
    + " ORDER BY asset_id ";

  public Observable<Tuple6<Integer, Double, Integer, Double, Integer, Integer>> queryForOp(int site_id, int hospital_id, Date from, Date to) {

	  return db.select(asset_op)
			  .parameter("site_id", site_id)
			  .parameter("hospital_id", hospital_id)
			  .parameter("from", from)
			  .parameter("to", to)
			  //    |    id       |   exposure   |    scan     |    usage     |    stop      |     fix     |
			  .getAs(Integer.class, Double.class, Integer.class, Double.class, Integer.class, Integer.class);
  }


// #3
  private static final String asset_roi

    = " SELECT asset_id as id, SUM(revenue) as revenue, SUM(revenue-maintenance_cost-deprecation_cost) as profit, COUNT(*) as day "
    + " FROM asset_summit "
    + " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to "
    + " GROUP BY asset_id "
    + " ORDER BY asset_id ";

  public Observable<Tuple4<Integer, Double, Double, Integer>> queryForRoi(int site_id, int hospital_id, Date from, Date to) {

	  return db.select(asset_roi)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.parameter("from", from)
			.parameter("to", to)
			//    |     id      |   revenue   |   profit    |   day   |
			.getAs(Integer.class, Double.class, Double.class, Integer.class);
  }

// #4
  private static final String asset_bm

    = " SELECT left_table.asset_id as id, exposure, scan, usage, stop, fix, profit "
    + " FROM "
        + " (SELECT DISTINCT asset_id, asset_group FROM asset_summit WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to ) left_table "
        + " LEFT JOIN "
        + " (SELECT asset_group, AVG(expose_count) as exposure, AVG(exam_count) as scan, AVG(exam_duration)/3600.0 as usage, AVG(down_time)/86400 as stop, AVG(work_order_count) as fix, AVG(revenue - maintenance_cost - deprecation_cost) as profit "
            + " FROM asset_summit "
            + " WHERE created >= :from AND created <= :to "
            + " GROUP BY asset_group ) right_table "
        + " ON left_table.asset_group = right_table.asset_group "
    + " ORDER BY left_table.asset_id ";

  public Observable<Tuple7<Integer, Double, Integer, Double, Double, Integer, Double>> queryForBm(int site_id, int hospital_id, Date from, Date to) {

	  return db.select(asset_bm)
			  .parameter("site_id", site_id)
			  .parameter("hospital_id", hospital_id)
			  .parameter("from", from)
			  .parameter("to", to)
			  //    |     id      |   exposure   |    scan     |    usage     |    stop      |     fix     |    Profit   |
			  .getAs(Integer.class, Double.class, Integer.class, Double.class, Double.class, Integer.class, Double.class);

  }


// #5
  private static final String asset_count

    = " SELECT COUNT(DISTINCT asset_id) count "
    + " FROM asset_summit "
    + " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to ";

  public Integer queryForCount(int site_id, int hospital_id, Date from, Date to) {

	  return db.select(asset_count)
			  .parameter("site_id", site_id)
			  .parameter("hospital_id", hospital_id)
			  .parameter("from", from)
			  .parameter("to", to)
			  .getAs(Integer.class)
			  .first()
			  .toBlocking()
			  .single();
  }





// #6
  private static final String asset_tick_min

  = " SELECT MIN(expose_sum) as exposure_min, "
          + " MIN(scan_sum) as scan_min, "
          + " MIN(usage_sum) as usage_min, "
          + " MIN(stop_sum) as stop_min, "
          + " MIN(fix_sum) as fix_min, "
          + " MIN(profix_sum) as profix_min "
  + "FROM "
  + " ( SELECT asset_id, SUM(expose_count) as expose_sum, "
          + " SUM(exam_count) as scan_sum, "
          + " SUM(exam_duration)/3600.0 as usage_sum, "
          + " SUM(down_time) as stop_sum, "
          + " SUM(work_order_count) as fix_sum, "
          + " SUM(revenue - maintenance_cost - deprecation_cost) as profix_sum "
   + " FROM asset_summit "
   + " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to %s %s "
   + " GROUP BY asset_id ) as t1";


  public Tuple6<Double,Integer,Double,Integer,Integer,Double> queryForTickMin(int site_id, int hospital_id, int dept, int type, Date from, Date to) {


	  	String filter_1 = dept == Integer.MIN_VALUE ? "" : " AND dept_id = " + dept;
	  	String filter_2 = type == Integer.MIN_VALUE ? "" : " AND asset_group = " + type;

	    return db.select(String.format(asset_tick_min, filter_1, filter_2 ))
	    		.parameter("site_id", site_id)
	    		.parameter("hospital_id", hospital_id)
	    		.parameter("from", from)
	    		.parameter("to", to)
	    		.getAs(Double.class, Integer.class, Double.class, Integer.class, Integer.class, Double.class)
	    		.first()
	    		.toBlocking()
	    		.single();
}

//#7
 private static final String asset_tick_max

 	= " SELECT MAX(expose_sum) as exposure_max, "
    + " MAX(scan_sum) as scan_max, "
    + " MAX(usage_sum) as usage_max, "
    + " MAX(stop_sum) as stop_max, "
    + " MAX(fix_sum) as fix_max, "
    + " MAX(profix_sum) as profix_max "
    + "FROM "
          + " ( SELECT asset_id, SUM(expose_count) as expose_sum, "
          + " SUM(exam_count) as scan_sum, "
          + " SUM(exam_duration)/3600.0 as usage_sum, "
          + " SUM(down_time) as stop_sum, "
          + " SUM(work_order_count) as fix_sum, "
          + " SUM(revenue - maintenance_cost - deprecation_cost) as profix_sum "
          + " FROM asset_summit "
          + " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to %s %s "
          + " GROUP BY asset_id ) as t1";

  public Tuple6<Double,Integer,Double,Integer,Integer,Double> queryForTickMax(int site_id, int hospital_id, int dept, int type, Date from, Date to) {



	  	String filter_1 = dept == Integer.MIN_VALUE ? "" : " AND dept_id = " + dept;
	  	String filter_2 = type == Integer.MIN_VALUE ? "" : " AND asset_group = " + type;

	    return db.select(String.format(asset_tick_max, filter_1, filter_2 ))
	    		.parameter("site_id", site_id)
	    		.parameter("hospital_id", hospital_id)
	    		.parameter("from", from)
	    		.parameter("to", to)
	    		.getAs(Double.class, Integer.class, Double.class, Integer.class, Integer.class, Double.class)
	    		.first()
	    		.toBlocking()
	    		.single();
  }

}

