package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class OrgInfoCrudController extends JpaCRUDController<OrgInfo> {

    OrgInfoRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(OrgInfoRepository.class);
    }

    @Override
    protected OrgInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<OrgInfo> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(OrgInfo object) {
    }
    
    @Override
    public void onAfterNewObject(OrgInfo object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(OrgInfo object) {
    }
    
    @Override
    public void onAfterUpdateObject(OrgInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(OrgInfo object) {
    }
    
    @Override
    public void onAfterDeleteObject(OrgInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(OrgInfo object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}