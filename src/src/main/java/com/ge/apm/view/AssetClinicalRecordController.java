package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.domain.AssetClinicalRecord;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetClinicalRecordController extends JpaCRUDController<AssetClinicalRecord> {

    AssetClinicalRecordRepository dao = null;
    private String text1;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetClinicalRecordRepository.class);
    }

    @Override
    protected AssetClinicalRecordRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetClinicalRecord> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetClinicalRecord> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    
    public String getText1() {
        return this.text1;
    }
    
    public void setText1(String text1) {
        this.text1 = text1;
    }

/*
    @Override
    public void onBeforeNewObject(AssetClinicalRecord object) {
    }
    
    @Override
    public void onAfterNewObject(AssetClinicalRecord object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(AssetClinicalRecord object) {
    }
    
    @Override
    public void onAfterUpdateObject(AssetClinicalRecord object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(AssetClinicalRecord object) {
    }
    
    @Override
    public void onAfterDeleteObject(AssetClinicalRecord object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(AssetClinicalRecord object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}