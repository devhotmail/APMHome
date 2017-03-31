/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web.wechat;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserModel;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.wechat.CoreService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import webapp.framework.context.ExternalLoginHandler;
import webapp.framework.web.service.UserContext;

/**
 *
 * @author 212595360
 */
@Controller
public class WeChatCoreController {
    
    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected CoreService coreService;
    
    @Autowired
    UserAccountService userAccountService;

    @Autowired
    protected ExternalLoginHandler loginHandler;
    /**
     * 微信公众号webservice主服务接口，提供与微信服务器的信息交互
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "core")
    public void wechatCore(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        if (!this.wxMpService.checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            response.getWriter().println("非法请求");
            return;
        }

        String echoStr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echoStr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            String echoStrOut = String.copyValueOf(echoStr.toCharArray());
            response.getWriter().println(echoStrOut);
            return;
        }
        System.out.println("current token is \n"+wxMpService.getAccessToken());
        String apiTicket = wxMpService.getJsapiTicket();
        System.out.println("current apiTicket is \n"+apiTicket);
        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type"))
            ? "raw"
            : request.getParameter("encrypt_type");

        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            WxMpXmlOutMessage outMessage = this.coreService.route(inMessage);
            response.getWriter().write(outMessage==null?"":outMessage.toXml());
            return;
        }

        if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(
                request.getInputStream(), this.configStorage, timestamp, nonce,
                msgSignature);
            WxMpXmlOutMessage outMessage = this.coreService.route(inMessage);
            response.getWriter()
                .write(outMessage==null?"":outMessage.toEncryptedXml(this.configStorage));
            return;
        }

        response.getWriter().println("不可识别的加密类型");
        return;
    }
    
    /**
     * 授权页面
     * @param request
     * @param response 
     */
    @RequestMapping(value = "authurl")
    public String authUrl(HttpServletRequest request,Model model) {
    	UserModel user = null;
    	UserAccount userAccount = UserContext.getCurrentLoginUser(request);
    	if(userAccount != null){
    		user = userAccountService.getUserAccoutByUserId(userAccount.getId());
    	}
    	if(user == null){
    		user = new UserModel();
    	}
    	model.addAttribute("user",user);
        return "userInfo";
    }
    
    @RequestMapping(value = "binduser")
    public @ResponseBody String bindUser(HttpServletRequest request,HttpServletResponse response, String openId,
    		String username, String password, String newPwd, String confirmPwd) throws WxErrorException{
        return coreService.bindingUserInfo(request, response, openId, username, password, newPwd, confirmPwd)==0?"success":"failed";
    }
    
}
