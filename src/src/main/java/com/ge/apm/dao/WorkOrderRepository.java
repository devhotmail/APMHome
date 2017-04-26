package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkOrderRepository extends GenericRepository<WorkOrder> {

    public WorkOrder getByIdAndCurrentPersonId(int id, int currentPersonId);

    public List<WorkOrder> findByAssetId(Integer assetId);

    public List<WorkOrder> findByAssetIdAndIntExtType(Integer assetId, int intExtType);
    public List<WorkOrder> findByStatus(int status);
    public List<WorkOrder>  findByRequestorId(int requestorId);
    public List<WorkOrder> findByRequestorIdAndStatusOrderByIdDesc(int requestorId, int status);
    
    public List<WorkOrder> findByAssetIdOrderByIdDesc(Integer assetId);


    @Query(value="select wos from  WorkOrderStep wos,WorkOrder wo where wos.ownerId =?1 and wos.workOrderId=wo.id and wo.status=2")
    public List<WorkOrder> findClosedWorkOrder(Integer ownerId);


    @Modifying
    @Query("update WorkOrder wo set wo.currentPersonId=?2 ,wo.currentPersonName=?3 ,wo.currentStepId=?4 where wo.id=?1")
    public void updateWorkderOrder(Integer woId, Integer userId, String userName, Integer stepId);

    //gl:todo 7 是应该可配的
    @Query(value ="select * from Work_Order wo where wo.asset_Id=?1 and wo.hospital_Id=?2 and wo.site_Id=?3 and (wo.close_Time + cast(?4 as interval)) > now() order by wo.id desc", nativeQuery = true)
    public List<WorkOrder> isReopenWorkOrder(Integer assetId,Integer hospitalId,Integer siteId, String reopen);

    public WorkOrder getById(Integer id);
    public WorkOrder findByAssetIdAndStatus(Integer assetId, int status);
    public List<WorkOrder> findByAssetIdAndStatusOrderByIdDesc(Integer assetId, int status);
    
    public Page<WorkOrder> getByHospitalIdAndStatusAndCurrentStepIdOrderByIdDesc(int hospitalId, int status, int currentStepId, Pageable pageable);

    @Query(value ="select wo.* from work_order wo, (select distinct wos.work_order_id from work_order_step wos where wos.step_id = 2 and wos.owner_id = ?1) wos where wo.id = wos.work_order_id and wo.status = 1 and wo.current_step_id <> 2 order by ?#{#pageable}", 
            countQuery ="select count(1) from work_order wo, (select distinct wos.work_order_id from work_order_step wos where wos.step_id = 2 and wos.owner_id = ?1) wos where wo.id = wos.work_order_id and wo.status = 1 and wo.current_step_id <> 2", 
            nativeQuery = true)
    public Page<WorkOrder> getAssignedWorkOrder(int currentPersonId, Pageable pageable);

    @Query("select wo from WorkOrder wo where (wo.currentPersonId=?1 or (wo.currentPersonId = -1 and wo.siteId = ?2)) and wo.status = 1 and wo.currentStepId = 3 order by wo.id desc")
    public Page<WorkOrder> getUnAcceptedWorkOrder(int currentPersonId, int siteId, Pageable pageable);

    @Query("select wo from WorkOrder wo where wo.currentPersonId=?1 and wo.status = 1 and (wo.currentStepId = 4 or wo.currentStepId = 5) order by wo.id desc")
    public Page<WorkOrder> getAcceptedWorkOrder(int currentPersonId, Pageable pageable);

    @Query("select wo from WorkOrder wo where wo.currentPersonId<>?1 and wo.status = 1 and wo.siteId = ?2 and wo.currentStepId in (3,4)")
    public Page<WorkOrder> getOtherPersonWorkOrder(int currentPersonId, int siteId, Pageable pageable);
    
    @Query(value="select wo.* from work_order wo where exists (select wos.work_order_id from work_order_step wos where wos.owner_id = ?1 and wo.id = wos.work_order_id ) and wo.status = 2 order by ?#{#pageable}", 
            countQuery="select count(1) from work_order wo where exists (select wos.work_order_id from work_order_step wos where wos.owner_id = ?1 and wo.id = wos.work_order_id ) and wo.status = 2 ",
            nativeQuery = true)
    public Page<WorkOrder> getClosedWorkOrder(int currentPersonId, Pageable pageable);

}
