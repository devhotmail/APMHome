package com.ge.apm.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.mvc.SqlDataController;

@ManagedBean
@ViewScoped
public class DemoTableController extends SqlDataController {

    @Override
    public String getColumnNameList(){
        return "d-id d-name u-user_id u-user_name";
    };
    
    @Override
    public void init(){
        //SQL format: select xxx from xxx where xxx :#searchFilter :#orderBy
        this.sql = "select u.* from user_account u where :#searchFilter :#orderBy";
        //this.sqlParams.put("examStatus", "3");
    }
}