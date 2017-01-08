/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212595360
 */
@Controller
public class DataGetAndPushController {
    
    @RequestMapping(value = "/dataget/{tablename}", method = RequestMethod.GET )
    public @ResponseBody Object getDataRow(@PathVariable String tablename, 
            @RequestParam(required=false) String page, 
            @RequestParam(required=false) String size) {
        if (tablename == null) return null;
        String tableName = tablename.toLowerCase();
        String daoName = "com.ge.apm.dao."+tabelNameToClassName(tableName) + "Repository";
        Class<?> dao = getDao(daoName);
        try {
            try {
                if (page == null || size == null || "".equals(page) || "".equals(size)) {
                    return dao.getMethod("findAll").invoke(WebUtil.getBean(dao));
                } else {
                    PageRequest pageRequest = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
                    Page<Object> objPage = (Page<Object>)dao.getMethod("findAll", Pageable.class).invoke(WebUtil.getBean(dao), pageRequest);
                    return objPage.getContent();
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @RequestMapping(value = "/dataget/{tablename}/{id}", method = RequestMethod.GET )
    public @ResponseBody Object getData(@PathVariable String tablename, @PathVariable String id) {
        if (tablename == null || id == null) return null;
        String tableName = tablename.toLowerCase();
        String daoName = "com.ge.apm.dao."+tabelNameToClassName(tableName) + "Repository";
        Class<?> dao = getDao(daoName);
        try {
            try {
                return dao.getMethod("findById", Integer.class).invoke(WebUtil.getBean(dao), Integer.parseInt(id));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @RequestMapping(value = "/datapost/{tablename}", method = RequestMethod.POST) 
    public @ResponseBody Object postData(@PathVariable String tablename, @RequestBody List<Map> list) {
        if(tablename == null) return "请输入表名。";
        if (list == null || list.isEmpty()) return "无数据保存";
        String tableName = tablename.toLowerCase();
        String talbeClassName = "com.ge.apm.domain." + tabelNameToClassName(tableName);
        String daoName = "com.ge.apm.dao." + tabelNameToClassName(tableName) + "Repository";
        Class<?> dao = getDao(daoName);
        Class<?> table = getDao(talbeClassName);
        if (dao == null || table == null) return "保存失败";
        List saveList = new ArrayList();
        for (int i=0; i<list.size();i++){
            try {
                Object obj = table.newInstance();
                BeanUtils.populate(obj, list.get(i));
                saveList.add(obj);
            } catch (Exception ex) {
                Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        try {
            dao.getMethod("save", Iterable.class).invoke(WebUtil.getBean(dao), saveList);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "数据保存成功。";
    }
    
    @RequestMapping(value = "/datapost/direct/{tablename}", method = RequestMethod.POST) 
    public @ResponseBody Object postDirectData(@PathVariable String tablename, @RequestBody List<Map> list) {
        if(tablename == null) return "请输入表名。";
        if (list == null || list.isEmpty()) return "无数据保存";
        String tableName = tablename.toLowerCase();
        String talbeClassName = "com.ge.apm.domain." + tabelNameToClassName(tableName);
        Class<?> table = getDao(talbeClassName);
        Map<String, String> fields = getFields(table);
        
        EntityManagerFactory emf = WebUtil.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = null;
        try{
            for (int i=0; i<list.size();i++){
                Map<String, Object> map = list.get(i);
                String outColumnStr = "insert into "+ tableName+ " (";
                String outValueStr = ") values(";
                String[] strs = insertColumn(fields, map);
                outColumnStr += strs[0]; 
                outValueStr += strs[1];
                String sql = outColumnStr + outValueStr + ")" ;
                query = em.createNativeQuery(sql);
                query.executeUpdate();
            }
            em.getTransaction().commit();
        } catch(Exception ex) {
            Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            em.close();
        }
        return "数据保存成功。";
    }
    
    private String tabelNameToClassName(String tableName) {
        if (tableName == null) return null;
        String[] names = tableName.split("_");
        String ret = "";
        for(String name : names) {
            ret += captureName(name);
        }
        return ret;
    }
    
    private Map<String, String> getFields(Class<?> table){
        Field[] fields = table.getDeclaredFields();
        Map<String, String> map = new TreeMap<String, String>();
        for (int i = 0; i < fields.length; i++) {
            Column column = fields[i].getAnnotation(Column.class);
            if (column == null) continue;
            map.put(fields[i].getName(), column.name());
        }
        return map;
    }
    
    private String[] insertColumn(Map<String, String> fields, Map<String, Object> map) {
        List<String> cols = new ArrayList<String>();
        List<Object> vals = new ArrayList<Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String obj = fields.get(entry.getKey());
            if (obj == null) continue;
            cols.add(fields.get(entry.getKey()));
            Object str = entry.getValue();
            if (str == null || "null".equals(str)) {
                vals.add(str);
            } else {
                vals.add("'"+str+"'");
            }
        }
        
        return new String[]{cols.toString().replace("[", "").replace("]", ""),
            vals.toString().replace("[", "").replace("]", "")};
    }
    
    /**
    * 将首字符大写
    * @param name
    * @return
    */
    private String captureName(String name) 
    {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }
    
    private Class<?> getDao(String daoName) {
        Class<?> classObj = null;
        try {
            classObj = Class.forName(daoName);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return classObj;
    }
    
}
