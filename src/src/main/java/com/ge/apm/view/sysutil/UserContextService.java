package com.ge.apm.view.sysutil;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

@ManagedBean(name="userContextService")
@SessionScoped
public class UserContextService implements Serializable{
    private static final long serialVersionUID = 1L;

    private UserAccount userAccount=null;
    
    public void processAfterLogin(){
        getLoginUser();
    }

    public boolean isLoggedIn() {
        return !isAnonymousUser();
    }

    public String getUserLoginName() {
        return UserContext.getUsername();
    }
    public String getUserName() {
        if(userAccount==null)
            return "";
        else
            return userAccount.getName();
    }

    public boolean isAnonymousUser() {
        return UserContext.ANONYMOUS_USER.equalsIgnoreCase(getUserLoginName());
    }

    private boolean isFirstRequestAfterLogin = true;
    public boolean isIsFirstRequestAfterLogin() {
        return isFirstRequestAfterLogin;
    }

    public void checkIsSuperAdminAndFirstRequest(){
        if(!getLoginUser().getIsSuperAdmin()) return;

        if(!isFirstRequestAfterLogin) return;
        isFirstRequestAfterLogin = false;

        //navigate to super admin's home page 
        FacesContext context = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler)context.getApplication().getNavigationHandler();
        handler.performNavigation("admin/adminHome.xhtml?faces-redirect=true");
    }
    
    public static List<String> getRoles() {
        return UserContext.getRoles();
    }

    public boolean hasRole(String role) {
        return UserContextService.checkRole(role);
    }

    public static boolean checkRole(String role) {
        if(UserContextService.isSuperAdmin()) return true;
        else if("SuperAdmin".equals(role)) return false;

        if(UserContextService.isTenantAdmin()) return true;
        return UserContext.getRoles().contains(role);
    }

    protected UserAccount getLoginUser(){
        if (userAccount==null){
            String userName = getUserLoginName();
            if(userName==null) return null; // user is not logined
            
            UserAccountRepository dao=(UserAccountRepository) WebUtil.getBean(UserAccountRepository.class);
            userAccount = dao.getByLoginName(getUserLoginName());
        }
        
        return userAccount;
    }

    public boolean getIsSuperAdmin(){
        return UserContextService.isSuperAdmin();
    }
    
    public boolean getIsTenantAdmin(){
        return UserContextService.isTenantAdmin();
    }

    public static UserAccount getCurrentUserAccount(){
        UserContextService userContextService = (UserContextService) WebUtil.getBean(UserContextService.class);
        return userContextService.getLoginUser();
    }
    
    public static boolean isSuperAdmin(){
        UserAccount userAccount = getCurrentUserAccount();
        return userAccount.getIsSuperAdmin();
    }

    public static boolean isTenantAdmin(){
        UserAccount userAccount = getCurrentUserAccount();
        return userAccount.getIsTenantAdmin();
    }

    public static int getUserID(){
        UserAccount userAccount = getCurrentUserAccount();
        if (userAccount!=null) return userAccount.getId();
        
        return -1;
    }
    
    public static int getSiteId(){
        UserAccount userAccount = getCurrentUserAccount();
        if (userAccount!=null) return userAccount.getSiteId();
        
        return -1;
    }
    
    public static void setSiteFilter(List<SearchFilter> filters){
        filters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getSiteId()));
    }
}
