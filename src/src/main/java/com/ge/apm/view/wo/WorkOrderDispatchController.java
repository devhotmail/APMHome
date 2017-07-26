/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.BiomedGroupRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.wo.V2_WorkOrderService;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
public class WorkOrderDispatchController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable {

    V2_WorkOrderRepository dao;

    private ServiceRequestRepository srDao;
    private V2_WorkOrderService woService;

    //status：0-待派工/1-待接单/2-维修中/3-已完成/4-已派工/10-已取消  待验收-5, 待关单-6, 已关单-7
    private Integer queryIndex;

    private Integer category;
    
    private V2_ServiceRequest selectedServiceRequest;
    private BiomedGroupRepository groupDao;
    private V2_WorkOrder selectedWorkOrder;
    UserAccount ua;
    protected void init() {
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        srDao = WebUtil.getBean(ServiceRequestRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        groupDao = WebUtil.getBean(BiomedGroupRepository.class);
        this.filterBySite = true;
        queryIndex = 2;
        ua = UserContextService.getCurrentUserAccount();

    }

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return dao;
    }


    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        Page<V2_WorkOrder> pages = null;
        this.setSiteFilter();
        removeFilterOnField("status");

        if (queryIndex == 10) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 3));
        }
        if (queryIndex == 7) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
            removeFilterOnField("currentStepId");
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, queryIndex));
        } else {
           /* if(queryIndex==3){
                Integer groupCount = groupDao.getCountByHospital(ua.getSiteId(),ua.getHospitalId());
                Sort sort = new Sort(Sort.Direction.DESC, "current_step_id");
                PageRequest pr = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
                pages= dao.fetchAvailableWorkOrderByUser(ua.getId(),pr);
                pages= dao.findByHospitalIdAndStatusAndCurrentStepIdAndCurrentPersonIdIn(ua.getHospitalId(),1,3, Arrays.asList(-1,ua.getId()),pageRequest);
                switch(category){
                    case 11: pages = groupCount>0? dao.fetchAvailableWorkOrderByUser(ua.getId(),pageRequest):dao.findByHospitalIdAndStatusAndCurrentStepIdAndCurrentPersonIdIn(ua.getHospitalId(),1,3, Arrays.asList(-1,ua.getId()),pageRequest); break;
                    case 12: pages =

                }
                return pages;
            }*/


            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            removeFilterOnField("currentStepId");
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, queryIndex));
        }
        this.selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }


    }

    public void defineCategory(Integer cate){
        this.category = cate;
        cancel();
    }
    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        cancel();
    }

    public void cancel() {
        selected=null;
        selectedServiceRequest = null;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public V2_ServiceRequest getServiceRequest(String srId){
        return srDao.findById(srId);
    }
    
    public void onSelectWorkOrder() {
        selectedServiceRequest = srDao.findById(selected.getSrId());
    }
    
    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }
    
    public List<V2_WorkOrder_Detail> getWOrkOrderDetail(String woId) {
        return woService.getWorkOrderDetails(woId);
    }
    
    public void prepareDispatch(String woid){
        selectedWorkOrder = dao.findById(woid);
    }
    
    
    public List<UserAccount> getWorkerList(){
        return woService.getWorkerList();
    }
    
    public void dispatchWorkOrder(){
        woService.dispatchWorkOrder(selectedWorkOrder);
        cancel();
    }
    

    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public V2_ServiceRequest getSelectedServiceRequest() {
        return selectedServiceRequest;
    }

    public void setSelectedServiceRequest(V2_ServiceRequest selectedServiceRequest) {
        this.selectedServiceRequest = selectedServiceRequest;
    }

    public V2_WorkOrder getSelectedWorkOrder() {
        return selectedWorkOrder;
    }

    public void setSelectedWorkOrder(V2_WorkOrder selectedWorkOrder) {
        this.selectedWorkOrder = selectedWorkOrder;
    }
    
    

}
