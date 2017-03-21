package com.ge.apm.dao;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface WorkOrderRepository extends GenericRepository<WorkOrder> {

    public WorkOrder getByIdAndCurrentPersonId(int id, int currentPersonId);

    public List<WorkOrder> findByAssetId(Integer assetId);

    public List<WorkOrder> findByAssetIdAndIntExtType(Integer assetId, int intExtType);
    public List<WorkOrder> findByStatus(int status);
    public List<WorkOrder>  findByRequestorId(int requestorId);



    @Modifying
    @Query("update WorkOrder wo set wo.currentPersonId=?2 ,wo.currentPersonName=?3 ,wo.currentStepId=?4 where wo.id=?1")
    public void updateWorkderOrder(Integer woId, Integer userId, String userName,Integer stepId);

    //gl:todo 7 是应该可配的
    @Query("select wo from WorkOrder wo where wo.assetId=:assetId and wo.hospitalId=:hospitalId and wo.siteId=:siteId and cast(wo.closeTime as date)+7>now() ")
    public List<WorkOrder> isReopenWorkOrder(@Param("assetId") Integer assetId,@Param("hospitalId") Integer hospitalId,@Param("siteId")Integer siteId);

    public WorkOrder getById(Integer id);

}
