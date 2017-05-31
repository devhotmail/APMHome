/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "v2_service_request")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "V2ServiceRequest.findAll", query = "SELECT v FROM V2ServiceRequest v")
    , @NamedQuery(name = "V2ServiceRequest.findById", query = "SELECT v FROM V2ServiceRequest v WHERE v.id = :id")
    , @NamedQuery(name = "V2ServiceRequest.findByCreatedBy", query = "SELECT v FROM V2ServiceRequest v WHERE v.createdBy = :createdBy")
    , @NamedQuery(name = "V2ServiceRequest.findByCreatedDate", query = "SELECT v FROM V2ServiceRequest v WHERE v.createdDate = :createdDate")
    , @NamedQuery(name = "V2ServiceRequest.findByLastModifiedBy", query = "SELECT v FROM V2ServiceRequest v WHERE v.lastModifiedBy = :lastModifiedBy")
    , @NamedQuery(name = "V2ServiceRequest.findByLastModifiedDate", query = "SELECT v FROM V2ServiceRequest v WHERE v.lastModifiedDate = :lastModifiedDate")
    , @NamedQuery(name = "V2ServiceRequest.findByAssetId", query = "SELECT v FROM V2ServiceRequest v WHERE v.assetId = :assetId")
    , @NamedQuery(name = "V2ServiceRequest.findByAssetName", query = "SELECT v FROM V2ServiceRequest v WHERE v.assetName = :assetName")
    , @NamedQuery(name = "V2ServiceRequest.findByCasePriority", query = "SELECT v FROM V2ServiceRequest v WHERE v.casePriority = :casePriority")
    , @NamedQuery(name = "V2ServiceRequest.findByCloseTime", query = "SELECT v FROM V2ServiceRequest v WHERE v.closeTime = :closeTime")
    , @NamedQuery(name = "V2ServiceRequest.findByConfirmedDownTime", query = "SELECT v FROM V2ServiceRequest v WHERE v.confirmedDownTime = :confirmedDownTime")
    , @NamedQuery(name = "V2ServiceRequest.findByConfirmedUpTime", query = "SELECT v FROM V2ServiceRequest v WHERE v.confirmedUpTime = :confirmedUpTime")
    , @NamedQuery(name = "V2ServiceRequest.findByEstimatedCloseTime", query = "SELECT v FROM V2ServiceRequest v WHERE v.estimatedCloseTime = :estimatedCloseTime")
    , @NamedQuery(name = "V2ServiceRequest.findByFromDeptId", query = "SELECT v FROM V2ServiceRequest v WHERE v.fromDeptId = :fromDeptId")
    , @NamedQuery(name = "V2ServiceRequest.findByFromDeptName", query = "SELECT v FROM V2ServiceRequest v WHERE v.fromDeptName = :fromDeptName")
    , @NamedQuery(name = "V2ServiceRequest.findByHospitalId", query = "SELECT v FROM V2ServiceRequest v WHERE v.hospitalId = :hospitalId")
    , @NamedQuery(name = "V2ServiceRequest.findByHospitalName", query = "SELECT v FROM V2ServiceRequest v WHERE v.hospitalName = :hospitalName")
    , @NamedQuery(name = "V2ServiceRequest.findByRequestReason", query = "SELECT v FROM V2ServiceRequest v WHERE v.requestReason = :requestReason")
    , @NamedQuery(name = "V2ServiceRequest.findByRequestReasonVoice", query = "SELECT v FROM V2ServiceRequest v WHERE v.requestReasonVoice = :requestReasonVoice")
    , @NamedQuery(name = "V2ServiceRequest.findByRequestTime", query = "SELECT v FROM V2ServiceRequest v WHERE v.requestTime = :requestTime")
    , @NamedQuery(name = "V2ServiceRequest.findByRequestorId", query = "SELECT v FROM V2ServiceRequest v WHERE v.requestorId = :requestorId")
    , @NamedQuery(name = "V2ServiceRequest.findByRequestorName", query = "SELECT v FROM V2ServiceRequest v WHERE v.requestorName = :requestorName")
    , @NamedQuery(name = "V2ServiceRequest.findByReponseTime", query = "SELECT v FROM V2ServiceRequest v WHERE v.reponseTime = :reponseTime")
    , @NamedQuery(name = "V2ServiceRequest.findBySiteId", query = "SELECT v FROM V2ServiceRequest v WHERE v.siteId = :siteId")
    , @NamedQuery(name = "V2ServiceRequest.findByStatus", query = "SELECT v FROM V2ServiceRequest v WHERE v.status = :status")
    , @NamedQuery(name = "V2ServiceRequest.findByEquipmentTaker", query = "SELECT v FROM V2ServiceRequest v WHERE v.equipmentTaker = :equipmentTaker")
    , @NamedQuery(name = "V2ServiceRequest.findByTakeTime", query = "SELECT v FROM V2ServiceRequest v WHERE v.takeTime = :takeTime")})
public class V2ServiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "id")
    private String id;
    @Size(max = 50)
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Size(max = 50)
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "asset_id")
    private int assetId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "asset_name")
    private String assetName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "case_priority")
    private int casePriority;
    @Column(name = "close_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closeTime;
    @Column(name = "confirmed_down_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedDownTime;
    @Column(name = "confirmed_up_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedUpTime;
    @Column(name = "estimated_close_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedCloseTime;
    @Column(name = "from_dept_id")
    private Integer fromDeptId;
    @Size(max = 64)
    @Column(name = "from_dept_name")
    private String fromDeptName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private int hospitalId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "hospital_name")
    private String hospitalName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "request_reason")
    private String requestReason;
    @Column(name = "request_reason_voice")
    private Integer requestReasonVoice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "request_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requestor_id")
    private int requestorId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "requestor_name")
    private String requestorName;
    @Column(name = "reponse_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reponseTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private int siteId;
    @Column(name = "status")
    private Integer status;
    @Size(max = 255)
    @Column(name = "equipment_taker")
    private String equipmentTaker;
    @Column(name = "take_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takeTime;

    public V2ServiceRequest() {
    }

    public V2ServiceRequest(String id) {
        this.id = id;
    }

    public V2ServiceRequest(String id, int assetId, String assetName, int casePriority, int hospitalId, String hospitalName, String requestReason, Date requestTime, int requestorId, String requestorName, int siteId) {
        this.id = id;
        this.assetId = assetId;
        this.assetName = assetName;
        this.casePriority = casePriority;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.requestReason = requestReason;
        this.requestTime = requestTime;
        this.requestorId = requestorId;
        this.requestorName = requestorName;
        this.siteId = siteId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public int getCasePriority() {
        return casePriority;
    }

    public void setCasePriority(int casePriority) {
        this.casePriority = casePriority;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public Date getConfirmedUpTime() {
        return confirmedUpTime;
    }

    public void setConfirmedUpTime(Date confirmedUpTime) {
        this.confirmedUpTime = confirmedUpTime;
    }

    public Date getEstimatedCloseTime() {
        return estimatedCloseTime;
    }

    public void setEstimatedCloseTime(Date estimatedCloseTime) {
        this.estimatedCloseTime = estimatedCloseTime;
    }

    public Integer getFromDeptId() {
        return fromDeptId;
    }

    public void setFromDeptId(Integer fromDeptId) {
        this.fromDeptId = fromDeptId;
    }

    public String getFromDeptName() {
        return fromDeptName;
    }

    public void setFromDeptName(String fromDeptName) {
        this.fromDeptName = fromDeptName;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public Integer getRequestReasonVoice() {
        return requestReasonVoice;
    }

    public void setRequestReasonVoice(Integer requestReasonVoice) {
        this.requestReasonVoice = requestReasonVoice;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public int getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(int requestorId) {
        this.requestorId = requestorId;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public void setRequestorName(String requestorName) {
        this.requestorName = requestorName;
    }

    public Date getReponseTime() {
        return reponseTime;
    }

    public void setReponseTime(Date reponseTime) {
        this.reponseTime = reponseTime;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public Date getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Date takeTime) {
        this.takeTime = takeTime;
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
        if (!(object instanceof V2ServiceRequest)) {
            return false;
        }
        V2ServiceRequest other = (V2ServiceRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.V2ServiceRequest[ id=" + id + " ]";
    }
    
}
