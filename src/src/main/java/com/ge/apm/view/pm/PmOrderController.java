package com.ge.apm.view.pm;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.PmOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.PmOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class PmOrderController extends JpaCRUDController<PmOrder> {

    PmOrderRepository dao = null;
    
    UserAccount currentUser;

    @Override
    protected void init() {
        dao = WebUtil.getBean(PmOrderRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected PmOrderRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<PmOrder> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<PmOrder> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    @Override
    public void onBeforeSave(PmOrder po) {
    	po.setSiteId(this.currentUser.getSiteId());//device site or currentUser site?
    	po.setCreatorId(this.currentUser.getId());
    	po.setCreatorName(this.currentUser.getName());
//    	po.setOwnerId(this.currentUser.getId());
//    	po.setOwnerName(this.currentUser.getName());
    	po.setOwnerOrgId(this.currentUser.getOrgInfoId());//orgId from where?
    	po.setCreateTime(TimeUtil.timeNowInDefaultTimeZone().toDate());
    }
   
    
    @Override
    public void onAfterNewObject(PmOrder object, boolean isOK) {
    	if(isOK){
    		this.selected = null;
    	}
    }
    
    public void onCancel(){
    	this.selected = null;
    	this.onBeforeNewObject(null);
    }
    
    @Override
    public void onAfterUpdateObject(PmOrder object, boolean isOK) {
    	if(isOK){
    		this.selected = null;
    	}
    }
    
//    @Override
//    public void onBeforeNewObject(PmOrder object) {
//    	this.selected = null;
//    }

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;
        if(asset==null) return;
        
        if(this.selected==null) return;
        this.selected.setAssetId(asset.getId());
        this.selected.setAssetName(asset.getName());
        this.selected.setOwnerId(asset.getAssetOwnerId());
        this.selected.setOwnerName(asset.getAssetOwnerName());
    }
    
    
/*
    @Override
    public void onBeforeNewObject(PmOrder object) {
    }
    
    @Override
    public void onAfterNewObject(PmOrder object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(PmOrder object) {
    }
    
    @Override
    public void onAfterUpdateObject(PmOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(PmOrder object) {
    }
    
    @Override
    public void onAfterDeleteObject(PmOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(PmOrder object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
    

}