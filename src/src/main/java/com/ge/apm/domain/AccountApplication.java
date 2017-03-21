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

import com.ge.apm.service.utils.TimeUtils;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "account_application")
public class AccountApplication implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "wechat_id")
    private String wechatId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "telephone")
    private String telephone;
    @Basic(optional = false)
    @Column(name = "hospital_name")
    private String hospitalName;
    @Basic(optional = false)
    @Column(name = "role_id")
    private int roleId;
    @Column(name = "comment")
    private String comment;
    @Column(name = "application_date")
    @Temporal(TemporalType.DATE)
    private Date applicationDate;
    @Column(name = "status")
    private Integer status;
    
    private String password;
    private String applyDate;
    
    
    public String getApplyDate() {
    	return TimeUtils.getStrDate(applicationDate, null);
	}

	@Column(name = "clinical_dept_name")
    private String clinicalDeptName;
    
	public AccountApplication() {
    }
	
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public String getClinicalDeptName() {
		return clinicalDeptName;
	}

	public void setClinicalDeptName(String clinicalDeptName) {
		this.clinicalDeptName = clinicalDeptName;
	}

	public AccountApplication(Integer id) {
        this.id = id;
    }

    public AccountApplication(Integer id, String wechatId, String name, String telephone, String hospitalName, int roleId) {
        this.id = id;
        this.wechatId = wechatId;
        this.name = name;
        this.telephone = telephone;
        this.hospitalName = hospitalName;
        this.roleId = roleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getApplicationDate() {
    	
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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
        if (!(object instanceof AccountApplication)) {
            return false;
        }
        AccountApplication other = (AccountApplication) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountApplication [id=");
		builder.append(id);
		builder.append(", wechatId=");
		builder.append(wechatId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", telephone=");
		builder.append(telephone);
		builder.append(", hospitalName=");
		builder.append(hospitalName);
		builder.append(", roleId=");
		builder.append(roleId);
		builder.append(", comment=");
		builder.append(comment);
		builder.append(", applicationDate=");
		builder.append(applicationDate);
		builder.append(", status=");
		builder.append(status);
		builder.append(", password=");
		builder.append(password);
		builder.append(", clinicalDeptName=");
		builder.append(clinicalDeptName);
		builder.append("]");
		return builder.toString();
	}
	
    
    
}
