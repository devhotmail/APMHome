/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web.wo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.service.wechat.CoreService;
import com.ge.apm.service.wechat.WorkOrderWeChatService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.PathVariable;
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
                
        
        return "scanWoReport";
    }
    
    @RequestMapping(value="findasset")
    public @ResponseBody Object findAsset(String assetId) {
        List<AssetInfo> list = assetDao.getByQrCode(assetId);
        if (list.isEmpty())
            return null;
        return list.get(0);
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
    
    /**
     * my work order list page, only rute to the page
     * @param request
     * @param response
     * @param model
     * @return 
     */
    @RequestMapping(value = "wolistpage")
    public String woListPage(HttpServletRequest request,HttpServletResponse response, Model model) {
        WxJsapiSignature s = null;
        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
        } catch (WxErrorException ex) {
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "myWoList";
        }
        model.addAttribute("appId",s.getAppid());
        model.addAttribute("timestamp",s.getTimestamp());
        model.addAttribute("nonceStr",s.getNoncestr());
        model.addAttribute("signature",s.getSignature());
        
        return "myWoList";
    }
    
    /**
     * my work order list page data
     * @param request
     * @return 
     */
    @RequestMapping(value = "wolistdata")
    public @ResponseBody Object woListData(HttpServletRequest request) {
        return woWcService.woList(request);
    }

    /**
     * work order detail, the data of one work order
     * @param id
     * @return 
     */
    @RequestMapping(value = "wodetail")
    public @ResponseBody Object woDetail(Integer id){
        return woWcService.woDetail(id);
    }

    /**
     * work order steps, all steps of the work order
     * @param id
     * @return 
     */
    @RequestMapping(value = "wostepdetail")
    public @ResponseBody Object woStepDetail(Integer id){
        return woWcService.woStep(id);
    }

    /**
     * every step's cost, the workorderstepdetails
     * @param id
     * @return 
     */
    @RequestMapping(value = "detailcost")
    public @ResponseBody Object detailCost(Integer id){
        return woWcService.stepDetail(id);
    }

    /**
     * finish one work order
     * @param request
     * @param map
     * @return 
     */
    @RequestMapping(value = "finishwo")
    public @ResponseBody Object finishWo(HttpServletRequest request, @RequestBody Map map){
        try {
            return woWcService.finishWo(request, map);
        } catch (Exception ex) {
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }
    
    /**
     * work order list data associate with the asset
     * @param request
     * @return 
     */
    @RequestMapping(value = "assetwolist")
    public @ResponseBody Object assetWorkOrderList(HttpServletRequest request, Integer assetId) {
        return woWcService.assetWorkOrderList(request, assetId);
    }
    
    @RequestMapping(value = "/image/{imageId}")
    public void getImage(HttpServletRequest request,HttpServletResponse response, @PathVariable Integer imageId) {
        response.setContentType("image/jpg");
        InputStream is = null;
        try {
            OutputStream out = response.getOutputStream();
            is = woWcService.getFile(imageId);
            byte[] b = new byte[is.available()];
            is.read(b);
            out.write(b);
            out.flush();
        } catch (Exception ex) {
             Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (is != null) {
                try {
                   is.close();
                } catch (IOException e) {
                    Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, e);
                }   
              }
        }
    }
    
    @RequestMapping(value = "voicerecord")
    public String voiceRecord(HttpServletRequest request,HttpServletResponse response, Model model) {
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
        
        return "voiceRecord";
    }
    @RequestMapping(value="savevoice")
    public @ResponseBody Object savevoice(@RequestBody String serverId) {
        try{
            service.saveVoice(serverId);
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "failed";
        }
        return "success";
    }
    @RequestMapping(value = "/media")
    public @ResponseBody String uploadMediaToWeChat() {
        try{
            InputStream is = woWcService.getFile(10);
            return service.uploadMediaToWechat(is);
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
}
