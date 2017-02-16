/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetContractRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetContract;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInfoService {

    AssetInfoRepository assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
    AssetContractRepository contractDao = WebUtil.getBean(AssetContractRepository.class);
    
    UaaService uaaService = WebUtil.getBean(UaaService.class);

    public String getAssetInfoName(Integer id) {
        if(null==id || id==0)
            return "";
        else
            return assetInfoDao.findById(id).getName();
    }

    public AssetInfo getAssetInfo(int id) {
        return assetInfoDao.findById(id);
    }

    public List<AssetInfo> getAssetList() {
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        List<SearchFilter> assetInfoFilters = new ArrayList<>();
        assetInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId()));
        List<AssetInfo> assetList = assetInfoDao.findBySearchFilter(assetInfoFilters);
        return assetList;
    }
    
    public List<AssetContract> getContractList(Integer assetId){
        return contractDao.findByAssetId(assetId);
    }
    
    public List<OrgInfo> getHospitalList(){
        return uaaService.getHospitalListBySiteId(UserContextService.getCurrentUserAccount().getSiteId());
    }
}
