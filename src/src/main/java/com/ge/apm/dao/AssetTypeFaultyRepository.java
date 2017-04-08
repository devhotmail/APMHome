package com.ge.apm.dao;

import com.ge.apm.domain.AssetTypeFaulty;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface AssetTypeFaultyRepository extends GenericRepository<AssetTypeFaulty> {
    public List<AssetTypeFaulty> getByAstypeId(int astypeId);

    public List<AssetTypeFaulty> getByFaultId(int faultId);


    

}
