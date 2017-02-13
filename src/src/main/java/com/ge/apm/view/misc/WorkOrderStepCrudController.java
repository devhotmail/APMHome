package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.WorkOrderStep;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

@ManagedBean
@ViewScoped
public class WorkOrderStepCrudController extends GenericCRUDController<WorkOrderStep> {

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
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<WorkOrderStep> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(WorkOrderStep object) {
    }
    
    @Override
    public void onAfterNewObject(WorkOrderStep object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(WorkOrderStep object) {
    }
    
    @Override
    public void onAfterUpdateObject(WorkOrderStep object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(WorkOrderStep object) {
    }
    
    @Override
    public void onAfterDeleteObject(WorkOrderStep object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(WorkOrderStep object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}