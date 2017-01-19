package webapp.framework.web.service;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.springframework.stereotype.Component;

/**
 * Use it during development to throw exceptions from your flows and thus verify that your 
 * error handling politic is correct.
 */
@ManagedBean
@SessionScoped
public class ExceptionUtil {

    public void throwRuntimeException() {
        throw new RuntimeException("Just testing from ExceptionUtil ...");
    }
    
   public static boolean isCausedBy(Throwable e, Class<?> cause) {
        Throwable current = e;
        while (current != null) {
            if (cause.isAssignableFrom(current.getClass())) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
    
}