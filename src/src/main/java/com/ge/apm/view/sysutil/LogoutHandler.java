package com.ge.apm.view.sysutil;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212547631
 */
public class LogoutHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    private static UserAccountRepository userDao = null;
    
    private UserAccount getLoginUserAccount(String userLoginName){
        if(userDao==null)
            userDao = (UserAccountRepository) WebUtil.getBean(UserAccountRepository.class);
        
        if(userDao==null) 
            return null;
        
        return userDao.getByLoginName(userLoginName);
    }
            
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException, ServletException {
        if (auth != null) {
            Object principal = auth.getPrincipal();

            if (principal instanceof UserDetails) {
                String userLoginName = ((UserDetails) principal).getUsername();
                UserAccount userAccount = getLoginUserAccount(userLoginName);
                
                AppContextService.removeActiveUser(userAccount);
            }

            super.onLogoutSuccess(request, response, auth);
        }
        else
            request.getRequestDispatcher("/login.xhtml").forward(request, response);
    }
}