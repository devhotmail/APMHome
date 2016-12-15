package com.ge.apm.view.uaa;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.domain.SiteInfo;
import java.util.HashMap;
import java.util.Map;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SiteInfoController extends JpaCRUDController<SiteInfo> {

    SiteInfoRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SiteInfoRepository.class);
    }

    @Override
    protected SiteInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<SiteInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<SiteInfo> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    @Override
    public String getErrorMessageForDuplicateKey(SiteInfo site){
        return String.format(WebUtil.getMessage("DuplicateSiteName"), site.getName());
    }
    
    @Override
    public String getKeyFieldNameValue(SiteInfo site){
        return WebUtil.getMessage("name")+"="+site.getName();
    }
    
}