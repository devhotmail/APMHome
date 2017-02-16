package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.apm.dao.FieldCodeTypeRepository;
import com.ge.apm.domain.FieldCodeType;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class FieldCodeTypeCrudController extends GenericCRUDController<FieldCodeType> {

    FieldCodeTypeRepository dao = null;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(FieldCodeTypeRepository.class);
    }

    @Override
    protected FieldCodeTypeRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<FieldCodeType> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<FieldCodeType> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(FieldCodeType object) {
    }
    
    @Override
    public void onAfterNewObject(FieldCodeType object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(FieldCodeType object) {
    }
    
    @Override
    public void onAfterUpdateObject(FieldCodeType object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(FieldCodeType object) {
    }
    
    @Override
    public void onAfterDeleteObject(FieldCodeType object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(FieldCodeType object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}