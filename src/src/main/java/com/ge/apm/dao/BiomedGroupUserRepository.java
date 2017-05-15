package com.ge.apm.dao;

import com.ge.apm.domain.BiomedGroupUser;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 212605082 on 2017/5/12.
 */
public interface BiomedGroupUserRepository extends GenericRepository<BiomedGroupUser> {
    public List<BiomedGroupUser> findByGroupId(Integer groupId);
    public void deleteByGroupId(Integer groupId);
}
