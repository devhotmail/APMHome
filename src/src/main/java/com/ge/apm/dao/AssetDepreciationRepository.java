package com.ge.apm.dao;

import com.ge.apm.domain.AssetDepreciation;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface AssetDepreciationRepository extends GenericRepository<AssetDepreciation> {
   
    
    public List<AssetDepreciation> getByContractId(Integer contractId);
}
