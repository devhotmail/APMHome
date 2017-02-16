package webapp.framework.context;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

public class LocaleHolder {

    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public static void setLocale(Locale locale) {
        LocaleContextHolder.setLocale(locale);
    }

    public static void resetLocaleContext() {
        LocaleContextHolder.resetLocaleContext();
    }
}