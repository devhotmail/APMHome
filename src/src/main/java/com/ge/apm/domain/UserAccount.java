/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import webapp.framework.util.StringUtil;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "user_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
@NamedQueries({
    @NamedQuery(name = "UserAccount.findAll", query = "SELECT u FROM UserAccount u")})
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "login_name")
    private String loginName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "password")
    private String password;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "email")
    private String email;
    @Size(max = 16)
    @Column(name = "telephone")
    private String telephone;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_super_admin")
    private boolean isSuperAdmin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_site_admin")
    private boolean isSiteAdmin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_local_admin")
    private boolean isLocalAdmin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "is_online")
    private Boolean isOnline;
    @Column(name = "site_id")
    private Integer siteId;
    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userAccount", fetch = FetchType.EAGER)
    private List<UserRole> userRoleList;

    @Column(name = "org_id")
    @Basic(optional = false)
    @NotNull
    private Integer orgInfoId;

    @JoinColumn(insertable=false,updatable=false, name = "org_id", referencedColumnName = "id")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private OrgInfo orgInfo;

    public UserAccount() {
        isSuperAdmin = false;
        isSiteAdmin = false;
        isLocalAdmin = false;
        isActive = true;
        isOnline = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordEncryped() {
        return password;
    }

    public String getPassword() {
        return StringUtil.desDecrypt(password);
    }

    public void setPassword(String password) {
        this.password = StringUtil.desEncrypt(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean getIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public boolean getIsSiteAdmin() {
        return isSiteAdmin;
    }

    public void setIsSiteAdmin(boolean isSiteAdmin) {
        this.isSiteAdmin = isSiteAdmin;
    }

    public boolean getIsLocalAdmin() {
        return isLocalAdmin;
    }

    public void setIsLocalAdmin(boolean isLocalAdmin) {
        this.isLocalAdmin = isLocalAdmin;
    }

    public Integer getOrgInfoId() {
        return orgInfoId;
    }

    public void setOrgInfoId(Integer orgInfoId) {
        this.orgInfoId = orgInfoId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public List<UserRole> getUserRoleList() {
        return userRoleList;
    }

    public void setUserRoleList(List<UserRole> userRoleList) {
        this.userRoleList = userRoleList;
    }

    public OrgInfo getOrgInfo() {
        return orgInfo;
    }

    public void setOrgInfo(OrgInfo orgInfo) {
        this.orgInfo = orgInfo;
    }

    @Transient
    private List<String> userRoleNames;
    public List<String> getRoleNames(){
        if(userRoleNames==null) {
            userRoleNames = new ArrayList<String>();
            for(UserRole userRole: userRoleList){
                userRoleNames.add(userRole.getRole().getName());
            }

            if(this.isSuperAdmin) userRoleNames.add("SuperAdmin");
            if(this.isSiteAdmin) userRoleNames.add("SiteAdmin");
            if(this.isLocalAdmin) userRoleNames.add("LocalAdmin");
        }

        return userRoleNames;
    }

    public OrgInfo getHospital(){
        return this.getOrgInfo().getHospital();
    }
    
    public Integer getHospitalId(){
        return this.getOrgInfo().getHospitalId();
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
        if (!(object instanceof UserAccount)) {
            return false;
        }
        UserAccount other = (UserAccount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.UserAccount[ id=" + id + " ]";
    }

}
