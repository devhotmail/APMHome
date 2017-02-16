package webapp.framework.context;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.Digests;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

/**
 *
 * @author wujianb
 */
@Component
public class ExternalLoginHandler {
    private static final Logger logger = Logger.getLogger(ExternalLoginHandler.class);    
    
    @Autowired
    SecurityContextRepository securityContextRepository;
    @Autowired
    UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;
    @Autowired 
    SessionAuthenticationStrategy sessionAuthenticationStrategy;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    public boolean doLoginByWeChatOpenId(String openId, HttpServletRequest request, HttpServletResponse response){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        UserAccount user = userDao.getByWeChatId(openId);
        if(user==null){
            //WebUtil.redirectTo("/login.xhtml");
            return false;
        }
        
        return doLoginWithEncryptedPassword(user.getLoginName(), user.getPassword(), request, response);
    }

    public boolean doLoginWithPlainPassword(String userName, String plainPassword, HttpServletRequest request, HttpServletResponse response){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        UserAccount user = userDao.getByLoginName(userName);

        if(user!=null) {
            try {
                byte[] salt = Digests.decodeHex(user.getPwdSalt());
                byte[] hashPassword;
                
                hashPassword = Digests.sha1(plainPassword.getBytes(), salt, UserAccount.HASH_INTERATIONS);
                String encryptedPassword = Digests.encodeHex(hashPassword);
                
                if (user.getPassword().equals(encryptedPassword)) {
                    return doLoginWithEncryptedPassword(userName, encryptedPassword, request, response);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return false;
    }
    
    public boolean doLoginWithEncryptedPassword(String userName, String encryptedPassword, HttpServletRequest request, HttpServletResponse response){
        Map<String, String[]> extraParams = new TreeMap<String, String[]>();
        
        extraParams.put("j_username", new String[]{userName});
        extraParams.put("j_password", new String[]{encryptedPassword});
        HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
        
        return doExternalLogin(wrappedRequest, response);
    }    
    
    public boolean doExternalLogin(HttpServletRequest request, HttpServletResponse response){
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext securityContext = securityContextRepository.loadContext(holder);
        SecurityContextHolder.setContext(securityContext);
        try {
            usernamePasswordAuthenticationFilter.setPostOnly(false);
            
            Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
            if (authResult == null) {
                return false;
            }
            
            try{
                sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
            }
            catch(Exception ex){
                handleError(ex, "onAuthentication");
            }

            securityContext.setAuthentication(authResult);
            usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
            if (applicationEventPublisher != null) {
                applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, getClass()));
            }
            
            return true;
        } catch (UsernameNotFoundException e) {
            handleError(e, "security_username_not_found");
        } catch (DisabledException e) {
            handleError(e, "security_account_disabled");
        } catch (LockedException e) {
            handleError(e, "security_account_locked");
        } catch (AccountExpiredException e) {
            handleError(e, "security_account_expired");
        } catch (CredentialsExpiredException e) {
            handleError(e, "security_account_credentials_expired");
        } catch (AccountStatusException e) {
            handleError(e, "security_account_status_invalid");
        } catch (BadCredentialsException e) {
            handleError(e, "security_bad_credentials");
        } catch (AuthenticationException e) {
            handleError(e, "security_error");
        } finally {
            securityContextRepository.saveContext(securityContext, holder.getRequest(), holder.getResponse());
        }
        return false;
    }

    public void handleError(Exception ex, String errKey){
        logger.error(ex.getMessage(), ex);
    }
    
}
