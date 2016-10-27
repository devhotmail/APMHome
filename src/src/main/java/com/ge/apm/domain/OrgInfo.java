/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
    private OrgInfo parentOrg;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;

    public OrgInfo() {
    }

    public OrgInfo(Integer id) {
        this.id = id;
    }

    public OrgInfo(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
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
        return "com.ge.apm.domain.OrgInfo[ id=" + id + " ]";
    }
    
}
