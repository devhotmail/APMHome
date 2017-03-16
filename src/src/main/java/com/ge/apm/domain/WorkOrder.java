/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private Integer siteId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date requestTime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "request_reason")
    private String requestReason;
    @Column(name = "case_type")
    private Integer caseType;
    @Column(name = "case_sub_type")
    private Integer caseSubType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "case_priority")
    private Integer casePriority;
    @Basic(optional = false)
    @NotNull
    @Column(name = "current_person_id")
    private Integer currentPersonId;
    @Size(max = 16)
    @Column(name = "current_person_name")
    private String currentPersonName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "current_step_id")
    private Integer currentStepId;
    @Column(name = "total_man_hour")
    private Integer totalManHour;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "total_price")
    private Double totalPrice;
    @Column(name = "confirmed_down_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date confirmedDownTime;
    @Column(name = "confirmed_up_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedUpTime;
    @Column(name = "current_step_name")
    private String currentStepName;
    @Column(name = "from_dept_id")
    private Integer fromDeptId;
    @Column(name = "from_dept_name")
    @Size(max = 64)
    private String fromDeptName;
    @Column(name = "ticket_no")
    @Size(max = 32)
    private String ticketNo;
    @Column(name = "reponse_time")
    private Integer responseTime; //in minutes
    @Column(name = "repaire_time")
    private Integer repaireTime;
    @Column(name = "status")
    private Integer status;
    @Column(name = "int_ext_type")
    private Integer intExtType;
    @Column(name = "parent_wo_id")
    private Integer parentWoId;
    @Column(name = "feedback_rating")
    private Integer feedbackRating;
    @Column(name = "feedback_comment")
    private String feedbackComment;
    @Column(name = "request_reason_voice")
    private Integer requestReasonVoice;
    @Column(name = "pat_actions")
    private String patActions;
    @Column(name = "pat_tests")
    private String patTests;
    
    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    public WorkOrder() {
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public WorkOrder(Integer id, int siteId, int hospitalId, String assetName, int requestorId, String requestorName, Date requestTime, String requestReason, int casePriority, int currentPersonId, int currentStepId) {
        this.id = id;
        this.siteId = siteId;
        this.hospitalId = hospitalId;
        this.assetName = assetName;
        this.requestorId = requestorId;
        this.requestorName = requestorName;
        this.requestTime = requestTime;
        this.requestReason = requestReason;
        this.casePriority = casePriority;
        this.currentPersonId = currentPersonId;
        this.currentStepId = currentStepId;
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

    public Integer getCaseType() {
        return caseType;
    }

    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }

    public Integer getCaseSubType() {
        return caseSubType;
    }

    public void setCaseSubType(Integer caseSubType) {
        this.caseSubType = caseSubType;
    }

    public Integer getCasePriority() {
        return casePriority;
    }

    public void setCasePriority(Integer casePriority) {
        this.casePriority = casePriority;
    }

    public Integer getCurrentPersonId() {
        return currentPersonId;
    }

    public void setCurrentPersonId(Integer currentPersonId) {
        this.currentPersonId = currentPersonId;
    }

    public String getCurrentPersonName() {
        return currentPersonName;
    }

    public void setCurrentPersonName(String currentPersonName) {
        this.currentPersonName = currentPersonName;
    }

    public Integer getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(Integer currentStepId) {
        this.currentStepId = currentStepId;
    }

     public String getCurrentStepName() {
        return currentStepName;
    }

    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
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

    public Integer getAssetId() {
        return assetId;
    }
   
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
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

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public Integer getRepaireTime() {
        return repaireTime;
    }

    public void setRepaireTime(Integer repaireTime) {
        this.repaireTime = repaireTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(Integer intExtType) {
        this.intExtType = intExtType;
    }

    public Integer getParentWoId() {
        return parentWoId;
    }

    public void setParentWoId(Integer parentWoId) {
        this.parentWoId = parentWoId;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }

    public Integer getRequestReasonVoice() {
        return requestReasonVoice;
    }

    public void setRequestReasonVoice(Integer requestReasonVoice) {
        this.requestReasonVoice = requestReasonVoice;
    }

    public String getPatActions() {
        return patActions;
    }

    public void setPatActions(String patActions) {
        this.patActions = patActions;
    }

    public String getPatTests() {
        return patTests;
    }

    public void setPatTests(String patTests) {
        this.patTests = patTests;
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
