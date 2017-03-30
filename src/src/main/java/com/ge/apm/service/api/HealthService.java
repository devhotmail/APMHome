package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;

import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
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
    	= " SELECT asset_id, asset_name, current_step_id, current_step_name, case_owner_name "
    	+ " FROM work_order "
    	+ " WHERE is_closed=false AND site_id=:site_id AND hospital_id=:hospital_id ";

	public Observable<Tuple5<Integer, String, Integer, String, String>> queryForMaintain(Integer site_id, Integer hospital_id) {

		return db
			.select(asset_maintain)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple5<Integer, String, Integer, String, String> (
					rs.getInt("asset_id"), rs.getString("asset_name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("case_owner_name") ) )
			.cache();

	}


    private static final String asset_outage
    	= " SELECT asset_info.id, asset_info.name, to_char(inner_2.create_time, 'YYYY-MM-DD'), inner_2.case_type "
    	+ " FROM "
    		+ " asset_info "
    		+ " LEFT JOIN "
    		+ " (SELECT inner_1.asset_id, inner_1.create_time, work_order.case_type "
    		+ " FROM "
    			+ " ( SELECT asset_id, MAX(create_time) AS create_time FROM work_order GROUP BY asset_id) AS inner_1 "
    			+ " INNER JOIN work_order "
    			+ " ON inner_1.asset_id = work_order.asset_id "
    			+ " WHERE inner_1.create_time = work_order.create_time ) AS inner_2 "
    		+ " ON asset_info.id = inner_2.asset_id "
    		+ " WHERE asset_info.is_valid = true AND asset_info.status = 2 AND asset_info.site_id = :site_id AND asset_info.hospital_id = :hospital_id "
    		+ " ORDER BY to_char, id, case_type ";

	public Observable<Tuple4<Integer, String, String, Integer>> queryForOutage(Integer site_id, Integer hospital_id) {

		return db
			.select(asset_outage)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple4<Integer, String, String, Integer> (
					rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type") ) )
			.cache();

	}


    private static final String asset_warranty
    	= " SELECT id, name, to_char(warranty_date, 'YYYY-MM-DD') "
    	+ " FROM asset_info "
    	+ " WHERE is_valid = true AND warranty_date <= (NOW() + interval '2 months') AND site_id = :site_id AND hospital_id = :hospital_id "
    	+ " ORDER BY warranty_date ";

	public Observable<Tuple3<Integer, String, String>> queryForWarranty(Integer site_id, Integer hospital_id) {

		return db
			.select(asset_warranty)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple3<Integer, String, String> (
					rs.getInt("id"), rs.getString("name"), rs.getString("to_char") ) )
			.cache();

	}


    private static final String asset_pm
    	= " SELECT asset_id, asset_name, to_char(start_time, 'YYYY-MM-DD') "
    	+ " FROM pm_order "
    	+ " WHERE site_id=:site_id AND hospital_id=:hospital_id AND is_finished=false AND start_time<(NOW() + interval '1 weeks') "
    	+ " ORDER BY start_time ";

	public Observable<Tuple3<Integer, String, String>> queryForPm(Integer site_id, Integer hospital_id) {

		return db
			.select(asset_pm)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple3<Integer, String, String> (
					rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char") ) )
			.cache();

	}


    private static final String assets_meterqa
    	= " SELECT DISTINCT asset_id, asset_name, to_char(start_time, 'YYYY-MM-DD') "
    	+ " FROM inspection_order "
    	+ " INNER JOIN inspection_order_detail "
    	+ " ON inspection_order.id = inspection_order_detail.order_id "
    	+ " WHERE inspection_order.site_id = :site_id AND inspection_order.hospital_id=:hospital_id AND start_time<=(NOW() + interval '2 months') AND is_finished=false AND order_type=%s "
    	+ " ORDER BY to_char ";

	public Observable<Tuple3<Integer, String, String>> queryForMeterqa(Integer site_id, Integer hospital_id, Integer meterqa) {

		return db
			.select(String.format(assets_meterqa, meterqa))
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple3<Integer, String, String> (
					rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char") ) )
			.cache();

	}

}
