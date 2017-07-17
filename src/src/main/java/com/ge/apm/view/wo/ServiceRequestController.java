/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class ServiceRequestController extends GenericCRUDUUIDController<V2_ServiceRequest> implements Serializable, ServerEventInterface {

    private static final long serialVersionUID = 1L;

    ServiceRequestRepository dao;

    List<V2_WorkOrder> workOrders;

    private V2_WorkOrderService woService;

    Integer queryIndex;

    private V2_ServiceRequest newServiceRequest;

    private AssetInfo selectedAsset;

    private Integer currentStatus = 1;

    private Boolean needConfirmTime = false;
    
    private String cancelReason;

    protected void init() {
        dao = WebUtil.getBean(ServiceRequestRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        this.filterBySite = true;
        queryIndex = 1;

    }

    @Override
    protected GenericRepositoryUUID<V2_ServiceRequest> getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_ServiceRequest> loadData(PageRequest pageRequest) {
        this.setSiteFilter();
        removeFilterOnField("status");
        if (queryIndex == 5) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 3));
        } else {
            removeFilterOnField("requestorId");
            removeFilterOnField("fromDeptId");
            if (queryIndex == 1 || queryIndex == 3) {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            } else {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
            }
            if (queryIndex == 1 || queryIndex == 2) {
                searchFilters.add(new SearchFilter("requestorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
            } else {
                searchFilters.add(new SearchFilter("fromDeptId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getOrgInfoId()));
            }
        }

        this.selected = null;
        this.workOrders = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getRequestReasonInShort(V2_ServiceRequest item) {

        if (item.getRequestReason() == null) {
            return null;
        }
        if (item.getRequestReason().length() <= 20) {
            return item.getRequestReason();
        } else {
            return item.getRequestReason().substring(1, 20) + "...";
        }
    }

    public List<UserAccount> getUsersInHospital() {
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getUserList(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    public void onSelectServiceRequest() {
        workOrders = woService.getWorkOrdersBySR(selected.getId());
    }

    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }

    public List<V2_WorkOrder_Detail> getWOrkOrderDetail(String woId) {
        return woService.getWorkOrderDetails(woId);
    }

    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        cancel();
    }

    public void removeFilterOnField(String fieldName) {
        SearchFilter temp = null;
        for (SearchFilter filter : searchFilters) {
            if (fieldName.equals(filter.fieldName)) {
                temp = filter;
                break;
            }
        }
        if (null != temp) {
            searchFilters.remove(temp);
        }
    }

    public void createServiceRequest() {
        newServiceRequest = new V2_ServiceRequest();
        newServiceRequest.setRequestorId(UserContextService.getCurrentUserAccount().getId());
        newServiceRequest.setFromDeptId(UserContextService.getCurrentUserAccount().getOrgInfoId());
        newServiceRequest.setRequestorName(UserContextService.getCurrentUserAccount().getName());
        newServiceRequest.setRequestTime(new Date());
    }

    public void cancel() {
        newServiceRequest = null;
        selectedAsset = null;
        needConfirmTime = false;
        currentStatus = 1;
    }

    public void saveServiceRequest() {

        woService.createServiceRequest(newServiceRequest, selectedAsset);

        cancel();
    }

    public void onAssetSelected(SelectEvent event) {
        onServerEvent("assetSelected", event.getObject());
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }

        if (this.newServiceRequest == null) {
            return;
        }

        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        needConfirmTime = false;
    }

    public void onStatusChange() {
        if (selectedAsset !=null && currentStatus == 2 && selectedAsset.getStatus() != 2) {
            needConfirmTime = true;
        } else {
            needConfirmTime = false;
        }
    }
    
    public void cancelWorkOrder(String woId){
        woService.cancelWorkOrder(woId,cancelReason);
        cancelReason=null;
        cancel();
    }

    //getter and setter
    public List<V2_WorkOrder> getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(List<V2_WorkOrder> workOrders) {
        this.workOrders = workOrders;
    }

    public Integer getQueryIndex() {
        return queryIndex;
    }

    public V2_ServiceRequest getNewServiceRequest() {
        return newServiceRequest;
    }

    public void setNewServiceRequest(V2_ServiceRequest newServiceRequest) {
        this.newServiceRequest = newServiceRequest;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
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

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

}
