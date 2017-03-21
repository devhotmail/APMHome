package com.ge.apm.service.uaa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.apm.dao.mapper.AccountApplicationMapper;
import com.ge.apm.domain.AccountApplication;

@Service
public class AccountApplicationService {
	
	@Autowired
	AccountApplicationMapper accountApplicationMapper;
	
	public void applyRegistration(AccountApplication accountApplication) {
		accountApplicationMapper.saveAccountApplication(accountApplication);
	}

	public List<AccountApplication> getApplyList(String openId) {
		return accountApplicationMapper.getAll();
	}

	public AccountApplication getApplyById(Integer applyId) {
		return accountApplicationMapper.getApplyById(applyId);
	}
	
}
