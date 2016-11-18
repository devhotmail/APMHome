/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "work_order_step")
public class WorkOrderStep implements Serializable {

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
    @Column(name = "step_id")
    private Integer stepId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "step_name")
    private String stepName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "owner_id")
    private int ownerId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "owner_name")
    private String ownerName;
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Size(max = 128)
    @Column(name = "description")
    private String description;
    @Size(max = 128)
    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "work_order_id")
    @Basic(optional = false)
    @NotNull
    private Integer workOrderId;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workOrderStep", fetch = FetchType.EAGER, orphanRemoval=true)
    private List<WorkOrderStepDetail> stepDetails;
    
    public WorkOrderStep() {
    }

    public WorkOrderStep(Integer id) {
        this.id = id;
    }

    public WorkOrderStep(Integer id, int siteId, String stepName, int ownerId, String ownerName, Date startTime, Date endTime) {
        this.id = id;
        this.siteId = siteId;
        this.stepName = stepName;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getStepName() {
        return stepName;
    }

    public Integer getStepId() {
        return stepId;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Integer getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Integer workOrderId) {
        this.workOrderId = workOrderId;
    }

    public List<WorkOrderStepDetail> getStepDetails() {
        return stepDetails;
    }

    public void setStepDetails(List<WorkOrderStepDetail> stepDetails) {
        this.stepDetails = stepDetails;
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
        if (!(object instanceof WorkOrderStep)) {
            return false;
        }
        WorkOrderStep other = (WorkOrderStep) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WorkOrderStep[ id=" + id + " ]";
    }
    
}
