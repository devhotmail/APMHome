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
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.service.wechat.CoreService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private AttachmentFileService attachFileService;

    private Integer[] selectedPictures;

    private UserAccount owner;

    private String rejectText;
    private List<String> rejectTextHistory;

    List<QrCodeAttachment> pictureList;
    List<QrCodeAttachment> audioList;

    @PostConstruct
    protected void init() {
        String qrCode = WebUtil.getRequestParameter("qrCode");
        acServie = WebUtil.getBean(AssetCreateService.class);
        attachFileService = WebUtil.getBean(AttachmentFileService.class);
        createRequest = acServie.getCreateRequest(qrCode);

        assetInfo = new AssetInfo();
        assetInfo.setSiteId(createRequest.getSiteId());
        assetInfo.setHospitalId(createRequest.getHospitalId());
        assetInfo.setQrCode(qrCode);
        assetInfo.setIsValid(true);
        assetInfo.setStatus(1);
        pictureList = acServie.getQrCodePictureList(createRequest.getId());
        audioList = acServie.getQrCodeAudioList(createRequest.getId());

        rejectTextHistory = new ArrayList();
        if (null != createRequest.getFeedback()) {
            Collections.addAll(rejectTextHistory, createRequest.getFeedback().split("//"));
        }

    }

    public List<OrgInfo> getClinicalDeptList() {
        return acServie.getClinicalDeptList(assetInfo.getHospitalId());
    }

    public String getHospitalName() {
        return acServie.getHospitalName(assetInfo.getHospitalId());
    }

    public String applyChange() {
        if (owner != null) {
            assetInfo.setAssetOwnerId(owner.getId());
            assetInfo.setAssetOwnerName(owner.getName());
            assetInfo.setAssetOwnerTel(owner.getTelephone());
        }

        Boolean res = acServie.CreateAeest(assetInfo);
        if (res) {
            acServie.createAttachments(assetInfo, selectedPictures);
            acServie.updateQrLib(assetInfo.getQrCode(), 3);
            WebUtil.addSuccessMessage("创建成功");
            return "RequestsList?faces-redirect=true";
        } else {
            WebUtil.addErrorMessage("创建失败");
            return "";
        }
    }

    public String getAudioBase64(Integer fileId) {
        String audio = attachFileService.getBase64File(fileId);
        return audio;
    }

    public void returnBack() {
        acServie.rejectReturn(createRequest, rejectText);
        this.rejectTextHistory.add(TimeUtils.getStrDate(new Date(),"[yyyy-MM-dd HH:mm:ss]").concat(rejectText));
        rejectText = "";
        WebUtil.addSuccessMessage("消息已发送");
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

    public List<QrCodeAttachment> getAudioList() {
        return audioList;
    }

    public String getRejectText() {
        return rejectText;
    }

    public void setRejectText(String rejectText) {
        this.rejectText = rejectText;
    }

    public List<String> getRejectTextHistory() {
        return rejectTextHistory;
    }

}
