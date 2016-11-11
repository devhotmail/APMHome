package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrderHistory;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface WorkOrderHistoryRepository extends GenericRepository<WorkOrderHistory> {
    public List<WorkOrderHistory> getByWorkOrderId(int workOrderId);

}
