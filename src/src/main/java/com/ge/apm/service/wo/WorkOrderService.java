package com.ge.apm.service.wo;

import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderStep;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212547631
 */
@Component
public class WorkOrderService {
    private static final Logger logger = Logger.getLogger(WorkOrderService.class);
    
    public WorkOrderStep initWorkOrderCurrentStep(WorkOrder wo){
        WorkOrderStep woStep = new WorkOrderStep();

        woStep.setWorkOrderId(wo.getId());
        woStep.setSiteId(wo.getSiteId());
        woStep.setOwnerId(wo.getCurrentPersonId());
        woStep.setOwnerName(wo.getCurrentPersonName());
        woStep.setStartTime(TimeUtil.now());
        woStep.setStepId(wo.getCurrentStep());
        woStep.setStepName(WebUtil.getMessage("woSteps"+"-"+wo.getCurrentStep()));
        
        return woStep;
    }
    
    @Transactional
    public void saveWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep, WorkOrderStep nextWoStep){
        WorkOrderRepository woDao = WebUtil.getBean(WorkOrderRepository.class);
        WorkOrderStepRepository woStepDao = WebUtil.getBean(WorkOrderStepRepository.class);
        
        if(wo!=null)  woDao.save(wo);
        if(currentWoStep!=null)  woStepDao.save(currentWoStep);
        if(nextWoStep!=null)  woStepDao.save(nextWoStep);
    }
    
    public void finishWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep){
        // update current Work Order Step
        currentWoStep.setEndTime(TimeUtil.now());
        
        // initiate next Work Order Step
        wo.setCurrentStep(currentWoStep.getStepId()+1);
        wo.setCurrentPersonId(wo.getCaseOwnerId());
        wo.setCurrentPersonName(wo.getCaseOwnerName());
        
        WorkOrderStep nextWoStep = initWorkOrderCurrentStep(wo);
        
        try{
            saveWorkOrderStep(wo, currentWoStep, nextWoStep);
        }
        catch(Exception ex){
            // rollback WorkOrder changes
            wo.setCurrentStep(currentWoStep.getStepId());
            wo.setCurrentPersonId(currentWoStep.getOwnerId());
            wo.setCurrentPersonName(currentWoStep.getOwnerName());
            
            throw ex;
        }
    }

    public void closeWorkOrder(WorkOrder wo, WorkOrderStep currentWoStep){
        wo.setCurrentStep(6);
        wo.setIsClosed(true);
        currentWoStep.setEndTime(TimeUtil.now());
                
        WorkOrderRepository woDao = WebUtil.getBean(WorkOrderRepository.class);
        WorkOrderStepRepository woStepDao = WebUtil.getBean(WorkOrderStepRepository.class);
        
        try{
            saveWorkOrderStep(wo, currentWoStep, null);
        }
        catch(Exception ex){
            // rollback WorkOrder changes
            wo.setIsClosed(false);
            
            throw ex;
        }        
    }

    @Transactional
    public void transferWorkOrder(WorkOrder wo, WorkOrderStep currentWoStep){
        WorkOrderStep nextWoStep = initWorkOrderCurrentStep(wo);
        
        try{
            saveWorkOrderStep(wo, currentWoStep, nextWoStep);
        }
        catch(Exception ex){
            // rollback WorkOrder changes
            wo.setIsClosed(false);
            
            throw ex;
        }        
    }
    
}
