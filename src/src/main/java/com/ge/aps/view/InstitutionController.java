package com.ge.aps.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.aps.dao.InstitutionRepository;
import com.ge.aps.domain.Institution;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InstitutionController extends GenericCRUDController<Institution> {

    InstitutionRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InstitutionRepository.class);
    }

    @Override
    protected InstitutionRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<Institution> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<Institution> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(Institution object) {
    }
    
    @Override
    public void onAfterNewObject(Institution object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(Institution object) {
    }
    
    @Override
    public void onAfterUpdateObject(Institution object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(Institution object) {
    }
    
    @Override
    public void onAfterDeleteObject(Institution object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(Institution object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}