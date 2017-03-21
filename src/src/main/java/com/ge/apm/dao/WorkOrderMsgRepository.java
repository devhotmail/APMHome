package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrderMsg;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface WorkOrderMsgRepository extends GenericRepository<WorkOrderMsg> {
    
    public WorkOrderMsg getById(int id);
    
    public List<WorkOrderMsg> findByWorkOrderId(Integer workOrderId);


}
