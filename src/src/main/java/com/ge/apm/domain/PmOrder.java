/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "pm_order")
public class PmOrder implements Serializable {

    public static final int LENGTH = 20;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private int siteId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private int hospitalId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creator_id")
    private int creatorId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "creator_name")
    private String creatorName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "owner_id")
    private Integer ownerId;
    @Size(max = 16)
    @Column(name = "owner_name")
    private String ownerName;
    @Column(name = "owner_org_id")
    private Integer ownerOrgId;
    @Size(max = 64)
    @Column(name = "owner_org_name")
    private String ownerOrgName;
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_finished")
    private boolean isFinished;
    @Size(max = 256)
    @Column(name = "comments")
    private String comments;
    @Column(name = "next_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextTime;
    @Size(max = 128)
    @Column(name = "report_url")
    private String reportUrl;

    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "file_id")
    private Integer fileId;
    
    @Column(name = "plan_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planTime;  //计划时间

    @Column(name = "nearest_sr_days")
    private Integer nearestSrDays;//此次保养后，最近的报修距此保养天数
    @Column(name = "nearest_sr_id")
    private Integer nearestSrId;//此次保养后，最近的报修SR ID
    

    public Integer getNearestSrDays() {
		return nearestSrDays;
	}

	public void setNearestSrDays(Integer nearestSrDays) {
		this.nearestSrDays = nearestSrDays;
	}

	public Integer getNearestSrId() {
		return nearestSrId;
	}

	public void setNearestSrId(Integer nearestSrId) {
		this.nearestSrId = nearestSrId;
	}

	public PmOrder() {
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public PmOrder(Integer id, int siteId, String name, int creatorId, String creatorName, Date createTime, boolean isFinished) {
        this.id = id;
        this.siteId = siteId;
        this.name = name;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.isFinished = isFinished;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getOwnerOrgId() {
        return ownerOrgId;
    }

    public void setOwnerOrgId(Integer ownerOrgId) {
        this.ownerOrgId = ownerOrgId;
    }

    public String getOwnerOrgName() {
        return ownerOrgName;
    }

    public void setOwnerOrgName(String ownerOrgName) {
        this.ownerOrgName = ownerOrgName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }
    
    public String getReportUrlInShort(){
        if(this.reportUrl == null){
        	return "";
        }
        if(this.reportUrl.length() < LENGTH){
        	return this.reportUrl;
        }
        return   this.reportUrl.substring(0,LENGTH)+"...";
    }
    
    public String getCommentsInShort(){
        if(this.comments == null){
        	return "";
        }
        if(this.comments.length() < LENGTH){
        	return this.comments;
        }
        return   this.comments.substring(0,LENGTH)+"...";
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PmOrder)) {
            return false;
        }
        PmOrder other = (PmOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.PmOrder[ id=" + id + " ]";
    }
    
}
