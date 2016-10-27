package com.ge.apm.dao;

import com.ge.apm.domain.OrgInfo;
import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import webapp.framework.dao.GenericRepository;

public interface OrgInfoRepository extends GenericRepository<OrgInfo> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    public List<OrgInfo> getByHospitalId(int hospitalId);
}
