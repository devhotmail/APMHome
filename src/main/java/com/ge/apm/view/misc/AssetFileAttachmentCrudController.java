package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.domain.AssetFileAttachment;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetFileAttachmentCrudController extends GenericCRUDController<AssetFileAttachment> {

    AssetFileAttachmentRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetFileAttachmentRepository.class);
    }

    @Override
    protected AssetFileAttachmentRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetFileAttachment> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetFileAttachment> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(AssetFileAttachment object) {
    }
    
    @Override
    public void onAfterNewObject(AssetFileAttachment object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(AssetFileAttachment object) {
    }
    
    @Override
    public void onAfterUpdateObject(AssetFileAttachment object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(AssetFileAttachment object) {
    }
    
    @Override
    public void onAfterDeleteObject(AssetFileAttachment object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(AssetFileAttachment object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}