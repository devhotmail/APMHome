package com.ge.apm.dao;

import com.ge.apm.domain.AssetTagRule;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 212605082 on 2017/5/12.
 */
public interface AssetTagRuleRepository extends GenericRepository<AssetTagRule> {

    public List<AssetTagRule> findByTagId(Integer tagId);
    public void deleteByTagId(Integer tagId);
}
