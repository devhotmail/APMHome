/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

/**
 *
 * @author 212579464
 */
@Component
public class AssetOwnerService {

    @Autowired
    private AssetInfoRepository assetDao;

    @Autowired
    private UserAccountRepository userDao;

    public List<AssetInfo> getAssetList(AssetInfo queryAsset) {

        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryAsset.getSiteId()));
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryAsset.getHospitalId()));
        if (null != queryAsset.getAssetGroup()) {
            OrgInfoFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (null != queryAsset.getClinicalDeptId()) {
            OrgInfoFilters.add(new SearchFilter("clinicalDeptId", SearchFilter.Operator.EQ, queryAsset.getClinicalDeptId()));
        }
        if (null != queryAsset.getAssetOwnerId()) {
            OrgInfoFilters.add(new SearchFilter("assetOwnerId", SearchFilter.Operator.EQ, queryAsset.getAssetOwnerId()));
        }
        if (null != queryAsset.getAssetOwnerId2()) {
            OrgInfoFilters.add(new SearchFilter("assetOwnerId2", SearchFilter.Operator.EQ, queryAsset.getAssetOwnerId2()));
        }

        return assetDao.findBySearchFilter(OrgInfoFilters);
    }

    public UserAccount getOwnerAccount(Integer userId) {
        return userDao.getById(userId);
    }

    @Transactional
    public void updateAssetOwner(List<AssetInfo> targetAssets, Boolean chanegeOwner, Integer ownerId, Boolean chanegeOwner2, Integer ownerId2) {

        UserAccount owner = null;
        UserAccount owner2 = null;
        if (null != ownerId && chanegeOwner) {
            owner = getOwnerAccount(ownerId);
        }
        if (null != ownerId2 && chanegeOwner2) {
            owner2 = getOwnerAccount(ownerId2);
        }

        for (AssetInfo item : targetAssets) {
            if (chanegeOwner) {
                item.setAssetOwnerId(null == owner ? null : owner.getId());
                item.setAssetOwnerName(null == owner ? null : owner.getName());
                item.setAssetOwnerTel(null == owner ? null : owner.getTelephone());
            }
            if (chanegeOwner2) {
                item.setAssetOwnerId2(null == owner2 ? null : owner2.getId());
                item.setAssetOwnerName2(null == owner2 ? null : owner2.getName());
                item.setAssetOwnerTel2(null == owner2 ? null : owner2.getTelephone());
            }
        }

        assetDao.save(targetAssets);
    }

}
