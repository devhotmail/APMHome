package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "MM3_data_record")
public class MM3DataRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

   
    @Column(name = "asset_id")
    @Basic(optional = true)
    private Integer assetId;

    @Column(name = "asset_uid")
    @Basic(optional = true)
    private String assetUid;
    
    @Column(name = "system_id")
    @Basic(optional = false)
    private String systemId;
    
    @Column(name = "chinese_name")
    @Basic(optional = true)
    private String chineseName;
    
    @Column(name = "product")
    @Basic(optional = true)
    private String product;
    
    
    @Column(name = "coldhead_ruo")
    @Basic(optional = true)
    private Double coldheadRuO;
    
    
    @Column(name = "he_pressure")
    @Basic(optional = true)
    private Double hePressure;
     
    
    @Column(name = "report_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportTime;
    
    @Column(name = "he_level")
    @Basic(optional = true)
    private Double heLevel;
    
    @Column(name = "shield_temp")
    @Basic(optional = true)
    private Double shieldTemp;
    
    @Column(name = "recon_ruo")
    @Basic(optional = true)
    private Double reconRuO;
    
    @Column(name = "heater_duty_cycle")
    @Basic(optional = true)
    private Double heaterDutyCycle;
    
    @Column(name = "heater_off_pressure")
    @Basic(optional = true)
    private Double heaterOffPressure;
    
    @Column(name = "heater_on_pressure")
    @Basic(optional = true)
    private Double heaterOnPressure;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Double getColdheadRuO() {
        return coldheadRuO;
    }

    public void setColdheadRuO(Double coldheadRuO) {
        this.coldheadRuO = coldheadRuO;
    }

    public Double getHePressure() {
        return hePressure;
    }

    public void setHePressure(Double hePressure) {
        this.hePressure = hePressure;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public Double getHeLevel() {
        return heLevel;
    }

    public void setHeLevel(Double heLevel) {
        this.heLevel = heLevel;
    }

    public Double getShieldTemp() {
        return shieldTemp;
    }

    public void setShieldTemp(Double shieldTemp) {
        this.shieldTemp = shieldTemp;
    }

    public Double getReconRuO() {
        return reconRuO;
    }

    public void setReconRuO(Double reconRuO) {
        this.reconRuO = reconRuO;
    }

    public Double getHeaterDutyCycle() {
        return heaterDutyCycle;
    }

    public void setHeaterDutyCycle(Double heaterDutyCycle) {
        this.heaterDutyCycle = heaterDutyCycle;
    }

    public Double getHeaterOffPressure() {
        return heaterOffPressure;
    }

    public void setHeaterOffPressure(Double heaterOffPressure) {
        this.heaterOffPressure = heaterOffPressure;
    }

    public Double getHeaterOnPressure() {
        return heaterOnPressure;
    }

    public void setHeaterOnPressure(Double heaterOnPressure) {
        this.heaterOnPressure = heaterOnPressure;
    }
    
    

    


    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MM3DataRecord)) {
            return false;
        }
        MM3DataRecord other = (MM3DataRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
