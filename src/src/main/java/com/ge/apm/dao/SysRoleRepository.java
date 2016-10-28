package com.ge.apm.dao;

import com.ge.apm.domain.SysRole;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import webapp.framework.dao.GenericRepository;

public interface SysRoleRepository extends GenericRepository<SysRole> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    public SysRole getByName(String name);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    public SysRole getByRoleDesc(String name);
    
}
