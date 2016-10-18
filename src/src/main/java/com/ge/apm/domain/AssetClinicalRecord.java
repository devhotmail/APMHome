/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.math.BigInteger;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author xiepin
 */
@Entity
@Table(name = "asset_clinical_record", catalog = "ge_apm", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetClinicalRecord.findAll", query = "SELECT a FROM AssetClinicalRecord a")
    , @NamedQuery(name = "AssetClinicalRecord.findById", query = "SELECT a FROM AssetClinicalRecord a WHERE a.id = :id")
    , @NamedQuery(name = "AssetClinicalRecord.findBySiteId", query = "SELECT a FROM AssetClinicalRecord a WHERE a.siteId = :siteId")
    , @NamedQuery(name = "AssetClinicalRecord.findByAssetId", query = "SELECT a FROM AssetClinicalRecord a WHERE a.assetId = :assetId")
    , @NamedQuery(name = "AssetClinicalRecord.findByModalityId", query = "SELECT a FROM AssetClinicalRecord a WHERE a.modalityId = :modalityId")
    , @NamedQuery(name = "AssetClinicalRecord.findByModalityType", query = "SELECT a FROM AssetClinicalRecord a WHERE a.modalityType = :modalityType")
    , @NamedQuery(name = "AssetClinicalRecord.findByProcedureId", query = "SELECT a FROM AssetClinicalRecord a WHERE a.procedureId = :procedureId")
    , @NamedQuery(name = "AssetClinicalRecord.findByProcedureName", query = "SELECT a FROM AssetClinicalRecord a WHERE a.procedureName = :procedureName")
    , @NamedQuery(name = "AssetClinicalRecord.findByProcedureStepId", query = "SELECT a FROM AssetClinicalRecord a WHERE a.procedureStepId = :procedureStepId")
    , @NamedQuery(name = "AssetClinicalRecord.findByProcedureStepName", query = "SELECT a FROM AssetClinicalRecord a WHERE a.procedureStepName = :procedureStepName")
    , @NamedQuery(name = "AssetClinicalRecord.findByPriceAmount", query = "SELECT a FROM AssetClinicalRecord a WHERE a.priceAmount = :priceAmount")
    , @NamedQuery(name = "AssetClinicalRecord.findByInjectCount", query = "SELECT a FROM AssetClinicalRecord a WHERE a.injectCount = :injectCount")
    , @NamedQuery(name = "AssetClinicalRecord.findByExposeCount", query = "SELECT a FROM AssetClinicalRecord a WHERE a.exposeCount = :exposeCount")
    , @NamedQuery(name = "AssetClinicalRecord.findByFilmCount", query = "SELECT a FROM AssetClinicalRecord a WHERE a.filmCount = :filmCount")
    , @NamedQuery(name = "AssetClinicalRecord.findByExamDate", query = "SELECT a FROM AssetClinicalRecord a WHERE a.examDate = :examDate")
    , @NamedQuery(name = "AssetClinicalRecord.findByExamStartTime", query = "SELECT a FROM AssetClinicalRecord a WHERE a.examStartTime = :examStartTime")
    , @NamedQuery(name = "AssetClinicalRecord.findByExamEndTime", query = "SELECT a FROM AssetClinicalRecord a WHERE a.examEndTime = :examEndTime")})
public class AssetClinicalRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id", nullable = false)
    private int siteId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "asset_id", nullable = false)
    private int assetId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modality_id", nullable = false)
    private int modalityId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modality_type", nullable = false)
    private int modalityType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "procedure_id", nullable = false)
    private int procedureId;
    @Size(max = 40)
    @Column(name = "procedure_name", length = 40)
    private String procedureName;
    @Column(name = "procedure_step_id")
    private Integer procedureStepId;
    @Size(max = 40)
    @Column(name = "procedure_step_name", length = 40)
    private String procedureStepName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price_amount", precision = 17, scale = 17)
    private Double priceAmount;
    @Column(name = "inject_count", precision = 17, scale = 17)
    private Double injectCount;
    @Column(name = "expose_count")
    private BigInteger exposeCount;
    @Column(name = "film_count")
    private Integer filmCount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exam_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date examDate;
    @Column(name = "exam_start_time")
    @Temporal(TemporalType.TIME)
    private Date examStartTime;
    @Column(name = "exam_end_time")
    @Temporal(TemporalType.TIME)
    private Date examEndTime;

    public AssetClinicalRecord() {
    }

    public AssetClinicalRecord(Integer id) {
        this.id = id;
    }

    public AssetClinicalRecord(Integer id, int siteId, int assetId, int modalityId, int modalityType, int procedureId, Date examDate) {
        this.id = id;
        this.siteId = siteId;
        this.assetId = assetId;
        this.modalityId = modalityId;
        this.modalityType = modalityType;
        this.procedureId = procedureId;
        this.examDate = examDate;
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

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public int getModalityId() {
        return modalityId;
    }

    public void setModalityId(int modalityId) {
        this.modalityId = modalityId;
    }

    public int getModalityType() {
        return modalityType;
    }

    public void setModalityType(int modalityType) {
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

    public Double getInjectCount() {
        return injectCount;
    }

    public void setInjectCount(Double injectCount) {
        this.injectCount = injectCount;
    }

    public BigInteger getExposeCount() {
        return exposeCount;
    }

    public void setExposeCount(BigInteger exposeCount) {
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
