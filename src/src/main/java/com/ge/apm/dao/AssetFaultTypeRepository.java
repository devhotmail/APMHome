package com.ge.apm.dao;

import com.ge.apm.domain.AssetFaultType;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface AssetFaultTypeRepository extends GenericRepository<AssetFaultType> {

    public List<AssetFaultType> getByAssetGroupId(Integer assetGroupId);

}
