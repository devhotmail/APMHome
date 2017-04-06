package com.ge.apm.service.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
        	map.put(entry.getKey(), entry.getValue());
//        	if(entry.getKey().equals("first") || entry.getKey().equalsß("remark")){
//        		map.put(entry.getKey(), entry.getValue());
//        		continue;
//        	}
//        	if((!entry.getKey().startsWith("_")) && entry.getValue() != null){
//        		map.put("_"+entry.getKey(),  entry.getValue().toString());
//        	}
        }
        return map;
    }
	
	
}