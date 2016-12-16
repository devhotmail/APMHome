package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetContractRepository;
import com.ge.apm.domain.AssetContract;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetContractCrudController extends JpaCRUDController<AssetContract> {

    AssetContractRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetContractRepository.class);
    }

    @Override
    protected AssetContractRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetContract> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetContract> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(AssetContract object) {
    }
    
    @Override
    public void onAfterNewObject(AssetContract object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(AssetContract object) {
    }
    
    @Override
    public void onAfterUpdateObject(AssetContract object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(AssetContract object) {
    }
    
    @Override
    public void onAfterDeleteObject(AssetContract object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(AssetContract object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}