package com.ge.apm.service.uaa;

import com.ge.apm.domain.UserAccount;

public interface WxUserService {
	public UserAccount getUser(String openId);
	public void resetPassword(String openId, String oldPassword, String newPassword);
}
