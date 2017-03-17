package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrder;

import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface WorkOrderRepository extends GenericRepository<WorkOrder> {
    
    public WorkOrder getByIdAndCurrentPersonId(int id, int currentPersonId);
    
    public List<WorkOrder> findByAssetId(Integer assetId);

    public List<WorkOrder> findByAssetIdAndIntExtType(Integer assetId, int intExtType);

}
