package com.ge.apm.service.uaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.apm.dao.mapper.AccountApplicationMapper;
import com.ge.apm.domain.AccountApplication;

@Service
public class AccountApplicationService {
	
	@Autowired
	AccountApplicationMapper AccountApplicationMapper;
	
	public void applyRegistration(AccountApplication accountApplication) {
		AccountApplicationMapper.saveAccountApplication(accountApplication);
	}
	
}
