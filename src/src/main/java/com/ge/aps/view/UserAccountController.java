package com.ge.aps.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.aps.dao.UserAccountRepository;
import com.ge.aps.domain.UserAccount;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class UserAccountController extends GenericCRUDController<UserAccount> {

    UserAccountRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(UserAccountRepository.class);
    }

    @Override
    protected UserAccountRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<UserAccount> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<UserAccount> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(UserAccount object) {
    }
    
    @Override
    public void onAfterNewObject(UserAccount object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(UserAccount object) {
    }
    
    @Override
    public void onAfterUpdateObject(UserAccount object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(UserAccount object) {
    }
    
    @Override
    public void onAfterDeleteObject(UserAccount object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(UserAccount object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}