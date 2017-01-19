/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import com.ge.apm.service.data.DataService;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author 212595360
 */
@Controller
public class DataGetAndPushController {
    @Autowired
    private DataService dataService;
    
    @RequestMapping(value = "/dataget/{tablename}", method = RequestMethod.GET )
    public @ResponseBody Object getDataRow(@PathVariable String tablename, 
            @RequestParam(required=false) String page, 
            @RequestParam(required=false) String size,
            @RequestParam(required=false)  String hospitalId, 
            @RequestParam(required=false)  String siteId) {
        return dataService.getDataRow(tablename, page, size, hospitalId, siteId);
    }
    
    @RequestMapping(value = "/dataget/{tablename}/{id}", method = RequestMethod.GET )
    public @ResponseBody Object getData(@PathVariable String tablename, @PathVariable String id) {
        return dataService.getDataRow(tablename, id);
    }
    
    @RequestMapping(value = "/datapost/{tablename}", method = RequestMethod.POST) 
    public @ResponseBody Object postData(@PathVariable String tablename, @RequestBody List<Map> list) {
        try {
            return dataService.postData(tablename, list);
        } catch(Exception ex) {
            Logger.getLogger(DataGetAndPushController.class.getName()).log(Level.SEVERE, null, ex);
            return "{code:1,msg:"+ org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(ex) +"}";
        }
    }
    
    @RequestMapping(value = "/datapost/direct/{tablename}", method = RequestMethod.POST) 
    public @ResponseBody Object postDirectData(@PathVariable String tablename, @RequestBody List<Map> list) {
        return dataService.postDirectData(tablename, list);
    }
}
