package com.ge.apm.dao;

import com.ge.apm.domain.BiomedGroup;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

/**
 * Created by 212605082 on 2017/5/12.
 */
public interface BiomedGroupRepository extends GenericRepository<BiomedGroup> {
    @Query("select count(*) from BiomedGroup bg where bg.siteId=?1 and bg.hospitalId=?2")
    public Integer getCountByHospital(Integer siteId,Integer hospitalId);
}
