/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "asset_depreciation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetDepreciation.findAll", query = "SELECT a FROM AssetDepreciation a"),
    @NamedQuery(name = "AssetDepreciation.findById", query = "SELECT a FROM AssetDepreciation a WHERE a.id = :id"),
    @NamedQuery(name = "AssetDepreciation.findBySiteId", query = "SELECT a FROM AssetDepreciation a WHERE a.siteId = :siteId"),
    @NamedQuery(name = "AssetDepreciation.findByAssetId", query = "SELECT a FROM AssetDepreciation a WHERE a.assetId = :assetId"),
    @NamedQuery(name = "AssetDepreciation.findByDeprecateDate", query = "SELECT a FROM AssetDepreciation a WHERE a.deprecateDate = :deprecateDate"),
    @NamedQuery(name = "AssetDepreciation.findByDeprecateAmount", query = "SELECT a FROM AssetDepreciation a WHERE a.deprecateAmount = :deprecateAmount"),
    @NamedQuery(name = "AssetDepreciation.findByContractId", query = "SELECT a FROM AssetDepreciation a WHERE a.contractId = :contractId")})
public class AssetDepreciation implements Serializable {

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
    @Column(name = "asset_id")
    private int assetId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "deprecate_date")
    @Temporal(TemporalType.DATE)
    private Date deprecateDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "deprecate_amount")
    private double deprecateAmount;
    @Column(name = "contract_id")
    private Integer contractId;

    public AssetDepreciation() {
    }

    public AssetDepreciation(Integer id) {
        this.id = id;
    }

    public AssetDepreciation(Integer id, int siteId, int assetId, Date deprecateDate, double deprecateAmount) {
        this.id = id;
        this.siteId = siteId;
        this.assetId = assetId;
        this.deprecateDate = deprecateDate;
        this.deprecateAmount = deprecateAmount;
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

    public Date getDeprecateDate() {
        return deprecateDate;
    }

    public void setDeprecateDate(Date deprecateDate) {
        this.deprecateDate = deprecateDate;
    }

    public double getDeprecateAmount() {
        return deprecateAmount;
    }

    public void setDeprecateAmount(double deprecateAmount) {
        this.deprecateAmount = deprecateAmount;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
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
        if (!(object instanceof AssetDepreciation)) {
            return false;
        }
        AssetDepreciation other = (AssetDepreciation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.AssetDepreciation[ id=" + id + " ]";
    }
    
}
