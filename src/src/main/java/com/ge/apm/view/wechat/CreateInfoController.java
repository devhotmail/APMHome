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
import java.io.IOException;
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

    private AssetCreateService acService;
    private AttachmentFileService attachFileService;
    private QrCodeLib createdRequest;
    List<QrCodeAttachment> pictureList;
    List<QrCodeAttachment> audioList;
    List<String> commentsList;
    List<String> rejectHistoryList;

    List<String> wxAudioList;

    private String openId;

    @PostConstruct
    protected void init() {

        acService = WebUtil.getBean(AssetCreateService.class);
        attachFileService = WebUtil.getBean(AttachmentFileService.class);
        String qrCode = WebUtil.getRequestParameter("qrCode");
        openId = WebUtil.getRequestParameter("openId");

        if (null != qrCode) {
            createdRequest = acService.getCreateRequest(qrCode);
            pictureList = acService.getQrCodePictureList(createdRequest.getId());
            audioList = acService.getQrCodeAudioList(createdRequest.getId());
            wxAudioList = new ArrayList();
            for (QrCodeAttachment item : audioList) {
                try {
                    wxAudioList.add(acService.pushVoiceToWechat(item.getFileId()));
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
            if (null == openId) {
                openId = createdRequest.getSubmitWechatId();
            }
        }
    }

    public String getAudioBase64(Integer fileId) {
        return attachFileService.getBase64File(fileId);
    }
    public String getAudioBase64Mp3(Integer fileId) {
        try {
            return attachFileService.getMp3Base64String(fileId);
        } catch (IOException ex) {
            WebUtil.addErrorMessage("声音加载失败");
            Logger.getLogger(CreateInfoController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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

    public String getOpenId() {
        return openId;
    }

}
