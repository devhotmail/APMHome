/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "edge_server_info")
@NamedQueries({
    @NamedQuery(name = "EdgeServerInfo.findAll", query = "SELECT e FROM EdgeServerInfo e")})
public class EdgeServerInfo implements Serializable {

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
    @Size(max = 256)
    @Column(name = "edge_server_key")
    private String edgeServerKey;
    @Size(max = 64)
    @Column(name = "edge_server_name")
    private String edgeServerName;
    @Size(max = 64)
    @Column(name = "hospital_name")
    private String hospitalName;
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    public EdgeServerInfo() {
        isEnabled = true;
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

    public String getEdgeServerKey() {
        return edgeServerKey;
    }

    public void setEdgeServerKey(String edgeServerKey) {
        this.edgeServerKey = edgeServerKey;
    }

    public String getEdgeServerName() {
        return edgeServerName;
    }

    public void setEdgeServerName(String edgeServerName) {
        this.edgeServerName = edgeServerName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
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
        if (!(object instanceof EdgeServerInfo)) {
            return false;
        }
        EdgeServerInfo other = (EdgeServerInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.EdgeServerInfo[ id=" + id + " ]";
    }
    
}
