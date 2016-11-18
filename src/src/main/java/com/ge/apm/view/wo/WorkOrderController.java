package com.ge.apm.view.wo;

import com.ge.apm.dao.UserAccountRepository;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderStep;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.math.NumberUtils;
import org.primefaces.event.TabChangeEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderController extends JpaCRUDController<WorkOrder> {

    WorkOrderRepository dao = null;
    UserAccount loginUser;

    @Override
    protected void init() {
        loginUser = UserContextService.getCurrentUserAccount();

        this.filterByHospital = true;
        this.filterBySite = true;

        dao = WebUtil.getBean(WorkOrderRepository.class);
    }

    @Override
    protected WorkOrderRepository getDAO() {
        return dao;
    }

    public void setLoginUserFilter(){
        if (this.searchFilters == null) 
            this.searchFilters = new ArrayList<SearchFilter>();
        
        this.searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));
    }
    
    @Override
    protected Page<WorkOrder> loadData(PageRequest pageRequest) {
        //only show my tasks
        //setSiteFilter();
        setHospitalFilter();
        //setLoginUserFilter();
        
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    private boolean isInViewMode = true;
    public boolean getIsInViewMode(){
        return isInViewMode;
    }
    
    @Override
    public void setSelected(WorkOrder workOrder){
        isInViewMode = true;
        this.selected = workOrder;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException{
        super.prepareCreate();
        isInViewMode = false;
    }

    public String getMyWorkOrderId() {
        return null;
    }
    public void setMyWorkOrderId(String selectedWorkOrderId) {
        System.out.println("********************* setMyWorkOrderId");
        int workOrder = NumberUtils.toInt(selectedWorkOrderId, -1);
        if(workOrder<0){
            isInViewMode = true;
            this.selected = null;
        }
        else{
            isInViewMode = false;
            this.selected = dao.getByIdAndCurrentPersonId(workOrder, loginUser.getId());
        }
        
        onSelectWorkOrder();
        prepareEditWorkOrderStep();
    }
    
    public void onSelectWorkOrder(){
        Integer workOrderId = -1;
        if(selected!=null && selected.getId()!=null)
            workOrderId = selected.getId();

        WorkOrderHistoryController woHistoryController = WebUtil.getBean(WorkOrderHistoryController.class);
        woHistoryController.loadWorkOrderHistory(workOrderId);
        
        WorkOrderStepController woStepController = WebUtil.getBean(WorkOrderStepController.class);
        woStepController.loadWorkOrderSteps(workOrderId);
    }
    
    public List<UserAccount> getHospitalUserList(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getUserList(loginUser.getHospitalId());
    }
    
    @Override
    public void onBeforeNewObject(WorkOrder workOrder) {
        onSelectWorkOrder();
        
        workOrder.setSiteId(loginUser.getSiteId());
        workOrder.setHospitalId(loginUser.getHospitalId());
        workOrder.setCreatorId(loginUser.getId());
        workOrder.setCreatorName(loginUser.getName());
        
        workOrder.setCreateTime(TimeUtil.now());
        workOrder.setCurrentStep(1);
        
        workOrder.setCasePriority(3);

        workOrder.setTotalManHour(0);
        workOrder.setTotalPrice(0.0);
        
        workOrder.setIsInternal(true);
        workOrder.setIsClosed(false);
    }

    @Override
    public void onBeforeSave(WorkOrder workOrder) {
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);

        UserAccount requestor = userDao.findById(workOrder.getRequestorId());
        workOrder.setRequestorName(requestor.getName());
        workOrder.getCurrentPersonId();

        UserAccount currentPerson = userDao.findById(workOrder.getCurrentPersonId());
        workOrder.setCurrentPersonName(currentPerson.getName());
        
        if(workOrder.getCurrentStep()==1)
            workOrder.setCurrentStep(2);
    }

    @Override
    public void onAfterNewObject(WorkOrder wo, boolean isOK) {
        if(!isOK){
            wo.setCurrentStep(1);
            return;
        }
        
        //create workOrderStep for workOrder step
        WorkOrderStep woStep = new WorkOrderStep();
        woStep.setWorkOrderId(wo.getId());
        woStep.setSiteId(wo.getSiteId());
        woStep.setStepId(wo.getCurrentStep());
        woStep.setStepName(FieldValueMessageController.doGetFieldValue("woSteps", wo.getCurrentStep().toString()));
        woStep.setOwnerId(wo.getCurrentPersonId());
        woStep.setOwnerName(wo.getCurrentPersonName());
        
        WorkOrderStepRepository woStepDao = WebUtil.getBean(WorkOrderStepRepository.class);
        woStepDao.save(woStep);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;
        if(asset==null) return;
        
        if(this.selected==null) return;
        this.selected.setAssetId(asset.getId());
        this.selected.setAssetName(asset.getName());
        this.selected.setCaseOwnerId(asset.getAssetOwnerId());
        this.selected.setCaseOwnerName(asset.getAssetOwnerName());
    }
    
    public void prepareEditWorkOrderStep(){
        WorkOrderStepController woStepController = WebUtil.getBean(WorkOrderStepController.class);
        woStepController.setSelectedByWorkOrder(selected);
    }
    
    private int activeTabId = 0;
    public int getActiveTabId() {
        return activeTabId;
    }
    public void setActiveTabId(int activeTabId) {
        this.activeTabId = activeTabId;
    }
    
    public void onTabChanged(TabChangeEvent event) {
        String tabName = event.getTab().getId();
        if("tabCloseOrder".equals(tabName))
            activeTabId = 1;
        else if("TransferOrder".equals(tabName))
            activeTabId = 2;
        else
            activeTabId = 0;
    }
    
}