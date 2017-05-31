/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class ServiceRequestController extends GenericCRUDUUIDController<V2_ServiceRequest> {

    ServiceRequestRepository dao;
    
    List<V2_WorkOrder> workOrders;
    
    private V2_WorkOrderService woService;
    
    
    protected void init() {
        dao = WebUtil.getBean(ServiceRequestRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        this.filterBySite = true;
        
    }

    @Override
    protected GenericRepositoryUUID<V2_ServiceRequest> getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_ServiceRequest> loadData(PageRequest pageRequest) {
        this.setSiteFilter();
        this.selected = null;
        this.workOrders = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    
    
    public String getRequestReasonInShort(V2_ServiceRequest item){
        
        if(item.getRequestReason()==null) return null;
        if(item.getRequestReason().length()<=20)
            return item.getRequestReason();
        else
            return item.getRequestReason().substring(1,20)+"...";
    }
    
     public List<UserAccount> getUsersInHospital(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getUserList(UserContextService.getCurrentUserAccount().getHospitalId());
    }
     
     public void onSelectServiceRequest(){
         workOrders = woService.getWorkOrdersBySR(selected.getId());
     }
     
     public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId){
         return woService.getWorkOrderSteps(woId);
     }
     public List<V2_WorkOrder_Detail> getWOrkOrderDetail(String woId){
         return woService.getWorkOrderDetails(woId);
     }

    public List<V2_WorkOrder> getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(List<V2_WorkOrder> workOrders) {
        this.workOrders = workOrders;
    }

     
}
