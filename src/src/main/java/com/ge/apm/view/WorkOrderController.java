package com.ge.apm.view;

import com.ge.apm.dao.UserAccountRepository;
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
        setSiteFilter();
        setHospitalFilter();
        setLoginUserFilter();
        
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public boolean getIsInViewMode(){
        return (this.selected==null || this.selected.getCurrentStep()!=1);
    }
    
    public void onSelectWorkOrder(){
        System.err.println("**************** onSelectWorkOrder");
    }
    
    public List<UserAccount> getHospitalUserList(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getUserList(loginUser.getHospitalId());
    }
    
    @Override
    public void onBeforeNewObject(WorkOrder workOrder) {
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
    
}