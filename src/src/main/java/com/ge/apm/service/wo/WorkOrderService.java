package com.ge.apm.service.wo;

import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderStep;
import com.ge.apm.service.uaa.UaaService;
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
        
        if(wo.getCurrentPersonId()!=null) woStep.setOwnerId(wo.getCurrentPersonId());
        if(wo.getCurrentPersonName()!=null) woStep.setOwnerName(wo.getCurrentPersonName());
        
        woStep.setStepId(wo.getCurrentStep());
        woStep.setStepName(WebUtil.getMessage("woSteps"+"-"+wo.getCurrentStep()));

        woStep.setStartTime(TimeUtil.now());
        
        return woStep;
    }
    
    @Transactional
    public void saveWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep, WorkOrderStep nextWoStep) throws RuntimeException{
        WorkOrderRepository woDao = WebUtil.getBean(WorkOrderRepository.class);
        WorkOrderStepRepository woStepDao = WebUtil.getBean(WorkOrderStepRepository.class);
        
        if(wo!=null){
            try{
                woDao.save(wo);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save WorkOrder:"+ex.getMessage());
            }
        }
        
        if(currentWoStep!=null){
            if(wo!=null){
                currentWoStep.setWorkOrderId(wo.getId());
                currentWoStep.setOwnerId(wo.getCaseOwnerId());
                currentWoStep.setOwnerName(wo.getCaseOwnerName());
            }
            
            try{
                woStepDao.save(currentWoStep);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save current WorkOrderStep:"+ex.getMessage());
            }
        }
        if(nextWoStep!=null){
            if(wo!=null){
                nextWoStep.setWorkOrderId(wo.getId());
                nextWoStep.setOwnerId(wo.getCaseOwnerId());
                nextWoStep.setOwnerName(wo.getCaseOwnerName());
            }
            
            try{
                woStepDao.save(nextWoStep);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save next WorkOrderStep:"+ex.getMessage());
            }
        }
    }

    protected void checkWorkOrderPersonNames(WorkOrder wo){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        UserAccount user;
        
        user = uaaService.getUserById(wo.getRequestorId());
        if(user!=null)
            wo.setRequestorName(user.getName());
        else 
            wo.setRequestorName("N/A");
        
        if(wo.getCaseOwnerId().equals(wo.getCurrentPersonId())){
            wo.setCurrentPersonName(wo.getCaseOwnerName());
        }
        else{
            user = uaaService.getUserById(wo.getCurrentPersonId());
            if(user!=null)
                wo.setCurrentPersonName(user.getName());
            else
                wo.setCurrentPersonName("N/A");
        }
    }
    
    public void createWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        finishWorkOrderStep(wo, currentWoStep);
    }

    public void finishWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        // update current Work Order Step
        currentWoStep.setEndTime(TimeUtil.now());
        
        // initiate next Work Order Step
        wo.setCurrentStep(currentWoStep.getStepId()+1);
        checkWorkOrderPersonNames(wo);
        
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

    public void closeWorkOrder(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
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
    public void transferWorkOrder(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        checkWorkOrderPersonNames(wo);
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
