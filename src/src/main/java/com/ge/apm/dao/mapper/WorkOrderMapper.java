package com.ge.apm.dao.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface WorkOrderMapper {
	@Select("select count(wo.id) from asset_info ai left join work_order wo on ai.id= wo.asset_id "+
				  "where date(wo.request_time)=date(#{day}) and ai.id=#{assetId}")
	public Integer fetchWorkOrdersByAssetId(@Param("assetId") Integer assetId,@Param("day") Date day);

	@Select("select sum(total_price) from work_order wo where asset_id=#{assetId} and wo.status = 2 and "+
					"date(wo.request_time)=date(#{day}) group by date(#{day})")
	public Double fetchWorkOrderCost(@Param("assetId") Integer assetId,@Param("day") Date day);

}
