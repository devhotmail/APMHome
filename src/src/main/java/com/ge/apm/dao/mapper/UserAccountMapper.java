package com.ge.apm.dao.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import com.ge.apm.dao.mapper.provider.UserAccountProvider;
import com.ge.apm.domain.UserAccount;

public interface UserAccountMapper {
	@Select("select * from user_account")
	public List<UserAccount> getAllUser();
	
	@SelectProvider(type = UserAccountProvider.class,method = "getUserWechatId")
	public List<UserAccount> getUserWechatId(@Param("ids") List<Integer> ids);
}
