/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wechat;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.primefaces.json.JSONObject;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WechatUtilService {

    static String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

    public String getJsSignature() {
        WxJsapiSignature jsSignature;
        WxMpService wxMpService = WebUtil.getBean(WxMpService.class);
        Map<String,String> signatureMap = new HashMap<>();
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            jsSignature = wxMpService.createJsapiSignature(request.getRequestURL().toString() + "?" + request.getQueryString());
            signatureMap.put("appid", jsSignature.getAppid());
            signatureMap.put("noncestr", jsSignature.getNoncestr());
            signatureMap.put("signature", jsSignature.getSignature());
            signatureMap.put("timestamp", String.valueOf(jsSignature.getTimestamp()));
        } catch (WxErrorException ex) {
            Logger.getLogger(WechatUtilService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String res= JSONObject.valueToString(signatureMap);
        return res;
    }

    public String getContextPath() {
        return contextPath;
    }

}
