/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.view.sysutil.AppContextService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import jodd.io.StreamUtil;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

@ManagedBean
@SessionScoped
public class AttachmentFileService {

    static String rootPath = AppContextService.getFileUploadFolder();
    static String tempPath = "rootPath" + File.separator + "temp";

    AssetFileAttachmentRepository attachDao = null;

    @PostConstruct
    public void init() {
        attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
    }

    public Boolean uploadFile(UploadedFile file, String fileUrl) {
        File outputFile = new File(tempPath + File.separator + fileUrl);
        try {
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream fis = file.getInputstream();
            StreamUtil.copy(fis, fos);
            fos.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public String getFileUrl() {
        return System.currentTimeMillis() + File.separator;
    }

    public String getFileName(UploadedFile file) {
        String fileName = "";
        try {
            fileName = new String(file.getFileName().getBytes(), "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileName;
    }

    /**
     * download file from page
     * @param file
     * @return 
     */
    public StreamedContent getFile(AssetFileAttachment attachFile) {
        File file = new File(rootPath + attachFile.getFileUrl());
        DefaultStreamedContent fileStream;
        try {
            fileStream = new DefaultStreamedContent(new FileInputStream(file));
            fileStream.setName(attachFile.getName());
            return fileStream;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean removeAttachment(AssetFileAttachment attachFile) {
        File removeFile = new File(rootPath + attachFile.getFileUrl());
        attachDao.delete(attachFile);
        removeFile.delete();
        removeFile.getParentFile().delete();
        return true;
    }

    public boolean addAttachment(AssetFileAttachment item, Integer hospitalId) {
        StringBuilder url = new StringBuilder(File.separator);
        url.append(item.getSiteId()).append(File.separator);
        url.append(hospitalId).append(File.separator);
        url.append(item.getAssetId()).append(File.separator);
        url.append(System.currentTimeMillis()).append(File.separator).append(item.getName());
        File newFile = new File(rootPath + url.toString());
        File oldFile;
        if (null == item.getId()) {
            oldFile = new File(tempPath + File.separator + item.getFileUrl());
        } else {
            oldFile = new File(rootPath + File.separator + item.getFileUrl());
        }
        if (!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }
        oldFile.renameTo(newFile);
        oldFile.getParentFile().delete();
        item.setFileUrl(url.toString());
        attachDao.save(item);
        return true;
    }
}
