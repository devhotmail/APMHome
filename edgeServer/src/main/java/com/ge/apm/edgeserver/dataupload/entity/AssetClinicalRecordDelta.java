/*
 */
package com.ge.apm.edgeserver.dataupload.entity;

import com.ge.apm.edgeserver.sysutil.JsonMapper;
import java.io.Serializable;
import java.util.Calendar;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author 212547631
 */
@XmlRootElement(name="AssetClinicalRecord")
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "asset_clinical_record_delta")
public class AssetClinicalRecordDelta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @XmlTransient
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_time")
    @Temporal(TemporalType.DATE)
    private Date createTime;
    
    @XmlTransient
    @Column(name = "upload_status")
    private String uploadStatus;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Column(name = "modality_id")
    private String modalityId;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Column(name = "modality_type_id")
    private int modalityTypeId;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "modality_type")
    private String modalityType;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Column(name = "procedure_id")
    private int procedureId;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "procedure_name")
    private String procedureName;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Column(name = "procedure_step_id")
    private int procedureStepId;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "procedure_step_name")
    private String procedureStepName;

    @XmlElement
    @Column(name = "price_amount")
    private Double priceAmount;

    @XmlElement
    @Column(name = "price_unit")
    private String priceUnit;

    @XmlElement
    @Column(name = "inject_count")
    private Double injectCount;

    @XmlElement
    @Column(name = "expose_count")
    private Double exposeCount;

    @XmlElement
    @Column(name = "film_count")
    private Integer filmCount;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Column(name = "exam_date")
    @Temporal(TemporalType.DATE)
    private Date examDate;

    @XmlElement
    @Basic(optional = false)
    @NotNull
    @Column(name = "exam_start_time")
    @Temporal(TemporalType.TIME)
    private Date examStartTime;
    
    @XmlElement
    @Column(name = "exam_duration")
    private Integer examDuration;
    
    @XmlElement
    @Column(name = "source_system")
    private String sourceSystem;

    @XmlElement
    @Column(name = "source_record_id")
    private String sourceRecordId;

    public AssetClinicalRecordDelta() {
        uploadStatus = "N";
        createTime = Calendar.getInstance().getTime();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModalityId() {
        return modalityId;
    }

    public void setModalityId(String modalityId) {
        this.modalityId = modalityId;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getSourceRecordId() {
        return sourceRecordId;
    }

    public void setSourceRecordId(String sourceRecordId) {
        this.sourceRecordId = sourceRecordId;
    }

    public int getModalityTypeId() {
        return modalityTypeId;
    }

    public void setModalityTypeId(int modalityTypeId) {
        this.modalityTypeId = modalityTypeId;
    }

    public String getModalityType() {
        return modalityType;
    }

    public void setModalityType(String modalityType) {
        this.modalityType = modalityType;
    }

    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public int getProcedureStepId() {
        return procedureStepId;
    }

    public void setProcedureStepId(int procedureStepId) {
        this.procedureStepId = procedureStepId;
    }

    public String getProcedureStepName() {
        return procedureStepName;
    }

    public void setProcedureStepName(String procedureStepName) {
        this.procedureStepName = procedureStepName;
    }

    public Double getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(Double priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Double getInjectCount() {
        return injectCount;
    }

    public void setInjectCount(Double injectCount) {
        this.injectCount = injectCount;
    }

    public Double getExposeCount() {
        return exposeCount;
    }

    public void setExposeCount(Double exposeCount) {
        this.exposeCount = exposeCount;
    }

    public Integer getFilmCount() {
        return filmCount;
    }

    public void setFilmCount(Integer filmCount) {
        this.filmCount = filmCount;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public Date getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(Date examStartTime) {
        this.examStartTime = examStartTime;
    }

    public Integer getExamDuration() {
        return examDuration;
    }

    public void setExamDuration(Integer examDuration) {
        this.examDuration = examDuration;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
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
        if (!(object instanceof AssetClinicalRecordDelta)) {
            return false;
        }
        AssetClinicalRecordDelta other = (AssetClinicalRecordDelta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        JsonMapper mapper = JsonMapper.nonEmptyMapper();
        return mapper.toJson(this);
    }
    
}
