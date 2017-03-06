package com.ge.apm.dao;

import com.ge.apm.domain.AssetSummit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.Date;
import java.util.List;

public interface AssetSummitRepository extends GenericRepository<AssetSummit> {
    public AssetSummit getByAssetIdAndCreated(int assetId, Date created);

    //public  List<Integer> findAll90
    @Query("select o from AssetSummit o")
    public List<AssetSummit> getAssetSummit();

    @Modifying
    @Query("update AssetSummit  ast set ast.examDuration = :exa ,ast.revenue= :pri where assetId = :aid ")
    public void updateAssetSummit(@Param("exa") Integer exa,
                                  @Param("pri") Double pri,
                                  @Param("aid") int id);

    @Query("select asm from AssetSummit asm where asm.siteId = :siteId and " +
            "asm.hospitalId = :hospitalId and asm.assetId = :assetId")
    public List<AssetSummit> getAssetSummit(@Param("assetId") int assetId,@Param("hospitalId") int hospitalId,@Param("siteId") int siteId);


}
