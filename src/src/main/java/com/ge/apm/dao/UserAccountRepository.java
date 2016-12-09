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
    List<UserAccount> getUserListByHospitalIdWithAssetStaffRole(int hospitalId);
    
    Page<UserAccount> getBySiteId(Pageable pageRequest, int siteId);
    Page<UserAccount> getByOrgInfoId(Pageable pageRequest, int orgInfoId);
    
}
