/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.FileUploadDao;
import com.ge.apm.domain.AssetFileAttachment;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

@ManagedBean
@SessionScoped
public class AttachmentFileService {

    AssetFileAttachmentRepository attachDao = null;

    FileUploadDao fileUploaddao;

    @PostConstruct
    public void init() {
        attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        fileUploaddao = new FileUploadDao();
    }

    public Integer uploadFile(UploadedFile file) {
        Integer id = saveFiletoDB(file);
        return id;
    }
    public String getFileName(UploadedFile file) {
        String fileName = "";
        try {
            fileName = new String(file.getFileName().getBytes(), "utf-8");
        } catch (UnsupportedEncodingException ex) {
            WebUtil.addErrorMessage("file name error");
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileName;
    }

    
    public StreamedContent getFile(Integer fileId) {
        InputStream is = null;
        try {
            Object[] result = fileUploaddao.getAttachmentFile(fileId);
            DefaultStreamedContent fileStream = new DefaultStreamedContent((InputStream)result[1]);
            fileStream.setName(new String(((String)result[0]).getBytes("utf-8"),"ISO8859-1"));
            return fileStream;
        } catch (SQLException | UnsupportedEncodingException ex) {
            WebUtil.addErrorMessage("file download error");
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    

    private Integer saveFiletoDB(UploadedFile file) {

        Integer returnId = 0;
        try {
            returnId = fileUploaddao.saveUploadFile(file.getInputstream(), Integer.valueOf(String.valueOf(file.getSize())), getFileName(file));
        } catch (SQLException | IOException ex) {
            WebUtil.addErrorMessage("file save error");
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnId;
    }


    public boolean deleteAttachment(Integer fileId) {
        fileUploaddao.deleteUploadFile(fileId);
        return true;
    }
    

}
