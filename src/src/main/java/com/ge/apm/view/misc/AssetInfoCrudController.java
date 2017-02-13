package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetInfoCrudController extends GenericCRUDController<AssetInfo> {

    AssetInfoRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetInfo> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(AssetInfo object) {
    }
    
    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(AssetInfo object) {
    }
    
    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(AssetInfo object) {
    }
    
    @Override
    public void onAfterDeleteObject(AssetInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(AssetInfo object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}