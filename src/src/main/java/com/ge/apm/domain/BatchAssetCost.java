package com.ge.apm.domain;

import java.util.Date;
import java.util.List;

public class BatchAssetCost {
	private List<Integer> assetIds;
	private String calDay;
	private Boolean isAll = false;
	
	public Boolean getIsAll() {
		return isAll;
	}
	public void setIsAll(Boolean isAll) {
		this.isAll = isAll;
	}
	public List<Integer> getAssetIds() {
		return assetIds;
	}
	public void setAssetIds(List<Integer> assetIds) {
		this.assetIds = assetIds;
	}
	public String getCalDay() {
		return calDay;
	}
	public void setCalDay(String calDay) {
		this.calDay = calDay;
	}
	
}
