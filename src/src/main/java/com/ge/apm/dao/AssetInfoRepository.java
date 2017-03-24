package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface AssetInfoRepository extends GenericRepository<AssetInfo> {
    @Query("select o from AssetInfo o where o.hospitalId = ?1 and o.isValid = true")
    public List<AssetInfo> getByHospitalId(int hospitalId);
    
    @Query("select o from AssetInfo o where o.modalityId = ?1 and o.isValid = true")
    public List<AssetInfo> getByModalityId(String modalityId);
    
    @Query("select o from AssetInfo o where o.siteId = ?1 and o.isValid = true")
    public List<AssetInfo> getBySiteId(int siteId);
    
    @Query("select o from AssetInfo o where o.qrCode = ?1")
    public List<AssetInfo> getByQrCode(String qrCode);
    
    @Modifying
    @Query("update AssetInfo ai set ai.lastPmDate = now() where id = ?1 and siteId = ?2")
    public void updateAssetInfoPmOrderDate(int id,int siteId);
    
    public AssetInfo getById(Integer id);
    
}
