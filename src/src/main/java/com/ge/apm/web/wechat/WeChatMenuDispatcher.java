/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web.wechat;

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
        URL_MAP.put("11", "/web/qrCreateAsset");
        URL_MAP.put("12", "/wechat/asset/QRQuery.xhtml");
        URL_MAP.put("13", "/wechat/asset/List.xhtml");
        
        URL_MAP.put("21", "/wechat/wo/scanAssetList.xhtml");
        URL_MAP.put("22", "/web/myreport");
//        URL_MAP.put("23", "/web/repairprocess");
        
        URL_MAP.put("31", "/wechat/uaa/viewUserAccount.xhtml");
        URL_MAP.put("32", "/wechat/uaa/resetAccountPassword.xhtml");
        URL_MAP.put("33", "/web/authurl");
        
        URL_MAP.put("34", "/web/scanwodetail");
        URL_MAP.put("35", "/web/mywolist");
//        URL_MAP.put("333", "/web/voicerecord");
    }

    public static Map<String, String> getUrlMap() {
        return URL_MAP;
    }
    
    
    @RequestMapping(value = "menu/{index}")
    public String wechatMenuItem(@PathVariable String index,HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {

        String openId = "";
        String code = request.getParameter("code");
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
        Boolean isBinded = false;
        try {
            //获得token
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            //获得用户基本信息
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            openId = wxMpUser.getOpenId();
            model.addAttribute("openId", openId);
            ExternalLoginHandler loginHandler = WebUtil.getServiceBean(ExternalLoginHandler.class);
            isBinded =  loginHandler.doLoginByWeChatOpenId(openId, request, response);
        } catch (WxErrorException ex) {
            Logger.getLogger(WeChatCoreController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(index.equals("11")){
            return  "redirect:" +  URL_MAP.get(index) + "/" + openId;
        }

        if (isBinded) {
                return  "redirect:" +  URL_MAP.get(index)+"?"+request.getQueryString();
            }else{
                return "userInfo";
            }
    }
}
