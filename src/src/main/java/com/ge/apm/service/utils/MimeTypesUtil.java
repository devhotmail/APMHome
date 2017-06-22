/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import javax.activation.MimetypesFileTypeMap;

/**
 *
 * @author 212579464
 */
public class MimeTypesUtil {

    
    static public String getMimeTypeByName(String fileName) {

        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        switch (suffix) {
            case ".doc":   return "application/msword";
            case ".arm":   return "image/gif";
            case ".mp3":   return "audio/mpeg";
            case ".mp4":   return "video/mpeg4";
            case ".pdf":   return "application/pdf";
            case ".png":   return "image/png";
            case ".ppt":   return "application/x-ppt";
            case ".wmv":   return "video/x-ms-wmv";
            case ".xls":   return "application/x-xls";
            case ".exe":   return "application/x-msdownload";
            case ".zip":   return "application/zip";
            default : return new MimetypesFileTypeMap().getContentType(fileName);
        }
    }

}
