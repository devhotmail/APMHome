package com.ge.apm.view.insp;

import com.ge.apm.dao.InspectionOrderRepository;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.InspectionOrder;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionServiceController {

    InspectionOrderRepository inspectionDao = WebUtil.getBean(InspectionOrderRepository.class);
    WorkOrderRepository workOrderDao = WebUtil.getBean(WorkOrderRepository.class);
    
    UaaService uuaService = WebUtil.getBean(UaaService.class);
    
    public List<InspectionOrder> getInspectionOrderList(Integer assetId,Integer orderType,Integer limit){
        List<InspectionOrder> res = inspectionDao.getRecentlyInspectionOrder(assetId,orderType);
        return res.subList(0, Math.min(limit, res.size()));
    }
    
    public List<WorkOrder> getWorkOrderList(Integer assetId,Integer limit){
        
        List<WorkOrder> res = workOrderDao.findByAssetId(assetId);
        return res.subList(0, Math.min(limit, res.size()));
    }
    
    public List<UserAccount> getOwnerList(){
        return uuaService.getUserList(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    public List<OrgInfo> getOwnerOrgList(){
        return uuaService.getOrgListByHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
    }
}
