package com.ge.apm.service.uaa;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserModel;

@Component
public class UserAccountService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	UserAccountMapper userAccountMapper;

	@Autowired
	AssetInfoMapper assetInfoMapper;

	public List<UserAccount> getUserAccount() {
		return userAccountMapper.getAllUser();
	};

	public UserAccount getUserById(Integer id) {
		return userAccountMapper.getUserById(id);
	}

	public UserModel getUserAccoutByUserId(Integer userId) {
		UserModel user = userAccountMapper.getUserAccoutByUserId(userId);
		if (user == null) {
			return null;
		}
		List<SysRole> roles = userAccountMapper.getUserRoles(user.getUserId());
		if (CollectionUtils.isEmpty(roles)) {
			logger.error("{} has none role,user id is {}", user.getLoginName(), user.getUserId());
			return user;
		}
		StringBuilder sb = new StringBuilder();
		for (SysRole sysRole : roles) {
			sb.append(sysRole.getRoleDesc() + ",");
		}
		String role = sb.toString();
		if (role.endsWith(",")) {
			role = role.substring(0, role.length() - 1);
		}
		user.setUserRole(role);
		return user;
	}

	public boolean isExists(String openId) {
		return userAccountMapper.getUserByWechatId(openId) != null ? true : false;
	}

	@Transactional
	public UserAccount createUser(String name, String mobile, Integer assetId, String wechatId, String telephone,String nickName) {
		logger.info("create user ,name is {},mobile is {},assetId is {},wechatId is {},telephone is {}", name, mobile,assetId, wechatId, telephone);
		AssetInfo asset = assetInfoMapper.fetchAssetInfoById(assetId);
		if (asset != null) {
			UserAccount user = new UserAccount();
			user.setWeChatId(wechatId);
			user.setName(name);
			user.setLoginName(wechatId);
			user.setTelephone(telephone);
			user.setSiteId(asset.getSiteId());
			user.setHospitalId(asset.getHospitalId());
			user.setOrgInfoId(asset.getClinicalDeptId());
			user.setIsActive(true);
			user.setPlainPassword("123456");
			//TODO nickName
			
			try {
				user.entryptPassword();
				userAccountMapper.saveUserAccount(user);
				userAccountMapper.saveUserRole(user.getId());
				user.setPlainPassword(null);
				return user;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		logger.error("cannot find asset by id : {}", assetId);
		return null;
	}
}
