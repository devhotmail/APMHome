package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.domain.UserRole;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class UserRoleCrudController extends JpaCRUDController<UserRole> {

    UserRoleRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(UserRoleRepository.class);
    }

    @Override
    protected UserRoleRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<UserRole> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<UserRole> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(UserRole object) {
    }
    
    @Override
    public void onAfterNewObject(UserRole object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(UserRole object) {
    }
    
    @Override
    public void onAfterUpdateObject(UserRole object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(UserRole object) {
    }
    
    @Override
    public void onAfterDeleteObject(UserRole object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(UserRole object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}