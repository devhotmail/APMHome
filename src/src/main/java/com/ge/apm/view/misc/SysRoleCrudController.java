package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.domain.SysRole;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SysRoleCrudController extends JpaCRUDController<SysRole> {

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
    public void onBeforeNewObject(SysRole object) {
    }
    
    @Override
    public void onAfterNewObject(SysRole object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(SysRole object) {
    }
    
    @Override
    public void onAfterUpdateObject(SysRole object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(SysRole object) {
    }
    
    @Override
    public void onAfterDeleteObject(SysRole object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(SysRole object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}