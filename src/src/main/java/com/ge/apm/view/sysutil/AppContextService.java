package com.ge.apm.view.sysutil;

import java.io.Serializable;
import java.util.Enumeration;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.servlet.http.HttpServletRequestWrapper;

@ManagedBean(name="appContextService")
@ApplicationScoped
public class AppContextService implements Serializable{
    private static final long serialVersionUID = 1L;

    private static String sqlPagingTemplate;
    public static String getSqlPagingTemplate() {
        return sqlPagingTemplate;  // MySQL: " limit :#_pageStart,:#_pageSize"  PostgreSql: " limit :#_pageSize offset :#_pageStart"
    }
    public static void setSqlPagingTemplate(String sqlPagingTemplate) {
        AppContextService.sqlPagingTemplate = sqlPagingTemplate;
    }

    private static String fileUploadFolder;

    public static String getFileUploadFolder() {
        return fileUploadFolder;
    }

    public static void setFileUploadFolder(String fileUploadFolder) {
        AppContextService.fileUploadFolder = fileUploadFolder;
    }
    
    public String getQueryStringWithLocal(HttpServletRequestWrapper request, String locale){
        String queryStr = "locale="+locale;
        Enumeration paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()){
            String paramName = paramNames.nextElement().toString();
            if("locale".equals(paramName)) continue;
            
            String paramValue = request.getParameter(paramName);
            queryStr = String.format("%s&%s=%s", queryStr, paramName, paramValue);
        }

        return queryStr;
    }
}
