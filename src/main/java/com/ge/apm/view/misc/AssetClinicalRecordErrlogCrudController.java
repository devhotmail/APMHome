package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.apm.dao.AssetClinicalRecordErrlogRepository;
import com.ge.apm.domain.AssetClinicalRecordErrlog;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetClinicalRecordErrlogCrudController extends GenericCRUDController<AssetClinicalRecordErrlog> {

    AssetClinicalRecordErrlogRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetClinicalRecordErrlogRepository.class);
    }

    @Override
    protected AssetClinicalRecordErrlogRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetClinicalRecordErrlog> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetClinicalRecordErrlog> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(AssetClinicalRecordErrlog object) {
    }
    
    @Override
    public void onAfterNewObject(AssetClinicalRecordErrlog object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(AssetClinicalRecordErrlog object) {
    }
    
    @Override
    public void onAfterUpdateObject(AssetClinicalRecordErrlog object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(AssetClinicalRecordErrlog object) {
    }
    
    @Override
    public void onAfterDeleteObject(AssetClinicalRecordErrlog object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(AssetClinicalRecordErrlog object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}