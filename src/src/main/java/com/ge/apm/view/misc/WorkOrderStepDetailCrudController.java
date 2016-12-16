package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderStepDetailRepository;
import com.ge.apm.domain.WorkOrderStepDetail;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderStepDetailCrudController extends JpaCRUDController<WorkOrderStepDetail> {

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

    @Override
    public List<WorkOrderStepDetail> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(WorkOrderStepDetail object) {
    }
    
    @Override
    public void onAfterNewObject(WorkOrderStepDetail object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(WorkOrderStepDetail object) {
    }
    
    @Override
    public void onAfterUpdateObject(WorkOrderStepDetail object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(WorkOrderStepDetail object) {
    }
    
    @Override
    public void onAfterDeleteObject(WorkOrderStepDetail object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(WorkOrderStepDetail object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}