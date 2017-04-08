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
@Table(name = "assettype_faulty")
public class AssetTypeFaulty implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "astype_id")
    private Integer astypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fault_id")
    private Integer faultId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAstypeId() {
        return astypeId;
    }

    public void setAstypeId(Integer astypeId) {
        this.astypeId = astypeId;
    }

    public Integer getFaultId() {
        return faultId;
    }

    public void setFaultId(Integer faultId) {
        this.faultId = faultId;
    }

    @Override
    public String toString() {
        return "AssetTypeFaulty[ id=" + id + " ]";
    }

}
