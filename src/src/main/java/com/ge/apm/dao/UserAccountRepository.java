package com.ge.apm.dao;

import com.ge.apm.domain.UserAccount;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import webapp.framework.dao.GenericRepository;

public interface UserAccountRepository extends GenericRepository<UserAccount> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAccount getByLoginName(String loginName);
    
    UserAccount getById(int userId);
}
