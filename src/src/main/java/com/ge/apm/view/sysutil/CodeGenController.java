package com.ge.apm.view.sysutil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import webapp.framework.broker.SiBroker;
import webapp.framework.codegen.CodeGenEngine;
import webapp.framework.web.WebUtil;

@ManagedBean
@ApplicationScoped
public class CodeGenController implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rootPath = System.getProperty("user.dir");
    private String resourceFilePath = "/src/main/resources/";
    private String daoPath = "/src/main/java/com/ge/apm/dao/";
    private String viewControllerPath = "/src/main/java/com/ge/apm/view/";
    private String viewPagePath = "/src/main/webapp/ui/";    
    private String basePackage = "com.ge.apm";
    private Boolean dataTableEnableFilter = false;
    private Boolean dataTableEnableSort = false;
    private Boolean dataTableToolbarIconOnly = false;
    private Integer dataTableToolbarPosition = 0;
    private String entityClassNames;
    
    private Boolean isUpdateMessageBundle = true;
    private Boolean isGenerateViewPageCode = true;
    private Boolean isGenerateDaoCode = true;
    private Boolean isGenerateControllerCode = true;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getResourceFilePath() {
        return resourceFilePath;
    }

    public void setResourceFilePath(String resourceFilePath) {
        this.resourceFilePath = resourceFilePath;
    }

    public String getDaoPath() {
        return daoPath;
    }

    public void setDaoPath(String daoPath) {
        this.daoPath = daoPath;
    }

    public String getViewControllerPath() {
        return viewControllerPath;
    }

    public void setViewControllerPath(String viewControllerPath) {
        this.viewControllerPath = viewControllerPath;
    }

    public String getViewPagePath() {
        return viewPagePath;
    }

    public void setViewPagePath(String viewPagePath) {
        this.viewPagePath = viewPagePath;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public Boolean getDataTableEnableFilter() {
        return dataTableEnableFilter;
    }

    public void setDataTableEnableFilter(Boolean dataTableEnableFilter) {
        this.dataTableEnableFilter = dataTableEnableFilter;
    }

    public Boolean getDataTableEnableSort() {
        return dataTableEnableSort;
    }

    public void setDataTableEnableSort(Boolean dataTableEnableSort) {
        this.dataTableEnableSort = dataTableEnableSort;
    }

    public Boolean getDataTableToolbarIconOnly() {
        return dataTableToolbarIconOnly;
    }

    public void setDataTableToolbarIconOnly(Boolean dataTableToolbarIconOnly) {
        this.dataTableToolbarIconOnly = dataTableToolbarIconOnly;
    }

    public Integer getDataTableToolbarPosition() {
        return dataTableToolbarPosition;
    }

    public void setDataTableToolbarPosition(Integer dataTableToolbarPosition) {
        this.dataTableToolbarPosition = dataTableToolbarPosition;
    }

    public String getEntityClassNames() {
        return entityClassNames;
    }

    public void setEntityClassNames(String entityClassNames) {
        this.entityClassNames = entityClassNames;
    }

    public Boolean getIsUpdateMessageBundle() {
        return isUpdateMessageBundle;
    }

    public void setIsUpdateMessageBundle(Boolean isUpdateMessageBundle) {
        this.isUpdateMessageBundle = isUpdateMessageBundle;
    }

    public Boolean getIsGenerateViewPageCode() {
        return isGenerateViewPageCode;
    }

    public void setIsGenerateViewPageCode(Boolean isGenerateViewPageCode) {
        this.isGenerateViewPageCode = isGenerateViewPageCode;
    }

    public Boolean getIsGenerateDaoCode() {
        return isGenerateDaoCode;
    }

    public void setIsGenerateDaoCode(Boolean isGenerateDaoCode) {
        this.isGenerateDaoCode = isGenerateDaoCode;
    }

    public Boolean getIsGenerateControllerCode() {
        return isGenerateControllerCode;
    }

    public void setIsGenerateControllerCode(Boolean isGenerateControllerCode) {
        this.isGenerateControllerCode = isGenerateControllerCode;
    }
    
    @PostConstruct
    protected void init() {
    }
    
    public void generateCode(){
        CodeGenEngine.isNativeSQL = false;
        
        CodeGenEngine.rootPath = rootPath;
        
        //configure paths
        CodeGenEngine.resourceFilePath = rootPath + resourceFilePath;
        CodeGenEngine.daoPath = CodeGenEngine.rootPath + daoPath;
        CodeGenEngine.viewControllerPath = CodeGenEngine.rootPath + viewControllerPath;
        CodeGenEngine.viewPagePath = CodeGenEngine.rootPath + viewPagePath; 

        //set package name
        CodeGenEngine.basePackage = basePackage;

        //set view xhtml file options
        CodeGenEngine.dataTableEnableFilter = dataTableEnableFilter;
        CodeGenEngine.dataTableEnableSort = dataTableEnableSort;
        CodeGenEngine.dataTableToolbarIconOnly = dataTableToolbarIconOnly;
        switch (dataTableToolbarPosition) {
            case 0:
                CodeGenEngine.dataTableToolbarPosition = CodeGenEngine.ToolbarPosition.Top;
                break;
            case 1:
                CodeGenEngine.dataTableToolbarPosition = CodeGenEngine.ToolbarPosition.Bottom;
                break;
            default:
                CodeGenEngine.dataTableToolbarPosition = CodeGenEngine.ToolbarPosition.TopAndBottom;
                break;
        }

        // entities to be processed
        String[] entityClassNameList = entityClassNames.replaceAll("\r", "").split("\n");
        for (String entityClassName : entityClassNameList) {
            String className = basePackage+".domain." + entityClassName;
            if(className==null || "".equals(className.trim())) continue;

            try{
                Class clazz = Class.forName(className).newInstance().getClass();
                
                //start to generate code
                Map<String, Object> params = CodeGenEngine.getTemplateParams(clazz);
                if( isUpdateMessageBundle )
                    CodeGenEngine.updateMessageBundleFile(clazz);
                
                if( isGenerateViewPageCode )
                    CodeGenEngine.generateViewPageCode(clazz, params);
                if( isGenerateDaoCode )
                    CodeGenEngine.generateDaoCode(clazz, params);
                if( isGenerateControllerCode )
                    CodeGenEngine.generateControllerCode(clazz, params);
                
                WebUtil.addSuccessMessage("Code Generated for Entity Class:"+className);
            }
            catch(Exception ex){
                WebUtil.addErrorMessage("Code Generation Failed, error: "+ex.getMessage());
                ex.printStackTrace();
                return;
            }
        }
    }
    
    private String jsfBeanName;
    private String sql;
    private String viewName;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getJsfBeanName() {
        return jsfBeanName;
    }

    public void setJsfBeanName(String jsfBeanName) {
        this.jsfBeanName = jsfBeanName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public void generateCodeForNativeSQL(){
        CodeGenEngine.isNativeSQL = true;
        CodeGenEngine.rootPath = rootPath;
        
        //configure paths
        CodeGenEngine.resourceFilePath = rootPath + resourceFilePath;
        CodeGenEngine.viewControllerPath = CodeGenEngine.rootPath + viewControllerPath;
        CodeGenEngine.viewPagePath = CodeGenEngine.rootPath + viewPagePath; 

        //set package name
        CodeGenEngine.basePackage = basePackage;

        //set view xhtml file options
        CodeGenEngine.dataTableEnableFilter = dataTableEnableFilter;
        CodeGenEngine.dataTableEnableSort = dataTableEnableSort;
        CodeGenEngine.dataTableToolbarIconOnly = dataTableToolbarIconOnly;
        switch (dataTableToolbarPosition) {
            case 0:
                CodeGenEngine.dataTableToolbarPosition = CodeGenEngine.ToolbarPosition.Top;
                break;
            case 1:
                CodeGenEngine.dataTableToolbarPosition = CodeGenEngine.ToolbarPosition.Bottom;
                break;
            default:
                CodeGenEngine.dataTableToolbarPosition = CodeGenEngine.ToolbarPosition.TopAndBottom;
                break;
        }

        // sql to be processed
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("_sql", sql);
        List<Map<String, Object>> sqlResult = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
        if(sqlResult.size()<1){
            WebUtil.addErrorMessage("Please make sure the SQL returns at lease 1 row.");
            return;
        }
        
        params.clear();
        params = CodeGenEngine.getTemplateParamsForNativeSQL(jsfBeanName, sqlResult.get(0));
        
        if( isUpdateMessageBundle )
            CodeGenEngine.updateMessageBundleFileForNativeSQL(params);
                
        if( isGenerateViewPageCode )
            CodeGenEngine.generateViewPageCodeForNativeSQL(this.viewName, this.jsfBeanName, sqlResult.get(0), params);

        if( isGenerateControllerCode )
            CodeGenEngine.generateControllerCodeForNativeSQL(sql, this.jsfBeanName, sqlResult.get(0), params);
                
        WebUtil.addSuccessMessage("Code Generated for this SQL statement");
    }

}
