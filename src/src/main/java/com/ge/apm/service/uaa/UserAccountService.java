package com.ge.apm.service.uaa;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserModel;

@Component
public class UserAccountService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	UserAccountMapper userAccountMapper;
	
	public List<UserAccount> getUserAccount(){
		return userAccountMapper.getAllUser();
	};
	
	public UserModel getUserAccoutByUserId(Integer userId){
		UserModel user = userAccountMapper.getUserAccoutByUserId(userId);
		if(user == null){
			return null;
		}
		List<SysRole> roles = userAccountMapper.getUserRoles(user.getUserId());
		if(CollectionUtils.isEmpty(roles)){
			logger.error("{} has none role,user id is {}",user.getLoginName(),user.getUserId());
			return user;
		}
		StringBuilder sb = new StringBuilder();
		for (SysRole sysRole : roles) {
			sb.append(sysRole.getRoleDesc()+",");
		}
		String role = sb.toString();
		if(role.endsWith(",")){
			role = role.substring(0, role.length()-1);
		}
		user.setUserRole(role);
		return user;
	}
}
