/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.utils.MimeTypesUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Controller
public class AudioStreamController {

    private AttachmentFileService attachFileService = WebUtil.getBean(AttachmentFileService.class);

    @RequestMapping(value = "/audio/{fileId}", method = RequestMethod.GET)
    public void getMP3AudioFile(@PathVariable Integer fileId, HttpServletRequest request, HttpServletResponse response) {
        InputStream mp3Src = null;
        ServletOutputStream outStream = null;
        try {
            mp3Src = attachFileService.getMp3Stream(fileId);
            outStream = response.getOutputStream();
            response.setContentType("audio/mpeg");
            response.addHeader("Content-Disposition", "attachment; filename="
                    + fileId.toString().concat(".mp3"));
            response.setContentLength(mp3Src.available());
            byte[] b = new byte[mp3Src.available()];
            mp3Src.read(b);
            outStream.write(b);
            outStream.flush();
            mp3Src.close();

        } catch (IOException ex) {
            Logger.getLogger(AudioStreamController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(AudioStreamController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    @RequestMapping(value = "/uploadedFile/{fileId}", method = RequestMethod.GET)
    public void getFile(@PathVariable Integer fileId, HttpServletRequest request, HttpServletResponse response) {
        InputStream fileStream = null;
        ServletOutputStream outStream = null;
        try {
            String fileName = attachFileService.getFileNameById(fileId);
            fileStream = attachFileService.getFile(fileId).getStream();
            String type = MimeTypesUtil.getMimeTypeByName(fileName);
            outStream = response.getOutputStream();
            response.setContentType(type);
            response.addHeader("Content-Disposition", "attachment; filename="
                    + fileName);
            response.setContentLength(fileStream.available());
            byte[] b = new byte[fileStream.available()];
            fileStream.read(b);
            outStream.write(b);
            outStream.flush();
            fileStream.close();

        } catch (IOException ex) {
            Logger.getLogger(AudioStreamController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(AudioStreamController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
