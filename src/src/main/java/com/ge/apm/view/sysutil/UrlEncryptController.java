package com.ge.apm.view.sysutil;

import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.service.utils.UrlParamUtil;

@ManagedBean
@RequestScoped
public class UrlEncryptController {
	public static  final Logger logger = LoggerFactory.getLogger(UrlEncryptController.class);
	private String str;
	
	public static String urlParamEncode(String str){
		return UrlParamUtil.encodeUrlQueryString(str);
	}
	
	public static Object urlParamDecode(String source){
		String des = UrlParamUtil.decodeUrlQueryString(source);
		if(des != null && des.trim().length() > 0){
			Map<String,Object> map  = UrlParamUtil.decodeUrlQueryStringToMap(des);
			if(map.isEmpty()){
				logger.error("get object from map error,source is "+source);
				return null;
			}
			return map.get(source);
		}
		return null;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	
}
