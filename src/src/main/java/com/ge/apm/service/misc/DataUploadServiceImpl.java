package com.ge.apm.service.misc;

import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.domain.AssetClinicalRecord;
import webapp.framework.web.WebUtil;

public class DataUploadServiceImpl implements DataUploadService{

    @Override
    public String uploadAssetClinicalRecord(AssetClinicalRecord assetClinicalRecord){
        assetClinicalRecord.setId(null);
        
        AssetClinicalRecordRepository dao = WebUtil.getBean(AssetClinicalRecordRepository.class);
        dao.save(assetClinicalRecord);
        return "dataUploadedOK";
    }
    
}
