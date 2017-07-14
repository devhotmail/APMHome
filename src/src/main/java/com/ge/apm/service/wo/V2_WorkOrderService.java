/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.dao.V2_WorkOrderStepRepository;
import com.ge.apm.dao.WorkOrderDetailRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private V2_WorkOrderStepRepository stepDao;

    @Autowired
    private WorkOrderDetailRepository detailDao;
    
    @Autowired
    private ServiceRequestApiService srApi;

    public List<V2_WorkOrder> getWorkOrdersBySR(String serviceRequest) {

        return woDao.findBySrId(serviceRequest);
    }

    public List<V2_ServiceRequest> getServiceRequestByAssetId(Integer id) {

        return srDao.findByAssetId(id);
    }

    public List<V2_WorkOrder_Step> getWorkOrderSteps(String woId) {
        return stepDao.findByWorkOrder(woId);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetails(String woId) {
        return detailDao.findByWoId(woId);
    }


    public void createServiceRequest(V2_ServiceRequest newServiceRequest, AssetInfo asset) {
        
        String token = UserContextService.getAccessToken();
        Map<String, Object> data = srApi.createServiceRequest(token, newServiceRequest, asset);

    }
}
