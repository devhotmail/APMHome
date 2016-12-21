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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "procedure_name_maping")
public class ProcedureNameMaping implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "ris_procedure_name")
    private String risProcedureName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "apm_procedure_name")
    private String apmProcedureName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "apm_procedure_id")
    private int apmProcedureId;

    public ProcedureNameMaping() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRisProcedureName() {
        return risProcedureName;
    }

    public void setRisProcedureName(String risProcedureName) {
        this.risProcedureName = risProcedureName;
    }

    public String getApmProcedureName() {
        return apmProcedureName;
    }

    public void setApmProcedureName(String apmProcedureName) {
        this.apmProcedureName = apmProcedureName;
    }

    public int getApmProcedureId() {
        return apmProcedureId;
    }

    public void setApmProcedureId(int apmProcedureId) {
        this.apmProcedureId = apmProcedureId;
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
        if (!(object instanceof ProcedureNameMaping)) {
            return false;
        }
        ProcedureNameMaping other = (ProcedureNameMaping) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ProcedureNameMaping[ id=" + id + " ]";
    }
    
}
