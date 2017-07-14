package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "v2_service_request")
public class V2_ServiceRequest extends JHipAbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "asset_name")
    private String assetName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "requestor_id")
    private Integer requestorId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "requestor_name")
    private String requestorName;
    @Basic(optional = false)

    @NotNull
    @Column(name = "request_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date requestTime;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "request_reason")
    private String requestReason;

    @Column(name = "request_reason_voice")
    private Integer requestReasonVoice;

    @Basic(optional = false)
    @NotNull
    @Column(name = "case_priority")
    private Integer casePriority;

    @Column(name = "confirmed_down_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date confirmedDownTime;

    @Column(name = "confirmed_up_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedUpTime;

    @Column(name = "from_dept_id")
    private Integer fromDeptId;

    @Column(name = "from_dept_name")
    @Size(max = 64)
    private String fromDeptName;

    @Column(name = "reponse_time")
    private Date responseTime;

    @Column(name = "estimated_close_time")
    private Date estimatedCloseTime;

    @Column(name = "close_time")
    private Date closeTime;

    @Column(name = "status")
    private Integer status;

    @Column(name = "equipment_taker")
    private String equipmentTaker;

    @Column(name = "nearest_sr_days")
    private Integer nearestSrDays;

    @Column(name = "nearest_sr_id") 
    private String nearestSrId;//上次报修的id 

    @Column(name = "take_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takeTime;

//    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,targetEntity=V2_BlobObject.class)
//    @JoinColumn(name="bo_uuid",referencedColumnName="id", foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//    @Fetch(FetchMode.SUBSELECT)
    //@Where(clause="bo_uuid=id")
//    private List<V2_BlobObject> attachments;

    @JoinColumn(name = "sr_id")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<V2_WorkOrder> v2_workOrder_List;

    public List<V2_WorkOrder> getV2_workOrder_List() {
        return v2_workOrder_List;
    }

    public void setV2_workOrder_List(List<V2_WorkOrder> v2_workOrder_List) {
        this.v2_workOrder_List = v2_workOrder_List;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
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

    public Integer getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(Integer requestorId) {
        this.requestorId = requestorId;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public void setRequestorName(String requestorName) {
        this.requestorName = requestorName;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
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

    public Integer getCasePriority() {
        return casePriority;
    }

    public void setCasePriority(Integer casePriority) {
        this.casePriority = casePriority;
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

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public Date getEstimatedCloseTime() {
        return estimatedCloseTime;
    }

    public void setEstimatedCloseTime(Date estimatedCloseTime) {
        this.estimatedCloseTime = estimatedCloseTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

//    public List<V2_BlobObject> getAttachments() {
//        return attachments;
//    }
//
//    public void setAttachments(List<V2_BlobObject> attachments) {
//        this.attachments = attachments;
//    }

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

    public Integer getNearestSrDays() {
        return nearestSrDays;
    }

    public void setNearestSrDays(Integer nearestSrDays) {
        this.nearestSrDays = nearestSrDays;
    }

    public String getNearestSrId() {
        return nearestSrId;
    }

    public void setNearestSrId(String nearestSrId) {
        this.nearestSrId = nearestSrId;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_ServiceRequest)) {
            return false;
        }
        V2_ServiceRequest other = (V2_ServiceRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
