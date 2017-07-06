package com.ge.apm.dao;

import com.ge.apm.domain.AssetSummit;
import com.ge.apm.domain.AssetSummitMaxMinPojo;
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
    public List<AssetSummit> findAssetSummit(@Param("assetId") int assetId, @Param("hospitalId") int hospitalId, @Param("siteId") int siteId);


    @Query("select asm from AssetSummit as asm where asm.rating is null or  asm.rating=0.0")
    public List<AssetSummit> findAssetSummitByRating();

    @Query("select asm from AssetSummit asm where asm.assetId = :assetId and asm.created = :created ")
    public AssetSummit getAssetSummitByAssetIdAndCreated(@Param("assetId") int assetId, @Param("created") Date date);

    @Modifying
    @Query("update AssetSummit asm set asm.exposeCount =?1,asm.injectCount=?2, filmCount=?3 where id =?4")
    public void updateAssetSummit(Double exposeCount,Double injectCount,Long filmCount,Integer id);

/*    @Modifying
    @Query("update AssetInfo ai set ai.lastPmDate = now() where id = ?1 and siteId = ?2")
    public void updateAssetInfoPmOrderDate(int id,int siteId);*/
   /* @Query("select new com.ge.apm.domain.AssetSummitMaxMinPojo(max(sub.np) as npMax,min(sub.np) as npMin , max(sub.ic) as icMax,min(sub.ic) as icMin, max(sub.ec) as ecMax, min(sub.ec) as ecMin,max(sub.fm) as fmMax, min(sub.fm) as fmMin ) from " +
            "(select sum(revenue-maintenanceCost-deprecationCost) as np, sum(injectCount) as ic,sum(exposeCount) as ec ,sum(filmCount) as fm from assetSummit group by assetId,created) as sub ")
    public AssetSummitMaxMinPojo getAssetSummitMaxMin();*/


}