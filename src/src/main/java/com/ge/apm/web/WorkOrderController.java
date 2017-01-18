/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author 212595360
 */
@Controller
public class WorkOrderController {
    
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    private AssetInfoRepository assetDao;
    
    @RequestMapping(value = "wocreate")
    public String woCreate(HttpServletRequest request,HttpServletResponse response, Model model) {
        //先判断是否有绑定
        
        WxJsapiSignature s = null;
        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString());
        } catch (WxErrorException ex) {
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "woCreate";
        }
        model.addAttribute("appId",s.getAppid());
        model.addAttribute("timestamp",s.getTimestamp());
        model.addAttribute("nonceStr",s.getNoncestr());
        model.addAttribute("signature",s.getSignature());
        return "woCreate";
    }
    
    @RequestMapping(value="findasset")
    public @ResponseBody Object findAsset(String assetId) {
        return assetDao.findById(Integer.parseInt(assetId));
    }
    
}
