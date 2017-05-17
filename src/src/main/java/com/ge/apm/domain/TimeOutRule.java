package com.ge.apm.domain;

public class TimeOutRule {
	private Integer time;//workflowConfig中的超时时间,分钟为单位
	private Boolean isTimeout;//是否超时
	
	public Integer getTime() {
		return time;
	}
	public void setTime(Integer time) {
		this.time = time;
	}
	public Boolean getIsTimeout() {
		return isTimeout;
	}
	public void setIsTimeout(Boolean isTimeout) {
		this.isTimeout = isTimeout;
	}
	
}
