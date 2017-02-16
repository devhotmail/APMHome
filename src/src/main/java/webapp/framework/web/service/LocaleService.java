package webapp.framework.web.service;

import java.io.Serializable;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import java.util.Locale;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;
import webapp.framework.context.LocaleHolder;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

@ManagedBean
@SessionScoped
public class LocaleService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private LocaleResolver localeResolver;

    @PostConstruct
    private void init() {
        WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        localeResolver = ctx.getBean(LocaleResolver.class);
    }
    
    public String getLocale() {
        return LocaleHolder.getLocale().toString();
    }

    public String getLanguage() {
        return LocaleHolder.getLocale().getLanguage();
    }

    public String switchToFrench() {
        return switchToLocale(FRENCH);
    }

    public String switchToEnglish() {
        return switchToLocale(ENGLISH);
    }

    private String switchToLocale(Locale locale) {
        updateJsfLocale(locale);
        updateSpringLocale(locale);
        return redirectToSelf();
    }

    private String redirectToSelf() {
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        return viewId + "?faces-redirect=true";
    }

    private void updateJsfLocale(Locale locale) {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    private void updateSpringLocale(Locale locale) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        localeResolver.setLocale((HttpServletRequest) externalContext.getRequest(), (HttpServletResponse) externalContext.getResponse(), locale);
        LocaleHolder.setLocale(locale);
    }

    public boolean isFrench() {
        // check 'fr_FR' or simply 'fr'
        return FRENCH.equals(LocaleHolder.getLocale()) || FRENCH.getLanguage().equals(getLanguage());
    }
}
