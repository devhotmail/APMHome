package com.ge.aps.service.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ObjectToMapUtil {

    public static final char UNDERLINE='_';  
    public static final String SERIALVERSION="serialVersionUID";
	public static Map<String,Object> toMap(Object domin){
		Map <String,Object> map=new HashMap<String,Object>();
		Class cl=domin.getClass();
		Field[] fields=cl.getDeclaredFields();
		for(Field field:fields){
			String fieldName=field.getName();
			if(SERIALVERSION.equals(fieldName))
				continue;
			
			try {
				PropertyDescriptor pd=new PropertyDescriptor(fieldName,cl);
				Method method=pd.getReadMethod();
				map.put(camelToUnderline(fieldName),field.getType().cast(method.invoke(domin)));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch(IntrospectionException e){
				e.printStackTrace();
			}catch(InvocationTargetException e){
				e.printStackTrace();
			}
		}
		return map;
	}
     
    private static String camelToUnderline(String str) {  
        if (str == null || str.trim().isEmpty()){    
            return "";    
        }  
  
        int len = str.length();    
        StringBuilder sb = new StringBuilder(len);    
        for (int i = 0; i < len; i++) {    
            char c = str.charAt(i);    
            if (Character.isUpperCase(c)){    
                sb.append(UNDERLINE);    
                sb.append(Character.toLowerCase(c));    
            }else{    
                sb.append(c);    
            }    
        }    
        return sb.toString();  
    } 
}
