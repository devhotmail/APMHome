package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.ChartConfigRepository;
import com.ge.apm.domain.ChartConfig;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;

@ManagedBean
@ViewScoped
public class ChartConfigController extends JpaCRUDController<ChartConfig> {

    ChartConfigRepository dao = null;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(ChartConfigRepository.class);
    }

    @Override
    protected ChartConfigRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ChartConfig> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<ChartConfig> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    
    @Override
    public void onAfterDataChanged(){
        SqlConfigurableChartController.reLoadChartConfig();
    };
}