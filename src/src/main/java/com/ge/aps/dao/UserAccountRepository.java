package com.ge.aps.dao;

import com.ge.aps.domain.UserAccount;
import webapp.framework.dao.GenericRepository;

public interface UserAccountRepository extends GenericRepository<UserAccount> {
    UserAccount getByLoginName(String loginName);
    UserAccount getById(int userId);
}
