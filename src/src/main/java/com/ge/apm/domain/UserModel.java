package com.ge.apm.domain;

public class UserModel {
	private Integer userId;
	private String hospitalName="无";
	private String userRole="无";
	private String loginName="无";
	private String userName="无";
	

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserModel [userId=");
		builder.append(userId);
		builder.append(", hospitalName=");
		builder.append(hospitalName);
		builder.append(", userRole=");
		builder.append(userRole);
		builder.append(", loginName=");
		builder.append(loginName);
		builder.append("]");
		return builder.toString();
	}
	
}
