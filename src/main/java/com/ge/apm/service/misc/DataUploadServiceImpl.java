package com.ge.apm.service.misc;

import com.ge.apm.dao.AssetClinicalRecordErrlogRepository;
import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.EdgeServerInfoRepository;
import com.ge.apm.dao.ProcedureNameMapingRepository;
import com.ge.apm.domain.AssetClinicalRecord;
import com.ge.apm.domain.AssetClinicalRecordErrlog;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.EdgeServerInfo;
import com.ge.apm.domain.ProcedureNameMaping;
import java.util.List;
import org.springframework.beans.BeanUtils;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

public class DataUploadServiceImpl implements DataUploadService{

    public EdgeServerInfo getEdgeServerInfo(String edgeServerKey, AssetClinicalRecord assetClinicalRecord) throws Exception{
        Integer siteId = assetClinicalRecord.getSiteId();
        EdgeServerInfoRepository dao = WebUtil.getBean(EdgeServerInfoRepository.class);
        EdgeServerInfo edgeServerInfo = dao.getByEdgeServerKey(edgeServerKey);
        if(edgeServerInfo==null) throw new Exception("invalid edgeServerKey");
        
        return edgeServerInfo;
    }
    
    public Integer getAssetIdByModalityId(AssetClinicalRecord assetClinicalRecord) throws Exception{
        if(assetClinicalRecord==null || assetClinicalRecord.getModalityId()==null) 
            throw new Exception("invalid modalityId");

        AssetInfoRepository dao = WebUtil.getBean(AssetInfoRepository.class);
        List<AssetInfo> assets = dao.getByModalityId(assetClinicalRecord.getModalityId());

        if(assets.size()>0)
            return assets.get(0).getId();
        else
            throw new Exception("invalid modalityId");
    }
    public void mapProcedureName(int siteId, int hospitalId, AssetClinicalRecord assetClinicalRecord) throws Exception{
        assetClinicalRecord.setOriginalProcedureStepId(assetClinicalRecord.getProcedureId().toString());
        assetClinicalRecord.setOriginalProcedureStepName(assetClinicalRecord.getProcedureName());
        
        ProcedureNameMapingRepository mapDao= WebUtil.getBean(ProcedureNameMapingRepository.class);
        List<ProcedureNameMaping> nameList = mapDao.getBySiteIdAndHospitalIdAndRisProcedureName(siteId, hospitalId, assetClinicalRecord.getOriginalProcedureStepName());
        if(nameList.size()>0){
            assetClinicalRecord.setProcedureId(nameList.get(0).getApmProcedureId());
            assetClinicalRecord.setProcedureName(nameList.get(0).getApmProcedureName());
        }
        else
            throw new Exception("procedure name not mapped.");
    }

    
    @Override
    public String uploadAssetClinicalRecord(ClinicalDataUploadRequest dataUploadRequest) throws Exception{
        String edgeServerKey = dataUploadRequest.getEdgeServerKey();
        AssetClinicalRecord assetClinicalRecord = dataUploadRequest.getClinicalRecord();
        try{
            EdgeServerInfo edgeServerInfo = getEdgeServerInfo(edgeServerKey, assetClinicalRecord);
            
            assetClinicalRecord.setSiteId(edgeServerInfo.getSiteId());
            assetClinicalRecord.setHospitalId(edgeServerInfo.getHospitalId());
            assetClinicalRecord.setAssetId(getAssetIdByModalityId(assetClinicalRecord));
            assetClinicalRecord.setId(null);

            mapProcedureName(edgeServerInfo.getSiteId(), edgeServerInfo.getHospitalId(), assetClinicalRecord);
            
            AssetClinicalRecordRepository dao = WebUtil.getBean(AssetClinicalRecordRepository.class);
            dao.save(assetClinicalRecord);
            return "dataUploadedOK";
        } 
        catch(Exception ex){
            AssetClinicalRecordErrlog assetClinicalRecordErrlog = new AssetClinicalRecordErrlog();
            BeanUtils.copyProperties(assetClinicalRecord, assetClinicalRecordErrlog);

            assetClinicalRecordErrlog.setEdgeServerKey(edgeServerKey);
            assetClinicalRecordErrlog.setUploadTime(TimeUtil.now());

            String errMsg = ex.getMessage();
            if(errMsg==null) errMsg = ex.getClass().getSimpleName();

            int len = errMsg.length();
            if(len>255) len = 255;
            assetClinicalRecordErrlog.setErrMsg(errMsg.substring(0, len));
            
            AssetClinicalRecordErrlogRepository dao = WebUtil.getBean(AssetClinicalRecordErrlogRepository.class);
            dao.save(assetClinicalRecordErrlog);
            
            throw ex;
        }
    }
    
}
