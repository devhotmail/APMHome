/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web.insp;

import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 212595360
 */
@Controller
public class InspOrderController {
    
    @Autowired
    protected WxMpService wxMpService;
    
    @RequestMapping(value = "inspOrderDetail")
    public String scanWoReport(HttpServletRequest request,HttpServletResponse response, Model model) {
        WxJsapiSignature s = null;
        model.addAttribute("inspId", request.getParameter("inspId"));
        model.addAttribute("orderType", request.getParameter("orderType"));
        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
        } catch (WxErrorException ex) {
            Logger.getLogger(InspOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "insp/inspOrderDetail";
        }
        if (s != null) {
            model.addAttribute("appId",s.getAppid());
            model.addAttribute("timestamp",s.getTimestamp());
            model.addAttribute("nonceStr",s.getNoncestr());
            model.addAttribute("signature",s.getSignature());
        }
        
        return "insp/inspOrderDetail";
    }
    
}
