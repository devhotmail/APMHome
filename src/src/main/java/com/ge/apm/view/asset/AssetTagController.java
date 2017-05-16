package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetTag;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SiteInfo;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2017/5/12.
 */
@ManagedBean
@ViewScoped
public class AssetTagController extends JpaCRUDController<AssetTag> {

    AssetTagRepository dao = null;
    I18nMessageRepository i18nDao = null;
    SiteInfoRepository siteInfoDao = null;
    OrgInfoRepository orgInfoDao = null;

    private AssetCreateService acService;

    private Integer siteId;
    private Integer hospitalId;

    private Integer siteIdFilter;
    private Integer hospitalIdFilter;

    private String tempSiteId = "";

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetTagRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        siteInfoDao = WebUtil.getBean(SiteInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        if (userContextService.hasRole("SuperAdmin")) {
            this.filterByHospital = false;
            this.filterBySite = false;
        } else {
            this.filterBySite = true;
            if (!userContextService.hasRole("MultiHospital")) {
                this.filterByHospital = false;
            } else {
                this.filterByHospital = true;
            }
        }
    }

    @Override
    protected AssetTagRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetTag> loadData(PageRequest pageRequest) {

        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null || searchFilters.size() == 0) {
            return dao.findAll(pageRequest);
        } else {
            Map<String, SearchFilter> tempSearchFilterMap = new HashMap<>(searchFilters.size());
            for (SearchFilter tempSearchFilter : searchFilters) {
                tempSearchFilterMap.put(tempSearchFilter.fieldName, tempSearchFilter);
            }

            if(tempSearchFilterMap.containsKey("siteId")){
                if(!tempSearchFilterMap.get("siteId").value.toString().equals(tempSiteId)){
                    tempSearchFilterMap.remove("hospitalId");
                }

                this.tempSiteId = tempSearchFilterMap.get("siteId").value.toString();
            }

            if(!tempSearchFilterMap.containsKey("siteId") && tempSearchFilterMap.containsKey("hospitalId")){
                tempSearchFilterMap.remove("hospitalId");
            }

            searchFilters = new ArrayList<SearchFilter>();
            for(Map.Entry<String, SearchFilter> entry : tempSearchFilterMap.entrySet()){
                searchFilters.add(entry.getValue());
            }

            if(searchFilters.size() <= 0){
                return dao.findAll(pageRequest);
            }else{
                return dao.findBySearchFilter(searchFilters, pageRequest);
            }

        }

    }

    @Override
    public List<AssetTag> getItemList() {
        return dao.find();
    }

    public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getSiteName(Integer siteId) {
        return acService.getSiteName(siteId);
    }

    public List<SiteInfo> getSiteList() {

        List<SiteInfo> siteList = siteInfoDao.find();

        return siteList;
    }

    public List<OrgInfo> getHospitalList() {

        List<OrgInfo> hospitalList = null;
        if (siteId != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteId));
        }

        return hospitalList;
    }

    public List<OrgInfo> getHospitalListFilter() {

        List<OrgInfo> hospitalList = null;
        if (siteIdFilter != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteIdFilter));
        }

        return hospitalList;
    }

    public void onSiteChange(){
        if(siteId == null){
            hospitalId = null;
        }
    }

    public void onSiteFilterChange(){
        if(siteIdFilter == null){
            hospitalIdFilter = null;
        }
    }

    public void onSave(){



        Object obj = this.selected;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getSiteIdFilter() {
        return siteIdFilter;
    }

    public void setSiteIdFilter(Integer siteIdFilter) {
        this.siteIdFilter = siteIdFilter;
    }

    public Integer getHospitalIdFilter() {
        return hospitalIdFilter;
    }

    public void setHospitalIdFilter(Integer hospitalIdFilter) {
        this.hospitalIdFilter = hospitalIdFilter;
    }
}
