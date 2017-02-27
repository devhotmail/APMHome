package webapp.framework.web.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.stereotype.Service;

@Service
@Lazy(false)
public class DbResourcesUtil {
    private static final Logger logger = Logger.getLogger(DbResourcesUtil.class);

    public static final String DATE_FORMAT_KEY = "dateformat_default";
    public final static String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    private static DbResourcesUtil instance;
    private static MessageSource messageSource;

    public DbResourcesUtil() {
        instance = this;
        messageSource = new DbMessageSource();
    }

    public static DbResourcesUtil getInstance() {
        return instance;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public ResourceBundle getAsResourceBundle() {
        return new MessageSourceResourceBundle(messageSource, LocaleContextHolder.getLocale());
    }

    public String getMsgValue(String key, Locale locale) {
        if (key == null)
            return "";
        else
            return messageSource.getMessage(key, null, locale);
    }
    
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public String getProperty(String key, Object[] args) {
        if (key == null) return "";

        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public String getFormattedDate(Date date) {
        if (date == null) {
            return "";
        }

        String format = getProperty(DATE_FORMAT_KEY);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, LocaleContextHolder.getLocale());
        return dateFormat.format(date);
    }

    public String getFormattedDate(LocalDate localDate) {
        if (localDate == null) {
            return "";
        }

        String format = getProperty(DATE_FORMAT_KEY);
        return localDate.toString(format, LocaleContextHolder.getLocale());
    }

    public String getFormattedDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }

        String format = getProperty(DATE_FORMAT_KEY);
        return localDateTime.toString(format, LocaleContextHolder.getLocale());
    }

}