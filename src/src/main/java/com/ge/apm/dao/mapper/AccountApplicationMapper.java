package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Insert;

import com.ge.apm.domain.AccountApplication;

public interface AccountApplicationMapper {

	@Insert("insert into account_application(wechat_id,name,telephone,hospital_name,clinical_dept_name,role_id,comment,application_date,status,password) values("+
			 "#{wechatId},#{name},#{telephone},#{hospitalName},#{clinicalDeptName},#{roleId},#{comment},now(),1,#{password})")
	public void saveAccountApplication(AccountApplication accountApplication);
	
}
