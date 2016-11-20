package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrderStep;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface WorkOrderStepRepository extends GenericRepository<WorkOrderStep> {
    public List<WorkOrderStep> getByWorkOrderIdOrderByIdAsc(int workOrderId);

    public List<WorkOrderStep> getByWorkOrderIdAndStepId(int workOrderId, int stepId);
}
