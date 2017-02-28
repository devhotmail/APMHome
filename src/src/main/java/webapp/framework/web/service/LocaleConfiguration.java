package webapp.framework.web.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import java.util.Locale;
import webapp.framework.context.LocaleHolder;

@Configuration
public class LocaleConfiguration {
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("locale");
        Locale locale = new Locale("zh", "CN");
        resolver.setDefaultLocale(locale);
        LocaleHolder.setLocale(locale);
        return resolver;
    }
}
