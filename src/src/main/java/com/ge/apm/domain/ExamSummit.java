package com.ge.apm.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "exam_summit")
public class ExamSummit implements Serializable {

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
    @Column(name = "asset_id")
    private Integer assetId;
    @Column(name = "asset_group")
    private Integer assetGroup;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "part_id")
    private Integer partId;
    @Column(name = "subpart_id")
    private Integer subPartId;
    @Column(name = "step_id")
    private Integer stepId;
    @Column(name = "exam_count")
    private Integer examCount;
    @Temporal(TemporalType.DATE)
    private Date created;


/////////////////////


    public ExamSummit() {
    }

    public ExamSummit(Integer id) {
        this.id = id;
    }

    public ExamSummit(Integer id, int siteId, int hospitalId, int assetId) {
        this.id = id;
        this.siteId = siteId;
        this.hospitalId = hospitalId;
        this.assetId = assetId;
    }



	public ExamSummit(Integer siteId, Integer hospitalId, Integer assetId) {
		super();
		this.siteId = siteId;
		this.hospitalId = hospitalId;
		this.assetId = assetId;
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

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Integer getSubPartId() {
        return subPartId;
    }

    public void setSubPartId(Integer subPartId) {
        this.subPartId = subPartId;
    }

    public Integer getStepId() {
        return stepId;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public Integer getExamCount() {
        return examCount;
    }

    public void setExamCount(Integer examCount) {
        this.examCount = examCount;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
        if (!(object instanceof ExamSummit)) {
            return false;
        }
        ExamSummit other = (ExamSummit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssetSummit [siteId=");
		builder.append(siteId);
		builder.append(", hospitalId=");
		builder.append(hospitalId);
		builder.append(", assetId=");
		builder.append(assetId);
		builder.append(", assetGroup=");
		builder.append(assetGroup);
		builder.append(", deptId=");
		builder.append(deptId);
		builder.append(", supplierId=");
		builder.append(examCount);
		builder.append("]");
		return builder.toString();
	}

}
