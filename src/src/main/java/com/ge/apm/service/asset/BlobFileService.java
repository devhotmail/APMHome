/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.asset.AssetFileAttachmentController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class BlobFileService {

    @Autowired
    BlobObjectRepository blobDao;

    @Autowired
    MicroServiceUtil microService;

    @Autowired
    FileScanService fsService;
    
    public boolean deleteBlobFile(String token, String fileUrl) {

        return microService.deleteFileObject(token, fileUrl);
    }

    public V2_BlobObject uploadFile(FileUploadEvent event) {
        String path = "";
        try {
            path = fsService.saveUploadedFile(event.getFile());
        } catch (Exception ex) {
            WebUtil.addErrorMessage("Upload file exception" + ex.getMessage());
            Logger.getLogger(AssetFileAttachmentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        String token = UserContextService.getAccessToken();
        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have premission");
            return null;
        }

        Map<String, Object> fileObject = microService.uploadSingleFile(token, path);

        if (null != fileObject) {
            V2_BlobObject blobObject = new V2_BlobObject();
            blobObject.setObjectName((String) fileObject.get("objectName"));
            blobObject.setObjectSize(Long.parseLong(String.valueOf(fileObject.get("objectSize"))));
            blobObject.setObjectStorageId((String) fileObject.get("objectId"));
            blobObject.setObjectType((String) fileObject.get("objectType"));
            return blobDao.save(blobObject);
        } else {
            return null;
        }

    }

    public Boolean deleteBlobFile(V2_BlobObject blobObject) {
        
        if(null == blobObject){
            return false;
        }
        
        String token = UserContextService.getAccessToken();
        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have premission");
            return false;
        }

        if (microService.deleteFileObject(token, blobObject.getObjectStorageId())) {
            blobDao.delete(blobObject);
            return true;
        } else {
            WebUtil.addErrorMessage("Fail to delete file");
            return false;
        }
        
    }

    public V2_BlobObject getBlobObjectByStorageId(String storagedId) {
        return blobDao.findByObjectStorageId(storagedId);
    }

}
