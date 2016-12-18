package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.PmOrderRepository;
import com.ge.apm.domain.PmOrder;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class PmOrderCrudController extends JpaCRUDController<PmOrder> {

    PmOrderRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(PmOrderRepository.class);
    }

    @Override
    protected PmOrderRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<PmOrder> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<PmOrder> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(PmOrder object) {
    }
    
    @Override
    public void onAfterNewObject(PmOrder object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(PmOrder object) {
    }
    
    @Override
    public void onAfterUpdateObject(PmOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(PmOrder object) {
    }
    
    @Override
    public void onAfterDeleteObject(PmOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(PmOrder object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}