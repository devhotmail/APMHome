/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import com.ge.apm.service.wechat.CoreService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
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
        System.out.println("current token is "+wxMpService.getAccessToken());
//        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type"))
//            ? "raw"
//            : request.getParameter("encrypt_type");
//
//        if ("raw".equals(encryptType)) {
//            // 明文传输的消息
//            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
//            WxMpXmlOutMessage outMessage = this.coreService.route(inMessage);
//            response.getWriter().write(outMessage.toXml());
//            return;
//        }
//
//        if ("aes".equals(encryptType)) {
//            // 是aes加密的消息
//            String msgSignature = request.getParameter("msg_signature");
//            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(
//                request.getInputStream(), this.configStorage, timestamp, nonce,
//                msgSignature);
//            WxMpXmlOutMessage outMessage = this.coreService.route(inMessage);
//            response.getWriter()
//                .write(outMessage.toEncryptedXml(this.configStorage));
//            return;
//        }

        response.getWriter().println("不可识别的加密类型");
        return;
    }
    
    /**
     * 授权页面
     * @param request
     * @param response 
     */
    @RequestMapping(value = "authurl")
    public String authUrl(HttpServletRequest request,HttpServletResponse response, Model model) throws WxErrorException{
        String code = request.getParameter("code");
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
        try {
            //获得token
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            //获得用户基本信息
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            model.addAttribute("openId", wxMpUser.getOpenId());
            
            coreService.loginByWeChatOpenId(wxMpUser.getOpenId(), request, response);
        } catch (WxErrorException ex) {
            Logger.getLogger(WeChatCoreController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "userInfo";
    }
    
    @RequestMapping(value = "binduser")
    public @ResponseBody String bindUser(String openId, String username, String password) throws WxErrorException{
        return coreService.bindingUserInfo(openId, username, password)==0?"success":"failed";
    }
    
}
