package com.ge.apm.edgeserver.sysutil;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppContext implements ApplicationContextAware {

    private Log log = LogFactory.getLog(AppContext.class);

    @Autowired
    private CamelContext brokerContext;

    @Autowired
    private ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public CamelContext getBrokerContext() {
        return brokerContext;
    }

    public void setBrokerContext(CamelContext brokerContext) {
        this.brokerContext = brokerContext;
    }

    private List<Route> getRoutes() {
        return brokerContext.getRoutes();
    }

    public String showRoutes() {
        StringBuilder b = new StringBuilder();
        getRoutes().forEach(route -> b.append(route.toString()));
        return b.toString();
    }

    public void stop() {
        getRoutes().forEach(route -> doRouteAction(ActionEnum.STOP, route));
    }

    public void suspend() {
        getRoutes().forEach(route -> doRouteAction(ActionEnum.SUSPEND, route));
    }

    public void resume() {
        getRoutes().forEach(route -> doRouteAction(ActionEnum.RESUME, route));
    }

    private void doRouteAction(ActionEnum action, Route route) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Trying action=" + action.toString() + " on route=" + route.getId());
            }
            switch (action) {
                case RESUME:
                    brokerContext.resumeRoute(route.getId());
                    break;
                case START:
                    brokerContext.startRoute(route.getId());
                    break;
                case STOP:
                    brokerContext.stopRoute(route.getId());
                    break;
                case SUSPEND:
                    brokerContext.suspendRoute(route.getId());
                    break;
                default:
                    break;
            }
            if (log.isInfoEnabled()) {
                log.info("ACTION route=" + route + " action=" + action.toString());
            }
        } catch (Exception e) {
            log.error("ACTION route=" + route + "action=" + action.toString(), e);
        }
    }

    private enum ActionEnum {
        SUSPEND, START, STOP, RESUME
    }

}
