package com.ge.apm.view.sysutil;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.service.utils.UrlParamUtil;

@ManagedBean
@RequestScoped
public class UrlEncryptController {
	
	public static  final Logger logger = LoggerFactory.getLogger(UrlEncryptController.class);
	public static final String PREFIX = "\\?";
	public static final String REDIRCT = "faces-redirect=true&amp;";
	
	public static String encodeUrlParam(String str){
		if(str == null || str.trim().length() == 0){
			logger.error("str should not be null !");
			return "";
		}
		if(!str.contains("?")){
			logger.error("str is illegal!");
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		String[] param = str.split(PREFIX); 
		logger.info("param is "+param +"after split");
		String result = null;
		sb.append("?").append(REDIRCT);
		if(str.startsWith("?")){
			sb.append("str=");
			result = sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString();
		}else{
			sb.append("str=");
			result =param[0].concat(sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString());
		}
	
		if(result.endsWith("=") && !result.endsWith("==")){
			logger.info("result ends with =,str is "+result);
			String temp1 = result.substring(0,result.length()-1);
			String temp2 = temp1+"%3D";
			result = temp2;
		}
		if(result.endsWith("==")){
			logger.info("result ends with ==,str is "+result);
			result = result.replaceAll("==", "%3D%3D");
		}
		return result;
	}
	
	@Deprecated
	public static String encodeUrlParam(String str,Boolean needEncode){
		return null;
//		if(str == null || str.trim().length() == 0 || needEncode == null){
//			logger.error("str should not be null !");
//			return "";
//		}
//		if(!str.contains("?")){
//			logger.error("str is illegal!");
//			return "";
//		}
//		StringBuilder sb = new StringBuilder();
//		String[] param = str.split(PREFIX); 
//		String s = null;
//		if(needEncode){
//			sb.append("?").append(REDIRCT);
//			if(str.startsWith(PREFIX)){
//				sb.append("str=");
//				s = sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString();
//			}else{
//				sb.append("str=");
//				s =param[0].concat(sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString());
//				}
//			}else{
//				if(str.startsWith("?")){
//					//sb.append("?").append(UrlParamUtil.encodeUrlQueryString(param[1]));
//					s = sb.append("?").append("str=").append(UrlParamUtil.encodeUrlQueryString(param[1])).toString();
//				}else{
//					sb.append("str=");
//					s = param[0].concat("?").concat(sb.append(UrlParamUtil.encodeUrlQueryString(param[1])).toString());
//				}
//			}
//		if(s.endsWith("=") && !s.endsWith("==")){
//			String s2 = s.substring(0,s.length()-1);
//			String s3 = s2+"%3D";
//			s = s3;
//		}
//		if(s.endsWith("==")){
//			s.replaceAll("==", "%3D%3D");
//		}
//		return s;
	}
	
	public static Object urlParamDecode(String source){
		if(source != null && source.trim().length() > 0){
			Map<String,Object> map  = UrlParamUtil.decodeUrlQueryStringToMap(source);
			if(map.isEmpty()){
				logger.error("get object from map error,source is "+source);
				return null;
			}
			return map;
		}
		logger.error("decode str is not right, str is "+source);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public  static Object getMap(String encodeStr,String key){
//        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
//        String encodeStr = request.getParameter("str");
        if(encodeStr == null){
        	logger.error("get param from map error ,encodeStr is null.");
        	return null;
        }
        if(encodeStr.contains("%3D")){
        	encodeStr = encodeStr.replaceAll("%3D", "=");
        }
        if(encodeStr != null){
			Map<String, Object> map = (Map<String, Object>) urlParamDecode(encodeStr);
        	if(map != null && !map.isEmpty()){
        		return map.get(key);
        	}
        }
        return null;
	}
	
}
