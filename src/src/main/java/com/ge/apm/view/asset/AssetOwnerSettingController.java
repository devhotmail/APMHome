/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AssetOwnerService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetOwnerSettingController {
    
    
    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets;
    private AssetCreateService acServie;
    
    private AssetOwnerService aoService;
    
    private AssetInfo queryAsset;
    
    private UserAccount owner;
    private UserAccount owner2;
    
    
    
    @PostConstruct
    protected void init() {
        
        acServie = WebUtil.getBean(AssetCreateService.class);
        aoService = WebUtil.getBean(AssetOwnerService.class);
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        queryAsset = new AssetInfo();
        queryAsset.setSiteId(currentUser.getSiteId());
        queryAsset.setHospitalId(currentUser.getHospitalId());
        sourceAssets = aoService.getAssetList(queryAsset);
        targetAssets = new ArrayList();
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }
    
     public void onAssetsTransfer(TransferEvent event) {
         
//        for(Object item : event.getItems()) {
//            builder.append(((Theme) item).getName()).append("<br />");
//        }
         
    } 
     
    public List<OrgInfo> getClinicalDeptList() {
        return acServie.getClinicalDeptList(queryAsset.getHospitalId());
    }
    
    
    public List<UserAccount> getOwnerList() {
        List<UserAccount> res = acServie.getAssetOnwers(queryAsset.getSiteId(), queryAsset.getHospitalId());
        return res;
    }
    
    
    //getter and setter
    public DualListModel getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public AssetInfo getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(AssetInfo queryAsset) {
        this.queryAsset = queryAsset;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public UserAccount getOwner2() {
        return owner2;
    }

    public void setOwner2(UserAccount owner2) {
        this.owner2 = owner2;
    }
    
    
    
}
