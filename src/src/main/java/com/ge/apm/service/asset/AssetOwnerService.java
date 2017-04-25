/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

/**
 *
 * @author 212579464
 */
@Component
public class AssetOwnerService {

    @Autowired
    private AssetInfoRepository assetDao;

    public List<AssetInfo> getAssetList(AssetInfo queryAsset) {

        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryAsset.getSiteId()));
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryAsset.getHospitalId()));
        
        return assetDao.findBySearchFilter(OrgInfoFilters);
    }

}
