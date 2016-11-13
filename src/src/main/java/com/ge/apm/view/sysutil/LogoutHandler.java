package com.ge.apm.view.sysutil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

/**
 *
 * @author 212547631
 */
public class LogoutHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private static String logoutSuccessUrl = "/login.xhtml";

    public static void setLogoutSuccessUrl(String _logoutSuccessUrl) {
        logoutSuccessUrl = _logoutSuccessUrl;
    }
        
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException, ServletException {
        if (auth != null) {
            super.onLogoutSuccess(request, response, auth);
        }
        else
            request.getRequestDispatcher("/login.xhtml").forward(request, response);
    }
    
    @Override
    protected String determineTargetUrl(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response){
        return logoutSuccessUrl;
    }    
}