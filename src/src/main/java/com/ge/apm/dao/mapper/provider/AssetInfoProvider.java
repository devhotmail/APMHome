package com.ge.apm.dao.mapper.provider;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.service.utils.TimeUtils;

public class AssetInfoProvider {
	Logger logger = LoggerFactory.getLogger(getClass());
	public String fetchAssetInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append("select asset_id from asset_summit where created = date(now())");
		if(logger.isInfoEnabled()){
			logger.info("fetchAssetInfo sql is {}",sb.toString());
		}
		return sb.toString();
	}
	
	public String fetchAssetCostStatisticsByAssetId(Map<String,Object> param){
		Integer assetId = (Integer) param.get("assetId");
		Date day = (Date) param.get("day");
		StringBuilder sb = new StringBuilder();
		sb.append("select ai. id asset_id,ai.site_id,ai.hospital_id,ai.asset_group,ai.asset_dept_id dept_id,ai.status, ");
		sb.append("wo.request_time,wo.confirmed_down_time,wo.is_closed wo_status,wo.id wo_id ");
		sb.append("from asset_info ai left join work_order wo ");
		sb.append("on ai. id = wo.asset_id and ai. id = "+assetId );
		if(day == null){
			sb.append(" and date(wo.request_time) = date(now()) ");
		}else{
			sb.append(" and date(wo.request_time) = '"+TimeUtils.getStrDate(day,null)+"' ");
		}
		sb.append("order by wo.confirmed_down_time asc,wo.request_time asc limit 1");
		if(logger.isInfoEnabled()){
			logger.info("fetchAssetCostStatisticsByAssetId sql is {}",sb.toString());
		}
		return sb.toString();
	}
	
	public String fetchAssetInfoByDay(Date day){
		StringBuilder sb = new StringBuilder();
		sb.append("select asset_id from asset_summit where created = '"+TimeUtils.getStrDate(day,null)+"' ");
		if(logger.isInfoEnabled()){
			logger.info("fetchAssetInfoByDay sql is {}",sb.toString());
		}
		return sb.toString();
	}
	
	
}
