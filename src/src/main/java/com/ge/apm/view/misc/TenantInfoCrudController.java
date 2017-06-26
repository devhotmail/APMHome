package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.domain.TenantInfo;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.apm.dao.TenantInfoRepository;

@ManagedBean
@ViewScoped
public class TenantInfoCrudController extends GenericCRUDController<TenantInfo> {

    TenantInfoRepository dao = null;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(TenantInfoRepository.class);
    }

    @Override
    protected TenantInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<TenantInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<TenantInfo> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(SiteInfo object) {
    }
    
    @Override
    public void onAfterNewObject(SiteInfo object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(SiteInfo object) {
    }
    
    @Override
    public void onAfterUpdateObject(SiteInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(SiteInfo object) {
    }
    
    @Override
    public void onAfterDeleteObject(SiteInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(SiteInfo object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}