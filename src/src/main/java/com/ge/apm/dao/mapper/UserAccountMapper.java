package com.ge.apm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.ge.apm.domain.UserAccount;

public interface UserAccountMapper {
	@Select("select * from user_account")
	public List<UserAccount> getAllUser();
}
