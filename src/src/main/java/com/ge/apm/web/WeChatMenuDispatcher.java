/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import com.ge.apm.service.wechat.CoreService;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import webapp.framework.context.ExternalLoginHandler;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212595360
 */
@Controller
public class WeChatMenuDispatcher {
    
    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected CoreService coreService;

    @Autowired
    protected ExternalLoginHandler loginHandler;
    
    static final Map<String, String> URL_MAP ;
    
    static {
        URL_MAP = new HashMap<>();
        URL_MAP.put("11", "/wechat/asset/List.xhtml");
        URL_MAP.put("12", "/wechat/asset/QRCreate.xhtml");
        URL_MAP.put("21", "/web/wocreate");
        URL_MAP.put("23", "/web/repairprocess");
        URL_MAP.put("22", "/wechat/wo/process.xhtml");
        URL_MAP.put("31", "/wechat/uaa/viewUserAccount.xhtml");
        URL_MAP.put("32", "/wechat/uaa/resetAccountPassword.xhtml");
        URL_MAP.put("33", "/web/authurl");
        URL_MAP.put("24", "/web/wolistpage");
    }

    public static Map<String, String> getUrlMap() {
        return URL_MAP;
    }
    
    
    @RequestMapping(value = "menu/{index}")
    public String wechatMenuItem(@PathVariable String index,HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        
        String code = request.getParameter("code");
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
        Boolean isBinded = false;
        try {
            //获得token
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            //获得用户基本信息
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            model.addAttribute("openId", wxMpUser.getOpenId());
            ExternalLoginHandler loginHandler = WebUtil.getServiceBean(ExternalLoginHandler.class);
            isBinded =  loginHandler.doLoginByWeChatOpenId(wxMpUser.getOpenId(), request, response);
        } catch (WxErrorException ex) {
            Logger.getLogger(WeChatCoreController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isBinded) {
                return  "redirect:" +  URL_MAP.get(index);
            }else{
                return "userInfo";
            }
    }
}
