/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "org_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class OrgInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max=32)
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "parent_uid", columnDefinition = "CHAR(32)")
    private String parentUID;
    
    @Column(name = "org_level")
    private Integer orgLevel;

    @Column(name = "org_type")
    private Integer orgType;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 64)
    @Column(name = "name_en")
    private String nameEn;
    
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private OrgInfo parentOrg;

    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @PrePersist
    public void initializeUid() {
        if(uid==null)
            uid = UUID.randomUUID().toString().replace("-", "");
    }
    
    public OrgInfo() {
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public String getParentUID() {
        return parentUID;
    }

    public void setParentUID(String parentUID) {
        this.parentUID = parentUID;
    }

    public Integer getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(Integer orgLevel) {
        this.orgLevel = orgLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public OrgInfo getParentOrg() {
        return parentOrg;
    }

    public void setParentOrg(OrgInfo parentOrg) {
        this.parentOrg = parentOrg;
    }

    @JsonIgnore
    public OrgInfo getHospital(){
        OrgInfo org = this;
        while(org!=null){
            if(org.getParentOrg()!=null)
                org = org.getParentOrg();
            else
                break;
        }
        
        return org;
    }

    public Integer getHospitalId() {
        if(hospitalId==null)
            return id;
        else
            return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
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
        if (!(object instanceof OrgInfo)) {
            return false;
        }
        OrgInfo other = (OrgInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getOrgTypeName(){
        if(this.orgType == null)  return "";
        
        if(this.orgType.equals(1))  return WebUtil.getMessage("Institution");
        else if(this.orgType.equals(2))  return WebUtil.getMessage("Hospital");
        else if(this.orgType.equals(3))  return WebUtil.getMessage("Site");
        else if(this.orgType.equals(4))  return WebUtil.getMessage("Department");
        else return "";
    }
    
}
