/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

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

import com.hazelcast.util.CollectionUtil;
import java.util.*;
import java.util.stream.Collectors;

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
            WebUtil.addErrorMessage("Fail to do cancel operation");
        }
    }

    public List<UserAccount> getWorkerList() {
        return userDao.getUsersWithAssetStaffRole(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    public void dispatchWorkOrder(V2_WorkOrder selectedWorkOrder) {
        String token = UserContextService.getAccessToken();
        String res = srApi.dispatchWorkOrderAction(token, selectedWorkOrder.getId(), selectedWorkOrder.getCurrentPersonId().toString(),selectedWorkOrder.getIntExtType().toString(), selectedWorkOrder.getCurrentStepId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage("Fail to do dispatch operation");
        }
    }

    @Autowired
    UserAccountRepository userAccountRepository;
    public List<UserAccount> getAssetResponser(int assetId) {
        List<UserAccount> userListTag = null;
        try {
            //获取关注过该资产标签的用户, 设备标签<->(设备+工程师组[员1，2..])
            userListTag = userAccountRepository.getResponserByAssetId(assetId);
            //获取和资产绑定的owner
            List<UserAccount> assetOwnerList=  userAccountRepository.getResponserWithAsset(assetId);
            List<UserAccount> defaultUserList=null;
            //如果该设备没有相应的owner并且根据设备标签找不到对应的负责人，就获取默认的医工.
            if(CollectionUtil.isEmpty(userListTag) &  CollectionUtil.isEmpty(assetOwnerList)){
                //search List from   (siteId and  roleid=3)
                defaultUserList=  userAccountRepository.getDefaultUsers(assetId);
                //duplication remove
                defaultUserList =new ArrayList<UserAccount>(new HashSet<UserAccount>(defaultUserList));
                defaultUserList=  defaultUserList.stream().filter(id -> id != null).collect(Collectors.toList());
                return defaultUserList;
            }

            if(CollectionUtil.isNotEmpty(userListTag) & CollectionUtil.isNotEmpty(assetOwnerList)){
                userListTag.addAll(assetOwnerList);
                userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
                userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
                return userListTag;
            }else if(CollectionUtil.isEmpty(assetOwnerList)){
                userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
                userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
                return userListTag;
            }else if(CollectionUtil.isEmpty(userListTag)){
                userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(assetOwnerList));
                defaultUserList=  userAccountRepository.getDefaultUsers(assetId);
                userListTag.addAll(defaultUserList);
                userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
                userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
                return userListTag;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userListTag;
    }
}
