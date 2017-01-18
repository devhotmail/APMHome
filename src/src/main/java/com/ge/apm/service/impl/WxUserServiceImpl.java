package com.ge.apm.service.impl;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.WxUserService;

@Service
public class WxUserServiceImpl implements WxUserService{
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserAccountRepository userAccountRepository;
	
	@Override
	public UserAccount getUser(String openId) {
		return userAccountRepository.getByWeChatId(openId);
	}

	@Override
	public void resetPassword(String openId, String newPassword) {
		UserAccount ua = userAccountRepository.getByWeChatId(openId);
		if(ua == null){
			logger.error("cannot find user by openId,openId is {}",openId);
			//TODO redirect to login
		}
		ua.setPlainPassword(newPassword);
		try {
			ua.entryptPassword();
		} catch (NoSuchAlgorithmException e) {
			logger.error("encry password error !");
			e.printStackTrace();
		}
		userAccountRepository.save(ua);
	}

}
