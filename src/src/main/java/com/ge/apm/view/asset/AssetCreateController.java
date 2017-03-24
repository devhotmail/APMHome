/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeAttachment;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetCreateService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetCreateController {

    private AssetInfo assetInfo;
    private QrCodeLib createRequest;
    private AssetCreateService acServie;

    private Integer[] selectedPictures;

    private UserAccount owner;
    
    List<QrCodeAttachment> pictureList;

    @PostConstruct
    protected void init() {
        String qrCode = WebUtil.getRequestParameter("qrCode");
        acServie = WebUtil.getBean(AssetCreateService.class);
        createRequest = acServie.getCreateRequest(qrCode);
        
        
        assetInfo = new AssetInfo();
        assetInfo.setSiteId(createRequest.getSiteId());
        assetInfo.setHospitalId(createRequest.getHospitalId());
        assetInfo.setQrCode(qrCode);
        assetInfo.setIsValid(true);
        assetInfo.setStatus(1);
//        pictureList = getPicturesList();
        
        pictureList = acServie.getQrCodePictureList(qrCode);

    }

//    private List<Integer> getPicturesList() {
//
//        
//        List<Integer> pictureList = acServie.getQrCodeArrachList(qrCode);
////        List<Integer> pictureList = new ArrayList();
//        pictureList.add(2);
//        pictureList.add(5);
//        pictureList.add(24);
//        return pictureList;
//    }

    public List<OrgInfo> getClinicalDeptList() {
        return acServie.getClinicalDeptList(assetInfo.getHospitalId());
    }

    public String getHospitalName() {
        return acServie.getHospitalName(assetInfo.getHospitalId());
    }

    public void applyChange() {
        
        assetInfo.setAssetOwnerId(owner.getId());
        assetInfo.setAssetOwnerName(owner.getName());
        assetInfo.setAssetOwnerTel(owner.getTelephone());
        
        Boolean res = acServie.CreateAeest(assetInfo);
        if (res) {
            acServie.createAttachments(assetInfo,selectedPictures);
            acServie.updateQrLib(assetInfo.getQrCode(),3);
            WebUtil.addSuccessMessage("创建成功");
        } else {
            WebUtil.addErrorMessage("创建失败");
        }
    }
    

    public List<UserAccount> getOwnerList() {
        List<UserAccount> res = acServie.getAssetOnwers(assetInfo.getSiteId(), assetInfo.getHospitalId());
        return res;
    }

    //getter and setter
    public QrCodeLib getCreateRequest() {
        return createRequest;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public Integer[] getSelectedPictures() {
        return selectedPictures;
    }

    public void setSelectedPictures(Integer[] selectedPictures) {
        this.selectedPictures = selectedPictures;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public List<QrCodeAttachment> getPictureList() {
        return pictureList;
    }
    

}
