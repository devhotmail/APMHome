/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web.wechat;

import com.ge.apm.domain.UserAccount;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import webapp.framework.context.ExternalLoginHandler;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

/**
 *
 * @author 212579464
 */
public class WechatUserLoginFilter extends OncePerRequestFilter {

    @Autowired
    protected WxMpService wxMpService;

    static final Set<String> URLS = new HashSet<String>();

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean(); //To change body of generated methods, choose Tools | Templates.
        wxMpService = WebUtil.getBean(WxMpService.class);
        URLS.add("/wechat/wo/scanAssetList.xhtml"); //21
        URLS.add("/web/myreport");                  //22

        URLS.add("/web/mywolist");                  //35
        URLS.add("/web/coding");                    //25,26,27,28,29
        URLS.add("/web/inspOrderList/1");
        URLS.add("/web/inspOrderList/2");

        URLS.add("/web/qrCreateAsset");             //11
        URLS.add("/wechat/asset/QRQuery.xhtml");    //12
        URLS.add("/wechat/asset/List.xhtml");       //13
        URLS.add("/web/authurl");                   //33

        URLS.add("/web/scanwodetail");     //msg entry
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (request.getContextPath().length() > 1) {
            path = path.substring(request.getContextPath().length());
        }
        String code = request.getParameter("code");
        String openId = "";
        String nickName = "";

        if (!URLS.contains(path)) {
            fc.doFilter(request, response);
            return;
        }
        
        if(path.equals("/web/qrCreateAsset")){
            if(null!=code && !code.isEmpty()){
                try {
                    openId = getOpenId(code);
                    request.setAttribute("openId", openId);
                    fc.doFilter(request, response);
                } catch (WxErrorException ex) {
                    Logger.getLogger(WechatUserLoginFilter.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }else{
                request.getRequestDispatcher("/wechat/relogin.jsp").forward(request, response);
            }
            return;
        }

        // for already logined users
        UserAccount currentUser = UserContext.getCurrentLoginUser(request);
        if (null != currentUser) {
            fc.doFilter(request, response);
            return;
        }
        
        if (null == code || code.isEmpty()) {
            request.getRequestDispatcher("/wechat/relogin.jsp").forward(request, response);
            return;
        }

        Boolean isBinded = false;

        try {
            WxMpUser wxUser = getWxMpUser(code); 
            openId = wxUser.getOpenId();
            nickName = wxUser.getNickname();
            ExternalLoginHandler loginHandler = WebUtil.getServiceBean(ExternalLoginHandler.class);
            isBinded = loginHandler.doLoginByWeChatOpenId(openId, request, response);
            //add url and openId to cookie
            try {
                Properties pro = new Properties();
                pro.load(PropertyUtils.class.getResourceAsStream("/url.properties"));
                String authenticalteUrl = pro.getProperty("authenticalteUrl").trim();
                Cookie cookie = new Cookie("authenticalteUrl", authenticalteUrl);
                cookie.setPath("/");
                response.addCookie(cookie);
                Cookie c = new Cookie("weChatId", openId);
                c.setPath("/");
                response.addCookie(c);
            } catch (Exception e) {
                Logger.getLogger(WeChatCoreController.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (WxErrorException ex) {
            Logger.getLogger(WeChatCoreController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (isBinded) {
            fc.doFilter(request, response);
        } else {
            request.setAttribute("openId", openId);
            if (path.equals("/wechat/wo/scanAssetList.xhtml")){
                request.setAttribute("nickName", nickName);
                WxJsapiSignature s = null;
                try {
                    s = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
                } catch (WxErrorException ex) {
                    Logger.getLogger(WechatUserLoginFilter.class.getName()).log(Level.SEVERE, null, ex);
                }
                request.setAttribute("appId",s.getAppid());
                request.setAttribute("timestamp",s.getTimestamp());
                request.setAttribute("nonceStr",s.getNoncestr());
                request.setAttribute("signature",s.getSignature());
                request.getRequestDispatcher("/web/unloginScan").forward(request, response);
            } else {
                request.getRequestDispatcher("/wechat/userInfo.jsp").forward(request, response);
            }
        }
    }

    private String getOpenId(String code) throws WxErrorException {
        return  getWxMpUser(code).getOpenId();
    }
    
    private WxMpUser getWxMpUser(String code) throws WxErrorException {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
        //获得token
        wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        //获得用户基本信息
        return wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
    }

}
