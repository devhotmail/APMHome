/*
 */
package com.ge.apm.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "asset_fault_type")
public class AssetFaultType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "asset_group_id")
    private Integer assetGroupId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fault_name")
    private String faultName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAssetGroupId() {
        return assetGroupId;
    }

    public void setAssetGroupId(Integer assetGroupId) {
        this.assetGroupId = assetGroupId;
    }

    public String getFaultName() {
        return faultName;
    }

    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }

    @Override
    public String toString() {
        return "AssetFaultType[ id=" + id + "name= "+faultName +" ]";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetFaultType assetFaultType = (AssetFaultType) o;

        if (!faultName.equals(assetFaultType.faultName)) return false;
        return faultName.equals(assetFaultType.faultName);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + faultName.hashCode();
        return result;
    }

}
