package com.ge.apm.service.wechat;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.web.WeChatCoreController;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.context.ExternalLoginHandler;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

/**
 * Created by FirenzesEagle on 2016/5/30 0030.
 * Email:liumingbo2008@gmail.com
 */
@Service
public class CoreService {

    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected UserAccountRepository userDao;

    public void requestGet(String urlWithParams) throws IOException {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(urlWithParams);
        httpget.addHeader("Content-Type", "text/html;charset=UTF-8");
        //配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(50)
            .setConnectTimeout(50)
            .setSocketTimeout(50).build();
        httpget.setConfig(requestConfig);

        CloseableHttpResponse response = httpclient.execute(httpget);
        System.out.println("StatusCode -> " + response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(entity);
        System.out.println(jsonStr);

        httpget.releaseConnection();
    }

    public void requestPost(String url, List<NameValuePair> params) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        CloseableHttpResponse response = httpclient.execute(httppost);
        System.out.println(response.toString());

        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(entity, "utf-8");
        System.out.println(jsonStr);

        httppost.releaseConnection();
    }

    public WxMpUser getUserInfo(String openid, String lang) {
        WxMpUser wxMpUser = null;
        try {
            wxMpUser = this.wxMpService.getUserService().userInfo(openid, lang);
        } catch (WxErrorException ex) {
            Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wxMpUser;
    }
    
    @Transactional
    public int bindingUserInfo(String openId, String username, String password) {
        WxMpUser user = getUserInfo(openId, null);
        UserAccount ua = userValidate(username, password);
        if (ua == null || user == null){
            return 1;//绑定失败
        } else {
            ua.setWeChatId(openId);
            userDao.save(ua);
            return 0;//绑定成功
        }
    }
    
    private UserAccount userValidate(String username, String password) {
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        UserAccount user = userDao.getByLoginName(username);
        if(user!=null) {
            try {
                byte[] salt = Digests.decodeHex(user.getPwdSalt());
                byte[] hashPassword;
                
                hashPassword = Digests.sha1(password.getBytes(), salt, UserAccount.HASH_INTERATIONS);
                String saltedPassword = Digests.encodeHex(hashPassword);
                
                if (user.getPassword().equals(saltedPassword)) {
                    return user;
                }
            } catch (Exception ex) {
                Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public boolean loginByWeChatOpenId(HttpServletRequest request,HttpServletResponse response){
        if(UserContext.isLoggedIn()) return true; // user already logged in.

        String code = request.getParameter("code");
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
        try {
            //获得token
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            //获得用户基本信息
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            String openId = wxMpUser.getOpenId();
            
            ExternalLoginHandler loginHandler = WebUtil.getServiceBean(ExternalLoginHandler.class);
            return loginHandler.doLoginByWeChatOpenId(openId, request, response);
        } catch (WxErrorException ex) {
            Logger.getLogger(WeChatCoreController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public boolean loginByWeChatOpenId(String weChatOpenId, HttpServletRequest request,HttpServletResponse response){
        ExternalLoginHandler loginHandler = WebUtil.getServiceBean(ExternalLoginHandler.class);
        return loginHandler.doLoginByWeChatOpenId(weChatOpenId, request, response);
    }
    
}
