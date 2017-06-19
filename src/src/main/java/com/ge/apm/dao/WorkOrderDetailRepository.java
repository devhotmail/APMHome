package com.ge.apm.dao;

import webapp.framework.dao.GenericRepositoryUUID;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import java.util.List;

public interface WorkOrderDetailRepository extends GenericRepositoryUUID<V2_WorkOrder_Detail> {
    
    public List<V2_WorkOrder_Detail>findByWoId(String woId);
}
