package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "v2_work_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class V2_WorkOrder extends JHipAbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "sr_id")
    private String srId;

//	    @JoinColumn(name = "sr_id", referencedColumnName = "id", insertable = false, updatable = false)
//	    @ManyToOne(fetch = FetchType.EAGER)
//	    private V2_ServiceRequest serviceRequest;
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

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

    @Column(name = "case_type")
    private Integer caseType;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "current_step_name")
    private String currentStepName;

    @Column(name = "total_man_hour")
    private Integer totalManHour;

    @Column(name = "total_price")
    private Double totalPrice;

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

    @Column(name = "pat_problems")
    private String patProblems;

    @Column(name = "pat_actions")
    private String patActions;

    @Column(name = "pat_tests")
    private String patTests;

    @Column(name = "close_time")
    private Date closeTime;

    @Column(name = "checkin")
    private Integer checkin;

    //	    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,targetEntity=V2_BlobObject.class) 
//	    @Where(clause="bo_uuid=id")
//	    private List<V2_BlobObject> attachments ;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id")
    private List<V2_WorkOrder_Step> v2_workOrder_step_list;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id")
    private List<V2_WorkOrder_Detail> v2_workOrder_detail_list;

    @Column(name = "comment")
    private String comment;

    @Column(name = "repair_type")
    private Integer repairType;

    @Column(name = "checkin_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkinTime;

	    
	    
    public Date getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(Date checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRepairType() {
        return repairType;
    }

    public void setRepairType(Integer repairType) {
        this.repairType = repairType;
    }

    public List<V2_WorkOrder_Step> getV2_workOrder_step_list() {
        return v2_workOrder_step_list;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setV2_workOrder_step_list(List<V2_WorkOrder_Step> v2_workOrder_step_list) {
        this.v2_workOrder_step_list = v2_workOrder_step_list;
    }

    public List<V2_WorkOrder_Detail> getV2_workOrder_detail_list() {
        return v2_workOrder_detail_list;
    }

    public void setV2_workOrder_detail_list(List<V2_WorkOrder_Detail> v2_workOrder_detail_list) {
        this.v2_workOrder_detail_list = v2_workOrder_detail_list;
    }

    public String getSrId() {
        return srId;
    }

    public void setSrId(String srId) {
        this.srId = srId;
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

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
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

    public String getPatProblems() {
        return patProblems;
    }

    public void setPatProblems(String patProblems) {
        this.patProblems = patProblems;
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

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Integer getCaseType() {
        return caseType;
    }

    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }

    public Integer getCheckin() {
        return checkin;
    }

    public void setCheckin(Integer checkin) {
        this.checkin = checkin;
    }

//		public V2_ServiceRequest getServiceRequest() {
//			return serviceRequest;
//		}
//
//		public void setServiceRequest(V2_ServiceRequest serviceRequest) {
//			this.serviceRequest = serviceRequest;
//		}
    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_WorkOrder)) {
            return false;
        }
        V2_WorkOrder other = (V2_WorkOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
