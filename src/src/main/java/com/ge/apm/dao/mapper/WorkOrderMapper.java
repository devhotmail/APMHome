package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Select;

public interface WorkOrderMapper {
	@Select("select count(wo.id) from asset_info ai left join work_order wo on ai.id= wo.asset_id "+
				  "where date(wo.request_time)=date(now()) and ai.id=#{assetId}")
	public Integer fetchWorkOrdersByAssetId(Integer assetId);

	@Select("select sum(total_price) from work_order wo where asset_id=#{assetId} and wo.is_closed = true and "+
					"date(wo.request_time)=date(now()) GROUP BY date(now())")
	public Double fetchWorkOrderCost(Integer assetId);

}
