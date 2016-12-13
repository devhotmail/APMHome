package com.ge.apm.service.utils;

import java.util.HashMap;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class UrlParamUtil {

    private static final Logger log = Logger.getLogger(UrlParamUtil.class.getName());
    private static Base64 base64=new Base64();
        
    public static String encodeUrlQueryString(String plainUrlQueryString){
        return new String(base64.encode(plainUrlQueryString.getBytes())); 
    }

    public static String decodeUrlQueryString(String encodedUrlQueryString){
        return new String(base64.decode(encodedUrlQueryString));
    }

    public static Map<String, Object> decodeUrlQueryStringToMap(){
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String queryString = request.getQueryString();
        if(queryString==null) return null;
        
        return decodeUrlQueryStringToMap(queryString);
    }
    
    public static Map<String, Object> decodeUrlQueryStringToMap(String encodedUrlQueryString){
        String strParam = decodeUrlQueryString(encodedUrlQueryString);
        String[] params = strParam.split("&");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        for(String paramKeyValuePair: params){
            String[] paramKeyValue = paramKeyValuePair.split("=");
            if(paramKeyValue.length>1){
                paramMap.put(paramKeyValue[0], paramKeyValue[1]);
            }
        }
        
        return paramMap;
    }
    
    public static void main(String[] args) {
		System.out.println(decodeUrlQueryStringToMap("ZmFjZXMtcmVkaXJlY3Q9dHJ1ZSZwcm9wZXJ0eVN0cj1oZWxsbw=="));
		System.out.println(decodeUrlQueryStringToMap("d29JZD0xMA%3D%3D"));
		
		System.out.println(encodeUrlQueryString("?propertyStr=hello"));
		System.out.println(encodeUrlQueryString("?propertyStr=中文"));
	}
    
}
