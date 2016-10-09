/*
 */
package com.ge.aps.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.joda.time.DateTime;
import webapp.framework.util.StringUtil;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "user_account")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "login_name")
    private String loginName;
    @Size(max = 50)
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Size(max = 20)
    @Column(name = "telephone")
    private String telephone;
    @Size(max = 20)
    @Column(name = "last_login_datetime")
    private String lastLoginDatetime;
    @Column(name = "is_super_admin")
    private Boolean isSuperAdmin;
    @Column(name = "is_tenant_admin")
    private Boolean isTenantAdmin;
    @Column(name = "institution_id")
    private Integer institutionId;
    @Column(name = "status")
    private Integer status;
    @Column(name = "is_online")
    private Boolean isOnline;
    
    @Transient
    private DateTime lastActiveTime;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userAccount", fetch = FetchType.LAZY)
    private List<UserRole> userRoles;
    
    public UserAccount() {
    }

    public UserAccount(Integer id) {
        this.id = id;
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

    public String getPassword2() {
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

    public String getLastLoginDatetime() {
        return lastLoginDatetime;
    }

    public void setLastLoginDatetime(String lastLoginDatetime) {
        this.lastLoginDatetime = lastLoginDatetime;
    }

    public Boolean getIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(Boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public Boolean getIsTenantAdmin() {
        return isTenantAdmin;
    }

    public void setIsTenantAdmin(Boolean isTenantAdmin) {
        this.isTenantAdmin = isTenantAdmin;
    }

    public Integer getInstitutionId() {
        return 1;
        //return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<String> getUserRoleNames() {
        return userRoleNames;
    }

    public void setUserRoleNames(List<String> userRoleNames) {
        this.userRoleNames = userRoleNames;
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
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Transient
    private List<String> userRoleNames;
    public List<String> getRoleNames(){
        if(userRoleNames==null) {
            userRoleNames = new ArrayList<String>();
            for(UserRole userRole: userRoles){
                userRoleNames.add(userRole.getRole().getName());
            }

            try{
                if(this.isSuperAdmin) userRoleNames.add("SuperAdmin");
            }
            catch(Exception ex){
            }
            try{
                if(this.isTenantAdmin) userRoleNames.add("TenantAdmin");
            }
            catch(Exception ex){
            }
        }

        return userRoleNames;
    }

    public DateTime getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(DateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public Boolean getIsOnline() {
            return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
            this.isOnline = isOnline;
    }
    
    @Override
    public String toString() {
        return "UserAccount[ id=" + id + ", lastActiveTime="+lastActiveTime+" ]";
    }
}
