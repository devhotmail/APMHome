package com.ge.apm.dao;

import com.ge.apm.domain.AssetDepreciation;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface AssetDepreciationRepository extends GenericRepository<AssetDepreciation> {
   
    @Modifying
    @Query("delete from AssetDepreciation a where a.assetId=?1 and contractId=?2")
    public void deleteByAssetIdAndContractType(int assetId, int contractId);
    
    public List<AssetDepreciation> getByContractId(Integer contractId);
    
}
