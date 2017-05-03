package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;

import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuple6;
import org.simpleflatmapper.tuple.Tuple9;
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


	public Observable<Tuple5<Integer, String, Integer, Integer, String>> queryForBasic(Integer site_id, Integer hospital_id, Date from, Date to) {

		return db.select(asset_basic)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.parameter("from", from)
			.parameter("to", to)
			.get(rs ->
				new Tuple5<Integer, String, Integer, Integer, String> (
					rs.getInt("asset_id"), rs.getString("name"), rs.getInt("type"), rs.getInt("dept_id"), rs.getString("dept") ) );

	}

// #2
	private static final String asset_op
		= " SELECT AVG(rating) as rating, SUM(expose_count) as exposure, SUM(exam_count) as scan, SUM(exam_duration)/3600.0 as usage, SUM(down_time)/86400 as stop, SUM(work_order_count) as fix, SUM(revenue) as revenue, SUM(revenue - maintenance_cost - deprecation_cost) as profit, COUNT(*) as day "
		+ " FROM asset_summit "
		+ " WHERE site_id = :site_id AND hospital_id = :hospital_id AND created >= :from AND created <= :to "
		+ " GROUP BY asset_id "
		+ " ORDER BY asset_id ";

	public Observable<Tuple9<Double, Double, Integer, Double, Double, Integer, Double, Double, Integer>> queryForOp(Integer site_id, Integer hospital_id, Date from, Date to) {

		return db.select(asset_op)
			  .parameter("site_id", site_id)
			  .parameter("hospital_id", hospital_id)
			  .parameter("from", from)
			  .parameter("to", to)
			  //    |   exposure   |    scan     |    usage     |    rate      |     fix     |   revenue    |   profit    |    day     |
			  .get(rs ->
			  		new Tuple9<Double, Double, Integer, Double, Double, Integer, Double, Double, Integer> (
			  				rs.getDouble("rating"), rs.getDouble("exposure"), rs.getInt("scan"), rs.getDouble("usage"), 100.0*(1-rs.getDouble("stop")/rs.getInt("day")), rs.getInt("fix"), rs.getDouble("revenue"), rs.getDouble("profit"), rs.getInt("day") ) );

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

	public Observable<Tuple6<Double, Double, Double, Double, Double, Double>> queryForBm(Integer site_id, Integer hospital_id, Date from, Date to) {

		return db.select(asset_bm)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.parameter("from", from)
			.parameter("to", to)
			.get(rs ->
				new Tuple6<Double, Double, Double, Double, Double, Double> (
				rs.getDouble("exposure"), rs.getDouble("scan"), rs.getDouble("usage"), 100.0*(1-rs.getDouble("stop")), rs.getDouble("fix"), rs.getDouble("profit") ) );

  }

}














