package com.ge.apm.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderStepDetailRepository;
import com.ge.apm.domain.WorkOrderStep;
import com.ge.apm.domain.WorkOrderStepDetail;
import java.util.ArrayList;
import org.primefaces.context.RequestContext;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderStepDetailController extends JpaCRUDController<WorkOrderStepDetail> {

    WorkOrderStepDetailRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(WorkOrderStepDetailRepository.class);
    }

    @Override
    protected WorkOrderStepDetailRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<WorkOrderStepDetail> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public void prepareCreate(WorkOrderStep woStep) throws InstantiationException, IllegalAccessException{
        this.prepareCreate();
        
        this.selected.setSiteId(woStep.getSiteId());
        this.selected.setWorkOrderStepId(woStep.getId());
    }
    
    public void prepareEdit(WorkOrderStepDetail woStepDetail){
        this.selected = woStepDetail;
        this.prepareEdit();
    }

    public void delete(WorkOrderStep woStep, WorkOrderStepDetail woStepDetail){
        woStep.getStepDetails().remove(woStepDetail);
    }
    
    public void validateInput(WorkOrderStep woStep){
        if(selected!=null){
            if((selected.getAccessory()==null || "".equals(selected.getAccessory())) &&
               selected.getAccessoryPrice()==null &&
               selected.getAccessoryQuantity()==null &&
               selected.getManHours()==null && selected.getOtherExpense()==null){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessageKey("ValidationRequire");
            }
            else{
                if(woStep.getStepDetails()==null){
                    woStep.setStepDetails(new ArrayList<WorkOrderStepDetail>());
                }
                woStep.getStepDetails().add(this.selected);
            }
        }
    }

    public void cancelInput(WorkOrderStep woStep){
        woStep.getStepDetails().remove(selected);
  }
    
}