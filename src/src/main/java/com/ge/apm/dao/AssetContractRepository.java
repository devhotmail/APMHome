package com.ge.apm.dao;


import com.ge.apm.domain.AssetContract;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface AssetContractRepository extends GenericRepository<AssetContract> {
    
    public List<AssetContract> findByAssetId(Integer assetId);
}
