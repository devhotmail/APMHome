package com.ge.apm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

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

	@Select("select id,name,wechat_id,login_name from user_account where id =#{id}")
	public UserAccount getUserById(Integer id);
	
	@Update("update user_account set wechat_id = null where wechat_id=#{openId}")
	public void updateUserWechatIdNull(String openId);

	@Select("select subscribe_user_id from message_subscriber where asset_id = #{asset_id} and is_receive_timeout_msg= true ")
	public List<Integer> getAssetSubscriber(Integer assetId);

	@Select("select user_id from user_role where role_id=8 and user_id in (select id from user_account where hospital_id = "+
			"(select hospital_id from user_account where id=#{requestorId}))")
	public List<Integer> fetchDispaterUser(Integer requestorId);
	
	@Select("select id from user_account where wechat_id =#{wechatId}")
	public UserAccount getUserByWechatId(String wechatId);

	@Insert("insert into user_account(telephone,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,is_super_admin,is_site_admin,is_local_admin,is_active,wechat_id) "+
			 " values "+
			" (#{telephone},#{siteId},#{hospitalId},#{orgInfoId},#{loginName},#{name},#{pwdSalt},#{password},'temp@ge.com',false,false,false,true,#{weChatId})")
	@Options(useGeneratedKeys=true)
	public Integer saveUserAccount(UserAccount user);

	@Insert("insert into user_role(user_id,role_id) values (#{userId},9)")
	public void saveUserRole(Integer userId);
} 
