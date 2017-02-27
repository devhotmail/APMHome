package com.ge.apm.dao.mapper.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetInfoProvider {
	Logger logger = LoggerFactory.getLogger(getClass());
	public String fetchAssetInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("ai.id asset_id,ai.site_id,ai.hospital_id,ai.asset_group,ai.asset_dept_id dept_id,ai.status,");
		sb.append("wo.request_time,wo.confirmed_down_time down_time,wo.is_closed wo_status,wo.id wo_id ");
		sb.append("from asset_info ai join work_order wo on ");
		sb.append("ai.id = wo.asset_id and ai.is_valid=true");
		if(logger.isInfoEnabled()){
			logger.info("fetchAssetInfo sql is {}",sb.toString());
		}
		return sb.toString();
	}
	
	public String fetchDownTimeAsset(){
		StringBuilder sb = new StringBuilder();
		sb.append("select ai. id asset_id,wo.request_time,wo.confirmed_down_time down_time from ");
		sb.append("asset_info ai join work_order wo on ai. id = wo.asset_id and ai.is_valid = true ");
		sb.append("where ai.status = 2 and date(wo.request_time)=date(now()) ");
		sb.append("group by ai. id,wo.confirmed_down_time,wo.request_time ");
		sb.append("order by ai. id asc,wo.confirmed_down_time asc,wo.request_time asc ");
		if(logger.isInfoEnabled()){
			logger.info("fetchDownTimeAsset sql is {}",sb.toString());
		}
		return sb.toString();
	}
	
	public String fetchAssetCostStatisticsByAssetId(Integer assetId){
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}
}
