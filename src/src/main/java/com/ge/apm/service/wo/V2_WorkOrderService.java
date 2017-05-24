/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 212579464
 */

@Service
public class V2_WorkOrderService {
    
    @Autowired
    private V2_WorkOrderRepository woDao;
    
    @Autowired
    private ServiceRequestRepository srDao;

    public List<V2_WorkOrder> getWorkOrdersBySR(String serviceRequest) {
        
        return woDao.findBySrId(serviceRequest);
    }

    public List<V2_ServiceRequest> getServiceRequestByAssetId(Integer id) {
        
        return srDao.findByAssetId(id);
    }
    
    
    
}
