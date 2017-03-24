/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeAttachmentRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeAttachment;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.SiteInfo;
import com.ge.apm.domain.UserAccount;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

/**
 *
 * @author 212579464
 */
@Component
public class AssetCreateService {

    @Autowired
    private SiteInfoRepository siteDao;
    @Autowired
    private OrgInfoRepository orgDao;
    
    @Autowired
    private QrCodeLibRepository qrLibDao;
    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    AssetFileAttachmentRepository attachDao;
    @Autowired
    QrCodeAttachmentRepository qrCodeAttachDao;
    
    
    
    public String getSiteName(Integer siteId) {
        SiteInfo si = siteDao.findById(siteId);
        return si.getName();
    }

    public String getHospitalName(Integer hospitalId) {
        OrgInfo org = orgDao.findById(hospitalId);
        return org.getName();
    }

    public QrCodeLib getCreateRequest(String qrCode) {
        return qrLibDao.findByQrCode(qrCode);
    }
    
     public List<OrgInfo> getClinicalDeptList(Integer hospitalId) {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, hospitalId));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    @Transactional
    public Boolean CreateAeest(AssetInfo assetInfo) {
        return (null!=assetDao.save(assetInfo));
    }

    public List<UserAccount> getAssetOnwers(int siteId, Integer hospitalId) {
        //get all users in this hospital and has 'MultiHospital' users in this site 
        List<UserAccount> users =userDao.getAssetOnwers(siteId,hospitalId);
        return users;
    }

    @Transactional
    public void createAttachments(AssetInfo assetInfo, Integer[] selectedPictures) {
        List<AssetFileAttachment> attachList = new ArrayList();
        for(Integer item : selectedPictures){
            AssetFileAttachment attach = new AssetFileAttachment();
            attach.setAssetId(assetInfo.getId());
            attach.setFileId(item);
            attach.setFileType("1");
            attach.setHospitalId(assetInfo.getHospitalId());
            attach.setSiteId(assetInfo.getSiteId());
            attach.setName("image".concat(item.toString()));
            attachList.add(attach);
        }
        attachDao.save(attachList);
    }

    public List<QrCodeAttachment> getQrCodePictureList(String qrCode) {
        List<SearchFilter> qrCodeFilters = new ArrayList<>();
        qrCodeFilters.add(new SearchFilter("qrCodeId", SearchFilter.Operator.EQ, qrCode));
        qrCodeFilters.add(new SearchFilter("fileType", SearchFilter.Operator.EQ, 1));
        List<QrCodeAttachment> picsList = qrCodeAttachDao.findBySearchFilter(qrCodeFilters);
        return picsList;
    }

    @Transactional
    public void updateQrLib(String qrCode, int i) {
        QrCodeLib qrLibItem = qrLibDao.findByQrCode(qrCode);
        qrLibItem.setStatus(i);
        qrLibDao.save(qrLibItem);
    }

}
