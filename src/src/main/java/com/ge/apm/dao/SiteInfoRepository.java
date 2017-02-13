package com.ge.apm.dao;

import com.ge.apm.domain.SiteInfo;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface SiteInfoRepository extends GenericRepository<SiteInfo> {
    
    public List<SiteInfo> getByName(String name);

}
