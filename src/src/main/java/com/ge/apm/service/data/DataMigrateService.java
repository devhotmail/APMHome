/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.data;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.QrCodeAttachmentRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.QrCodeAttachment;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.utils.MicroServiceUtil;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 212579464
 */
@Component
public class DataMigrateService {

    @Autowired
    MicroServiceUtil msService;
    @Autowired
    AssetInfoRepository assetDao;
    @Autowired
    BlobObjectRepository boDao;
    @Autowired
    AssetFileAttachmentRepository attachDao;
    @Autowired
    AttachmentFileService attachService;
    @Autowired
    QrCodeLibRepository qrLibDao;
    @Autowired
    QrCodeAttachmentRepository qrAttachDao;

    @Value("#{urlProperties.url_localFileUrl}")
    private String url_localFileUrl;

    @Transactional
    public Boolean migrateAssetAttchment(AssetFileAttachment item, String token) {

        Map<String, Object> docMap = msService.uploadSingleFileByUrl(token, url_localFileUrl.concat("/").concat(item.getFileId().toString()), item.getName());
        if (null == docMap) {
            return false;
        }
        AssetInfo asset = assetDao.getById(item.getAssetId());
        V2_BlobObject blobObject = new V2_BlobObject();
        blobObject.setBoId(item.getAssetId());
        blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
        blobObject.setBoSubType(item.getFileType());
        blobObject.setBoUuid(asset.getUid());
        blobObject.setObjectName((String) docMap.get("objectName"));
        blobObject.setObjectSize(Long.parseLong(String.valueOf(docMap.get("objectSize"))));
        blobObject.setObjectStorageId((String) docMap.get("objectId"));
        blobObject.setObjectType((String) docMap.get("objectType"));
        boDao.save(blobObject);

        item.setFileUrl(blobObject.getId());
        attachDao.save(item);
        return blobObject.getId() != null;
    }

    @Transactional
    public Boolean migrateQrAttchment(QrCodeAttachment item, String token) {

        String fileName = attachService.getFileNameById(item.getFileId());
        Map<String, Object> docMap = msService.uploadSingleFileByUrl(token, url_localFileUrl.concat("/").concat(String.valueOf(item.getFileId())), fileName);
        if (null == docMap) {
            return false;
        }
        
        QrCodeLib qrCodeItem = qrLibDao.findById(item.getQrCodeId());
        V2_BlobObject blobObject = new V2_BlobObject();
        blobObject.setBoId(item.getQrCodeId());
        blobObject.setBoType(V2_BlobObject.BO_TYPE_QRCodeLib);
        blobObject.setBoSubType(String.valueOf(item.getFileType()));
        blobObject.setBoUuid(qrCodeItem.getUid());
        blobObject.setObjectName((String) docMap.get("objectName"));
        blobObject.setObjectSize(Long.parseLong(String.valueOf(docMap.get("objectSize"))));
        blobObject.setObjectStorageId((String) docMap.get("objectId"));
        blobObject.setObjectType((String) docMap.get("objectType"));
        boDao.save(blobObject);
        
        item.setObjectId((String) docMap.get("objectId"));
        qrAttachDao.save(item);
        return blobObject.getId() != null;
    }

}
