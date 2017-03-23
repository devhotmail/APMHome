package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrder;
import webapp.framework.dao.GenericRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface WorkOrderRepository extends GenericRepository<WorkOrder> {
    
    public WorkOrder getByIdAndCurrentPersonId(int id, int currentPersonId);
    
    public List<WorkOrder> findByAssetId(Integer assetId);

    public List<WorkOrder> findByAssetIdAndIntExtType(Integer assetId, int intExtType);
    public List<WorkOrder> findByStatus(int status);
    public List<WorkOrder>  findByRequestorId(int requestorId);

    public WorkOrder findByAssetIdAndStatus(Integer assetId, int status);

    @Query("select wo from WorkOrder wo where wo.currentPersonId=?1 and wo.status = 1 and wo.currentStepId =2 order by wo.id desc")
    public List<WorkOrder> getNeedAssignWorkOrder(int currentPersonId);
    
    @Query("select wo from WorkOrder wo where wo.currentPersonId=?1 and wo.status = 1 and wo.currentStepId <> 2 order by wo.id desc")
    public List<WorkOrder> getAssignedWorkOrder(int currentPersonId);

    @Query("select wo from WorkOrder wo where (wo.currentPersonId=?1 or wo.currentPersonId = -1) and wo.status = 1 and wo.currentStepId = 3 order by wo.id desc")
    public List<WorkOrder> getUnAcceptedWorkOrder(int currentPersonId);
    
    @Query("select wo from WorkOrder wo where wo.currentPersonId=?1 and wo.status = 1 and (wo.currentStepId = 4 or wo.currentStepId = 5) order by wo.id desc")
    public List<WorkOrder> getAcceptedWorkOrder(int currentPersonId);
    
    @Query("select wo from WorkOrder wo where wo.currentPersonId<>?1 and wo.status = 1 and wo.siteId = ?2 and wo.currentStepId <> 2 order by wo.id desc")
    public List<WorkOrder> getOtherPersonWorkOrder(int currentPersonId, int siteId);
}
