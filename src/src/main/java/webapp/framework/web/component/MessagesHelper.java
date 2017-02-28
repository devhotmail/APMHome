package webapp.framework.web.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import org.springframework.stereotype.Component;

/**
 * Helper used from the appcc:messages composite component.
 */
@Component
public class MessagesHelper {

    public String getStyleClass() {
        Severity maxSeverity = null;
        Iterator<FacesMessage> msgs = FacesContext.getCurrentInstance().getMessages();

        while (msgs.hasNext()) {
            if (maxSeverity == null) {
                maxSeverity = FacesMessage.SEVERITY_INFO;
            }

            Severity currentSeverity = msgs.next().getSeverity();
            if (currentSeverity.getOrdinal() > maxSeverity.getOrdinal()) {
                maxSeverity = currentSeverity;
            }
        }

        if (maxSeverity != null) {
            if (maxSeverity.equals(FacesMessage.SEVERITY_INFO)) {
                return "ui-messages-info";
            }
            if (maxSeverity.equals(FacesMessage.SEVERITY_WARN)) {
                return "ui-messages-warn";
            }
            if (maxSeverity.equals(FacesMessage.SEVERITY_ERROR)) {
                return "ui-messages-error";
            }
            if (maxSeverity.equals(FacesMessage.SEVERITY_FATAL)) {
                return "ui-messages-fatal";
            }
        }

        return "";
    }

    public Message[] getGlobalMessages() {
        List<Message> res = new ArrayList<Message>();
        Iterator<FacesMessage> msgs = FacesContext.getCurrentInstance().getMessages(null);
        while (msgs.hasNext()) {
            res.add(new Message(null, msgs.next()));
        }
        return res.toArray(new Message[res.size()]);
    }

    public Message[] getNonGlobalMessages() {
        List<Message> res = new ArrayList<Message>();

        Iterator<String> ids = FacesContext.getCurrentInstance().getClientIdsWithMessages();
        while (ids.hasNext()) {
            String id = ids.next();
            Iterator<FacesMessage> msgs = FacesContext.getCurrentInstance().getMessages(id);
            while (msgs.hasNext()) {
                res.add(new Message(id, msgs.next()));
            }
        }

        return res.toArray(new Message[res.size()]);
    }

    public boolean hasGlobalMessages() {
        return FacesContext.getCurrentInstance().getMessages(null).hasNext();
    }

    public boolean hasNonGlobalMessages() {
        Iterator<String> ids = FacesContext.getCurrentInstance().getClientIdsWithMessages();
        while (ids.hasNext()) {
            String clientId = ids.next();
            if (clientId != null && !clientId.equals("null")) { /* the 'null' string is pretty disturbing */
                return true;
            }
        }
        return false;
    }

    public int nonGlobalMessagesCount() {
        int count = 0;
        Iterator<String> ids = FacesContext.getCurrentInstance().getClientIdsWithMessages();
        while (ids.hasNext()) {
            String clientId = ids.next();
            if (clientId != null && !clientId.equals("null")) { /* the 'null' string is pretty disturbing */
                count += FacesContext.getCurrentInstance().getMessageList(clientId).size();
            }
        }
        return count;
    }
}