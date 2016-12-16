package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.InspectionChecklistRepository;
import com.ge.apm.domain.InspectionChecklist;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionChecklistCrudController extends JpaCRUDController<InspectionChecklist> {

    InspectionChecklistRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InspectionChecklistRepository.class);
    }

    @Override
    protected InspectionChecklistRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<InspectionChecklist> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<InspectionChecklist> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(InspectionChecklist object) {
    }
    
    @Override
    public void onAfterNewObject(InspectionChecklist object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(InspectionChecklist object) {
    }
    
    @Override
    public void onAfterUpdateObject(InspectionChecklist object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(InspectionChecklist object) {
    }
    
    @Override
    public void onAfterDeleteObject(InspectionChecklist object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(InspectionChecklist object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}