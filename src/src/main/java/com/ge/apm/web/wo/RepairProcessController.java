package com.ge.apm.web.wo;

import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderPoJo;
import com.ge.apm.service.wechat.CoreService;
import com.ge.apm.service.wo.WorkOrderService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webapp.framework.web.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by lsg on 17/1/2017.
 */
@Controller
public class RepairProcessController {
    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected CoreService coreService;
    @Autowired
    protected WorkOrderService workOrderService;
    WorkOrderRepository workOrderDao = WebUtil.getBean(WorkOrderRepository.class);
    /*开单 */
    @RequestMapping(value = "/workorderCreate",method = RequestMethod.POST)
    @ResponseBody
    public String createWorkOrder(HttpServletRequest request, @RequestBody WorkOrderPoJo wopo) throws Exception
    {
        try{
        workOrderService.workWorderCreate(request, wopo);
            return "success";
        } catch(Exception e){
            Logger.getLogger(RepairProcessController.class.getName()).log(Level.SEVERE, null, e);
            return "error";
        }
    }

    /*派单*/
    @RequestMapping(value = "/workorderAssign")
    @ResponseBody
    public void assignWorkOrder(HttpServletRequest request, @RequestBody WorkOrderPoJo wopo ) throws Exception
    {
        workOrderService.assignWorkOrder(request, wopo);

    }

    @RequestMapping(value = "/workorderSign")
    @ResponseBody
    public void signWorkOrder(HttpServletRequest request, @RequestBody WorkOrderPoJo wopo ) throws Exception
    {
        workOrderService.signWorkOrder(request, wopo);

    }
    @RequestMapping(value = "/workorderRepair")
    @ResponseBody
    public void repair(HttpServletRequest request, @RequestBody WorkOrderPoJo wopo ) throws Exception
    {
        workOrderService.takeWorkOrder(request, wopo);

    }

    @RequestMapping(value = "/workorderClose")
    @ResponseBody
    public void takeWorkOrder(HttpServletRequest request, @RequestBody WorkOrderPoJo wopo ) throws Exception
    {
        workOrderService.closeWorkOrder(request, wopo);

    }



    @RequestMapping(value = "/repairprocess")
    public String repairProcess(HttpServletResponse response,
                                HttpServletRequest httpServletRequest,
                                Model model) {
        try {
            WxJsapiSignature sig = this.wxMpService.createJsapiSignature(
                    httpServletRequest.getRequestURL() + "?" +
                            httpServletRequest.getQueryString()
            );
           /* WxJsapiSignature sig = this.wxMpService.createJsapiSignature(
                    httpServletRequest.getRequestURL()+""
            );*/

           /* System.out.println("appId--"+sig.getAppid());
            System.out.println("timestamp-->"+sig.getTimestamp());
            System.out.println("nonceStr-->"+sig.getNoncestr());
            System.out.println("signature-->" +sig.getSignature());*/

            model.addAttribute("appId", sig.getAppid());
            model.addAttribute("timestamp", sig.getTimestamp());
            model.addAttribute("nonceStr", sig.getNoncestr());
            model.addAttribute("signature", sig.getSignature());
            return "processView";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "fail";
        }
    }
    @RequestMapping(value = "findRepairList")
    @ResponseBody
    public List<WorkOrder> findRepairList(HttpServletResponse response,
                                          HttpServletRequest httpServletRequest,Integer assetId, Model model) {
        System.out.println("findRepairList--->");
        List<WorkOrder> workOrderList = workOrderDao.findByAssetIdAndIntExtType(assetId, 1);
        model.addAttribute("data",workOrderList);
        return workOrderList;

    }
    @RequestMapping(value="findRepairDetail")
    @ResponseBody
    public Object[] findRepairDetail(Integer id) {
        System.out.println("findRepairDetail---->");
        Object[] obj=new Object[3];
        WorkOrder workOrder= workOrderDao.findById(id);
        String caseType= FieldValueMessageController.fieldValue("caseType",String.valueOf(workOrder.getCaseType()));
        String casePriority = FieldValueMessageController.fieldValue("casePriority",String.valueOf(workOrder.getCasePriority()) );
        obj[0]=workOrder;
        obj[1]=caseType;
        obj[2]=casePriority;
        return obj;
    }

}

