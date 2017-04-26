package com.ge.apm.web.wo;

import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderMsg;
import com.ge.apm.service.wo.WorkOrderMsgService;
import com.ge.apm.service.wo.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by lsg on 17/1/2017.
 */
@Controller
public class WorkOrderProcessController {

    @Autowired
    WorkOrderMsgService workOrderMsgService;
@Autowired
    WorkOrderService workOrderService;
    @RequestMapping(value="/workorder/{status}" ,method = RequestMethod.GET)
    @ResponseBody
    public List<WorkOrder> WorkOrderByStatus( @PathVariable  int status)throws Exception{
        List<WorkOrder> byStatus = workOrderService.findWorkOrderByStatus(status);
        return byStatus;

    }

    @RequestMapping(value="/msg/{workOrderId}" ,method = RequestMethod.GET)
    @ResponseBody
    public List<WorkOrderMsg> msgByWorkOrderId(@PathVariable  Integer workOrderId)throws Exception {
        List<WorkOrderMsg> workOrderMsgs = workOrderMsgService.msgByWorkOrderId(workOrderId);
        return workOrderMsgs;

    }

    //findWorkOrderByCon
    @RequestMapping(value="/workorder" ,method = RequestMethod.GET)
    @ResponseBody
    public Object workOrderByCon(HttpServletRequest request, Integer pageSize, Integer pageNum)throws Exception {
        return   workOrderService.findWorkOrderByCon(request, pageSize, pageNum);
    }
    /*gl ok*/
    @RequestMapping(value="/workorder2/{requestorId}" ,method = RequestMethod.GET)
    @ResponseBody
    public List<WorkOrder> workOrderByCon2(@PathVariable  Integer requestorId)throws Exception {
        return   workOrderService.findWorkOrderByContest(requestorId);
    }
    
    @RequestMapping(value="/scanreportlist" ,method = RequestMethod.GET)
    @ResponseBody
    public List<WorkOrder> scanReportList(String assetId)throws Exception {
        return   workOrderService.findWorkOrderByAssetIdAndStatus(Integer.parseInt(assetId));
    }

}

