/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.dao.V2_WorkOrderStepRepository;
import com.ge.apm.dao.WorkOrderDetailRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class V2_WorkOrderService {

    @Autowired
    private V2_WorkOrderRepository woDao;
    
    @Autowired
    private AssetInfoRepository assetDao;

    @Autowired
    private ServiceRequestRepository srDao;

    @Autowired
    private V2_WorkOrderStepRepository stepDao;

    @Autowired
    private WorkOrderDetailRepository detailDao;

    @Autowired
    private UserAccountRepository userDao;

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

    public void cancelWorkOrder(String workOrderId, String reason) {
        String token = UserContextService.getAccessToken();

        Integer stepId = woDao.findById(workOrderId).getCurrentStepId();
        String res = srApi.cancelWorkOrderAction(token, workOrderId, reason, stepId);
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Cancel"));
        }
    }

    public List<UserAccount> getWorkerList() {
        return userDao.getUsersWithAssetStaffRole(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    
    public AssetInfo getAssetInfo(Integer assetId){
        return assetDao.findById(assetId);
    }

    public void dispatchWorkOrder(V2_WorkOrder selectedWorkOrder) {
        String token = UserContextService.getAccessToken();
        String res = srApi.dispatchWorkOrderAction(token, selectedWorkOrder.getId(), selectedWorkOrder.getCurrentPersonId().toString(),selectedWorkOrder.getIntExtType().toString(), selectedWorkOrder.getCurrentStepId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("dispatchWorkOrder"));
        }
    }

    public void ackWorkOrder(V2_WorkOrder selectedWorkOrder) {
        String token = UserContextService.getAccessToken();
        String res = srApi.ackWorkOrderAction(token,selectedWorkOrder.getId(),selectedWorkOrder.getCurrentStepId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Acknowledge"));
        }
    }

    public void rejectWorkOrder(V2_WorkOrder selectedWorkOrder,String reason, Integer status, Date confirmedDowntime) {
        String token = UserContextService.getAccessToken();
        String res = srApi.rejectWorkOrderAction(token,selectedWorkOrder.getId(),selectedWorkOrder.getCurrentStepId(),reason,status,confirmedDowntime);
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Acknowledge"));
        }
    }

    public void rateWorkOrder(V2_WorkOrder selectedWorkOrder, Integer rating, String comments) {
        String token = UserContextService.getAccessToken();
        String res = srApi.rateWorkOrderAction(token,selectedWorkOrder.getId(),selectedWorkOrder.getCurrentStepId(),rating,comments);
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("feedbackComment"));
        }
    }
}
