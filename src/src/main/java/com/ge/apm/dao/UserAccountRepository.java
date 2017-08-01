package com.ge.apm.dao;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserInfo;

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

    @Query(value="select ua.* from user_account ua right join (select user_id from biomed_group_user where group_id in (select biomed_group_id from asset_tag_biomed_group where tag_id in (select tag_id from asset_tag_rule where asset_id = ?1)) ) tb1 on ua.id = tb1.user_id",nativeQuery = true)
    List<UserAccount> getResponserByAssetId(Integer assetId);

    /*select ua.* from asset_tag_rule atr,asset_tag ast,asset_tag_biomed_group atbg, biomed_group bg,biomed_group_user bgu, user_account ua where atr.asset_id=23 and atr.tag_id = ast.id and ast.id= atbg.tag_id and atbg.biomed_group_id =bg.id and bg.id=bgu.group_id and bgu.user_id = ua.id -- same as below*/
    @Query(value="select ua.* from user_account ua where id in (select asset_owner_id from asset_info where id = ?1 union select asset_owner_id2 from asset_info where id = ?1)",nativeQuery = true)
    List<UserAccount> getResponserWithAsset(Integer assetId);

    //如果没有对某资产配置过对应的医院工，那么就通过标签中的siteId获取默认的医工
    @Query(value="select ua.* from user_account ua, asset_info ai, user_role r where ai.id = ?1 and ai.site_id = ua.site_id and ua.id = r.user_id and r.role_id = 3",nativeQuery=true)
    List<UserAccount> getDefaultUsers(int assetId);
	List<UserInfo> findByInstitutionUID(String institutionUID);

    /* sysrole.id=2*/

}
