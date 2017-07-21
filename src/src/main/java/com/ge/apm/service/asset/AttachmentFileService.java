/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.FileUploadDao;
import com.ge.apm.service.utils.AmrAudioUtil;
import com.ge.apm.service.wechat.CoreService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.Part;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AttachmentFileService {

    FileUploadDao fileUploaddao;

    @PostConstruct
    public void init() {
        fileUploaddao = new FileUploadDao();
    }

    public Integer uploadFile(UploadedFile file) {
        Integer id = saveFiletoDB(file);
        return id;
    }

    public String getFileName(UploadedFile file) {
        return getFileName(file.getFileName());
    }

    public String getFileName(String rowName) {
        String fileName = "";
        try {
            fileName = new String(rowName.getBytes(), "utf-8");
        } catch (UnsupportedEncodingException ex) {
            WebUtil.addErrorMessage(WebUtil.getMessage("fileTransFail"));
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileName;
    }

    public String getFileNameById(Integer fileId) {
        try {
            return fileUploaddao.getFileNameById(fileId);
        } catch (SQLException ex) {
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public StreamedContent getFile(Integer fileId) {
        InputStream is = null;
        try {
            Object[] result = fileUploaddao.getAttachmentFile(fileId);
            DefaultStreamedContent fileStream = new DefaultStreamedContent((InputStream) result[1]);
            fileStream.setName(new String(((String) result[0]).getBytes("utf-8"), "ISO8859-1"));
            return fileStream;
        } catch (SQLException | UnsupportedEncodingException ex) {
            WebUtil.addErrorMessage(WebUtil.getMessage("fileTransFail"));
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Integer saveFiletoDB(UploadedFile file) {

        Integer returnId = 0;
        try {
            returnId = fileUploaddao.saveUploadFile(file.getInputstream(), Integer.valueOf(String.valueOf(file.getSize())), getFileName(file));
        } catch (SQLException | IOException ex) {
            WebUtil.addErrorMessage(WebUtil.getMessage("fileTransFail"));
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnId;
    }

    public boolean deleteAttachment(Integer fileId) {
        fileUploaddao.deleteUploadFile(fileId);
        return true;
    }

    public Integer uploadFile(Part file) {
        Integer returnId = 0;
        try {
            returnId = fileUploaddao.saveUploadFile(file.getInputStream(), Integer.valueOf(String.valueOf(file.getSize())), file.getSubmittedFileName());
        } catch (Exception ex) {
            WebUtil.addErrorMessage(WebUtil.getMessage("fileTransFail"));
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnId;
    }

    public Integer uploadFile(File file) {
        Integer returnId = 0;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            returnId = fileUploaddao.saveUploadFile(is, Integer.valueOf(String.valueOf(file.length())), file.getName());
        } catch (SQLException | IOException ex) {
            WebUtil.addErrorMessage(WebUtil.getMessage("fileTransFail"));
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return returnId;
    }

    public Integer uploadBase64File(String fileString, String fileName) {
        fileString = fileString.substring(fileString.indexOf("base64,") + 7);
        Base64 decoder = new Base64();
        InputStream is = null;
        Integer returnId = 0;
        try {
            byte[] bytes = decoder.decode(fileString);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            is = new ByteArrayInputStream(bytes);
            returnId = fileUploaddao.saveUploadFile(is, bytes.length, fileName);

        } catch (SQLException ex) {
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return returnId;
    }

    public String getBase64File(Integer fileId) {
        Base64 encoder = new Base64();
        StreamedContent stream = getFile(fileId);
        String res = "";
        try {
            byte[] bytes = new byte[stream.getStream().available()];
            stream.getStream().read(bytes);
            res = encoder.encodeToString(bytes);
        } catch (IOException ex) {
            Logger.getLogger(AttachmentFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public String getMp3Base64String(Integer fileId) throws IOException {

        StreamedContent stream = getFile(fileId);

        String res = AmrAudioUtil.changeAmrToMp3Base64(stream.getStream());
        return res;
    }

    public InputStream getMp3Stream(Integer fileId) throws IOException {
        String fileString = getMp3Base64String(fileId);

        Base64 decoder = new Base64();
        InputStream is = null;
        byte[] bytes = decoder.decode(fileString);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        is = new ByteArrayInputStream(bytes);

        return is;
    }

}
