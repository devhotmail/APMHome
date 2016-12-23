/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;

@Service
public class AssetInfoOperateService {
	
	@Autowired
    AssetInfoRepository assetInfoDao;
    
    @Transactional
    public void updateAssetInfoById(AssetInfo ai){
    	assetInfoDao.updateAssetInfoPmOrderDate(ai.getId(),ai.getSiteId());
    }
}
