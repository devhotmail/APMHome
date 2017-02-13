package com.ge.apm.view.wo;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderHistoryRepository;
import com.ge.apm.domain.WorkOrderHistory;
import java.util.ArrayList;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderHistoryController extends JpaCRUDController<WorkOrderHistory> {

    WorkOrderHistoryRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(WorkOrderHistoryRepository.class);
    }

    @Override
    protected WorkOrderHistoryRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<WorkOrderHistory> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        
        if(workOrderFilter!=null)
            searchFilters.add(workOrderFilter);
        
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    private List<WorkOrderHistory> workOrderHistoryList;

    public List<WorkOrderHistory> getWorkOrderHistoryList() {
        return workOrderHistoryList;
    }
    
    public void loadWorkOrderHistory(int workOrderId) {
        workOrderHistoryList = dao.getByWorkOrderId(workOrderId);
    }

    private SearchFilter workOrderFilter;
    public void setWorkOrderIdFilter(int workOrderId){
        workOrderFilter = new SearchFilter("workOrderId", SearchFilter.Operator.EQ, workOrderId);
    }
}