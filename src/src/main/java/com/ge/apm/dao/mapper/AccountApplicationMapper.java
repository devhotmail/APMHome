package com.ge.apm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import com.ge.apm.domain.AccountApplication;

public interface AccountApplicationMapper {

	@Insert("insert into account_application(wechat_id,name,telephone,hospital_name,clinical_dept_name,role_id,comment,application_date,status,password) values("+
			 "#{wechatId},#{name},#{telephone},#{hospitalName},#{clinicalDeptName},#{roleId},#{comment},now(),1,#{password})")
	public void saveAccountApplication(AccountApplication accountApplication);

	@Select("select * from account_application where wechat_id = #{openId} order by application_date desc")
	public List<AccountApplication> getApplyList(String openId);
	
	@Select("select * from account_application order by application_date desc")
	public List<AccountApplication> getAll();
	
	@Select("select * from account_application where id = #{applyId}")
	public AccountApplication getApplyById(Integer applyId);
	
}
