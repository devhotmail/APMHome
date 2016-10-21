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
@Table(name = "work_order")
public class WorkOrder implements Serializable {

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
    @Size(min = 1, max = 32)
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "requestor_id")
    private int requestorId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "requestor_name")
    private String requestorName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "request_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestTime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "request_reason")
    private String requestReason;
    @Column(name = "case_owner_id")
    private Integer caseOwnerId;
    @Size(max = 16)
    @Column(name = "case_owner_name")
    private String caseOwnerName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "case_type")
    private int caseType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "case_sub_type")
    private int caseSubType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "case_priority")
    private int casePriority;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_internal")
    private boolean isInternal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "current_person_id")
    private int currentPersonId;
    @Size(max = 16)
    @Column(name = "current_person_name")
    private String currentPersonName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "current_step")
    private int currentStep;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_closed")
    private boolean isClosed;
    @Size(max = 256)
    @Column(name = "close_reason")
    private String closeReason;
    @Size(max = 256)
    @Column(name = "comments")
    private String comments;
    @Column(name = "total_man_hour")
    private Integer totalManHour;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "total_price")
    private Double totalPrice;
    @Column(name = "confirmed_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedStartTime;
    @Column(name = "confirmed_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedEndTime;

    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    public WorkOrder() {
    }

    public WorkOrder(Integer id) {
        this.id = id;
    }

    public WorkOrder(Integer id, int siteId, String name, int creatorId, String creatorName, Date createTime, int requestorId, String requestorName, Date requestTime, String requestReason, int caseType, int caseSubType, int casePriority, boolean isInternal, int currentPersonId, int currentStep, boolean isClosed) {
        this.id = id;
        this.siteId = siteId;
        this.name = name;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.requestorId = requestorId;
        this.requestorName = requestorName;
        this.requestTime = requestTime;
        this.requestReason = requestReason;
        this.caseType = caseType;
        this.caseSubType = caseSubType;
        this.casePriority = casePriority;
        this.isInternal = isInternal;
        this.currentPersonId = currentPersonId;
        this.currentStep = currentStep;
        this.isClosed = isClosed;
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

    public Integer getCaseOwnerId() {
        return caseOwnerId;
    }

    public void setCaseOwnerId(Integer caseOwnerId) {
        this.caseOwnerId = caseOwnerId;
    }

    public String getCaseOwnerName() {
        return caseOwnerName;
    }

    public void setCaseOwnerName(String caseOwnerName) {
        this.caseOwnerName = caseOwnerName;
    }

    public int getCaseType() {
        return caseType;
    }

    public void setCaseType(int caseType) {
        this.caseType = caseType;
    }

    public int getCaseSubType() {
        return caseSubType;
    }

    public void setCaseSubType(int caseSubType) {
        this.caseSubType = caseSubType;
    }

    public int getCasePriority() {
        return casePriority;
    }

    public void setCasePriority(int casePriority) {
        this.casePriority = casePriority;
    }

    public boolean getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

    public int getCurrentPersonId() {
        return currentPersonId;
    }

    public void setCurrentPersonId(int currentPersonId) {
        this.currentPersonId = currentPersonId;
    }

    public String getCurrentPersonName() {
        return currentPersonName;
    }

    public void setCurrentPersonName(String currentPersonName) {
        this.currentPersonName = currentPersonName;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getTotalManHour() {
        return totalManHour;
    }

    public void setTotalManHour(Integer totalManHour) {
        this.totalManHour = totalManHour;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getConfirmedStartTime() {
        return confirmedStartTime;
    }

    public void setConfirmedStartTime(Date confirmedStartTime) {
        this.confirmedStartTime = confirmedStartTime;
    }

    public Date getConfirmedEndTime() {
        return confirmedEndTime;
    }

    public void setConfirmedEndTime(Date confirmedEndTime) {
        this.confirmedEndTime = confirmedEndTime;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
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
        if (!(object instanceof WorkOrder)) {
            return false;
        }
        WorkOrder other = (WorkOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WorkOrder[ id=" + id + " ]";
    }
    
}
