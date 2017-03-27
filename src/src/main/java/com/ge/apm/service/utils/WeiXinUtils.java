package com.ge.apm.service.utils;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

public class WeiXinUtils {
	private static Logger logger = LoggerFactory.getLogger(WeiXinUtils.class);
	
	@Autowired
	private static WxMpUserService wxMpUserService;
	
	@Autowired
	private WxMpService wxMpService;
	
	public static WxMpUser getWxUserInfo(String openId) throws WxErrorException{
		if(StringUtils.isEmpty(openId)){
			logger.error("get wxUser,openId cannot be null !");
			return null;
		}
		return wxMpUserService.userInfo("openId");
	}
	
	public static String getWxUserOpenId(){
		//TODO get user openId
		return "openId"+new Random().nextInt(1000);
	}
}