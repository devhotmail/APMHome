package com.ge.apm.dao.mapper.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.domain.AssetCostStatistics;

public class AssetCostStatisticsProvider {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public String updateAssetCostStatistics(AssetCostStatistics assetCostStatistics){
		StringBuilder sb = new StringBuilder();
		sb.append("update asset_summit set id=id, ");
		if(intIsNotNull(assetCostStatistics.getSiteId())){
			sb.append(" site_id = #{siteId},");
		}
		if(intIsNotNull(assetCostStatistics.getHospitalId())){
			sb.append(" hospital_id = #{hospitalId},");
		}
		if(intIsNotNull(assetCostStatistics.getAssetGroup())){
			sb.append(" asset_group = #{assetGroup},");
		}
		if(intIsNotNull(assetCostStatistics.getDeptId())){
			sb.append(" dept_id = #{deptId},");
		}
		if(assetCostStatistics.getAssetName() != null){
			sb.append(" asset_name = #{assetName},");
		}
		if(intIsNotNull(assetCostStatistics.getSupplierId())){
			sb.append(" supplier_id = #{supplierId},");
		}
		if(doubleIsNotNull(assetCostStatistics.getMaintenanceCost())){
			sb.append(" maintenance_cost = #{maintenanceCost},");
		}
		if(doubleIsNotNull(assetCostStatistics.getDeprecationCost())){
			sb.append(" deprecation_cost = #{deprecationCost},");
		}
		if(intIsNotNull(assetCostStatistics.getDownTime())){
			sb.append(" down_time =#{downTime},");
		}
		if(intIsNotNull(assetCostStatistics.getWorkOrderCount())){
			sb.append(" work_order_count =#{workOrderCount}");
		}
		sb.append("where asset_id = #{assetId} and created = date(now())");
		if(logger.isInfoEnabled()){
			logger.info("updateAssetCostStatistics sql is {}",sb.toString());
		}
		return sb.toString();
	}
	
	public static boolean intIsNotNull(Integer i){
		if(i == null){
			return false;
		}
		return true;
	}
	public static boolean doubleIsNotNull(Double i){
		if(i == null){
			return false;
		}
		return true;
	}
}
