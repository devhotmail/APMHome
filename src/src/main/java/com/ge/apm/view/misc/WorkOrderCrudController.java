package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.WorkOrder;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderCrudController extends JpaCRUDController<WorkOrder> {

    WorkOrderRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(WorkOrderRepository.class);
    }

    @Override
    protected WorkOrderRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<WorkOrder> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<WorkOrder> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(WorkOrder object) {
    }
    
    @Override
    public void onAfterNewObject(WorkOrder object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(WorkOrder object) {
    }
    
    @Override
    public void onAfterUpdateObject(WorkOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(WorkOrder object) {
    }
    
    @Override
    public void onAfterDeleteObject(WorkOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(WorkOrder object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}