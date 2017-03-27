package com.ge.apm.dao.mapper.provider;

import java.util.List;
import java.util.Map;

public class UserAccountProvider {
	public String getUserWechatId(Map<String,Object> param){
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		List<Integer> ids = (List<Integer>) param.get("ids");
		sb.append("select id,wechat_id from user_account where id in ( ");
		for(int i = 0;i < ids.size();i++){
			if(i == ids.size() - 1){
				sb.append(ids.get(i)).append(")");
			}else{
				sb.append(ids.get(i)).append(",");
			}
		}
		return sb.toString();
	}
}
