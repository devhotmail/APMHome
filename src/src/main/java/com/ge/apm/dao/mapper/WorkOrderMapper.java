package com.ge.apm.dao.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.ge.apm.domain.WorkFlow;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkflowConfig;

public interface WorkOrderMapper {
	@Select("select count(wo.id) from asset_info ai left join work_order wo on ai.id= wo.asset_id "+
				  "where date(wo.request_time)=date(#{day}) and ai.id=#{assetId}")
	public Integer fetchWorkOrdersByAssetId(@Param("assetId") Integer assetId,@Param("day") Date day);

	@Select("select sum(total_price) from work_order wo where asset_id=#{assetId} and wo.status = 2 and "+
					"date(wo.request_time)=date(#{day}) group by date(#{day})")
	public Double fetchWorkOrderCost(@Param("assetId") Integer assetId,@Param("day") Date day);
	
	@Select("select  id,site_id,hospital_id,current_person_id,current_step_id,parent_wo_id from work_order where status = 1 ")
	public List<WorkOrder> fetchUnFinishedWorkList();
	
	@Select("select  owner_id ,start_time,work_order_id from work_order_step where step_id <= #{currentStepId} and work_order_id = #{id} "+
	"order by start_time desc ")
	public List<WorkFlow> fetchWorkFlowList(WorkOrder workOrder);

	@Select("select * from workflow_config where site_id =#{siteId} and hospital_id =#{hospitalId}")
	public WorkflowConfig fetchWorkflowConfig(@Param("siteId") Integer siteId,@Param("hospitalId") Integer hospitalId);
	
	@Select("select id ,request_time from work_order  where id = #{parentWoId}")
	public WorkOrder fetchParentWorkOrder(Integer parentWoId);

}
