package com.ge.apm.dao.mapper.provider;

import com.ge.apm.domain.AssetCostStatistics;

public class AssetCostStatisticsProvider {
	public String updateAssetCostStatistics(AssetCostStatistics assetCostStatistics){
		StringBuilder sb = new StringBuilder();
		sb.append("update asset_submit set ");
		if(intIsNotNull(assetCostStatistics.getSiteId())){
			sb.append("site_id = #{siteId},");
		}
		if(intIsNotNull(assetCostStatistics.getHospitalId())){
			sb.append("hospital_id = #{hospitalId},");
		}
		if(intIsNotNull(assetCostStatistics.getAssetGroup())){
			sb.append("asset_group = #{assetGroup},");
		}
		if(intIsNotNull(assetCostStatistics.getDeptId())){
			sb.append("dept_id = #{deptId},");
		}
		if(assetCostStatistics.getAssetName() != null){
			sb.append("asset_name = #{assetName},");
		}
		if(intIsNotNull(assetCostStatistics.getSupplierId())){
			sb.append("supplier_id = #{supplierId},");
		}
		if(doubleIsNotNull(assetCostStatistics.getMaintenanceCost())){
			sb.append("maintenance_cost = #{maintenanceCost},");
		}
		if(doubleIsNotNull(assetCostStatistics.getDeprecationCost())){
			sb.append("deprecation_cost = #{deprecationCost},");
		}
		if(intIsNotNull(assetCostStatistics.getDownTime())){
			sb.append("down_time =#{downTime},");
		}
		if(intIsNotNull(assetCostStatistics.getWorkOrderCount())){
			sb.append("work_order_count =#{workOrderCount},");
		}
		sb.append("1 = 1 where asset_id = #{assetId}");
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
