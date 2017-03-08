package com.ge.apm.service.wechat;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.api.WxConsts;


import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 转发客户消息给客服Handler
 *
 */
@Service
public class TextMsgHandler implements WxMpMessageHandler {
    @Autowired
    private  HttpServletRequest request;
    
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        String content = "<a href='#url'>title</a>";
        String url = "";
        switch (wxMessage.getContent()) {
            case "1": url = getUrl(wxMpService, "31"); content = content.replace("title", "APM帐号信息"); break;
            case "2": url = getUrl(wxMpService, "32"); content = content.replace("title", "APM重置密码");break;
            case "3": url = getUrl(wxMpService, "33"); content = content.replace("title", "APM帐号绑定");break;
            default: content = "没有找到对应的链接";
        }
        
        WxMpXmlOutTextMessage m
            = WxMpXmlOutMessage.TEXT()
            .fromUser(wxMessage.getToUser())
            .toUser(wxMessage.getFromUser())
            .build();
        m.setContent(content.replace("#url", url));
        return m;
    }
    
    private String getUrl(WxMpService wxMpService, String order) {
        String serverName = request.getRequestURL().toString().replace("/web/core", "");
        return wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/"+order, WxConsts.OAUTH2_SCOPE_USER_INFO, ""); 
    }
}
