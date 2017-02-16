package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrderStepDetail;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface WorkOrderStepDetailRepository extends GenericRepository<WorkOrderStepDetail> {
    public List<WorkOrderStepDetail> getByWorkOrderStepId(int workOrderStepId);

}
