package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrder_Step;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import webapp.framework.dao.GenericRepositoryUUID;

public interface V2_WorkOrderStepRepository extends GenericRepositoryUUID<V2_WorkOrder_Step> {
    
    @Query("select s from V2_WorkOrder_Step s where s.woId=?1 order by s.startTime desc")
    public List<V2_WorkOrder_Step> findByWorkOrder(String woId);

    @Query("select s from V2_WorkOrder_Step s where s.woId=?1 and s.stepId=?2 and endTime is null")
    public V2_WorkOrder_Step getByWorkOrderIdAndStepIdAndEndTimeIsNull(String woId, int stepId);

//    
//    public List<V2_WorkOrder_Step> findByWoIdAndStepIdOrderByCreatedDateDesc(String woId, int stepId);
}
