/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.QrCodeAttachmentRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.QrCodeAttachment;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.WebUtil;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.data.DataMigrateService;
import com.ge.apm.service.utils.MicroServiceUtil;
import java.util.UUID;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class DataMigrateController {

    private List<AssetFileAttachment> assetAttachmentList;
    private List<QrCodeAttachment> qrAttachList;

    private AssetFileAttachmentRepository attchDao;
    private AssetInfoRepository assetDao;
    private QrCodeAttachmentRepository qrattachDao;
    private QrCodeLibRepository qrLibDao;

    private AssetCreateService acService;
    private DataMigrateService dmService;

    @PostConstruct
    protected void init() {

        attchDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        dmService = WebUtil.getBean(DataMigrateService.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        qrattachDao = WebUtil.getBean(QrCodeAttachmentRepository.class);
        qrLibDao = WebUtil.getBean(QrCodeLibRepository.class);

        assetAttachmentList = getAttachementList();
        qrAttachList = getQrAttachmentList();
    }

    public List<QrCodeAttachment> getQrAttachmentList() {
        return qrattachDao.getNoneAssetAttachments();
    }

    public List<AssetFileAttachment> getAttachementList() {
        List<AssetFileAttachment> alllist = attchDao.find();
        return alllist.stream().filter(item -> item.getFileUrl() == null || item.getFileUrl().isEmpty()).collect(Collectors.toList());
    }

    public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getSiteName(Integer siteId) {
        return acService.getSiteName(siteId);
    }

    public void migrateAssetAttachData() {

        String token = UserContextService.getAccessToken();
        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have premission");
            return;
        }

        assetAttachmentList.stream().forEach(item -> {
            dmService.migrateAssetAttchment(item, token);
        });

    }

    public void migrateQrAttachData() {
        String token = UserContextService.getAccessToken();
        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have premission");
            return;
        }
        qrAttachList.stream().forEach(item -> {
            dmService.migrateQrAttchment(item, token);
        });

    }

    public Integer getCountOfAssetEntity() {
        return assetDao.find().stream().filter(item -> item.getUid() == null).collect(Collectors.toList()).size();
    }

    public void updateAssetEntity() {
        assetDao.find().stream().filter(item -> item.getUid() == null).forEach(item -> {
            item.setUid(UUID.randomUUID().toString().replace("-", ""));
            assetDao.save(item);
        });
    }

    public Integer getCountOfQrLibEntity() {
        return qrLibDao.find().stream().filter(item -> item.getUid() == null).collect(Collectors.toList()).size();
    }

    public void updateQrLibEntity() {
        qrLibDao.find().stream().filter(item -> item.getUid() == null).forEach(item -> {
            item.setUid(UUID.randomUUID().toString().replace("-", ""));
            qrLibDao.save(item);
        });
    }

    public List<QrCodeAttachment> getQrAttachList() {
        return qrAttachList;
    }

    public List<AssetFileAttachment> getAssetAttachmentList() {
        return assetAttachmentList;
    }
}
