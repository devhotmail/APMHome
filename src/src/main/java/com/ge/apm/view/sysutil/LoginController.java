package com.ge.apm.view.sysutil;

import com.ge.apm.service.uaa.UaaService;
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

        System.out.println("******* home page: " + userContextService.getUserDefaultHomePage());
        WebUtil.redirectTo(userContextService.getUserDefaultHomePage());
    }
}
