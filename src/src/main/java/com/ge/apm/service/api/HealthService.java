package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuple6;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class HealthService {

	private Database db;

	@Resource(name = "connectionProvider")
	private ConnectionProvider connectionProvider;

	@PostConstruct
	public void init() {
		db = Database.from(connectionProvider);
	}



    private static final String asset_maintain
    	= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id "
    	+ " FROM v2_work_order "
    	+ " LEFT JOIN asset_info "
    	+ " ON v2_work_order.asset_id = asset_info.id "
    	+ " WHERE v2_work_order.status = 1 AND asset_info.site_id=:site_id AND asset_info.hospital_id=:hospital_id ";

	public Observable<Tuple6<Integer, String, Integer, String, String, Integer>> queryForMaintain(Integer site_id, Integer hospital_id) {

		return db
			.select(asset_maintain)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple6<Integer, String, Integer, String, String, Integer> (
					rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
			.cache();

	}

	public Observable<Tuple6<Integer, String, Integer, String, String, Integer>> queryForMaintain(Integer site_id, Integer hospital_id, Integer dept) {

		return db
			.select(asset_maintain)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple6<Integer, String, Integer, String, String, Integer> (
					rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
			.filter(t6 -> t6.getElement5()==dept)
			.cache();

	}


    private static final String asset_outage
    	= " SELECT asset_info.id, asset_info.name, to_char(inner_2.created_date, 'YYYY-MM-DD'), inner_2.int_ext_type as case_type, asset_info.clinical_dept_id "
    	+ " FROM "
    		+ " asset_info "
    		+ " LEFT JOIN "
    		+ " (SELECT inner_1.asset_id, inner_1.created_date, v2_work_order.int_ext_type "
    		+ " FROM "
    			+ " ( SELECT asset_id, MAX(created_date) AS created_date FROM v2_work_order GROUP BY asset_id) AS inner_1 "
    			+ " INNER JOIN v2_work_order "
    			+ " ON inner_1.asset_id = v2_work_order.asset_id "
    			+ " WHERE inner_1.created_date = v2_work_order.created_date ) AS inner_2 "
    		+ " ON asset_info.id = inner_2.asset_id "
    		+ " WHERE asset_info.is_valid = true AND asset_info.status = 2 AND asset_info.site_id = :site_id AND asset_info.hospital_id = :hospital_id "
    		+ " ORDER BY to_char, id, int_ext_type ";

	public Observable<Tuple5<Integer, String, String, Integer, Integer>> queryForOutage(Integer site_id, Integer hospital_id) {

		return db
			.select(asset_outage)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple5<Integer, String, String, Integer, Integer> (
					rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
			.cache();

	}

	public Observable<Tuple5<Integer, String, String, Integer, Integer>> queryForOutage(Integer site_id, Integer hospital_id, Integer dept) {

		return db
			.select(asset_outage)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple5<Integer, String, String, Integer, Integer> (
					rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
			.filter(t5 -> t5.getElement4()==dept)
			.cache();

	}

    private static final String asset_warranty
    	= " SELECT id, name, to_char(warranty_date, 'YYYY-MM-DD'), clinical_dept_id "
    	+ " FROM asset_info "
    	+ " WHERE is_valid = true AND warranty_date <= (NOW() + interval '2 months') AND site_id = :site_id AND hospital_id = :hospital_id "
    	+ " ORDER BY warranty_date ";

	public Observable<Tuple4<Integer, String, String, Integer>> queryForWarranty(Integer site_id, Integer hospital_id) {

		return db
			.select(asset_warranty)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple4<Integer, String, String, Integer> (
					rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
			.cache();

	}

	public Observable<Tuple4<Integer, String, String, Integer>> queryForWarranty(Integer site_id, Integer hospital_id, Integer dept) {

		return db
			.select(asset_warranty)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple4<Integer, String, String, Integer> (
					rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
			.filter(t4 -> t4.getElement3()==dept)
			.cache();

	}

    private static final String asset_pm
    	/*= " SELECT asset_id, asset_name, to_char(start_time, 'YYYY-MM-DD'), asset_info.clinical_dept_id "
    	+ " FROM pm_order "
    	+ " LEFT JOIN asset_info "
    	+ " ON pm_order.asset_id = asset_info.id "
    	+ " WHERE asset_info.site_id=:site_id AND asset_info.hospital_id=:hospital_id AND pm_order.is_finished=false AND pm_order.start_time<(NOW() + interval '1 weeks') "
    	+ " ORDER BY start_time ";*/
			= " SELECT asset_id, asset_name, to_char(plan_time, 'YYYY-MM-DD'), asset_info.clinical_dept_id "
			+ " FROM pm_order "
			+ " LEFT JOIN asset_info "
			+ " ON pm_order.asset_id = asset_info.id "
			+ " WHERE asset_info.site_id=:site_id AND asset_info.hospital_id=:hospital_id AND pm_order.is_finished=false AND pm_order.plan_time<(NOW() + interval '1 weeks') "
			+ " ORDER BY plan_time ";

	public Observable<Tuple4<Integer, String, String, Integer>> queryForPm(Integer site_id, Integer hospital_id) {

		return db
			.select(asset_pm)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple4<Integer, String, String, Integer> (
					rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
			.cache();

	}

	public Observable<Tuple4<Integer, String, String, Integer>> queryForPm(Integer site_id, Integer hospital_id, Integer dept) {

		return db
			.select(asset_pm)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple4<Integer, String, String, Integer> (
					rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
			.filter(t4 -> t4.getElement3()==dept)
			.cache();

	}

    private static final String assets_meterqa
    	/*= " SELECT left_table.asset_id, left_table.asset_name, left_table.to_char, asset_info.clinical_dept_id "
    	+ " FROM ( SELECT DISTINCT asset_id, asset_name, to_char(start_time, 'YYYY-MM-DD') "
    	+ " FROM inspection_order "
    	+ " INNER JOIN inspection_order_detail "
    	+ " ON inspection_order.id = inspection_order_detail.order_id "
    	+ " WHERE inspection_order.site_id = :site_id AND inspection_order.hospital_id=:hospital_id AND start_time<=(NOW() + interval '2 months') AND is_finished=false AND order_type=%s "
    	+ " ORDER BY to_char ) as left_table "
    	+ " LEFT JOIN asset_info "
    	+ " ON left_table.asset_id = asset_info.id "
    	+ " ORDER BY asset_id ";*/
			= " SELECT left_table.asset_id, left_table.asset_name, left_table.to_char, asset_info.clinical_dept_id "
			+ " FROM ( SELECT DISTINCT asset_id, asset_name, to_char(plan_time, 'YYYY-MM-DD') "
			+ " FROM inspection_order "
			+ " INNER JOIN inspection_order_detail "
			+ " ON inspection_order.id = inspection_order_detail.order_id "
			+ " WHERE inspection_order.site_id = :site_id AND inspection_order.hospital_id=:hospital_id AND plan_time<=(NOW() + interval '2 months') AND is_finished=false AND order_type=%s "
			+ " ORDER BY to_char ) as left_table "
			+ " LEFT JOIN asset_info "
			+ " ON left_table.asset_id = asset_info.id "
			+ " ORDER BY asset_id ";

	public Observable<Tuple4<Integer, String, String, Integer>> queryForMeterqa(Integer site_id, Integer hospital_id, Integer meterqa) {

		return db
			.select(String.format(assets_meterqa, meterqa))
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple4<Integer, String, String, Integer> (
					rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
			.cache();

	}

	public Observable<Tuple4<Integer, String, String, Integer>> queryForMeterqa(Integer site_id, Integer hospital_id, Integer meterqa, Integer dept) {

		return db
			.select(String.format(assets_meterqa, meterqa))
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple4<Integer, String, String, Integer> (
					rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
			.filter(t4 -> t4.getElement3()==dept)
			.cache();

	}
}
