/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web.wo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.service.wechat.CoreService;
import com.ge.apm.service.wechat.WorkOrderWeChatService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
    
    @RequestMapping(value = "scanworeport")
    public String scanWoReport(HttpServletRequest request,HttpServletResponse response, Model model) {
        UserAccount ua = service.getLoginUser(request);
        model.addAttribute("userId", ua.getId());
        WxJsapiSignature s = null;
        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
        } catch (WxErrorException ex) {
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "wo/scanWoReport";
        }
        model.addAttribute("appId",s.getAppid());
        model.addAttribute("timestamp",s.getTimestamp());
        model.addAttribute("nonceStr",s.getNoncestr());
        model.addAttribute("signature",s.getSignature());
        
        model.addAttribute("casePriority", 3);
        model.addAttribute("qrCode", request.getParameter("qrCode"));
        
        return "wo/scanWoReport";
    }
    
    @RequestMapping(value = "myreport")
    public String myReport(HttpServletRequest request,HttpServletResponse response, Model model) {
        UserAccount ua = service.getLoginUser(request);
        model.addAttribute("userId", ua.getId());
        WxJsapiSignature s = null;
        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
        } catch (WxErrorException ex) {
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "wo/myReport";
        }
        model.addAttribute("appId",s.getAppid());
        model.addAttribute("timestamp",s.getTimestamp());
        model.addAttribute("nonceStr",s.getNoncestr());
        model.addAttribute("signature",s.getSignature());
        
        return "wo/myReport";
    }
    
    @RequestMapping(value = "scanwodetail")
    public String scanWoDetail(HttpServletRequest request,HttpServletResponse response, Model model) {
        UserAccount ua = service.getLoginUser(request);
        model.addAttribute("userId", ua.getId());
        WxJsapiSignature s = null;
        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
        } catch (WxErrorException ex) {
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "wo/scanWoDetail";
        }
        model.addAttribute("appId",s.getAppid());
        model.addAttribute("timestamp",s.getTimestamp());
        model.addAttribute("nonceStr",s.getNoncestr());
        model.addAttribute("signature",s.getSignature());
        
        model.addAttribute("woId", request.getParameter("woId"));
        model.addAttribute("from", request.getParameter("from"));
        
        return "wo/scanWoDetail";
    }
    
    /**
     * my work order list page, only rute to the page
     * @param request
     * @param response
     * @param model
     * @return 
     */
    @RequestMapping(value = "mywolist")
    public String woListPage(HttpServletRequest request,HttpServletResponse response, Model model) {
        UserAccount ua = service.getLoginUser(request);
        model.addAttribute("userId", ua.getId());
        WxJsapiSignature s = null;
        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
        } catch (WxErrorException ex) {
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return "wo/myWoList";
        }
        model.addAttribute("appId",s.getAppid());
        model.addAttribute("timestamp",s.getTimestamp());
        model.addAttribute("nonceStr",s.getNoncestr());
        model.addAttribute("signature",s.getSignature());
        
        return "wo/myWoList";
    }
    
    @RequestMapping(value="findassetinfo")
    public @ResponseBody Object findAssetInfo(HttpServletRequest request, String qrCode) {
        UserAccount ua = service.getLoginUser(request);
        List<AssetInfo> list = assetDao.getByQrCodeAndHospitalId(qrCode, ua.getHospitalId());
        if (list.isEmpty())
            return null;
        AssetInfo info = list.get(0);
        Map map = new HashMap();
        map.put("assetId", info.getId());
        map.put("assetName", info.getName());
        map.put("supplier", info.getSupplierId()==null?"":service.getSupplierName(info.getSupplierId()));
        map.put("assetGroup", service.getMsgValue("assetGroup", info.getAssetGroup().toString()));
//        map.put("assetStatus", service.getMsgValue("assetStatus", info.getStatus()+""));
        map.put("assetStatus", info.getStatus());
        
        WorkOrder wo = woWcService.scanAction(info);
        if (wo != null) {
            map.put("woId", wo.getId());
            map.put("view", true);
            map.put("requestorId", wo.getRequestorId());
            map.put("currentStepId", wo.getCurrentStepId());
        }
        return map;
    }
    
    @RequestMapping(value="scanaction")
    public @ResponseBody Object scanAction(String qrCode, String woId) {
        WorkOrder wo = null;
        if (woId != null && !"".equals(woId)) {
            wo = woWcService.scanActionByWoId(Integer.parseInt(woId));
        } else {
            List<AssetInfo> list = assetDao.getByQrCode(qrCode);
            if (list.isEmpty())
                return null;
            AssetInfo info = list.get(0);
            wo = woWcService.scanAction(info);
        }
        return wo;
    }
    
    @RequestMapping(value="choosetab")
    public @ResponseBody Object chooseTab(HttpServletRequest request) {
        return woWcService.chooseTab(request);
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
            return service.getUsersWithAssetStaffRole(request);
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * my work order list page data
     * @param request
     * @return 
     */
    @RequestMapping(value = "wolistdata")
    public @ResponseBody Object woListData(HttpServletRequest request, String stepId) {
        return woWcService.woList(request, stepId);
    }

    /**
     * work order detail, the data of one work order
     * @param id
     * @return 
     */
    @RequestMapping(value = "wodetail")
    public @ResponseBody Object woDetail(Integer id){
        WorkOrder wo = woWcService.woDetail(id);
        if (wo == null)
            return null;
        Map map = new HashMap();
        map.put("woId", wo.getId());
        map.put("requestReasonVoice", wo.getRequestReasonVoice());
        map.put("currentStepId", wo.getCurrentStepId());
        map.put("requestReason", wo.getRequestReason());
        map.put("estimatedCloseTime", wo.getEstimatedCloseTime()==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(wo.getEstimatedCloseTime()));
        map.put("feedbackRating", wo.getFeedbackRating());
        map.put("feedbackComment", wo.getFeedbackComment());
        map.put("status", wo.getStatus());
        map.put("requestor", wo.getRequestorName());
        map.put("requestTime", wo.getRequestTime()==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(wo.getRequestTime()));
        map.put("caseType", wo.getCaseType());
        map.put("caseSubType", wo.getCaseSubType());
        map.put("patProblems", wo.getPatProblems());
        map.put("patActions", wo.getPatActions());
        map.put("patTests", wo.getPatTests());
        AssetInfo ai = assetDao.getById(wo.getAssetId());
        if (ai == null)
            return map;
        map.put("assetId", ai.getId());
        map.put("assetName", ai.getName());
        map.put("supplier", ai.getSupplierId()==null?"":service.getSupplierName(ai.getSupplierId()));
        map.put("assetGroup", service.getMsgValue("assetGroup", ai.getAssetGroup().toString()));
//        map.put("assetStatus", service.getMsgValue("assetStatus", ai.getStatus()+""));
        map.put("assetStatus", ai.getStatus());
        map.put("clinicalDeptName", ai.getClinicalDeptName());
        map.put("locationName", ai.getLocationName());
        return map;
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
    public @ResponseBody String uploadMediaToWeChat(String serverId) {
        InputStream is = null;
        String ret = "";
        try{
            is = woWcService.getFile(Integer.parseInt(serverId));
            if (is != null)
                ret = service.uploadMediaToWechat(is);
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if (is != null) {
                try{
                    is.close();
                }catch(Exception ex){
                    Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return ret;
    }
    
    @RequestMapping(value = "/wo_img_list")
    public @ResponseBody Object getWoImgList(String woId) {
        try{
            return woWcService.getWoImgList(Integer.parseInt(woId));
        }catch(Exception ex){
            Logger.getLogger(WorkOrderController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @RequestMapping(value = "coding")
    public String scanWoReport() {
        return "wo/coding";
    }
    
}
