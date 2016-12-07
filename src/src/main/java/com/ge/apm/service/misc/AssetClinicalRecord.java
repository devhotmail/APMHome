/*
 */
package com.ge.apm.service.misc;

import java.io.Serializable;
import java.util.Date;

public class AssetClinicalRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private int siteId;
    private int hospitalId;
    private int modalityId;
    private int modalityTypeId;
    private String modalityType;
    private int procedureId;
    private String procedureName;
    private int procedureStepId;
    private String procedureStepName;
    private Double priceAmount;
    private Integer priceUnit;
    private String patientId;
    private String patientNameZh;
    private String patientNameEn;
    private String patientAge;
    private Integer patientGender;
    private Double injectCount;
    private Double exposeCount;
    private Integer filmCount;
    private Date examDate;
    private Date examStartTime;
    private Integer examDuration;
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

    public Integer getExamDuration() {
        return examDuration;
    }

    public void setExamDuration(Integer examDuration) {
        this.examDuration = examDuration;
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
