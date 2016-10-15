package com.ge.apm.view.sysutil;

import com.ge.apm.domain.UserAccount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.servlet.http.HttpServletRequestWrapper;
import org.joda.time.DateTime;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.util.TimeUtil;

@ManagedBean(name="appContextService")
@ApplicationScoped
public class AppContextService implements Serializable{
    private static final long serialVersionUID = 1L;

    private static Integer maxIdleMinutes;
    private static final ConcurrentHashMap<Integer,UserAccount> activeUsers = new ConcurrentHashMap<Integer,UserAccount>();

    private static String sqlPagingTemplate;
    public static String getSqlPagingTemplate() {
        return sqlPagingTemplate;  // MySQL: " limit :#_pageStart,:#_pageSize"  PostgreSql: " limit :#_pageSize offset :#_pageStart"
    }
    public static void setSqlPagingTemplate(String sqlPagingTemplate) {
        AppContextService.sqlPagingTemplate = sqlPagingTemplate;
    }

    private static String sql4UserOnline;
    private static String sql4UserOffline;
    public static String getSql4UserOnline() {
        return sql4UserOnline;
    }
    public static void setSql4UserOnline(String sql4UserOnline) {
        AppContextService.sql4UserOnline = sql4UserOnline;
    }
    public static String getSql4UserOffline() {
        return sql4UserOffline;
    }
    public static void setSql4UserOffline(String sql4UserOffline) {
        AppContextService.sql4UserOffline = sql4UserOffline;
    }
    
    public static void setMaxIdleMinutes(Integer _maxIdleMinutes) {
        maxIdleMinutes = _maxIdleMinutes;
    }
    
    private static String fileUploadFolder;

    public static String getFileUploadFolder() {
        return fileUploadFolder;
    }

    public static void setFileUploadFolder(String fileUploadFolder) {
        AppContextService.fileUploadFolder = fileUploadFolder;
    }
    
    //will be called periodically
    public static void checkActiveUsers(){
        DateTime now = TimeUtil.timeNow();
        Collection<UserAccount> users = activeUsers.values();

        for(UserAccount user: users){
            if(user.getLastActiveTime().plusMinutes(maxIdleMinutes).isBefore(now))
                removeActiveUser(user);
        }
    }

    public void addActiveUser(){
        UserAccount user = UserContextService.getCurrentUserAccount();
        addActiveUser(user);
    }

    
    public static void addActiveUser(UserAccount user){
        if(user==null) return;

        if(activeUsers.get(user.getId())==null){
            //changed from offline to online
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", user.getId());
            NativeSqlUtil.execute(sql4UserOnline, params);
        }
        user.setLastActiveTime(TimeUtil.timeNow());
        activeUsers.put(user.getId(), user);
    }

    public static void removeActiveUser(UserAccount user){
        if(user==null) return;
        activeUsers.remove(user.getId());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", user.getId());
        NativeSqlUtil.execute(sql4UserOffline, params);
    }
    
    public static Collection<UserAccount> getActiveUsers(){
        return activeUsers.values();
    }

    public List<UserAccount> getActiveUserList(){
        return new ArrayList<UserAccount>(activeUsers.values());
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
