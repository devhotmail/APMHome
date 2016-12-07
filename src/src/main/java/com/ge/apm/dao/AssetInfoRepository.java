package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface AssetInfoRepository extends GenericRepository<AssetInfo> {
    @Query("select o from AssetInfo o where o.hospitalId = ?1 and o.isValid = true")
    public List<AssetInfo> getByHospitalId(int hospitalId);
}
