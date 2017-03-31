package com.ge.apm.view.sysutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
  private static final Logger log = LoggerFactory.getLogger(LogoutHandler.class);
  private static String logoutSuccessUrl = "/login.xhtml";

  public static void setLogoutSuccessUrl(String _logoutSuccessUrl) {
    logoutSuccessUrl = _logoutSuccessUrl;
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
    throws IOException, ServletException {
    logoutSuccessUrl = String.format("/login.xhtml?uid=%s", request.getParameter("uid"));
    if (auth != null) {
      super.onLogoutSuccess(request, response, auth);
    } else
      request.getRequestDispatcher(logoutSuccessUrl).forward(request, response);
  }

  @Override
  protected String determineTargetUrl(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
    return logoutSuccessUrl;
  }
}