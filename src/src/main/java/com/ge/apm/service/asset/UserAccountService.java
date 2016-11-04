/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean(name="userAccountService", eager = true)
@ApplicationScoped
public class UserAccountService {
    
    UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
    
    
    public UserAccount getUser(int id) {
        return userDao.findById(id);
    }
    public List<UserAccount> getUserList() {
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        List<SearchFilter> usersSearchFilters = new ArrayList<>();
        usersSearchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId()));
        List<UserAccount> userList = userDao.findBySearchFilter(usersSearchFilters);
        return userList;
    }
}
