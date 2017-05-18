package com.ge.apm.dao;

import com.ge.apm.domain.OrgInfo;

import java.util.Collection;
import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import webapp.framework.dao.GenericRepository;

public interface OrgInfoRepository extends GenericRepository<OrgInfo> {
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    public List<OrgInfo> getByHospitalId(int hospitalId);
    
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Query("select t from OrgInfo t where t.siteId=?1 and t.parentOrg is null")
    public List<OrgInfo> getHospitalBySiteId(int siteId);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Query("select t from OrgInfo t where t.siteId=?1")
    public List<OrgInfo> getFullOrgListBySiteId(int siteId);
    
    public Page<OrgInfo> getByHospitalId(Pageable pageRequest, int hospitalId);
    public Page<OrgInfo> getBySiteId(Pageable pageRequest, int siteId);
    
}
