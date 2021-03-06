package com.ge.apm.view.wo;

import com.ge.apm.dao.AssetInfoRepository;
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
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
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
    AssetInfoRepository assetDao = null;
    private Integer assetIdFromUrl;

    @Override
    protected void init() {
        loginUser = UserContextService.getCurrentUserAccount();

        this.filterByHospital = true;
        this.filterBySite = true;
        if (UserContextService.checkRole("MultiHospital")) {
            this.filterByHospital = false;
        }

        dao = WebUtil.getBean(WorkOrderRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
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
        setLoginUserFilter();
/*        
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String assetId = (String)UrlEncryptController.getValueFromMap(encodeStr,"assetId");
        if (assetId != null && !"".equals(assetId)) {
            this.assetIdFromUrl = Integer.parseInt(assetId);
        }
        if (assetIdFromUrl != null){
            searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetIdFromUrl));
        }
*/        
        this.selected = null;
        
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
    
    public List<UserAccount> getUsersInHospital(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getUserList(loginUser.getHospitalId());
    }

    public List<UserAccount> getUsersWithAssetHeadOrStaffRole(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getUsersWithAssetHeadOrStaffRole(loginUser.getHospitalId());
    }

    public List<UserAccount> getUsersWithAssetStaffRole(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getUsersWithAssetStaffRole(loginUser.getHospitalId());
    }
    
    public void prepareCreateWorkOrder() throws InstantiationException, IllegalAccessException {
        if(!WebUtil.isHttpGetRequest()) return;

        this.prepareCreate();
        
        selected.setSiteId(loginUser.getSiteId());
//        hospitalId改为从设备上面取
//        selected.setHospitalId(loginUser.getHospitalId());
        selected.setCreatorId(loginUser.getId());
        selected.setCreatorName(loginUser.getName());
        
        selected.setCreateTime(TimeUtil.now());
        selected.setCurrentStepId(1);
        
        selected.setCasePriority(3);

        selected.setTotalManHour(0);
        selected.setTotalPrice(0.0);
        
        selected.setIsInternal(true);
        selected.setIsClosed(false);
        
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String assetId = (String)UrlEncryptController.getValueFromMap(encodeStr,"assetId");
        if (assetId != null && !"".equals(assetId)) {
            selected.setAssetId(Integer.parseInt(assetId));
            selected.setAssetName((String) UrlEncryptController.getValueFromMap(encodeStr,"assetName"));
            selected.setCaseOwnerId(Integer.parseInt((String)UrlEncryptController.getValueFromMap(encodeStr,"assetOwnerId")));
            selected.setCaseOwnerName((String) UrlEncryptController.getValueFromMap(encodeStr,"assetOwnerName"));
            selected.setHospitalId(getHospitalIdFromAsset(Integer.parseInt(assetId)));
        }

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
        selected.setHospitalId(asset.getHospitalId());
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

    public String filterAssetId;

    public String getFilterAssetId() {
        return filterAssetId;
    }

    public void setFilterAssetId(String filterAssetId) {
        this.filterAssetId = filterAssetId;
    }
    
    public void setViewFilter(){
        if(filterIsClosed!=null){
            if (("true".equals(filterIsClosed) || "false".equals(filterIsClosed))){
                if(searchFilters==null) searchFilters = new ArrayList<SearchFilter>();
                searchFilters.add(new SearchFilter("isClosed", SearchFilter.Operator.EQ, Boolean.parseBoolean(filterIsClosed)));
            }
        }
        
        if(filterAssetId!=null){
            if(searchFilters==null) searchFilters = new ArrayList<SearchFilter>();
            try{
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, Integer.parseInt(filterAssetId)));
            }
            catch(Exception ex){
            }
        }        
    }
    
    private int assetStatus;

    public int getAssetStatus() {
        if (assetStatus == 0) {
            if (this.selected != null && this.selected.getAssetId() != null) {
                AssetInfo asset = assetDao.findById(this.selected.getAssetId());
                if (asset != null){
                    assetStatus = asset.getStatus();
                }
            }
        }
        return assetStatus;
    }

    public void setAssetStatus(int assetStatus) {
        this.assetStatus = assetStatus;
    }
    
    public void assetStatusChange() {
        if(assetStatus == 0) return;
        AssetInfo asset = assetDao.findById(this.selected.getAssetId());
        asset.setStatus(assetStatus);
        assetDao.save(asset);
    }
    public void workOrderConfirmTimeChange(String changeType) {
        if (selected.getConfirmedUpTime()!=null&&selected.getConfirmedDownTime()!=null){
            if(selected.getConfirmedUpTime().before(selected.getConfirmedDownTime())){
                if ("up".equals(changeType)) {
                    WebUtil.addSuccessMessage("恢复可用时间不能早于停机时间。", "UpTime can not before than DownTime is uploaded.");
                    this.selected.setConfirmedUpTime(null);
                } else {
                    this.selected.setConfirmedUpTime(null);
                }
            } 
        }
        dao.save(this.selected);
    }

    public Integer getAssetIdFromUrl() {
        return assetIdFromUrl;
    }

    public void setAssetIdFromUrl(Integer assetIdFromUrl) {
        this.assetIdFromUrl = assetIdFromUrl;
    }
    
    private int getHospitalIdFromAsset(Integer id) {
        AssetInfo asset = assetDao.findById(id);
        if (asset == null) return -1;
        return asset.getHospitalId();
    }

}