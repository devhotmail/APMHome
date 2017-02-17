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
import javax.validation.constraints.NotNull;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "asset_summit")
public class AssetSummit implements Serializable {

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
    @Column(name = "asset_id")
    private int assetId;
    @Column(name = "asset_group")
    private Integer assetGroup;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "supplier_id")
    private Integer supplierId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "revenue")
    private Double revenue;
    @Column(name = "maintenance_cost")
    private Double maintenanceCost;
    @Column(name = "deprecation_cost")
    private Double deprecationCost;
    @Column(name = "inject_count")
    private Integer injectCount;
    @Column(name = "expose_count")
    private Double exposeCount;
    @Column(name = "film_count")
    private Integer filmCount;
    @Column(name = "exam_count")
    private Integer examCount;
    @Column(name = "exam_duration")
    private Integer examDuration;
    @Column(name = "down_time")
    private Integer downTime;
    @Column(name = "work_order_count")
    private Integer workOrderCount;
    @Column(name = "rating")
    private Integer rating;
    @Column(name = "created")
    @Temporal(TemporalType.DATE)
    private Date created;
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    public AssetSummit() {
    }

    public AssetSummit(Integer id) {
        this.id = id;
    }

    public AssetSummit(Integer id, int siteId, int hospitalId, int assetId) {
        this.id = id;
        this.siteId = siteId;
        this.hospitalId = hospitalId;
        this.assetId = assetId;
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

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(Double maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public Double getDeprecationCost() {
        return deprecationCost;
    }

    public void setDeprecationCost(Double deprecationCost) {
        this.deprecationCost = deprecationCost;
    }

    public Integer getInjectCount() {
        return injectCount;
    }

    public void setInjectCount(Integer injectCount) {
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

    public Integer getExamCount() {
        return examCount;
    }

    public void setExamCount(Integer examCount) {
        this.examCount = examCount;
    }

    public Integer getExamDuration() {
        return examDuration;
    }

    public void setExamDuration(Integer examDuration) {
        this.examDuration = examDuration;
    }

    public Integer getDownTime() {
        return downTime;
    }

    public void setDownTime(Integer downTime) {
        this.downTime = downTime;
    }

    public Integer getWorkOrderCount() {
        return workOrderCount;
    }

    public void setWorkOrderCount(Integer workOrderCount) {
        this.workOrderCount = workOrderCount;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
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
        if (!(object instanceof AssetSummit)) {
            return false;
        }
        AssetSummit other = (AssetSummit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.AssetSummit[ id=" + id + " ]";
    }
    
}
