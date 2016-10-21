package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.InspectionOrderRepository;
import com.ge.apm.domain.InspectionOrder;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionOrderController extends JpaCRUDController<InspectionOrder> {

    InspectionOrderRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InspectionOrderRepository.class);
    }

    @Override
    protected InspectionOrderRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<InspectionOrder> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<InspectionOrder> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(InspectionOrder object) {
    }
    
    @Override
    public void onAfterNewObject(InspectionOrder object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(InspectionOrder object) {
    }
    
    @Override
    public void onAfterUpdateObject(InspectionOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(InspectionOrder object) {
    }
    
    @Override
    public void onAfterDeleteObject(InspectionOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(InspectionOrder object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}