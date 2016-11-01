package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface AssetInfoRepository extends GenericRepository<AssetInfo> {
    public List<AssetInfo> getByHospitalId(int hospitalId);
}
