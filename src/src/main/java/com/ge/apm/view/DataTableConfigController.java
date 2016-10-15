package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.DataTableConfigRepository;
import com.ge.apm.domain.DataTableConfig;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableDataController;

@ManagedBean
@ViewScoped
public class DataTableConfigController extends JpaCRUDController<DataTableConfig> {

    DataTableConfigRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(DataTableConfigRepository.class);
    }

    @Override
    protected DataTableConfigRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<DataTableConfig> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<DataTableConfig> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    @Override
    public void onAfterDataChanged(){
        SqlConfigurableDataController.reLoadDataTableConfig();
    };
}