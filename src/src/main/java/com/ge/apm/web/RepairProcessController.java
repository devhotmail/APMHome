package com.ge.apm.web;

import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.service.wechat.CoreService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import webapp.framework.web.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    WorkOrderRepository workOrderDao = WebUtil.getBean(WorkOrderRepository.class);

    @RequestMapping(value = "repairprocess")
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
        List<WorkOrder> workOrderList = workOrderDao.findByAssetIdAndIsClosed(assetId,false);
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

