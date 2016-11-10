package com.ge.apm.view;

import com.ge.apm.view.sysutil.UserContextService;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableDataController;

@ManagedBean
@ViewScoped
public class DemoDataTableController extends SqlConfigurableDataController {

    @Override
    public String getSQL(String dataTableName, String sql){
        System.out.println("********* getSQL="+sql);
        return sql;
    }
    
    @Override
    public String getCountSQL(String dataTableName, String countSql){
        System.out.println("********* getCountSQL="+countSql);
        return countSql;
    }

    @Override
    public void getSqlParam(String dataTableName, Map<String, Object> params){
        System.out.println("********* getSqlParam="+params);
    }

    @Override
    public void setSearchFilter() {
        super.setSearchFilter();
        this.searchFilterList.add(new SearchFilter("hospital_id", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getHospitalId()));
        
        DemoChartController demoChartController = WebUtil.getBean(DemoChartController.class);
        demoChartController.setSearchFilter();
    }

}
