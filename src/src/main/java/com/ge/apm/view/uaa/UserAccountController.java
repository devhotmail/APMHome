package com.ge.apm.view.uaa;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDController;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserRole;
import com.ge.apm.service.uaa.UaaService;
import java.util.ArrayList;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class UserAccountController extends GenericCRUDController<UserAccount> {

    UserAccountRepository dao = null;
    UaaService uaaService = WebUtil.getBean(UaaService.class);

    @Override
    protected void init() {
        uaaService = WebUtil.getBean(UaaService.class);
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
    public String getKeyFieldNameValue(UserAccount obj){
        return WebUtil.getMessage("login_name")+"="+obj.getLoginName();
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
    
    private List<String> allRoles;
    private List<String> assignedRoles;
    public List<String> getAllRoles() {
        return allRoles;
    }

    public void setAllRoles(List<String> allRoles) {
        this.allRoles = allRoles;
    }

    public List<String> getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(List<String> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }
    
    public void prepareRoleData() {
        if(selected == null) return;
        
        allRoles = uaaService.getSysRoleNames();
        
        assignedRoles = new ArrayList<String>();
        for(UserRole userRole: selected.getUserRoleList()){
            assignedRoles.add(userRole.getRole().getRoleDesc());
        }
    }

    public void saveRoles() {
        if(selected == null) return;
        
        uaaService.setUserRoles(selected, assignedRoles);
        WebUtil.addSuccessMessage(WebUtil.getMessage("UserRole") + WebUtil.getMessage("Updated"));
    }
    

}