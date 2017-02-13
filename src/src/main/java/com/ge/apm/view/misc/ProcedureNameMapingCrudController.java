package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.ProcedureNameMapingRepository;
import com.ge.apm.domain.ProcedureNameMaping;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

@ManagedBean
@ViewScoped
public class ProcedureNameMapingCrudController extends GenericCRUDController<ProcedureNameMaping> {

    ProcedureNameMapingRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ProcedureNameMapingRepository.class);
    }

    @Override
    protected ProcedureNameMapingRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ProcedureNameMaping> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<ProcedureNameMaping> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(ProcedureNameMaping object) {
    }
    
    @Override
    public void onAfterNewObject(ProcedureNameMaping object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(ProcedureNameMaping object) {
    }
    
    @Override
    public void onAfterUpdateObject(ProcedureNameMaping object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(ProcedureNameMaping object) {
    }
    
    @Override
    public void onAfterDeleteObject(ProcedureNameMaping object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(ProcedureNameMaping object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}