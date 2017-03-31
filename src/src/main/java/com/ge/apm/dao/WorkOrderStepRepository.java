package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrderStep;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface WorkOrderStepRepository extends GenericRepository<WorkOrderStep> {
    public List<WorkOrderStep> getByWorkOrderIdOrderByIdAsc(int workOrderId);

    @Query("select s from WorkOrderStep s where s.workOrderId=?1 and s.stepId=?2 and endTime is null order by s.id desc")
    public List<WorkOrderStep> getByWorkOrderIdAndStepId(int workOrderId, int stepId);
    
    @Query("select s from WorkOrderStep s where s.workOrderId=?1 and s.stepId=?2 order by s.id desc")
    public List<WorkOrderStep> getByWorkOrderIdAndStepIdAndWithoutEndTime(int workOrderId, int stepId);
    
    public List<WorkOrderStep> findByWorkOrderIdAndStepId(int workOrderId, int stepId);
}
