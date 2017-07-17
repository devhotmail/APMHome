/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.service.utils.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212579464
 */
@Component
public class ServiceRequestApiService extends MicroServiceInvoker {

    @Value("#{urlProperties.url_serviceRequestCreate}")
    private String url_serviceRequestCreate;
    @Value("#{urlProperties.url_workOrderAction}")
    private String url_workOrderAction;

    
    
    
    public String cancelWorkOrderAction(String token, String woId, String reason,Integer stepId) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);
        
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setDesc(reason);
        formData.setCurrentStepId(stepId);
        HttpEntity<WorkOrderActionForm> requestEntity = new HttpEntity<WorkOrderActionForm>(formData, headers);
        
        String url = url_workOrderAction.replace("{workOrderId}", woId).concat("?type=cancel");
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("data");
        } else {
            return null;
        }
    }
    
    public Map<String, Object> createServiceRequest(String token, V2_ServiceRequest newServiceRequest, AssetInfo asset) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);

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
        } else {
            return null;
        }
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

class WorkOrderActionForm {
	String serviceRequestId;
    String  strDate;
    String desc;
    String estimatedCloseTime;
    String assigneeId;
    String feedbackRating;
    String patProblems;
    String patActions;
    String patTests;
    String caseType;
    String confirmedDownTime;
    String confirmedUpTime;
    String assetStatus;
    String intExtType;
    Object stepDetail;
    Integer currentStepId;
    String equipmentTaker;
    String takeTime;    
    Integer repairType;
    List<V2_BlobObject> attachments;
    
    public String getServiceRequestId() {
		return serviceRequestId;
	}

	public void setServiceRequestId(String serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}

	public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEstimatedCloseTime() {
        return estimatedCloseTime;
    }

    public void setEstimatedCloseTime(String estimatedCloseTime) {
        this.estimatedCloseTime = estimatedCloseTime;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(String feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getPatProblems() {
        return patProblems;
    }

    public void setPatProblems(String patProblems) {
        this.patProblems = patProblems;
    }

    public String getPatActions() {
        return patActions;
    }

    public void setPatActions(String patActions) {
        this.patActions = patActions;
    }

    public String getPatTests() {
        return patTests;
    }

    public void setPatTests(String patTests) {
        this.patTests = patTests;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(String confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public String getConfirmedUpTime() {
        return confirmedUpTime;
    }

    public void setConfirmedUpTime(String confirmedUpTime) {
        this.confirmedUpTime = confirmedUpTime;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(String intExtType) {
        this.intExtType = intExtType;
    }

    public Object getStepDetail() {
        return stepDetail;
    }

    public void setStepDetail(Object stepDetail) {
        this.stepDetail = stepDetail;
    }

    public Integer getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(Integer currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

    public Integer getRepairType() {
        return repairType;
    }

    public void setRepairType(Integer repairType) {
        this.repairType = repairType;
    }
    
}