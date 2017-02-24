package com.ge.apm.domain;

import java.util.Date;

public class DownTimeAsset {
	private Integer assetId;//资产id
	private Date requestTime;//工单创建时间
	private Date downTime;//宕机时间
	private Boolean isCal = false;//是否已计算
	
	public Boolean getIsCal() {
		return isCal;
	}
	public void setIsCal(Boolean isCal) {
		this.isCal = isCal;
	}
	public Integer getAssetId() {
		return assetId;
	}
	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}
	public Date getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}
	public Date getDownTime() {
		return downTime;
	}
	public void setDownTime(Date downTime) {
		this.downTime = downTime;
	}
	
}
