package com.ge.apm.view.sysutil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
	public static final String PERIOD="?";
	public static final String REQUEST="str=";
	
	public static String encodeUrlParam(String str){
		if(str == null || str.trim().length() == 0){
			logger.error("str should not be null !");
			return "";
		}
		if(!str.contains(PERIOD)){
			logger.error("str is illegal!");
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		String[] param = str.split(PREFIX); 
		String result = null;
		sb.append(PERIOD).append(REDIRCT);
		String temp;
		if(str.startsWith(PERIOD)){
			sb.append(REQUEST);
			result = sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString();
		}else{
			sb.append(REQUEST);
			temp =UrlParamUtil.encodeUrlQueryString(param[1]);
			try {
				temp = URLEncoder.encode(temp, "utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("os not support utf-8,msg is "+e.getMessage());
			}
			result =param[0].concat(sb.append(temp).toString());
		}
		return result;
	}

	public  static Object getValueFromMap(String encodeStr,String key){
        if(encodeStr != null){
			Map<String, Object> map = (Map<String, Object>) UrlParamUtil.getMapFromStr(encodeStr);
        	if(map != null && !map.isEmpty()){
        		return map.get(key);
        	}
        }
        return null;
	}
	
}
