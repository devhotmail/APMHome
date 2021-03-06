package com.ge.apm.view.sysutil;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.servlet.http.HttpServletRequestWrapper;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;
import webapp.framework.web.mvc.SqlConfigurableDataController;
import webapp.framework.web.service.DbMessageSource;

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
    
    public void reloadI18nMessage(){
        DbMessageSource.reLoadMessages();
    }

    public void reloadChartConfig(){
        SqlConfigurableChartController.reLoadChartConfig();
    }

    public void reloadDataTableConfig(){
        SqlConfigurableDataController.reLoadDataTableConfig();
    }
    
     public String getStringHeader(String str, int length) {
        if (null == str) {
            return null;
        }
        else if (str.length() <= length) {
            return str;
        }else {
            return str.substring(0,length)+"...";
        }
    }
     
     public Date getCurentDate(){
         return TimeUtil.now();
     }
}
