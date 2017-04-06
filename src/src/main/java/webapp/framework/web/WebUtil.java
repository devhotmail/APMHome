package webapp.framework.web;

import java.util.List;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.jsf.FacesContextUtils;

import com.ge.apm.domain.UserAccount;

import webapp.framework.util.StringUtil;
import webapp.framework.web.service.DbResourcesUtil;
import webapp.framework.web.service.UserContext;

@Component
public class WebUtil implements ApplicationContextAware {

    private static ApplicationContext webAppContext = null;

    public static ApplicationContext getApplicationContext() {
        return webAppContext;
    }

    private String webAppPath;

    public String getWebAppPath() {
        if (webAppPath == null) {
            try {
                webAppPath = webAppContext.getResource("/").getFile().getAbsolutePath();
            } catch (Exception ex) {
                System.err.println("-------------- failed to get WebAppPath");
            }
        }

        return webAppPath;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        webAppContext = applicationContext;
    }
    
    public static <T> T getBean(Class<T> beanClass, String beanName) {
        T bean;
        try {
            bean = getServiceBean(beanName, beanClass);
            if (bean != null) {
                return bean;
            }
        } catch (Exception ex) {
            //do nothing
        }

        bean = getJsfViewBean(beanName, beanClass);
        return bean;
    }

    public static <T> T getBean(Class<T> beanClass) {
        T bean;
        try {
            bean = getServiceBean(beanClass);
            if (bean != null) {
                return bean;
            }
        } catch (Exception ex) {
            //do nothing
        }

        bean = getJsfViewBean(beanClass);
        return bean;
    }

    public static <T> T getServiceBean(Class<T> beanClass) {
        return getServiceBean(null, beanClass);
    }

    public static <T> T getServiceBean(String beanName, Class<T> beanClass) {
        if (webAppContext == null) {
            System.out.println("*************  webAppContext is null");
            webAppContext = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        }
        if (beanName == null) {
            return webAppContext.getBean(beanClass);
        } else {
            return webAppContext.getBean(beanName, beanClass);
        }
    }

    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    public static <T> T getJsfViewBean(Class<T> beanClass) {
        return getJsfViewBean(toLowerCaseFirstOne(beanClass.getSimpleName()), beanClass);
    }

    public static <T> T getJsfViewBean(String beanName, Class<T> beanClass) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        ELContext elContext = context.getELContext();

        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, String.format("#{%s}", beanName), beanClass);
        T obj = (T) valueExpression.getValue(elContext);

        if (obj == null) {
            System.out.println("=============================================================");
            System.out.println("************  failed to get JSF bean, name=" + beanName);
            System.out.println("=============================================================");
        }
        return obj;
    }

    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    public static String getFieldValueMessage(String fieldName, String fieldValue) {
        DbResourcesUtil resourcesUtil = (DbResourcesUtil) getServiceBean(DbResourcesUtil.class);
        return WebUtil.getMessage(fieldName+"-"+fieldValue);
    }

    public static String getMessage(String messageKey, Object... obj) {
        return String.format(getMessage(messageKey), obj);
    }
    
    public static String getMessage(String messageKey) {
        DbResourcesUtil resourcesUtil = (DbResourcesUtil) getServiceBean(DbResourcesUtil.class);
        return resourcesUtil.getProperty(messageKey);
    }

    public static boolean isValidationFailed() {
        return FacesContext.getCurrentInstance().isValidationFailed();
    }

    public static void addErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(defaultMsg);
        }
    }

    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    public static void addErrorMessageKey(String msgKey, Object... obj) {
        addErrorMessage(String.format(getMessage(msgKey), obj));
    }

    public static void addErrorMessageKey(String msgKey) {
        addErrorMessage(getMessage(msgKey));
    }

    public static void addErrorMessage(String msg) {
        addErrorMessage(msg, "xx");
    }

    public static void addErrorMessage(String msg, String detailMessage) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, detailMessage);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addSuccessMessageKey(String msgKey, Object... obj) {
        addSuccessMessageKey(String.format(getMessage(msgKey), obj));
    }

    public static void addSuccessMessageKey(String msgKey) {
        addSuccessMessage(getMessage(msgKey));
    }

    public static void addSuccessMessage(String msg) {
        addSuccessMessage(msg, null);
    }

    public static void addSuccessMessage(String msg, String detailMessage) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }

    public static String getHttpRequestMethod() {
        HttpServletRequestWrapper request = (HttpServletRequestWrapper) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getMethod();
    }
    
    public static boolean isHttpGetRequest() {
        return "GET".equals(getHttpRequestMethod());
    }

    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }

    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
        String theId = WebUtil.getRequestParameter(requestParameterName);
        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);

    }

    public static void navigateTo(String url){
        FacesContext context = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler)context.getApplication().getNavigationHandler();
        handler.performNavigation(url);
    }

    public static void redirectTo(String url){
        FacesContext context = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler)context.getApplication().getNavigationHandler();
        handler.performNavigation(url + "?faces-redirect=true");
    }
    
    public static enum PersistAction {
        CREATE,
        DELETE,
        UPDATE
    }

    public static String encodeUrlParameters(JSONObject params){
        if(params==null) return "";
        
        return StringUtil.desEncrypt(params.toString());
    }
    
    public static JSONObject decodeUrlParameters(){
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String queryString = request.getQueryString();
        if(queryString==null) return null;

        return new JSONObject(StringUtil.desDecrypt(queryString));
    }
    
    public static UserAccount getUserAccountFromRequest(){
    	 HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	return UserContext.getCurrentLoginUser(request);
    }
    
    
}
