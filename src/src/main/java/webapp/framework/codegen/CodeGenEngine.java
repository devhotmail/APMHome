package webapp.framework.codegen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import webapp.framework.web.WebUtil;

/**
 *
 * @author wujianb
 */
public class CodeGenEngine {
    public static enum ToolbarPosition{
        Top, Bottom, TopAndBottom;
    }
    
    public static String rootPath = System.getProperty("user.dir");
    public static String resourceFilePath = rootPath + "/src/main/resources/";
    public static String daoPath = rootPath + "/src/main/java/myapp/dao/";
    public static String viewControllerPath = rootPath + "/src/main/java/myapp/view/entitycontroller/";
    public static String viewPagePath = rootPath + "/src/main/webapp/portal/";
    public static String basePackage = "myapp";
    public static boolean dataTableEnableFilter = true;
    public static boolean dataTableEnableSort = true;
    public static ToolbarPosition dataTableToolbarPosition = ToolbarPosition.Top;
    public static boolean dataTableToolbarIconOnly = true;
 
   public static boolean isNativeSQL = false;
    
    private static String templateFilePath;
     
    public static Map<String, Object> getTemplateParams(Class clazz){
        String entityName = clazz.getSimpleName();
        String managedBean = WebUtil.toLowerCaseFirstOne(entityName).concat("Controller");
        
        Map<String, Object> params = doGetTemplateParams(entityName, managedBean);
        params.put("fieldInfoList", getFieldInfoList(clazz, entityName, managedBean));
        
        return params;
    }
    
    public static Map<String, Object> doGetTemplateParams(String entityName, String jsfBeanName){
        templateFilePath = resourceFilePath + "codegen/";
        
        HashMap<String, Object> params = new HashMap<String, Object>();
        
        params.put("basePackage", basePackage);
        
        params.put("entityName", entityName);
        params.put("viewTitle", String.format("#{msg.%s}", entityName));
        params.put("lazyModel", String.format("#{%s.lazyModel}", jsfBeanName));
        params.put("itemId", "#{item.id}");
        params.put("selected", String.format("#{%s.selected}", jsfBeanName));
        params.put("selectedNotNull", String.format("#{%s.selected != null}", jsfBeanName));
        params.put("dataTableEnableFilter", dataTableEnableFilter);
        params.put("msgFilterTo", "#{msg.filterTo}");
        params.put("msgAll", "#{msg.All}");
        params.put("msgTrue2Yes", "#{msg.True2Yes}");
        params.put("msgFalse2No", "#{msg.False2No}");
        params.put("msgSelectOne", "#{msg.SelectOneMessage}");
        
        params.put("noRecordFound", "#{msg.noRecordFound}");
        params.put("recordCountLabel", "#{msg.recordCount}");
        params.put("rowsPerPage", "#{msg.RowsPerPage}");
        
        params.put("recordCountValue", String.format("#{%s.lazyModel.rowCount}", jsfBeanName));
        params.put("exportToXLS", "#{msg.exportToXLS}");
        params.put("deleteConfirmation", "<p:confirm header=\"#{msg.DeleteConformation}\" message=\"#{msg.DeleteConformationMsg}\" icon=\"ui-icon-alert\"/>");

        if(dataTableToolbarIconOnly)
            params.put("buttonIconTitle", "title");
        else
            params.put("buttonIconTitle", "value");
        
        params.put("buttonView", "#{msg.View}");
        params.put("buttonEdit", "#{msg.Edit}");
        params.put("buttonCreate", "#{msg.Create}");
        params.put("buttonDelete", "#{msg.Delete}");
        params.put("buttonSave", "#{msg.Save}");
        params.put("buttonCancel", "#{msg.Cancel}");
        params.put("buttonClose", "#{msg.Close}");
        
        params.put("createListner", String.format("#{%s.prepareCreate}", jsfBeanName));
        params.put("viewListner", String.format("#{%s.prepareView}", jsfBeanName));
        params.put("deleteListner", String.format("#{%s.prepareDelete}", jsfBeanName));
        params.put("deleteAction", String.format("#{%s.delete}", jsfBeanName));
        params.put("editListner", String.format("#{%s.prepareEdit}", jsfBeanName));
        params.put("saveListner", String.format("#{%s.save}", jsfBeanName));
        params.put("buttonDisabled", String.format("#{empty %s.selected}", jsfBeanName));
        params.put("renderDeleteButton", String.format("#{%s.deleteAction}", jsfBeanName));
        params.put("crudActionName", String.format("#{%s.CRUDActionName}", jsfBeanName));
        params.put("onFilter", String.format("#{%s.onFilter}", jsfBeanName));

        if(ToolbarPosition.Top == dataTableToolbarPosition)
            params.put("toolbarPosition", "top");
        else if(ToolbarPosition.Bottom == dataTableToolbarPosition)
            params.put("toolbarPosition", "bottom");
        else
            params.put("toolbarPosition", "both");
        
        params.put("toolbarIconOnly", dataTableToolbarIconOnly);

        return params;
    }
    
    private static List<FieldInfo> getFieldInfoList(Class clazz, String entityName, String managedBean){
        managedBean = managedBean + "Crud";
        FieldInfo.dataTableEnableFilter = dataTableEnableFilter;
        FieldInfo.dataTableEnableSort = dataTableEnableSort;
        
        List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();

        for (Field f : clazz.getDeclaredFields()) {
            PropertyDescriptor prop = BeanUtils.getPropertyDescriptor(clazz, f.getName());
            if(prop==null || prop.getWriteMethod()==null){
                //System.out.println("********************  skipped, f="+f.getName());
                continue;
            }
            
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfoList.add(fieldInfo);
            
            fieldInfo.isNativeSQL = isNativeSQL;
            fieldInfo.entityName = entityName;
            fieldInfo.setManagedBean(managedBean);
            
            fieldInfo.setFieldName(f.getName());
            fieldInfo.setBlob(false);
            fieldInfo.setRelationshipMany(false);
            
            String fieldType = f.getType().toString();
            fieldInfo.setFieldType(fieldType);
            fieldInfo.setDateTime(fieldType.contains("Date"));
            fieldInfo.setBooleanField(fieldType.contains("oolean"));
                
            for (Annotation ann : f.getAnnotations()) {
                String annText = ann.toString();
                if(annText.contains("NotNull") || annText.contains("optional=false"))
                    fieldInfo.setRequired(true);
                
                if(annText.contains("ManyToOne")){
                    fieldInfo.setRelationshipOne(true);
                    //fieldDesc.setValuesGetter(JsfUtil.toLowerCaseFirstOne(f.getType().getSimpleName().concat("Controller.itemsAvailableSelectOne")));
                    fieldInfo.setRefObject(WebUtil.toLowerCaseFirstOne(f.getType().getSimpleName()));
                }        
                else if(annText.contains("OneToMany")){
                    fieldInfoList.remove(fieldInfo);
                }        
            }
            
        }
        
        return fieldInfoList;
    }

    private static List<FieldInfo> getFieldInfoListFromSqlResult(String jsfBeanName, Map<String, Object> sqlRowData){
        FieldInfo.dataTableEnableFilter = dataTableEnableFilter;
        FieldInfo.dataTableEnableSort = dataTableEnableSort;
        
        List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();

        for(Map.Entry<String, Object> fieldData: sqlRowData.entrySet()){
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfoList.add(fieldInfo);
            
            fieldInfo.isNativeSQL = isNativeSQL;
            
            fieldInfo.entityName = "NA";
            fieldInfo.setManagedBean(jsfBeanName);
            
            fieldInfo.setFieldName(fieldData.getKey());
            fieldInfo.setBlob(false);
            fieldInfo.setRelationshipMany(false);

            String fieldType = "String";
            try{
                fieldType = fieldData.getValue().getClass().toString();
            }
            catch(Exception ex){
            }
            
            fieldInfo.setFieldType(fieldType);
            fieldInfo.setDateTime(fieldType.contains("Date"));
            fieldInfo.setBooleanField(fieldType.contains("oolean"));
        }
        
        return fieldInfoList;
    }
    
    public static boolean mkdirs(String path) {
        File dirs = new File(path);
        boolean flag = true;
        if (!dirs.exists() || !dirs.isDirectory()) {
            flag = dirs.mkdirs();
        }
        return flag;
    }

    private static String getBetterName(String inputName){
        boolean isFirstChar = true;
        String result = "";
        for(Character ch: inputName.toCharArray()){
            if(isFirstChar){
                result = result + Character.toUpperCase(ch);
                isFirstChar = false;
            }
            else if(Character.isUpperCase(ch)){
                result = result.concat(" ")+Character.toUpperCase(ch);
            }
            else
                result = result + ch;
        }
        return result;
    }
    
    public static boolean updateMessageBundleFile(Class clazz){
        return updateMessageBundleFile(clazz, null);
    }
        
    private static boolean updateMessageBundleFile(Class clazz, Map<String, Object> params){
        if(params==null) params = getTemplateParams(clazz);

        try {
            FileWriter fw = new FileWriter(resourceFilePath + "bundle.properties", true);
            FileWriter fwZh = new FileWriter(resourceFilePath + "bundle_zh.properties", true);
            
            String entityName = clazz.getSimpleName();
            String entityNameTitle = getBetterName(entityName);
            fw.write(entityName+"Title="+entityNameTitle+"\n");
            fwZh.write(entityName+"Title="+entityNameTitle+"\n");

            for(FieldInfo field: (List<FieldInfo> )params.get("fieldInfoList")){
                String fieldName = getBetterName(field.getFieldName());
                //fw.write(entityName+"Field_"+field.getFieldName()+"="+fieldName+"\n");
                //fwZh.write(entityName+"Field_"+field.getFieldName()+"="+fieldName+"\n");
                fw.write(field.getFieldName()+"="+fieldName+"\n");
                fwZh.write(field.getFieldName()+"="+fieldName+"\n");
            }
            
            fw.close();
            fwZh.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static boolean updateMessageBundleFileForNativeSQL(Map<String, Object> params){
        try {
            FileWriter fw = new FileWriter(resourceFilePath + "bundle.properties", true);
            FileWriter fwZh = new FileWriter(resourceFilePath + "bundle_zh.properties", true);
            
            for(FieldInfo field: (List<FieldInfo> )params.get("fieldInfoList")){
                String fieldName = getBetterName(field.getFieldName());
                fw.write(field.getFieldName()+"="+fieldName+"\n");
                fwZh.write(field.getFieldName()+"="+fieldName+"\n");
            }
            
            fw.close();
            fwZh.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    private static boolean generateFile(Class clazz, Map<String, Object> params, String templateFilePath, String templateFileName, String outputFilePath,
            String outputFileName) {
        if(params==null) params = getTemplateParams(clazz);
        
        try {
            Configuration cfg = new Configuration();
            cfg.setDirectoryForTemplateLoading(new File(templateFilePath));
            cfg.setDefaultEncoding("UTF-8");
            Template template = cfg.getTemplate(templateFileName);

            mkdirs(outputFilePath);
            File file = new File(outputFilePath + outputFileName);
            
            //Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            StringWriter out = new StringWriter();
            template.process(params, out);
            String result = out.getBuffer().toString();
            result = result.replaceAll("<~", "#{").replaceAll("~>", "}");
            
            FileWriter fw = new FileWriter(outputFilePath + outputFileName);    
            fw.write(result);
            fw.close();
            out.flush();
            out.close();
        } catch (TemplateException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static void generateDaoCode(Class clazz, Map<String, Object> params){
        if(params==null) params = getTemplateParams(clazz);
        
        CodeGenEngine.generateFile(clazz, params, templateFilePath, "dao.ftl", daoPath, clazz.getSimpleName().concat("Repository.java"));
    }

    public static void generateControllerCode(Class clazz, Map<String, Object> params){
        if(params==null) params = getTemplateParams(clazz);

        CodeGenEngine.generateFile(clazz, params, templateFilePath, "controller.ftl", viewControllerPath, clazz.getSimpleName().concat("CrudController.java"));
    }
    
    public static void generateViewPageCode(Class clazz, Map<String, Object> params){
        if(params==null) params = getTemplateParams(clazz);
        
        String outputPath = viewPagePath+WebUtil.toLowerCaseFirstOne(clazz.getSimpleName())+"/";
        //String outputPath = templateFilePath+clazz.getSimpleName()+"/";
        mkdirs(outputPath);
        
        CodeGenEngine.generateFile(clazz, params, templateFilePath, "edit.ftl", outputPath, "Edit.xhtml");
        CodeGenEngine.generateFile(clazz, params, templateFilePath, "list.ftl", outputPath, "List.xhtml");
        CodeGenEngine.generateFile(clazz, params, templateFilePath, "view.ftl", outputPath, "View.xhtml");
    }

    public static void generateAllCode(Class clazz){
        Map<String, Object> params = getTemplateParams(clazz);

        updateMessageBundleFile(clazz, params);
        generateViewPageCode(clazz, params);
        generateDaoCode(clazz, params);
        generateControllerCode(clazz, params);
    }

    public static void generateAllCode(Class[] classes){
        for (Class clazz : classes) {
            CodeGenEngine.generateAllCode(clazz);
        }
    }
    public static void generateAllCode(String[] classNames){
        for (String className : classNames) {
            try {
                CodeGenEngine.generateAllCode(Class.forName(className).newInstance().getClass());
            } catch (ClassNotFoundException ex) {
                System.err.println("****** class not found: "+className);
            } catch (InstantiationException ex) {
                System.err.println("****** class InstantiationException: "+className);
            } catch (IllegalAccessException ex) {
                System.err.println("****** class IllegalAccessException: "+className);
            }
        }
    }

    public static Map<String, Object> getTemplateParamsForNativeSQL(String jsfBeanName, Map<String, Object> sqlRowData){
        String entityName = jsfBeanName;
        String managedBeanName = WebUtil.toLowerCaseFirstOne(jsfBeanName);

        Map<String, Object> params = doGetTemplateParams(entityName, managedBeanName);
        params.put("fieldInfoList", getFieldInfoListFromSqlResult(jsfBeanName, sqlRowData));
        
        return params;
    }
        
    public static void generateViewPageCodeForNativeSQL(String viewName, String jsfBeanName, Map<String, Object> sqlRowData, Map<String, Object> params){
        if(params==null) params = getTemplateParamsForNativeSQL(jsfBeanName, sqlRowData);
        
        String outputPath = viewPagePath;
        mkdirs(outputPath);
        
        CodeGenEngine.generateFile(null, params, templateFilePath, "listNativeSQL.ftl", outputPath, viewName+".xhtml");
    }
    
    public static void generateControllerCodeForNativeSQL(String sql, String jsfBeanName, Map<String, Object> sqlRowData, Map<String, Object> params){
        if(params==null) params = getTemplateParamsForNativeSQL(jsfBeanName, sqlRowData);
        params.put("sql", sql);
        
        CodeGenEngine.generateFile(null, params, templateFilePath, "controllerNativeSQL.ftl", viewControllerPath, jsfBeanName+".java");
    }
    
}
