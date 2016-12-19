package com.ge.apm.view.uaa;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.domain.SysRole;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SysRoleController extends GenericCRUDController<SysRole> {

    SysRoleRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SysRoleRepository.class);
    }

    @Override
    protected SysRoleRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<SysRole> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<SysRole> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(PRole object) {
    }
    
    @Override
    public void onAfterNewObject(PRole object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(PRole object) {
    }
    
    @Override
    public void onAfterUpdateObject(PRole object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(PRole object) {
    }
    
    @Override
    public void onAfterDeleteObject(PRole object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(PRole object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}