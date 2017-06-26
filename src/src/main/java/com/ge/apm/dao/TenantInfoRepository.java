package com.ge.apm.dao;

import com.ge.apm.domain.TenantInfo;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface TenantInfoRepository extends GenericRepository<TenantInfo> {
    
    public List<TenantInfo> getByName(String name);

}
