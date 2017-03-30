package com.ge.apm.service.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.Map.Entry;

public class WeiXinUtils {
	public static List<Integer> removeDuplicateId(List<Integer> users) {
		Set<Integer> set = new HashSet<Integer>(users);
		return new ArrayList<Integer>(set);
	}
	
	public static Map<String, Object> object2Map(Object object){
        JSONObject jsonObject = (JSONObject) JSON.toJSON(object);
        Set<Entry<String,Object>> entrySet = jsonObject.entrySet();
        Map<String, Object> map=new HashMap<String,Object>();
        for (Entry<String, Object> entry : entrySet) {
        	if((!entry.getKey().startsWith("_")) && entry.getValue() != null){
        		map.put("_"+entry.getKey(),  entry.getValue().toString());
        	}
        }
        System.out.println(map);
        return map;
    }
}