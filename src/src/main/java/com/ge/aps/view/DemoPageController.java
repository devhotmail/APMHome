package com.ge.aps.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.mvc.SqlDataController;

@ManagedBean
@ViewScoped
public class DemoPageController extends SqlDataController {

    @Override
    public String getColumnNameList(){
        return "name name_en";
    };
    
    @Override
    public void init(){
        //SQL format: select xxx from xxx where xxx :#searchFilter :#orderBy
        this.sql = "select * from institution";
        //this.sqlParams.put("examStatus", "3");
    }
}