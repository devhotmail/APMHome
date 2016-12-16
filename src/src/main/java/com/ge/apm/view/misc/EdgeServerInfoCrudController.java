package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.EdgeServerInfoRepository;
import com.ge.apm.domain.EdgeServerInfo;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class EdgeServerInfoCrudController extends JpaCRUDController<EdgeServerInfo> {

    EdgeServerInfoRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(EdgeServerInfoRepository.class);
    }

    @Override
    protected EdgeServerInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<EdgeServerInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<EdgeServerInfo> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(EdgeServerInfo object) {
    }
    
    @Override
    public void onAfterNewObject(EdgeServerInfo object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(EdgeServerInfo object) {
    }
    
    @Override
    public void onAfterUpdateObject(EdgeServerInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(EdgeServerInfo object) {
    }
    
    @Override
    public void onAfterDeleteObject(EdgeServerInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(EdgeServerInfo object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}