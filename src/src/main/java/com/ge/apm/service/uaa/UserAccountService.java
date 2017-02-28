package com.ge.apm.service.uaa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.UserAccount;

@Component
public class UserAccountService {
	
	@Autowired
	UserAccountMapper userAccountMapper;
	
	public List<UserAccount> getUserAccount(){
		return userAccountMapper.getAllUser();
	};
}
