package ${basePackage}.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.mvc.SqlDataController;

@ManagedBean
@ViewScoped
public class ${entityName} extends SqlDataController {
    @Override
    public void init(){
        //SQL format: select xxx from xxx where xxx :#searchFilter :#orderBy
        this.sql = "${sql}";
        //this.sqlParams.put("examStatus", "3");
    }
}