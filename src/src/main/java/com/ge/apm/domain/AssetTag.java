/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 *
 * @author 212601104
 */
@Entity
@Table(name = "asset_tag")
public class AssetTag  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assetTag", fetch = FetchType.LAZY)
    private List<AssetTagBiomedGroup> assetTagBiomedGroup;

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

    public List<AssetTagBiomedGroup> getAssetTagBiomedGroup() {
        return assetTagBiomedGroup;
    }

    public void setAssetTagBiomedGroup(List<AssetTagBiomedGroup> assetTagBiomedGroup) {
        this.assetTagBiomedGroup = assetTagBiomedGroup;
    }
}
