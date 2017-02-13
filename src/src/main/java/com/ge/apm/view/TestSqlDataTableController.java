package com.ge.apm.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.mvc.SqlDataController;

/**
 *
 * @author 212547631
 */
@ManagedBean
@ViewScoped
public class TestSqlDataTableController extends SqlDataController{

    public void onRowSelected(){
        //NativeSQLUtil.queryForList("select * from PatientInfo", null);
        
        System.out.println("************ onRowSelected="+this.selected);
    }

    @Override
    public void init(){
        //select xxx from xxx where xxx :#searchFilter :#orderBy
        this.sql = "select e.*, p.PatientNameChinese,p.PatientGender, p.PatientBirtDay from PatientInfo p, ExamInfo e where e.PatientIntraID=p.PatientIntraID and e.ExamSatus>=:#examStatus :#searchFilter :#orderBy";
        this.sqlParams.put("examStatus", "3");
    }

    @Override
    public String getColumnNameList() {
        return "";
    }

}
