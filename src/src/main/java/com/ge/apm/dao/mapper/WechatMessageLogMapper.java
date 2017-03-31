package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ge.apm.domain.WechatMessageLog;

public interface WechatMessageLogMapper {
	@Select("select id,message_count from wechat_message_log where wechatid=#{wechatid} and wo_id=#{woId} "+
				"and wo_step_id=#{woStepId} and message_type=1")
	public WechatMessageLog fetchByProperties(WechatMessageLog wml);

	@Update("update wechat_message_log set message_count= message_count+1 where id =#{id}")
	public void updateMessageLogMapper(WechatMessageLog wml);

	@Insert("insert into wechat_message_log(wechatid,wo_id,wo_step_id,message_count,message_type) "+
			 	"values(#{wechatid},#{woId},#{woStepId},1,1) ")
	public void insertMessageLogMapper(WechatMessageLog wml);
}
