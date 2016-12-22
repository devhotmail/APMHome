package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.domain.SiteInfo;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

@ManagedBean
@ViewScoped
public class SiteInfoCrudController extends GenericCRUDController<SiteInfo> {

    SiteInfoRepository dao = null;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(SiteInfoRepository.class);
    }

    @Override
    protected SiteInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<SiteInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<SiteInfo> getItemList() {
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