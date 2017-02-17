package com.ge.apm.domain;

import org.joda.time.DateTime;

public class DownTimeAsset {
	private Integer assetId;//资产id
	private DateTime requestTime;//工单创建时间
	private DateTime downTime;//宕机时间
	private Boolean isCal;//是否已计算
	
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
	public DateTime getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(DateTime requestTime) {
		this.requestTime = requestTime;
	}
	public DateTime getDownTime() {
		return downTime;
	}
	public void setDownTime(DateTime downTime) {
		this.downTime = downTime;
	}
	
}
