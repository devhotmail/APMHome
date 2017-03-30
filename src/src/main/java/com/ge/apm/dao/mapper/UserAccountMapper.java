package com.ge.apm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import com.ge.apm.dao.mapper.provider.UserAccountProvider;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserModel;

public interface UserAccountMapper {
	@Select("select * from user_account")
	public List<UserAccount> getAllUser();
	
	@SelectProvider(type = UserAccountProvider.class,method = "getUserWechatId")
	public List<UserAccount> getUserWechatId(@Param("ids") List<Integer> ids);

	@Select("select ua.id user_id, ua.login_name,ua.name user_name,oi.name hospital_name from user_account ua left join "+
					"org_info oi on ua.hospital_id = oi.id where ua.id=#{userId}")
	public UserModel getUserAccoutByUserId(Integer userId);

	@Select("select id,role_desc from sys_role where id in(select role_id from user_role where user_id = #{userId})")
	public List<SysRole> getUserRoles(Integer userId);
}
