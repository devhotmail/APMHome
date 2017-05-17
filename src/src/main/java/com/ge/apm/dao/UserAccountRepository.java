package com.ge.apm.dao;

import com.ge.apm.domain.UserAccount;
import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import webapp.framework.dao.GenericRepository;

public interface UserAccountRepository extends GenericRepository<UserAccount> {
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAccount getByLoginName(String loginName);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAccount getById(int userId);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<UserAccount> getByHospitalId(int hospitalId);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Query("select u from UserAccount u, UserRole r where u.hospitalId=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> getUsersWithAssetStaffRole(int hospitalId);

    @Query("select u from UserAccount u, UserRole r where u.siteId=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> getUsersWithAssetStaffRoleBySiteId(int siteId);

    @Query("select distinct u from UserAccount u, UserRole r where u.hospitalId=?1 and u.id=r.userId and r.roleId in (2,3)")
    List<UserAccount> getUsersWithAssetHeadOrStaffRole(int hospitalId);

    @Query("SELECT distinct u FROM UserAccount u,UserRole ur where u.id=ur.userId and ((ur.roleId='6' and u.siteId=?1 ) or (u.hospitalId=?2 and ur.roleId=3 ))")
    List<UserAccount> getAssetOnwers(int siteId,int hospitalId);

    @Query("SELECT distinct u FROM UserAccount u,UserRole ur where u.id=ur.userId and ((ur.roleId='2' and u.siteId=?1 ) or (u.hospitalId=?2 and ur.roleId=2 ))")
    List<UserAccount> getAssetHead(int siteId,int hospitalId);
    
    @Query("select u from UserAccount u, UserRole r where u.hospitalId=?1 and u.id=r.userId and r.roleId=8")
    List<UserAccount> getUsersWithWorkOrderDispatcherRole(int hospitalId);

    List<UserAccount> getBySiteId(int siteId);

    Page<UserAccount> getBySiteId(Pageable pageRequest, int siteId);
    Page<UserAccount> getByOrgInfoId(Pageable pageRequest, int orgInfoId);
    UserAccount getByWeChatId(String weChatId);
    List<UserAccount> getByOrgInfoId(int orgInfoId);

    /* sysrole.id=2*/

}
