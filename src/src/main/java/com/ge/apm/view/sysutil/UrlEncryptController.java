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
	public static final String PREFIX = "\\?";
	public static final String REDIRCT = "faces-redirect=true&amp;";
	
	public static String encodeUrlParam(String str,Boolean needEncode){
		if(str == null || str.trim().length() == 0 || needEncode == null){
			logger.error("str should not be null !");
			return "";
		}
		if(!str.contains("?")){
			logger.error("str is illegal!");
			return "";
		}
		StringBuilder sb = new StringBuilder();
		String[] param = str.split(PREFIX);
		if(needEncode){
			sb.append("?").append(REDIRCT);
			if(str.startsWith(PREFIX)){
				sb.append(UrlParamUtil.encodeUrlQueryString(param[1]));
				return sb.toString();
			}else{
				sb.append("str=");
				String s =param[0].concat(sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString());
				//return param[0].concat(sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString());
				return s;
				}
			}else{
				if(str.startsWith(PREFIX)){
					sb.append("?").append(UrlParamUtil.encodeUrlQueryString(param[1]));
					return sb.toString();
				}else{
					return param[0].concat(sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString());
				}
			}
	}
	
	public static Object urlParamDecode(String source){
		if(source == null || source.trim().length() == 0){
			logger.error("source is illegal !");
			return null;
		}
		String des = UrlParamUtil.decodeUrlQueryString(source);
		if(des != null && des.trim().length() > 0){
			Map<String,Object> map  = UrlParamUtil.decodeUrlQueryStringToMap(des);
			if(map.isEmpty()){
				logger.error("get object from map error,source is "+source);
				return null;
			}
			return map;
		}
		logger.error("decode str is not right, str is "+source);
		return null;
	}
	
}
