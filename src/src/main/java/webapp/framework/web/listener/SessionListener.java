package webapp.framework.web.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * SessionListener class used to listen to session creation and destruction.<br>
 * Please add in web.xml its definition to enable it.
 *
 * <pre>
 *  &lt;listener&gt;
 *      &lt;listener-class&gt;com.demo.web.listener.SessionListener&lt;/listener-class&gt;
 *  &lt;/listener&gt;
 * </pre>
 *
 */
public class SessionListener implements HttpSessionListener {

    private static final Logger log = Logger.getLogger(SessionListener.class);

    /**
     * called upon session creation
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        if (log.isInfoEnabled()) {
            HttpSession session = se.getSession();
            log.info("sessionId=" + session.getId() + " maxInactiveInterval=" + session.getMaxInactiveInterval()
                    + " seconds");
        }
    }

    /**
     * called upon session deletion
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        if (log.isInfoEnabled()) {
            HttpSession session = se.getSession();
            // session has been invalidated and all session data (except Id)is no longer available
            log.info("sessionId=" + session.getId());
        }
    }
}
