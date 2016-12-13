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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "asset_clinical_record_errlog")
@XmlRootElement
public class AssetClinicalRecordErrlog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "site_id")
    private Integer siteId;
    @Column(name = "hospital_id")
    private Integer hospitalId;
    @Column(name = "asset_id")
    private Integer assetId;
    @Size(max = 64)
    @Column(name = "modality_id")
    private String modalityId;
    @Column(name = "modality_type_id")
    private Integer modalityTypeId;
    @Size(max = 32)
    @Column(name = "modality_type")
    private String modalityType;
    @Column(name = "procedure_id")
    private Integer procedureId;
    @Size(max = 32)
    @Column(name = "procedure_name")
    private String procedureName;
    @Column(name = "procedure_step_id")
    private Integer procedureStepId;
    @Size(max = 32)
    @Column(name = "procedure_step_name")
    private String procedureStepName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price_amount")
    private Double priceAmount;
    @Size(max = 16)
    @Column(name = "price_unit")
    private String priceUnit;
    @Column(name = "inject_count")
    private Double injectCount;
    @Column(name = "expose_count")
    private Double exposeCount;
    @Column(name = "film_count")
    private Integer filmCount;
    @Column(name = "exam_date")
    @Temporal(TemporalType.DATE)
    private Date examDate;
    @Column(name = "exam_start_time")
    @Temporal(TemporalType.TIME)
    private Date examStartTime;
    @Column(name = "exam_duration")
    private Integer examDuration;
    @Size(max = 32)
    @Column(name = "source_system")
    private String sourceSystem;
    @Size(max = 64)
    @Column(name = "source_record_id")
    private String sourceRecordId;
    @Size(max = 256)
    @Column(name = "site_key")
    private String siteKey;
    @Column(name = "upload_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadTime;
    @Size(max = 256)
    @Column(name = "err_msg")
    private String errMsg;

    public AssetClinicalRecordErrlog() {
    }

    public AssetClinicalRecordErrlog(Integer id) {
        this.id = id;
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

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getModalityId() {
        return modalityId;
    }

    public void setModalityId(String modalityId) {
        this.modalityId = modalityId;
    }

    public Integer getModalityTypeId() {
        return modalityTypeId;
    }

    public void setModalityTypeId(Integer modalityTypeId) {
        this.modalityTypeId = modalityTypeId;
    }

    public String getModalityType() {
        return modalityType;
    }

    public void setModalityType(String modalityType) {
        this.modalityType = modalityType;
    }

    public Integer getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(Integer procedureId) {
        this.procedureId = procedureId;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public Integer getProcedureStepId() {
        return procedureStepId;
    }

    public void setProcedureStepId(Integer procedureStepId) {
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

    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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
        if (!(object instanceof AssetClinicalRecordErrlog)) {
            return false;
        }
        AssetClinicalRecordErrlog other = (AssetClinicalRecordErrlog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.AssetClinicalRecordErrlog[ id=" + id + " ]";
    }
    
}
