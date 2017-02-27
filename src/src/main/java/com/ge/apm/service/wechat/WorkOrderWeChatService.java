/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wechat;

import com.ge.apm.dao.FileUploadDao;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.dao.WorkOrderStepDetailRepository;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.ge.apm.domain.WorkOrderStep;
import com.ge.apm.domain.WorkOrderStepDetail;
import com.ge.apm.service.wo.WorkOrderService;
import java.io.File;
import java.util.Map;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.service.UserContext;

/**
 *
 * @author 212595360
 */
@Service
public class WorkOrderWeChatService {
    
    @Autowired
    protected WorkOrderRepository woDao;
    @Autowired
    protected WorkOrderStepRepository woStepDao;
    @Autowired
    protected WorkOrderStepDetailRepository detailDao;
    @Autowired
    protected WorkOrderService woService;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected CoreService coreService;
    
    public List<WorkOrder> woList(HttpServletRequest request) {
        List<SearchFilter> searchFilters = new ArrayList<>();
        UserAccount ua = UserContext.getCurrentLoginUser(request);
        searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, ua.getId()));
        searchFilters.add(new SearchFilter("isClosed", SearchFilter.Operator.EQ, false));
        return woDao.findBySearchFilter(searchFilters);
    }

    public WorkOrder woDetail(Integer id) {
        return woDao.findById(id);
    }

    public List<WorkOrderStep> woStep(Integer id) {
        return woStepDao.getByWorkOrderIdOrderByIdAsc(id);
    }

    public List<WorkOrderStepDetail> stepDetail(int id) {
        return detailDao.getByWorkOrderStepId(id);
    }
    
    @Transactional
    public String finishWo(HttpServletRequest request, Map map) throws Exception {
        WorkOrder workOrder = woDetail(Integer.parseInt((String)map.get("id")));
        String type = (String)map.remove("type");
        String serverId = (String)map.remove("closeReason");
        String comments = (String)map.remove("comments");
        Object stepDetails = map.remove("stepDetails");
        
        BeanUtils.populate(workOrder, map);
        List<WorkOrderStep> currentWoSteps = woStepDao.getByWorkOrderIdAndStepId(workOrder.getId(), workOrder.getCurrentStepId());
        if (currentWoSteps == null || currentWoSteps.isEmpty()) return "error";
        WorkOrderStep currentWoStep = currentWoSteps.get(0);
        currentWoStep.setDescription(comments);
        
        JSONArray array = JSONArray.fromObject(stepDetails);
        List<WorkOrderStepDetail> list = JSONArray.toList(array, new WorkOrderStepDetail(), new JsonConfig());
        for (WorkOrderStepDetail sd : list) {
            sd.setWorkOrderStepId(currentWoStep.getId());
            currentWoStep.addStepDetail(sd);
        }
        
        if (null != type) switch (type) {
            case "save":
                woService.finishWorkOrderStep(workOrder, currentWoStep);
                break;
            case "transfer":
                woService.transferWorkOrder(workOrder, currentWoStep);
                break;
            default:
                woService.closeWorkOrder(workOrder, currentWoStep);
                break;
        }
        
        //保存完成后，再把上传的图片保存
        if (serverId != null && !"".equals(serverId))
            upload(currentWoStep, serverId);
        return "success";
    }
    
    @Transactional
    public void upload(WorkOrderStep currentWoStep, String serverId) throws WxErrorException {
        File file = wxMpService.getMaterialService().mediaDownload(serverId);
        if (file == null)
            return;
        Integer uploadFileId = coreService.uploadFile(file);
        String fileName = coreService.getFileName(file);
        //如果有文件则删除以前的文件
        if (currentWoStep.getFileId() != null) {
            FileUploadDao fileUploaddao = new FileUploadDao();
            fileUploaddao.deleteUploadFile(currentWoStep.getFileId());
        }
        currentWoStep.setAttachmentUrl(fileName);
        if (uploadFileId > 0) {
            currentWoStep.setFileId(uploadFileId);
        }
        woStepDao.save(currentWoStep);
        file.delete();
    }
    
    public List<WorkOrder> assetWorkOrderList(HttpServletRequest request, Integer assetId) {
        List<SearchFilter> searchFilters = new ArrayList<>();
        UserAccount ua = UserContext.getCurrentLoginUser(request);
        searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, ua.getSiteId()));
        searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetId));
        searchFilters.add(new SearchFilter("isClosed", SearchFilter.Operator.EQ, true));
        return woDao.findBySearchFilter(searchFilters);
    }
    
}
