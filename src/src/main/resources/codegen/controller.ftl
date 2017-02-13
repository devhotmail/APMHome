package ${basePackage}.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import ${basePackage}.dao.${entityName}Repository;
import ${basePackage}.domain.${entityName};
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class ${entityName}Controller extends JpaCRUDController<${entityName}> {

    ${entityName}Repository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(${entityName}Repository.class);
    }

    @Override
    protected ${entityName}Repository getDAO() {
        return dao;
    }

    @Override
    protected Page<${entityName}> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<${entityName}> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(${entityName} object) {
    }
    
    @Override
    public void onAfterNewObject(${entityName} object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(${entityName} object) {
    }
    
    @Override
    public void onAfterUpdateObject(${entityName} object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(${entityName} object) {
    }
    
    @Override
    public void onAfterDeleteObject(${entityName} object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(${entityName} object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}