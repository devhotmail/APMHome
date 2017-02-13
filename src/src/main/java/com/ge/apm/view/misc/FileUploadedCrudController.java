package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.FileUploadedRepository;
import com.ge.apm.domain.FileUploaded;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

@ManagedBean
@ViewScoped
public class FileUploadedCrudController extends GenericCRUDController<FileUploaded> {

    FileUploadedRepository dao = null;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(FileUploadedRepository.class);
    }

    @Override
    protected FileUploadedRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<FileUploaded> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<FileUploaded> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(FileUploaded object) {
    }
    
    @Override
    public void onAfterNewObject(FileUploaded object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(FileUploaded object) {
    }
    
    @Override
    public void onAfterUpdateObject(FileUploaded object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(FileUploaded object) {
    }
    
    @Override
    public void onAfterDeleteObject(FileUploaded object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(FileUploaded object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}