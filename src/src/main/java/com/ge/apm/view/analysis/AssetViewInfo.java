package com.ge.apm.view.analysis;

import com.google.common.base.MoreObjects;

public class AssetViewInfo {
    private String asset;
    private String stepId;
    private String stepName;
    private String owner;
    private String downTime;
    private String downReason;
    private String warrantyDate;
    private String pmDate;
    private String meteringDate;
    private String qaDate;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("asset", asset).add("stepId", stepId).add("stepName", stepName).add("owner", owner)
                .add("downTime", downTime).add("downReason", downReason).add("warrantyDate", warrantyDate)
                .add("pmDate", pmDate).add("meteringDate", meteringDate).add("qaDate", qaDate).toString();
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDownTime() {
        return downTime;
    }

    public void setDownTime(String downTime) {
        this.downTime = downTime;
    }

    public String getDownReason() {
        return downReason;
    }

    public void setDownReason(String downReason) {
        this.downReason = downReason;
    }

    public String getWarrantyDate() {
        return warrantyDate;
    }

    public void setWarrantyDate(String warrantyDate) {
        this.warrantyDate = warrantyDate;
    }

    public String getPmDate() {
        return pmDate;
    }

    public void setPmDate(String pmDate) {
        this.pmDate = pmDate;
    }

    public String getMeteringDate() {
        return meteringDate;
    }

    public void setMeteringDate(String meteringDate) {
        this.meteringDate = meteringDate;
    }

    public String getQaDate() {
        return qaDate;
    }

    public void setQaDate(String qaDate) {
        this.qaDate = qaDate;
    }
}
