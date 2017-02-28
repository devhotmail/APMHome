package webapp.framework.web.service;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;
import webapp.framework.context.WrappedRequest;

public class LoginService implements Serializable, ApplicationEventPublisherAware {

    private static final long serialVersionUID = 1L;

    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private SecurityContextRepository securityContextRepository;

    private MessageUtil messageUtil;

    private ApplicationEventPublisher applicationEventPublisher;

    static {
        IdFuzzyResolver.registerResolver();
        
        //Global setting: change default timezone to UTC+0
        //TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }

    @PostConstruct
    private void init() {
        WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        usernamePasswordAuthenticationFilter = ctx.getBean(UsernamePasswordAuthenticationFilter.class);
        sessionAuthenticationStrategy = ctx.getBean(SessionAuthenticationStrategy.class);
        securityContextRepository = ctx.getBean(SecurityContextRepository.class);
        messageUtil = ctx.getBean(MessageUtil.class);
    }

    public void setSessionExpired(String value) {
        messageUtil.error("session_expired");
    }

    public String getSessionExpired() {
        return null;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void forgetPassword() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        String loginName = request.getParameter("j_username");

        FacesMessage msg = new FacesMessage();
        msg.setSummary("Password reset");
        msg.setDetail("Password reset");
        FacesContext.getCurrentInstance().addMessage("forgetPassword", msg);
    }
    
    public String onPasswordEncrypted(String loginName, String plainPassword){
        return plainPassword;
    }

    protected void afterLogin(){
        
    }
    
    public void doLogin() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext securityContext = securityContextRepository.loadContext(holder);
        SecurityContextHolder.setContext(securityContext);

        try {
            Map<String, String[]> extraParams = new TreeMap<String, String[]>();
            String userName = request.getParameter("j_username");
            String password = request.getParameter("j_password");

            if("".equals(userName) || userName==null){
                userName = request.getParameter("input_j_username");
                password = request.getParameter("input_j_password");
                extraParams.put("j_username", new String[]{userName});
                extraParams.put("j_password", new String[]{password});
                HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
                request = wrappedRequest;
            }
            if(password==null) password = "";
            
            password = onPasswordEncrypted(userName, password);
            extraParams.put("j_username", new String[]{userName});
            extraParams.put("j_password", new String[]{password});
            HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
            request = wrappedRequest;
            
            Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
            if (authResult == null) {
                messageUtil.error("security_error");
                return;
            }
            sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
            // below : do the same thing as in AbstractAuthenticationProcessingFilter.successfulAuthentication(),
            // except for the redirection to the login success URL that is managed by JSF
            securityContext.setAuthentication(authResult);
            usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
            if (applicationEventPublisher != null) {
                applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, getClass()));
            }

            afterLogin();
            // redirects to the home page
/*
            FacesContext context = FacesContext.getCurrentInstance();
            ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
            handler.performNavigation("/home.xhtml?faces-redirect=true");
*/
        } catch (UsernameNotFoundException e) {
            messageUtil.error("security_username_not_found");
        } catch (DisabledException e) {
            messageUtil.error("security_account_disabled");
        } catch (LockedException e) {
            messageUtil.error("security_account_locked");
        } catch (AccountExpiredException e) {
            messageUtil.error("security_account_expired");
        } catch (CredentialsExpiredException e) {
            messageUtil.error("security_account_credentials_expired");
        } catch (AccountStatusException e) {
            messageUtil.error("security_account_status_invalid");
        } catch (BadCredentialsException e) {
            messageUtil.error("security_bad_credentials");
        } catch (AuthenticationException e) {
            messageUtil.error("security_error");
        } finally {
            securityContextRepository.saveContext(securityContext, holder.getRequest(), holder.getResponse());
        }
    }
}
