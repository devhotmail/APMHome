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

    public void queryDB(){
        //准备 SQL 参数
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("hospitalId", 1);
        sqlParams.put("assetName", "%CT%");
        
        //执行 SQL
        List<Map<String, Object>> result = NativeSqlUtil.queryForList("select * from asset_info where hospital_id=:#hospitalId and name like :#assetName", sqlParams);
        
        //取第一行记录的资产名称字段
        String assetName = result.get(0).get("name").toString();
        
    }
/*
    @Override
    public void onBeforeNewObject(SiteInfo object) {
    }
    
    @Override
    public void onAfterNewObject(SiteInfo object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(SiteInfo object) {
    }
    
    @Override
    public void onAfterUpdateObject(SiteInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(SiteInfo object) {
    }
    
    @Override
    public void onAfterDeleteObject(SiteInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(SiteInfo object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}