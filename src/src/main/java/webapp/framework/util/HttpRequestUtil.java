package webapp.framework.util;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author 212547631
 */
public class HttpRequestUtil {

    public static Map<String, Object> getRequestParameterMap() {
        return getParametersStartingWith("");
    }
    
    public static Map<String, Object> getParametersStartingWith(String prefix) {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return getParametersStartingWith(request, prefix);
    }
    
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "";
        }
        while ((paramNames != null) && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            int i = paramName.indexOf(prefix);
            if ("".equals(prefix) || i>=0) {
                String unprefixed = paramName.substring(i+prefix.length());
                String[] values = request.getParameterValues(paramName);
                if ((values == null) || (values.length == 0)) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }
        return params;
    }

    public static Map<String, Object> getParametersEndWith(String str) {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return getParametersEndWith(request, str);
    }
    
    public static Map<String, Object> getParametersEndWith(ServletRequest request, String str) {
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (str == null) {
            str = "";
        }
        while ((paramNames != null) && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (paramName.endsWith(str)) {
                String[] values = request.getParameterValues(paramName);
                if ((values == null) || (values.length == 0)) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(paramName, values);
                } else {
                    params.put(paramName, values[0]);
                }
            }
        }
        return params;
    }
    
    
}
