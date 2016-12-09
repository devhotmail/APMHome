package com.ge.apm.view.wo;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.service.uaa.UaaService;
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

    private boolean filterByLoginUser = false;
    public boolean isFilterByLoginUser() {
        return filterByLoginUser;
    }
    public void setFilterByLoginUser(boolean filterByLoginUser) {
        this.filterByLoginUser = filterByLoginUser;
    }
    
    public void setLoginUserFilter(){
        if(filterByLoginUser){
            if (this.searchFilters == null) 
                this.searchFilters = new ArrayList<SearchFilter>();
        
            this.searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));
        }
    }

    @Override
    protected Page<WorkOrder> loadData(PageRequest pageRequest) {
        //only show my tasks
        //setSiteFilter();
        setHospitalFilter();
        setLoginUserFilter();
        
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String getMyWorkOrderId() {
        return null;
    }
    
    public void setMyWorkOrderId(String selectedWorkOrderId) {
        int workOrder = NumberUtils.toInt(selectedWorkOrderId, -1);
        if(workOrder<0){
            this.selected = null;
        }
        else{
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
        woStepController.setSelectedByWorkOrder(selected);
    }
    
    public List<UserAccount> getHospitalUserList(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getUserList(loginUser.getHospitalId());
    }
    
    public void prepareCreateWorkOrder() throws InstantiationException, IllegalAccessException {
        if(!WebUtil.isHttpGetRequest()) return;

        this.prepareCreate();
        
        selected.setSiteId(loginUser.getSiteId());
        selected.setHospitalId(loginUser.getHospitalId());
        selected.setCreatorId(loginUser.getId());
        selected.setCreatorName(loginUser.getName());
        
        selected.setCreateTime(TimeUtil.now());
        selected.setCurrentStepId(1);
        
        selected.setCasePriority(3);

        selected.setTotalManHour(0);
        selected.setTotalPrice(0.0);
        
        selected.setIsInternal(true);
        selected.setIsClosed(false);

        onSelectWorkOrder();
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
        if("TransferOrder".equals(tabName))
            activeTabId = 1;
        else if("tabCloseOrder".equals(tabName))
            activeTabId = 2;
        else
            activeTabId = 0;
    }
    
    private String filterIsClosed = null;

    public String getFilterIsClosed() {
        return filterIsClosed;
    }

    public void setFilterIsClosed(String filterIsClosed) {
        this.filterIsClosed = filterIsClosed;
    }
    
    public void setIsClosedFilter(){
        if(filterIsClosed==null) return;
        if (!("true".equals(filterIsClosed) || "false".equals(filterIsClosed))) return;
        
        if(searchFilters==null) searchFilters = new ArrayList<SearchFilter>();
        setSiteFilter();
        searchFilters.add(new SearchFilter("isClosed", SearchFilter.Operator.EQ, Boolean.parseBoolean(filterIsClosed)));
    }
    
}