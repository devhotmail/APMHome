package webapp.framework.web.service;

import java.util.Enumeration;
import java.util.ResourceBundle;


/**
 * This ResourceBundle is set in faces-config.xml under 'msg' var.
 * Implementation uses Spring MessageSource.
 * From your JSF2 pages, you may use #{msg.property_key} 
 */
public class MessageBundle extends ResourceBundle {

    @Override
    public Enumeration<String> getKeys() {
        return DbResourcesUtil.getInstance().getAsResourceBundle().getKeys();
    }

    @Override
    protected Object handleGetObject(String key) {
        return DbResourcesUtil.getInstance().getAsResourceBundle().getObject(key);
    }
}