package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface WorkOrderRepository extends GenericRepository<WorkOrder> {

    public WorkOrder getByIdAndCurrentPersonId(int id, int currentPersonId);

    public List<WorkOrder> findByAssetId(Integer assetId);

    public List<WorkOrder> findByAssetIdAndIntExtType(Integer assetId, int intExtType);
    public List<WorkOrder> findByStatus(int status);
    public List<WorkOrder>  findByRequestorId(int requestorId);
    public List<WorkOrder> findByRequestorIdAndStatusOrderById(int requestorId, int status);


    @Modifying
    @Query("update WorkOrder wo set wo.currentPersonId=?2 ,wo.currentPersonName=?3 ,wo.currentStepId=?4 where wo.id=?1")
    public void updateWorkderOrder(Integer woId, Integer userId, String userName,Integer stepId);

    //gl:todo 7 是应该可配的
    @Query(value ="select * from Work_Order wo where wo.asset_Id=?1 and wo.hospital_Id=?2 and wo.site_Id=?3 and (wo.close_Time + cast(?4 as interval)) > now() order by wo.id desc", nativeQuery = true)
    public List<WorkOrder> isReopenWorkOrder(Integer assetId,Integer hospitalId,Integer siteId, String reopen);

    public WorkOrder getById(Integer id);
    public WorkOrder findByAssetIdAndStatus(Integer assetId, int status);
    @Query("select wo from WorkOrder wo where wo.currentPersonId=?1 and wo.status = 1 and wo.currentStepId =2 order by wo.id desc")
    public List<WorkOrder> getNeedAssignWorkOrder(int currentPersonId);

    @Query(value ="select wo.* from work_order wo, (select distinct wos.work_order_id from work_order_step wos where wos.step_id = 2 and wos.owner_id = ?1) wos where wo.id = wos.work_order_id and wo.status = 1 and wo.current_step_id <> 2", nativeQuery = true)
    public List<WorkOrder> getAssignedWorkOrder(int currentPersonId);

    @Query("select wo from WorkOrder wo where (wo.currentPersonId=?1 or wo.currentPersonId = -1) and wo.status = 1 and wo.currentStepId = 3 order by wo.id desc")
    public List<WorkOrder> getUnAcceptedWorkOrder(int currentPersonId);

    @Query("select wo from WorkOrder wo where wo.currentPersonId=?1 and wo.status = 1 and (wo.currentStepId = 4 or wo.currentStepId = 5) order by wo.id desc")
    public List<WorkOrder> getAcceptedWorkOrder(int currentPersonId);

    @Query("select wo from WorkOrder wo where wo.currentPersonId<>?1 and wo.status = 1 and wo.siteId = ?2 and wo.currentStepId in (3,4) order by wo.id desc")
    public List<WorkOrder> getOtherPersonWorkOrder(int currentPersonId, int siteId);

}
