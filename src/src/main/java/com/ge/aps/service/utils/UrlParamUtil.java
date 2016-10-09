package com.ge.aps.service.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
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

    public static Map<String, String> decodeUrlQueryStringToMap(String encodedUrlQueryString){
        String strParam = decodeUrlQueryString(encodedUrlQueryString);
        String[] params = strParam.split("&");
        Map<String, String> paramMap = new HashMap<String, String>();
        for(String paramKeyValuePair: params){
            String[] paramKeyValue = paramKeyValuePair.split("=");
            if(paramKeyValue.length>1){
                paramMap.put(paramKeyValue[0], paramKeyValue[1]);
            }
        }
        
        return paramMap;
    }
    
}
