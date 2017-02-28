package com.ge.apm.dao;

import com.ge.apm.domain.AssetSummit;
import java.util.Date;
import webapp.framework.dao.GenericRepository;

public interface AssetSummitRepository extends GenericRepository<AssetSummit> {
    public AssetSummit getByAssetIdAndCreated(int assetId, Date created);
}
