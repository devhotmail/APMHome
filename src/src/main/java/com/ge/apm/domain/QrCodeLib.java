/*
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "qr_code_lib")
public class QrCodeLib implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "site_id")
    private int siteId;
    @Basic(optional = false)
    @Column(name = "hospital_id")
    private int hospitalId;
    @Basic(optional = false)
    @Column(name = "qr_code")
    private String qrCode;
    @Basic(optional = false)
    @Column(name = "issue_date")
    @Temporal(TemporalType.DATE)
    private Date issueDate;
    @Column(name = "submit_date")
    @Temporal(TemporalType.DATE)
    private Date submitDate;
    @Column(name = "submit_wechat_id")
    private Integer submitWechatId;
    @Column(name = "comment")
    private String comment;
    @Basic(optional = false)
    @Column(name = "status")
    private int status;

    public QrCodeLib() {
    }

    public QrCodeLib(Integer id) {
        this.id = id;
    }

    public QrCodeLib(Integer id, int siteId, int hospitalId, String qrCode, Date issueDate, int status) {
        this.id = id;
        this.siteId = siteId;
        this.hospitalId = hospitalId;
        this.qrCode = qrCode;
        this.issueDate = issueDate;
        this.status = status;
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

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Integer getSubmitWechatId() {
        return submitWechatId;
    }

    public void setSubmitWechatId(Integer submitWechatId) {
        this.submitWechatId = submitWechatId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
        if (!(object instanceof QrCodeLib)) {
            return false;
        }
        QrCodeLib other = (QrCodeLib) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.QrCodeLib[ id=" + id + " ]";
    }
    
}
