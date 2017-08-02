/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderListController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable {
    private Date esTime;
    private Date confirmedUpDate;
    private V2_WorkOrderRepository dao;
    private ServiceRequestRepository srDao;
    private V2_WorkOrderService woService;
    private List<I18nMessage>   msgModeList;
    //status：0-待派工/1-待接单/2-维修中/3-已完成/4-已派工/10-已取消  待验收-5, 待关单-6, 已关单-7
    private Integer queryIndex;
    private Integer category;
    private boolean addOrUpdate;
    private V2_ServiceRequest selectedServiceRequest;
    private BiomedGroupRepository groupDao;
    private V2_WorkOrder selectedWorkOrder;
    private AssetFaultTypeService assetFaultTypeService;
    private AssetInfoRepository assetInfoRepository;
    private UserAccount ua;
    private List<V2_WorkOrder_Detail> workOrderDetailsList;
    private V2_WorkOrder_Detail itemDetail ;
    private UserAccountRepository userAccountRepository;
    private String testMethod;
    private String solution;
    private String problemDes;
    protected void init() {
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        srDao = WebUtil.getBean(ServiceRequestRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        groupDao = WebUtil.getBean(BiomedGroupRepository.class);
        this.filterBySite = true;
        queryIndex = 3;
        ua = UserContextService.getCurrentUserAccount();
        assetFaultTypeService=WebUtil.getBean(AssetFaultTypeService.class);
        assetInfoRepository=WebUtil.getBean(AssetInfoRepository.class);
        userAccountRepository=WebUtil.getBean(UserAccountRepository.class);
        initWorkOrderDetailList();
    }
    public void initWorkOrderDetailList(){
        itemDetail = new V2_WorkOrder_Detail();
        workOrderDetailsList = new ArrayList<V2_WorkOrder_Detail>();
        woUid=UUID.randomUUID().toString().replace("-", "");
        itemDetail.setId(woUid);
        itemDetail.setManHours(0);
        workOrderDetailsList.add(itemDetail);

    }
    public boolean isManHours(){
       return  itemDetail.getManHours()==null?false:true;
    }

    public void saveDetail(){
        String username=null;
        if(woUid!=null){
            Iterator<V2_WorkOrder_Detail> sListIterator = workOrderDetailsList.iterator();
            while(sListIterator.hasNext()){
                V2_WorkOrder_Detail workOrderDetail = sListIterator.next();
                if(workOrderDetail.getId().equals(woUid)){
                    sListIterator.remove();
                }
            }
            woUid=null;
        }
        V2_WorkOrder_Detail v2 = new V2_WorkOrder_Detail();
        v2.setManHours(itemDetail.getManHours());
        v2.setId(UUID.randomUUID().toString().replace("-", ""));
        v2.setParts(itemDetail.getParts());
        v2.setPartsPrice(itemDetail.getPartsPrice());
        v2.setApartsQuantity(itemDetail.getApartsQuantity());
        v2.setOtherExpense(itemDetail.getOtherExpense());
        v2.setCowokerUserId(itemDetail.getCowokerUserId());
        username=userAccountRepository.findById(itemDetail.getCowokerUserId()).getName();
        v2.setCowokerUserName(username);
        workOrderDetailsList.add(v2);
    }

    public void createWorkOrder(){
        selectedWorkOrder.setComment(comments);
        V2_ServiceRequest sr = srDao.findById(selectedWorkOrder.getSrId());
        sr.setRequestReason(comments);
        woService.reCreateWorkOrder(selectedWorkOrder,selectedWorkOrder.getCurrentPersonId(),2,comments,selectedWorkOrder.getIntExtType());
    }

    public void newOrderCancel(){
        comments =null;
    }

    public void repairWorkOrder(){
        //gl:1表示是正常
        woService.repairWorkOrder(selectedWorkOrder,confirmedUpDate,1,workOrderDetailsList);

    }
    public V2_WorkOrder_Detail getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(V2_WorkOrder_Detail itemDetail) {
        this.itemDetail = itemDetail;
    }

    public boolean isWoListEmpty(){
        return  (workOrderDetailsList.size()==0);
    }
    String woUid=null;


    public void prepareEdit(V2_WorkOrder_Detail workOrderDetail){
         this.prepareEdit();
    }


    public void prepareDispatch(String woid){
        selectedWorkOrder = dao.findById(woid);
        initWorkOrderDetailList();

    }

    public void clickCloseOrder(String woid){
        selectedWorkOrder = dao.findById(woid);
        initWorkOrderDetailList();
    }
    public void prepareEditDetail(V2_WorkOrder_Detail workOrderDetail){
        itemDetail =workOrderDetail;
        System.out.println(itemDetail);
        setAddOrUpdate(false);

    }
    public void updateEditDetail(){
        itemDetail.setCowokerUserName(userAccountRepository.getById(itemDetail.getCowokerUserId()).getName());
        setAddOrUpdate(false);
    }
    public void prepareCreateDetail(){
        itemDetail = new V2_WorkOrder_Detail();
        setAddOrUpdate(true);
    }

   public void deleteWoDetail(String itemId){

       if(workOrderDetailsList.size()==1){
           V2_WorkOrder_Detail v = new V2_WorkOrder_Detail();
           workOrderDetailsList = new ArrayList<V2_WorkOrder_Detail>();
           woUid=UUID.randomUUID().toString().replace("-", "");
           v.setId(woUid);
           v.setManHours(0);
           workOrderDetailsList.add(v);
       }
       else {
           Iterator<V2_WorkOrder_Detail> sListIterator = workOrderDetailsList.iterator();
           while (sListIterator.hasNext()) {
               V2_WorkOrder_Detail workOrderDetail = sListIterator.next();
               if (workOrderDetail.getId().equals(itemId)) {
                   sListIterator.remove();
               }
           }
       }
   }


    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getProblemDes() {
        return problemDes;
    }

    public void setProblemDes(String problemDes) {
        this.problemDes = problemDes;
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
        }
        else  { //待接单(queryIndex == 3)
            removeFilterOnField("currentPersonId");
           // searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, ua.getId()));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            removeFilterOnField("currentStepId");
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, queryIndex));
        }
        /*else {
            if(queryIndex==3){
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



        }}*/
        this.selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }


    }
private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public List<V2_WorkOrder_Detail> getWorkOrderDetailsList() {
        return workOrderDetailsList;
    }

    public void setWorkOrderDetailsList(List<V2_WorkOrder_Detail> workOrderDetailsList) {
        this.workOrderDetailsList = workOrderDetailsList;
    }

    public List<UserAccount> findRepairPerson(){
        List<UserAccount> assetResponser=null;
        if(this.selectedWorkOrder!=null)
            assetResponser = woService.getAssetResponser(this.selectedWorkOrder.getAssetId());
        return assetResponser;
    }


    public Date getEsTime() {
        return esTime;
    }

    public void setEsTime(Date esTime) {
        this.esTime = esTime;
    }

    public Date getEstimatedClosedTime(){
        V2_ServiceRequest sr = srDao.findById(this.selectedWorkOrder.getSrId());
        return sr.getEstimatedCloseTime();
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
        comments=null;
        esTime=null;
        selectedWorkOrder=null;
    }
public void expenseDialogClose(){
        itemDetail=null;
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

    public List<UserAccount> getWorkerList(){
        return woService.getWorkerList();
    }

    public List<AssetFaultType> getAssetFaultType(){
        AssetInfo assetinfo = assetInfoRepository.findById(this.selectedWorkOrder.getAssetId());
        return assetFaultTypeService.getFaultTypeList(assetinfo.getAssetGroup());
    }
    
    public void dispatchWorkOrder(){
        woService.dispatchWorkOrder(selectedWorkOrder);
        cancel();
    }

    public void closeWorkOrder(){
        woService.closeWorkOrder(selectedWorkOrder,workOrderDetailsList);
        cancel();
    }

    public void acceptWorkOrder(){
        //gl:question:1 为什么这里的esTime要比预估时间慢一天? 2 repairType到底传过去的是中文字符还是就数值型?
        String repairType =selectedWorkOrder.getRepairType().toString();
        woService.acceptWorkOrder(selectedWorkOrder,esTime,repairType,comments);
    }
    public void reassignWorkOrder(){
        woService.reassignWorkOrder(selectedWorkOrder, selectedWorkOrder.getCurrentPersonId(), comments);
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
    
    //费用明细字段
    Integer manHours;

    public Integer getManHours() {
        return manHours;
    }

    public void setManHours(Integer manHours) {
        this.manHours = manHours;
    }

    public Date getConfirmedUpDate() {
        return confirmedUpDate;
    }

    public void setConfirmedUpDate(Date confirmedUpDate) {
        this.confirmedUpDate = confirmedUpDate;
    }

    public V2_WorkOrderRepository getDao() {
        return dao;
    }

    public void setDao(V2_WorkOrderRepository dao) {
        this.dao = dao;
    }

public boolean renderAddUpdate(){
        return addOrUpdate;
}
    public boolean isAddOrUpdate() {
        return addOrUpdate;
    }

    public void setAddOrUpdate(boolean addOrUpdate) {
        this.addOrUpdate = addOrUpdate;
    }
}
