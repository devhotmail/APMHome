package com.ge.apm.view.pm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class PmOrderController extends JpaCRUDController<PmOrder> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PmOrderRepository dao = null;
    
    UserAccount currentUser;

    @Override
    protected void init() {
        dao = WebUtil.getBean(PmOrderRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.filterByHospital = true;
        this.filterBySite = true;
    }

    @Override
    protected PmOrderRepository getDAO() {
        return dao;
    }

    @Override
    public void onBeforeSave(PmOrder po) {
    	po.setSiteId(this.currentUser.getSiteId());
    	po.setHospitalId(this.currentUser.getHospitalId());
    	po.setCreatorId(this.currentUser.getId());
    	po.setCreatorName(this.currentUser.getName());
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
   

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;
        if(asset==null) return;
        
        if(this.selected==null) return;
        this.selected.setAssetId(asset.getId());
        this.selected.setAssetName(asset.getName());
        this.selected.setOwnerId(asset.getAssetOwnerId());
        this.selected.setOwnerName(asset.getAssetOwnerName());
    	this.selected.setOwnerOrgId(asset.getAssetOwnerId());
        OrgInfoRepository orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        OrgInfo orgInfo = orgInfoRepository.findById(asset.getAssetOwnerId());
        if(orgInfo != null){
        	this.selected.setOwnerOrgName(orgInfo.getName());
        }
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