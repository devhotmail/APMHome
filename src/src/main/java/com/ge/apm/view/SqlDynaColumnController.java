package com.ge.apm.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlDataController;

@ManagedBean
@ViewScoped
public class SqlDynaColumnController extends SqlDataController {

    @Override
    public void init(){
        //SQL format: select xxx from xxx where xxx :#searchFilter :#orderBy
        this.sql = "select e.exam_id, e.exam_status, p.patient_name, p.gender_id,p.birthday from exam_info e, patient_info p :#searchFilter and p.patient_intra_id=e.patient_intra_id :#orderBy";
        //this.sqlParams.put("examStatus", "3");
    }
    
    public void onRowSelected(){
        WebUtil.addSuccessMessageKey("test", "AAA", "BBB");
        WebUtil.addErrorMessageKey("test", "CCC", "DDD");
        String str = WebUtil.getMessage("test", 
                WebUtil.getFieldValueMessage("exam_status", "6"),
                WebUtil.getFieldValueMessage("gender", "1"));
        
        System.out.println("*************"+ WebUtil.getMessage("exam_status"));
        System.out.println("*************"+ WebUtil.getFieldValueMessage("exam_status", "6"));
        
    }
}