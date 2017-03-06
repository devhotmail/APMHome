package webapp.framework.web.component;

import javax.faces.application.FacesMessage;

/**
 * Wrap {@link FacesMessage} along with the id of the associated component.
 */
public class Message {

    private String sourceId;
    private FacesMessage facesMessage;

    public Message(String sourceId, FacesMessage facesMessage) {
        this.sourceId = sourceId;
        this.facesMessage = facesMessage;
    }

    public String getSourceId() {
        return sourceId;
    }

    public FacesMessage getFacesMessage() {
        return facesMessage;
    }
}
