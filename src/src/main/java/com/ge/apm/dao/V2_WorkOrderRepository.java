package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrder;
import java.util.List;
import webapp.framework.dao.GenericRepositoryUUID;

public interface V2_WorkOrderRepository extends GenericRepositoryUUID<V2_WorkOrder> {

    List<V2_WorkOrder> findBySrId(String srId);
    
}
