package com.ge.apm.view.asset;

import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.SiteInfo;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class QrCodeLibCreateController extends JpaCRUDController<QrCodeLib> {

    QrCodeLibRepository dao = null;
    SiteInfoRepository sitedao = null;

    private AssetCreateService acService;
    UserContextService userContextService;

    private SearchFilter statusFilter = new SearchFilter("status", SearchFilter.Operator.EQ, 2);

    @Override
    protected void init() {
        dao = WebUtil.getBean(QrCodeLibRepository.class);
        sitedao = WebUtil.getBean(SiteInfoRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);

        userContextService = WebUtil.getBean(UserContextService.class);

        if (userContextService.hasRole("SuperAdmin")) {
            this.filterByHospital = false;
            this.filterBySite = false;
        } else {
            this.filterBySite = true;
            if (userContextService.hasRole("MultiHospital")) {
                this.filterByHospital = false;
            } else {
                this.filterByHospital = true;
            }
        }
//        searchFilters.add(statusFilter);

    }

    @Override
    protected QrCodeLibRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<QrCodeLib> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
            searchFilters.add(statusFilter);
            return dao.findBySearchFilter(searchFilters, pageRequest);
        } else {
            if (!searchFilters.contains(statusFilter)) {
                searchFilters.add(statusFilter);
            }
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<QrCodeLib> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    public List<SiteInfo> getSiteList() {

        List<SiteInfo> res = new ArrayList();
        if (userContextService.hasRole("SuperAdmin")) {
            res.addAll(sitedao.find());
        } else {
            res.add(sitedao.findById(UserContextService.getCurrentUserAccount().getSiteId()));
        }
        return res;
    }

    public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getSiteName(Integer siteId) {
        return acService.getSiteName(siteId);
    }

}
