package com.ge.apm.domain;

import java.util.List;

public class BatchAssetCost {
	private List<Integer> assetIds;
	private String calDay;
	private Boolean isAll = false;
	private String from;//起始日期
	private String to;//结束日期
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BatchAssetCost [assetIds=");
		builder.append(assetIds);
		builder.append(", calDay=");
		builder.append(calDay);
		builder.append(", isAll=");
		builder.append(isAll);
		builder.append(", from=");
		builder.append(from);
		builder.append(", to=");
		builder.append(to);
		builder.append("]");
		return builder.toString();
	}
	
}
