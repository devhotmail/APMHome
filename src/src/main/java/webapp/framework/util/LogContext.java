package webapp.framework.util;

import org.apache.log4j.MDC;

/**
 * Used to store to use global info for logging purposes. This method prevents passing all the data you want to see in your logs in all the methods.
 * <p>
 * Please configure log4j with the data provided by this class where <code>session_id</code> and <code>login</code> are objects stored in the log context.
 * <p>
 * Example: <br>
 * <code>
 * log4j.appender.CONSOLE.layout.ConversionPattern=%d{HH:mm:ss-SSS} [%t] %X{session_id} %p %X{login} %C.%M(%L) | %m%n
 * </code>
 */
public class LogContext {

    /**
     * parameter name that holds logins
     */
    public static final String LOGIN = "login";

    /**
     * parameter name that holds web session ids
     */
    public static final String SESSION_ID = "session_id";

    /**
     * set the given login in the map
     */
    public static void setLogin(String login) {
        put(LOGIN, login);
    }

    /**
     * set the given web session in the map
     */
    public static void setSessionId(String sessionId) {
        put(SESSION_ID, sessionId);
    }

    /**
     * Get the context identified by the key parameter.
     */
    public static Object get(String key) {
        return MDC.get(key);
    }

    /**
     * Put a context value (the o parameter) as identified with the key parameter into the current thread's context map.
     */
    public static void put(String key, Object o) {
        MDC.put(key, o);
    }

    /**
     * Remove the the context identified by the key parameter.
     */
    public static void remove(String key) {
        MDC.remove(key);
    }

    /**
     * Remove all the object put in this thread context.
     */
    public static void resetLogContext() {
        if (MDC.getContext() != null) {
            MDC.getContext().clear();
        }
    }
}
