/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ge.apm.service.utils.Digests;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
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
@Table(name = "user_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
@NamedQueries({
    @NamedQuery(name = "UserAccount.findAll", query = "SELECT u FROM UserAccount u")})
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "login_name")
    private String loginName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;
    @Pattern(regexp="^$|^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
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
    @Column(name = "hospital_id")
    private Integer hospitalId;
    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "pwd_salt")
    private String pwdSalt;

    @Column(name = "wechat_id")
    private String weChatId;
    
    @Column(name = "leader_user_id")
    private Integer leaderUserId;

    @Column(name = "password_update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordUpdateDate;

    @Column(name = "password_error_count")
    private Integer passwordErrorCount;

    @Column(name = "is_locked")
    private Boolean isLocked;
    
    @Transient
    private String plainPassword;
    
    @Transient
    private boolean isUnbunding;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userAccount", fetch = FetchType.LAZY)
    private List<UserRole> userRoleList;

    @Column(name = "org_id")
    @Basic(optional = false)
    @NotNull
    private Integer orgInfoId;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Date getPasswordUpdateDate() {
        return passwordUpdateDate;
    }

    public void setPasswordUpdateDate(Date passwordUpdateDate) {
        this.passwordUpdateDate = passwordUpdateDate;
    }

    public Integer getPasswordErrorCount() {
        return passwordErrorCount;
    }

    public void setPasswordErrorCount(Integer passwordErrorCount) {
        this.passwordErrorCount = passwordErrorCount;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Transient
    private List<String> userRoleNames;
    @JsonIgnore
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

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getWeChatId() {
        return weChatId;
    }

    public void setWeChatId(String weChatId) {
        this.weChatId = weChatId;
    }

    public Integer getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(Integer leaderUserId) {
        this.leaderUserId = leaderUserId;
    }
    
    public String getPwdSalt() {
        return pwdSalt;
    }

    public void setPwdSalt(String pwdSalt) {
        this.pwdSalt = pwdSalt;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public void entryptPassword() throws NoSuchAlgorithmException {
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        setPwdSalt(Digests.encodeHex(salt));
            
        byte[] hashPassword = Digests.sha1(getPlainPassword().getBytes(), salt, HASH_INTERATIONS);
        setPassword(Digests.encodeHex(hashPassword));
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

	public boolean getIsUnbunding() {
		return isUnbunding;
	}

	public void setIsUnbunding(boolean isUnbunding) {
		this.isUnbunding = isUnbunding;
	}
    
    

}
