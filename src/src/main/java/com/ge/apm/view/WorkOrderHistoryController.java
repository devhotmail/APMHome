package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderHistoryRepository;
import com.ge.apm.domain.WorkOrderHistory;
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
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<WorkOrderHistory> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(WorkOrderHistory object) {
    }
    
    @Override
    public void onAfterNewObject(WorkOrderHistory object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(WorkOrderHistory object) {
    }
    
    @Override
    public void onAfterUpdateObject(WorkOrderHistory object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(WorkOrderHistory object) {
    }
    
    @Override
    public void onAfterDeleteObject(WorkOrderHistory object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(WorkOrderHistory object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}