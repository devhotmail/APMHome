package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetTagBiomedGroupRepository;
import com.ge.apm.dao.AssetTagRuleRepository;
import com.ge.apm.dao.BiomedGroupRepository;
import com.ge.apm.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 212605082 on 2017/5/15.
 */
@Component
public class AssetTagService {

    @Autowired
    private BiomedGroupRepository biomedGroupDao;
    @Autowired
    private AssetTagBiomedGroupRepository assetTagBiomedGroupDao;
    @Autowired
    private AssetTagRuleRepository assetTagRuleDao;
    @Autowired
    private AssetInfoRepository assetDao;

    public List<BiomedGroup> getBiomedGroupList(BiomedGroup queryBiomedGroup){
        List<SearchFilter> biomedGroupFilters = new ArrayList<>();
        biomedGroupFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryBiomedGroup.getSiteId()));
        biomedGroupFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryBiomedGroup.getHospitalId()));

        return biomedGroupDao.findBySearchFilter(biomedGroupFilters);
    }

    public List<BiomedGroup> getTargetBiomedGroup(AssetTag tempAssetTag){
        List<BiomedGroup> tempTargetBiomedGroup = new ArrayList<>();

        if(tempAssetTag.getId() != null){
            for(AssetTagBiomedGroup tempAssetTagBiomedGroup : assetTagBiomedGroupDao.findByTagId(tempAssetTag.getId())){
                tempTargetBiomedGroup.add(tempAssetTagBiomedGroup.getBiomedGroup());
            }
        }

        return tempTargetBiomedGroup;
    }

    public List<AssetInfo> getAssetList(AssetInfo queryAsset) {

        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryAsset.getSiteId()));
        assetFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryAsset.getHospitalId()));
        if (null != queryAsset.getAssetGroup()) {
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (null != queryAsset.getClinicalDeptId()) {
            assetFilters.add(new SearchFilter("clinicalDeptId", SearchFilter.Operator.EQ, queryAsset.getClinicalDeptId()));
        }

        return assetDao.findBySearchFilter(assetFilters);
    }

    public List<AssetInfo> getTargetAssets(AssetTag tempAssetTag){
        List<AssetInfo> tempTargetAssets = new ArrayList<>();

        if(tempAssetTag.getId() != null){
            for(AssetTagRule tempAssetTagRule : assetTagRuleDao.findByTagId(tempAssetTag.getId())){
                tempTargetAssets.add(tempAssetTagRule.getAssetInfo());
            }
        }

        return tempTargetAssets;
    }

    @Transactional
    public void saveAssetTag(AssetTag tempAssetTag, List<BiomedGroup> targetBiomedGroup, List<AssetInfo> targetAssets){
        if(tempAssetTag.getId() != null){
            assetTagBiomedGroupDao.deleteByTagId(tempAssetTag.getId());
            assetTagRuleDao.deleteByTagId(tempAssetTag.getId());
        }

        for(BiomedGroup tempBiomedGroup : targetBiomedGroup){
            AssetTagBiomedGroup tempAssetTagBiomedGroup = new AssetTagBiomedGroup();
            tempAssetTagBiomedGroup.setBiomedGroupId(tempBiomedGroup.getId());
            tempAssetTagBiomedGroup.setTagId(tempAssetTag.getId());
            assetTagBiomedGroupDao.save(tempAssetTagBiomedGroup);
        }

        for(AssetInfo tempAssetInfo : targetAssets){
            AssetTagRule tempAssetTagRule = new AssetTagRule();
            tempAssetTagRule.setAssetId(tempAssetInfo.getId());
            tempAssetTagRule.setTagId(tempAssetTag.getId());
            assetTagRuleDao.save(tempAssetTagRule);
        }
    }
}
