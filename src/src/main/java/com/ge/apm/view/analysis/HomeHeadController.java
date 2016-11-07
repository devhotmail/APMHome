package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.broker.SiBroker;
import webapp.framework.codegen.CodeGenEngine;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;

import webapp.framework.dao.NativeSqlUtil;
import org.apache.log4j.Logger;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@ViewScoped
public class HomeHeadController extends SqlConfigurableChartController {
    private double anualCost = 0.0;
    private int revenue = 0; 
    private static final Logger logger = Logger.getLogger(HomeHeadController.class);
    private String logStr;
    
    // chart parameters
    private int targetYear = 2015;
    private int hospitalId = 0;
    private HashMap<String, Object> sqlParams = new HashMap<>();
    
    private BarChartModel barAnnualRevenue = new BarChartModel();
    private PieChartModel pieAnnualRevenue = new PieChartModel();
    
    
    
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
    
    public BarChartModel getBarAnnualRevenue() {
        createAnnualBar();
        return barAnnualRevenue;
    }
    
    public PieChartModel getPieAnnualRevenue() {
        createAnnualPie();
        return pieAnnualRevenue;
    }
    
    private void createAnnualPie() {  
         
        pieAnnualRevenue.set("Brand 1", 540);
        pieAnnualRevenue.set("Brand 2", 325);
        pieAnnualRevenue.set("Brand 3", 702);
        pieAnnualRevenue.set("Brand 4", 421);
         
        pieAnnualRevenue.setTitle("Simple Pie");
        pieAnnualRevenue.setLegendPosition("w");
    }
    
    private List<Map<String, Object>> getAnnualRevenue() {
        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        this.logStr = "Current User Account is " +  UserContextService.getCurrentUserAccount().getName() + 
                ". Hospital id is " + hospitalId + ".";
        logger.debug(this.logStr);
        
        HashMap<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("targetYear", this.targetYear);
        
        List<Map<String, Object>> result = runSQLForAnnualRevenu(sqlParams);
        
        return result;
    }
    
    private List<Map<String, Object>> runSQLForAnnualRevenu(HashMap<String, Object> sqlParams) {
        String sql = "select a.function_type as type, "
            + "sum(r.price_amount) as revenue "
            + "from asset_info a left join asset_clinical_record r "
            + "on a.id = r.asset_id "
            + "and a.hospital_id = :#hospitalId and extract(year from r.exam_date) = :#targetYear "
            + "group by a.function_type "
            + "order by a.function_type asc;";
        logger.debug(sql);
        
        List<Map<String, Object>> result = NativeSqlUtil.queryForList(sql, sqlParams);
        return result;
    }

    private List<Map<String, Object>> getAnnualDep() {
        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        this.logStr = "Current User Account is " +  UserContextService.getCurrentUserAccount().getName() + 
                ". Hospital id is " + hospitalId + ".";
        logger.debug(this.logStr);
        
        HashMap<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("hospitalId", hospitalId);
        
        List<Map<String, Object>> result = runSQLForAnnualDep(sqlParams);
        
        return result;
    }
    
    private List<Map<String, Object>> runSQLForAnnualDep(HashMap<String, Object> sqlParams) {
        String sql = "select a.function_type as type, "
                    + "sum(d.deprecate_amount) as cost "
                    + "from asset_info a left join asset_depreciation d "
                    + "on a.id = d.asset_id and a.hospital_id = :#hospitalId "
                    + "group by a.function_type order by a.function_type asc;";
                
        logger.debug(sql);
        
        List<Map<String, Object>> result = NativeSqlUtil.queryForList(sql, sqlParams);
        return result;
    }

    private List<Map<String, Object>> getAnnualWorkOrderCost() {
        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        this.logStr = "Current User Account is " +  UserContextService.getCurrentUserAccount().getName() + 
                ". Hospital id is " + hospitalId + ".";
        logger.debug(this.logStr);
        
        HashMap<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("targetYear", this.targetYear);
        
        List<Map<String, Object>> result = runSQLForAnnualWorkOrderCost(sqlParams);
        
        return result;
    }
    
    private List<Map<String, Object>> runSQLForAnnualWorkOrderCost(HashMap<String, Object> sqlParams) {
        String sql = "select a.function_type as type, "
                    + "sum(w.total_price) as cost "
                    + "from asset_info a left join work_order w "
                    + "on w.asset_id = a.id and a.hospital_id = :#hospitalId "
                    + "and extract(year from w.request_time) = :#targetYear "
                    + "and w.is_closed = true "
                    + "group by a.function_type order by a.function_type asc;";
  
        logger.debug(sql);
        
        List<Map<String, Object>> result = NativeSqlUtil.queryForList(sql, sqlParams);
        return result;
    }
    
    private void createAnnualBar() {
//        // revenue
//        List<Map<String, Object>> revenue = getAnnualRevenue();
//        
//        if (revenue.size() == 0) {
//            logger.error("No revenue result was select.");
//            return;
//        }
//        
//        String label = new String("revenue");
//        initBarAssetROIByType(label, revenue);
//        
//        // asset depreciation
//        List<Map<String, Object>> dep = getAnnualDep();
//        
//        if (dep.size() == 0) {
//            logger.error("No dep result was select.");
//            return;
//        }
//        
//        // work order cost
//        List<Map<String, Object>> cost = getAnnualWorkOrderCost();
//        
//        if (cost.size() == 0) {
//            logger.error("No work order cost result was select.");
//            return;
//        }
//        
//        for (Map<String, Object> item : cost) {
//  
//            System.out.print(item.get("type") + " : " + item.get("cost").getClass().getName());
//        }
//        
//        String label = new String("revenue");
//        initBarAssetROIByType(label, result);        

        barAnnualRevenue.setLegendPosition("ne");
         
        Axis xAxis = barAnnualRevenue.getAxis(AxisType.X);
        xAxis.setLabel("Assets Type");
         
        Axis yAxis = barAnnualRevenue.getAxis(AxisType.Y);
        yAxis.setLabel("CNY");
        
        
        
//        yAxis.setMin(0);
//        yAxis.setMax(500);

        String label = new String("re");    
        
        sqlParams.put("hospitalId", hospitalId);
        sqlParams.put("targetYear", this.targetYear);

        // get revenue
        sql = "select a.function_type as key, "
            + "sum(r.price_amount) as value "
            + "from asset_info a left join asset_clinical_record r "
            + "on a.id = r.asset_id "
            + "and a.hospital_id = :#hospitalId and extract(year from r.exam_date) = :#targetYear "
            + "group by a.function_type "
            + "order by a.function_type asc;";
        
        List<Map<String, Object>> r = prepareData(sql, sqlParams);
        label = "re";
        drawBar(label, r);
        
        // get asset deprecate value
        sql = "select a.function_type as key, "
            + "sum(d.deprecate_amount) as value "
            + "from asset_info a left join asset_depreciation d "
            + "on a.id = d.asset_id and a.hospital_id = :#hospitalId "
            + "group by a.function_type order by a.function_type asc;";
        
        List<Map<String, Object>> d = prepareData(sql, sqlParams);
//        label = "dep";
//        drawBar(label, d);
        
        // get maintenance cost
        sql = "select a.function_type as key, "
            + "sum(w.total_price) as value "
            + "from asset_info a left join work_order w "
            + "on w.asset_id = a.id and a.hospital_id = :#hospitalId "
            + "and extract(year from w.request_time) = :#targetYear "
            + "and w.is_closed = true "
            + "group by a.function_type order by a.function_type asc;";
        
        List<Map<String, Object>> w = prepareData(sql, sqlParams);
        
//        label = "work";
//        drawBar(label, w);
        
        // Calcuate profile = revenue - deprecate - maintenance cost
        int index = 0;
        List<Map<String, Object>> profit = new ArrayList<>();
        for (Map<String, Object> item : r) {
            String key = item.get("key").toString();  
            
            double value = (Double) item.get("value");
            if (d.get(index).get("key") == null) {
                logger.debug("Cannot get key: " + key + "in deprecate sql query.");             
            }
            else {
                 value = value - (Double) d.get(index).get("value");
            }
            
            if (w.get(index).get("key") == null) {
                logger.debug("Cannot get key: " + key + "in maintenance sql query.");             
            }
            else {
                 value = value - (Double) w.get(index).get("value");
            }
            
            HashMap<String, Object> node = new HashMap<>();
            node.put("key", key);
            node.put("value", value);
            profit.add(node);
            index++;
        }
        label = "profit";
        printList(profit);
        drawBar(label, profit);
    }
    
    private List<Map<String, Object>> prepareData(String sql, HashMap<String, Object> sqlParams) {
        logger.debug(sql);
        
        List<Map<String, Object>> result = NativeSqlUtil.queryForList(sql, sqlParams);
        
        if (result.size() == 0) {
            Map<String, Object> item = new HashMap<>();
            item.put("key", 1);
            item.put("value", 0.1);
            result.add(item);
        }
        else {
            for (Map<String, Object> item : result) {
                checkNull(item, "Double");
            }
        }
        
          
        return result;
    }
    
    private void printList (List<Map<String, Object>> result) {
        for (Map<String, Object> item : result) {
            if (item.get("value") == null) {

                System.out.print("Print result, key = " + item.get("key").toString() + ", value is null");
            }
            else {
                System.out.print("Print result, key = " + item.get("key").toString() + ", value = " + item.get("value").toString() + "\n");
            }
        }
    }
    
    private void drawBar(String label, List<Map<String, Object>> result) {
        ChartSeries revenue = new ChartSeries();
        revenue.setLabel(label);
      
        for (Map<String, Object> item : result) {
            revenue.set(item.get("key").toString(), (Double) item.get("value"));
        }
   
        barAnnualRevenue.addSeries(revenue);
    }
    
    private void checkNull(Map<String, Object> item, String targetType) {        
        if (item.get("value") == null) {
            
            // add targetType, if needed.
            // convert null to associated empty value for drawing
            switch (targetType) {
                
                case "Double":
                    item.put("value", 0.1);
                default:
                    ;
            }            
        }
    }
    
    private void initBarAssetROIByType(String label, List<Map<String, Object>> result) {

        ChartSeries revenue = new ChartSeries();
        revenue.setLabel(label);
      
        for (Map<String, Object> item : result) {
            revenue.set(item.get("type").toString(), (Double) item.get("revenue"));
        }
   
        barAnnualRevenue.addSeries(revenue);

    }
    
    private void getAnualAssetCost() {
        
    }
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
        getAnnualRevenue();
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
