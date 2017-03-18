package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrder;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface WorkOrderRepository extends GenericRepository<WorkOrder> {
    
    public WorkOrder getByIdAndCurrentPersonId(int id, int currentPersonId);
    
    public List<WorkOrder> findByAssetId(Integer assetId);

    public List<WorkOrder> findByAssetIdAndIntExtType(Integer assetId, int intExtType);
    public List<WorkOrder> findByStatus(int status);
    public List<WorkOrder>  findByRequestorId(int requestorId);





}
