package com.ge.apm.view.wo;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderStep;
import java.util.ArrayList;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderStepController extends JpaCRUDController<WorkOrderStep> {

    WorkOrderStepRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(WorkOrderStepRepository.class);
    }

    @Override
    protected WorkOrderStepRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<WorkOrderStep> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) 
            searchFilters = new ArrayList<SearchFilter>();

        searchFilters.clear();
        
        if(workOrderFilter!=null)
            searchFilters.add(workOrderFilter);

        return dao.findBySearchFilter(this.searchFilters, pageRequest);

    }

    List<WorkOrderStep> workOrderStepList;
    public List<WorkOrderStep> getWorkOrderStepList() {
        return workOrderStepList;
    }
    
    public  void loadWorkOrderSteps(int workId) {
        workOrderStepList = dao.getByWorkOrderId(workId);
    }

    private SearchFilter workOrderFilter;
    public void setWorkOrderIdFilter(int workOrderId){
        workOrderFilter = new SearchFilter("workOrderId", SearchFilter.Operator.EQ, workOrderId);
    }
    
    public boolean getHasStepDetails(){
        return (this.selected!=null) && (this.selected.getStepDetails()!=null) && (!this.selected.getStepDetails().isEmpty());
    }
    
    public void setSelectedByWorkOrder(WorkOrder wo){
        this.selected = null;
        if(wo!=null){
            List<WorkOrderStep> woStepList = dao.getByWorkOrderIdAndStepId(wo.getId(), wo.getCurrentStep());

            if(woStepList.isEmpty()){
                selected = new WorkOrderStep();
                
                selected.setWorkOrderId(wo.getId());
                selected.setSiteId(wo.getSiteId());
                selected.setOwnerId(wo.getCurrentPersonId());
                selected.setOwnerName(wo.getCurrentPersonName());
                selected.setStartTime(TimeUtil.now());
                selected.setStepId(wo.getCurrentStep());
                selected.setStepName(WebUtil.getMessage("woSteps", wo.getCurrentStep()));
            }
            else{
                this.selected = woStepList.get(0);
            }
        }
    }

}