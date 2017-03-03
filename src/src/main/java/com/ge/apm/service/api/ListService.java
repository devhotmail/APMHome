package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;

import org.simpleflatmapper.tuple.Tuple12;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuple6;
import org.simpleflatmapper.tuple.Tuple8;
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
			  //       asset_id      |   name      |     type      |   dept_id    |  dept_name
			  //.getAs(Integer.class, String.class, Integer.class, Integer.class, String.class);
			  .get(rs -> 
			  		new Tuple5<Integer, String, Integer, Integer, String> (
			  				rs.getInt("asset_id"), rs.getString("name"), rs.getInt("type"), rs.getInt("dept_id"), rs.getString("dept") ) );
			  
  }

// #2
  private static final String asset_op

    = " SELECT SUM(expose_count) as exposure, SUM(exam_count) as scan, SUM(exam_duration)/3600.0 as usage, SUM(down_time)/86400 as stop, SUM(work_order_count) as fix, SUM(revenue) as revenue, SUM(revenue-maintenance_cost-deprecation_cost) as profit, COUNT(*) as day "
    + " FROM asset_summit "
    + " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to "
    + " GROUP BY asset_id "
    + " ORDER BY asset_id ";

  public Observable<Tuple8<Double, Integer, Double, Double, Integer, Double, Double, Integer>> queryForOp(int site_id, int hospital_id, Date from, Date to) {

	  return db.select(asset_op)
			  .parameter("site_id", site_id)
			  .parameter("hospital_id", hospital_id)
			  .parameter("from", from)
			  .parameter("to", to)
			  //    |   exposure   |    scan     |    usage     |    stop      |     fix     |   revenue    |   profit    |    day     |
			  //.getAs(Double.class, Integer.class, Double.class, Integer.class, Integer.class, Double.class, Double.class, Integer.class);
			  .get(rs -> 
			  		new Tuple8<Double, Integer, Double, Double, Integer, Double, Double, Integer> (
			  				rs.getDouble("exposure"), rs.getInt("scan"), rs.getDouble("usage"), 100.0*rs.getDouble("stop")/rs.getInt("day"), rs.getInt("fix"), rs.getDouble("revenue"), rs.getDouble("profit"), rs.getInt("day") ) );
			  
  }


// #3
  private static final String asset_bm

    = " SELECT exposure, scan, usage, stop, fix, profit "
    + " FROM "
        + " (SELECT DISTINCT asset_id, asset_group FROM asset_summit WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to ) left_table "
        + " LEFT JOIN "
        + " (SELECT asset_group, AVG(expose_count) as exposure, AVG(exam_count) as scan, AVG(exam_duration)/3600.0 as usage, AVG(down_time)/86400 as stop, AVG(work_order_count) as fix, AVG(revenue - maintenance_cost - deprecation_cost) as profit "
            + " FROM asset_summit "
            + " WHERE created >= :from AND created <= :to "
            + " GROUP BY asset_group ) right_table "
        + " ON left_table.asset_group = right_table.asset_group "
    + " ORDER BY left_table.asset_id ";

  public Observable<Tuple6<Double, Integer, Double, Double, Integer, Double>> queryForBm(int site_id, int hospital_id, Date from, Date to) {

	  return db.select(asset_bm)
			  .parameter("site_id", site_id)
			  .parameter("hospital_id", hospital_id)
			  .parameter("from", from)
			  .parameter("to", to)
			  //    |   exposure   |    scan     |    usage     |    stop    |     fix     |    Profit   |
			  //.getAs(Double.class, Integer.class, Double.class, Double.class, Integer.class, Double.class);
			  .get(rs -> 
		  		new Tuple6<Double, Integer, Double, Double, Integer, Double> (
		  				rs.getDouble("exposure"), rs.getInt("scan"), rs.getDouble("usage"), 100.0*rs.getDouble("stop"), rs.getInt("fix"), rs.getDouble("profit") ) );

  }


// #5
  private static final String asset_count

    = " SELECT COUNT(DISTINCT asset_id) count "
    + " FROM asset_summit "
    + " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to  %s %s ";

  public Integer queryForCount(int site_id, int hospital_id, int dept, int type, Date from, Date to) {

	  	String filter_1 = dept == Integer.MIN_VALUE ? "" : " AND dept_id = " + dept;
	  	String filter_2 = type == Integer.MIN_VALUE ? "" : " AND asset_group = " + type;
	  	
	  	return db.select(String.format(asset_count, filter_1, filter_2 ))
			  .parameter("site_id", site_id)
			  .parameter("hospital_id", hospital_id)
			  .parameter("from", from)
			  .parameter("to", to)
			  .getAs(Integer.class)
			  .first()
			  .toBlocking()
			  .single();
  }


//#7
 private static final String asset_tick

 	= " SELECT MIN(expose_sum) as exposure_min, MAX(expose_sum) as exposure_max, "
    + " MIN(scan_sum) as scan_min, MAX(scan_sum) as scan_max, "
    + " MIN(usage_sum) as usage_min, MAX(usage_sum) as usage_max, "
    + " MIN(stop_sum) as stop_min, MAX(stop_sum) as stop_max, "
    + " MIN(fix_sum) as fix_min, MAX(fix_sum) as fix_max, "
    + " MIN(profix_sum) as profix_min, MAX(profix_sum) as profix_max "
    + "FROM "
          + " ( SELECT asset_id, SUM(expose_count) as expose_sum, "
          + " SUM(exam_count) as scan_sum, "
          + " SUM(exam_duration)/3600.0 as usage_sum, "
          + " SUM(down_time)/86400.0 as stop_sum, "
          + " SUM(work_order_count) as fix_sum, "
          + " SUM(revenue - maintenance_cost - deprecation_cost) as profix_sum "
          + " FROM asset_summit "
          + " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to %s %s "
          + " GROUP BY asset_id ) as t1";

  public Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double> queryForMinMax(int site_id, int hospital_id, int dept, int type, Date from, Date to) {

	  	String filter_1 = dept == Integer.MIN_VALUE ? "" : " AND dept_id = " + dept;
	  	String filter_2 = type == Integer.MIN_VALUE ? "" : " AND asset_group = " + type;

	    return db.select(String.format(asset_tick, filter_1, filter_2) )
	    		.parameter("site_id", site_id)
	    		.parameter("hospital_id", hospital_id)
	    		.parameter("from", from)
	    		.parameter("to", to)
	    		.get(rs -> 
	    			new Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double>(
	    					rs.getDouble("exposure_min"), rs.getDouble("exposure_max"),
	    					rs.getInt("scan_min"), rs.getInt("scan_max"),
	    					rs.getDouble("usage_min"), rs.getDouble("usage_max"),
	    					rs.getDouble("stop_min"), rs.getDouble("stop_max"),
	    					rs.getInt("fix_min"), rs.getInt("fix_max"), 
	    					rs.getDouble("profix_min"), rs.getDouble("profix_max")
	    					) )
	    		.first()
	    		.toBlocking()
	    		.single();

  }
  
  
  private static final String asset_bm_tick

	  = " SELECT MIN(exposure) as exposure_min, MAX(exposure) as exposure_max, "
		    + " MIN(scan) as scan_min, MAX(scan) as scan_max, "
		    + " MIN(usage) as usage_min, MAX(usage) as usage_max, "
		    + " MIN(stop) as stop_min, MAX(stop) as stop_max, "
		    + " MIN(fix) as fix_min, MAX(fix) as fix_max, "
		    + " MIN(profit) as profix_min, MAX(profit) as profix_max "
	  + " FROM "
      + " (SELECT asset_group, AVG(expose_count) as exposure, AVG(exam_count) as scan, AVG(exam_duration)/3600.0 as usage, AVG(down_time)/86400 as stop, AVG(work_order_count) as fix, AVG(revenue - maintenance_cost - deprecation_cost) as profit "
          + " FROM asset_summit "
          + " WHERE created >= :from AND created <= :to %s "
          + " GROUP BY asset_group ) as t1 ";
          
          
  public Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double> queryForBmTick(int site_id, int hospital_id, int dept, int type, Date from, Date to) {

	  	String filter_2 = type == Integer.MIN_VALUE ? "" : " AND asset_group = " + type;

	    return db.select(String.format(asset_bm_tick, filter_2) )
	    		.parameter("from", from)
	    		.parameter("to", to)
	    		.get(rs -> 
	    			new Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double>(
	    					rs.getDouble("exposure_min"), rs.getDouble("exposure_max"),
	    					rs.getInt("scan_min"), rs.getInt("scan_max"),
	    					rs.getDouble("usage_min"), rs.getDouble("usage_max"),
	    					rs.getDouble("stop_min"), rs.getDouble("stop_max"),
	    					rs.getInt("fix_min"), rs.getInt("fix_max"), 
	    					rs.getDouble("profix_min"), rs.getDouble("profix_max")
	    					) )
	    		.first()
	    		.toBlocking()
	    		.single();

  }  
  
  
  public Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double> queryForTick (
		  Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double> tick1,
		  Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double> tick2
		  ) {
	  
	  return new Tuple12<Double, Double, Integer, Integer, Double, Double, Double, Double, Integer, Integer, Double, Double> (
			  
			  Math.min(tick1.getElement0(), tick2.getElement0()), Math.min(tick1.getElement1(), tick2.getElement1()),
			  Math.min(tick1.getElement2(), tick2.getElement2()), Math.min(tick1.getElement3(), tick2.getElement3()),
			  Math.min(tick1.getElement4(), tick2.getElement4()), Math.min(tick1.getElement5(), tick2.getElement5()),
			  Math.min(tick1.getElement6(), tick2.getElement6()), Math.min(tick1.getElement7(), tick2.getElement7()),
			  Math.min(tick1.getElement8(), tick2.getElement8()), Math.min(tick1.getElement9(), tick2.getElement9()),
			  Math.min(tick1.getElement10(), tick2.getElement10()), Math.min(tick1.getElement11(), tick2.getElement11() ) );
	  
  }
  

  
  
}














