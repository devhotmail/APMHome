package com.ge.apm.view.wo;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.WorkOrderStep;
import java.util.ArrayList;
import webapp.framework.dao.SearchFilter;
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
}