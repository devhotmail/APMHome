package com.ge.apm.dao;


import java.util.List;

import com.ge.apm.domain.AssetContract;

import webapp.framework.dao.GenericRepository;

public interface AssetContractRepository extends GenericRepository<AssetContract> {
    
    public List<AssetContract> findByAssetId(Integer assetId);
}
