/*
 */
package com.ge.apm.service.analysis;

import com.ge.apm.dao.AssetDepreciationRepository;
import com.ge.apm.dao.WorkOrderStepDetailRepository;
import com.ge.apm.domain.AssetCostStatistics;
import com.ge.apm.domain.DownTimeAsset;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 212547631
 */
@Component
public class AssetCostDataAggregator {
	
	private final static Logger logger = LoggerFactory.getLogger(AssetCostDataAggregator.class);
	private static final int ASSET_STATUS_DOWN = 2;
	private static final int ONE_DAY = 24 * 60 * 60;
	
    @Autowired
    private AssetDepreciationRepository depreciationDao;

    @Autowired
	private WorkOrderStepDetailRepository woStepDetailDao;

	@Autowired
	private JdbcTemplate jdbcTemplate;
    
    private static final String QUERY_ASSET_INFO =
    		"select ai.id asset_id,ai.site_id,ai.hospital_id,ai.asset_group,ai.asset_dept_id dept_id,"+
    		"wo.id woId from asset_info ai join work_order wo on ai.id = wo.asset_id and ai.is_valid=true";
    private static final String QUERY_DOWN_TIME_ASSET=
    		"select ai. id,wo.request_time,wo.confirmed_down_time from"+
    		"asset_info ai join work_order wo on ai. id = wo.asset_id and ai.is_valid = true"+
    		"where ai.status = 2 and date(wo.request_time)=date(now())"+
    		"group by ai. id,wo.confirmed_down_time,wo.request_time"+
    		"order by ai. id asc,wo.confirmed_down_time asc,wo.request_time asc";
    private static final String QUERY_WORK_ORDER_NUM = 
    		"select count(wo.id) from asset_info ai left join work_order wo on ai.id= wo.asset_id"+
    		"where date(wo.request_time)=date(now()) and ai.id=?";
    
    public void aggregateCostData(){
    	if(logger.isDebugEnabled()){
    		logger.debug("query asset info sql is ",QUERY_ASSET_INFO);
    	}
    	// 1、获取资产名称
    	List<AssetCostStatistics> assetInfos = getAssetInfos();
    	if(CollectionUtils.isEmpty(assetInfos)){
			logger.error("assetInfos is empty");
			return;
    	}
    	/***
    	 * 查出当天所有宕机资产，找出最早的一条，然后以该条记录的时间计算宕机时间
    	 */
    	List<DownTimeAsset> downTimeAsset = getDownTimeAsset();
    	Map<Integer,DownTimeAsset> map = new HashMap<Integer,DownTimeAsset>();
    	if(CollectionUtils.isNotEmpty(downTimeAsset)){
    		for (DownTimeAsset dta : downTimeAsset) {
				map.put(dta.getAssetId(), dta);
			}
    	}
    	for (AssetCostStatistics acs : assetInfos) {
    		if(map.containsKey(acs.getAssetId())){
    			if(map.get(acs.getAssetId()).getIsCal()){
    				continue;
    			}else{
//    				acs.setRequestTime(map.get(acs.getAssetId()).getRequestTime());
//    				acs.setConfirmedDownTime(map.get(acs.getAssetId()).getDownTime());
    			}
    		}
//    		excute(acs);
    		if(map.containsKey(acs.getAssetId())){
    			DownTimeAsset dta = map.get(acs.getAssetId());
    			dta.setIsCal(true);
    			map.put(acs.getAssetId(), dta);
    		}
    	}
    	
    }
    
    private List<DownTimeAsset> getDownTimeAsset() {
    	return jdbcTemplate.query(QUERY_DOWN_TIME_ASSET, new RowMapper<DownTimeAsset>(){
			@Override
			public DownTimeAsset mapRow(ResultSet rs, int i) throws SQLException {
				DownTimeAsset dta = new DownTimeAsset();
				dta.setAssetId(rs.getInt("asset_id"));
//				dta.setRequestTime(rs.getDate("request_time") == null ?null:new DateTime(rs.getDate("request_time").getTime()));
//				dta.setDownTime(rs.getDate("confirmed_down_time")== null ?null:new DateTime(rs.getDate("confirmed_down_time").getTime()));
				return dta;
			}
    	});
	}

	public List<AssetCostStatistics> getAssetInfos(){
    	List<AssetCostStatistics> assetInfos = jdbcTemplate.query(QUERY_ASSET_INFO, new RowMapper<AssetCostStatistics>(){
			@Override
			public AssetCostStatistics mapRow(ResultSet rs, int i) throws SQLException {
				AssetCostStatistics acs = new AssetCostStatistics();
				acs.setAssetId(rs.getInt("asset_id"));
				acs.setSiteId(rs.getInt("site_id"));
				acs.setHospitalId(rs.getInt("hospital_id"));
				acs.setAssetGroup(rs.getInt("asset_group"));
				acs.setDeptId(rs.getInt("dept_id"));
				acs.setStatus(rs.getInt("status"));
//				acs.setRequestTime(rs.getDate("request_time") == null ?null:new DateTime(rs.getDate("request_time").getTime()));
//				acs.setConfirmedDownTime(rs.getDate("confirmed_down_time")== null ?null:new DateTime(rs.getDate("confirmed_down_time").getTime()));
				return acs;
			}
    	});
    	return assetInfos;
    }
    
//    public void excute(AssetCostStatistics assetCostStatistics ){
//    	//1、查询资产
//    	//2、计算宕机时间
//		DateTime confirmDownTime = null;//assetCostStatistics.getConfirmedDownTime();
//		DateTime requestTime = null;//assetCostStatistics.getRequestTime();
//		if(assetCostStatistics.getStatus().intValue() == ASSET_STATUS_DOWN){
//			DateTime now =new DateTime();
//			if(confirmDownTime != null){
//				if(Days.daysBetween(now, confirmDownTime).getDays()>1){
//					assetCostStatistics.setDownTime(ONE_DAY);
//				}else{
//					assetCostStatistics.setDownTime(Seconds.secondsBetween(now, confirmDownTime).getSeconds());
//				}
//			}else{
//				if(Days.daysBetween(now, requestTime).getDays()>1){
//					assetCostStatistics.setDownTime(ONE_DAY);
//				}else{
//					assetCostStatistics.setDownTime(Seconds.secondsBetween(now, requestTime).getSeconds());
//				}
//			}
//		}else{
//			assetCostStatistics.setDownTime(0);
//		}
//		assetCostStatistics.setWorkOrderCount(getWorkOrderCount(assetCostStatistics.getAssetId()));
//    	//3、计算维修费用
//		
//    	//4、计算折旧费用
//    }
    
    private Integer getWorkOrderCount(int assetId) {
    	return jdbcTemplate.queryForObject(QUERY_WORK_ORDER_NUM, Integer.TYPE,assetId);
	}

	public void aggregateCostDataByAssetId(int assetId,String whichDay){
    	
    }
}
