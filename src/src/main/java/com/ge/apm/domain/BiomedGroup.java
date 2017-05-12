/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


/**
 *
 * @author 212601104
 */
@Entity
@Table(name = "biomed_group")
public class BiomedGroup  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "group_name")
    private String groupName;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "biomedGroup", fetch = FetchType.LAZY)
    private List<BiomedGroupUser> biomedGroupUserList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHospitalId() {
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<BiomedGroupUser> getBiomedGroupUserList() {
        return biomedGroupUserList;
    }

    public void setBiomedGroupUserList(List<BiomedGroupUser> biomedGroupUserList) {
        this.biomedGroupUserList = biomedGroupUserList;
    }
}
