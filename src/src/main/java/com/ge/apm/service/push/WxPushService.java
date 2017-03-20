package com.ge.apm.service.push;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

@Service
public class WxPushService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private WxMpService wxService;

	@SuppressWarnings("unchecked")
	public void buildTemplateMessage(Map<String, Object> param) {
		WxMpTemplateMessage message = new WxMpTemplateMessage(); // WxMpTemplateMessage.builder().build();
		message.setToUser((String) param.get("toUser"));
		message.setTemplateId((String) param.get("templateId"));
		message.setUrl((String) param.get("url"));
		message.setData((List<WxMpTemplateData>) param.get("data"));
		consume(message);
	}

	public void consume(WxMpTemplateMessage message) {
		try {
			String msgId = wxService.getTemplateMsgService().sendTemplateMsg(message);
			logger.info("send msg to weixin end,msgId is {}" + msgId);
		} catch (WxErrorException e) {
			logger.error("send msg to weixin error,msg is {}", e.getMessage());
		} catch (Exception e) {
			logger.error("send wx,other error is {}", e.getMessage());
		}
	}
}

