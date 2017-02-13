package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.Supplier;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

@ManagedBean
@ViewScoped
public class SupplierCrudController extends GenericCRUDController<Supplier> {

    SupplierRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SupplierRepository.class);
    }

    @Override
    protected SupplierRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<Supplier> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<Supplier> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(Supplier object) {
    }
    
    @Override
    public void onAfterNewObject(Supplier object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(Supplier object) {
    }
    
    @Override
    public void onAfterUpdateObject(Supplier object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(Supplier object) {
    }
    
    @Override
    public void onAfterDeleteObject(Supplier object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(Supplier object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}