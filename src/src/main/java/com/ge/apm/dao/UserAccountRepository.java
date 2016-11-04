package com.ge.apm.dao;

import com.ge.apm.domain.UserAccount;
import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import webapp.framework.dao.GenericRepository;

public interface UserAccountRepository extends GenericRepository<UserAccount> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAccount getByLoginName(String loginName);
    
    UserAccount getById(int userId);
    
    @Query("select u from UserAccount u, OrgInfo h where u.orgInfoId=h.id and h.hospitalId=?1")
    List<UserAccount> getUserListByHospitalId(int hospitalId);
    
    @Query("select u from UserAccount u, OrgInfo h, UserRole r where u.orgInfoId=h.id and h.hospitalId=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> getUserListByHospitalIdWithAssetStuffRole(int hospitalId);
}
