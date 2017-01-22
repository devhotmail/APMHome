package webapp.framework.web.service;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import webapp.framework.web.WebUtil;

/**
 * Get Spring security context to access user data security infos
 */
public class UserContext {
    public static final String ANONYMOUS_USER = "anonymousUser";

    public static String getUsername(HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext==null){
            if(request==null){
                try{
                    request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
                }
                catch(Exception ex){
                }
            }
            if(request==null) return null;
            
            securityContext = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        }
        if(securityContext==null) return null;
        
        Authentication auth = securityContext.getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();

            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }

            return (String) principal.toString();
        }

        return null;
    }
    
    public static boolean isLoggedIn(HttpServletRequest request) {
        String userName = getUsername(request);
        
        return (userName!=null) && (!UserContext.ANONYMOUS_USER.equalsIgnoreCase(userName));
    }
    
    public static boolean isAnonymousUser() {
        return UserContext.ANONYMOUS_USER.equalsIgnoreCase(UserContext.getUsername());
    }

    /**
     * Get the current username. Note that it may not correspond to a username that
     * currently exists in your accounts' repository; it could be a spring security
     * 'anonymous user'.
     *
     * @return the current user's username, or null if none.
     */
    public static String getUsername() {
        return getUsername(null);
    }

    public static UserAccount getCurrentLoginUser(HttpServletRequest request){
        String loginName = getUsername(request);
        if(loginName==null) return null;
        
        UserAccountRepository dao=(UserAccountRepository) WebUtil.getBean(UserAccountRepository.class);
        return dao.getByLoginName(loginName);
    }
    
    public static UserAccount getCurrentLoginUser(){
        UserAccountRepository dao=(UserAccountRepository) WebUtil.getBean(UserAccountRepository.class);
        return dao.getByLoginName(getUsername());
    }
    
    /**
     * return the current locale
     */
    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * Retrieve the current UserDetails bound to the current thread by Spring Security, if any.
     */
    public static UserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal());
        }

        return null;
    }

    /**
     * Return the current roles bound to the current thread by Spring Security.
     */
    public static List<String> getRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            return toStringList(auth.getAuthorities());
        }

        return Collections.emptyList();
    }

    // ---------------------------------------
    // Conversion utils
    // ---------------------------------------
    public static List<String> toStringList(Iterable<? extends GrantedAuthority> grantedAuthorities) {
        List<String> result = new ArrayList<String>();

        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            result.add(grantedAuthority.getAuthority());
        }

        return result;
    }
}