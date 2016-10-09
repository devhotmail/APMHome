package com.ge.aps.domain;

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
@Table(name = "data_table_config")
public class DataTableConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(max = 30)
    @Column(name = "data_table_name")
    private String dataTableName;
    @Basic(optional = false)
    @NotNull
    @Size(max = 500)
    @Column(name = "data_sql")
    private String dataSql;
    @Basic(optional = false)
    @NotNull
    @Size(max = 500)
    @Column(name = "data_count_sql")
    private String dataCountSql;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_default_empty_result")
    private boolean isDefaultEmptyResult;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_pagination_supported")
    private boolean isPaginationSupported;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public String getDataCountSql() {
        return dataCountSql;
    }

    public void setDataCountSql(String dataCountSql) {
        this.dataCountSql = dataCountSql;
    }

    public String getDataSql() {
        return dataSql;
    }

    public void setDataSql(String dataSql) {
        this.dataSql = dataSql;
    }

    public boolean getIsPaginationSupported() {
        return isPaginationSupported;
    }

    public boolean getIsDefaultEmptyResult() {
        return isDefaultEmptyResult;
    }

    public void setIsDefaultEmptyResult(boolean isDefaultEmptyResult) {
        this.isDefaultEmptyResult = isDefaultEmptyResult;
    }

    public void setIsPaginationSupported(boolean isPaginationSupported) {
        this.isPaginationSupported = isPaginationSupported;
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
        if (!(object instanceof DataTableConfig)) {
            return false;
        }
        DataTableConfig other = (DataTableConfig) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "ChartConfig[ id=" + id + " ]";
    }
    
}
