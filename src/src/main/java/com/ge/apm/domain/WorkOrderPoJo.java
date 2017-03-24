package com.ge.apm.domain;

/**
 * Created by lsg on 23/3/2017.
 */
public class WorkOrderPoJo {
    Integer assetId;
    String woId;
    String  strDate;
    String desc;
    String estimatedCloseTime;
    String assigneeId;

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getWoId() {
        return woId;
    }

    public void setWoId(String woId) {
        this.woId = woId;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
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
}
