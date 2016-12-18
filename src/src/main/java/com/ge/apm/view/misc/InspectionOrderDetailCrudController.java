package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.InspectionOrderDetailRepository;
import com.ge.apm.domain.InspectionOrderDetail;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionOrderDetailCrudController extends JpaCRUDController<InspectionOrderDetail> {

    InspectionOrderDetailRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InspectionOrderDetailRepository.class);
    }

    @Override
    protected InspectionOrderDetailRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<InspectionOrderDetail> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<InspectionOrderDetail> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(InspectionOrderDetail object) {
    }
    
    @Override
    public void onAfterNewObject(InspectionOrderDetail object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(InspectionOrderDetail object) {
    }
    
    @Override
    public void onAfterUpdateObject(InspectionOrderDetail object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(InspectionOrderDetail object) {
    }
    
    @Override
    public void onAfterDeleteObject(InspectionOrderDetail object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(InspectionOrderDetail object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}