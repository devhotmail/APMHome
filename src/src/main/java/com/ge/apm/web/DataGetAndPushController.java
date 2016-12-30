/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212595360
 */
@Controller
public class DataGetAndPushController {
    
    @RequestMapping(value = "/dataget/{tablename}", method = RequestMethod.GET )
    public @ResponseBody Object getDataRow(@PathVariable String tablename) {
        if (tablename == null) return null;
        String tableName = tablename.toLowerCase();
        String daoName = "com.ge.apm.dao."+tabelNameToClassName(tableName) + "Repository";
        Class<?> dao = getDao(daoName);
        try {
            try {
                return dao.getMethod("findAll").invoke(WebUtil.getBean(dao));
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
    
}
