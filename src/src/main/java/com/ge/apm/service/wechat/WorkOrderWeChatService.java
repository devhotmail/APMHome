/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wechat;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.wo.WorkOrderService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    @Autowired
    protected AssetInfoRepository assetDao;
    @Autowired
    protected UserRoleRepository roleDao;
    @Autowired
    protected WorkOrderPhotoRepository photoDao;
    
    public Page<WorkOrder> woList(HttpServletRequest request, String stepId, Integer pageSize, Integer pageNum) {
        UserAccount ua = UserContext.getCurrentLoginUser(request);
        Page page = null;
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageNum, pageSize, sort);
        switch(stepId) {
            case "1": page = woDao.getByHospitalIdAndStatusAndCurrentStepIdOrderByIdDesc(ua.getHospitalId(), 1, 2, pageRequest);break; //needAssign
            case "2": page = woDao.getAssignedWorkOrder(ua.getId(), pageRequest);break;
            case "3": page = woDao.getUnAcceptedWorkOrder(ua.getId(), ua.getSiteId(), pageRequest);break;
            case "4": page = woDao.getAcceptedWorkOrder(ua.getId(), pageRequest);break;
            case "5": page = woDao.getOtherPersonWorkOrder(ua.getId(), ua.getSiteId(), pageRequest);break;
            default: page = woDao.getClosedWorkOrder(ua.getId(), pageRequest);
        }
        return page;
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
        String assetStatus = ""+map.remove("assetStatus");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date conDownTime = sdf.parse(map.remove("confirmedDownTime")+"");
        Date conUpTime = sdf.parse(map.remove("confirmedUpTime")+"");
        workOrder.setConfirmedDownTime(conDownTime);
        workOrder.setConfirmedUpTime(conUpTime);
        
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
        
        //change asset status
        AssetInfo ai = assetDao.findById(workOrder.getAssetId());
        ai.setStatus(Integer.parseInt(assetStatus));
        assetDao.save(ai);
        
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
    
    public InputStream getFile(int fileId) throws Exception {
        Object[] obj = coreService.getFile(fileId);
        if (obj != null && obj.length != 0) {
            return (InputStream)obj[1];
        } else {
            return null;
        }
    }
    
    /**
     * 扫码接单、签到、关单
     * @param info
     * @return 
     */
    public WorkOrder scanAction(AssetInfo info) {
        WorkOrder wo =  woDao.findByAssetIdAndStatus(info.getId(), 1);
        if (wo == null)
            return null;
        List<WorkOrderStep> steps = woStepDao.getByWorkOrderIdAndStepIdAndWithoutEndTime(wo.getId(), wo.getCurrentStepId());
        wo.setPointStepNumber(steps.size());
        return wo;
    }
    
    public List<WorkOrder> unclosedWorkOrder(AssetInfo info) {
        return woDao.findByAssetIdAndStatusOrderByIdDesc(info.getId(), 1);
    }
    
    /**
     * 从消息找到工单
     * @param info
     * @return 
     */
    public WorkOrder scanActionByWoId(Integer woId) {
        WorkOrder wo =  woDao.getById(woId);
        if (wo == null)
            return null;
        List<WorkOrderStep> steps = woStepDao.getByWorkOrderIdAndStepIdAndWithoutEndTime(wo.getId(), wo.getCurrentStepId());
        wo.setPointStepNumber(steps.size());
        return wo;
    }
    
    /**
     * get current login user's role names
     * @param request   get code to find user account
     * @return 
     */
    public List<String> getLoginUserRoleName(HttpServletRequest request) {
        UserAccount loginUser = coreService.getLoginUser(request);
        List<UserRole> roles = roleDao.findByUserId(loginUser.getId());
        List<String> list = new ArrayList<String>();
        for (UserRole userRole : roles) {
            list.add(userRole.getRole().getName());
        }
        return list;
    }
    
    public List<WorkOrderPhoto> getWoImgList(int woId) {
        return photoDao.findByWorkOrderId(woId);
    }
    
    public int chooseTab(HttpServletRequest request) {
        List<String> rnames = getLoginUserRoleName(request);
        if(rnames.contains("WorkOrderDispatcher")){//assinger
            return 1;
        } else if (rnames.contains("AssetStaff")){//worker
            return 2;
        } else {//no roles
            return 3;
        }
    }
    
}
