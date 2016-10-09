package com.ge.aps.view.sysutil;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 * @author 212547631
 */
public class JsfPhaseListner implements PhaseListener {
 
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
 
    @Override
    public void beforePhase(PhaseEvent event) {
        AppContextService.addActiveUser(UserContextService.getCurrentUserAccount());
    }
 
    @Override
    public void afterPhase(PhaseEvent event) {
        //FacesContext fc = event.getFacesContext();
    }
}
