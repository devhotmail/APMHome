package com.ge.apm.dao;

import com.ge.apm.domain.UserAccount;
import webapp.framework.dao.GenericRepository;

public interface UserAccountRepository extends GenericRepository<UserAccount> {
    UserAccount getByLoginName(String loginName);
    UserAccount getById(int userId);
}
