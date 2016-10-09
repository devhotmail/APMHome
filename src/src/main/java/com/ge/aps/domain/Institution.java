/*
 */
package com.ge.aps.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "institution")
public class Institution implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 60)
    @Column(name = "name")
    private String name;
    @Size(max = 60)
    @Column(name = "name_en")
    private String nameEn;
    @Size(max = 60)
    @Column(name = "alias")
    private String alias;
    @Size(max = 600)
    @Column(name = "description")
    private String description;
    @Size(max = 30)
    @Column(name = "contact_person")
    private String contactPerson;
    @Size(max = 16)
    @Column(name = "contact_phone")
    private String contactPhone;
    @Size(max = 30)
    @Column(name = "contact_email")
    private String contactEmail;
    @Size(max = 80)
    @Column(name = "location")
    private String location;
    @Size(max = 80)
    @Column(name = "location_en")
    private String locationEn;
    @Column(name = "time_zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeZone;
    @Column(name = "is_enabled")
    private Boolean isEnabled;
    @Size(max = 6)
    @Column(name = "default_lang")
    private String defaultLang;

    public Institution() {
    }

    public Institution(Integer id) {
        this.id = id;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public Date getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Date timeZone) {
        this.timeZone = timeZone;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
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
        if (!(object instanceof Institution)) {
            return false;
        }
        Institution other = (Institution) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "Institution[ id=" + id + " ]";
    }
    
}
