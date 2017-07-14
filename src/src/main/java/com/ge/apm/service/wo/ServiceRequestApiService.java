/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.service.utils.*;
import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author 212579464
 */
@Component
public class ServiceRequestApiService extends MicroServiceInvoker {

    @Value("#{urlProperties.url_serviceRequestCreate}")
    private String url_serviceRequestCreate;

    public Map<String, Object> createServiceRequest(String token, V2_ServiceRequest newServiceRequest, AssetInfo asset) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);

//        Map<String, String> postParameters = new HashMap<String, String>();
//        postParameters.put("urlFile", url);
//        postParameters.put("fileName", fileName);
        ServiceRequestForm formData = new ServiceRequestForm();
        formData.setAssetId(asset.getId());
        formData.setAssetStatus(String.valueOf(asset.getStatus()));
        if (null != newServiceRequest.getConfirmedDownTime()) {
            formData.setConfirmedDownTime(TimeUtils.getStrDate(newServiceRequest.getConfirmedDownTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        formData.setPriority(newServiceRequest.getCasePriority());
        formData.setRequestReason(newServiceRequest.getRequestReason());

        HttpEntity<ServiceRequestForm> requestEntity = new HttpEntity<ServiceRequestForm>(formData, headers);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url_serviceRequestCreate, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            return data;
        }
        return null;
    }

}

class ServiceRequestForm {

    Integer assetId;
    String requestReason;
    String voiceId;
    List<V2_BlobObject> attachments;
    Integer priority;
    String assetStatus;
    String confirmedDownTime;
    String userName;
    String telephone;
    String wechatId;
    String nickName;

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(String confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }
}
