/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.service.wechat.CoreService;
import com.ge.apm.service.wechat.WorkOrderWeChatService;
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
import org.springframework.web.bind.annotation.RequestBody;
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
    @Autowired
    private CoreService service;
    @Autowired
    private WorkOrderWeChatService woWcService;
    
    @RequestMapping(value = "wocreate")
    public String woCreate(HttpServletRequest request,HttpServletResponse response, Model model) {
        WxJsapiSignature s = null;
        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
        } catch (WxErrorException ex) {
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "woCreate";
        }
        model.addAttribute("appId",s.getAppid());
        model.addAttribute("timestamp",s.getTimestamp());
        model.addAttribute("nonceStr",s.getNoncestr());
        model.addAttribute("signature",s.getSignature());
        //初始化信息
        model.addAttribute("casePriority", 3);
        model.addAttribute("isInternal", true);
        model.addAttribute("creatorName", service.getLoginUser(request).getName());
                
        
        return "woCreate";
    }
    
    @RequestMapping(value="findasset")
    public @ResponseBody Object findAsset(String assetId) {
        return assetDao.findById(Integer.parseInt(assetId));
    }
    
    @RequestMapping(value="saveworkorder")
    public @ResponseBody Object saveWorkOrder(HttpServletRequest request, @RequestBody WorkOrder workOrder) {
        try{
            service.saveWorkOrder(workOrder, request);
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "failed";
        }
        return "success";
    }
    
    @RequestMapping(value="getmsg")
    public @ResponseBody Object getMsg(String msgType) {
        try{
            return service.getMsg(msgType);
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @RequestMapping(value="getrequestor")
    public @ResponseBody Object getRequestor(HttpServletRequest request, HttpServletResponse response) {
        try{
            return service.getUsersInHospital(request);
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @RequestMapping(value="getcurrentperson")
    public @ResponseBody Object getCurrentPerson(HttpServletRequest request, HttpServletResponse response) {
        try{
            return service.getUsersWithAssetHeadOrStaffRole(request);
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @RequestMapping(value = "wolistpage")
    public String woListPage() {
        return "myWoList";
    }
    
    @RequestMapping(value = "wolistdata")
    public @ResponseBody Object woListData(HttpServletRequest request) {
        return woWcService.woList(request);
    }
    
}