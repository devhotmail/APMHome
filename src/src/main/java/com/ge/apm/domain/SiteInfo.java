/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "site_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class SiteInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uid")
    @Size(max=32)
    private String uid;
    
    @Basic(optional = false)
    @NotNull
    @Size(max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 64)
    @Column(name = "name_en")
    private String nameEn;
    @Size(max = 64)
    @Column(name = "alias_name")
    private String aliasName;
    @Size(max = 256)
    @Column(name = "site_description")
    private String description;
    @Size(max = 64)
    @Column(name = "contact_person")
    private String contactPerson;
    @Size(max = 64)
    @Column(name = "contact_phone")
    private String contactPhone;
    @Pattern(regexp="^$|^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 64)
    @Column(name = "contact_email")
    private String contactEmail;
    @Size(max = 64)
    @Column(name = "location")
    private String location;
    @Size(max = 256)
    @Column(name = "location_en")
    private String locationEn;
    @Column(name = "time_zone")
    private Integer timeZone;
    @Size(max = 16)
    @Column(name = "default_lang")
    private String defaultLang;
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Basic(optional = false)
    @NotNull
    @Column(name = "manhour_price")
    private Double manhourPrice;
    
    @Column(name = "wf_auto_step2")
    private Boolean wfAutoStep2;
    @Column(name = "wf_auto_step3")
    private Boolean wfAutoStep3;
    @Column(name = "wf_auto_step4")
    private Boolean wfAutoStep4;
    @Column(name = "wf_auto_step5")
    private Boolean wfAutoStep5;
    @Column(name = "wf_auto_step6")
    private Boolean wfAutoStep6;

    @Column(name = "password_lifetime")
    private Integer passwordLifeTime; //密码有效天数

    public SiteInfo() {
        isEnabled = true;
        wfAutoStep2 = false;
        wfAutoStep3 = false;
        wfAutoStep4 = false;
        wfAutoStep5 = false;
        wfAutoStep6 = false;
        manhourPrice = 1000.0;
    }

    @PrePersist
    public void initializeUid() {
        if(uid==null)
            uid = UUID.randomUUID().toString().replace("-", "");
    }
    
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationEn() {
        return locationEn;
    }

    public void setLocationEn(String locationEn) {
        this.locationEn = locationEn;
    }

    public Integer getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Integer timeZone) {
        this.timeZone = timeZone;
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getWfAutoStep2() {
        return wfAutoStep2;
    }

    public void setWfAutoStep2(Boolean wfAutoStep2) {
        this.wfAutoStep2 = wfAutoStep2;
    }

    public Boolean getWfAutoStep3() {
        return wfAutoStep3;
    }

    public void setWfAutoStep3(Boolean wfAutoStep3) {
        this.wfAutoStep3 = wfAutoStep3;
    }

    public Boolean getWfAutoStep4() {
        return wfAutoStep4;
    }

    public void setWfAutoStep4(Boolean wfAutoStep4) {
        this.wfAutoStep4 = wfAutoStep4;
    }

    public Boolean getWfAutoStep5() {
        return wfAutoStep5;
    }

    public void setWfAutoStep5(Boolean wfAutoStep5) {
        this.wfAutoStep5 = wfAutoStep5;
    }

    public Boolean getWfAutoStep6() {
        return wfAutoStep6;
    }

    public void setWfAutoStep6(Boolean wfAutoStep6) {
        this.wfAutoStep6 = wfAutoStep6;
    }

    public Double getManhourPrice() {
        return manhourPrice;
    }

    public void setManhourPrice(Double manhourPrice) {
        this.manhourPrice = manhourPrice;
    }

    public Integer getPasswordLifeTime() {
        return passwordLifeTime;
    }

    public void setPasswordLifeTime(Integer passwordLifeTime) {
        this.passwordLifeTime = passwordLifeTime;
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
        if (!(object instanceof SiteInfo)) {
            return false;
        }
        SiteInfo other = (SiteInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.SiteInfo[ id=" + id + " ]";
    }
    
}
