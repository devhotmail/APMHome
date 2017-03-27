package com.ge.apm.view.asset;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.service.asset.AssetCreateService;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class QrCodeLibController extends JpaCRUDController<QrCodeLib> {

    QrCodeLibRepository dao = null;
    
    private AssetCreateService acService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(QrCodeLibRepository.class);
        acService=WebUtil.getBean(AssetCreateService.class);
        
        this.filterBySite = false;
    }

    @Override
    protected QrCodeLibRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<QrCodeLib> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<QrCodeLib> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    
    public String getHospitalName(Integer hospitalId){
        return acService.getHospitalName(hospitalId);
    }
    
    public String getSiteName(Integer siteId){
        return acService.getSiteName(siteId);
    }

/*
    @Override
    public void onBeforeNewObject(QrCodeLib object) {
    }
    
    @Override
    public void onAfterNewObject(QrCodeLib object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(QrCodeLib object) {
    }
    
    @Override
    public void onAfterUpdateObject(QrCodeLib object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(QrCodeLib object) {
    }
    
    @Override
    public void onAfterDeleteObject(QrCodeLib object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(QrCodeLib object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}