package com.ge.apm.domain;

import java.io.Serializable;
import org.joda.time.DateTime;

public class AssetCostStatistics implements Serializable{

	private static final long serialVersionUID = -1L;
	private Integer id;
	private Integer assetId;//资产id
	private Integer siteId;//租户id
	private Integer hospitalId;//医院id
	private Integer assetGroup;//资产分组
	private Integer deptId;//科室id
	private Integer supplierId;//供应商id
	private String assetName;//资产名称
	private Double maintenanceCost;//维修费用
	private Double deprecationCost;//折旧费用
	private Integer downTime;//宕机时长
	private Integer workOrderCount;
	private DateTime created;//createTime
	private DateTime lastModified;//lastModifyTime
	private Integer rating;//综合排名
	private Integer status;//资产状态
	
	//========wo=============//
	private Integer woId;//订单id
	private DateTime requestTime;//报修时间
	private DateTime confirmedDownTime;//宕机时间
	private Boolean woStatus;//工单状态
	
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
	public Boolean getWoStatus() {
		return woStatus;
	}
	public void setWoStatus(Boolean woStatus) {
		this.woStatus = woStatus;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public DateTime getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(DateTime requestTime) {
		this.requestTime = requestTime;
	}
	public DateTime getConfirmedDownTime() {
		return confirmedDownTime;
	}
	public void setConfirmedDownTime(DateTime confirmedDownTime) {
		this.confirmedDownTime = confirmedDownTime;
	}
	public Integer getAssetId() {
		return assetId;
	}
	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}
	public Integer getWoId() {
		return woId;
	}
	public void setWoId(Integer woId) {
		this.woId = woId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Integer getAssetGroup() {
		return assetGroup;
	}
	public void setAssetGroup(Integer assetGroup) {
		this.assetGroup = assetGroup;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public Double getMaintenanceCost() {
		return maintenanceCost;
	}
	public void setMaintenanceCost(Double maintenanceCost) {
		this.maintenanceCost = maintenanceCost;
	}
	public Double getDeprecationCost() {
		return deprecationCost;
	}
	public void setDeprecationCost(Double deprecationCost) {
		this.deprecationCost = deprecationCost;
	}
	public Integer getDownTime() {
		return downTime;
	}
	public void setDownTime(Integer downTime) {
		this.downTime = downTime;
	}
	public Integer getWorkOrderCount() {
		return workOrderCount;
	}
	public void setWorkOrderCount(Integer workOrderCount) {
		this.workOrderCount = workOrderCount;
	}
	public DateTime getCreated() {
		return created;
	}
	public void setCreated(DateTime created) {
		this.created = created;
	}
	public DateTime getLastModified() {
		return lastModified;
	}
	public void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	
	
	
}
