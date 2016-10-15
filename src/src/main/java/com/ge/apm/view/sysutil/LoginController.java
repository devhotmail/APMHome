package com.ge.apm.view.sysutil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        //AppContextService.addActiveUser(userContextService.getLoginUser());
        
        deleteUserReport();
    }

    private void deleteUserReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        String rootPath = request.getRealPath("/") + "/" + "report" + "/" + UserContext.getUsername();
        
        File rootPathFile = new File(rootPath);
        
        try {
            FileUtils.deleteDirectory(rootPathFile);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
