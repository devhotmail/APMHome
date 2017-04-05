/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wechat;

import com.ge.apm.domain.QrCodeAttachment;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AttachmentFileService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class CreateInfoController {

    private AssetCreateService acServie;
    private AttachmentFileService attachFileService;
    private QrCodeLib createdRequest;
    List<QrCodeAttachment> pictureList;
    List<QrCodeAttachment> audioList;
    List<String> commentsList;
    List<String> rejectHistoryList;
    
    List<String> wxAudioList;

    @PostConstruct
    protected void init() {

        acServie = WebUtil.getBean(AssetCreateService.class);
        attachFileService = WebUtil.getBean(AttachmentFileService.class);
        String qrCode = WebUtil.getRequestParameter("qrCode");
        if (null != qrCode) {
            createdRequest = acServie.getCreateRequest(qrCode);
            pictureList = acServie.getQrCodePictureList(createdRequest.getId());
            audioList = acServie.getQrCodeAudioList(createdRequest.getId());
            wxAudioList = new ArrayList();
            for(QrCodeAttachment item : audioList){
                try {
                    wxAudioList.add(acServie.pushVoiceToWechat(item.getFileId()));
                } catch (Exception ex) {
                    WebUtil.addErrorMessage("声音加载失败");
                }
            }
            rejectHistoryList = new ArrayList();
            if (null != createdRequest.getFeedback()) {
                Collections.addAll(rejectHistoryList, createdRequest.getFeedback().split("//"));
            }
            commentsList = new ArrayList();
            if (null != createdRequest.getComment()) {
                Collections.addAll(commentsList, createdRequest.getComment().split("\\|"));
            }
        }
    }
    
    
    

    public String getAudioBase64(Integer fileId) {
        return attachFileService.getBase64File(fileId);
    }

    public QrCodeLib getCreatedRequest() {
        return createdRequest;
    }

    public List<QrCodeAttachment> getPictureList() {
        return pictureList;
    }

    public List<QrCodeAttachment> getAudioList() {
        return audioList;
    }

    public List<String> getCommentsList() {
        return commentsList;
    }

    public List<String> getRejectHistoryList() {
        return rejectHistoryList;
    }

    public List<String> getWxAudioList() {
        return wxAudioList;
    }
    
}
