package com.ge.apm.view.sysutil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.LoginService;
import webapp.framework.web.service.UserContext;

@ManagedBean
@RequestScoped
public class LoginController extends LoginService {

    @Override
    protected void afterLogin() {
        // called after user logined.
        UserContextService userContextService = (UserContextService) WebUtil.getBean(UserContextService.class);
        userContextService.processAfterLogin();

        FacesContext context = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler)context.getApplication().getNavigationHandler();

        String url;
        System.out.println("userRoles="+userContextService.getLoginUser().getRoleNames());
        if(userContextService.hasRole("HospitalHead"))
            url = "/home.xhtml";
        else if(userContextService.hasRole("AssetHead"))
            url = "/home.xhtml";
        else if(userContextService.hasRole("DeptHead"))
            url = "/home.xhtml";
        else if(userContextService.hasRole("DeptStuff"))
            url = "/home.xhtml";
        else
            url = "/utils.xhtml";
        
        handler.performNavigation(url + "?faces-redirect=true");
    }
}
