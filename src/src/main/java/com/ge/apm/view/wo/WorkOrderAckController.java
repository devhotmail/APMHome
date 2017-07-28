/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderAckController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable {

    V2_WorkOrderRepository dao;

    private ServiceRequestRepository srDao;
    private V2_WorkOrderService woService;

    //status：0-待派工/1-待接单/2-维修中/3-已完成/4-已派工/5-已取消

    private V2_WorkOrder selectedWorkOrder;

    private String failReason;

    private Integer currentStatus = 1;

    private Boolean needConfirmTime = false;

    private Date confirmedDownTime;
    
    private AssetInfo currentAsset;
   

    protected void init() {
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        srDao = WebUtil.getBean(ServiceRequestRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        this.filterBySite = true;

    }

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        this.setSiteFilter();
        removeFilterOnField("status");
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
        searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));

        this.selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }


    public void cancel() {
        selected = null;
    }

    public V2_ServiceRequest getServiceRequest(String srId) {
        return srDao.findById(srId);
    }

    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }

    public List<V2_WorkOrder_Detail> getWOrkOrderDetail(String woId) {
        return woService.getWorkOrderDetails(woId);
    }


    public void ackWorkOrder() {
        woService.ackWorkOrder(selectedWorkOrder);
        cancel();
    }

    public void rejectWorkOrder() {
        woService.rejectWorkOrder(selectedWorkOrder,failReason,currentStatus,confirmedDownTime);
        cancel();
    }

    public void prepareAck(String woid) {
        selectedWorkOrder = dao.findById(woid);
        currentAsset = woService.getAssetInfo(selectedWorkOrder.getAssetId());
        currentStatus = currentAsset.getStatus();
    }

    public void onStatusChange() {
        if (currentAsset != null && currentStatus == 2 && currentAsset.getStatus() != 2) {
            needConfirmTime = true;
        } else {
            needConfirmTime = false;
        }
    }

    //getter and setter

    public V2_WorkOrder getSelectedWorkOrder() {
        return selectedWorkOrder;
    }

    public void setSelectedWorkOrder(V2_WorkOrder selectedWorkOrder) {
        this.selectedWorkOrder = selectedWorkOrder;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getNeedConfirmTime() {
        return needConfirmTime;
    }

    public void setNeedConfirmTime(Boolean needConfirmTime) {
        this.needConfirmTime = needConfirmTime;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

}
