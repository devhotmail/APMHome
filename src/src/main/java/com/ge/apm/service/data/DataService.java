/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.data;

import com.ge.apm.web.DataGetAndPushController;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212595360
 */
@Service
public class DataService {
    
    public Object getDataRow(String tablename, String page, String size, String hospitalId, String siteId) {
        if (tablename == null) return null;
        String tableName = tablename.toLowerCase();
        String daoName = "com.ge.apm.dao."+tabelNameToClassName(tableName) + "Repository";
        Class<?> dao = getDao(daoName);
        try {
            try {
                List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
                if (hospitalId != null && !"".equals(hospitalId))
                    searchFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, hospitalId));
                if (siteId != null && !"".equals(siteId))
                    searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, siteId));
                if (page == null || size == null || "".equals(page) || "".equals(size)) {
                    return dao.getMethod("findBySearchFilter", List.class).invoke(WebUtil.getBean(dao), searchFilters);
                } else {
                    PageRequest pageRequest = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
                    Page<Object> objPage = (Page<Object>)dao.getMethod("findBySearchFilter", List.class, Pageable.class).invoke(WebUtil.getBean(dao), searchFilters, pageRequest);
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
    
    public Object getDataRow(String tablename, String id) {
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
    
    @Transactional
    public String postData(String tablename, List<Map> list) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if(tablename == null) return "{\"code\":\"1\",\"msg\":\"请输入表名\"}";
        if (list == null || list.isEmpty()) return "{\"code\":\"1\",\"msg\":\"无数据保存\"}";
        String tableName = tablename.toLowerCase();
        String talbeClassName = "com.ge.apm.domain." + tabelNameToClassName(tableName);
        String daoName = "com.ge.apm.dao." + tabelNameToClassName(tableName) + "Repository";
        Class<?> dao = getDao(daoName);
        Class<?> table = getDao(talbeClassName);
        if (dao == null || table == null) return "{\"code\":\"1\",\"msg\":\"保存失败\"}";
        int fortimes = list.size()/50 +1;//循环次数
        for (int j=0; j<fortimes; j++) {
            List<Map> subList = list.subList(j*50, (j+1)*50<list.size()?(j+1)*50:list.size());
            List saveList = new ArrayList();
            for (int i=0; i<subList.size();i++){
                Object obj = table.newInstance();
                BeanUtils.populate(obj, subList.get(i));
                saveList.add(obj);
            }
            dao.getMethod("save", Iterable.class).invoke(WebUtil.getBean(dao), saveList);
        }
        return "{\"code\":\"0\",\"msg\":\"保存成功\"}";//成功
    }
    
    public Object postDirectData(String tablename, List<Map> list) {
        if(tablename == null) return "{\"code\":\"1\",\"msg\":\"请输入表名\"}";
        if (list == null || list.isEmpty()) return "{\"code\":\"1\",\"msg\":\"无数据保存\"}";
        String tableName = tablename.toLowerCase();
        String talbeClassName = "com.ge.apm.domain." + tabelNameToClassName(tableName);
        Class<?> table = getDao(talbeClassName);
        Map<String, String> fields = getFields(table);
        
        EntityManagerFactory emf = WebUtil.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();
        int fortimes = list.size()/50 +1;//循环次数
        for (int j=0; j<fortimes; j++) {
            List<Map> subList = list.subList(j*50, (j+1)*50<list.size()?(j+1)*50:list.size());
            
            em.getTransaction().begin();
            Query query = null;
            for (int i=0; i<subList.size();i++){
                Map<String, Object> map = subList.get(i);
                String outColumnStr = "insert into "+ tableName+ " (";
                String outValueStr = ") values(";
                String[] strs = insertColumn(fields, map);
                outColumnStr += strs[0]; 
                outValueStr += strs[1];
                String sql = outColumnStr + outValueStr + ")" ;
                query = em.createNativeQuery(sql);
                query.executeUpdate();
            }
            try{
                em.getTransaction().commit();
            } catch(Exception ex) {
                em.getTransaction().rollback();
                Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
                em.close();
                return "{\"code\":\"1\",\"msg\":\"保存失败\"}";//失败
            } 
        }
        if (em != null) {
            em.close();
        }
        return "{\"code\":\"0\",\"msg\":\"保存成功\"}";//成功
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
    
}
