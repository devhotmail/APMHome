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
@Table(name = "asset_clinical_record")
public class AssetClinicalRecord implements Serializable {

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
    @Column(name = "modality_id")
    private int modalityId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modality_type_id")
    private int modalityTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "modality_type")
    private String modalityType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "procedure_id")
    private int procedureId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "procedure_name")
    private String procedureName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "procedure_step_id")
    private int procedureStepId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "procedure_step_name")
    private String procedureStepName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price_amount")
    private Double priceAmount;
    @Column(name = "price_unit")
    private Integer priceUnit;
    @Size(max = 64)
    @Column(name = "patient_id")
    private String patientId;
    @Size(max = 32)
    @Column(name = "patient_name_zh")
    private String patientNameZh;
    @Size(max = 32)
    @Column(name = "patient_name_en")
    private String patientNameEn;
    @Size(max = 32)
    @Column(name = "patient_age")
    private String patientAge;
    @Column(name = "patient_gender")
    private Integer patientGender;
    @Column(name = "inject_count")
    private Double injectCount;
    @Column(name = "expose_count")
    private Double exposeCount;
    @Column(name = "film_count")
    private Integer filmCount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exam_date")
    @Temporal(TemporalType.DATE)
    private Date examDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exam_start_time")
    @Temporal(TemporalType.TIME)
    private Date examStartTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exam_end_time")
    @Temporal(TemporalType.TIME)
    private Date examEndTime;
    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    public AssetClinicalRecord() {
    }

    public AssetClinicalRecord(Integer id) {
        this.id = id;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
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

    public int getModalityId() {
        return modalityId;
    }

    public void setModalityId(int modalityId) {
        this.modalityId = modalityId;
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

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientNameZh() {
        return patientNameZh;
    }

    public void setPatientNameZh(String patientNameZh) {
        this.patientNameZh = patientNameZh;
    }

    public String getPatientNameEn() {
        return patientNameEn;
    }

    public void setPatientNameEn(String patientNameEn) {
        this.patientNameEn = patientNameEn;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public Integer getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(Integer patientGender) {
        this.patientGender = patientGender;
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

    public Date getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(Date examEndTime) {
        this.examEndTime = examEndTime;
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
        if (!(object instanceof AssetClinicalRecord)) {
            return false;
        }
        AssetClinicalRecord other = (AssetClinicalRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.AssetClinicalRecord[ id=" + id + " ]";
    }
    
}
