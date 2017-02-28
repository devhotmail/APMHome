package webapp.framework.web.service;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Convenient bean to create JSF info/warn/error messsages from your flow.
 */
@Component
public class MessageUtil {

    @Autowired
    private ResourcesUtil resourcesUtil;

    public void info(String summaryKey, Object... args) {
        addFacesMessage(FacesMessage.SEVERITY_INFO, summaryKey, args);
    }

    public void warning(String summaryKey, Object... args) {
        addFacesMessage(FacesMessage.SEVERITY_WARN, summaryKey, args);
    }

    public void error(String summaryKey, Object... args) {
        addFacesMessage(FacesMessage.SEVERITY_ERROR, summaryKey, args);
    }

    private void addFacesMessage(Severity severity, String summaryKey, Object[] args) {
        FacesMessage fm = new FacesMessage(resourcesUtil.getProperty(summaryKey, args));
        fm.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }
}